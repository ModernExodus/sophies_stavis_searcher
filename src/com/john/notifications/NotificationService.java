package com.john.notifications;

import com.john.notifications.model.Recipient;

public interface NotificationService {
	
	public void notify(Recipient recipient, String subject, String message);
	
}
