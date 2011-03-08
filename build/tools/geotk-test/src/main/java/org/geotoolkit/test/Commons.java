/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import java.text.Format;
import java.text.ParseException;
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
 * @version 3.17
 *
 * @since 3.00
 */
public class Commons {
    /**
     * The version of the EPSG database used. Please update this field if the version of
     * the embedded EPSG database provided in the {@code "geotk-epsg"} module is updated.
     *
     * @since 3.11
     */
    public static final String EPSG_VERSION = "7.6";

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
     * Serializes the given object, deserialize it and ensure that the deserialized object
     * is equal to the original one.
     * <p>
     * If the serialization fails, then this method thrown a {@link AssertionError}
     * as do the other JUnit assertion methods.
     *
     * @param  <T> The type of the object to serialize.
     * @param  object The object to serialize.
     * @return The deserialized object.
     */
    public static <T> T serialize(final T object) {
        final Object deserialized;
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(object);
            out.close();
            /*
             * Now reads the object we just serialized.
             */
            final byte[] data = buffer.toByteArray();
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
            try {
                deserialized = in.readObject();
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
            in.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        /*
         * Compares with the original object and returns it.
         */
        @SuppressWarnings("unchecked")
        final Class<? extends T> type = (Class<? extends T>) object.getClass();
        assertEquals("Deserialized object not equal to the original one.", object, deserialized);
        assertEquals("Deserialized object has a different hash code.",
                object.hashCode(), deserialized.hashCode());
        return type.cast(deserialized);
    }

    /**
     * Formats the given value using the given formatter, and parses the text back to its value.
     * If the parsed value is not equal to the original one, an {@link AssertionError} is thrown.
     *
     * @param  formatter The formatter to use for formatting and parsing.
     * @param  value The value to format.
     * @return The formatted value.
     */
    public static String formatAndParse(final Format formatter, final Object value) {
        final String text = formatter.format(value);
        final Object parsed;
        try {
            parsed = formatter.parseObject(text);
        } catch (ParseException e) {
            throw new AssertionError(e);
        }
        assertEquals("Parsed text not equal to the original value", value, parsed);
        return text;
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
     * Prints the given text to the standard output stream, formatted as a Java {@link String}
     * declaration constant. The quote character is escaped to special unicode characters for
     * easier reading. The generated code presumes that the following import statement is
     * declared in the class where to code is going to be copied:
     *
     * {@preformat java
     *     import static org.geotoolkit.test.Commons.*;
     * }
     *
     * @param text The text to format as Java code.
     */
    public static void printAsJavaCode(final String text) {
        final PrintStream out = System.out;
        out.print("        final String text =");
        final boolean hasQuotes = text.indexOf('"') >= 0;
        if (hasQuotes) {
            out.print(" decodeQuotes(");
        }
        out.println();

        final String margin = "                "; // 4 indentation levels (16 spaces).
        boolean continuing = false;
        for (final StringIterator it=new StringIterator(text); it.hasNext();) {
            if (continuing) {
                out.println("\\n\" +");
            }
            continuing = true;
            out.print(margin);
            out.print('"');
            int quotes = 0;
            final String line = it.next();
            for (int i=0; i<line.length(); i++) {
                char c = line.charAt(i);
                switch (c) {
                    case OPENING_QUOTE: // fallthrough
                    case CLOSING_QUOTE: {
                        throw new IllegalArgumentException("Text already contains quotation marks.");
                    }
                    case '"': {
                        c = (quotes & 1) == 0 ? OPENING_QUOTE : CLOSING_QUOTE;
                        quotes++;
                    }
                }
                out.print(c);
            }
        }
        out.print('"');
        if (hasQuotes) {
            out.print(')');
        }
        out.println(';');
    }
}
