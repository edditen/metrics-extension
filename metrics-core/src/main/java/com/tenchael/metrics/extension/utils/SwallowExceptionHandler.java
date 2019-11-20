package com.tenchael.metrics.extension.utils;

public class SwallowExceptionHandler {

    public static void swallow(Exception e) {
        swallow(UniformSwallowHolder.getListener(), null, e);
    }

    public static void swallow(String message, Exception e) {
        swallow(UniformSwallowHolder.getListener(), message, e);
    }

    public static void swallow(SwallowExceptionListener listener, String message, Exception e) {
        if (listener == null) {
            return;
        }

        try {
            listener.onException(message, e);
        } catch (OutOfMemoryError oome) {
            throw oome;
        } catch (VirtualMachineError vme) {
            throw vme;
        } catch (Throwable t) {
            // Ignore. Enjoy the irony.
        }
    }

    public static void swallow(SwallowExceptionListener listener, Exception e) {
        swallow(listener, null, e);
    }


}
