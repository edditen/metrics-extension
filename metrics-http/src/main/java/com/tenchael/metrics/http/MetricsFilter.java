package com.tenchael.metrics.http;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;
import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.tenchael.metrics.extension.utils.SwallowExceptionHandler.swallow;
import static com.tenchael.metrics.http.Constants.*;

public class MetricsFilter implements Filter {

	public static final String METRICS_EXTENSION_ENABLE = "metrics.extension.http.enable";
	public static final boolean METRICS_ENABLE_DEFAULT = true;
	private final MetricsRegistry registry;
	private boolean metricsEnable;

	public MetricsFilter() {
		String metricsEnableProps = System.getProperty(METRICS_EXTENSION_ENABLE, "true");
		this.metricsEnable = convertEnable(metricsEnableProps);
		this.registry = MetricsRegistry.getInstance();
		this.registry.addListener(JmxReporter.JmxListener.getInstance());
	}

	public boolean convertEnable(String enableStr) {
		if (enableStr == null) {
			return METRICS_ENABLE_DEFAULT;
		}
		switch (enableStr) {
			case "false":
				return false;
			case "true":
				return true;
			default:
				return METRICS_ENABLE_DEFAULT;
		}
	}


	@Override
	public void init(FilterConfig config) throws ServletException {
		String enableIni = config.getInitParameter(METRICS_EXTENSION_ENABLE);
		metricsEnable = convertEnable(enableIni);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!metricsEnable) {
			chain.doFilter(request, response);
			return;
		}

		String methodName = requestUri((HttpServletRequest) request);
		counterIncr(COUNT_REQUEST, methodName);
		Histogram.Context ctx = beginRecord(HISTOGRAM_ELAPSE, methodName);
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			counterIncr(COUNT_FAILED, methodName);
			throw e;
		} finally {
			counterIncr(COUNT_COMPLETE, methodName);
			metricsError(response, methodName);
			try {
				ctx.stop();
			} catch (Exception e) {
				//ignore
			}
		}
	}

	private void metricsError(final ServletResponse response, String methodName) {
		try {
			String errType = this.errorType(response);
			if (errType != null) {
				registry.counter(Constants.METRICS_COUNT, errType, methodName).incr();

			}
		} catch (Exception e) {
			swallow("error type occurs error", e);
		}
	}

	private void counterIncr(String type, String name) {
		try {
			registry.counter(METRICS_COUNT, type, name).incr();
		} catch (Exception e) {
			swallow("counter incr occurs error", e);
		}
	}

	private Histogram.Context beginRecord(String type, String name) {
		try {
			return registry.histogram(METRICS_HISTOGRAM, type, name)
					.time();
		} catch (Exception e) {
			swallow("histogram occurs error", e);
			return null;
		}
	}

	protected String errorType(ServletResponse response) {
		HttpServletResponse resp = (HttpServletResponse) response;
		if (resp.getStatus() >= 500) {
			return COUNT_FAILED;
		}
		return null;
	}

	@Override
	public void destroy() {
	}

	public String requestUri(HttpServletRequest request) {
		return request.getRequestURI();
	}
}
