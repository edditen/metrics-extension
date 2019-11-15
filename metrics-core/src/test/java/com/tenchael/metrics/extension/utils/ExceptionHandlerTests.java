package com.tenchael.metrics.extension.utils;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionHandlerTests extends Assert {

    @Test
    public void testHandleException() {
        final Exception ex = new RuntimeException("something wrong");
        ExceptionHandler.handleException(new ExceptionListener() {
            @Override
            public void onException(String message, Exception e) {
                assertNull(message);
                assertEquals(ex, e);
            }
        }, ex);
    }

    @Test
    public void testHandleExceptionWithMessage() {
        final Exception ex = new RuntimeException("something wrong");
        ExceptionHandler.handleException(new ExceptionListener() {
            @Override
            public void onException(String message, Exception e) {
                assertEquals("oops", message);
                assertEquals(ex, e);
            }
        }, "oops", ex);
    }
}
