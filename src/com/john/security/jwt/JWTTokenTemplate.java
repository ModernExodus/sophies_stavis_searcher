package com.john.security.jwt;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.john.security.crypto.SigningStrategy;

/**
 * A class following the Template design pattern that is capable of building <code>JWTToken</code>
 * objects by following predefined steps. Concrete classes must extend this abstract class and override methods to provide
 * required components of JWT tokens, like the algorithm (alg), audience (aud), issuer (iss), and 
 * scope. Optionally, children can override <code>resolveSub</code>, <code>resolveNbf</code>, and
 * <code>resolveJti</code> to include optional claims in the constructed <code>JWTToken</code>.
 */
public abstract class JWTTokenTemplate implements JWTToken {
	private static final Logger log = Logger.getLogger(JWTTokenTemplate.class.getPackageName());
	
	private JSONObject header;
	private JSONObject claims;
	private String signature;
	private Instant issued;
	private Instant expires;
	
	private SigningStrategy signingStrategy;
	
	private static final Encoder base64UrlEncoder;
	
	static {
		base64UrlEncoder = Base64.getUrlEncoder();
	}
	
	protected JWTTokenTemplate(SigningStrategy signingStrategy) {
		this.signingStrategy = signingStrategy;
		log.info(String.format("Initializing JWTToken with a signing strategy of %s", signingStrategy.getStrategyName()));
		buildToken();
	}
	
	protected void buildToken() {
		issued = Instant.now();
		expires = getExpiry(issued);
		header = constructHeaders();
		claims = constructClaims(issued.getEpochSecond(), expires.getEpochSecond());
	}
	
	// must be provided by implementing child
	protected abstract String resolveAlg(); // algorithm
	protected abstract String resolveAud(); // audience
	protected abstract String resolveIss(); // issuer
	protected abstract String resolveScope(); // scope
	
	// can optionally be provided by child by overriding
	protected String resolveSub() {
		return "";
	}
	
	protected String resolveNbf() {
		return "";
	}
	
	protected String resolveJti() {
		return "";
	}
	
	private JSONObject constructHeaders() {
		JSONObject headers = new JSONObject();
		headers.put("alg", resolveAlg());
		headers.put("typ", "JWT");
		return headers;
	}
	
	private JSONObject constructClaims(long issued, long expires) {
		JSONObject claims = new JSONObject();
		claims.put("iss", resolveIss());
		claims.put("scope", resolveScope());
		claims.put("aud", resolveAud());
		claims.put("iat", issued);
		claims.put("exp", expires);
		
		String sub = resolveSub();
		if (sub != null && !sub.isEmpty()) {
			claims.put("sub", sub);
		}
		
		String nbf = resolveNbf();
		if (nbf != null && !nbf.isEmpty()) {
			claims.put("nbf", nbf);
		}
		
		String jti = resolveJti();
		if (jti != null && !jti.isEmpty()) {
			claims.put("jti", jti);
		}
		return claims;
	}
	
	private Instant getExpiry(Instant issuedAt) {
		return issuedAt.plus(1, ChronoUnit.HOURS);
	}
	
	private String toUrlEncodedUTF8String(JSONObject toEncode) {
		byte[] utf8Encoded = toEncode.toString().getBytes(UTF_8);
		return new String(base64UrlEncoder.encode(utf8Encoded), UTF_8);
	}
	
	@Override
	public boolean hasExpired() {
		return Instant.now().isAfter(expires);
	}
	
	@Override
	public String getUrlEncodedHeader() {
		return toUrlEncodedUTF8String(header);
	}
	
	@Override
	public String getUrlEncodedClaims() {
		return toUrlEncodedUTF8String(claims);
	}
	
	@Override
	public String toTransmissionReadyToken() {
		String toSign = getUrlEncodedHeader().concat(".").concat(getUrlEncodedClaims());
		byte[] signedBytes = signingStrategy.sign(toSign.getBytes(UTF_8));
		String signed = new String(base64UrlEncoder.encode(signedBytes), UTF_8);
		return toSign.concat(".").concat(signed);
	}
	
	@Override
	public String getScope() {
		return claims.getString("scope");
	}
	
	@Override
	public String getAudience() {
		return claims.getString("aud");
	}
	
	@Override
	public Instant getIssuedAt() {
		return issued;
	}
	
	@Override
	public Instant getExpiresAt() {
		return expires;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof JWTToken)) {
			return false;
		}
		JWTToken otherToken = (JWTToken) other;
		return this == other || (this.getUrlEncodedHeader().equals(otherToken.getUrlEncodedHeader())
				&& this.getUrlEncodedClaims().equals(otherToken.getUrlEncodedClaims())
				&& this.issued.equals(otherToken.getIssuedAt()) && this.expires.equals(otherToken.getExpiresAt()));
	}
	
	@Override
	public String toString() {
		return String.format("Header: %s %nClaims: %s %nSig: %s %n", header.toString(2), claims.toString(2), signature);
	}
}
