package com.beike.util;



/**
 * @Title: GuidEncryption.java
 * @Package com.beike.util
 * @Description: 交易流水、返现流水生成工具类
 * @author wh.cheng@sinobogroup.com
 * @date May 8, 2011 8:16:36 PM
 * @version V1.0
 */
public class GuidEncryption {
	private static final int[] divisors = { 31, 37, 43, 53, 59, 61, 73, 79, 89,
			97 };

	public GuidEncryption() {
	}

	public static String encryptSimple(String stringToEncrypt) {
		if (Integer.parseInt(stringToEncrypt) < 1) {
			return stringToEncrypt;
		}

		int divisorIndex = Integer.parseInt(stringToEncrypt
				.substring(stringToEncrypt.length() - 1));
		String quotient = String.valueOf(Long.parseLong(stringToEncrypt)
				/ divisors[divisorIndex]);
		String remainder = String.valueOf(Integer.parseInt(stringToEncrypt)
				% divisors[divisorIndex]);
		int quotientLength = quotient.length();
		int quotientLengthLength = String.valueOf(quotientLength).length();
		int remainderLength = remainder.length();
		StringBuffer encryptedStringBuffer = new StringBuffer();
		encryptedStringBuffer.append(quotientLength).append(quotient).append(
				remainderLength).append(remainder);

		int encryptedStringLength = encryptedStringBuffer.length();

		for (int i = 0; i < (14 - encryptedStringLength); i++)
			encryptedStringBuffer
					.append(((Integer.parseInt(encryptedStringBuffer.substring(
							i, i + 1)) + 14) - i) % 10);

		if ((divisorIndex % 2) == 1) {
			encryptedStringBuffer.reverse();
		}

		if (divisorIndex == 0) {
			divisorIndex = quotientLength;
			quotientLengthLength += 5;
		}

		encryptedStringBuffer.insert(0, quotientLengthLength).insert(0,
				divisorIndex);

		return encryptedStringBuffer.toString();
	}

	public static String encryptShortSimple(String stringToEncrypt) {
		if (Integer.parseInt(stringToEncrypt) < 1) {
			return stringToEncrypt;
		}

		int divisorIndex = Integer.parseInt(stringToEncrypt
				.substring(stringToEncrypt.length() - 1));
		String quotient = String.valueOf(Long.parseLong(stringToEncrypt)
				/ divisors[divisorIndex]);
		String remainder = String.valueOf(Integer.parseInt(stringToEncrypt)
				% divisors[divisorIndex]);
		int quotientLength = quotient.length();
		int quotientLengthLength = String.valueOf(quotientLength).length();
		int remainderLength = remainder.length();
		StringBuffer encryptedStringBuffer = new StringBuffer();
		encryptedStringBuffer.append(quotientLength).append(quotient).append(
				remainderLength).append(remainder);

		int encryptedStringLength = encryptedStringBuffer.length();

		for (int i = 0; i < (14 - encryptedStringLength); i++)
			encryptedStringBuffer
					.append(((Integer.parseInt(encryptedStringBuffer.substring(
							i, i + 1)) + 14) - i) % 10);

		if ((divisorIndex % 2) == 1) {
			encryptedStringBuffer.reverse();
		}

		if (divisorIndex == 0) {
			divisorIndex = quotientLength;
			quotientLengthLength += 5;
		}

		encryptedStringBuffer.insert(0, quotientLengthLength).insert(0,
				divisorIndex);

		return encryptedStringBuffer.toString();
	}

	public static String encryptSimpleTrx(String stringToEncrypt) {
		String encryptedString = encryptSimple(stringToEncrypt);
		try {
			String postfix = encryptedString
					.substring(encryptedString.length() - 1);
			encryptedString = encryptedString.substring(0, encryptedString
					.length() - 1);
			char c = (char) (65 + Integer.parseInt(postfix));
			return "T" + encryptedString + c;
		} catch (Exception e) {
			e.printStackTrace();
			return encryptedString + "U";
		}
	}

	public static String encryptSimpler(String fix, String stringToEncrypt) {
		String encryptedString = encryptSimple(stringToEncrypt);
		try {
			String postfix = encryptedString
					.substring(encryptedString.length() - 1);
			encryptedString = encryptedString.substring(0, encryptedString
					.length() - 1);
			char c = (char) (65 + Integer.parseInt(postfix));
			return fix + encryptedString + c;
		} catch (Exception e) {
			e.printStackTrace();
			return encryptedString + "U";
		}
	}

	public static String encryptShortSimpler(String stringToEncrypt) {
		String encryptedString = encryptShortSimple(stringToEncrypt);

		// System.out.println(encryptedString);
		try {
			// String postfix =
			// encryptedString.substring(encryptedString.length()-1);
			encryptedString = encryptedString.substring(0, 8);
			// char c = (char)(65+Integer.parseInt(postfix));
			return encryptedString;
		} catch (Exception e) {
			e.printStackTrace();
			return encryptedString;
		}
	}

	public static String decryptSimple(String stringToDecrypt) {
		StringBuffer encryptedStringBuffer = new StringBuffer(stringToDecrypt);

		if (encryptedStringBuffer.length() < 16) {
			return stringToDecrypt;
		}

		int divisorIndex = Integer.parseInt(encryptedStringBuffer.substring(0,
				1));
		int quotientLengthLength = Integer.parseInt(encryptedStringBuffer
				.substring(1, 2));

		if (quotientLengthLength > 5) {
			quotientLengthLength -= 5;
			divisorIndex = 0;
		}

		int nextRead = 2;

		if ((divisorIndex % 2) == 1) {
			encryptedStringBuffer.reverse();
			nextRead = 0;
		}

		int quotientLength = Integer.parseInt(encryptedStringBuffer.substring(
				nextRead, nextRead += quotientLengthLength));
		long quotient = Long.parseLong(encryptedStringBuffer.substring(
				nextRead, nextRead += quotientLength));
		int remainderLength = Integer.parseInt(encryptedStringBuffer.substring(
				nextRead++, nextRead));
		int remainder = Integer.parseInt(encryptedStringBuffer.substring(
				nextRead, nextRead + remainderLength));

		return String.valueOf((quotient * divisors[divisorIndex]) + remainder);
	}

	/*public static void main(String[] arg) throws Exception {
		Set set = new HashSet();
		for (int i = 0; i < 500000; i++) {
			String resultStr = encryptShortSimpler(i + "");
			System.out.println(resultStr); //
			// System.out.println(encryptSimplefo"1"));

			set.add(resultStr);
			// System.out.println(encryptSimpleTrx("35555555"));

		}
		System.out.println("set.size:" + set.size());
	}*/
}
