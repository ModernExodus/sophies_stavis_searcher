package com.john.application;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.john.notifications.EmailNotificationService;
import com.john.notifications.TextNotificationService;
import com.john.notifications.model.Recipient;
import com.john.utils.providers.ApplicationPropertyProvider;
import com.john.utils.providers.SubscriberProvider;
import com.john.utils.providers.ApplicationPropertyProvider.Property;

/**
 * Use to start and stop periodic health checks. Health checks are simple notifications sent
 * to all registered admins to indicate the application is still operational.
 */
public class HealthChecker {
	private static final Logger log = Logger.getLogger(HealthChecker.class.getCanonicalName());
	private static final Recipient[] ADMINS;
	
	static {
		ADMINS = SubscriberProvider.getAdmins();
	}
	
	/**
	 * Schedules a notification to be sent to every admin recipient using the provided
	 * <code>ScheduledExecutorService</code>. It starts after an initial delay of 1 minute.
	 * */
	public static void start(ScheduledExecutorService executor) {
		if (ApplicationPropertyProvider.getBooleanProperty(Property.HEALTHCHECKER_ENABLED)) {
			final long frequency = ApplicationPropertyProvider.getLongProperty(Property.HEALTHCHECKER_FREQUENCY);
			log.info(String.format("Starting automatic health checks with a frequency of %d minutes", frequency));
			executor.scheduleAtFixedRate(() -> {
				try {
					log.info("The health checker is now running in Thread #" + Thread.currentThread().getId());
					var email = new EmailNotificationService();
					var text = new TextNotificationService();
					String subject = ApplicationPropertyProvider.getProperty(Property.HEALTHCHECKER_SUBJECT);
					String message = ApplicationPropertyProvider.getProperty(Property.HEALTHCHECKER_MESSAGE);
					for (Recipient admin : ADMINS) {
						if (admin.getEmailEnabled()) {
							email.notify(admin, subject, message);
						}
						if (admin.getSmsEnabled()) {
							text.notify(admin, subject, message);
						}
					}
					log.info("The Health Checker has completed this notification cycle");
				} catch (Exception e) {
					log.warning(String.format(
							"Exception was caught during the scheduled health check: [%s]. The health check will try again in %d minutes.",
							e.getMessage(), frequency));
				}
			}, 1, frequency, TimeUnit.MINUTES);
		} else {
			log.warning("Health checks are disabled");
		}
	}
}
