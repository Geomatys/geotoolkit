/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter.accessor;

import java.util.Map;
import java.util.HashMap;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test map accessor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapAccessorTest {

    @Test
    public void testGetter(){
        final Map<String,Object> map = new HashMap<String, Object>();
        map.put("age", 45);
        map.put("name", "marcel");
        map.put("human", true);

        final PropertyAccessor accessor = Accessors.getAccessor(Map.class, "age", Object.class);
        assertNotNull(accessor);

        //test access
        assertEquals(Integer.valueOf(45), accessor.get(map, "age", Object.class));
        assertEquals("marcel", accessor.get(map, "name", Object.class));
        assertEquals(true, accessor.get(map, "human", Object.class));

        //test convertion
        assertEquals("45", accessor.get(map, "age", String.class));
    }

    @Test
    public void testSetter(){
        final Map<String,Object> map = new HashMap<String, Object>();

        final PropertyAccessor accessor = Accessors.getAccessor(Map.class, "age", Object.class);
        assertNotNull(accessor);

        accessor.set(map, "age", 45, Integer.class);
        accessor.set(map, "name", "marcel", String.class);
        accessor.set(map, "human", true, Boolean.class);

        //test access
        assertEquals(Integer.valueOf(45), accessor.get(map, "age", Object.class));
        assertEquals("marcel", accessor.get(map, "name", Object.class));
        assertEquals(true, accessor.get(map, "human", Object.class));

        //test convertion
        assertEquals("45", accessor.get(map, "age", String.class));
    }

}
