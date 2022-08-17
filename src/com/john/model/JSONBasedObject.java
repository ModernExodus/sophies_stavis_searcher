package com.john.model;

import org.json.JSONObject;

public abstract class JSONBasedObject {
	private final JSONObject rawObject;
	
	protected JSONBasedObject(JSONObject object) {
		rawObject = object;
	}
	
	@Override
	public String toString() {
		return rawObject.toString(2);
	}
}
