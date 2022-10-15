package com.john.utils.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 
 * Responsible for managing key-value pair arguments passed to the application at runtime, generally when starting via the command line. 
 * Currently, this only supports key-value pair arguments of the format <code>"--argument=value"</code> or <code>"argument=value"</code>.
 */
public final class RuntimeArgumentProvider {
	private static final Logger log = Logger.getLogger(RuntimeArgumentProvider.class.getCanonicalName());
	private static final Set<String> SUPPORTED_ARGS;
	
	private static Map<String, String> arguments;
	
	static {
		SUPPORTED_ARGS = Stream.of(RuntimeArgument.values()).map(arg -> arg.getValue()).collect(Collectors.toSet());
	}
	
	/** Initialize the RuntimeArgumentProvider with the given arguments. This can only be invoked once! */
	public static void init(String[] args) {
		if (arguments != null) {
			throw new IllegalStateException("RuntimeArgumentProvider cannot be initialized more than once!");
		}
		log.info("Initializing RuntimeArgumentProvider");
		if (args.length == 0) {
			arguments = Map.of();
			log.info("No arguments found for RuntimeArgumentProvider");
		} else {
			arguments = parseArguments(args);
			log.info(String.format("Successfully initialized RuntimeArgumentProvider with %d argument(s)", arguments.size()));
		}
	}
	
	/** Returns the value associated with the given <code>RuntimeArgument</code> as a String */
	public static String getArgumentValue(RuntimeArgument argument) {
		assertInitialized();
		return arguments.get(argument.getValue());
	}
	
	/** Returns the value associated with the given <code>RuntimeArgument</code> as a boolean */
	public static boolean getBooleanArgumentValue(RuntimeArgument argument) {
		assertInitialized();
		return Boolean.parseBoolean(arguments.get(argument.getValue()));
	}
	
	/** Returns <code>true</code> if the argument is available, <code>false</code> otherwise*/
	public static boolean hasArgumentValue(RuntimeArgument argument) {
		assertInitialized();
		return arguments.containsKey(argument.getValue());
	}
	
	private static void assertInitialized() {
		if (arguments == null) {
			throw new IllegalStateException("RuntimeArgumentProvider not yet initialized!");
		}
	}
	
	private static Map<String, String> parseArguments(String[] args) {
		Map<String, String> parsed = new HashMap<>();
		for (String arg : args) {
			String[] keyValue = arg.replace("--", "").split("=");
			if (SUPPORTED_ARGS.contains(keyValue[0].toLowerCase())) {
				log.fine(String.format("Found argument %s", arg));
				parsed.put(keyValue[0], keyValue[1]);
			} else {
				log.warning(String.format("Unknown argument %s provided", arg));
			}
		}
		return parsed;
	}
	
	public static enum RuntimeArgument {
		SECRETS_LOCATION("secrets-location"),
		CACHE_SECRETS("cache-secrets");
		
		private final String value;
		private RuntimeArgument(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
}
