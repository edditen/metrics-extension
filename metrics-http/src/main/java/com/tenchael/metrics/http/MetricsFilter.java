package com.tenchael.metrics.http;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;
import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsFilter implements Filter {

    public static final String REQUEST_COUNT = "Request";
    public static final String COMPLETE_COUNT = "Complete";
    public static final String SYS_FAILED = "SysFailed";
    public static final String HISTOGRAM = "Histogram";
    public static final String METRICS_EXTENSION_ENABLE = "metrics.extension.http.enable";
    public static final boolean METRICS_ENABLE_DEFAULT = true;
    private final MetricsRegistry registery;
    private boolean metricsEnable;

    public MetricsFilter() {
        String metricsEnableProps = System.getProperty(METRICS_EXTENSION_ENABLE, "true");
        this.metricsEnable = convertEnable(metricsEnableProps);
        this.registery = MetricsRegistry.getInstance();
        this.registery.addListener(JmxReporter.JmxListener.getInstance());
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
        registery.counter(Constants.METRICS_COUNT, REQUEST_COUNT, methodName).incr();
        Histogram.Context ctx = registery
                .histogram(Constants.METRICS_HISTOGRAM, HISTOGRAM, methodName).time();
        try {
            chain.doFilter(request, response);
            metricsError(response, methodName);
        } catch (Exception e) {
            registery.counter(Constants.METRICS_COUNT, SYS_FAILED, methodName).incr();
            throw e;
        } finally {
            registery.counter(Constants.METRICS_COUNT, COMPLETE_COUNT, methodName).incr();
            ctx.stop();
        }
    }

    private void metricsError(final ServletResponse response, String methodName) {
        String errType = errorType(response);
        if (errType != null) {
            registery.counter(Constants.METRICS_COUNT, errType, methodName).incr();

        }
    }

    protected String errorType(ServletResponse response) {
        HttpServletResponse resp = (HttpServletResponse) response;
        if (resp.getStatus() >= 500) {
            return SYS_FAILED;
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
