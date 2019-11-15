package com.tenchael.metrics.extension.jmx;

import com.tenchael.metrics.extension.utils.ExceptionListener;

import javax.management.*;
import java.lang.management.ManagementFactory;

import static com.tenchael.metrics.extension.utils.ExceptionHandler.handleException;

public class MBeanRegistry {

    private static final String DEFAULT_JMX_NAME_PREFIX = "metrics";

    private static volatile MBeanRegistry instance = new MBeanRegistry();

    private MBeanServer mBeanServer;

    private ExceptionListener exceptionListener;


    public MBeanRegistry() {
        try {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        } catch (Error e) {
            mBeanServer = MBeanServerFactory.createMBeanServer();
        }
    }

    public static MBeanRegistry getInstance() {
        return instance;
    }

    public MBeanServer getPlatformMBeanServer() {
        return mBeanServer;
    }

    public ObjectName register(String nameBase, String namePrefix, Object mBean) {
        ObjectName objectName = null;
        MBeanServer mbs = getPlatformMBeanServer();
        int i = 1;
        boolean registered = false;
        String base = nameBase;
        while (!registered) {
            try {
                ObjectName objName = new ObjectName(base + namePrefix + "_" + i);
                mbs.registerMBean(mBean, objName);
                objectName = objName;
                registered = true;
            } catch (MalformedObjectNameException e) {
                // Must be an invalid name. Use the defaults instead.
                handleException(exceptionListener, "invalid name", e);
                namePrefix = DEFAULT_JMX_NAME_PREFIX;
                base = nameBase;
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
        return objectName;
    }

    public void unregister(ObjectName oname) {
        if (oname != null) {
            try {
                getPlatformMBeanServer().unregisterMBean(oname);
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
