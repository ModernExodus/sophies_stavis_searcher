package com.john.utils;

import static com.john.utils.Utils.getSaltingStrategy;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import com.john.utils.providers.secrets.MissingSecretException;
import com.john.utils.providers.secrets.SecretProvider;
import com.john.utils.providers.secrets.SecretProvider.Secret;
import com.saltweaver.salting.api.InvalidSaltingStrategyException;
import com.saltweaver.salting.api.SaltingStrategy;

public interface FileReader {
	byte[] readFile() throws IOException;
	
	static String toUTF8String(byte[] content) {
		return new String(content, UTF_8);
	}
	
	/** 
	 * Helper method to construct a composed <code>FileReader</code> capable of decrypting 
	 * data that was encrypted using the application standards.
	 */
	static FileReader standardDecryptionReader(String path, int bufferSize)
			throws MissingSecretException, InvalidSaltingStrategyException {
		SaltingStrategy strategy = getSaltingStrategy();
		return new SaltedFileReader(
				new EncryptedFileReader(new Base64DecodingFileReader(new SimpleFileReader(path, bufferSize)),
						SecretProvider.getSecret(Secret.AES_PRIVATE_KEY).getBytes(UTF_8)),
				strategy);
	}
}
