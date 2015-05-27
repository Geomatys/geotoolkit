/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.image;

import org.geotoolkit.image.color.ScaledColorSpace;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

//import org.geotoolkit.coverage.grid.Viewer;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the {@link ScaledColorSpace} implementation.
 * This is a visual test when run from the command line.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
public final strictfp class ScaledColorSpaceTest {
    /**
     * Random number generator for this test.
     */
    private static final Random random = new Random(5078987324568283L);

    /**
     * The minimal and maximal values to renderer.
     */
    private double minimum, maximum;

    /**
     * The scaled color space to test.
     */
    private ScaledColorSpace colors;

    /**
     * The image to use for test.
     */
    private RenderedImage image;

    /**
     * Sets up common objects used for all tests.
     */
    @Before
    public void setUp() {
        minimum = random.nextDouble()*100;
        maximum = random.nextDouble()*200 + minimum + 10;
        colors  = new ScaledColorSpace(1, 0, minimum, maximum);

        final int transparency = Transparency.OPAQUE;
        final int datatype     = DataBuffer.TYPE_FLOAT;
        final ColorModel model = new ComponentColorModel(colors, false, false, transparency, datatype);
        final WritableRaster data = model.createCompatibleWritableRaster(200,200);
        final BufferedImage image = new BufferedImage(model, data, false, null);
        final int width  = data.getWidth();
        final int height = data.getHeight();
        for (int x=width; --x>=0;) {
            for (int y=height; --y>=0;) {
                double v = hypot((double)x / width - 0.5, (double)y / height - 0.5);
                v = v*(maximum - minimum) + minimum;
                data.setSample(x,y,0,v);
            }
        }
        this.image = image;
    }

    /**
     * Tests the color space.
     */
    @Test
    public void testColorSpace() {
        assertEquals(minimum, colors.getMinValue(0), 1E-4);
        assertEquals(maximum, colors.getMaxValue(0), 1E-4);

        final float[] array = new float[1];
        final double step = (maximum-minimum) / 256;
        for (double x=minimum; x<maximum; x+=step) {
            array[0] = (float)x;
            assertEquals(x, colors.fromRGB(colors.toRGB(array))[0], 1E-3);
        }
    }
}
