package com.tenchael.metrics.extension.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.utils.ExceptionListener;
import com.tenchael.metrics.extension.utils.NameUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.ObjectName;

public class MBeanRegistryTests extends Assert {

    private static MBeanRegistry mBeanRegistry;

    @BeforeClass
    public static void setUp() {
        mBeanRegistry = MBeanRegistry.getInstance();
        mBeanRegistry.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(String message, Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Test
    public void testGetInstance() {
        assertNotNull(mBeanRegistry);
    }

    @Test
    public void testRegister() {
        Counter counter = new Counter("counter", "myName");
        String baseOName = NameUtils.baseOName(counter);
        ObjectName oName = mBeanRegistry.register(baseOName, "hello", counter);
        System.out.println(oName);
        assertNotNull(oName);
    }

    @Test
    public void testRegister_duplicated() {
        Counter counter = new Counter("counter", "myName");
        String baseOName = NameUtils.baseOName(counter);
        ObjectName oName = mBeanRegistry.register(baseOName, "hello", counter);
        assertEquals("com.tenchael.metrics.extension.metrics:type=Counter,name=hello_1", oName.toString());

        Counter counter2 = new Counter("counter", "myName");
        String baseOName2 = NameUtils.baseOName(counter2);
        ObjectName oName2 = mBeanRegistry.register(baseOName2, "hello", counter2);
        assertEquals("com.tenchael.metrics.extension.metrics:type=Counter,name=hello_2", oName2.toString());

    }

    @Test
    public void testUnregister() {
        Counter counter = new Counter("counter", "myName");
        String baseOName = NameUtils.baseOName(counter);
        ObjectName oName = mBeanRegistry.register(baseOName, "hello", counter);
        System.out.println(oName);
        mBeanRegistry.unregister(oName);
        assertTrue(true);
    }

}
