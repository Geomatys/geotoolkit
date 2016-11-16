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

import org.apache.sis.parameter.ParameterBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Test parameter accessor.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ParameterBindingTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testGetter(){
        final ParameterDescriptor p1 = new ParameterBuilder().addName("age").create(Integer.class, null);
        final ParameterDescriptor p2 = new ParameterBuilder().addName("name").create(String.class, null);
        final ParameterDescriptor p3 = new ParameterBuilder().addName("human").create(Boolean.class, null);
        final ParameterDescriptorGroup desc = new ParameterBuilder().addName("ele").createGroup(p1,p2,p3);
        final ParameterValueGroup param = desc.createValue();
        param.parameter("age").setValue(45);
        param.parameter("name").setValue("marcel");
        param.parameter("human").setValue(true);

        final Binding accessor = Bindings.getBinding(ParameterValueGroup.class, "age");
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
        final ParameterDescriptor p1 = new ParameterBuilder().addName("age").create(Integer.class, null);
        final ParameterDescriptor p2 = new ParameterBuilder().addName("name").create(String.class, null);
        final ParameterDescriptor p3 = new ParameterBuilder().addName("human").create(Boolean.class, null);
        final ParameterDescriptorGroup desc = new ParameterBuilder().addName("ele").createGroup(p1,p2,p3);
        final ParameterValueGroup param = desc.createValue();
        param.parameter("age").setValue(45);
        param.parameter("name").setValue("marcel");
        param.parameter("human").setValue(true);

        final Binding accessor = Bindings.getBinding(ParameterValueGroup.class, "age");
        assertNotNull(accessor);

        accessor.set(param, "age", 45);
        accessor.set(param, "name", "marcel");
        accessor.set(param, "human", true);

        //test access
        assertEquals(Integer.valueOf(45), accessor.get(param, "age", Object.class));
        assertEquals("marcel", accessor.get(param, "name", Object.class));
        assertEquals(true, accessor.get(param, "human", Object.class));

        //test convertion
        assertEquals("45", accessor.get(param, "age", String.class));
    }

}
