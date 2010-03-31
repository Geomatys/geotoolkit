/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.referencing;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.referencing.operation.matrix.MatrixFactory;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MatrixUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
public final class MatrixUtilitiesTest {
    /**
     * Tests {@link MatrixUtilities#invert}
     *
     * @throws NoninvertibleTransformException Should not happen.
     */
    @Test
    public void testInvertNonSquare() throws NoninvertibleTransformException {
        final Matrix matrix = MatrixFactory.create(3, 5, new double[] {
            2, 0, 0, 0, 8,
            0, 0, 4, 0, 5,
            0, 0, 0, 0, 1
        });
        final double[] expected = new double[] {
            0.5, 0,    -4,
            0,   0,     Double.NaN,
            0,   0.25, -1.25,
            0,   0,     Double.NaN,
            0,   0,     1
        };
        final Matrix inverse = MatrixUtilities.invert(matrix);
        assertEquals(5, inverse.getNumRow());
        assertEquals(3, inverse.getNumCol());
        int k = 0;
        for (int j=0; j<5; j++) {
            for (int i=0; i<3; i++) {
                assertEquals(expected[k++], inverse.getElement(j, i), 1E-12);
            }
        }
    }
}
