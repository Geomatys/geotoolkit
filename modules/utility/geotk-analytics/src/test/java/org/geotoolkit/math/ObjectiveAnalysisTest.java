/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.math;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.Random;

import org.opengis.metadata.spatial.PixelOrientation;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link ObjectiveAnalysis} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from 1.0)
 */
public final strictfp class ObjectiveAnalysisTest {
    /**
     * Tolerance factor for comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * Tests the Objective Analysis computation on integer values.
     * The computed results are compared with the original values.
     */
    @Test
    public void testIntegers() {
        final int      s = 10;
        final double[] x = new double[s];
        final double[] y = new double[s];
        final double[] z = new double[s];
        final Random r = new Random(380951990);
        for (int i=0; i<z.length; i++) {
            x[i] = r.nextInt(s);
            y[i] = r.nextInt(s);
            z[i] = x[i] * y[i];
        }
        final ObjectiveAnalysis ob = new ObjectiveAnalysis(
                new Rectangle(s-1, s-1), s, s, PixelOrientation.UPPER_LEFT);
        ob.setInputs(x, y, z);
        final double[] computed = ob.interpolate((double[]) null);
        assertEquals(s*s, computed.length);
        for (int i=0; i<z.length; i++) {
            final double index = x[i] + x.length * (s-1 - y[i]);
            assertEquals(z[i], computed[(int) index], EPS);
        }
        /*
         * Tests image creation. For now we merely check that no exception is thrown.
         */
        final RenderedImage image = ob.createImage();
        assertNotNull(image);
    }
}
