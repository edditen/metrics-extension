package com.tenchael.metrics.dubbo;

import com.tenchael.metrics.dubbo.support.Whitebox;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DubboMetricsFilterTests extends Assert {

	@Before
	public void setUp() {
		System.setProperty(DubboMetricsFilter.METRICS_EXTENSION_ENABLE, "");
	}

	@Test
	public void testMetricsEnable() {
		DubboMetricsFilter filter = new DubboMetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(true, metricsEnable);
	}

	@Test
	public void testMetricsEnable_setFalse() {
		System.setProperty(DubboMetricsFilter.METRICS_EXTENSION_ENABLE, "false");
		DubboMetricsFilter filter = new DubboMetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(false, metricsEnable);
	}

	@Test
	public void testMetricsEnable_illegalValue() {
		System.setProperty(DubboMetricsFilter.METRICS_EXTENSION_ENABLE, "hahaha");
		DubboMetricsFilter filter = new DubboMetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(true, metricsEnable);
	}

	@Test
	public void testMetrics() {
		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);
		Result result = mock(Result.class);

		doReturn("domain.Hello#echo").when(filter).fullMethodName(invoker, invocation);
		doReturn(result).when(invoker).invoke(invocation);
		Result ret = filter.invoke(invoker, invocation);
		assertEquals(result, ret);
	}

}
