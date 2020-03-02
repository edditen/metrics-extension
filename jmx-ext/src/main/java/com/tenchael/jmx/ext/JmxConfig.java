package com.tenchael.jmx.ext;

import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tenchael.jmx.ext.Commons.DEFAULT_DOMAIN;
import static com.tenchael.jmx.ext.Commons.DEFAULT_TYPE;

/**
 * Created by Tenchael on 2020/2/22.
 */
public class JmxConfig extends MBean.BaseMBean implements JmxConfigMXBean {

	public static final String CONFIG_NAME = "enabled";
	private static final Logger LOGGER = LoggerFactory.getLogger(JmxConfig.class);
	private static final JmxConfig INSTANCE = newInstance(DEFAULT_DOMAIN, DEFAULT_TYPE, CONFIG_NAME);
	private final AtomicBoolean enabled = new AtomicBoolean(true);
	private final List<JmxConfigListener> listeners = new ArrayList<>();

	private final Executor executor = Executors.newFixedThreadPool(4);

	public JmxConfig(ObjectName oname, boolean enabled) {
		super(oname);
		this.enabled.set(enabled);
		Executors.newSingleThreadExecutor()
				.execute(() -> registerToJmx());
	}

	public static JmxConfig newInstance(String domain, String type, String name) {
		try {
			return new JmxConfig(Commons.objectName(domain, type, name), true);
		} catch (MalformedObjectNameException | NullPointerException e) {
			throw new IllegalStateException(e);
		}
	}

	public static JmxConfig instance() {
		return JmxConfig.INSTANCE;
	}

	private void registerToJmx() {
		MBeanRegistry.getInstance().register(getOname(), this);
	}

	@Override
	public boolean isEnabled() {
		return this.enabled.get();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (this.enabled.compareAndSet(!enabled, enabled)) {
			updateNotify();
		}
	}

	private void updateNotify() {
		for (JmxConfigListener listener : this.listeners) {
			executor.execute(() -> {
						try {
							listener.onUpdate(this.isEnabled());
						} catch (Exception e) {
							LOGGER.error(e);
						}
					}
			);
		}
	}

	public void addListener(JmxConfigListener listener) {
		if (this.listeners.contains(listener)) {
			return;
		}

		listeners.add(listener);
		executor.execute(() -> listener.onInit(this.isEnabled()));
	}
}


