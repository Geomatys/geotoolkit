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
package org.geotoolkit.filter.binding;

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
public class MapBindingTest {

    @Test
    public void testGetter(){
        final Map<String,Object> map = new HashMap<String, Object>();
        map.put("age", 45);
        map.put("name", "marcel");
        map.put("human", true);

        final Binding binding = Bindings.getBinding(Map.class, "age");
        assertNotNull(binding);

        //test access
        assertEquals(Integer.valueOf(45), binding.get(map, "age", Object.class));
        assertEquals("marcel", binding.get(map, "name", Object.class));
        assertEquals(true, binding.get(map, "human", Object.class));

        //test convertion
        assertEquals("45", binding.get(map, "age", String.class));
    }

    @Test
    public void testSetter(){
        final Map<String,Object> map = new HashMap<String, Object>();

        final Binding binding = Bindings.getBinding(Map.class, "age");
        assertNotNull(binding);

        binding.set(map, "age", 45);
        binding.set(map, "name", "marcel");
        binding.set(map, "human", true);

        //test access
        assertEquals(Integer.valueOf(45), binding.get(map, "age", Object.class));
        assertEquals("marcel", binding.get(map, "name", Object.class));
        assertEquals(true, binding.get(map, "human", Object.class));

        //test convertion
        assertEquals("45", binding.get(map, "age", String.class));
    }

}
