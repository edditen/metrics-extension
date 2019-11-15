package com.tenchael.metrics.extension.utils;

import com.tenchael.metrics.extension.jmx.MBean;

public class NameUtils {

    public static String baseOName(MBean mBean) {
        Class<? extends MBean> clazz = mBean.getClass();
        String packageName = clazz.getPackage().getName();
        String clazzName = clazz.getSimpleName();
        return String.format("%s:type=%s,name=", packageName, clazzName);
    }

}
