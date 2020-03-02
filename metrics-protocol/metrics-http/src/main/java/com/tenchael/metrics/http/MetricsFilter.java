package com.tenchael.metrics.http;

import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.protocol.Protocol;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.tenchael.metrics.http.Constants.*;

public class MetricsFilter extends Protocol.Base implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsFilter.class);


	public MetricsFilter() {
		super();
	}


	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!metricsEnable()) {
			chain.doFilter(request, response);
			return;
		}

		String methodName = requestUrl((HttpServletRequest) request);
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
			String errType = errorType(response);
			if (errType != null) {
				counterIncr(errType, methodName);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 错误类型
	 *
	 * @param response
	 * @return 如果有错误返回错误名称，如果没有错误，返回null
	 */
	protected String errorType(final ServletResponse response) {
		HttpServletResponse resp = (HttpServletResponse) response;
		if (resp.getStatus() >= 500) {
			return COUNT_FAILED;
		}
		return null;
	}

	@Override
	public void destroy() {
	}

	public String requestUrl(HttpServletRequest request) {
		return request.getRequestURI();
	}
}
