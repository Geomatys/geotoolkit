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
import java.text.Format;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
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

import org.apache.sis.util.CharSequences;
import static org.junit.Assert.*;


/**
 * Provides shared methods and constants for Geotk tests.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
public strictfp class Commons {
    /**
     * The version of the EPSG database used. Please update this field if the version of
     * the embedded EPSG database provided in the {@code "geotk-epsg"} module is updated.
     *
     * @since 3.11
     */
    public static final String EPSG_VERSION = "7.9";

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
     * Returns the single element from the given collection. If the given collection is null
     * or does not contains exactly one element, then an {@link AssertionError} is thrown.
     *
     * @param  <E> The type of collection elements.
     * @param  collection The collection from which to get the singleton.
     * @return The singleton element from the collection.
     *
     * @since 3.19
     */
    public static <E> E getSingleton(final Iterable<? extends E> collection) {
        assertNotNull("Null collection.", collection);
        final Iterator<? extends E> it = collection.iterator();
        assertTrue("The collection is empty.", it.hasNext());
        final E element = it.next();
        assertFalse("The collection has more than one element.", it.hasNext());
        return element;
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
        for (final CharSequence cs : CharSequences.splitOnEOL(text)) {
            if (continuing) {
                out.println("\\n\" +");
            }
            continuing = true;
            out.print(margin);
            out.print('"');
            int quotes = 0;
            final String line = cs.toString();
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

    /**
     * Reads the first object found in the given file. If the given file can not be read,
     * then this method throws an {@link AssertionError} - which will typically be reported
     * as a JUnit test failure.
     * <p>
     * This method is typically used in order to inspect the object saved by
     * {@link #serializeToSurefireDirectory(Class, Object)} after a test failure.
     *
     * @param  file The file from which to read an object.
     * @return The first object read from the given file.
     *
     * @since 3.19
     */
    public static Object deserialize(final String file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getLocalizedMessage());
        }
    }
}
