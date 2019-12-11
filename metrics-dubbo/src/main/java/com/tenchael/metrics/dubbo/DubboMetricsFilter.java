package com.tenchael.metrics.dubbo;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;
import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import com.tenchael.metrics.extension.utils.UniformSwallowHolder;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import static com.tenchael.metrics.dubbo.Constants.*;
import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

@Activate(group = {CONSUMER, PROVIDER}, value = METRICS_KEY)
public class DubboMetricsFilter implements Filter {

	public static final String METRICS_EXTENSION_ENABLE = "metrics.extension.dubbo.enable";
	private static final Logger LOGGER = LoggerFactory.getLogger(DubboMetricsFilter.class);
	private final MetricsRegistry registry;
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
		this.registry = MetricsRegistry.getInstance();
		this.registry.addListener(JmxReporter.JmxListener.getInstance());

	}

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		if (!metricsEnable) {
			return invoker.invoke(invocation);
		}

		String methodName = fullMethodName(invoker, invocation);
		counterIncr(COUNT_REQUEST, methodName);
		Histogram.Context ctx = beginRecord(HISTOGRAM_ELAPSE, methodName);

		Result result = null;
		try {
			result = invoker.invoke(invocation);
		} catch (Exception e) {
			registry.counter(METRICS_COUNT, COUNT_FAILED, methodName).incr();
			throw e;
		} finally {
			metricsError(result, methodName);
			counterIncr(COUNT_COMPLETE, methodName);
			try {
				ctx.stop();
			} catch (Exception e) {
				//ignore
			}
		}
		return result;
	}

	private void counterIncr(String type, String name) {
		try {
			registry.counter(METRICS_COUNT, type, name).incr();
		} catch (Exception e) {
			LOGGER.error("counter incr occurs error", e);
		}
	}

	private Histogram.Context beginRecord(String type, String name) {
		try {
			return registry.histogram(METRICS_HISTOGRAM, type, name)
					.time();
		} catch (Exception e) {
			LOGGER.warn("histogram occurs error", e);
			return null;
		}
	}


	private void metricsError(Result result, String methodName) {
		try {
			String errType = errorType(result);
			if (errType != null) {
				counterIncr(errType, methodName);
			}
		} catch (Exception e) {
			LOGGER.warn("error type occurs error", e);
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
			return COUNT_FAILED;
		}
		return null;
	}

	public String fullMethodName(Invoker<?> invoker, Invocation invocation) {
		String typeName = invoker.getInterface().getName();
		String methodName = invocation.getMethodName();
		return String.format("%s#%s", typeName, methodName);
	}
}