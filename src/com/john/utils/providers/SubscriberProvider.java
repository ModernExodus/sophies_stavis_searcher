package com.john.utils.providers;

import java.io.IOException;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.john.notifications.model.Recipient;
import com.john.utils.FileReader;
import com.john.utils.SimpleFileReader;

/**
 * Keeps a record of all interested parties who wish to receive notifications
 */
public class SubscriberProvider {
	private static final String SUBSCRIBER_FILE = "./resources/active_subscribers.json";
	private static final String ADMIN_FILE = "./resources/admins.json";
	private static final Logger log = Logger.getLogger(SubscriberProvider.class.getCanonicalName());
	private static final Recipient[] SUBSCRIBERS;
	private static final Recipient[] ADMINS;
	
	static {
		SUBSCRIBERS = loadSubscribers();
		ADMINS = loadAdmins();
	}
	
	/**
	 * Returns a deep copy of all active subscribers
	 */
	public static Recipient[] getSubscribers() {
		return deepCloneRecipients(SUBSCRIBERS);
	}
	
	/**
	 * Returns a deep copy of all admins
	 */
	public static Recipient[] getAdmins() {
		return deepCloneRecipients(ADMINS);
	}
	
	private static Recipient[] loadSubscribers() {
		log.info("Loading subscriber data");
		return loadRecipientsFromFile(SUBSCRIBER_FILE);
	}
	
	private static Recipient[] loadAdmins() {
		log.info("Loading administrator data");
		return loadRecipientsFromFile(ADMIN_FILE);
	}
	
	private static Recipient[] deepCloneRecipients(Recipient[] original) {
		Recipient[] recipients = new Recipient[original.length];
		for (int i = 0; i < recipients.length; i++) {
			recipients[i] = new Recipient(original[i]);
		}
		return recipients;
	}
	
	private static Recipient[] loadRecipientsFromFile(String path) {
		FileReader fr = new SimpleFileReader(path, 250);
		try {
			JSONArray subs = new JSONObject(fr.readFile()).getJSONArray("subscribers");
			Recipient[] result = new Recipient[subs.length()];
			for (int i = 0; i < subs.length(); i++) {
				result[i] = new Recipient(subs.getJSONObject(i));
			}
			return result;
		} catch (IOException e) {
			log.severe(String.format("Failed to load subscriber data due to [%s]", e.getMessage()));
			throw new RuntimeException(e);
		}
	}
}
