package com.tenchael.metrics.extension.common;

public interface Constants {

	String MODULE_NAME = "metrics-ext";
	String CONFIG_KEY = "metrics.ext.config";
	String DEFAULT_CONFIG_FILE = "META-INF/metrics-ext/config.properties";


	class PropsKey {
		public static final String LOGGER_APP = "metrics.ext.app";
		public static final String METRICS_ENABLE = "metrics.ext.enable";
		public static final String REPORTER_LOGGER = "metrics.ext.reporter.logger";
	}


}
