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
import java.awt.image.Raster;
import javax.media.jai.InterpolationBilinear;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Bilinear Interpolation test.
 *
 * @author Remi Marechal (Geomatys).
 */
public class BilinearTest extends InterpolationTest {

    Raster raster;

    public BilinearTest() {
        super();
        interpol = new BilinearInterpolation(pixIterator);
    }

    /**
     * <p>Test interpolate method from biLinear class.<br/><br/>
     *
     * Verify that interpolation at Integer pixel position equal pixel position.<br/>
     * Verify that none-integer pixels position interpolation is between minimum and maximum interpolation values.<br/><br/>
     *
     * To find minimum and maximum values :<br/>
     * - Compute pixels interpolation at nearest integer pixel position and get maximum and minimum values.<br/>
     * - Find interpolation roots, get roots interpolation values if its possible,<br/>
     * and get maximum and minimum values from previous maximum and minimum.</p>
     */
    @Test
    public void globalTest() {
        InterpolationBilinear jaiInterpol =  new InterpolationBilinear(64);
        raster = renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY());
        int minpx = raster.getMinX();
        int minpy = raster.getMinY();
        int rw    = raster.getWidth();
        int rh    = raster.getHeight();
        double min, max, v00, v01, v10, v11, vInter, jaiInter;
        double[] geotkInter;
        for (int mpy = minpy; mpy < minpy + rh - 1; mpy++) {
            for (int mpx = minpx; mpx < minpx +rw - 1; mpx++) {
                v00 = raster.getSampleDouble(mpx, mpy, 0);
                v01 = raster.getSampleDouble(mpx+1, mpy, 0);
                v10 = raster.getSampleDouble(mpx, mpy+1, 0);
                v11 = raster.getSampleDouble(mpx+1, mpy+1, 0);
                min = Math.min(Math.min(v00  , v01), Math.min(v10, v11));
                max = Math.max(Math.max(v00  , v01), Math.max(v10, v11));
                for (double y = mpy + 0.5; y < mpy + 1.5; y += 0.1) {
                    for (double x = mpx + 0.5; x < mpx + 1.5; x += 0.1) {
                        //-- shift by 0.5 to simulate pixel center --//
                        jaiInter = jaiInterpol.interpolate(v00, v01, v10, v11, (float)(x - mpx - 0.5), (float)(y - mpy - 0.5));

                        // Interpolation per sample
                        vInter = interpol.interpolate(x, y, 0);
                        assertTrue("Interpolate value : "+vInter+" should be smaller than maximum : "+max, vInter <= max + 1E-15);
                        assertTrue("Interpolate value : "+vInter+" should be greater than minimum : "+min, vInter >= (min - 1E-15));
                        assertEquals("Bilinear global test at ("+x+", "+y+") position : ", vInter, jaiInter, 1E-7);

                        // Interpolation per pixel
                        geotkInter = interpol.interpolate(x, y);
                        assertTrue("Interpolate value : "+geotkInter[0]+" should be smaller than maximum : "+max, geotkInter[0] <= max + 1E-15);
                        assertTrue("Interpolate value : "+geotkInter[0]+" should be greater than minimum : "+min, geotkInter[0] >= (min - 1E-15));
                        assertEquals("Bilinear global test at ("+x+", "+y+") position : ", geotkInter[0], jaiInter, 1E-7);
                    }
                }
            }
        }
    }

    /**
     * Test 4 interpolation results near raster lower corner.
     * X direction coordinate min.
     * Y direction coordinate min.
     */
    @Test
    public void lowLCornerTest() {
        // Interpolation per sample
        double resulTest = interpol.interpolate(-0.5, -1, 0);
        assertEquals("lowLCornerTest : at coordinate : (-0.5, -1)", -1.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0, -0.5, 0);
        assertEquals("lowLCornerTest : at coordinate : (0, -0.5)", 0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-1, -0.5, 0);
        assertEquals("lowLCornerTest : at coordinate : (-1, -0.5)", -0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-0.5, 0, 0);
        assertEquals("lowLCornerTest : at coordinate : (-0.5, 0)", 1.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-0.5, -0.5, 0);
        assertEquals("lowLCornerTest : at coordinate : (-0.5, -0.5)", 0, resulTest, TOLERANCE);
        
        // Interpolation per pixel
        resulTest = interpol.interpolate(-0.5, -1)[0];
        assertEquals("lowLCornerTest : at coordinate : (-0.5, -1)", -1.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0, -0.5)[0];
        assertEquals("lowLCornerTest : at coordinate : (0, -0.5)", 0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-1, -0.5)[0];
        assertEquals("lowLCornerTest : at coordinate : (-1, -0.5)", -0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-0.5, 0)[0];
        assertEquals("lowLCornerTest : at coordinate : (-0.5)[0]", 1.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-0.5, -0.5)[0];
        assertEquals("lowLCornerTest : at coordinate : (-0.5, -0.5)", 0, resulTest, TOLERANCE);
    }

    /**
     * Test 4 interpolation results near raster lower right corner.
     * X direction coordinate max.
     * Y direction coordinate min.
     */
    @Test
    public void lowRCornerTest() {
        // Interpolation per sample
        double resulTest = interpol.interpolate(0.5, -1, 0);
        assertEquals("lowRCornerTest : at coordinate : (0.5, -1)", -0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.5, -1, 0);
        assertEquals("lowRCornerTest : at coordinate : (1.5, -1)", 0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1, 0.5, 0);
        assertEquals("lowRCornerTest : at coordinate : (1, 0.5)", 4.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.5, -0.5, 0);
        assertEquals("lowRCornerTest : at coordinate : (0.5, -0.5)", 1, resulTest, TOLERANCE);
        
        // Interpolation per pixel
        resulTest = interpol.interpolate(0.5, -1)[0];
        assertEquals("lowRCornerTest : at coordinate : (0.5, -1)", -0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.5, -1)[0];
        assertEquals("lowRCornerTest : at coordinate : (1.5, -1)", 0.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1, 0.5)[0];
        assertEquals("lowRCornerTest : at coordinate : (1, 0.5)", 4.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.5, -0.5)[0];
        assertEquals("lowRCornerTest : at coordinate : (0.5, -0.5)", 1, resulTest, TOLERANCE);
    }

    /**
     * Test 4 interpolation results near raster upper left corner.
     * X direction coordinate min.
     * Y direction coordinate max.
     */
    @Test
    public void uppLCornerTest() {
        // Interpolation per sample
        double resulTest = interpol.interpolate(-0.5, 1, 0);
        assertEquals("uppLCornerTest : at coordinate : (-0.5, 1)", 4.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-1, 0.5, 0);
        assertEquals("uppLCornerTest : at coordinate : (-1, 0.5)", 2.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.5, 1, 0);
        assertEquals("uppLCornerTest : at coordinate : (0.5, 1)", 5.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-1, 1.5, 0);
        assertEquals("uppLCornerTest : at coordinate : (-1, 1.5)", 5.5, resulTest, TOLERANCE);
        
        // Interpolation per pixel
        resulTest = interpol.interpolate(-0.5, 1)[0];
        assertEquals("uppLCornerTest : at coordinate : (-0.5, 1)", 4.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-1, 0.5)[0];
        assertEquals("uppLCornerTest : at coordinate : (-1, 0.5)", 2.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(0.5, 1)[0];
        assertEquals("uppLCornerTest : at coordinate : (0.5, 1)", 5.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(-1, 1.5)[0];
        assertEquals("uppLCornerTest : at coordinate : (-1, 1.5)", 5.5, resulTest, TOLERANCE);
    }

    /**
     * Test 4 interpolation results near raster upper corner.
     * X direction coordinate max.
     * Y direction coordinate max.
     */
    @Test
    public void uppRCornerTest() {
        // Interpolation per sample
        double resulTest = interpol.interpolate(0.5, 0, 0);
        assertEquals("uppRCornerTest : at coordinate : (0.5, 0)", 2.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1, 0.5, 0);
        assertEquals("uppRCornerTest : at coordinate : (1, 0.5)", 4.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.5, 1, 0);
        assertEquals("uppRCornerTest : at coordinate : (1.5, 1)", 6.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1, 1.5, 0);
        assertEquals("uppRCornerTest : at coordinate : (1, 1.5)", 7.5, resulTest, TOLERANCE);
        
        // Interpolation per pixel
        resulTest = interpol.interpolate(0.5, 0)[0];
        assertEquals("uppRCornerTest : at coordinate : (0.5, 0)", 2.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1, 0.5)[0];
        assertEquals("uppRCornerTest : at coordinate : (1, 0.5)", 4.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1.5, 1)[0];
        assertEquals("uppRCornerTest : at coordinate : (1.5, 1)", 6.5, resulTest, TOLERANCE);

        resulTest = interpol.interpolate(1, 1.5)[0];
        assertEquals("uppRCornerTest : at coordinate : (1, 1.5)", 7.5, resulTest, TOLERANCE);
    }

    /**
     * Test about interpolation coordinate out of iterate object boundary.
     */
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
