package com.john.utils.providers.secrets;

public class MissingSecretException extends Exception {
	private static final long serialVersionUID = -1647042355317302866L;

	MissingSecretException(String message) {
		super(message);
	}
	
	MissingSecretException(Throwable t) {
		super(t);
	}
}
