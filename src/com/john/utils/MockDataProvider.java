package com.john.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;

/**
 * Should be used to load mock data from JSON files
 */
public final class MockDataProvider {
	private final static Logger log = Logger.getLogger(MockDataProvider.class.getCanonicalName());
	private final static Map<String, JSONObject> cachedMocks;
	private final static boolean shouldUseMocks;
	
	static {
		if (shouldUseMocks = ApplicationPropertyProvider.shouldUseMocks()) {
			log.info("Mocks are enabled --> initializing with mock data");
			cachedMocks = new HashMap<>();
			try {
				loadMockData();
			} catch (RuntimeException | IOException e) {
				log.severe(String.format("Failed to load all mock data due to %s", e.getMessage()));
				throw new RuntimeException(e.getMessage());
			}
		} else {
			log.warning("Initializing MockDataProvider, even though mocks are disabled --> using real data");
			cachedMocks = null;
		}
	}
	
	public static JSONObject getMockCalendarListData() {
		assertMocksActive();
		return cachedMocks.get(MOCK_DATA.CALENDAR_LIST_RESPONSE.getKey());
	}
	
	public static JSONObject getMockEventsNextNDays() {
		assertMocksActive();
		return cachedMocks.get(MOCK_DATA.EVENT_LIST_N_DAYS.getKey());
	}
	
	public static JSONObject getSearchedEventsNextNDays() {
		assertMocksActive();
		return cachedMocks.get(MOCK_DATA.SEARCH_EVENTS_N_DAYS.getKey());
	}
	
	public static JSONObject getGoogleAccessKey() {
		assertMocksActive();
		return cachedMocks.get(MOCK_DATA.GOOGLE_ACCESS_TOKEN.getKey());
	}
	
	private static void loadMockData() throws IOException {
		for (MOCK_DATA mock : MOCK_DATA.values()) {
			cachedMocks.put(mock.getKey(), new JSONObject(new SimpleFileReader(mock.getFileLocation(), 1000).readFile()));
			log.fine(String.format("Loaded mock data with key=%s", mock.getKey()));
		}
	}
	
	private static void assertMocksActive() {
		if (!shouldUseMocks) {
			throw new RuntimeException("Mocks are disabled, yet you are trying to use mock data.");
		}
	}
	
	private static enum MOCK_DATA {
		CALENDAR_LIST_RESPONSE("CalendarList", "./calendar_list_0808.json"),
		EVENT_LIST_N_DAYS("EventListNDays", "./events_next_14_days.json"),
		SEARCH_EVENTS_N_DAYS("SearchEventListNDays", "./event_list_queried_45_days.json"),
		GOOGLE_ACCESS_TOKEN("GoogleAccessToken", "./mock_google_access_token.json");
		
		String key;
		String fileLocation;
		MOCK_DATA(String key, String fileLocation) {
			this.key = key;
			this.fileLocation = fileLocation;
		}
		
		String getKey() {
			return key;
		}
		
		String getFileLocation() {
			return fileLocation;
		}
	}
}
