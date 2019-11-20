package com.tenchael.metrics.extension.utils;

import org.junit.Assert;
import org.junit.Test;

public class NameUtilsTests extends Assert {

    @Test
    public void testBaseOName() {
        String oName = NameUtils.oname("hello.world", "Ruok", "i.am.fine");
        System.out.println(oName);
        String expect = "hello.world:type=Ruok,name=\"i.am.fine\"";
        assertEquals(expect, oName);
    }

    @Test
    public void testName() {
        String prefix = "hello.com";
        String part1 = "world";
        String part2 = "come";
        String part3 = "on";
        String name = NameUtils.name(prefix, '#', part1, part2, part3);
        System.out.println(name);
        assertEquals("hello.com#world#come#on", name);
    }


}
