package com.john.notifications;

import org.simplejavamail.api.email.Email;

import com.john.notifications.model.Recipient;
import com.john.utils.providers.EmailProvider;

public class HtmlEmailNotificationService extends EmailNotificationService {
	
	@Override
	protected Email composeEmail(Recipient recipient, String subject, String message) {
		return EmailProvider.baseEmailBuilder().to(recipient.getFirstName(), recipient.getEmail()).withSubject(subject)
				.withHTMLText(message).buildEmail();
	}
}
