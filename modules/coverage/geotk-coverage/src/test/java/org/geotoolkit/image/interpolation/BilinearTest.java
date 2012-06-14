/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.interpolation;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Bilinear Interpolation test.
 *
 * @author Remi Marechal (Geomatys).
 */
public class BilinearTest extends InterpolationTest {

    public BilinearTest() {
        super();
        interpol = new BilinearInterpolation(pixIterator);
    }

    @Test
    public void lowLCornerTest() {
        double[] resulTest = interpol.interpolate(-0.5, -1);
        assertTrue(resulTest[0] == 0.5);

        resulTest = interpol.interpolate(0, -0.5);
        assertTrue(resulTest[0] == 2.5);

        resulTest = interpol.interpolate(-1, -0.5);
        assertTrue(resulTest[0] == 1.5);

        resulTest = interpol.interpolate(-0.5, 0);
        assertTrue(resulTest[0] == 3.5);

        resulTest = interpol.interpolate(-0.5, -0.5);
        assertTrue(resulTest[0] == 2);
    }

    @Test
    public void lowRCornerTest() {
        double[] resulTest = interpol.interpolate(0.5, -1);
        assertTrue(resulTest[0] == 1.5);

        resulTest = interpol.interpolate(1, -0.5);
        assertTrue(resulTest[0] == 3.5);

        resulTest = interpol.interpolate(0, -0.5);
        assertTrue(resulTest[0] == 2.5);

        resulTest = interpol.interpolate(0.5, -0.5);
        assertTrue(resulTest[0] == 3);
    }

    @Test
    public void uppLCornerTest() {
        double[] resulTest = interpol.interpolate(-0.5, 1);
        assertTrue(resulTest[0] == 6.5);

        resulTest = interpol.interpolate(-1, 0.5);
        assertTrue(resulTest[0] == 4.5);

        resulTest = interpol.interpolate(0, 0.5);
        assertTrue(resulTest[0] == 5.5);

        resulTest = interpol.interpolate(-0.5, 0.5);
        assertTrue(resulTest[0] == 5);
    }

    @Test
    public void uppRCornerTest() {
        double[] resulTest = interpol.interpolate(0.5, 0);
        assertTrue(resulTest[0] == 4.5);

        resulTest = interpol.interpolate(1, 0.5);
        assertTrue(resulTest[0] == 6.5);

        resulTest = interpol.interpolate(0.5, 1);
        assertTrue(resulTest[0] == 7.5);

        resulTest = interpol.interpolate(0.5, 0.5);
        assertTrue(resulTest[0] == 6);
    }
}
