/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.util.Locale;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.io.IOException;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.test.coverage.image.ImageWriterTestCase;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.test.referencing.WKT;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.MetadataNodeAccessor;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;

import static org.junit.Assert.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * The base class for {@link TextImageWriter} tests.
 * The writers should use the {@link Locale#CANADA}.
 * This arbitrary locale is fixed in order to keep the build locale-independent.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.06 (derived from 2.4)
 */
public abstract strictfp class TextImageWriterTestBase extends ImageWriterTestCase {
    /**
     * Creates a new test suite.
     */
    protected TextImageWriterTestBase() {
    }

    /**
     * Tests the number format. This method must be invoked explictly in sub-class,
     * because this test is not suitable to all of them. The code is declared in this
     * class instead than sub-class in order to get access to package-protected methods.
     *
     * @param  writer the {@code TextImageWriter} to test.
     * @throws IOException Should never happen.
     */
    protected static void testCreateNumberFormat(final TextImageWriter writer) throws IOException {
        final IIOImage image = createImage(false);
        assertEquals(Locale.CANADA, writer.getDataLocale(null));

        final NumberFormat format = writer.createNumberFormat(image, null);
        assertEquals(2, format.getMinimumFractionDigits());
        assertEquals(2, format.getMaximumFractionDigits());
        assertEquals(1, format.getMinimumIntegerDigits());
        assertEquals( "0.12", format.format( 0.1216));
        assertEquals("-0.30", format.format(-0.2978));

        final FieldPosition pos = writer.getExpectedFractionPosition(format);
        assertEquals("Field type", NumberFormat.FRACTION_FIELD, pos.getField());
        assertEquals("Fraction width", 2, pos.getEndIndex() - pos.getBeginIndex());
        assertEquals("Total width (including sign)", 6, pos.getEndIndex());
    }

    /**
     * Creates dummy metadata for the image to be returned by {@link #createImage()}.
     */
    private static IIOMetadata createMetadata() {
        final IIOMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
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
            new ReferencingBuilder(metadata).setCoordinateReferenceSystem(CRS.parseWKT(WKT.PROJCS_MERCATOR));
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
    protected static IIOImage createImage(final boolean withNaN) throws IOException {
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
     * Clears the user objects in the given metadata.
     * This is used for forcing recreation of objects, for testing this creation process.
     *
     * @param metadata The metadata in which to clear user objects.
     *
     * @since 3.09
     */
    protected static void clearUserObjects(final IIOMetadata metadata) {
        clearUserObject(metadata, CoordinateReferenceSystem.class);
        clearUserObject(metadata, CoordinateSystem.class);
        clearUserObject(metadata, Datum.class);
        clearUserObject(metadata, Ellipsoid.class);
        clearUserObject(metadata, PrimeMeridian.class);
    }

    /**
     * Clears the user object of the given type in the given metadata.
     * This is used for forcing recreation of objects, for testing this creation process.
     */
    private static void clearUserObject(final IIOMetadata metadata, final Class<? extends IdentifiedObject> type) {
        new MetadataNodeAccessor(metadata, SpatialMetadataFormat.GEOTK_FORMAT_NAME, type).setUserObject(null);
    }
}
