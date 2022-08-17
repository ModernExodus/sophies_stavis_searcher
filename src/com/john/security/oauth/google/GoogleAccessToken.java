package com.john.security.oauth.google;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.john.security.oauth.AccessToken;

/**
 * An implementation of <code>AccessToken</code> representing a token returned by Google 
 * authentication servers.
 */
public class GoogleAccessToken implements AccessToken {
	private static final Logger log = Logger.getLogger(GoogleAccessToken.class.getCanonicalName());
	private static final String TOKEN_TYPE = "Bearer";
	private String bearerToken;
	private String scope;
	private Instant expiry;
	
	public GoogleAccessToken(String bearerToken, String scope, int expiresIn) {
		this.bearerToken = bearerToken;
		this.scope = scope;
		expiry = Instant.now().plusSeconds(expiresIn);
		LocalDateTime expiresAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());
		log.info(String.format("Successfully built GoogleAccessToken with scope=[%s] that expires at %s", scope,
				expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
	}

	@Override
	public String getRawAccessToken() {
		return bearerToken;
	}

	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String getTokenType() {
		return TOKEN_TYPE;
	}

	@Override
	public boolean hasExpired() {
		return Instant.now().isAfter(expiry);
	}
	
	@Override
	public String toString() {
		return String.format("{access_token: %s,%n scope: %s,%n token_type: %s,%n expiresIn: %d}", getRawAccessToken(),
				getScope(), getTokenType(), expiry.getEpochSecond() - Instant.now().getEpochSecond());
	}

}
