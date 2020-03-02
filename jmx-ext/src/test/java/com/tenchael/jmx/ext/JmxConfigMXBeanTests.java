package com.tenchael.jmx.ext;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tenchael on 2020/2/21.
 */
public class JmxConfigMXBeanTests extends Assert {

	@Test
	public void testGetSet() {
		JmxConfig config = JmxConfig.instance();
		config.setEnabled(false);
		assertEquals(false, config.isEnabled());

	}

	@Test
	public void testNewInstance() {
		JmxConfig config = JmxConfig.newInstance("domain", "Test", "hello");
		System.out.println(config.getOname());
		String expect = "domain:type=Test,name=\"hello\"";
		assertEquals(expect, config.getOname().toString());
	}

	@Test(expected = IllegalStateException.class)
	public void testNewInstance_error() {
		try {
			JmxConfig.newInstance("domain", "Test", null);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	@Test
	public void testNotify() throws Exception {
		JmxConfig config = JmxConfig.newInstance("domain", "Test", "testNotify");
		config.setEnabled(true);
		final AtomicBoolean atb = new AtomicBoolean(false);
		final AtomicInteger count = new AtomicInteger(0);
		final CyclicBarrier barrier = new CyclicBarrier(2);
		JmxConfigListener listener = new JmxConfigListener() {
			@Override
			public void onUpdate(boolean newValue) {
				System.out.println("update to value: " + newValue);
				atb.set(newValue);
				count.incrementAndGet();
				try {
					barrier.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onInit(boolean value) {
				System.out.println("init value: " + value);
				atb.set(value);
				count.incrementAndGet();
				try {
					barrier.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		};

		//init
		assertEquals(false, atb.get());
		assertEquals(0, count.get());

		config.addListener(listener);
		barrier.await();
		assertEquals(true, atb.get());
		assertEquals(1, count.get());

		config.addListener(listener);//duplicated add
		assertEquals(true, atb.get());
		assertEquals(1, count.get());

		barrier.reset();
		config.setEnabled(false);
		barrier.await();
		assertEquals(false, atb.get());
		assertEquals(2, count.get());

		config.setEnabled(false);//set not work
		assertEquals(false, atb.get());
		assertEquals(2, count.get());

		barrier.reset();
		config.setEnabled(true);//set not work
		barrier.await();
		assertEquals(true, atb.get());
		assertEquals(3, count.get());
	}

	@Test
	public void testNotify_error() throws Exception {
		JmxConfig config = JmxConfig.newInstance("domain", "Test", "testNotify");
		config.setEnabled(true);
		final Holder<Exception> holder = new Holder<>();
		final CyclicBarrier barrier = new CyclicBarrier(2);
		JmxConfigListener listener = new JmxConfigListener() {
			@Override
			public void onUpdate(boolean newValue) {
				System.out.println("update to value: " + newValue);
				RuntimeException e = new RuntimeException("oops!");
				holder.set(e);
				try {
					barrier.await();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (BrokenBarrierException e1) {
					e1.printStackTrace();
				}
				throw e;
			}

			@Override
			public void onInit(boolean value) {
				System.out.println("init value: " + value);
			}
		};


		config.addListener(listener);
		config.setEnabled(false);//set not work
		barrier.await();
		assertNotNull(holder.get());
		assertEquals("oops!", holder.get().getMessage());
	}

	static class Holder<T> {
		private T source;

		public T get() {
			return source;
		}

		public void set(T source) {
			this.source = source;
		}
	}
}
