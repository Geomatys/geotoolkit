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

import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import javax.media.jai.TiledImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 * Test any interpolation.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class InterpolationTest {

    protected static final double TOLERANCE = 1E-9;

    /**
     * {@code PixelIterator} Iterator use to interpolate.
     */
    protected PixelIterator pixIterator;

    /**
     * Tested {@code RenderedImage}.
     */
    TiledImage renderedImage;

    /**
     * Current interpolate tested.
     */
    protected Interpolation interpol;

    /**
     * Create default interpolation test.
     */
    public InterpolationTest() {
        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, 3, 3, 1);
//        final PixelInterleavedSampleModel sampleM = new PixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE, 3, 3, 1, 3, new int[1]);
        renderedImage = new TiledImage(-1, -1, 3, 3, -1, -1, sampleM, null);
        double val = 0;
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                renderedImage.setSample(x, y, 0, val++);
            }
        }
        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage);
    }
}
