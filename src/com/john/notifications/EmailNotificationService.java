package com.john.notifications;

import static com.john.utils.Utils.maskEmail;

import java.util.logging.Logger;

import org.simplejavamail.api.email.Email;

import com.john.notifications.model.Recipient;
import com.john.utils.ApplicationPropertyProvider;
import com.john.utils.ApplicationPropertyProvider.Property;
import com.john.utils.EmailProvider;

public class EmailNotificationService implements NotificationService {
	private static final Logger log = Logger.getLogger(EmailNotificationService.class.getCanonicalName());

	@Override
	public void notify(Recipient recipient, String subject, String message) {
		Email email = EmailProvider.baseEmailBuilder().to(recipient.getFirstName(), recipient.getEmail())
				.withSubject(subject).withPlainText(message).buildEmail();
		
		try {
			if (ApplicationPropertyProvider.getBooleanProperty(Property.EMAIL_ENABLED)) {
				log.info(String.format("Sending email to %s", maskEmail(recipient.getEmail())));
				EmailProvider.sendMail(email);
				log.info(String.format("Successfully sent email to %s", maskEmail(recipient.getEmail())));
			} else {
				log.warning(String.format("Email notifications are disabled. No email will be sent to %s", maskEmail(recipient.getEmail())));
			}
		} catch (RuntimeException e) {
			log.severe(String.format("Failed to send email to %s due to [%s] with root cause [%s]",
					maskEmail(recipient.getEmail()), e.getMessage(), e.getCause()));
		}
	}

}
