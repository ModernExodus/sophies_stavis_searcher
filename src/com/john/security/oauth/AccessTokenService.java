package com.john.security.oauth;

import java.util.Optional;

/** 
 * Defines the API for <code>AccessTokenService</code> implementations, namely that they
 * must return an <code>AccessToken</code> if one exists based on an <code>AccessTokenStrategy</code>
 */
public interface AccessTokenService {
	/**
	 * Given an <code>AccessTokenStrategy</code>, will return an <code>Optional</code> object
	 * containing the valid <code>AccessToken</code> if the appropriate <code>AccessTokenRetrievalStrategy</code>
	 * was able to obtain a valid token. If obtaining the token failed for any reason, will return an
	 * empty <code>Optional</code>.
	 */
	public Optional<AccessToken> retrieveAccessToken(AccessTokenStrategy strategy);
	
	/**
	 * Defines the supported Access Token Strategies that can be used to get an
	 * <code>AccessToken</code>
	 */
	public enum AccessTokenStrategy {
		GOOGLE("Google OAuth Token");
		
		private String name;
		private AccessTokenStrategy(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}
