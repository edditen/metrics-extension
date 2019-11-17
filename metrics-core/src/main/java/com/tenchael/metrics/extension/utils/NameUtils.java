package com.tenchael.metrics.extension.utils;

public class NameUtils {

    public static String baseOName(Object mBean) {
        Class clazz = mBean.getClass();
        String packageName = clazz.getPackage().getName();
        String clazzName = clazz.getSimpleName();
        return String.format("%s:type=%s,name=", packageName, clazzName);
    }

}
