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
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.junit.Test;

import static org.junit.Assert.*;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Test parameter accessor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ParameterAccessorTest {

    @Test
    public void testGetter(){
        final ParameterDescriptor p1 = new DefaultParameterDescriptor("age", Integer.class, null, null);
        final ParameterDescriptor p2 = new DefaultParameterDescriptor("name", String.class, null, null);
        final ParameterDescriptor p3 = new DefaultParameterDescriptor("human", Boolean.class, null, null);
        final ParameterDescriptorGroup desc = new DefaultParameterDescriptorGroup("ele",p1,p2,p3);        
        final ParameterValueGroup param = desc.createValue();
        param.parameter("age").setValue(45);
        param.parameter("name").setValue("marcel");
        param.parameter("human").setValue(true);
        
        final PropertyAccessor accessor = Accessors.getAccessor(ParameterValueGroup.class, "age", Object.class);
        assertNotNull(accessor);

        //test access
        assertEquals(Integer.valueOf(45), accessor.get(param, "age", Object.class));
        assertEquals("marcel", accessor.get(param, "name", Object.class));
        assertEquals(true, accessor.get(param, "human", Object.class));

        //test convertion
        assertEquals("45", accessor.get(param, "age", String.class));
    }

    @Test
    public void testSetter(){
        final ParameterDescriptor p1 = new DefaultParameterDescriptor("age", Integer.class, null, null);
        final ParameterDescriptor p2 = new DefaultParameterDescriptor("name", String.class, null, null);
        final ParameterDescriptor p3 = new DefaultParameterDescriptor("human", Boolean.class, null, null);
        final ParameterDescriptorGroup desc = new DefaultParameterDescriptorGroup("ele",p1,p2,p3);        
        final ParameterValueGroup param = desc.createValue();
        param.parameter("age").setValue(45);
        param.parameter("name").setValue("marcel");
        param.parameter("human").setValue(true);

        final PropertyAccessor accessor = Accessors.getAccessor(ParameterValueGroup.class, "age", Object.class);
        assertNotNull(accessor);

        accessor.set(param, "age", 45, Integer.class);
        accessor.set(param, "name", "marcel", String.class);
        accessor.set(param, "human", true, Boolean.class);

        //test access
        assertEquals(Integer.valueOf(45), accessor.get(param, "age", Object.class));
        assertEquals("marcel", accessor.get(param, "name", Object.class));
        assertEquals(true, accessor.get(param, "human", Object.class));

        //test convertion
        assertEquals("45", accessor.get(param, "age", String.class));
    }

}
