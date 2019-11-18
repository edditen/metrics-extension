package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.utils.NameUtils;

public interface NameFactory {

    String createName(String domain, String type, String name);

    class DefaultNameFactory implements NameFactory {


        @Override
        public String createName(String domain, String type, String name) {
            return NameUtils.oname(domain, type, name);
        }
    }
}
