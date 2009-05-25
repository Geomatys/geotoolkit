/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import org.junit.*;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.provider.NADCON;


/**
 * Tests {@link NadconTransform}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class NadconTransformTest extends TransformTestCase {
    /**
     * Creates a new test suite.
     */
    public NadconTransformTest() {
        super(NadconTransform.class, null);
    }

    /**
     * Loads an ASCII file and compares the content with the binary file.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testASCII() throws FactoryException, TransformException {
        Assume.assumeTrue(NADCON.isAvailable());
        final NadconTransform ascii  = new NadconTransform("nyhpgn.loa", "nyhpgn.laa");
        final NadconTransform binary = new NadconTransform("nyhpgn.los", "nyhpgn.las");
        assertEquals(ascii, binary);
        transform = ascii;
        tolerance = 1E-10;
        stress(CoordinateDomain.GEOGRAPHIC, 426005043);
    }
}
