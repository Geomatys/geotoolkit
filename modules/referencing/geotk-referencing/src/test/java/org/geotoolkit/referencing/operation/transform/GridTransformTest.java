/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.util.Arrays;
import java.awt.Dimension;
import java.awt.image.DataBufferDouble;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.referencing.operation.transform.CoordinateDomain;
import org.junit.*;

import static java.lang.Double.NaN;
import static org.apache.sis.test.Assert.*;


/**
 * Tests {@link GridTransform}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class GridTransformTest extends TransformTestBase {
    /**
     * Creates the test suite.
     */
    public GridTransformTest() {
        super(GridTransform.class, null);
        isInverseTransformSupported = false;
    }

    /**
     * Tests the serialization of a small grid. The NaN values
     * are dummy values just for testing array of different length.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testSerialization() throws TransformException {
//      Disabled for now because test is outside domain of validity.
        isDerivativeSupported = false;

        final int width  = 3;
        final int height = 2;
        final double[] x = {
            NaN, NaN, NaN,
            1, 2, 3,
            4, 5, 6,
            NaN
        };
        final double[] y = {
            NaN,
            7,   8,  9,
            10, 11, 12
        };
        final DataBufferDouble buffer = new DataBufferDouble(
                new double[][] {x,y}, width*height, new int[] {3, 1});
        transform = GridTransform.create(GridType.LOCALIZATION, buffer, new Dimension(width, height), null);
        assertInstanceOf("Expected a MathTransform2D.", GridTransform2D.class, transform);
        verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS, 956296895);
        final MathTransform deserialized = assertSerializedEquals(transform);
        final double[][] check = ((DataBufferDouble) ((GridTransform) deserialized).grid).getBankData();
        assertTrue(Arrays.equals(check[0], new double[] {1, 2, 3, 4, 5, 6}));
        assertTrue(Arrays.equals(check[1], new double[] {7, 8, 9, 10, 11, 12}));
    }
}
