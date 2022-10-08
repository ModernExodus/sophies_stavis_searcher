package com.john.application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.john.application.configuration.CustomLogManagement;
import com.john.utils.providers.ApplicationPropertyProvider;
import com.john.utils.providers.RuntimeArgumentProvider;
import com.john.utils.providers.ApplicationPropertyProvider.Property;

public class StaviSearcherApplication {
	private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	static {
		CustomLogManagement.enableApplicationLogging();
	}

	public static void main(String[] args) {
		log.info("Sophie's Stavi's Searcher application is starting up!");
		
		// process any arguments provided
		RuntimeArgumentProvider.init(args);
		
		// create the ScheduledExecutorService
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		// schedule main query operation
		executor.scheduleAtFixedRate(new StavisQueryOperator(), 0,
				ApplicationPropertyProvider.getLongProperty(Property.QUERY_FREQUENCY_MINUTES), TimeUnit.MINUTES);
		log.info("Stavi's Query Operator has been scheduled with the executor");
		
		// kick off health checks
		HealthChecker.start(executor);
		log.info("The health checking system has been started");
		
		// initialization complete
		log.info("Sophie's Stavi's Searcher application has been initialized successfully!");
	}
}
