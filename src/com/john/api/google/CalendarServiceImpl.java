package com.john.api.google;

import static com.john.utils.Utils.logExecutionDuration;

import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.john.api.google.model.Calendar;
import com.john.api.google.model.Event;
import com.john.security.oauth.google.GoogleAccessToken;
import com.john.utils.http.HttpClientHelper;
import com.john.utils.http.HttpHeader;
import com.john.utils.providers.ApplicationPropertyProvider;
import com.john.utils.providers.MockDataProvider;

public class CalendarServiceImpl {
	private static final Logger log = Logger.getLogger(CalendarServiceImpl.class.getCanonicalName());
	private static final String BASE_URL = "https://www.googleapis.com/calendar/v3/calendars/%s/events";
	private static final String TIME_MIN_PARAM = "timeMin";
	private static final String TIME_MAX_PARAM = "timeMax";
	private static final String QUERY_PARAM = "q";
	
	private final String URL;
	
	public CalendarServiceImpl(String calendarId) {
		URL = String.format(BASE_URL, calendarId);
	}

	public Optional<Calendar> getCalendar(GoogleAccessToken token) {
		final String operationName = "getCalendar";
		final long start = System.currentTimeMillis();
		if (ApplicationPropertyProvider.shouldUseMocks()) {
			logExecutionDuration(start, operationName, log);
			return Optional.of(new Calendar(MockDataProvider.getMockCalendarListData()));
		}
		HttpResponse<String> response = HttpClientHelper.GET(URL, Collections.emptyMap(), HttpHeader.JSON_CONTENT_TYPE,
				new HttpHeader.HttpBearerAuthorizationHeader(token.getRawAccessToken())).get();		
		logExecutionDuration(start, operationName, log);
		return Optional.of(new Calendar(new JSONObject(response.body())));
	}
	
	public Optional<List<Event>> getEventsForNextNDays(GoogleAccessToken token, int numDays) {
		final String operationName = "getEventsForNextNDays";
		final long start = System.currentTimeMillis();
		if (ApplicationPropertyProvider.shouldUseMocks()) {
			Calendar calendar = new Calendar(MockDataProvider.getMockEventsNextNDays());
			logExecutionDuration(start, operationName, log);
			return Optional.of(Arrays.asList(calendar.getEvents()));
		}
		
		Map<String, String> qParams = new HashMap<>();
		ZonedDateTime now = ZonedDateTime.now();
		qParams.put(TIME_MIN_PARAM, now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		qParams.put(TIME_MAX_PARAM, now.plusDays(numDays).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		HttpResponse<String> response = HttpClientHelper.GET(URL, qParams, HttpHeader.JSON_CONTENT_TYPE,
				new HttpHeader.HttpBearerAuthorizationHeader(token.getRawAccessToken())).get();
		Calendar calendar = new Calendar(new JSONObject(response.body()));
		logExecutionDuration(start, operationName, log);
		return Optional.of(Arrays.asList(calendar.getEvents()));
	}
	
	public Optional<List<Event>> searchEventsForNextNDays(GoogleAccessToken token, int numDays, String query) {
		final String operationName = "searchEventsForNextNDays";
		final long start = System.currentTimeMillis();
		if (ApplicationPropertyProvider.shouldUseMocks()) {
			Calendar calendar = new Calendar(MockDataProvider.getSearchedEventsNextNDays());
			logExecutionDuration(start, operationName, log);
			return Optional.of(Arrays.asList(calendar.getEvents()));
		}
		
		Map<String, String> qParams = new HashMap<>();
		ZonedDateTime now = ZonedDateTime.now();
		qParams.put(TIME_MIN_PARAM, now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		qParams.put(TIME_MAX_PARAM, now.plusDays(numDays).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		qParams.put(QUERY_PARAM, query);
		HttpResponse<String> response = HttpClientHelper.GET(URL, qParams, HttpHeader.JSON_CONTENT_TYPE,
				new HttpHeader.HttpBearerAuthorizationHeader(token.getRawAccessToken())).get();
		Calendar calendar = new Calendar(new JSONObject(response.body()));
		logExecutionDuration(start, operationName, log);
		return Optional.of(Arrays.asList(calendar.getEvents()));
	}

}
