package com.tenchael.jmx.ext;

/**
 * Created by Tenchael on 2020/2/22.
 */
public interface JmxConfigListener {
	default void onUpdate(boolean newValue) {
	}

	default void onInit(boolean value) {
	}
}
