package com.xc.util;

/**
 * 整型和字节数组互换
 */
public class ByteUtils {

	// 整型转字节数组
	public static byte[] IntToBytes(int value, int arrayLength) {
		return IntToBytes(value, arrayLength, true);
	}

	public static byte[] IntToBytes(int value, int arrayLength, boolean isBigEdian) {
		byte[] result = new byte[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			// 高->低/低 -> 高
			// 判断 BIG-EDIAN 还是 LITTLE-EDIAN
			int moveBit = isBigEdian ? (arrayLength - i - 1) * 8 : i * 8;
			// 右移后 与运算 (0xff = 255)
			result[i] = (byte) ((value >> moveBit) & 0xff);
		}
		return result;
	}

	// 字节数组转整型
	public static int BytesToInt(byte[] bytes) {
		return BytesToInt(bytes, true);
	}

	public static int BytesToInt(byte[] bytes, boolean isBigEdian) {
		int result = 0;
		for (int i = 0, arrayLength = bytes.length; i < arrayLength; i++) {
			byte b = bytes[i];
			int moveBit = isBigEdian ? (arrayLength - i - 1) * 8 : i * 8;
			// 与运算后 左位移
			int realValue = ((b & 0xff) << moveBit);
			result += realValue;
		}
		return result;
	}

	// 改变指定位的值
	public static long changeBit(long originalValue, int post, boolean on) {
		long opValue = 1L << post;
		long result = originalValue;
		if (on) {
			result = originalValue | opValue;
		} else {
			// 取反后 与运算
			result = originalValue & ~opValue;
		}
		return result;
	}

	// 判断指定位是否1
	public static boolean isBitOn(long originalValue, int post) {
		long value = changeBit(originalValue, post, true);
		return value == originalValue;
	}

	// 开关指定位
	public static long toggleBit(long originalValue, int post) {
		long opValue = 1L << post;
		long result = originalValue ^ opValue;
		return result;
	}
/*
	public static void main(String[] args) {
		long originalValue = 13L;
		System.out.println(isBitOn(originalValue, 1));
		long changeValue = changeBit(originalValue, 1, true);
		System.out.println(originalValue + " -> " + changeValue);
		String resultBinary = Long.toBinaryString(changeValue);
		System.out.println(Long.toBinaryString(originalValue));
		System.out.println(resultBinary);
		
	    long permissionValue = 0;
        permissionValue = ByteUtils.changeBit(permissionValue, 0, true);
        permissionValue = ByteUtils.changeBit(permissionValue, 1, true);
        System.out.println(permissionValue);
        System.out.println(Integer.parseInt("00100101",2)+"========"+Integer.parseInt("10001010",2));
        int i=2;
        System.out.println(Integer.toBinaryString(i)+"===~==="+(~-2));
	}
	*/
}
