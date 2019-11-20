package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.NameFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class MBeanRegistryTests extends Assert {

    private static MBeanRegistry mBeanRegistry;

    private static NameFactory nameFactory;

    @BeforeClass
    public static void setUp() {
        mBeanRegistry = MBeanRegistry.getInstance();
        nameFactory = new NameFactory.DefaultNameFactory();
    }


    @Test
    public void testGetInstance() {
        assertNotNull(mBeanRegistry);
    }

    @Test
    public void testRegister() throws MalformedObjectNameException {
        String oName = nameFactory.createName("total", "Counter", "sayHello");
        ObjectName objectName = new ObjectName(oName);
        System.out.println(objectName);
        Counter counter = new Counter();
        JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
        mBeanRegistry.register(objectName, jmxCounter);

        assertTrue(true);
    }

    @Test
    public void testRegister_duplicated() throws Exception {
        String oName = nameFactory.createName("total.count", "Counter", "come.on.sayHello2");
        ObjectName objectName = new ObjectName(oName);
        System.out.println(objectName);
        Counter counter = new Counter();
        JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
        mBeanRegistry.register(objectName, jmxCounter);
        mBeanRegistry.register(objectName, new JmxCounterMXBean.JmxCounter(objectName, counter));

        assertTrue(true);

    }

    @Test
    public void testRegister_duplicated_sameObject() throws MalformedObjectNameException {
        String oName = nameFactory.createName("failed.count", "Counter", "sayHello3");
        String oName2 = nameFactory.createName("complete.count", "Counter", "sayHello4");
        ObjectName objectName = new ObjectName(oName);
        ObjectName objectName2 = new ObjectName(oName2);
        System.out.println(objectName);
        Counter counter = new Counter();
        JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
        mBeanRegistry.register(objectName, jmxCounter);
        mBeanRegistry.register(objectName, new JmxCounterMXBean.JmxCounter(objectName2, counter));

        assertTrue(true);
    }

    @Test
    public void testUnregister() throws MalformedObjectNameException {
        String oName = nameFactory.createName("hello.world", "Counter", "sayHello");
        ObjectName objectName = new ObjectName(oName);
        System.out.println(objectName);
        Counter counter = new Counter();
        JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
        mBeanRegistry.register(objectName, jmxCounter);
        mBeanRegistry.unregister(objectName);

        assertTrue(true);
    }

}
