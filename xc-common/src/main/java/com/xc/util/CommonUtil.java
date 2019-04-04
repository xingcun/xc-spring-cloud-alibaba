package com.xc.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class CommonUtil {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 总线程数控制 获认CPU数量*2
	 */
	public static final ExecutorService thread = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

	/**
	 * 判断符串是否为空
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isNotNull(Object obj) {
		if ((obj != null) && (!obj.toString().equals(""))) {
			return true;
		}
		return false;
	}

	public static Long nullLong(Object s) {
		Long v = Long.valueOf(-1L);
		if (s != null)
			try {
				v = Long.valueOf(Long.parseLong(s.toString()));
			} catch (Exception localException) {
			}
		return v;
	}

	public static String getString(Object s) {
		return s == null ? "" : s.toString().trim();
	}

	public static String getJsonString(Object obj) {
		return JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss",
				SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 相同对象进行copy,空值过滤
	 *
	 * @param origin
	 * @param destination
	 */
	public synchronized static <T> void mergeObject(T origin, T destination) {
		mergeObject(origin, destination, false);
	}

	public synchronized static <T> void mergeObject(T origin, T destination, boolean isNull, String... filters) {
		if (origin == null || destination == null)
			return;
		if (!origin.getClass().equals(destination.getClass()))
			return;

		Field[] fields = origin.getClass().getDeclaredFields();
		List<String> filterList = filters == null ? new ArrayList<String>() : Arrays.asList(filters);
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				Object value = fields[i].get(origin);
				boolean flag = filterList.contains(fields[i].getName());
				if (flag || ((isNull && value != null) || CommonUtil.isNotNull(value))) {
					fields[i].set(destination, value);
				}
				fields[i].setAccessible(false);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @param date1 需要比较的时间 不能为空(null),需要正确的日期格式
	 * @param date2 被比较的时间 为空(null)则为当前时间
	 * @param stype 返回值类型 0为多少天，1为多少个月，2为多少年
	 * @return
	 */
	public static int compareDate(String date1, String date2, int stype) {
		int n = 0;
		// String[] u = {"天","月","年"};
		String formatStyle = stype == 1 ? "yyyy-MM" : "yyyy-MM-dd";

		date2 = isNotNull(date2) ? date2 : formatTime(formatStyle, new Date());

		DateFormat df = new SimpleDateFormat(formatStyle);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(df.parse(date1));
			c2.setTime(df.parse(date2));
		} catch (Exception e3) {
			System.out.println("wrong occured");
		}
		int[] p1 = { c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH) };
		int[] p2 = { c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH) };
		return stype == 0 ? (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / (24 * 3600 * 1000))
				: stype == 1 ? p2[0] * 12 + p2[1] - p1[0] * 12 - p1[1] : p2[0] - p1[0];
	}

	public synchronized static String hideMsg(String msg) {

		if (msg != null && !"".equals(msg)) {
			msg = msg.trim();
			int length = msg.length();
			if (length > 1) {
//            	System.out.println(length/2);
				int hideLength = length / 2;
				if (length == 2 || hideLength >= length - 1) {
					hideLength = hideLength - 1;
				}
				if (hideLength < 1) {
					hideLength = 1;
				}
				String hide = "";
				for (int i = 0; i < hideLength; i++) {
					hide += "*";
				}

				int start = (length - hideLength) / 2;

				int end = length - start - hideLength;
				if (end < start) {
					int t_s = end;
					end = start;
					start = t_s;
				}

				if (start == 0 && end == 1) {
					start = 1;
					end = 0;
				}
//                System.out.println(hideLength+"    "+msg +"        "+msg.substring(0,(length-hideLength)/2)+hide+msg.substring((length-hideLength)/2+hideLength)+"     "+(length/2+hideLength));
				msg = msg.substring(0, start) + hide + msg.substring(length - end);
			}

		}

		return msg;
	}

	public static Object deepClone(Object origin) {
		// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = null;
		ObjectInputStream oi = null;
		ByteArrayInputStream bi = null;
		try {
			oo = new ObjectOutputStream(bo);
			oo.writeObject(origin);
			// 从流里读出来
			bi = new ByteArrayInputStream(bo.toByteArray());
			oi = new ObjectInputStream(bi);
			return (oi.readObject());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oi != null) {
					oi.close();
				}
				if (bi != null) {
					bi.close();
				}
				if (bo != null) {
					bo.close();
				}
				if (oo != null) {
					oo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String formatLongDate(Object v) {
		if ((v == null) || (v.equals("")))
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(v);
	}

	public static Date formatDatePlus(String s) {
		Date date = formatDate(s);
		return formatDatePlus(date);
	}

	public static Date formatDatePlus(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		date = calendar.getTime();
		return date;
	}

	public static Date formatDateStartPlus(String s) {
		Date date = formatDate(s);
		return formatDateStartPlus(date);
	}

	public static Date formatDateStartPlus(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		date = calendar.getTime();
		return date;
	}

	public static Date formatDate(String s) {
		Date d = null;
		try {
			d = dateFormat.parse(s);
		} catch (Exception localException) {
		}
		return d;
	}

	public static Date formatDateLong(String s) {
		Date d = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			d = dateFormat.parse(s);
		} catch (Exception localException) {
		}
		return d;
	}

	public static Date formatTime(String format, String v) {
		if (v == null)
			return null;
		if (v.equals(""))
			return null;
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(v);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatTime(String format, Object v) {
		if (v == null)
			return null;
		if (v.equals(""))
			return "";
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(v);
	}

	public static String httpPost(String reqUrl, String data) {
		HttpURLConnection url_con = null;
		String responseContent = null;
		try {
			URL url = new URL(reqUrl);
			url_con = (HttpURLConnection) url.openConnection();
			url_con.setRequestMethod("POST");
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// （单位：毫秒）jdk1.4换成这个,连接超时
			System.setProperty("sun.net.client.defaultReadTimeout", "60000"); // （单位：毫秒）jdk1.4换成这个,读操作超时
			url_con.setReadTimeout(60000);
			url_con.setConnectTimeout(60000);
			url_con.setDoOutput(true);
			url_con.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			url_con.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式

			if (isNotNull(data)) {
				byte[] b = data.getBytes();
				url_con.getOutputStream().write(b, 0, b.length);
			}
			url_con.getOutputStream().flush();
			url_con.getOutputStream().close();

			InputStream in = url_con.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (url_con != null) {
				url_con.disconnect();
			}
		}
		return responseContent;
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public static final String randomString(int length) {
		char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	public static final String randomInt(int length) {
		if (length < 1) {
			return null;
		}
		Random randGen = new Random();
		char[] numbersAndLetters = "0123456789".toCharArray();

		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
		}
		return new String(randBuffer);
	}

	public static Date getPolicyEndDate(Date date) {
		if (date != null) {
			DateTime current = new DateTime();
			DateTime dateTime = new DateTime(date);
			int year = dateTime.getYear() - current.getYear();
			for (; year < 0; year++) {
				dateTime = dateTime.plusYears(1);
			}
			int day = Days.daysBetween(current, dateTime).getDays();
			if ((day >= 0 && day <= 90) || day < 0) {
				dateTime = dateTime.plusYears(1);
			}
			day = Days.daysBetween(current, dateTime).getDays();
			if ((day >= 0 && day <= 90) || day < 0) {
				dateTime = dateTime.plusYears(1);
			}
			return dateTime.toDate();

		}
		return null;
	}

	public static String formatSecond(Object second) {
		String html = "";
		if (second != null) {
			long s = Long.valueOf(second.toString()) / 1000;
			String format;
			Object[] array;
			int day = (int) (s / (60 * 60 * 24));
			Integer hours = (int) (s / (60 * 60)) - day * 24;
			Integer minutes = (int) (s / 60 - hours * 60 - day * 24 * 60);
			Integer seconds = (int) (s - minutes * 60 - hours * 60 * 60 - day * 24 * 60 * 60);
			if (day > 0) {
				format = "%1$,d天%2$,d时%3$,d分%4$,d秒";
				array = new Object[] { day, hours, minutes, seconds };
			} else if (hours > 0) {
				format = "%1$,d时%2$,d分%3$,d秒";
				array = new Object[] { hours, minutes, seconds };
			} else if (minutes > 0) {
				format = "%1$,d分%2$,d秒";
				array = new Object[] { minutes, seconds };
			} else {
				if (seconds == 0) {
					return "";
				}
				format = "%1$,d秒";
				array = new Object[] { seconds };
			}
			html = String.format(format, array);
		}

		return html;

	}

}
