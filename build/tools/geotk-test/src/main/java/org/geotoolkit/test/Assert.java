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
import java.awt.geom.AffineTransform;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.ParserConfigurationException;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.measure.unit.Unit;
import org.xml.sax.SAXException;

import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;

import org.geotoolkit.test.xml.DomComparator;

import static org.geotoolkit.test.image.ImageTestBase.SAMPLE_TOLERANCE;


/**
 * Assertion methods used by the Geotk project in addition of the JUnit and GeoAPI assertions.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
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
        assertDomEquals(expected, actual, 0, ignoredAttributes);
    }

    /**
     * Parses two XML tree as DOM documents, and compares the nodes with the given tolerance
     * threshold for numerical values. The inputs given to this method can be any of the types
     * documented {@linkplain #assertDomEquals(Object, Object, String[]) above}. This method
     * will ignore comments and the optional attributes given in arguments.
     *
     * @param  expected  The expected XML document.
     * @param  actual    The XML document to compare.
     * @param  tolerance The tolerance threshold for comparison of numerical values.
     * @param  ignoredAttributes The fully-qualified names of attributes to ignore
     *         (typically {@code "xmlns:*"} and {@code "xsi:schemaLocation"}).
     *
     * @see DomComparator
     *
     * @since 3.18
     */
    public static void assertDomEquals(final Object expected, final Object actual,
            final double tolerance, final String... ignoredAttributes)
    {
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
        comparator.tolerance = tolerance;
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
     * Asserts that the given parameter values are equal to the expected ones within a
     * positive delta. Only the elements in the given descriptor are compared, and the
     * comparisons are done in the units declared in the descriptor.
     *
     * @param expected  The expected parameter values.
     * @param actual    The actual parameter values.
     * @param tolerance The tolerance threshold for comparison of numerical values.
     *
     * @since 3.19
     */
    public static void assertParameterEquals(final ParameterValueGroup expected,
            final ParameterValueGroup actual, final double tolerance)
    {
        for (final GeneralParameterValue candidate : expected.values()) {
            if (!(candidate instanceof ParameterValue<?>)) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            final ParameterValue<?> value = (ParameterValue<?>) candidate;
            final ParameterDescriptor<?> descriptor = value.getDescriptor();
            final String   name       = descriptor.getName().getCode();
            final Unit<?>  unit       = descriptor.getUnit();
            final Class<?> valueClass = descriptor.getValueClass();
            final ParameterValue<?> e = expected.parameter(name);
            final ParameterValue<?> a = actual  .parameter(name);
            if (unit != null) {
                final double f = e.doubleValue(unit);
                assertEquals(name, f, a.doubleValue(unit), tolerance);
            } else if (valueClass == Float.class || valueClass == Double.class) {
                final double f = e.doubleValue();
                assertEquals(name, f, a.doubleValue(), tolerance);
            } else {
                assertEquals(name, e.getValue(), a.getValue());
            }
        }
    }

    /**
     * Compares two affine transforms for equality.
     *
     * @param expected The expected affine transform.
     * @param actual   The actual affine transform.
     */
    public static void assertTransformEquals(final AffineTransform expected, final AffineTransform actual) {
        assertEquals("scaleX",     expected.getScaleX(),     actual.getScaleX(),     SAMPLE_TOLERANCE);
        assertEquals("scaleY",     expected.getScaleY(),     actual.getScaleY(),     SAMPLE_TOLERANCE);
        assertEquals("shearX",     expected.getShearX(),     actual.getShearX(),     SAMPLE_TOLERANCE);
        assertEquals("shearY",     expected.getShearY(),     actual.getShearY(),     SAMPLE_TOLERANCE);
        assertEquals("translateX", expected.getTranslateX(), actual.getTranslateX(), SAMPLE_TOLERANCE);
        assertEquals("translateY", expected.getTranslateY(), actual.getTranslateY(), SAMPLE_TOLERANCE);
    }

    /**
     * Asserts that two images have the same origin and the same size.
     *
     * @param expected The image having the expected size.
     * @param actual   The image to compare with the expected one.
     */
    public static void assertBoundEquals(final RenderedImage expected, final RenderedImage actual) {
        assertEquals("Min X",  expected.getMinX(),   actual.getMinX());
        assertEquals("Min Y",  expected.getMinY(),   actual.getMinY());
        assertEquals("Width",  expected.getWidth(),  actual.getWidth());
        assertEquals("Height", expected.getHeight(), actual.getHeight());
    }

    /**
     * Compares two rasters for equality.
     *
     * @param expected The image containing the expected pixel values.
     * @param actual   The image containing the actual pixel values.
     */
    public static void assertRasterEquals(final RenderedImage expected, final RenderedImage actual) {
        final RectIter e = RectIterFactory.create(expected, null);
        final RectIter a = RectIterFactory.create(actual,   null);
        if (!e.finishedLines()) do {
            assertFalse(a.finishedLines());
            if (!e.finishedPixels()) do {
                assertFalse(a.finishedPixels());
                if (!e.finishedBands()) do {
                    assertFalse(a.finishedBands());
                    final float pe = e.getSampleFloat();
                    final float pa = a.getSampleFloat();
                    assertEquals(pe, pa, SAMPLE_TOLERANCE);
                    a.nextBand();
                } while (!e.nextBandDone());
                assertTrue(a.finishedBands());
                a.nextPixel();
                a.startBands();
                e.startBands();
            } while (!e.nextPixelDone());
            assertTrue(a.finishedPixels());
            a.nextLine();
            a.startPixels();
            e.startPixels();
        } while (!e.nextLineDone());
        assertTrue(a.finishedLines());
    }

    /**
     * Compares the rendered view of two coverages for equality.
     *
     * @param expected The coverage containing the expected pixel values.
     * @param actual   The coverage containing the actual pixel values.
     */
    public static void assertRasterEquals(final Coverage expected, final Coverage actual) {
        assertRasterEquals(expected.getRenderableImage(0,1).createDefaultRendering(),
                             actual.getRenderableImage(0,1).createDefaultRendering());
    }
}
