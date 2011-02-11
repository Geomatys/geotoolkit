/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.awt.image.RenderedImage;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.geotoolkit.test.xml.DomComparator;


/**
 * Assertion methods used by the Geotk project in addition of the JUnit and GeoAPI assertions.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.16 (derived from 3.00)
 */
public class Assert extends org.opengis.test.Assert {
    /**
     * For subclass constructor only.
     */
    protected Assert() {
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
     * Parses two XML tree as DOM documents, and compares the nodes.
     * The inputs given to this method can be any of the following types:
     * <p>
     * <ul>
     *   <li>{@link org.w3c.dom.Node}; used directly without further processing.</li>
     *   <li>{@link java.io.File}, {@link java.net.URL} or {@link java.net.URI}: the
     *       stream is opened and parsed as a XML document.</li>
     *   <li>{@link String}: The string content is parsed directly as a XML document.
     *       Encoding <strong>must</strong> be UTF-8 (no other encoding is supported
     *       by current implementation of this method).</li>
     * </ul>
     * <p>
     * This method will ignore comments and the optional attributes given in arguments.
     *
     * @param  expected The expected XML document.
     * @param  actual   The XML document to compare.
     * @param  ignoredAttributes The fully-qualified names of attributes to ignore
     *         (typically {@code "xmlns:*"} and {@code "xsi:schemaLocation"}).
     *
     * @see DomComparator
     *
     * @since 3.17
     */
    public static void assertDomEquals(final Object expected, final Object actual, final String... ignoredAttributes) {
        final DomComparator comparator;
        try {
            comparator = new DomComparator(expected, actual);
        } catch (IOException e) {
            // We don't throw directly those exceptions since failing to parse the XML file can
            // be considered as part of test failures and the JUnit exception for such failures
            // is AssertionError. Having no checked exception in "assert" methods allow us to
            // declare the checked exceptions only for the library code being tested.
            throw new AssertionError(e);
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e); // TODO: multi-catch with JDK 7.
        } catch (SAXException e) {
            throw new AssertionError(e); // TODO: multi-catch with JDK 7.
        }
        comparator.ignoreComments = true;
        comparator.ignoredAttributes.addAll(Arrays.asList(ignoredAttributes));
        comparator.compare();
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
    public static void assertSameImageBounds(final RenderedImage expected, final RenderedImage actual) {
        assertEquals("Min X",  expected.getMinX(),   actual.getMinX());
        assertEquals("Min Y",  expected.getMinY(),   actual.getMinY());
        assertEquals("Width",  expected.getWidth(),  actual.getWidth());
        assertEquals("Height", expected.getHeight(), actual.getHeight());
    }
}
