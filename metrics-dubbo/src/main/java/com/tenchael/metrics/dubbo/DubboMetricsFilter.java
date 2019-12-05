package com.tenchael.metrics.dubbo;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;
import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import com.tenchael.metrics.extension.utils.NameUtils;
import com.tenchael.metrics.extension.utils.UniformSwallowHolder;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

@Activate(group = {CONSUMER, PROVIDER}, value = Constants.METRICS_KEY)
public class DubboMetricsFilter implements Filter {
    public static final String REQUEST_COUNT = "Request";
    public static final String COMPLETE_COUNT = "Complete";
    public static final String SYS_FAILED = "SysFailed";
    public static final String HISTOGRAM = "Histogram";
    public static final String METRICS_EXTENSION_ENABLE = "metrics.extension.dubbo.enable";
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboMetricsFilter.class);
    private final MetricsRegistry registery;
    private final boolean metricsEnable;


    public DubboMetricsFilter() {
        String metricsEnableProps = System.getProperty(METRICS_EXTENSION_ENABLE, "true");
        switch (metricsEnableProps) {
            case "true":
                metricsEnable = true;
                break;
            case "false":
                metricsEnable = false;
                break;
            default:
                metricsEnable = true;
        }

        UniformSwallowHolder.setListener((message, e) ->
                LOGGER.error(message, e)
        );
        this.registery = MetricsRegistry.getInstance();
        this.registery.addListener(JmxReporter.JmxListener.getInstance());

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!metricsEnable) {
            return invoker.invoke(invocation);
        }

        String methodName = fullMethodName(invoker, invocation);
        registery.counter(Constants.METRICS_COUNT, REQUEST_COUNT, methodName).incr();
        Histogram.Context ctx = registery
                .histogram(Constants.METRICS_HISTOGRAM, HISTOGRAM, methodName).time();
        try {
            Result result = invoker.invoke(invocation);
            metricsError(result, methodName);
            return result;
        } catch (Exception e) {
            registery.counter(Constants.METRICS_COUNT, SYS_FAILED, methodName).incr();
            throw e;
        } finally {
            registery.counter(Constants.METRICS_COUNT, COMPLETE_COUNT, methodName).incr();
            ctx.stop();
        }
    }

    private void metricsError(Result result, String methodName) {
        String errType = errorType(result);
        if (errType != null) {
            registery.counter(Constants.METRICS_COUNT, errType, methodName).incr();
        }
    }

    /**
     * the type of error
     *
     * @param result
     * @return return the name of error if error exists, then return null
     */
    protected String errorType(Result result) {
        if (result == null || result.hasException()) {
            return SYS_FAILED;
        }
        return null;
    }

    public String fullMethodName(Invoker<?> invoker, Invocation invocation) {
        String typeName = invoker.getInterface().getName();
        String methodName = invocation.getMethodName();
        return NameUtils.name(typeName, '#', methodName);
    }
}