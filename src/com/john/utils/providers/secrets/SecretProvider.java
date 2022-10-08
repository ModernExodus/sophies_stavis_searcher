package com.john.utils.providers.secrets;

import static com.john.utils.providers.RuntimeArgumentProvider.RuntimeArgument.SECRETS_LOCATION;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.john.utils.providers.RuntimeArgumentProvider;

/**
 * Responsible for knowing how to find secrets that may be needed in other parts of the 
 * application. Unlike other providers, <code>SecretProvider</code> will attempt to retrieve
 * the requested secret each time and will not cache secrets. This is to prevent secrets from
 * living indefinitely in the heap which can introduce vulnerabilities during heap dumps.
 */
public final class SecretProvider {
	private static final Logger log = Logger.getLogger(SecretProvider.class.getCanonicalName());
	private static final String SECRETS_FILE_LOCATION;
	
	static {
		if (RuntimeArgumentProvider.hasArgumentValue(SECRETS_LOCATION)) {
			SECRETS_FILE_LOCATION = RuntimeArgumentProvider.getArgumentValue(SECRETS_LOCATION);
		} else {
			throw new RuntimeException("No secret location was provided!");
		}
	}
	
	public static String getSecret(Secret secret) throws MissingSecretException {
		Properties secrets = loadSecrets();
		if (secrets.containsKey(secret.getPropertyName()) && !secrets.getProperty(secret.getPropertyName()).isBlank()) {
			return secrets.getProperty(secret.getPropertyName());
		}
		throw new MissingSecretException(secret.toString().concat(" was not found!"));
	}
	
	public static int getIntSecret(Secret secret) throws MissingSecretException {
		return Integer.parseInt(getSecret(secret));
	}
	
	private static Properties loadSecrets() throws MissingSecretException {
		Properties secrets = new Properties();
		try {
			secrets.load(new FileInputStream(SECRETS_FILE_LOCATION));
		} catch (IOException e) {
			log.severe(e.getMessage());
			throw new MissingSecretException(e);
		}
		return secrets;
	}

	public static enum Secret {
		AES_PRIVATE_KEY("aes.private_key"),
		SALTING_STRATEGY_COMPOSITION("salting_strategy.composition"),
		SALTING_STRATEGY_LENGTH("salting_strategy.length");
		
		private final String propertyName;
		private Secret(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	}
}
