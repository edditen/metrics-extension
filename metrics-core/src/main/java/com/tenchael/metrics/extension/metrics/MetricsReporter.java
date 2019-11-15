package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.jmx.MBean;
import com.tenchael.metrics.extension.jmx.MBeanRegistry;
import com.tenchael.metrics.extension.utils.NameUtils;
import com.tenchael.metrics.extension.utils.ExceptionListener;

import javax.management.ObjectName;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static com.tenchael.metrics.extension.utils.ExceptionHandler.handleException;

public class MetricsReporter {
    private final Map<String, MBean> metricBeans;
    private final Set<ObjectName> objectNames;
    private final ReentrantLock lock = new ReentrantLock();
    private final Executor executor = Executors.newFixedThreadPool(20);


    private final MBeanRegistry mBeanRegistry;

    private ExceptionListener exceptionListener;


    public MetricsReporter() {
        this.metricBeans = new ConcurrentHashMap<>();
        this.objectNames = new HashSet<>();
        this.mBeanRegistry = MBeanRegistry.getInstance();
        this.mBeanRegistry.setExceptionListener(exceptionListener);
    }

    public void incr(String category, String name) {
        Counter counter = getMBean(category, name, Counter.class);
        if (counter != null) {
            counter.incr();
        }
    }

    private <T extends MBean> T getMBean(String category, String name, Class<T> type) {
        String key = metricsKey(category, name);
        MBean mBean = metricBeans.get(key);
        if (mBean == null) {
            lock.lock();
            try {
                if (mBean == null) {
                    mBean = newMBean(category, name, type);
                    metricBeans.putIfAbsent(key, mBean);
                    asyncRegister(mBean);
                }
            } catch (Exception e) {
                handleException(exceptionListener, e);
                return null;
            } finally {
                lock.unlock();
            }
        }
        return (T) mBean;
    }

    private <T extends MBean> MBean newMBean(String category, String name, Class<T> type) {
        if (type == Counter.class) {
            return new Counter(category, name);
        } else if (type == Histogram.class) {
            return new Histogram(category, name);
        } else {
            throw new IllegalArgumentException("not support now");
        }
    }

    public void update(String category, String name, long value) {
        Histogram histogram = getMBean(category, name, Histogram.class);
        if (histogram != null) {
            histogram.update(value);
        }
    }

    public void updateAtEnd(String category, String name, long begin) {
        update(category, name, System.nanoTime() - begin);
    }

    private String metricsKey(String category, String name) {
        return new StringBuilder(name)
                .append("@")
                .append(category)
                .toString();
    }

    private void register(MBean mBean) {
        try {
            ObjectName oname = mBeanRegistry.register(NameUtils.baseOName(mBean),
                    mBean.getCategory(), mBean);
            this.objectNames.add(oname);
        } catch (Exception e) {
            //handle the exception, can not interrupt thread because exception
            handleException(exceptionListener, e);
        }
    }

    private void asyncRegister(final MBean mBean) {
        executor.execute(() -> {
            register(mBean);
        });
    }

    private void unregister(ObjectName oname) {
        mBeanRegistry.unregister(oname);
    }

    public void close() {
        for (ObjectName oname : objectNames) {
            unregister(oname);
        }
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }
}
