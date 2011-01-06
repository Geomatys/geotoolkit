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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test accessors.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AccessorTest {

    @Test
    public void testList(){
        final PropertyAccessorFactory[] factories = Accessors.getAccessorFactories();
        assertNotNull(factories);
        assertEquals(3, factories.length);

        //check correct order, by priority
        assertTrue( factories[0] instanceof MockAccessorFactory2);
        assertTrue( factories[1] instanceof MapAccessorFactory);
        assertTrue( factories[2] instanceof MockAccessorFactory1);
    }

    @Test
    public void testNoAccessor(){
        //should not raise any error and result must be null.
        final PropertyAccessor accessor = Accessors.getAccessor(Double.class, "test", null);
        assertNull(accessor);
    }

}
