/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.awt.image.BufferedImage;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestData;

import static org.junit.Assert.*;



/**
 * Enumeration of sample data that can be loaded by {@link ImageTestCase#loadSampleImage}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.00
 */
public enum SampleImage {
    /**
     * A RGB image having a rotation.
     */
    RGB_ROTATED("MODIS.png", 3650654124L),

    /**
     * Sea Surface Temperature (SST) using indexed color model.
     */
    INDEXED("QL95209.png", 1873283205L),

    /**
     * Chlorophyl-a concentration (CHL) using indexed color model. From a color model point of view,
     * this image brings nothing new compared to {@link #INDEXED}. However the interresting part for
     * a coverage is that the relationship between pixels and geophysics values is a logarithmic one
     * instead than a linear one.
     *
     * @since 3.02
     */
    INDEXED_LOGARITHMIC("CHL01195.png", 600909489L),

    /**
     * The boundary of a shape. Used for testing <cite>scanline flood fill</cite>.
     * There is no accurate CRS associated to this image.
     */
    CONTOUR("Contour.png", 1089548139L);

    /**
     * The filename to load.
     */
    public final String filename;

    /**
     * The expected checksum.
     */
    private final long checksum;

    /**
     * The loaded image, cached for reuse.
     */
    private transient Reference<BufferedImage> cache;

    /**
     * Creates an enumeration for the given filename.
     *
     * @param filename The filename of the image to be loaded.
     * @param checksum The expected checksum.
     */
    private SampleImage(final String filename, final long checksum) {
        this.filename = filename;
        this.checksum = checksum;
    }

    /**
     * Loads the sample image. This method may returns a cached image for performance reasons.
     *
     * @return The sample image.
     * @throws IOException If the image can not be read.
     */
    public final synchronized BufferedImage load() throws IOException {
        BufferedImage image;
        if (cache != null) {
            image = cache.get();
            if (image != null && Commons.checksum(image) == checksum) {
                return image;
            }
        }
        image = ImageIO.read(TestData.url(SampleImage.class, filename));
        assertEquals(filename, checksum, Commons.checksum(image));
        cache = new SoftReference<BufferedImage>(image);
        return image;
    }
}
