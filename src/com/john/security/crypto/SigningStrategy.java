package com.john.security.crypto;

/**
 * Defines the API that signing strategies must follow. Namely, they must be able to cryptographically
 * sign a byte array, returning the signed byte array.
 */
public interface SigningStrategy {
	public byte[] sign(byte[] toSign);
	public String getStrategyName();
}
