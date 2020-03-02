package com.tenchael.metrics.extension.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by Tenchael on 2020/3/1.
 */
public class PropertiesLoaderTests extends Assert {


	@Test
	public void testPropertiesLoader() {
		PropertiesManager propertiesLoader = PropertiesManager.getInstance();
		Properties props = propertiesLoader.getProperties();

		Object nation = props.getOrDefault("nation", "America");
		assertEquals("China", nation);

		Object shortName = props.getOrDefault("short", "us");
		assertEquals("us", shortName);
	}

	@Test
	public void testPropertiesLoader2() {
		PropertiesManager propertiesLoader = new PropertiesManager("META-INF/metrics-ext/hello.properties");
		Properties props = propertiesLoader.getProperties();

		Object val1 = props.get("abc");
		assertEquals("123", val1);

		Object val2 = props.get("hello");
		assertEquals("world", val2);
	}

	@Test
	public void testPropertiesLoader_notExist() {
		PropertiesManager propertiesLoader = new PropertiesManager("META-INF/notExist/hello.properties");
		Properties props = propertiesLoader.getProperties();

		Object val1 = props.get("abc");
		assertNull(val1);
	}


	@Test
	public void testPropertiesLoader_illegalFormat() {
		PropertiesManager propertiesLoader = new PropertiesManager("META-INF/metrics-ext/readme.txt");
		Properties props = propertiesLoader.getProperties();

		Object val1 = props.get("abc");
		assertNull(val1);
	}

}
