/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test NeighborInterpolation class.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class NeighborTest extends InterpolationTest {

    private static double TOLERANCE = 1E-9;
    
    public NeighborTest() {
        super();
        super.interpol = new NeighborInterpolation(pixIterator);
    }

    @Test
    public void lowLCornerTest() {
        double resulTest = interpol.interpolate(-1, -1, 0);
        assertEquals("lowLCornerTest : at coordinate : (-1, -1)", 0, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.4, -0.6, 0);
        assertEquals("lowLCornerTest : at coordinate : (0.4, -0.6)", 1, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-0.7, 0.4, 0);
        assertEquals("lowLCornerTest : at coordinate : (-0.7, 0.4)", 3, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.4, 0.2, 0);
        assertEquals("lowLCornerTest : at coordinate : (0.4, 0.2)", 4, resulTest, TOLERANCE);
    }

    @Test
    public void lowRCornerTest() {
        double resulTest = interpol.interpolate(0.7, -0.6, 0);
        assertEquals("lowRCornerTest : at coordinate : (0.7, -0.6)", 1, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.4, -0.6, 0);
        assertEquals("lowRCornerTest : at coordinate : (1.4, -0.6)", 2, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.7, 0.4, 0);
        assertEquals("lowRCornerTest : at coordinate : (1.7, 0.4)", 5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.4, 0.2, 0);
        assertEquals("lowRCornerTest : at coordinate : (0.4, 0.2)", 4, resulTest, TOLERANCE);
    }

    @Test
    public void uppLCornerTest() {
        double resulTest = interpol.interpolate(-0.2, 1.6, 0);
        assertEquals("uppLCornerTest : at coordinate : (-0.2, 1.6)", 6, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.4, 1.6, 0);
        assertEquals("uppLCornerTest : at coordinate : (0.4, 1.6)", 7, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-0.7, 0.4, 0);
        assertEquals("uppLCornerTest : at coordinate : (-0.7, 0.4)", 3, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.1, 0.2, 0);
        assertEquals("uppLCornerTest : at coordinate : (0.1, 0.2)", 4, resulTest, TOLERANCE);
    }

    @Test
    public void uppRCornerTest() {
        double resulTest = interpol.interpolate(1.7, 0.6, 0);
        assertEquals("uppRCornerTest : at coordinate : (1.7, 0.6)", 5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.4, 1.6, 0);
        assertEquals("uppRCornerTest : at coordinate : (1.4, 1.6)", 8, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.7, 1.4, 0);
        assertEquals("uppRCornerTest : at coordinate : (0.7, 1.4)", 7, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.4, 0.2, 0);
        assertEquals("uppRCornerTest : at coordinate : (0.4, 0.2)", 4, resulTest, TOLERANCE);
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
