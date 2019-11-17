package com.tenchael.metrics.extension.jmx;

import com.tenchael.metrics.extension.utils.ExceptionListener;
import com.tenchael.metrics.extension.utils.NameUtils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import static com.tenchael.metrics.extension.utils.ExceptionHandler.handleException;

public class MBeanRegistry {

    private static final String DEFAULT_JMX_NAME_PREFIX = "metrics";

    private static final MBeanRegistry INSTANCE = new MBeanRegistry();
    private final Map<Object, ObjectName> registeredMBeans = new HashMap<>();
    private MBeanServer mBeanServer;
    private ExceptionListener exceptionListener;

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

    public MBeanServer getPlatformMBeanServer() {
        return mBeanServer;
    }

    public ObjectName register(String namePrefix, Object mBean) {
        if (registeredMBeans.containsKey(mBean)) {
            //already registered
            return registeredMBeans.get(mBean);
        }
        ObjectName oName = null;
        MBeanServer mbs = getPlatformMBeanServer();
        int i = 1;
        boolean registered = false;
        String base = NameUtils.baseOName(mBean);
        while (!registered) {
            try {
                ObjectName objName = new ObjectName(base + namePrefix + "_" + i);
                mbs.registerMBean(mBean, objName);
                oName = objName;
                registered = true;
                registeredMBeans.putIfAbsent(mBean, oName);
            } catch (MalformedObjectNameException e) {
                // Must be an invalid name. Use the defaults instead.
                handleException(exceptionListener, "invalid name", e);
                namePrefix = DEFAULT_JMX_NAME_PREFIX;
            } catch (InstanceAlreadyExistsException e) {
                // Increment the index and try again
                handleException(exceptionListener, e);
                i++;
            } catch (MBeanRegistrationException e) {
                // Shouldn't happen. Skip registration if it does.
                handleException(exceptionListener, e);
                registered = true;
            } catch (NotCompliantMBeanException e) {
                // Shouldn't happen. Skip registration if it does.
                handleException(exceptionListener, e);
                registered = true;
            }
        }
        return oName;
    }

    public void unregister(ObjectName oName) {
        if (oName != null) {
            try {
                getPlatformMBeanServer().unregisterMBean(oName);
            } catch (MBeanRegistrationException e) {
                handleException(exceptionListener, e);
            } catch (InstanceNotFoundException e) {
                handleException(exceptionListener, e);
            }
        }
    }


    public final void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }


}
