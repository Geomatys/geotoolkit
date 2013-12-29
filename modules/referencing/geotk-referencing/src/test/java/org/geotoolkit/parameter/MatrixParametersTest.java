/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.parameter;

import java.util.Random;
import java.util.Collections;
import java.awt.geom.AffineTransform;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;

import org.apache.sis.io.wkt.Formatter;
import org.geotoolkit.io.wkt.WKTFormat;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.Symbols;

import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.test.referencing.ParameterTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.opengis.test.Validators.*;


/**
 * Tests the {@link MatrixParameters} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
public final strictfp class MatrixParametersTest extends ParameterTestBase {
    /**
     * Tests the storage of matrix parameters.
     */
    @Test
    public void testEdition() {
        final int size = 8;
        final Random random = new Random(47821365);
        final GeneralMatrix matrix = new GeneralMatrix(size);
        for (int j=0; j<size; j++) {
            for (int i=0; i<size; i++) {
                matrix.setElement(j, i, 200*random.nextDouble()-100);
            }
        }
        final MatrixParameterDescriptors descriptor =
                new MatrixParameterDescriptors(Collections.singletonMap("name", "Test"));
        validate(descriptor);

        for (int height=2; height<=size; height++) {
            for (int width=2; width<=size; width++) {
                MatrixParameters parameters = (MatrixParameters) descriptor.createValue();
                validate((ParameterValueGroup) parameters);

                GeneralMatrix copy = matrix.clone();
                copy.setSize(height, width);
                parameters.setMatrix(copy);
                assertEquals("height", height, parameters.parameter("num_row").intValue());
                assertEquals("width",  width,  parameters.parameter("num_col").intValue());
                assertTrue  ("equals", copy.equals(parameters.getMatrix(), 0));
                assertEquals("equals", parameters, parameters.clone());
            }
        }
    }

    /**
     * Tests WKT formatting of transforms backed by matrix. This is not strictly
     * {@link MatrixParameters} job, but we test it here anyway because it is
     * closely related.
     */
    @Test
    public void testFormatting() {
        final Formatter  formatter = new Formatter(Convention.OGC, Symbols.DEFAULT, null, WKTFormat.SINGLE_LINE);
        final GeneralMatrix matrix = new GeneralMatrix(4);
        matrix.setElement(0,2,  4);
        matrix.setElement(1,0, -2);
        matrix.setElement(2,3,  7);
        MathTransform transform = MathTransforms.linear(matrix);
        assertFalse(transform instanceof AffineTransform);
        formatter.append(transform);
        assertEquals("PARAM_MT[\"Affine\", "          +
                     "PARAMETER[\"num_row\", 4], "    +
                     "PARAMETER[\"num_col\", 4], "    +
                     "PARAMETER[\"elt_0_2\", 4.0], "  +
                     "PARAMETER[\"elt_1_0\", -2.0], " +
                     "PARAMETER[\"elt_2_3\", 7.0]]", formatter.toString());
        matrix.setSize(3,3);
        transform = MathTransforms.linear(matrix);
        assertTrue(transform instanceof AffineTransform);
        formatter.clear();
        formatter.append(transform);
        assertEquals("PARAM_MT[\"Affine\", "          +
                     "PARAMETER[\"num_row\", 3], "    +
                     "PARAMETER[\"num_col\", 3], "    +
                     "PARAMETER[\"elt_0_2\", 4.0], "  +
                     "PARAMETER[\"elt_1_0\", -2.0]]", formatter.toString());
    }
}
