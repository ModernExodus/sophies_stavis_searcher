package com.john.utils;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import com.john.utils.providers.secrets.MissingSecretException;
import com.john.utils.providers.secrets.SecretProvider;
import com.john.utils.providers.secrets.SecretProvider.Secret;
import com.saltweaver.salting.api.InvalidSaltingStrategyException;
import com.saltweaver.salting.api.SaltingStrategy;

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
	
	public static SaltingStrategy getSaltingStrategy() throws MissingSecretException, InvalidSaltingStrategyException {
		int saltLength = SecretProvider.getIntSecret(Secret.SALTING_STRATEGY_LENGTH);
		String saltingComposition = SecretProvider.getSecret(Secret.SALTING_STRATEGY_COMPOSITION);
		return SaltingStrategy.getStrategy(Arrays.asList(saltingComposition.split(",")), saltLength);
	}
	
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		return file.exists() && file.delete();
	}
}
