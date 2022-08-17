package com.john.utils;

import java.util.logging.Logger;

public class Utils {
	
	public static void logExecutionDuration(long start, String name, Logger logger) {
		long end = System.currentTimeMillis();
		logger.info(String.format("%s took %dms to execute", name, end - start));
	}
	
	public static String maskPhoneNumber(String numberToMask) {
		String lastFourDigits = numberToMask.substring(6);
		return "XXX-XXX-".concat(lastFourDigits);
	}
	
	public static String maskEmail(String emailToMask) {
		String firstFourChars = emailToMask.substring(0, 4);
		String domain = emailToMask.substring(emailToMask.indexOf('@'));
		return firstFourChars.concat("xxxxxxxxxxx").concat(domain);
	}
}
