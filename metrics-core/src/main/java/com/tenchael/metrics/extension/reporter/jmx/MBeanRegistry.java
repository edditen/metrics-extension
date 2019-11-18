package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.utils.ExceptionListener;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import static com.tenchael.metrics.extension.utils.ExceptionHandler.handleException;

public class MBeanRegistry {

    private static final MBeanRegistry INSTANCE = new MBeanRegistry();
    private final Set<Object> registeredMBeans = new HashSet<>();
    private final Set<Object> registeredOnames = new HashSet<>();
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

    public void register(ObjectName oname, Object mBean) {
        if (registeredMBeans.contains(mBean) || registeredOnames.contains(oname)) {
            //already registered
            return;
        }
        MBeanServer mbs = getPlatformMBeanServer();
        try {
            mbs.registerMBean(mBean, oname);
            registeredOnames.add(oname);
            registeredMBeans.add(mBean);
        } catch (InstanceAlreadyExistsException e) {
            // Increment the index and try again
            handleException(exceptionListener, e);
        } catch (MBeanRegistrationException e) {
            // Shouldn't happen. Skip registration if it does.
            handleException(exceptionListener, e);
        } catch (NotCompliantMBeanException e) {
            // Shouldn't happen. Skip registration if it does.
            handleException(exceptionListener, e);
        }
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
