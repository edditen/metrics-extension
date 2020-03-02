package com.tenchael.metrics.extension.common;


import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;
import com.tenchael.metrics.extension.common.utils.Validator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展加载
 * Created by Tenchael on 2020/2/25.
 */
public class ExtensionLoader<T> {

	private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionLoader.class);
	private final List<T> cachedInstances;
	private final Class<T> type;

	private ExtensionLoader(Class<T> type) {
		this.type = type;
		this.cachedInstances = loadExtension();
	}


	public static <E> ExtensionLoader<E> getExtensionLoader(Class<E> type) {
		Validator.notNull(type, "type can not be null");
		Validator.isTrue(type.isInterface(), "type(%s) must be a interface", type);

		ExtensionLoader<E> loader = (ExtensionLoader<E>) EXTENSION_LOADERS.get(type);
		if (loader == null) {
			synchronized (ExtensionLoader.class) {
				if (loader == null) {
					loader = new ExtensionLoader<>(type);
				}
				EXTENSION_LOADERS.putIfAbsent(type, loader);
			}
		}
		return loader;
	}


	private List<T> loadExtension() {
		ServiceLoader<T> serviceLoader = null;
		try {
			serviceLoader = ServiceLoader.load(type);
		} catch (Throwable e) {
			LOGGER.error(String.format("Error when load SPI of %s", type), e);
		}
		List<T> serviceList = new ArrayList<>();
		if (serviceLoader == null) {
			return serviceList;
		}
		Iterator<T> iter = serviceLoader.iterator();
		while (iter.hasNext()) {
			try {
				T item = iter.next();
				if (serviceList.contains(item)) {
					continue;
				}
				serviceList.add(item);
			} catch (Throwable t) {
				LOGGER.error("Error when iterate serviceLoader", t);
			}
		}
		return serviceList;
	}

	public List<T> getServices() {
		return cachedInstances;
	}

}