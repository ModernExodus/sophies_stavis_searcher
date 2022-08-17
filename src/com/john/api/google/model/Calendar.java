package com.john.api.google.model;

import org.json.JSONArray;
import org.json.JSONObject;

import com.john.model.JSONBasedObject;

public class Calendar extends JSONBasedObject {
	private final String summary;
	private final String timeZone;
	private final Event[] events;
	
	public Calendar(JSONObject object) {
		super(object);
		summary = object.optString("summary", "");
		timeZone = object.optString("timeZone", "");
		JSONArray items = object.optJSONArray("items");
		if (items != null) {			
			events = new Event[items.length()];
			for (int i = 0; i < items.length(); i++) {
				events[i] = new Event(items.getJSONObject(i));
			}
		} else {
			events = new Event[0];
		}
	}
	
	public String getSummary() {
		return summary;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public Event[] getEvents() {
		return events;
	}
}
