package com.tenchael.jmx.ext;

import com.tenchael.jmx.ext.support.Whitebox;
import com.tenchael.metrics.extension.common.SwallowExceptionListener;
import com.tenchael.metrics.extension.common.UniformSwallowHolder;
import mockit.Mock;
import mockit.MockUp;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MBeanRegistryTests extends Assert {

	private SwallowExceptionListener originListener;

	@Before
	public void setUp() {
		originListener = UniformSwallowHolder.getListener();
	}

	@After
	public void tearDown() {
		UniformSwallowHolder.setListener(originListener);
		MBeanRegistry.getInstance().unregisterAll();
	}

	@Test
	public void testMBeanRegistryConstructor() throws Exception {
		new MockUp<ManagementFactory>() {
			@Mock
			public MBeanServer getPlatformMBeanServer() {
				throw new Error("get platform mBeanServer failed");
			}
		};

		MBeanRegistry mBeanRegistry = Whitebox.newInstance(MBeanRegistry.class);
		assertNotNull(mBeanRegistry);
		assertNotNull(mBeanRegistry.getMBeanServer());
	}

	@Test
	public void testGetInstance() {
		assertNotNull(MBeanRegistry.getInstance());
	}

	private MBean createMBean(String domain, String type, String name) throws MalformedObjectNameException {
		final ObjectName objectName = Commons.objectName(domain, type, name);
		return new MBean.BaseMBean(objectName) {
			@Override
			public ObjectName getOname() {
				return super.getOname();
			}
		};
	}

	private MBean createMBean(String name) throws MalformedObjectNameException {
		return createMBean("jmx.test", "Test", name);
	}

	@Test
	public void testRegister() throws MalformedObjectNameException {
		MBean mBean = JmxConfig.newInstance("hello", "Test", "testRegister");
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		MBeanRegistry.getInstance().register(oname, mBean);

		Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
				.getInternalState(MBeanRegistry.getInstance(), "registeredMBeans");
		assertTrue(map.containsKey(oname));
		assertTrue(map.containsValue(mBean));
	}


	@Test
	public void testRegister_InstanceAlreadyExistsException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(InstanceAlreadyExistsException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String name = "testRegister_InstanceAlreadyExistsException";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof InstanceAlreadyExistsException);
			}
		});

		registry.register(oname, mBean);

		assertTrue(true);
	}

	@Test
	public void testRegister_MBeanRegistrationException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(MBeanRegistrationException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String name = "testRegister_MBeanRegistrationException";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof MBeanRegistrationException);
			}
		});

		registry.register(oname, mBean);

		assertTrue(true);
	}

	@Test
	public void testRegister_NotCompliantMBeanException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(NotCompliantMBeanException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String name = "testRegister_NotCompliantMBeanException";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof NotCompliantMBeanException);
			}
		});

		registry.register(oname, mBean);

		assertTrue(true);
//        Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
//                .getInternalState(registry, "registeredMBeans");
//        assertFalse(map.containsKey(objectName));
//        assertFalse(map.containsValue(mBean));
	}

	@Test
	public void testRegister_duplicated() throws Exception {
		String name = "testRegister_duplicated";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		MBeanRegistry.getInstance().register(oname, mBean);
		MBeanRegistry.getInstance().register(oname, createMBean("testRegister_duplicated2"));

		assertTrue(true);

	}

	@Test
	public void testRegister_duplicated_sameObject() throws MalformedObjectNameException {
		String name = "testRegister_duplicated_sameObject";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		String name2 = "testRegister_duplicated_sameObject2";
		MBean mBean2 = createMBean(name2);
		ObjectName oname2 = mBean2.getOname();
		System.out.println(oname);

		MBeanRegistry.getInstance().register(oname, mBean);
		MBeanRegistry.getInstance().register(oname2, mBean);

		assertTrue(true);
	}

	@Test
	public void testUnregister() throws MalformedObjectNameException {
		MBean mBean = JmxConfig.newInstance("hello", "Test", "testUnregister");
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		MBeanRegistry.getInstance().register(oname, mBean);

		Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
				.getInternalState(MBeanRegistry.getInstance(), "registeredMBeans");
		assertTrue(map.containsKey(oname));
		assertTrue(map.containsValue(mBean));

		MBeanRegistry.getInstance().unregister(oname);

		assertFalse(map.containsKey(oname));
		assertFalse(map.containsValue(mBean));
	}

	@Test
	public void testUnregisterAll() throws MalformedObjectNameException {
		MBean mBean = JmxConfig.newInstance("hello", "Test", "testUnregister");
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		MBeanRegistry.getInstance().register(oname, mBean);

		Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
				.getInternalState(MBeanRegistry.getInstance(), "registeredMBeans");
		assertTrue(map.containsKey(oname));
		assertTrue(map.containsValue(mBean));

		MBeanRegistry.getInstance().unregisterAll();

		assertTrue(map.isEmpty());
	}

	@Test
	public void testUnregister_MBeanRegistrationException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(MBeanRegistrationException.class).when(mbeanServer)
				.unregisterMBean(any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String name = "testUnregister";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof MBeanRegistrationException);
			}
		});

		registry.unregister(oname);

		assertTrue(true);
	}

	@Test
	public void testUnregister_InstanceNotFoundException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(InstanceNotFoundException.class).when(mbeanServer)
				.unregisterMBean(any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String name = "testUnregister";
		MBean mBean = createMBean(name);
		ObjectName oname = mBean.getOname();
		System.out.println(oname);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof InstanceNotFoundException);
			}
		});

		registry.unregister(oname);

		assertTrue(true);
	}

}
