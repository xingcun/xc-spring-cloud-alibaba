package com.xc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;

/**
 * Excel导出
 *
 */
public class ExcelUtil {
	/**
	 *
	 * @param list        导出的数据
	 * @param keys        取每列数据的key
	 * @param columnNames 列名
	 * @return
	 */
	public Workbook createWorkBook(List<Map<String, Object>> listMap, List keys, List columnNames,
			boolean isFormatDateTime) {
		// 创建excel工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		Sheet sheet = wb.createSheet("Sheet1");
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for (int i = 0; i < keys.size(); i++) {
			sheet.setColumnWidth((short) i, (short) (50 * 100));
		}

		// 创建两种单元格格式
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		// 创建两种字体
		Font f = wb.createFont();
		Font f2 = wb.createFont();

		// 创建第一种字体样式（用于列名）
		f.setFontHeightInPoints((short) 14);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setFontName("微软雅黑");
		f.setBold(true);

		// 创建第二种字体样式（用于值）
		f2.setFontHeightInPoints((short) 10);
		f2.setFontName("微软雅黑");
//        f2.setColor(IndexedColors.BLACK.getIndex());

		// 设置第一种单元格的样式（用于列名）
		cs.setFont(f);
//        cs.setBorderLeft(BorderStyle.THIN);
//        cs.setBorderRight(BorderStyle.THIN);
//        cs.setBorderTop(BorderStyle.THIN);
//        cs.setBorderBottom(BorderStyle.THIN);
		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);

		// 设置第二种单元格的样式（用于值）
		cs2.setFont(f2);
//        cs2.setBorderLeft(BorderStyle.THIN);
//        cs2.setBorderRight(BorderStyle.THIN);
//        cs2.setBorderTop(BorderStyle.THIN);
//        cs2.setBorderBottom(BorderStyle.THIN);
		cs2.setAlignment(HorizontalAlignment.CENTER_SELECTION);

		// 创建第一行
		Row row = sheet.createRow((short) 0);
		row.setHeight((short) 1000);
		// 设置列名
		for (int i = 0; i < columnNames.size(); i++) {
			Cell cell = row.createCell(i);
			String name = columnNames.get(i).toString();
			cell.setCellValue(columnNames.get(i).toString());
			cell.setCellStyle(cs);
		}
		CellStyle cs3 = wb.createCellStyle();
		cs3.cloneStyleFrom(cs2);
		cs3.setDataFormat(wb.createDataFormat().getFormat("YYYY/MM/dd"));

		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		// 设置每行每列的值
		for (short i = 0; i < listMap.size(); i++) {
			// Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
			// 创建一行，在页sheet上
			Row row1 = sheet.createRow((short) i + 1);
			// 在row行上创建一个方格
			for (short j = 0; j < keys.size(); j++) {
				Cell cell = row1.createCell(j);
				String[] key = keys.get(j).toString().split("_&_");
				Object value = listMap.get(i).get(key[0]);
				if (key.length > 1) {
					if (styles.containsKey(key[1])) {
						cell.setCellStyle(styles.get(key[1]));
					} else {
						CellStyle cellStyle = wb.createCellStyle();
						cellStyle.cloneStyleFrom(cs2);
						cellStyle.setDataFormat(wb.createDataFormat().getFormat(key[1]));
						cell.setCellStyle(cellStyle);
						styles.put(key[1], cellStyle);
					}

				} else {
					cell.setCellStyle(cs2);
				}
				if (value != null) {
					if (value instanceof Number) {
						cell.setCellValue(new BigDecimal(value.toString()).doubleValue());
					} else if ("Timestamp".equals(value.getClass().getSimpleName())) {
						if (isFormatDateTime) {
							cell.setCellValue(CommonUtil.formatLongDate(value));
						} else {
							cell.setCellValue(new DateTime(CommonUtil.formatTime("YYYY-MM-dd", value)).toDate());
							cell.setCellStyle(cs3);
						}
					} else {

						cell.setCellValue(value.toString());
					}

				} else {
					cell.setCellValue("");
				}

			}
		}

		return wb;
	}

	public List<Map<String, String>> getExcelData(InputStream is, String fileName, String keys[]) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Workbook wb = null;
		try {

			if (fileName == null || fileName.length() == 0 || fileName.endsWith("xlsx")) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = new HSSFWorkbook(is);
			}
			// 获得第一个表单
			Sheet sheet = wb.getSheetAt(0);
			// 获得第一个表单的迭代器
			Iterator<Row> rows = sheet.rowIterator();
			int i = 0;
			while (rows.hasNext()) {
				i++;
				Map<String, String> rowMap = new HashMap<String, String>();
				// 获得行数据
				Row row = rows.next();
				// 跳过头部
				if (row.getRowNum() == 0) {
					continue;
				}
				Iterator<Cell> cells = row.cellIterator();
				// 获得行的迭代器
				int j = 0, k = 0;
				while (cells.hasNext()) {
					Cell cell = cells.next();
					// 类型判断
					String key = "";
					// 防止越界
					if (keys.length > cell.getColumnIndex()) {
						key = keys[cell.getColumnIndex()];
					}
					if (CommonUtil.isNotNull(key)) {
						String value = formatCell(cell);
						rowMap.put(key, value);
						if (!CommonUtil.isNotNull(value)) {
							j++; // 记录空值得数量
						}
						k++; // 记录多少列
					}
				}
				// 如果i=j，说明一行都是空的
				if (j == k) {
					break;
				} else {
					data.add(rowMap);
				}
				if (i > 50001) {
					System.err.println("\n============导入数据大于五万条，立即停止===============");
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(wb!=null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				is.close();
			} catch (Exception e) {

			}

		}
		return data;
	}

	/**
	 * 获取导入Excel的数据
	 * 
	 * @param request
	 * @param filePro
	 * @param keys
	 * @return
	 */
	public List<Map<String, String>> getExcelData(HttpServletRequest request, String filePro, String keys[]) {
		if (!CommonUtil.isNotNull(filePro)) {
			filePro = "file";
		}

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(filePro);
		// 获得文件名
		String fileName = file.getOriginalFilename();
		try {
			return getExcelData(file.getInputStream(), fileName, keys);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<Map<String, String>>();
	}

	/**
	 * 反射机制 map转实体
	 * 
	 * @param classPath java类路径
	 * @param listData  map数据
	 * @return
	 */
	public List<Object> factoryMapToJavaObj(String classPath, List<Map<String, String>> listData) {
		List<Object> list = new ArrayList<Object>();
		try {
			// 获取类
			Class<?> c = Class.forName(classPath);
			// 获取类的所有属性
			Field[] fs = c.getDeclaredFields();
			Object valObj = null;
			for (Map<String, String> mapData : listData) {
				// 获取类的一个实例
				Object o = c.newInstance();
				for (Field field : fs) {
					String fieldName = field.getName();
					String fieldType = field.getType().getName();
					String value = mapData.get(fieldName);
					if (CommonUtil.isNotNull(value)) {
						valObj = value.trim();
					} else {
						valObj = null;
					}
					if (Modifier.toString(field.getModifiers()).indexOf("static") != -1 || fieldName.equals("id")) {
						continue;
					}
					if (fieldType.equals("java.math.BigDecimal")) {
						if (CommonUtil.isNotNull(valObj)) {
							if (valObj.toString().indexOf("%") != -1) {
								String newVal = valObj.toString().replaceAll("%", "");
								Double num = Double.valueOf(newVal) / 100;
								valObj = BigDecimal.valueOf(num);
							} else {
								valObj = BigDecimal.valueOf(Double.valueOf(value.replace(",", "")));
							}
						} else {
							valObj = BigDecimal.valueOf(0);
						}
					}
					if (fieldType.equals("java.util.Date") && CommonUtil.isNotNull(value)) {
						valObj = CommonUtil.formatDate(value.replace("/", "-"));
					}
					if (fieldType.equals("java.lang.Integer")) {
						if (CommonUtil.isNotNull(valObj)) {
							valObj = Integer.valueOf(valObj.toString());
						} else {
							valObj = Integer.valueOf(0);
						}
					}
					if (fieldType.equals("java.lang.Long")) {
						if (CommonUtil.isNotNull(valObj)) {
							valObj = Long.valueOf(valObj.toString());
						} else {
							valObj = Long.valueOf(0);
						}
					}
					if (CommonUtil.isNotNull(valObj)) {
						// 设置可访问私有属性
						field.setAccessible(true);
						// 给o对象的属性赋值
						field.set(o, valObj);
					}
				}
				list.add(o);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
//    	Map<String, String> map = new HashMap<>();
//    	List l = new ArrayList<>();
//    	l.add(map);
//    	new ExcelUtil().factoryMapToJavaObj("com.qhiex.foundation.domain.po.system.BusMediInterface", l);

		long start = System.currentTimeMillis() / 1000;
		String[] strs = { "出单日期", "台帐编号", "供应商(ID)", "所属合同", "产品(ID)", "机构", "代理人", "销售员", "拓展员", "保单类型", "保单号", "被保人",
				"投保人", "车牌号", "车型", "车架号", "发动机号", "投保险种", "保费金额", "不含税保费", "代理佣金率", "代理人佣金", "代理佣金率①", "代理人佣金①",
				"绩效佣金率②", "绩效佣金②", "投保时间", "起保时间", "保险止期", "收费日期", "是否营运车", "是否新车", "转续保", "系统跟单手续费", "手续费率等级", "业绩提成率",
				"业绩提成（元）", "保险公司", "险种名称", "营业部", "出单员", "录单员", "审核", "备注" };
		List<Map<String, String>> result=null;
		try {
			result = new ExcelUtil().getExcelData(new FileInputStream( "D:\\share\\2018-08-09台帐.xls"), "D:\\share\\2018-08-09台帐.xls", strs);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result.get(0));
		System.out.println(System.currentTimeMillis() / 1000 - start);
	}

	/**
	 * 按类型取值
	 * 
	 * @param cell
	 * @return
	 */
	public String formatCell(Cell cell) {
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) {

			// 数值格式
			case NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是日期格式
					return CommonUtil.formatTime("YYYY-MM-dd", (HSSFDateUtil.getJavaDate(cell.getNumericCellValue())));
				} else {
					// 字符时
	//            	cell.setCellType(CellType.STRING);
//					cell.setCellFormula("TEXT(" + cell.getNumericCellValue() + ",\"?\")");
					return String.valueOf(cell.getNumericCellValue());
				}
	
				// 字符串
			case STRING:
				return cell.getStringCellValue();
	
			// 公式
			case FORMULA:
				return cell.getCellFormula();
	
			// 空白
			case BLANK:
				return "";
	
			// 布尔取值
			case BOOLEAN:
				return cell.getBooleanCellValue() + "";
	
			// 错误类型
			case ERROR:
				break;
		}
		return "";
	}
}
