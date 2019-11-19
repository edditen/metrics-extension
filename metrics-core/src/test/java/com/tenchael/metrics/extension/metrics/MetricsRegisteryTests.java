package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import com.tenchael.metrics.extension.utils.ExceptionListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    public void testRegister() throws Exception {
        Counter counter = registery.counter("total.count", "Counter", "echo");
        counter.incr();
        assertTrue(true);
    }

    @Test
    public void testRegister_histogram() throws Exception {
        Histogram histogram = registery.histogram("histogram", "Histogram", "someMethod");
        assertNotNull(histogram);
        Histogram.Context ctx = histogram.time();
        TimeUnit.MILLISECONDS.sleep(1);
        ctx.stop();
        assertTrue(true);
    }

    private int genRandom(int bound) {
        return new Random().nextInt(bound);
    }

}
