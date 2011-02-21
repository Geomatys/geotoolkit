/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.test;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link Assert} utility class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16 (derived from 3.00)
 */
public final class AssertTest {
    /**
     * Tests the {@link Assert#assertMultilinesEquals} method.
     */
    @Test
    public void testAssertEqualsMultilines() {
        assertMultilinesEquals("Line 1\nLine 2\r\nLine 3\n\rLine 5",
                               "Line 1\rLine 2\nLine 3\n\nLine 5");
    }
}
