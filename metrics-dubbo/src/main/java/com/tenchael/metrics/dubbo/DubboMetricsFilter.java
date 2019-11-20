package com.tenchael.metrics.dubbo;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricsRegistery;
import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import com.tenchael.metrics.extension.reporter.jmx.MBeanRegistry;
import com.tenchael.metrics.extension.utils.ExceptionListener;
import com.tenchael.metrics.extension.utils.NameUtils;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

@Activate(group = {CONSUMER, PROVIDER}, value = Constants.METRICS_KEY)
public class DubboMetricsFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboMetricsFilter.class);
    private final MetricsRegistery registery;
    private final ExceptionListener exceptionListener;

    private static final String REQUEST_COUNT = "Request";
    private static final String COMPLETE_COUNT = "Complete";
    private static final String FAILED_COUNT = "Failed";
    private static final String HISTOGRAM = "Histogram";

    public DubboMetricsFilter() {
        this.registery = MetricsRegistery.getInstance();
        this.exceptionListener = new ExceptionListener() {
            @Override
            public void onException(String message, Exception e) {
                LOGGER.error(message, e);
            }
        };
        JmxReporter.JmxListener jmxListener = new JmxReporter.JmxListener();
        jmxListener.setExceptionListener(exceptionListener);
        MBeanRegistry.getInstance().setExceptionListener(exceptionListener);
        registery.addListener(jmxListener);
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = fullMethodName(invoker, invocation);
        registery.counter(Constants.METRICS_COUNT, REQUEST_COUNT, methodName).incr();
        Histogram.Context ctx = registery
                .histogram(Constants.METRICS_HISTOGRAM, HISTOGRAM, methodName).time();
        try {
            Result result = invoker.invoke(invocation);
            if (result == null || result.hasException()) {
                registery.counter(Constants.METRICS_COUNT, FAILED_COUNT, methodName).incr();
            }
            return result;
        } catch (Exception e) {
            registery.counter(Constants.METRICS_COUNT, FAILED_COUNT, methodName).incr();
            throw e;
        } finally {
            registery.counter(Constants.METRICS_COUNT, COMPLETE_COUNT, methodName).incr();
            ctx.stop();
        }
    }


    public String fullMethodName(Invoker<?> invoker, Invocation invocation) {
        String typeName = invoker.getInterface().getName();
        String methodName = invocation.getMethodName();
        return NameUtils.name(typeName, '#', methodName);
    }
}