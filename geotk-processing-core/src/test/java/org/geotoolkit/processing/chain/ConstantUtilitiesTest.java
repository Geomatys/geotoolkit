package org.geotoolkit.processing.chain;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public class ConstantUtilitiesTest {

    @Test
    public void testMap() {

        Map<String, String> inMap1 = new LinkedHashMap<>();
        inMap1.put("x", "y");
        inMap1.put("z", "a1");

        Map<String, String> inMap3 = new LinkedHashMap<>();
        inMap3.put("machin", "bidule");

        Map<String, Object> inMap2 = new LinkedHashMap<>();
        inMap2.put("test", "truc");
        inMap2.put("test2", inMap3);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", "b");
        map.put("c", "asf");
        map.put("d", "54");
        map.put("e", inMap1);
        map.put("f", inMap2);

        String value = ConstantUtilities.valueToString(map);
        assertEquals("1:a1:b1:c3:asf1:d2:541:e13:1:x1:y1:z2:a11:f38:4:test4:truc5:test216:6:machin6:bidule", value);

        Map<String, Object> result = ConstantUtilities.stringToValue(value, map.getClass());
        assertEquals(map.get("a"), result.get("a"));
        assertEquals(map.get("c"), result.get("c"));
        assertEquals(map.get("d"), result.get("d"));

        // As the output of objects inside the "source" Map are HashMap (we lost the LinkedHashMap from the source data)
        // results can be in different orders

        assertEquals(Set.of("x", "z"), ((Map)result.get("e")).keySet());
        assertEquals(Set.of("test", "test2"), ((Map)result.get("f")).keySet());
        assertEquals(Set.of("machin"), ((Map)((Map)result.get("f")).get("test2")).keySet());
    }
}
