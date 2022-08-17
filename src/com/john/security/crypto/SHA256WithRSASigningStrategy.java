package com.john.security.crypto;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * A <code>SigningStrategy</code> implementation capable of signing data using the SHA256WithRSA signing
 * algorithm. This algorithm is required to sign jwt tokens sent to Google's authentication servers. It 
 * must be instantiated with a valid base64-encoded private key string, which it will use to sign data.
 */
public class SHA256WithRSASigningStrategy implements SigningStrategy {
	private static final Logger log = Logger.getLogger(SHA256WithRSASigningStrategy.class.getCanonicalName());
	private static final String NAME = "SHA256WithRSA";
	private static final String SIGNING_ALGORITHM = "SHA256WithRSA";
	private final PrivateKey pk;

	public SHA256WithRSASigningStrategy(String pk) throws InvalidKeySpecException {
		log.info("Initializing SHA256WithRSASigningStrategy and generating private key");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pk.getBytes(UTF_8)));
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			log.severe("No such algorithm exists for Key Factory");
		}
		this.pk = keyFactory.generatePrivate(keySpec);
		log.info("Successfully generated private key using the provided string");
	}
	
	/** 
	 * Signs the provided byte array using the instance's private key. It returns a byte array
	 * representing the signed data. If signing fails for any reason, an empty byte array will
	 * be returned.
	 */
	@Override
	public byte[] sign(byte[] toSign) {
		log.info(String.format("Signing data with %s signing algorithm", SIGNING_ALGORITHM));
		try {
			Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
			signature.initSign(pk);
			signature.update(toSign);
			return signature.sign();
		} catch (Exception e) {
			log.severe(String.format("Failed to sign data: ", e.getMessage()));
			return new byte[0];
		}
	}
	
	@Override
	public String getStrategyName() {
		return NAME;
	}

}
