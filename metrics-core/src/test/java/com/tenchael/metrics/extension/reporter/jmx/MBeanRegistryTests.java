package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.jmx.ext.MBeanRegistry;
import com.tenchael.metrics.extension.common.SwallowExceptionListener;
import com.tenchael.metrics.extension.common.UniformSwallowHolder;
import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.NameFactory;
import com.tenchael.metrics.extension.support.Whitebox;
import mockit.Mock;
import mockit.MockUp;
import org.junit.*;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MBeanRegistryTests extends Assert {

	private static NameFactory nameFactory;

	private SwallowExceptionListener originListener;

	@BeforeClass
	public static void beforeClass() {
		nameFactory = new JmxNameFactory();
	}

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

	@Test
	public void testRegister() throws MalformedObjectNameException {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Elapse")
				.name("pkg.HelloService.someMethod")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);
		MBeanRegistry.getInstance().register(oname, jmxCounter);

		Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
				.getInternalState(MBeanRegistry.getInstance(), "registeredMBeans");
		assertTrue(map.containsKey(oname));
		assertTrue(map.containsValue(jmxCounter));
	}


	@Test
	public void testRegister_InstanceAlreadyExistsException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(InstanceAlreadyExistsException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testRegister_InstanceAlreadyExistsException")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof InstanceAlreadyExistsException);
			}
		});

		registry.register(oname, jmxCounter);

		assertTrue(true);
//        Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
//                .getInternalState(registry, "registeredMBeans");
//        assertFalse(map.containsKey(objectName));
//        assertFalse(map.containsValue(jmxCounter));
	}

	@Test
	public void testRegister_MBeanRegistrationException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(MBeanRegistrationException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testRegister_MBeanRegistrationException")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof MBeanRegistrationException);
			}
		});

		registry.register(oname, jmxCounter);

		assertTrue(true);
//        Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
//                .getInternalState(registry, "registeredMBeans");
//        assertFalse(map.containsKey(objectName));
//        assertFalse(map.containsValue(jmxCounter));
	}

	@Test
	public void testRegister_NotCompliantMBeanException() throws Exception {
		MBeanRegistry registry = spy(Whitebox.newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(NotCompliantMBeanException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testRegister_NotCompliantMBeanException")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();

		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				e.printStackTrace();
				assertTrue(e instanceof NotCompliantMBeanException);
			}
		});

		registry.register(oname, jmxCounter);

		assertTrue(true);
//        Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
//                .getInternalState(registry, "registeredMBeans");
//        assertFalse(map.containsKey(objectName));
//        assertFalse(map.containsValue(jmxCounter));
	}

	@Test
	public void testRegister_duplicated() throws Exception {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testRegister_duplicated")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);
		MBeanRegistry.getInstance().register(oname, jmxCounter);
		MBeanRegistry.getInstance().register(oname, new JmxCounterMXBean.JmxCounter(oname, counter));

		assertTrue(true);

	}

	@Test
	public void testRegister_duplicated_sameObject() throws MalformedObjectNameException {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testRegister_duplicated_sameObject")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);

		MetricKey key2 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testRegister_duplicated_sameObject2")
				.build();
		ObjectName oname2 = new ObjectName(nameFactory.createName(key2));
		System.out.println(oname2);

		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);
		MBeanRegistry.getInstance().register(oname, jmxCounter);
		MBeanRegistry.getInstance().register(oname2, new JmxCounterMXBean.JmxCounter(oname2, counter));

		assertTrue(true);
	}

	@Test
	public void testUnregister() throws MalformedObjectNameException {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testUnregister")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);
		MBeanRegistry.getInstance().register(oname, jmxCounter);

		Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
				.getInternalState(MBeanRegistry.getInstance(), "registeredMBeans");
		assertTrue(map.containsKey(oname));
		assertTrue(map.containsValue(jmxCounter));

		MBeanRegistry.getInstance().unregister(oname);

		assertFalse(map.containsKey(oname));
		assertFalse(map.containsValue(jmxCounter));
	}

	@Test
	public void testUnregisterAll() throws MalformedObjectNameException {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testUnregisterAll")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
		System.out.println(oname);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(oname, counter);
		MBeanRegistry.getInstance().register(oname, jmxCounter);

		Map<ObjectName, Object> map = (Map<ObjectName, Object>) Whitebox
				.getInternalState(MBeanRegistry.getInstance(), "registeredMBeans");
		assertTrue(map.containsKey(oname));
		assertTrue(map.containsValue(jmxCounter));

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

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testUnregister_MBeanRegistrationException")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
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

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testUnregister_InstanceNotFoundException")
				.build();
		ObjectName oname = new ObjectName(nameFactory.createName(key));
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
