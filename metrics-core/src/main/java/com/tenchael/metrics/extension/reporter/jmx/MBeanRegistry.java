package com.tenchael.metrics.extension.reporter.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import static com.tenchael.metrics.extension.utils.SwallowExceptionHandler.swallow;

/**
 * JMX mbean registry
 * Created by Tenchael on 2019/11/26.
 */
public class MBeanRegistry {

	private static final MBeanRegistry INSTANCE = new MBeanRegistry();
	private final Set<Object> registeredMBeans = new HashSet<>();
	private final Set<Object> registeredOnames = new HashSet<>();
	private MBeanServer mBeanServer;

	private MBeanRegistry() {
		try {
			mBeanServer = ManagementFactory.getPlatformMBeanServer();
		} catch (Error e) {
			mBeanServer = MBeanServerFactory.createMBeanServer();
		}
	}

	public static MBeanRegistry getInstance() {
		return INSTANCE;
	}

	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	public void register(ObjectName oname, Object mBean) {
		if (registeredMBeans.contains(mBean) || registeredOnames.contains(oname)) {
			//already registered
			return;
		}
		MBeanServer mbs = getMBeanServer();
		try {
			mbs.registerMBean(mBean, oname);
			registeredOnames.add(oname);
			registeredMBeans.add(mBean);
		} catch (InstanceAlreadyExistsException e) {
			// Increment the index and try again
			swallow(e);
		} catch (MBeanRegistrationException e) {
			// Shouldn't happen. Skip registration if it does.
			swallow(e);
		} catch (NotCompliantMBeanException e) {
			// Shouldn't happen. Skip registration if it does.
			swallow(e);
		}
	}

	public void unregister(ObjectName oName) {
		if (oName != null) {
			try {
				getMBeanServer().unregisterMBean(oName);
			} catch (MBeanRegistrationException e) {
				swallow(e);
			} catch (InstanceNotFoundException e) {
				swallow(e);
			}
		}
	}

}
