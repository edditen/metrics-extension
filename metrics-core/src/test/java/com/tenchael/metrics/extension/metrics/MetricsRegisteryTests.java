package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import com.tenchael.metrics.extension.utils.ExceptionListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MetricsRegisteryTests extends Assert {

    private MetricsRegistery registery = MetricsRegistery.getInstance();
    private JmxReporter.JmxListener listener = new JmxReporter.JmxListener();

    @Before
    public void setUp() {
        listener.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(String message, Exception e) {
                e.printStackTrace();
            }
        });
        registery.addListener(listener);
    }

    @Test
    public void testRegister() throws IOException {
        Counter counter = registery.counter("total.count", "Counter", "echo");
        counter.incr();
        assertTrue(true);
    }

}
