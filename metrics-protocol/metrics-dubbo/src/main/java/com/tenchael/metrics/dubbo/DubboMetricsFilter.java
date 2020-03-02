package com.tenchael.metrics.dubbo;


import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.protocol.Protocol;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import static com.tenchael.metrics.dubbo.Constants.*;
import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;


@Activate(group = {CONSUMER, PROVIDER})
public class DubboMetricsFilter extends Protocol.Base implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(DubboMetricsFilter.class);

	public DubboMetricsFilter() {
		super();
	}

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		if (!metricsEnable()) {
			return invoker.invoke(invocation);
		}
		String remoteAddr = remoteAddress();
		counterIncr(COUNT_REMOTE_REQUESTS, remoteAddr);

		String methodName = fullMethodName(invoker, invocation);
		counterIncr(COUNT_REQUEST, methodName);

		Histogram.Context ctx = beginRecord(HISTOGRAM_ELAPSE, methodName);
		Result result = null;
		try {
			result = invoker.invoke(invocation);
		} catch (Exception e) {
			counterIncr(COUNT_FAILED, methodName);
			throw e;
		} finally {
			counterIncr(COUNT_COMPLETE, methodName);
			metricsError(result, methodName);
			try {
				ctx.stop();
			} catch (Exception e) {
				//ignore
			}
		}


		return result;
	}

	private void metricsError(Result result, String methodName) {
		try {
			String errType = this.errorType(result);
			if (errType != null) {
				counterIncr(errType, methodName);
			}
		} catch (Exception e) {
			LOGGER.warn("errorType occurs error ", e);
		}

	}

	/**
	 * 错误类型
	 *
	 * @param result 结果
	 * @return 如果有错误返回错误名称，如果没有错误，返回null
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

	public String remoteAddress() {
		return RpcContext.getContext().getRemoteHost();
	}

}