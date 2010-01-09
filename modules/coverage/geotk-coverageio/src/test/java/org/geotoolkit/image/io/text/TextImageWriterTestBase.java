/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.text;

import java.util.Locale;
import java.io.IOException;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.WKT;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.image.io.SpatialImageWriter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.CRSAccessor;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;

import static org.junit.Assert.*;


/**
 * The base class for {@link TextImageWriter} tests.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @since 3.06 (derived from 2.4)
 */
public abstract class TextImageWriterTestBase {
    /**
     * Creates dummy metadata for the image to be returned by {@link #createImage()}.
     */
    private static IIOMetadata createMetadata() {
        final IIOMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final GridDomainAccessor domain = new GridDomainAccessor(metadata);
        domain.setOrigin(-500, 400);
        domain.addOffsetVector(100, 0);
        domain.addOffsetVector(0, -100);
        final DimensionAccessor dimensions = new DimensionAccessor(metadata);
        dimensions.selectChild(dimensions.appendChild());
        dimensions.setValueRange(0f, 88.97f);
        dimensions.setFillSampleValues(-9998); // Intentionnaly use a value different than -9999.
        /*
         * Adds a Coordinate Reference System.
         * We use a simple Mercator projection.
         */
        try {
            new CRSAccessor(metadata).setCRS(CRS.parseWKT(WKT.PROJCS_MERCATOR));
        } catch (FactoryException e) {
            fail(e.toString());
        }
        return metadata;
    }

    /**
     * Returns a one-banded grayscale image with the sample values documented below.
     *
     * {@preformat text
     *     0.00   0.01   0.02   0.03   0.04   0.05   0.06   0.07
     *     0.10   0.11   0.12   0.13   0.14   0.15   0.16   0.17
     *     0.20   0.21   0.22   0.23   0.24   0.25   0.26   0.27
     *     0.30   0.31   0.32   0.33   0.34   0.35   0.36   0.37
     *     0.40   0.41   0.42   0.43   0.44   0.45   0.46   0.47
     *    88.50  88.51  88.52  88.53  88.54  88.55  88.56  88.57
     *    88.60  88.61  88.62  88.63  88.64  88.65  88.66  88.67
     *    88.70  88.71  88.72  88.73  88.74  88.75  88.76  88.77
     *    88.80  88.81  88.82  88.83  88.84  88.85  88.86  88.87
     *    88.90  88.91  88.92  88.93  88.94  88.95  88.96  88.97
     * }
     *
     * @param  withNaN If {@code true}, a few NaN numbers will be added in the matrix.
     *         They are put in place of {@code 0.32} and {@code 88.76} values.
     * @return The image.
     * @throws IOException Should never happen.
     */
    protected static strictfp IIOImage createImage(final boolean withNaN) throws IOException {
        final int width  = 8;
        final int height = 10;
        final ColorModel cm = PaletteFactory.getDefault().getContinuousPalette(
                "grayscale", 0f, 1f, DataBuffer.TYPE_FLOAT, 1, 0).getColorModel();
        final WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                double value = (10*y + x) / 100.0;
                if (y >= 5) {
                    value += 88;
                }
                if (withNaN) {
                    if (x == 2 && y == 3) value = Double.NaN;
                    if (x == 6 && y == 7) value = Double.NaN;
                }
                raster.setSample(x, y, 0, value);
            }
        }
        return new IIOImage(new BufferedImage(cm, raster, false, null), null, createMetadata());
    }

    /**
     * Creates the image writer using the {@link Locale#CANADA}.
     * This arbitrary locale is fixed in order to keep the build locale-independent.
     *
     * @return The reader to test.
     * @throws IOException If an error occured while creating the format.
     */
    protected abstract SpatialImageWriter createImageWriter() throws IOException;
}
