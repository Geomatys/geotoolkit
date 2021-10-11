/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.awt.image.BufferedImage;
import java.awt.image.SampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.MultiPixelPackedSampleModel;

import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestData;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Enumeration of sample data that can be loaded by {@link SampleImageTestBase#loadSampleImage}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.00
 */
public strictfp enum SampleImage {
    /**
     * A RGB image having a rotation. Checksum values are for:
     * <p>
     * <ol>
     *   <li>Java 6 update 17 and before</li>
     *   <li>Java 6 update 18 and after</li>
     * </ol>
     */
    RGB_ROTATED("MODIS.png", PixelInterleavedSampleModel.class, 3650654124L, 4050219331L),

    /**
     * Sea Surface Temperature (SST) using indexed color model.
     */
    INDEXED("QL95209.png", PixelInterleavedSampleModel.class, 1873283205L),

    /**
     * Chlorophyl-a concentration (CHL) using indexed color model. From a color model point of view,
     * this image brings nothing new compared to {@link #INDEXED}. However the interesting part for
     * a coverage is that the relationship between pixels and geophysics values is a logarithmic one
     * instead than a linear one.
     *
     * @since 3.02
     */
    INDEXED_LOGARITHMIC("CHL01195.png", PixelInterleavedSampleModel.class, 600909489L),

    /**
     * The boundary of a shape. Used for testing <cite>scanline flood fill</cite>.
     * There is no accurate CRS associated to this image.
     */
    CONTOUR("Contour.png", MultiPixelPackedSampleModel.class, 1089548139L);

    /**
     * Ensure that the standard PNG reader (the one provided in JSE) is used.
     */
    static {
        Registry.setNativeCodecAllowed("png", ImageReaderSpi.class, false);
    }

    /**
     * The filename to load.
     */
    public final String filename;

    /**
     * The expected sample model for which the checksum has been computed.
     * Some platform will use a different sample model, in which case we
     * can not expect the checksum to work.
     */
    private final Class<? extends SampleModel> model;

    /**
     * The expected checksum. The values found in practice may vary according the
     * Java version or the platform.
     */
    private final long[] checksum;

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
    private SampleImage(final String filename, final Class<? extends SampleModel> model, final long... checksum) {
        this.filename = filename;
        this.model    = model;
        this.checksum = checksum;
    }

    /**
     * Loads the sample image. This method may returns a cached image for performance reasons.
     * <p>
     * If the loaded image doesn't use the expected sample model (which may happen on some
     * platforms), the checksum will not be correct. In such case the test will be stopped
     * with an {@code assumeTrue(boolean)} instruction.
     *
     * @return The sample image.
     * @throws IOException If the image can not be read.
     */
    public final synchronized BufferedImage load() throws IOException {
        BufferedImage image;
        if (cache != null) {
            image = cache.get();
            if (image != null) {
                final long c = Commons.checksum(image);
                for (final long expected : checksum) {
                    if (c == expected) {
                        return image;
                    }
                }
            }
        }
        image = ImageIO.read(TestData.url(SampleImage.class, filename));
        assumeTrue(model.isInstance(image.getSampleModel()));
        final long c = Commons.checksum(image);
        for (final long expected : checksum) {
            if (c == expected) {
                cache = new SoftReference<>(image);
                return image;
            }
        }
        fail("Image " + filename + " as invalid checksum: " + c);
        return image;
    }
}
