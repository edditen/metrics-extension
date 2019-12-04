package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.NameFactory;
import org.junit.Test;

import javax.management.ObjectName;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MBeanTests {

	@Test
	public void testBaseMBean() {
		ObjectName objectName = null;
		MBean mBean = new MBean.BaseMBean(objectName) {
			@Override
			public ObjectName getOname() {
				return super.getOname();
			}
		};
		ObjectName oname = mBean.getOname();
		assertNull(oname);
	}


	@Test
	public void testJmxCounter() throws Exception {
		NameFactory nameFactory = new NameFactory.DefaultNameFactory();
		String oname = nameFactory.createName("com.tenchael.metrics.extension.reporter.jmx", "Test", "testJmxCounter");
		ObjectName objectName = new ObjectName(oname);
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, new Counter());
		long count = jmxCounter.getCount();
		assertEquals(0, count);
		assertNotNull(jmxCounter.getOname());
	}

	@Test
	public void testJmxHistogram() throws Exception {
		NameFactory nameFactory = new NameFactory.DefaultNameFactory();
		String oname = nameFactory.createName("com.tenchael.metrics.extension.reporter.jmx", "Test", "testJmxCounter");
		ObjectName objectName = new ObjectName(oname);
		JmxHistogramMXBean.JmxHistogram jmxHistogram = new JmxHistogramMXBean.JmxHistogram(objectName, new Histogram());
		assertEquals(0, jmxHistogram.getCount());
		assertEquals(TimeUnit.MILLISECONDS, jmxHistogram.getDurationUnit());
		assertEquals(0, jmxHistogram.getU10());
		assertEquals(0, jmxHistogram.getU50());
		assertEquals(0, jmxHistogram.getU500());
		assertNotNull(jmxHistogram.getOname());
		jmxHistogram.getMax();
		jmxHistogram.getMin();
		jmxHistogram.getMean();
		jmxHistogram.getMedian();
		jmxHistogram.getP75();
		jmxHistogram.getP95();
		jmxHistogram.getP98();
		jmxHistogram.getP99();
		jmxHistogram.getP999();
		jmxHistogram.getStddev();
		assertTrue(true);

	}

}
