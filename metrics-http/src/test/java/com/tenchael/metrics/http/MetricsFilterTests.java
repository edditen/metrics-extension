package com.tenchael.metrics.http;

import com.tenchael.metrics.http.support.Whitebox;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MetricsFilterTests {

	@Before
	public void setUp() {
		System.setProperty(MetricsFilter.METRICS_EXTENSION_ENABLE, "");
	}

	@Test
	public void testMetricsEnable() {
		MetricsFilter filter = new MetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(true, metricsEnable);
	}

	@Test
	public void testMetricsEnable_setTrue() {
		System.setProperty(MetricsFilter.METRICS_EXTENSION_ENABLE, "true");
		MetricsFilter filter = new MetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(true, metricsEnable);
	}

	@Test
	public void testMetricsEnable_setFalse() {
		System.setProperty(MetricsFilter.METRICS_EXTENSION_ENABLE, "false");
		MetricsFilter filter = new MetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(false, metricsEnable);
	}

	@Test
	public void testMetricsEnable_illegalValue() {
		System.setProperty(MetricsFilter.METRICS_EXTENSION_ENABLE, "hahaha");
		MetricsFilter filter = new MetricsFilter();
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(true, metricsEnable);
	}

	@Test
	public void testConvertEnable() {
		MetricsFilter filter = new MetricsFilter();
		boolean enable = filter.convertEnable(null);
		assertEquals(MetricsFilter.METRICS_ENABLE_DEFAULT, enable);
	}

	@Test
	public void testConvertEnable_true() {
		MetricsFilter filter = new MetricsFilter();
		boolean enable = filter.convertEnable("true");
		assertEquals(true, enable);
	}

	@Test
	public void testConvertEnable_false() {
		MetricsFilter filter = new MetricsFilter();
		boolean enable = filter.convertEnable("false");
		assertEquals(false, enable);
	}

	@Test
	public void testConvertEnable_illegalBool() {
		MetricsFilter filter = new MetricsFilter();
		boolean enable = filter.convertEnable("sgs");
		assertEquals(MetricsFilter.METRICS_ENABLE_DEFAULT, enable);
	}

	@Test
	public void testInit() throws ServletException {
		//1. prepare data
		MetricsFilter filter = new MetricsFilter();
		FilterConfig config = mock(FilterConfig.class);

		//2. mock
		doReturn("false").when(config).getInitParameter(MetricsFilter.METRICS_EXTENSION_ENABLE);
		//3. execution
		filter.init(config);
		//4. assert
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(false, metricsEnable);
	}

	@Test
	public void testDestroy() throws ServletException {
		//1. prepare data
		MetricsFilter filter = new MetricsFilter();
		filter.destroy();
		assertTrue(true);
	}

	@Test
	public void testInit_null() throws ServletException {
		//1. prepare data
		MetricsFilter filter = new MetricsFilter();
		FilterConfig config = mock(FilterConfig.class);

		//2. mock
		doReturn(null).when(config).getInitParameter(MetricsFilter.METRICS_EXTENSION_ENABLE);
		//3. execution
		filter.init(config);
		//4. assert
		Object metricsEnable = Whitebox.getInternalState(filter, "metricsEnable");
		assertEquals(MetricsFilter.METRICS_ENABLE_DEFAULT, metricsEnable);
	}

	@Test
	public void testDoFilter() throws Exception {
		//1. prepare data
		MetricsFilter filter = spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doNothing().when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUri(request);
		//3. execution
		filter.doFilter(request, response, chain);
		//4. assert
		assertTrue(true);
	}

	@Test(expected = RuntimeException.class)
	public void testDoFilter_exception() throws Exception {
		//1. prepare data
		MetricsFilter filter = spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doThrow(new RuntimeException("oops")).when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUri(request);
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
		System.setProperty(MetricsFilter.METRICS_EXTENSION_ENABLE, "false");
		MetricsFilter filter = spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doNothing().when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUri(request);
		//3. execution
		filter.doFilter(request, response, chain);
		//4. assert
		assertTrue(true);
	}

	@Test
	public void testDoFilter_requestUri() throws Exception {
		//1. prepare data
		MetricsFilter filter = spy(new MetricsFilter());
		HttpServletRequest request = mock(HttpServletRequest.class);

		//2. mock
		doReturn("/hello").when(request).getRequestURI();
		//3. execution
		String uri = filter.requestUri(request);
		//4. assert
		assertEquals("/hello", uri);
	}

	@Test
	public void testDoSubFilter() throws Exception {
		//1. prepare data
		MetricsFilter filter = spy(new MetricsFilter() {
			@Override
			protected String errorType(ServletResponse response) {
				return "paramFailed";
			}
		});
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		//2. mock
		doNothing().when(chain).doFilter(request, response);
		doReturn("/hello").when(filter).requestUri(request);
		//3. execution
		filter.doFilter(request, response, chain);
		//4. assert
		assertTrue(true);
	}

}
