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
import java.awt.image.Raster;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void globalTest() {
        raster = renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY());
        int minpx = raster.getMinX();
        int minpy = raster.getMinY();
        int rw    = raster.getWidth();
        int rh    = raster.getHeight();
        double min, max, v00, v01, v10, v11, vInter;
        for (int mpy = minpy; mpy<minpy+rh-1; mpy++) {
            for (int mpx = minpx; mpx<minpx+rw-1; mpx++) {
                v00 = raster.getSampleDouble(mpx, mpy, 0);
                v01 = raster.getSampleDouble(mpx+1, mpy, 0);
                v10 = raster.getSampleDouble(mpx, mpy+1, 0);
                v11 = raster.getSampleDouble(mpx+1, mpy+1, 0);
                min = Math.min(Math.min(v00  , v01), Math.min(v10, v11));
                max = Math.max(Math.max(v00  , v01), Math.max(v10, v11));
                for (double y = mpy; y<mpy+1;y+=0.1) {
                    for (double x = mpx; x<mpx+1;x+=0.1) {
                        vInter = interpol.interpolate(x, y)[0];
                        assertTrue(vInter >= (min - 1E-15) && vInter <= max + 1E-15);
                    }
                }
            }
        }
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
