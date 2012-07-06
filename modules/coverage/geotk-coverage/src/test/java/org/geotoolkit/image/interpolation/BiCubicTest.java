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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import javax.media.jai.RasterFactory;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * BiCubic Interpolation test.
 *
 * @author Remi Marechal (Geomatys).
 */
public class BiCubicTest extends InterpolationTest {

    private int miny, minx, width, height;
    private WritableRaster rastertest;

    public BiCubicTest() {
        miny = -1;
        minx = -2;
        width = 4;
        height = 4;

    }

    /**
     * <p>Test interpolate method from biCubic class.<br/><br/>
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
        double val = -55;
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        for (int y = miny; y < miny + height; y++) {
            for (int x = minx; x < minx + width; x++) {
                rastertest.setSample(x, y, 0, val++);
            }
        }
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
        pixIterator.moveTo(0, -1);
        final BiCubicInterpolation biCInterpol = (BiCubicInterpolation)interpol;
        final double[] minAndMax = biCInterpol.getMinMaxValue(null);
        double interpolXDeb, interpolXEnd, interpolYDeb, interpolYEnd, interPol;
        for (int y = miny; y < miny + height-1; y++) {
            for (int x = minx; x < minx + width-1; x++) {
                // interpolation verification at integer pixel position.
                interpolXDeb = interpol.interpolate(x, y)[0];
                assertTrue(Math.abs(rastertest.getSampleDouble(x, y, 0) - interpolXDeb) <= 1E-12);
                interpolXEnd = interpol.interpolate(x+1, y)[0];
                assertTrue(Math.abs(rastertest.getSampleDouble(x+1, y, 0) - interpolXEnd) <= 1E-12);
                interpolYDeb = interpol.interpolate(x, y+1)[0];
                assertTrue(Math.abs(rastertest.getSampleDouble(x, y+1, 0) - interpolYDeb) <= 1E-12);
                interpolYEnd = interpol.interpolate(x+1, y+1)[0];
                assertTrue(Math.abs(rastertest.getSampleDouble(x+1, y+1, 0) - interpolYEnd) <= 1E-12);
                // verify each positions within x->x+1 and y->y+1 with step equals 0.1
                // are always between minimum and maximum interpolation value.
                for (double y2 = y + 0.01; y2 < y + 1; y2 += 0.01) {
                    for (double x2 = x + 0.01; x2 < x + 1; x2 += 0.01) {
                        interPol = interpol.interpolate(x2, y2)[0];
                        assertTrue(interPol >= minAndMax[0]);
                        assertTrue(interPol <= minAndMax[3]);
                    }
                }
            }
        }
    }

    /**
     * <p>Test find min and max values.<br/>
     * First time verify that expected max value is at center of raster.<br/>
     * Then verify that expected min value is at center of raster.<br/>
     * Also test conformity with mean value theorem.</p>
     */
    @Test
    public void minAndMaxTest() {
        int idBand = 0;
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        rastertest.setSample(-2, -1, idBand, 1);
        rastertest.setSample(-1, -1, idBand, 1);
        rastertest.setSample( 0, -1, idBand, 1);
        rastertest.setSample( 1, -1, idBand, 1);
        rastertest.setSample(-2, 0, idBand, 1);
        rastertest.setSample(-1, 0, idBand, 2);
        rastertest.setSample(-0, 0, idBand, 2);
        rastertest.setSample( 1, 0, idBand, 1);
        rastertest.setSample(-2, 1, idBand, 1);
        rastertest.setSample(-1, 1, idBand, 2);
        rastertest.setSample( 0, 1, idBand, 2);
        rastertest.setSample( 1, 1, idBand, 1);
        rastertest.setSample(-2, 2, idBand, 1);
        rastertest.setSample(-1, 2, idBand, 1);
        rastertest.setSample( 0, 2, idBand, 1);
        rastertest.setSample( 1, 2, idBand, 1);
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
        double[] minMax = interpol.getMinMaxValue(null);
        assertTrue(Math.abs(minMax[4] + 0.5) <= 1E-9);//x = -0.5 expected value
        assertTrue(Math.abs(minMax[5] - 0.5) <= 1E-9);//y =  0.5 expected value
        assertTrue(tAF(minMax[4], minMax[5], 1E-5, idBand));

        rastertest.setSample(-2, -1, idBand, 2);
        rastertest.setSample(-1, -1, idBand, 2);
        rastertest.setSample( 0, -1, idBand, 2);
        rastertest.setSample( 1, -1, idBand, 2);
        rastertest.setSample(-2, 0, idBand, 2);
        rastertest.setSample(-1, 0, idBand, 1);
        rastertest.setSample(-0, 0, idBand, 1);
        rastertest.setSample( 1, 0, idBand, 2);
        rastertest.setSample(-2, 1, idBand, 2);
        rastertest.setSample(-1, 1, idBand, 1);
        rastertest.setSample( 0, 1, idBand, 1);
        rastertest.setSample( 1, 1, idBand, 2);
        rastertest.setSample(-2, 2, idBand, 2);
        rastertest.setSample(-1, 2, idBand, 2);
        rastertest.setSample( 0, 2, idBand, 2);
        rastertest.setSample( 1, 2, idBand, 2);
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
        minMax = interpol.getMinMaxValue(null);
        assertTrue(Math.abs(minMax[1] + 0.5) <= 1E-9);//x = -0.5 expected value
        assertTrue(Math.abs(minMax[2] - 0.5) <= 1E-9);//y =  0.5 expected value
        assertTrue(tAF(minMax[1], minMax[2], 1E-5, idBand));
    }

    @Test
    public void RandomValueMultiBandsTest(){
        int numband = 2;
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, numband, new Point(minx, miny));
        for (int band = 0; band<numband; band++) {
            for (int y = miny; y<miny+4; y++) {
                for (int x = minx; x<minx+4; x++) {
                    rastertest.setSample(x, y, band, Math.random()*10);
                }
            }
        }
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
        double[] minMax = interpol.getMinMaxValue(null);
        double interPol;
        assertTrue(minMax.length == 12);
        //verify validity from derivative.
        for (int band = 0; band<numband; band++) {
            //test minimum
//            if (!tAF(minMax[6*band+1], minMax[6*band+2], 1E-5, band)) {
//                System.out.println("");
//            }
//            assertTrue(tAF(minMax[6*band+1], minMax[6*band+2], 1E-5, band));
//            //test maximum
//            assertTrue(tAF(minMax[6*band+4], minMax[6*band+5], 1E-5, band));
            // verify each positions within x->x+1 and y->y+1 with step equals 0.1
            // are always between minimum and maximum interpolation value.
            for (double y = miny + 0.01; y <= miny+3; y += 0.01) {
                for (double x = minx + 0.01; x <= minx+3; x += 0.01) {
                    interPol = interpol.interpolate(x, y)[band];
                    assertTrue(interPol >= minMax[6*band]);
                    if (!(interPol<= minMax[6*band+3])) {
                        System.out.println("");
                    }
                    assertTrue(interPol <= minMax[6*band+3]);
                }
            }
        }
    }

    /**
     * test get min max values on raster corner.
     */
    @Test
    public void testFail() {
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
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
     * Test getCubicRoot method and verify validity from results by mean value theorem.
     */
    @Test
    public void testCubicRoots() {
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
        double t0 = -1.5;
        double[] f = new double[]{1,2,2,1};
        double eps = 1E-5;
        double[] roots = ((BiCubicInterpolation)interpol).getCubicRoots(t0, t0, t0+4, f);
        assertTrue(roots.length == 1);
        if (roots != null) {
            for (int i = 0; i<roots.length; i++) {
                assertTrue(tAF(t0, f, roots[i], eps));
            }
        }
        t0 = 10.3;
        f = new double[]{2,-2,4,-1};
        roots = ((BiCubicInterpolation)interpol).getCubicRoots(t0, t0, t0+4, f);
        assertTrue(roots.length == 2);
        if (roots != null) {
            for (int i = 0; i<roots.length; i++) {
                assertTrue(tAF(t0, f, roots[i], eps));
            }
        }
        t0 = -100.3;
        f = new double[]{55,20,20,55};
        roots = ((BiCubicInterpolation)interpol).getCubicRoots(t0, t0, t0+4, f);
        assertTrue(roots.length == 1);
        if (roots != null) {
            for (int i = 0; i<roots.length; i++) {
                assertTrue(tAF(t0, f, roots[i], eps));
            }
        }
    }

    /**
     * Verify root value conformity with mean value theorem.
     *
     * @param t0
     * @param root found root.
     * @param f
     * @param epsilon
     * @return true if root value is verified else false.
     */
    private boolean tAF(double t0, double[] f, double root, double epsilon) {
        assert (epsilon>0) : "epsilon will be able to be define positively";
        final BiCubicInterpolation bInterpol = (BiCubicInterpolation) interpol;
        final double fx0 = bInterpol.getCubicValue(t0, root-epsilon, f);
        final double fx1 = bInterpol.getCubicValue(t0, root+epsilon, f);
        final double taf = Math.abs((fx1-fx0)/epsilon/2.0);
        return taf <= 1E-9;
    }

    /**
     * Control validity of extremum coordinates in X and Y direction.
     *
     * @param rootX X extremum coordinate.
     * @param rootY Y extremum coordinate.
     * @param epsilon
     * @param indexBand band number.
     * @return true if roots coordinate values are conform else false.
     */
    private boolean tAF(double rootX, double rootY, double epsilon, int indexBand) {
        assert (epsilon>0) : "epsilon will be able to be define positively";
        final BiCubicInterpolation bInterpol = (BiCubicInterpolation) interpol;
        double[] pt0 = bInterpol.interpolate(rootX - epsilon, rootY);
        double[] pt1 = bInterpol.interpolate(rootX + epsilon, rootY);
        final double tafX = Math.abs((pt1[indexBand] - pt0[indexBand])/epsilon/2);
        pt0 = bInterpol.interpolate(rootX, rootY - epsilon);
        pt1 = bInterpol.interpolate(rootX, rootY + epsilon);
        final double tafY = Math.abs((pt1[indexBand] - pt0[indexBand])/epsilon/2);
        return (tafX <= 1E-9 && tafY <= 1E-9);
    }

}
