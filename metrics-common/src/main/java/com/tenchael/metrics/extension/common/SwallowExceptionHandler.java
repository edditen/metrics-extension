package com.tenchael.metrics.extension.common;

/**
 * Swallow exception
 * Created by Tenchael on 2019/11/26.
 */
public class SwallowExceptionHandler {

	private SwallowExceptionHandler() throws IllegalAccessException {
		throw new IllegalAccessException("Illegal access!");
	}

	public static void swallow(Throwable e) {
		swallow(UniformSwallowHolder.getListener(), null, e);
	}

	public static void swallow(String message, Throwable e) {
		swallow(UniformSwallowHolder.getListener(), message, e);
	}

	/**
	 * Swallow exception for listener call back, and throws OOM and VirtualMachineError
	 *
	 * @param listener
	 * @param message
	 * @param e
	 */
	public static void swallow(SwallowExceptionListener listener, String message, Throwable e) {
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

	public static void swallow(SwallowExceptionListener listener, Throwable e) {
		swallow(listener, null, e);
	}


}
