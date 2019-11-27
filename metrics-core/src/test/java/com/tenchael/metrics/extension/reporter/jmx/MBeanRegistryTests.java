package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.NameFactory;
import com.tenchael.metrics.extension.utils.SwallowExceptionListener;
import com.tenchael.metrics.extension.utils.UniformSwallowHolder;
import mockit.Mock;
import mockit.MockUp;
import org.junit.*;

import javax.management.*;
import java.lang.management.ManagementFactory;

import static com.tenchael.metrics.extension.support.Whitebox.newInstance;
import static org.mockito.Mockito.*;

public class MBeanRegistryTests extends Assert {

	private static NameFactory nameFactory;

	private SwallowExceptionListener originListener;

	@BeforeClass
	public static void beforeClass() {
		nameFactory = new NameFactory.DefaultNameFactory();
	}

	@Before
	public void setUp() {
		originListener = UniformSwallowHolder.getListener();
	}

	@After
	public void tearDown() {
		UniformSwallowHolder.setListener(originListener);
	}

	@Test
	public void testMBeanRegistryConstructor() throws Exception {
		new MockUp<ManagementFactory>() {
			@Mock
			public MBeanServer getPlatformMBeanServer() {
				throw new Error("get platform mBeanServer failed");
			}
		};

		MBeanRegistry mBeanRegistry = newInstance(MBeanRegistry.class);
		assertNotNull(mBeanRegistry);
		assertNotNull(mBeanRegistry.getMBeanServer());
	}

	@Test
	public void testGetInstance() {
		assertNotNull(MBeanRegistry.getInstance());
	}

	@Test
	public void testRegister() throws MalformedObjectNameException {
		String oName = nameFactory.createName("total", "Counter", "sayHello");
		ObjectName objectName = new ObjectName(oName);
		System.out.println(objectName);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
		MBeanRegistry.getInstance().register(objectName, jmxCounter);

		assertTrue(true);
	}


	@Test
	public void testRegister_InstanceAlreadyExistsException() throws Exception {
		MBeanRegistry registry = spy(newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(InstanceAlreadyExistsException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String oName = nameFactory.createName("total", "Counter", "testRegister_InstanceAlreadyExistsException");
		ObjectName objectName = new ObjectName(oName);
		System.out.println(objectName);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				e.printStackTrace();
				assertTrue(e instanceof InstanceAlreadyExistsException);
			}
		});

		registry.register(objectName, jmxCounter);

		assertTrue(true);
	}

	@Test
	public void testRegister_MBeanRegistrationException() throws Exception {
		MBeanRegistry registry = spy(newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(MBeanRegistrationException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String oName = nameFactory.createName("total", "Counter", "testRegister_MBeanRegistrationException");
		ObjectName objectName = new ObjectName(oName);
		System.out.println(objectName);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				e.printStackTrace();
				assertTrue(e instanceof MBeanRegistrationException);
			}
		});

		registry.register(objectName, jmxCounter);

		assertTrue(true);
	}

	@Test
	public void testRegister_NotCompliantMBeanException() throws Exception {
		MBeanRegistry registry = spy(newInstance(MBeanRegistry.class));
		MBeanServer mbeanServer = mock(MBeanServer.class);
		doThrow(NotCompliantMBeanException.class).when(mbeanServer)
				.registerMBean(any(Object.class), any(ObjectName.class));
		doReturn(mbeanServer).when(registry).getMBeanServer();

		String oName = nameFactory.createName("total", "Counter", "testRegister_NotCompliantMBeanException");
		ObjectName objectName = new ObjectName(oName);
		System.out.println(objectName);
		Counter counter = new Counter();

		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);

		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				e.printStackTrace();
				assertTrue(e instanceof NotCompliantMBeanException);
			}
		});

		registry.register(objectName, jmxCounter);

		assertTrue(true);
	}

	@Test
	public void testRegister_duplicated() throws Exception {
		String oName = nameFactory.createName("total.count", "Counter", "come.on.sayHello2");
		ObjectName objectName = new ObjectName(oName);
		System.out.println(objectName);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
		MBeanRegistry.getInstance().register(objectName, jmxCounter);
		MBeanRegistry.getInstance().register(objectName, new JmxCounterMXBean.JmxCounter(objectName, counter));

		assertTrue(true);

	}

	@Test
	public void testRegister_duplicated_sameObject() throws MalformedObjectNameException {
		String oName = nameFactory.createName("failed.count", "Counter", "sayHello3");
		String oName2 = nameFactory.createName("complete.count", "Counter", "sayHello4");
		ObjectName objectName = new ObjectName(oName);
		ObjectName objectName2 = new ObjectName(oName2);
		System.out.println(objectName);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
		MBeanRegistry.getInstance().register(objectName, jmxCounter);
		MBeanRegistry.getInstance().register(objectName, new JmxCounterMXBean.JmxCounter(objectName2, counter));

		assertTrue(true);
	}

	@Test
	public void testUnregister() throws MalformedObjectNameException {
		String oName = nameFactory.createName("hello.world", "Counter", "sayHello");
		ObjectName objectName = new ObjectName(oName);
		System.out.println(objectName);
		Counter counter = new Counter();
		JmxCounterMXBean.JmxCounter jmxCounter = new JmxCounterMXBean.JmxCounter(objectName, counter);
		MBeanRegistry.getInstance().register(objectName, jmxCounter);
		MBeanRegistry.getInstance().unregister(objectName);

		assertTrue(true);
	}

}
