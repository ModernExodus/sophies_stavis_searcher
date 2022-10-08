package com.john.application;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.john.api.google.CalendarServiceImpl;
import com.john.api.google.model.Event;
import com.john.notifications.EmailNotificationService;
import com.john.notifications.NotificationService;
import com.john.notifications.TextNotificationService;
import com.john.notifications.model.Recipient;
import com.john.security.oauth.AccessToken;
import com.john.security.oauth.AccessTokenService;
import com.john.security.oauth.AccessTokenServiceProvider;
import com.john.security.oauth.google.GoogleAccessToken;
import com.john.utils.providers.ApplicationPropertyProvider;
import com.john.utils.providers.SubscriberProvider;
import com.john.utils.providers.ApplicationPropertyProvider.Property;
import com.john.security.oauth.AccessTokenService.AccessTokenStrategy;

/**
 * The class containing the majority of the business logic. It's the brain
 * of the application, and must be able to trigger a query, interpret the results,
 * and trigger notifications. It implements <code>Runnable</code> so that it can 
 * execute in its own thread.
 */
public class StavisQueryOperator implements Runnable {
	private static final Logger log = Logger.getLogger(StavisQueryOperator.class.getCanonicalName());
	private static final int MAX_DAYS;
	private static final String CALENDAR_ID;
	private static final String KEYWORD;
	
	static {
		MAX_DAYS = ApplicationPropertyProvider.getIntProperty(Property.QUERY_MAX_DAYS);
		CALENDAR_ID = ApplicationPropertyProvider.getProperty(Property.CALENDAR_ID);
		KEYWORD = ApplicationPropertyProvider.getProperty(Property.QUERY_KEYWORD);
	}

	@Override
	public void run() {
		log.info("Stavi's Query Operator now running in Thread #" + Thread.currentThread().getId());
		try {
			processEvents(queryEvents());
			log.info("Stavi's Query Operator has completed this notification cycle.");
		} catch (Exception e) {
			log.severe("The following exception prevented this notification cycle from completing: ".concat(e.toString()));
		}
	}
	
	// returns a list of events sorted based on start time in asc order
	private List<Event> queryEvents() {
		AccessTokenService accessService = AccessTokenServiceProvider.getAccessTokenService();
		Optional<AccessToken> token = accessService.retrieveAccessToken(AccessTokenStrategy.GOOGLE);
		if (token.isEmpty()) {
			log.severe("Failed to retrieve access token --> Query Operator will not proceed");
			throw new RuntimeException("No Google Access Token");
		}
		
		CalendarServiceImpl calendar = new CalendarServiceImpl(CALENDAR_ID);
		Optional<List<Event>> results = null;
		if (KEYWORD.isEmpty()) {
			results = calendar.getEventsForNextNDays((GoogleAccessToken) token.get(), MAX_DAYS);
		} else {
			results = calendar.searchEventsForNextNDays((GoogleAccessToken) token.get(), MAX_DAYS, KEYWORD);
		}
		if (results.isEmpty()) {
			log.severe("Failed to retrieve events from the Calendar API --> Query Operator will not proceed");
			throw new RuntimeException("Calendar API request failed");
		}
		
		List<Event> sortedEvents = results.get();
		sortedEvents.sort(null);
		return sortedEvents;
	}
	
	private void processEvents(List<Event> events) {
		final String msgSubject = "Stavi's Searcher Alert";
		String msgBody = "";
		if (events.isEmpty()) {
			log.info("No events found with the specified properties");
			msgBody = String.format("We're sorry, but Stavi's is not coming in the next %d days", MAX_DAYS);
		} else {
			log.info(String.valueOf(events.size()).concat(" events found with the specified properties"));
			msgBody = formatEvent(events.get(0));
		}
		
		NotificationService emailService = new EmailNotificationService();
		NotificationService textService = new TextNotificationService();
		Recipient[] recipients = SubscriberProvider.getSubscribers();
		for (Recipient recipient : recipients) {
			if (recipient.getEmailEnabled()) {
				emailService.notify(recipient, msgSubject, msgBody);
			}
			if (recipient.getSmsEnabled()) {
				textService.notify(recipient, msgSubject, msgBody);
			}
		}
	}
	
	private String formatEvent(Event event) {
		return String.format("Stavi's will be at %s on %s at %s", event.getLocation(),
				event.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE),
				event.getStart().format(DateTimeFormatter.ofPattern("hh:mm a")));
	}

}
