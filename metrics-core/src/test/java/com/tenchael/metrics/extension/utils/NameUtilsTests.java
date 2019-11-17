package com.tenchael.metrics.extension.utils;

import com.tenchael.metrics.extension.jmx.MBean;
import org.junit.Assert;
import org.junit.Test;

public class NameUtilsTests extends Assert {

    @Test
    public void testBaseOName() {
        MBean.BaseMBean mBean = new MyMBean("myCat", "myName");
        String oName = NameUtils.baseOName(mBean);
        System.out.println(oName);
        String expect = "com.tenchael.metrics.extension.utils:type=MyMBean,name=";
        assertEquals(expect, oName);
    }

    static class MyMBean extends MBean.BaseMBean {

        public MyMBean(String category, String name) {
            super(category, name);
        }
    }
}
