package com.john.security.oauth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.john.security.oauth.google.GoogleAccessTokenRetrievalStrategy;

/**
 * An implementation of <code>AccessTokenService</code> that can get <code>AccessToken</code>
 * objects as described in a provided <code>AccessTokenStrategy</code>. It caches access tokens
 * as it receives them, and will return an existing access token instead of getting a new one
 * if it exists and is not expired. Access tokens are maintained in a synchronized map, so can be safely
 * requested and accessed by multiple threads.
 */
public class AccessTokenServiceImpl implements AccessTokenService {
	private static final Logger log = Logger.getLogger(AccessTokenServiceImpl.class.getCanonicalName());
	
	private final Map<AccessTokenStrategy, AccessToken> currentAccessTokens;
	
	AccessTokenServiceImpl() {
		currentAccessTokens = Collections.synchronizedMap(new HashMap<>());
	}

	public Optional<AccessToken> retrieveAccessToken(AccessTokenStrategy strategy) {
		if (currentAccessTokens.get(strategy) != null && !currentAccessTokens.get(strategy).hasExpired()) {
			log.info(String.format("Valid access token exists for %s strategy --> returning existing token", strategy.getName()));
			return Optional.of(currentAccessTokens.get(strategy));
		}
		switch (strategy) {
		case GOOGLE:
			log.info(String.format("No valid access token exists for %s strategy --> returning new token", strategy.getName()));
			AccessTokenRetrievalStrategy googleStrategy = GoogleAccessTokenRetrievalStrategy.getInstance();
			Optional<AccessToken> token = googleStrategy.retrieveAccessToken();
			if (token.isPresent()) {				
				currentAccessTokens.put(AccessTokenStrategy.GOOGLE, token.get());
				return token;
			}
		default:
			return Optional.empty();
		}
	}
}
