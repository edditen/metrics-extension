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


}
