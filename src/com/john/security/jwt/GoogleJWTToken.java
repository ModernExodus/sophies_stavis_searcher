package com.john.security.jwt;

import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import com.john.security.crypto.SHA256WithRSASigningStrategy;
import com.john.security.crypto.SigningStrategy;

/** 
 * A child of <code>JWTTokenTemplate</code> that resolves required claims needed to 
 * construct a <code>GoogleJWTToken</code> to interact with Google authentication servers. It does so
 * by overriding the abstract methods defined in <code>JWTTokenTemplate</code>. It is
 * a singleton, and will return an existing <code>GoogleJWTToken</code> if one exists, is not expired,
 * and the private key has not changed since the last token was requested.
 */
public final class GoogleJWTToken extends JWTTokenTemplate {
	private static final Logger log = Logger.getLogger(GoogleJWTToken.class.getPackageName());
	private static GoogleJWTToken instance;
	private static String privateKey;
	
	// descriptor of the intended target of the assertion
	private static final String AUD = "https://oauth2.googleapis.com/token";
	
	// email of service account
	private static final String ISS = "querycalendar@sophies-stavi-searcher.iam.gserviceaccount.com";
	
	// space-delimited list of the permissions
	private static final String SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
	
	private GoogleJWTToken(SigningStrategy signingStrategy) {
		super(signingStrategy);
		log.info("Successfully initialized a new GoogleJWTToken");
	}

	@Override
	protected String resolveAlg() {
		return "RS256";
	}

	@Override
	protected String resolveAud() {
		return AUD;
	}

	@Override
	protected String resolveIss() {
		return ISS;
	}

	@Override
	protected String resolveScope() {
		return SCOPE;
	}
	
	public static synchronized GoogleJWTToken getInstance(String googlePrivateKey) throws InvalidKeySpecException {
		log.info("New GoogleJWTToken requested");
		if (instance == null) {
			privateKey = googlePrivateKey;
			return new GoogleJWTToken(new SHA256WithRSASigningStrategy(googlePrivateKey));
		}
		if (instance.hasExpired()) {
			privateKey = googlePrivateKey;
			return new GoogleJWTToken(new SHA256WithRSASigningStrategy(googlePrivateKey));
		}
		if (!googlePrivateKey.equals(privateKey)) {
			privateKey = googlePrivateKey;
			return new GoogleJWTToken(new SHA256WithRSASigningStrategy(googlePrivateKey));
		}
		log.info("Existing GoogleJWTToken is still valid --> returning existing token");
		return instance;
	}
}
