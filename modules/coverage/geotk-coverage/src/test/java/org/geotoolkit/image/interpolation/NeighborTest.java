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

import java.awt.Rectangle;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test NeighborInterpolation class.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class NeighborTest extends InterpolationTest {

    public NeighborTest() {
        super();
        super.interpol = new NeighborInterpolation(pixIterator);
    }

    @Test
    public void lowLCornerTest() {
        double[] resulTest = interpol.interpolate(-0.7, -0.6);
        assertTrue(resulTest[0] == 0);

        resulTest = interpol.interpolate(-0.4, -0.6);
        assertTrue(resulTest[0] == 1);

        resulTest = interpol.interpolate(-0.7, -0.4);
        assertTrue(resulTest[0] == 3);

        resulTest = interpol.interpolate(-0.4, -0.2);
        assertTrue(resulTest[0] == 4);
    }

    @Test
    public void lowRCornerTest() {
        double[] resulTest = interpol.interpolate(0.7, -0.6);
        assertTrue(resulTest[0] == 2);

        resulTest = interpol.interpolate(0.4, -0.6);
        assertTrue(resulTest[0] == 1);

        resulTest = interpol.interpolate(0.7, -0.4);
        assertTrue(resulTest[0] == 5);

        resulTest = interpol.interpolate(0.4, -0.2);
        assertTrue(resulTest[0] == 4);
    }

    @Test
    public void uppLCornerTest() {
        double[] resulTest = interpol.interpolate(-0.7, 0.6);
        assertTrue(resulTest[0] == 6);

        resulTest = interpol.interpolate(-0.4, 0.6);
        assertTrue(resulTest[0] == 7);

        resulTest = interpol.interpolate(-0.7, 0.4);
        assertTrue(resulTest[0] == 3);

        resulTest = interpol.interpolate(-0.4, 0.2);
        assertTrue(resulTest[0] == 4);
    }

    @Test
    public void uppRCornerTest() {
        double[] resulTest = interpol.interpolate(0.7, 0.6);
        assertTrue(resulTest[0] == 8);

        resulTest = interpol.interpolate(0.4, 0.6);
        assertTrue(resulTest[0] == 7);

        resulTest = interpol.interpolate(0.7, 0.4);
        assertTrue(resulTest[0] == 5);

        resulTest = interpol.interpolate(0.4, 0.2);
        assertTrue(resulTest[0] == 4);
    }

    @Test
    public void minMaxTest() {
        double[] minMax = interpol.getMinMaxValue(null);
        assertTrue(minMax[0] == 0);
        assertTrue(minMax[1] == -1);//x coordinate
        assertTrue(minMax[2] == -1);//y coordinate
        assertTrue(minMax[3] == 8);
        assertTrue(minMax[4] == 1);//x coordinate
        assertTrue(minMax[5] == 1);//y coordinate
        assertTrue(minMax == interpol.getMinMaxValue(null));
        try {
            interpol.getMinMaxValue(new Rectangle(-2, -1, 3, 3));
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }
    }
}
