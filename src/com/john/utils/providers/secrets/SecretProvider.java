package com.john.utils.providers.secrets;

import static com.john.utils.Utils.deleteFile;
import static com.john.utils.providers.RuntimeArgumentProvider.RuntimeArgument.SECRETS_LOCATION;
import static com.john.utils.providers.RuntimeArgumentProvider.RuntimeArgument.CACHE_SECRETS;
import static com.john.utils.providers.RuntimeArgumentProvider.RuntimeArgument.EAGER_LOAD_SECRETS;
import static com.john.utils.providers.RuntimeArgumentProvider.RuntimeArgument.DELETE_SECRETS_ON_LOAD;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.john.utils.providers.RuntimeArgumentProvider;

/**
 * Responsible for knowing how to find secrets that may be needed in other parts of the 
 * application. If the runtime argument "cache-secrets" is <code>false</code> or not provided, <code>SecretProvider</code> 
 * will attempt to retrieve the requested secret each time and will not cache secrets. This prevents secrets from
 * living indefinitely in the heap which can introduce vulnerabilities during heap dumps. If "cache-secrets" is
 * <code>true</code>, this provider will only retrieve secrets once, and all future attempts to retrieve secrets
 * will return the original secrets.
 */
public final class SecretProvider {
	private static final Logger log = Logger.getLogger(SecretProvider.class.getCanonicalName());
	private static final String SECRETS_FILE_LOCATION;
	
	private static Properties SECRETS;
	
	static {
		if (RuntimeArgumentProvider.hasArgumentValue(SECRETS_LOCATION)) {
			SECRETS_FILE_LOCATION = RuntimeArgumentProvider.getArgumentValue(SECRETS_LOCATION);
			if (RuntimeArgumentProvider.getBooleanArgumentValue(EAGER_LOAD_SECRETS)) {
				try {
					log.info("Eager loading of secrets enabled --> Loading secrets now");
					if (!RuntimeArgumentProvider.getBooleanArgumentValue(CACHE_SECRETS)) {
						log.warning("Secrets are being eagerly loaded, yet secrets caching is disabled. The eager loading has no effect.");
					}
					loadSecrets();
				} catch (MissingSecretException e) {
					log.severe("Failed to eagerly load secrets");
					throw new RuntimeException(e);
				}
			}
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
		if (SECRETS != null) {
			log.fine("Secrets are cached --> returning cached secrets");
			return SECRETS;
		}
		Properties secrets = new Properties();
		try (FileInputStream fis = new FileInputStream(SECRETS_FILE_LOCATION)) {
			secrets.load(fis);
		} catch (IOException e) {
			log.severe(e.getMessage());
			throw new MissingSecretException(e);
		} finally {
			if (RuntimeArgumentProvider.getBooleanArgumentValue(DELETE_SECRETS_ON_LOAD)) {
				log.info("Attempting to delete secrets file");
				if (deleteFile(SECRETS_FILE_LOCATION)) {
					log.info("Successfully deleted secrets file");
				} else {
					log.warning("Failed to delete secrets file");
				}
			}
		}
		if (RuntimeArgumentProvider.getBooleanArgumentValue(CACHE_SECRETS)) {
			SECRETS = secrets;
			log.fine("Secret caching is enabled --> secrets cached successfully");
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
