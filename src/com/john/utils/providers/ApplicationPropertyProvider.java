package com.john.utils.providers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Keeps a record of application properties loaded during application initialization from application.properties.
 * It should be the only way to access properties.
 */
public final class ApplicationPropertyProvider {
	private static final Logger log = Logger.getLogger(ApplicationPropertyProvider.class.getCanonicalName());
	private static final Properties APP_PROPERTIES;
	
	static {
		APP_PROPERTIES = new Properties();
		try (FileInputStream fis = new FileInputStream("./resources/application.properties")) {			
			APP_PROPERTIES.load(fis);
		} catch (IOException e) {
			log.severe("Failed to load application properties");
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static boolean shouldUseMocks() {
		return getBooleanProperty(Property.USE_MOCKS);
	}
	
	public static String getProperty(Property property) {
		return APP_PROPERTIES.getProperty(property.toString());
	}
	
	public static String getProperty(Property property, String defaultValue) {
		if (hasProperty(property)) {
			return getProperty(property);
		}
		return defaultValue;
	}
	
	public static int getIntProperty(Property property) {
		return Integer.parseInt(APP_PROPERTIES.getProperty(property.toString()));
	}
	
	public static int getIntProperty(Property property, int defaultValue) {
		if (hasProperty(property)) {
			return getIntProperty(property);
		}
		return defaultValue;
	}
	
	public static long getLongProperty(Property property) {
		return Long.parseLong(APP_PROPERTIES.getProperty(property.toString()));
	}
	
	public static long getLongProperty(Property property, long defaultValue) {
		if (hasProperty(property)) {
			return getLongProperty(property);
		}
		return defaultValue;
	}
	
	public static boolean getBooleanProperty(Property property) {
		return Boolean.parseBoolean(APP_PROPERTIES.getProperty(property.toString()));
	}
	
	public static boolean hasProperty(Property property) {
		return APP_PROPERTIES.containsKey(property.toString());
	}
	
	public static enum Property {
		USE_MOCKS("mockdata.enabled"),
		PROD("prod"),
		QUERY_MAX_DAYS("query.maxdays"),
		QUERY_FREQUENCY_MINUTES("query.frequency"),
		QUERY_INITIAL_DELAY("query.initialdelay"),
		QUERY_KEYWORD("query.keyword"),
		CALENDAR_ID("calendar.id"),
		NOTIFICATIONS_ENABLED("notifications.enabled"),
		EMAIL_ENABLED("email.enabled"),
		EMAIL_ADDRESS("email.address"),
		EMAIL_SUBJECT("email.subject"),
		EMAIL_SMTP_SERVER("email.smtp.server"),
		EMAIL_SMTP_PORT("email.smtp.port"),
		EMAIL_SENDER_NAME("email.sender.name"),
		SMS_ENABLED("sms.enabled"),
		SMS_SUBJECT("sms.subject"),
		LOGGING_ROTATION_FREQUENCY("logging.rotation.frequency"),
		LOGGING_ROTATION_PREFIX("logging.rotation.prefix"),
		HEALTHCHECKER_ENABLED("healthchecker.enabled"),
		HEALTHCHECKER_FREQUENCY("healthchecker.frequency"),
		HEALTHCHECKER_INITIAL_DELAY("healthchecker.initialdelay"),
		HEALTHCHECKER_SUBJECT("healthchecker.subject"),
		HEALTHCHECKER_MESSAGE("healthchecker.message");
		
		private String value;
		Property(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
}
