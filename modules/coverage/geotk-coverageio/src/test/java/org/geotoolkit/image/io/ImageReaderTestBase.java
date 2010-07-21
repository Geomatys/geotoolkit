/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.image.io;

import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.math.Statistics;


/**
 * The base class for {@link ImageReader} tests.
 * <p>
 * This class provides also {@link #loadAndPrint} and {@link #printStatistics} static methods
 * for manual testings.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.06
 */
public abstract class ImageReaderTestBase extends org.geotoolkit.test.image.ImageReaderTestBase {
    /**
     * Loads the given image using the given provider, and prints information about it.
     * This is used only as a helper tools for tuning the test suites.
     *
     * @param  provider     The provider from which to get a reader.
     * @param  input        The file to read.
     * @param  region       The region in the file to read.
     * @param  xSubsampling Subsampling along the <var>x</var> axis (1 if none).
     * @param  ySubsampling Subsampling along the <var>y</var> axis (1 if none).
     * @return The raster which have been read.
     * @throws IOException In an error occurred while reading.
     */
    public static Raster loadAndPrint(final ImageReaderSpi provider, final File input,
            final Rectangle region, final int xSubsampling, final int ySubsampling) throws IOException
    {
        final ImageReader reader = provider.createReaderInstance();
        reader.setInput(input);
        System.out.println(reader.getImageMetadata(0));

        final ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceRegion(region);
        param.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);

        final long timestamp = System.currentTimeMillis();
        BufferedImage image = reader.read(0, param);
        System.out.println("Ellapsed time: " + (System.currentTimeMillis() - timestamp) / 1000f + " seconds.");
        reader.dispose();

        final Raster raster = image.getRaster();
        printStatistics(raster);
        System.out.println();
        return raster;
    }

    /**
     * Prints the minimal and maximal values found in the given raster.
     * This is used only as a helper tools for tuning the test suites.
     *
     * @param  raster The raster for which to print extrema.
     */
    public static void printStatistics(final Raster raster) {
        final Statistics stats = new Statistics();
        final int xmin = raster.getMinX();
        final int ymin = raster.getMinY();
        final int xmax = raster.getWidth()  + xmin;
        final int ymax = raster.getHeight() + ymin;
        final int nb   = raster.getNumBands();
        for (int b=0; b<nb; b++) {
            for (int y=ymin; y<ymax; y++) {
                for (int x=xmin; x<xmax; x++) {
                    stats.add(raster.getSampleDouble(x, y, b));
                }
            }
        }
        System.out.println("Raster bounds: (" + xmin + ',' + ymin + ") - (" + xmax + ',' + ymax + ')');
        System.out.println("Statistics on sample values (" + nb + " bands):");
        System.out.println(stats);
    }
}
