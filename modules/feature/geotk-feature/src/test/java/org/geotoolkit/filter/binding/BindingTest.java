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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test accessors.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class BindingTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testList(){
        final Binding[] factories = Bindings.getBindings();
        assertNotNull(factories);
        assertEquals(11, factories.length);

        //check correct order, by priority
        assertTrue( factories[0] instanceof AttributeBinding);
        assertTrue( factories[1] instanceof ComplexAttributeBinding);
        assertTrue( factories[2] instanceof XPathBinding.CAXPath);
        assertTrue( factories[3] instanceof ComplexTypeArrayBinding);
        assertTrue( factories[4] instanceof ComplexTypeBinding);
        assertTrue( factories[5] instanceof XPathBinding.CTXPath);
        assertTrue( factories[6] instanceof MockBinding2); //higher priority
        assertTrue( factories[7] instanceof MapBinding);
        assertTrue( factories[8] instanceof ParameterBinding);
        assertTrue( factories[9] instanceof MockBinding1); //lower priority
        assertTrue( factories[10] instanceof BeanBinding);
    }

//    @Test
//    public void testNoAccessor(){
//        //should not raise any error and result must be null.
//        final Binding accessor = Bindings.getBinding(Double.class, "test");
//        assertNull(accessor);
//    }

}
