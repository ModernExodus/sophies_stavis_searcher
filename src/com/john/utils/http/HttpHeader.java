package com.john.utils.http;

public class HttpHeader {
	public static final HttpHeader JSON_CONTENT_TYPE;
	
	static {
		JSON_CONTENT_TYPE = new HttpHeader("Content-Type", "application/json");
	}
	
	private String name;
	private String value;
	
	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final class HttpBearerAuthorizationHeader extends HttpHeader {
		public HttpBearerAuthorizationHeader(String bearerToken) {
			super("Authorization", String.format("Bearer %s", bearerToken));
		}
	}
}
