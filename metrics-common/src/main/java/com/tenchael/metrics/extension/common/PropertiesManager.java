package com.tenchael.metrics.extension.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.tenchael.metrics.extension.common.Constants.CONFIG_KEY;
import static com.tenchael.metrics.extension.common.Constants.DEFAULT_CONFIG_FILE;
import static com.tenchael.metrics.extension.common.SwallowExceptionHandler.swallow;

/**
 * Created by Tenchael on 2020/3/1.
 */
public class PropertiesManager {


	private static final PropertiesManager INSTANCE = new PropertiesManager();
	private final Properties properties;

	public PropertiesManager() {
		this(System.getProperty(CONFIG_KEY, DEFAULT_CONFIG_FILE));
	}

	public PropertiesManager(String config) {
		this.properties = new Properties();
		try (InputStream in =
				     Thread.currentThread().getContextClassLoader()
						     .getResourceAsStream(config)) {
			if (in == null) {
				swallow(String.format("No input stream load in path %s", config), null);
				return;
			}
			properties.load(in);
		} catch (IOException e) {
			swallow(String.format("Load properties failed in path %s", config), e);
		}
	}


	public static PropertiesManager getInstance() {
		return INSTANCE;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getString(String key) {
		Object obj = getProperties().get(key);
		if (obj == null) {
			return null;
		} else {
			return obj.toString();
		}
	}

	public String getStringOrDefault(String key, String defaultVal) {
		try {
			String val = getString(key);
			return (val != null) ? val : defaultVal;
		} catch (Exception e) {
			swallow(e);
			return defaultVal;
		}

	}

	public Integer getInt(String key) {
		String string = getString(key);
		if (string == null) {
			return null;
		}
		return Integer.valueOf(string);
	}

	public Integer getIntOrDefault(String key, Integer defaultVal) {
		try {
			Integer val = getInt(key);
			return (val != null) ? val : defaultVal;
		} catch (Exception e) {
			swallow(e);
			return defaultVal;
		}
	}

	public Boolean getBool(String key) {
		String string = getString(key);
		if (string == null) {
			return null;
		}
		return Boolean.valueOf(string);
	}

	public Boolean getBoolOrDefault(String key, Boolean defaultVal) {
		try {
			Boolean val = getBool(key);
			return (val != null) ? val : defaultVal;
		} catch (Exception e) {
			swallow(e);
			return defaultVal;
		}
	}
}
