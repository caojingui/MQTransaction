package com.pea.mqtransaction.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringUtilTest {

    @Test
    public void mapToString() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "b");
        map.put("a2", "b1");
        map.put("a3", "b3");
        System.out.println(StringUtil.mapToString(map));

    }

    @Test
    public void stringToMap() {
        String str = "a=b&a2=b1&a3=b3";
        Map<String, String> map = StringUtil.stringToMap(str);
        Assert.assertEquals("b", map.get("a"));
        Assert.assertEquals("b1", map.get("a2"));
        Assert.assertEquals("b3", map.get("a3"));
    }
}