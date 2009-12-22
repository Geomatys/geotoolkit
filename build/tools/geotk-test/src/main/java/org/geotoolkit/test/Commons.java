/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.io.*;
import java.text.Format;
import java.text.ParseException;
import java.util.zip.CRC32;
import java.util.Enumeration;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.DataBufferByte;
import javax.swing.tree.TreeNode;

import static org.junit.Assert.*;


/**
 * Provides assertion methods in addition of the JUnit ones.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.00
 */
public final class Commons {
    /**
     * Do not allows instantiation of this class.
     */
    private Commons() {
    }

    /**
     * Asserts that two strings are equal, ignoring the differences in EOL characters.
     * The comparisons is performed one a line-by-line basis. For each line, leading
     * and trailing spaces are ignored in order to make the comparison independent of
     * indentation.
     *
     * @param expected The expected string.
     * @param actual The actual string.
     */
    public static void assertMultilinesEquals(final String expected, final String actual) {
        assertMultilinesEquals(null, expected, actual);
    }

    /**
     * Asserts that two strings are equal, ignoring the differences in EOL characters.
     * The comparisons is performed one a line-by-line basis. For each line, leading
     * and trailing spaces are ignored in order to make the comparison independent of
     * indentation.
     *
     * @param message  The message to print in case of failure, or {@code null} if none.
     * @param expected The expected string.
     * @param actual The actual string.
     */
    public static void assertMultilinesEquals(final String message, final String expected, final String actual) {
        final StringBuilder buffer = new StringBuilder();
        if (message != null) {
            buffer.append(message).append(" at line ");
        } else {
            buffer.append("Line ");
        }
        int line = 0;
        final int length = buffer.length();
        final StringIterator c1 = new StringIterator(expected);
        final StringIterator c2 = new StringIterator(actual);
        while (c1.hasNext()) {
            final String next = c1.next().trim();
            if (!c2.hasNext()) {
                fail("Expected more lines: " + next);
            }
            buffer.setLength(length);
            buffer.append(++line);
            assertEquals(buffer.toString(), next, c2.next().trim());
        }
        if (c2.hasNext()) {
            fail("Unexpected line: " + c2.next());
        }
    }

    /**
     * Asserts that two strings formatted by the XML marshaller are equal. The XML header is
     * skipped, then the remaining is compared as with {@link #assertMultilinesEquals(String,
     * String)}.
     *
     * @param expected The expected string.
     * @param actual The actual string.
     *
     * @since 3.07
     */
    public static void assertXmlEquals(String expected, String actual) {
        expected = skipHeader(expected);
        actual   = skipHeader(actual);
        assertMultilinesEquals(expected, actual);
    }

    /**
     * Skips the two first lines, because the xlmns are not always in the same order.
     */
    private static String skipHeader(final String xml) {
        return xml.substring(xml.indexOf('\n', xml.indexOf('\n') + 1) + 1);
    }

    /**
     * Ensures that a tree is equals to an other tree. This method invokes itself
     * recursively for every child nodes.
     *
     * @param  expected The expected tree, or {@code null}.
     * @param  actual The tree to compare with the expected one, or {@code null}.
     * @return The number of nodes.
     *
     * @since 3.04
     */
    public static int assertTreeEquals(final TreeNode expected, final TreeNode actual) {
        if (expected == null) {
            assertNull(actual);
            return 0;
        }
        int n = 1;
        assertNotNull(actual);
        assertEquals("isLeaf()",            expected.isLeaf(),            actual.isLeaf());
        assertEquals("getAllowsChildren()", expected.getAllowsChildren(), actual.getAllowsChildren());
        assertEquals("getChildCount()",     expected.getChildCount(),     actual.getChildCount());
        @SuppressWarnings("unchecked") final Enumeration<? extends TreeNode> ec = expected.children();
        @SuppressWarnings("unchecked") final Enumeration<? extends TreeNode> ac = actual  .children();

        int childIndex = 0;
        while (ec.hasMoreElements()) {
            assertTrue("hasMoreElements()", ac.hasMoreElements());
            final TreeNode nextExpected = ec.nextElement();
            final TreeNode nextActual   = ac.nextElement();
            final String message = "getChildAt(" + childIndex + ')';
            assertSame(message, nextExpected, expected.getChildAt(childIndex));
            assertSame(message, nextActual,   actual  .getChildAt(childIndex));
            assertSame("getParent()", expected, nextExpected.getParent());
            assertSame("getParent()", actual,   nextActual  .getParent());
            assertSame("getIndex(TreeNode)", childIndex, expected.getIndex(nextExpected));
            assertSame("getIndex(TreeNode)", childIndex, actual  .getIndex(nextActual));
            n += assertTreeEquals(nextExpected, nextActual);
            childIndex++;
        }
        assertFalse("hasMoreElements()", ac.hasMoreElements());
        assertEquals("toString()", expected.toString(), actual.toString());
        return n;
    }

    /**
     * Asserts that two images have the same origin and the same size.
     *
     * @param expected The image having the expected size.
     * @param actual   The image to compare with the expected one.
     */
    public static void assertSameSize(final RenderedImage expected, final RenderedImage actual) {
        assertEquals("Min X",  expected.getMinX(),   actual.getMinX());
        assertEquals("Min Y",  expected.getMinY(),   actual.getMinY());
        assertEquals("Width",  expected.getWidth(),  actual.getWidth());
        assertEquals("Height", expected.getHeight(), actual.getHeight());
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
     * Decodes as plain text a string encoded by {@link Tools#printAsJavaCode}.
     *
     * @param  text The text encoded by {@link Tools#printAsJavaCode}.
     * @return The decoded text.
     */
    public static String decodeQuotes(final String text) {
        return text.replace(Tools.OPENING_QUOTE, '"').replace(Tools.CLOSING_QUOTE, '"');
    }

    /**
     * Returns {@code true} if the given value is neither {@linkplain Double#NaN NaN}
     * or infinity.
     *
     * @param  value The value to test.
     * @return {@code true} if the given value is neither NaN of infinity.
     */
    public static boolean isReal(final double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
}
