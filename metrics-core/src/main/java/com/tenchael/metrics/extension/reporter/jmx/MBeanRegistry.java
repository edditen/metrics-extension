package com.tenchael.metrics.extension.reporter.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tenchael.metrics.extension.utils.SwallowExceptionHandler.swallow;

/**
 * JMX mbean registry
 * Created by Tenchael on 2019/11/26.
 */
public class MBeanRegistry {

    private static final MBeanRegistry INSTANCE = new MBeanRegistry();
    private final Map<ObjectName, Object> registeredMBeans = new ConcurrentHashMap<>();
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
        if (registeredMBeans.containsKey(oname) || registeredMBeans.containsValue(mBean)) {
            //already registered
            return;
        }
        MBeanServer mbs = getMBeanServer();
        try {
            mbs.registerMBean(mBean, oname);
            registeredMBeans.putIfAbsent(oname, mBean);
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

    public void unregister(ObjectName oname) {
        if (oname != null) {
            try {
                getMBeanServer().unregisterMBean(oname);
                registeredMBeans.remove(oname);
            } catch (MBeanRegistrationException e) {
                swallow(e);
            } catch (InstanceNotFoundException e) {
                swallow(e);
            }
        }
    }

    public void unregisterAll() {
        for (ObjectName oname : registeredMBeans.keySet()) {
            unregister(oname);
        }
    }

}
