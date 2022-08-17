package com.john.security.oauth;

/**
 * An data model representing an access token and its metadata as returned from an authentication server.
 * It should be able to deduce if it has expired based on when it was issued.
 */
public interface AccessToken {
	public String getRawAccessToken();
	public String getScope();
	public String getTokenType();
	public boolean hasExpired();
}
