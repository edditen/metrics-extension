package com.tenchael.metrics.extension.utils;

import javax.management.ObjectName;

public class NameUtils {

    public static String oname(String domain, String type, String name) {
        String quoteName = ObjectName.quote(name);
        return String.format("%s:type=%s,name=%s", domain, type, quoteName);
    }


}
