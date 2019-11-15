package com.tenchael.metrics.extension.utils;

public class ExceptionHandler {

    public static void handleException(ExceptionListener listener, String message, Exception e) {
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

    public static void handleException(ExceptionListener listener, Exception e) {
        handleException(listener, null, e);
    }


}
