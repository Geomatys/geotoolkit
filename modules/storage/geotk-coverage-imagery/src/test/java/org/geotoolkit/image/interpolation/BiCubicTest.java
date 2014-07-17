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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.RasterFactory;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>BiCubic Interpolation test.<br/>
 *
 * Test 2 made of biCubic interpolation.<br/>
 *
 * All interpolation values are compared to JAI interpolation results.</p>
 *
 * @author Remi Marechal (Geomatys).
 */
public class BiCubicTest extends InterpolationTest {

    /**
     * Raster attributes.
     */
    private int miny, minx, width, height;

    /**
     * Raster use for biCubic test.
     */
    private WritableRaster rastertest;

    public BiCubicTest() {
        miny   = -1;
        minx   = -2;
        width  = 4;
        height = 4;
    }

    /**
     * <p>Test interpolate method from biCubic class.<br/><br/>
     *
     * Verify that interpolation at Integer pixel position equal pixel position value.<br/>
     */
    @Test
    public void globalTest() {
        double val = -55;
        width  = 8;
        height = 8;
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        for (int y = miny; y < miny + height; y++) {
            for (int x = minx; x < minx + width; x++) {
                rastertest.setSample(x, y, 0, val++);
            }
        }
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation1(pixIterator);
        double interpolVal;
        for (int y = miny+1; y < miny + height-2; y++) {
            for (int x = minx+1; x < minx + width-2; x++) {
                //-- interpolation verification at center pixel position.
                interpolVal = interpol.interpolate(x, y,0);
                assertTrue(Math.abs(rastertest.getSampleDouble(x, y, 0) - interpolVal) <= 1E-12);
                interpolVal = interpol.interpolate(x, y)[0];
                assertTrue(Math.abs(rastertest.getSampleDouble(x, y, 0) - interpolVal) <= 1E-12);
            }
        }
        interpol = new BiCubicInterpolation2(pixIterator);
        for (int y = miny+1; y < miny + height-2; y++) {
            for (int x = minx+1; x < minx + width-2; x++) {
                //-- interpolation verification at center pixel position.
                interpolVal = interpol.interpolate(x, y, 0);
                assertTrue(Math.abs(rastertest.getSampleDouble(x, y, 0) - interpolVal) <= 1E-12);
                interpolVal = interpol.interpolate(x, y)[0];
                assertTrue(Math.abs(rastertest.getSampleDouble(x, y, 0) - interpolVal) <= 1E-12);
            }
        }
    }

    /**
     * Compare interpolation values with JAI library biCubic interpolation results.
     */
    @Test
    public void minAndMaxTest() throws InterruptedException {
        width  = 4;
        height = 4;
        //fill first band
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 3, new Point(minx, miny));
        rastertest.setSample(-2, -1, 0, 1);
        rastertest.setSample(-1, -1, 0, 1);
        rastertest.setSample( 0, -1, 0, 1);
        rastertest.setSample( 1, -1, 0, 1);
        rastertest.setSample(-2, 0, 0, 1);
        rastertest.setSample(-1, 0, 0, 2);
        rastertest.setSample(-0, 0, 0, 2);
        rastertest.setSample( 1, 0, 0, 1);
        rastertest.setSample(-2, 1, 0, 1);
        rastertest.setSample(-1, 1, 0, 2);
        rastertest.setSample( 0, 1, 0, 2);
        rastertest.setSample( 1, 1, 0, 1);
        rastertest.setSample(-2, 2, 0, 1);
        rastertest.setSample(-1, 2, 0, 1);
        rastertest.setSample( 0, 2, 0, 1);
        rastertest.setSample( 1, 2, 0, 1);

        //fill second band
        rastertest.setSample(-2, -1, 1, 2);
        rastertest.setSample(-1, -1, 1, 2);
        rastertest.setSample( 0, -1, 1, 2);
        rastertest.setSample( 1, -1, 1, 2);
        rastertest.setSample(-2, 0, 1, 2);
        rastertest.setSample(-1, 0, 1, 1);
        rastertest.setSample(-0, 0, 1, 1);
        rastertest.setSample( 1, 0, 1, 2);
        rastertest.setSample(-2, 1, 1, 2);
        rastertest.setSample(-1, 1, 1, 1);
        rastertest.setSample( 0, 1, 1, 1);
        rastertest.setSample( 1, 1, 1, 2);
        rastertest.setSample(-2, 2, 1, 2);
        rastertest.setSample(-1, 2, 1, 2);
        rastertest.setSample( 0, 2, 1, 2);
        rastertest.setSample( 1, 2, 1, 2);

        // fill third band
        double val = 32;
        for (int y = miny; y<miny+height; y++) {
            for (int x = minx; x<minx+width; x++) {
                rastertest.setSample(x, y, 2, val++);
            }
        }

        //test about classic bicubic interpolation
        checkBicubicInterpolation(rastertest, false);

        //test about other made bicubic interpolation
        checkBicubicInterpolation(rastertest, true);
    }

    /**
     * test get min max values on raster corner.
     */
    @Test
    public void testFail() {
        rastertest  = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol    = new BiCubicInterpolation1(pixIterator);
        //lower corner
        try {
            interpol.getMinMaxValue(new Rectangle(-2, -3, 3, 3));
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //lower right corner
        try {
            interpol.getMinMaxValue(new Rectangle(2, -3, 3, 3));
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //upper left corner
        try {
            interpol.getMinMaxValue(new Rectangle(-3, 3, 3, 3));
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //upper corner
        try {
            interpol.getMinMaxValue(new Rectangle(1, 2, 3, 3));
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }
    }

    /**
     * Compare biCubic interpolation results from Jai library {@link javax.media.jai.Interpolation}
     * and from biCubic interpolation {@link BiCubicInterpolation}.
     *
     * @param raster tested raster.
     * @param keys define type of biCubic interpolation.
     */
    private void checkBicubicInterpolation(Raster raster, boolean keys) {
        final int numBand = raster.getNumBands();
        final int minX    = raster.getMinX();
        final int minY    = raster.getMinY();
        final int rW      = raster.getWidth();
        final int rH      = raster.getHeight();
        double[] jaiInter;

        javax.media.jai.Interpolation jaiInterpol = (keys) ? new InterpolationBicubic2(8) : new InterpolationBicubic(8);
        PixelIterator pixelIterator = PixelIteratorFactory.createDefaultIterator(raster);
        interpol = (keys) ? new BiCubicInterpolation2(pixelIterator) : new BiCubicInterpolation1(pixelIterator);

        double x, y, tolerance;
        for(int b = 0; b < numBand; b++) {
            for (int ny = 0; ny < 100; ny++) {
                for (int nx = 0; nx < 100; nx++) {
                    x = minX + 1 + nx * 0.01;
                    y = minY + 1 + ny * 0.01;
                    
                    jaiInter = getJAIInterpolate(jaiInterpol, raster, x, y, rW, rH, numBand);
                    for (int b2 = 0; b2 <numBand; b2++) {
                        //-- to simulate pixel center
                        double inter = interpol.interpolate(x, y, b2);
                        tolerance = ((inter + jaiInter[b2]) / 2) * 1E-2;//1%
                        assertEquals("checkBicubicInterpolation at position : ("+x+", "+y+", "+b2+") : ", jaiInter[b2], inter, tolerance);
                    }
                }
            }
        }
    }

    /**
     * Find interpolation value at x, y coordinate for each raster band.
     *
     * @param jaiInterpol interpolation type from jai library.
     * @param raster raster which contain data.
     * @param x interpolation X coordinate.
     * @param y interpolation Y coordinate.
     * @param rasterWidth raster width.
     * @param rasterHeight raster height.
     * @param rasterNumBand raster number bands.
     * @return double table which contain interpolation value at x, y coordinate for each raster band.
     */
    private double[] getJAIInterpolate(javax.media.jai.Interpolation jaiInterpol, Raster raster, double x, double y, int rasterWidth, int rasterHeight, int rasterNumBand){

        int mx = (int) x;
        int my = (int) y;
        if (x < mx) mx--;
        if (y < my) my--;
        
        //-- ajust area interpolation on x, y center.
        mx -= rasterWidth  / 2 - 1;
        my -= rasterHeight / 2 - 1;
        
        float ix = (float) (x-mx-1);
        float iy = (float) (y-my-1);
        final double[] jaiResult = new double[rasterNumBand];
        final double[][] interpolSample = new double[4][4];
        for (int b = 0; b<rasterNumBand; b++) {
            for (int idy = my; idy < my + 4; idy++) {
                for (int idx = mx; idx < mx + 4; idx++) {
                    interpolSample[idy - my][idx - mx] = raster.getSampleDouble(idx, idy, b);
                }
            }
            jaiResult[b] = jaiInterpol.interpolate(interpolSample, (float) ix, (float) iy);
        }
        return jaiResult;
    }
}
