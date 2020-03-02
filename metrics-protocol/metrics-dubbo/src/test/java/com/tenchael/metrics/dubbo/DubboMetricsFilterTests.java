package com.tenchael.metrics.dubbo;


import com.tenchael.metrics.extension.common.Constants;
import com.tenchael.metrics.extension.common.PropertiesManager;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.mockito.Mockito.*;

public class DubboMetricsFilterTests extends Assert {

	@Before
	public void setUp() {
		Properties properties = PropertiesManager.getInstance().getProperties();
//		properties.setProperty(Constants.PropsKey.METRICS_ENABLE, "");
		properties.remove(Constants.PropsKey.METRICS_ENABLE);
	}


	@Test
	public void testInvoke() {
		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);
		Result result = mock(Result.class);

		doReturn("domain.Hello#echo").when(filter).fullMethodName(invoker, invocation);
		doReturn(result).when(invoker).invoke(invocation);
		Result ret = filter.invoke(invoker, invocation);
		assertEquals(result, ret);
	}

	@Test
	public void testInvoke_notMetiricsEnable() throws Exception {
		Properties properties = PropertiesManager.getInstance().getProperties();
		properties.setProperty(Constants.PropsKey.METRICS_ENABLE, "false");


		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());

		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);
		Result result = mock(Result.class);

		doReturn(result).when(invoker).invoke(invocation);
		Result ret = filter.invoke(invoker, invocation);
		assertEquals(result, ret);
	}

	@Test
	public void testInvoke_Exception() {
		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);

		doReturn("domain.Hello#testInvoke_Exception").when(filter).fullMethodName(invoker, invocation);
		doThrow(new RuntimeException("oops")).when(invoker).invoke(invocation);
		try {
			filter.invoke(invoker, invocation);
			assertFalse(true);
		} catch (Exception e) {
			assertEquals("oops", e.getMessage());
		}
	}

	@Test
	public void testInvoke_hasException() {
		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);
		Result result = mock(Result.class);

		doReturn("domain.Hello#testInvoke_hasException")
				.when(filter).fullMethodName(invoker, invocation);
		doReturn(result).when(invoker).invoke(invocation);
		doReturn(true).when(result).hasException();

		Result ret = filter.invoke(invoker, invocation);
		assertEquals(result, ret);
	}

	@Test
	public void testInvoke_result_null() {
		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);

		doReturn("domain.Hello#testInvoke_hasException")
				.when(filter).fullMethodName(invoker, invocation);
		doReturn(null).when(invoker).invoke(invocation);

		Result ret = filter.invoke(invoker, invocation);
		assertEquals(null, ret);
	}

	@Test
	public void testInvoke_sub() {
		DubboMetricsFilter subFilter = new DubboMetricsFilter() {
			@Override
			protected String errorType(Result result) {
				return "paramFailed";
			}
		};

		DubboMetricsFilter filter = Mockito.spy(subFilter);
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);
		Result result = mock(Result.class);

		doReturn("domain.Hello#testInvoke_sub").when(filter).fullMethodName(invoker, invocation);
		doReturn(result).when(invoker).invoke(invocation);
		Result ret = filter.invoke(invoker, invocation);
		assertEquals(result, ret);
	}

	@Test
	public void testGetFullName() {
		//1. prepare data
		Class clazz = DubboMetricsFilterTests.class;
		DubboMetricsFilter filter = Mockito.spy(new DubboMetricsFilter());
		Invoker invoker = mock(Invoker.class);
		Invocation invocation = mock(Invocation.class);
		//2. mock
		doReturn(clazz).when(invoker).getInterface();
		String methodName = "testGetFullName";
		doReturn(methodName).when(invocation).getMethodName();
		//3. execution
		String fullName = filter.fullMethodName(invoker, invocation);
		//4.assert
		assertEquals(String.format("%s#%s", clazz.getName(), methodName), fullName);
	}

}
