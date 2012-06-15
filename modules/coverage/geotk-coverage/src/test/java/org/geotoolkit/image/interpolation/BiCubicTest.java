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
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import javax.media.jai.RasterFactory;
import static org.junit.Assert.assertTrue;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
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
        double val = -55;
        rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 1, new Point(minx, miny));
        for (int y = miny; y < miny + height; y++) {
            for (int x = minx; x < minx + width; x++) {
                rastertest.setSample(x, y, 0, val++);
            }
        }
        pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
        interpol = new BiCubicInterpolation(pixIterator);
    }

    @Test
    public void globalTest() {
        pixIterator.moveTo(0, -1);
        final BiCubicInterpolation biCInterpol = (BiCubicInterpolation)interpol;
        double[] lowRowRoots, uppRowRoots, lowColRacine, uppColRacine;
        double interpolXDeb, interpolXEnd, interpolYDeb, interpolYEnd, interMin, interMax, tRoots, interPol;
        final double[] lowRowValues = new double[4];
        final double[] uppRowValues = new double[4];
        final double[] lowColValues = new double[4];
        final double[] uppColValues = new double[4];
        for (int y = miny; y < miny + height-1; y++) {
            int index = 0;
            for (int x = minx; x<minx+width; x++) {
                lowRowValues[index++] = rastertest.getSampleDouble(x, y, 0);
            }
            index = 0;
            for (int x = minx; x<minx+width; x++) {
                uppRowValues[index++] = rastertest.getSampleDouble(x, y+1, 0);
            }
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

                // get minimum and maximum interpolation value.
                interMin = Math.min(Math.min(interpolXDeb, interpolXEnd), Math.min(interpolYDeb, interpolYEnd))-1E-12;
                interMax = Math.max(Math.max(interpolXDeb, interpolXEnd), Math.max(interpolYDeb, interpolYEnd))+1E-12;

                // get roots within appropriate definition domain.
                lowRowRoots = biCInterpol.getCubicRoots(minx, x, x+1,lowRowValues);
                uppRowRoots = biCInterpol.getCubicRoots(minx, x, x+1,uppRowValues);
                index = 0;
                for (int yt = miny; yt < miny+height; yt++) {
                    lowColValues[index++] = rastertest.getSampleDouble(x, yt, 0);
                }
                index = 0;
                for (int yt = miny; yt < miny+height; yt++) {
                    uppColValues[index++] = rastertest.getSampleDouble(x + 1, yt, 0);
                }
                lowColRacine = biCInterpol.getCubicRoots(miny, y, y+1,lowColValues);
                uppColRacine = biCInterpol.getCubicRoots(miny, y, y+1,uppColValues);
                
                // get minimum and maximum interpolation value at roots instant.
                if (lowRowRoots != null) {
                    for (double t : lowRowRoots) {
                        tRoots = biCInterpol.getCubicValue(minx, t, lowRowValues);
                        interMin = Math.min(interMin, tRoots);
                        interMax = Math.max(interMax, tRoots);
                    }
                }
                if (uppRowRoots != null) {
                    for (double t : uppRowRoots) {
                        tRoots = biCInterpol.getCubicValue(minx, t, uppRowValues);
                        interMin = Math.min(interMin, tRoots);
                        interMax = Math.max(interMax, tRoots);
                    }
                }
                if (lowColRacine != null) {
                    for (double t : lowColRacine) {
                        tRoots = biCInterpol.getCubicValue(miny, t, lowColValues);
                        interMin = Math.min(interMin, tRoots);
                        interMax = Math.max(interMax, tRoots);
                    }
                }
                if (uppColRacine != null) {
                    for (double t : uppColRacine) {
                        tRoots = biCInterpol.getCubicValue(miny, t, uppColValues);
                        interMin = Math.min(interMin, tRoots);
                        interMax = Math.max(interMax, tRoots);
                    }
                }
                // verify each positions within x->x+1 and y->y+1 with step equals 0.1
                // are always between minimum and maximum interpolation value.
                for (double y2 = y + 0.1; y2 < y + 1; y2 += 0.1) {
                    for (double x2 = x+0.1; x2 < x + 1; x2 += 0.1) {
                        interPol = interpol.interpolate(x2, y2)[0];
                        assertTrue(interPol >= interMin);
                        assertTrue(interPol <= interMax);
                    }
                }
            }
        }
    }

}
