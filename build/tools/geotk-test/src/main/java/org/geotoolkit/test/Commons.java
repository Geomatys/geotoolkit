/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.io.*;
import java.util.Collection;
import java.util.zip.CRC32;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.DataBufferByte;
import java.awt.geom.AffineTransform;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.operation.MathTransform;

import static org.junit.Assert.*;


/**
 * Provides shared methods and constants for Geotk tests.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @since 3.00
 */
public strictfp class Commons {
    /**
     * The character to be substituted to {@code '"'} in {@link #printAsJavaCode}.
     */
    private static final char OPENING_QUOTE = '\u201C', CLOSING_QUOTE = '\u201D';

    /**
     * For subclass constructor only.
     */
    protected Commons() {
    }

    /**
     * Returns the string representation of all elements in the given collection, in iteration
     * order. If a collection element is {@code null}, then the corresponding array element will
     * be {@code null} too.
     *
     * @param  collection The collection from which to get the string representation of each elements.
     * @return The string representation of collection elements. May contain null values.
     *
     * @since 3.20
     */
    public static String[] toStringArray(final Collection<?> collection) {
        final String[] strings = new String[collection.size()];
        int i=0; for (final Object element : collection) {
            if (element != null) {
                strings[i] = element.toString();
            }
            i++;
        }
        assertEquals("Premature end of iteration.", strings.length, i);
        return strings;
    }

    /**
     * Returns the "Sample to geophysics" transform as an affine transform, or {@code null}
     * if none. Note that the returned instance may be an immutable one, not necessarily the
     * default Java2D implementation.
     *
     * @param  coverage The coverage for which to get the "grid to CRS" affine transform.
     * @return The "grid to CRS" affine transform of the given coverage, or {@code null}
     *         if none or if the transform is not affine.
     */
    public static AffineTransform getAffineTransform(final Coverage coverage) {
        if (coverage instanceof GridCoverage) {
            final GridGeometry geometry = ((GridCoverage) coverage).getGridGeometry();
            if (geometry != null) {
                final MathTransform gridToCRS = geometry.getGridToCRS();
                if (gridToCRS instanceof AffineTransform) {
                    return (AffineTransform) gridToCRS;
                }
            }
        }
        return null;
    }

    /**
     * Computes the checksum on pixels of the given image. Current implementation assumes that
     * the data type are {@link DataBuffer#TYPE_BYTE}. Note that this computation is sensitive
     * to image tiling, if there is any.
     *
     * @param  image The image for which to compute the checksum.
     * @return The checksum of the given image.
     */
    public static long checksum(final RenderedImage image) {
        assertEquals("Current implementation requires byte data type.",
                DataBuffer.TYPE_BYTE, image.getSampleModel().getDataType());
        final CRC32 sum = new CRC32();
        int ty = image.getMinTileY();
        for (int ny=image.getNumYTiles(); --ny>=0; ty++) {
            int tx = image.getMinTileX();
            for (int nx=image.getNumXTiles(); --nx>=0; tx++) {
                final Raster raster = image.getTile(tx, ty);
                final DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
                final int[] offsets = buffer.getOffsets();
                final int size = buffer.getSize();
                for (int i=0; i<offsets.length; i++) {
                    sum.update(buffer.getData(i), offsets[i], size);
                }
            }
        }
        return sum.getValue();
    }

    /**
     * Decodes as plain text a string encoded by {@link #printAsJavaCode}.
     *
     * @param  text The text encoded by {@link #printAsJavaCode}.
     * @return The decoded text.
     */
    public static String decodeQuotes(final String text) {
        return text.replace(OPENING_QUOTE, '"').replace(CLOSING_QUOTE, '"');
    }

    /**
     * Serializes the given object to the {@link target/surefire-reports/} directory.
     * This method can be invoked after a test failure in order to allow the developer
     * to examine the objects that caused the failure. In case of I/O error the problem
     * is reported on the standard error stream but no exception is thrown in order to
     * not mask the main problem, which was the test failure.
     *
     * @param  testClass The test class, which will determine the filename.
     * @param  object The object to serialize, or {@code null} if none.
     */
    public static void serializeToSurefireDirectory(final Class<?> testClass, final Object object) {
        if (object == null) {
            return;
        }
        File file = new File("target");
        if (!file.isDirectory() || !file.canWrite()) {
            System.err.println("Directory not found or read only: " + file.getAbsolutePath());
            return;
        }
        file = new File(file, "surefire-reports");
        if (!file.exists() && !file.mkdir()) {
            System.err.println("Failed to create the output directory: " + file.getAbsolutePath());
            return;
        }
        file = new File(file, testClass.getName() + ".serialized");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(object);
        } catch (IOException e) {
            System.err.println(e);
            file.delete();
        }
    }
}
