package com.tenchael.metrics.http;

import com.tenchael.metrics.extension.common.Constants;
import com.tenchael.metrics.extension.common.PropertiesManager;
import com.tenchael.metrics.http.support.Whitebox;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MetricsFilterTests {

	@Before
	public void setUp() {
		Properties properties = PropertiesManager.getInstance().getProperties();
//		properties.setProperty(Constants.PropsKey.METRICS_ENABLE, null);
		properties.remove(Constants.PropsKey.METRICS_ENABLE);
	}

	@Test
	public void testMetricsEnable() {
		MetricsFilter filter = new MetricsFilter();
		Object metricsEnable = Whitebox.invokeMethod(filter, "metricsEnable");
		assertEquals(true, metricsEnable);
	}


	@Test
	public void testDestroy() throws ServletException {
		//1. prepare data
		MetricsFilter filter = new MetricsFilter();
		filter.destroy();
		assertTrue(true);
	}


	@Test
	public void testDoFilter() throws Exception {
		//1. prepare data
		MetricsFilter filter = Mockito.spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doNothing().when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUrl(request);
		//3. execution
		filter.doFilter(request, response, chain);
		//4. assert
		assertTrue(true);
	}

	@Test(expected = RuntimeException.class)
	public void testDoFilter_exception() throws Exception {
		//1. prepare data
		MetricsFilter filter = Mockito.spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doThrow(new RuntimeException("oops")).when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUrl(request);
		//3. execution
		try {
			filter.doFilter(request, response, chain);
			assertFalse(true);
		} catch (Exception e) {
			assertEquals("oops", e.getMessage());
			throw e;
		}
	}

	@Test
	public void testDoFilter_notMetric() throws Exception {
		//1. prepare data

		Properties properties = PropertiesManager.getInstance().getProperties();
		properties.setProperty(Constants.PropsKey.METRICS_ENABLE, "false");

		MetricsFilter filter = Mockito.spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doNothing().when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUrl(request);
		//3. execution
		filter.doFilter(request, response, chain);
		//4. assert
		assertTrue(true);
	}

	@Test
	public void testDoFilter_requestUri() throws Exception {
		//1. prepare data
		MetricsFilter filter = Mockito.spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);

		//2. mock
		doReturn("/hello").when(request).getRequestURI();
		//3. execution
		String uri = filter.requestUrl(request);
		//4. assert
		assertEquals("/hello", uri);
	}

	@Test
	public void testDoSubFilter() throws Exception {
		//1. prepare data
		MetricsFilter filter = spy(new MetricsFilter() {
			@Override
			protected String errorType(ServletResponse response) {
				return "ParamFailed";
			}
		});
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doNothing().when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUrl(request);
		//3. execution
		filter.doFilter(request, response, chain);
		//4. assert
		assertTrue(true);
	}

}
