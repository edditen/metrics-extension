package com.tenchael.metrics.dubbo;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricsRegistery;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboMetricsFilter.class);
    private final MetricsRegistery registery;

    private static final String REQUEST_COUNT = "Request";
    private static final String COMPLETE_COUNT = "Complete";
    private static final String SYS_FAILED_COUNT = "SysFailed";
    private static final String BUSS_FAILED_COUNT = "BussFailed";
    private static final String PARAM_FAILED_COUNT = "ParamFailed";
    private static final String HISTOGRAM = "Histogram";

    public DubboMetricsFilter() {
        UniformSwallowHolder.setListener((message, e) ->
                LOGGER.error(message, e)
        );
        this.registery = MetricsRegistery.getInstance();
        registery.addListener(new JmxReporter.JmxListener());

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = fullMethodName(invoker, invocation);
        registery.counter(Constants.METRICS_COUNT, REQUEST_COUNT, methodName).incr();
        Histogram.Context ctx = registery
                .histogram(Constants.METRICS_HISTOGRAM, HISTOGRAM, methodName).time();
        try {
            Result result = invoker.invoke(invocation);
            metricsError(result, methodName);
            return result;
        } catch (Exception e) {
            registery.counter(Constants.METRICS_COUNT, SYS_FAILED_COUNT, methodName).incr();
            throw e;
        } finally {
            registery.counter(Constants.METRICS_COUNT, COMPLETE_COUNT, methodName).incr();
            ctx.stop();
        }
    }

    private void metricsError(Result result, String methodName) {
        if (this.hasSystemError(result)) {
            registery.counter(Constants.METRICS_COUNT, SYS_FAILED_COUNT, methodName).incr();
        }
        if (this.hasBusinessError(result)) {
            registery.counter(Constants.METRICS_COUNT, BUSS_FAILED_COUNT, methodName).incr();
        }
        if (this.hasParamError(result)) {
            registery.counter(Constants.METRICS_COUNT, PARAM_FAILED_COUNT, methodName).incr();
        }
    }

    protected boolean hasSystemError(Result result) {
        return (result == null || result.hasException());
    }

    protected boolean hasBusinessError(Result result) {
        return false;
    }

    protected boolean hasParamError(Result result) {
        return false;
    }


    public String fullMethodName(Invoker<?> invoker, Invocation invocation) {
        String typeName = invoker.getInterface().getName();
        String methodName = invocation.getMethodName();
        return NameUtils.name(typeName, '#', methodName);
    }
}