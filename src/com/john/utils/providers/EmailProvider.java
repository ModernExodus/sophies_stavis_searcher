package com.john.utils.providers;

import java.io.IOException;
import java.util.logging.Logger;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import com.john.utils.FileReader;
import com.john.utils.providers.ApplicationPropertyProvider.Property;
import com.john.utils.providers.secrets.MissingSecretException;
import com.saltweaver.salting.api.InvalidSaltingStrategyException;

public class EmailProvider {
	private static final Logger log = Logger.getLogger(EmailProvider.class.getCanonicalName());
	private static final String SENDER_NAME;
	private static final String SENDER_EMAIL;
	private static final String SMTP_SERVER;
	private static final int SMTP_PORT;
	private static final String SENDER_PASSWORD;
	
	static {
		SENDER_NAME = ApplicationPropertyProvider.getProperty(Property.EMAIL_SENDER_NAME);
		SENDER_EMAIL = ApplicationPropertyProvider.getProperty(Property.EMAIL_ADDRESS);
		SMTP_SERVER = ApplicationPropertyProvider.getProperty(Property.EMAIL_SMTP_SERVER);
		SMTP_PORT = ApplicationPropertyProvider.getIntProperty(Property.EMAIL_SMTP_PORT);

		try {
			FileReader fr = FileReader.standardDecryptionReader("./resources/google/gmail_app_pw.txt", 50);
			SENDER_PASSWORD = FileReader.toUTF8String(fr.readFile());
		} catch (IOException | MissingSecretException | InvalidSaltingStrategyException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static EmailPopulatingBuilder baseEmailBuilder() {
		return EmailBuilder.startingBlank().from(SENDER_NAME, SENDER_EMAIL);
	}
	
	public static void sendMail(Email email) {
		if (ApplicationPropertyProvider.getBooleanProperty(Property.NOTIFICATIONS_ENABLED)) {
			baseMailer().sendMail(email);
		} else {
			log.warning("Notifications are disabled. No mail will be sent.");
		}
	}
	
	public static String getDefaultSender() {
		return SENDER_EMAIL;
	}
	
	private static Mailer baseMailer() {
		return MailerBuilder.withSMTPServer(SMTP_SERVER, SMTP_PORT, SENDER_EMAIL, SENDER_PASSWORD)
				.withTransportStrategy(TransportStrategy.SMTP_TLS).buildMailer();
	}

}
