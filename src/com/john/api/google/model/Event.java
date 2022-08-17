package com.john.api.google.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import com.john.model.JSONBasedObject;

public class Event extends JSONBasedObject implements Comparable<Event> {
	private final String id;
	private final String status;
	private final String summary;
	private final String location;
	private final LocalDateTime start;
	private final LocalDateTime end;
	
	public Event(JSONObject object) {
		super(object);
		id = object.optString("id", "");
		status = object.optString("status", "");
		summary = object.optString("summary", "");
		location = object.optString("location", "");
		
		String startStr = (String) object.optQuery("/start/dateTime");
		if (startStr != null) {
			start = LocalDateTime.parse(startStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		} else {
			start = null;
		}
		
		String endStr = (String) object.optQuery("/end/dateTime");
		if (endStr != null) {
			end = LocalDateTime.parse(endStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		} else {
			end = null;
		}
	}
	
	public String getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public String getSummary() {
		return summary;
	}

	public String getLocation() {
		return location;
	}
	
	public LocalDateTime getStart() {
		return start;
	}
	
	public LocalDateTime getEnd() {
		return end;
	}

	@Override
	public int compareTo(Event o) {
		if (start.isBefore(o.getStart())) {
			return -1;
		}
		if (start.isAfter(o.getStart())) {
			return 1;
		}
		return 0;
	}
}
