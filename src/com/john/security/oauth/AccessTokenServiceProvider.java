package com.john.security.oauth;

import java.util.logging.Logger;

/** 
 * A provider responsible for keeping track of existing <code>AccessTokenService</code> instances,
 * and supplying existing instances when asked. Each <code>AccessTokenService</code> implementation should
 * be a singleton, and be registered in this class. The only way to get an instance is to ask this provider.
 */
public class AccessTokenServiceProvider {
	private static final Logger log = Logger.getLogger(AccessTokenServiceProvider.class.getCanonicalName());
	private static AccessTokenService service;
	
	private AccessTokenServiceProvider() {}
	
	/** 
	 * Provides a concrete implementation of the <code>AccessTokenService</code>, and is thread-safe
	 */
	public static synchronized AccessTokenService getAccessTokenService() {
		if (service == null) {
			log.info("Constructing new Access Token Service");
			service = new AccessTokenServiceImpl();
		}
		return service;
	}
}
