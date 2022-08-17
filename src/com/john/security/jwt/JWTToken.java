package com.john.security.jwt;

import java.time.Instant;

/** 
 * Defines the interface that any concrete implementations must follow. It represents 
 * a <code>JWTToken</code> used to contact authentication servers, generally to obtain
 * an <code>AccessToken</code>.
 */
public interface JWTToken {
	public String toTransmissionReadyToken();
	public boolean hasExpired();
	public String getUrlEncodedHeader();
	public String getUrlEncodedClaims();
	public String getScope();
	public String getAudience();
	public Instant getIssuedAt();
	public Instant getExpiresAt();
}
