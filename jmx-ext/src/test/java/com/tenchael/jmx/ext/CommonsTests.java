package com.tenchael.jmx.ext;

import com.tenchael.jmx.ext.support.Whitebox;
import org.junit.Assert;
import org.junit.Test;

import javax.management.ObjectName;

/**
 * Created by Tenchael on 2020/2/21.
 */
public class CommonsTests extends Assert {

	@Test
	public void testConstruct() throws Exception {
		try {
			Whitebox.newInstance(Commons.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}

	@Test
	public void testOname() {
		String oName = Commons.oname("hello.world", "Ruok", "i.am.fine");
		System.out.println(oName);
		String expect = "hello.world:type=Ruok,name=\"i.am.fine\"";
		assertEquals(expect, oName);
	}

	@Test
	public void testObjectName() throws Exception {
		ObjectName objectName = Commons.objectName("hello.world", "Ruok", "i.am.fine");
		System.out.println(objectName);
		String expect = "hello.world:type=Ruok,name=\"i.am.fine\"";
		assertEquals(expect, objectName.toString());
	}
}
