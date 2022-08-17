package com.john.security.oauth;

import java.util.Optional;

/**
 * Defines the API that concrete implementations must implement. Namely, they should define
 * strategy-specific methodologies for retrieving a valid <code>AccessToken</code>.
 */
public interface AccessTokenRetrievalStrategy {
	/** 
	 * Carries out the necessary operations to get a valid <code>AccessToken</code>. If 
	 * a token was successfully obtained, it should return it wrapped in an <code>Optional</code>
	 * object. Failing to obtain a token for any reason should result in an empty <code>Optional</code>.
	 */
	public Optional<AccessToken> retrieveAccessToken();
}
