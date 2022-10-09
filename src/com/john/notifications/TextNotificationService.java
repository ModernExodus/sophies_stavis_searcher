package com.john.notifications;

import static com.john.utils.Utils.maskPhoneNumber;

import java.util.logging.Logger;

import org.simplejavamail.api.email.Email;

import com.john.notifications.model.Recipient;
import com.john.utils.providers.ApplicationPropertyProvider;
import com.john.utils.providers.EmailProvider;
import com.john.utils.providers.ApplicationPropertyProvider.Property;

public class TextNotificationService implements NotificationService {
	private static final Logger log = Logger.getLogger(TextNotificationService.class.getCanonicalName());

	@Override
	public void notify(Recipient recipient, String subject, String message) {
		String toAddress = recipient.getPhoneNumber().concat(recipient.getPhoneProvider().getEmailExtension());
		Email email = EmailProvider.baseEmailBuilder().to(recipient.getFirstName(), toAddress).withSubject(subject)
				.withPlainText(limitMessageSize(subject, message, EmailProvider.getDefaultSender(), recipient.getPhoneProvider().getMaxLength()))
				.buildEmail();
		
		try {
			if (ApplicationPropertyProvider.getBooleanProperty(Property.SMS_ENABLED)) {
				log.info(String.format("Sending SMS text to: %s over the %s network",
						maskPhoneNumber(recipient.getPhoneNumber()), recipient.getPhoneProvider()));
				EmailProvider.sendMail(email);
				log.info(String.format("Successfully sent SMS text to: %s", maskPhoneNumber(recipient.getPhoneNumber())));
			} else {
				log.warning(String.format("SMS notifications are disabled. No text will be sent to %s", maskPhoneNumber(recipient.getPhoneNumber())));
			}
		} catch (RuntimeException e) {
			log.severe(String.format("Failed to send SMS text to %s due to [%s] with root cause [%s]",
					maskPhoneNumber(recipient.getPhoneNumber()), e.getMessage(), e.getCause()));
		}
	}
	
	// phone carriers limit the length of messages sent
	private String limitMessageSize(String subject, String message, String sender, short maxLength) {
		if (subject.length() + message.length() + sender.length() > maxLength) {
			int diff = subject.length() + message.length() + sender.length() - maxLength;
			return message.substring(0, message.length() - 3 - diff).concat("...");
		}
		return message;
	}

}
