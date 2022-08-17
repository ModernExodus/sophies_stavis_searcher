package com.john.security.oauth.google;

import static com.john.utils.Utils.logExecutionDuration;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.john.security.jwt.GoogleJWTToken;
import com.john.security.jwt.JWTToken;
import com.john.security.oauth.AccessToken;
import com.john.security.oauth.AccessTokenRetrievalStrategy;
import com.john.utils.ApplicationPropertyProvider;
import com.john.utils.FileReader;
import com.john.utils.MockDataProvider;
import com.john.utils.SimpleFileReader;
import com.john.utils.http.HttpClientHelper;
import com.john.utils.http.HttpHeader;

/** 
 * An <code>AccessTokenRetrievalStrategy</code> able to obtain a valid <code>AccessToken</code> from
 * Google authentication servers.
 */
public class GoogleAccessTokenRetrievalStrategy implements AccessTokenRetrievalStrategy {
	private static final Logger log = Logger.getLogger(GoogleAccessTokenRetrievalStrategy.class.getCanonicalName());
	private static final String PRIVATE_KEY_LOCATION = "./resources/google/sophies-stavi-searcher-pk.txt";
	private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
	private static GoogleAccessTokenRetrievalStrategy googleRetrievalStrategy;
	
	private GoogleAccessTokenRetrievalStrategy() {}

	@Override
	public Optional<AccessToken> retrieveAccessToken() {
		log.info("Attempting to retrieve a Google Access Token");
		JWTToken jwtToken = getGoogleJWTToken(readPrivateKey());
		return Optional.ofNullable(fetchNewAccessToken(jwtToken));
	}
	
	private String readPrivateKey() {
		FileReader fr = new SimpleFileReader(200);
		try {
			return fr.readFile(PRIVATE_KEY_LOCATION);
		} catch (IOException e) {
			log.severe(String.format("Failed to retrieve private key to generate JWT to get Google Access Token: %s",
					e.getMessage()));
			return "";
		}
	}
	
	private JWTToken getGoogleJWTToken(String privateKey) {
		try {
			return GoogleJWTToken.getInstance(privateKey);
		} catch (InvalidKeySpecException e) {
			log.severe("Invalid private key used when attempting to get GoogleJWTToken");
			return null;
		}
	}
	
	private AccessToken fetchNewAccessToken(JWTToken token) {
		JSONObject body = new JSONObject();
		body.put("grant_type", GRANT_TYPE);
		body.put("assertion", token.toTransmissionReadyToken());
		
		final String operationName = "Fetch Google Access Token";
		final long start = System.currentTimeMillis();
		
		log.info("Fetching new Google Access Token");
		HttpResponse<String> response = null;
		JSONObject responseBody = null;
		if (ApplicationPropertyProvider.shouldUseMocks()) {
			responseBody = MockDataProvider.getGoogleAccessKey();
		} else {
			response = HttpClientHelper.POST(token.getAudience(), body.toString(), HttpHeader.JSON_CONTENT_TYPE).get();
			responseBody = new JSONObject(response.body());
		}
		if (responseBody.has("error")) {
			log.severe(responseBody.getString("error_description"));
			logExecutionDuration(start, operationName, log);
			return null;
		} else {
			logExecutionDuration(start, operationName, log);
			return convertToAccessToken(responseBody, token.getScope());
		}
	}
	
	private GoogleAccessToken convertToAccessToken(JSONObject response, String jwtScope) {
		String scope = response.has("scope") ? response.getString("scope") : jwtScope;
		return new GoogleAccessToken(response.getString("access_token"), scope, response.getInt("expires_in"));
	}
	
	public static synchronized GoogleAccessTokenRetrievalStrategy getInstance() {
		if (googleRetrievalStrategy == null) {
			log.info("Constructing new Google Access Token Retrieval Strategy");
			googleRetrievalStrategy = new GoogleAccessTokenRetrievalStrategy();
		}
		return googleRetrievalStrategy;
	}

}
