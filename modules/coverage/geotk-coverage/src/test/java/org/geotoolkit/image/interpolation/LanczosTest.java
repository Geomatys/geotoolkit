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
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test Lanczos class.
 *
 * Todo : find how compute max and min values from interpolate boundary.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class LanczosTest extends InterpolationTest {

    private int miny, minx, width, height;
    private WritableRaster rastertest;

    public LanczosTest() {
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
        interpol = new LanczosInterpolation(pixIterator, 2);
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
        pixIterator.moveTo(0, -1);
        double interpolXDeb, interpolXEnd, interpolYDeb, interpolYEnd;
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
            }
        }
    }
}
