package com.john.application.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.john.utils.ApplicationPropertyProvider;
import com.john.utils.ApplicationPropertyProvider.Property;

/**
 * Responsible for loading the appropriate log properties file, and kicking off the scheduled task
 * of rotating log files. All interactions with the <code>LogManager</code> should occur through this class.
 */
public class CustomLogManagement {
	private static final Logger log = Logger.getLogger(CustomLogManagement.class.getCanonicalName());
	private static final String FILE_HANDLER_PATTERN_KEY = "java.util.logging.FileHandler.pattern";
	private static final String HANDLERS_KEY = "handlers";
	private static final String FILE_HANDLER = "java.util.logging.FileHandler";
	
	/**
	 * Read the logging.properties file and apply the configs globally
	 */
	public static void enableApplicationLogging() {
		readConfiguration();
		log.fine("Enabled application logging");
	}
	
	/**
	 * Enable the use of different logging files swapped out at
	 * predefined time intervals instead of always logging to the same
	 * file. This only takes effect if a FileHandler is in use. The
	 * provided <code>ScheduledExecutorService</code> instance will be in
	 * charge of rotating log files. The log rotation will begin after an
	 * initial delay of the specified frequency.
	 */
	public static void enableRotatingLogs(ScheduledExecutorService executor) {
		LogManager manager = LogManager.getLogManager();
		if (manager.getProperty(HANDLERS_KEY).contains(FILE_HANDLER)) {
			long frequency = Long.parseLong(ApplicationPropertyProvider.getProperty(Property.LOGGING_ROTATION_FREQUENCY));
			log.fine(String.format("Enabling log rotation --> Logs will be rotated every %d minutes", frequency));
			
			try {
				manager.updateConfiguration(key -> {
					if (key.equals(FILE_HANDLER_PATTERN_KEY)) {
						System.out.println(constructFileHandlerPattern());
						return (oldV, newV) -> constructFileHandlerPattern();
					}
					return (oldV, newV) -> oldV;
				});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static void readConfiguration() {
		LogManager logManager = LogManager.getLogManager();
		File logFile = null;
		if (ApplicationPropertyProvider.getBooleanProperty(Property.PROD)) {
			log.info("Prod logging properties will be used.");
			logFile = new File("./resources/prod_logging.properties");
		} else {
			log.info("Dev logging properties will be used.");
			logFile = new File("./resources/dev_logging.properties");
		}
		
		try {
			logManager.readConfiguration(new FileInputStream(logFile));
		} catch (IOException e) {
			log.severe("Failed to enable application logging due to: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	private static String constructFileHandlerPattern() {
		LogManager manager = LogManager.getLogManager();
		final Path filePath = Path.of(manager.getProperty(FILE_HANDLER_PATTERN_KEY));
		String fileName = filePath.getName(filePath.getNameCount() - 1).toString();
		fileName = LocalDateTime.now()
				.format(DateTimeFormatter
						.ofPattern(ApplicationPropertyProvider.getProperty(Property.LOGGING_ROTATION_PREFIX)))
				.concat(fileName);
		return filePath.resolveSibling(fileName).toString().replaceAll("\\\\", "/");
	}
}
