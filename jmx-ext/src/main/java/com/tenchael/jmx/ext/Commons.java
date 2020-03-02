package com.tenchael.jmx.ext;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by Tenchael on 2020/2/21.
 */
public class Commons {

	public static final String DEFAULT_DOMAIN = "extension.jmx.config";
	public static final String DEFAULT_TYPE = "Config";

	private Commons() throws IllegalAccessException {
		throw new IllegalAccessException("Illegal access!");
	}

	public static String oname(String domain, String type, String name) {
		String quoteName = ObjectName.quote(name);
		return String.format("%s:type=%s,name=%s", domain, type, quoteName);
	}

	public static ObjectName objectName(String onameString) throws MalformedObjectNameException {
		return new ObjectName(onameString);
	}

	public static ObjectName objectName(String domain, String type, String name)
			throws MalformedObjectNameException {
		return objectName(oname(domain, type, name));
	}
}
