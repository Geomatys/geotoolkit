/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.measure.unit.Unit;

import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;

import static org.geotoolkit.test.image.ImageTestBase.SAMPLE_TOLERANCE;


/**
 * Assertion methods used by the Geotk project in addition of the JUnit, GeoAPI and SIS assertions.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public strictfp class Assert extends org.apache.sis.test.Assert {
    /**
     * For subclass constructor only.
     */
    protected Assert() {
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
     * Asserts that two rectangles have the same location and the same size.
     *
     * @param expected The expected rectangle.
     * @param actual   The rectangle to compare with the expected one.
     * @param tolx     The horizontal tolerance threshold on location.
     * @param toly     The vertical tolerance threshold on location.
     *
     * @since 3.20
     */
    public static void assertRectangleEquals(final RectangularShape expected, final RectangularShape actual, final double tolx, final double toly) {
        assertEquals("Min X",    expected.getMinX(),    actual.getMinX(),    tolx);
        assertEquals("Min Y",    expected.getMinY(),    actual.getMinY(),    toly);
        assertEquals("Max X",    expected.getMaxX(),    actual.getMaxX(),    tolx);
        assertEquals("Max Y",    expected.getMaxY(),    actual.getMaxY(),    toly);
        assertEquals("Center X", expected.getCenterX(), actual.getCenterX(), tolx);
        assertEquals("Center Y", expected.getCenterY(), actual.getCenterY(), toly);
        assertEquals("Width",    expected.getWidth(),   actual.getWidth(),   tolx*2);
        assertEquals("Height",   expected.getHeight(),  actual.getHeight(),  toly*2);
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
     * Compares two rasters for equality. The sample values are compared with {@code float}
     * precision, because this is the format used by the majority of geophysics raster data.
     * <p>
     * This method does not test if {@linkplain #assertBoundEquals bounds are equal} (actually,
     * it ensures that the image are of the same size but doesn't check if the origin is the
     * same). It is user responsibility to invoke the above method if desired.
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

    /**
     * Ensures that all sample values in every bands are either inside the given range,
     * or {@link Double#NaN}.
     *
     * @param minimum The lower bound of the range, inclusive.
     * @param maximum The upper bound of the range, inclusive.
     * @param image   The image to test.
     *
     * @since 3.19
     */
    public static void assertSampleValuesInRange(final double minimum, final double maximum,
            final RenderedImage image)
    {
        final RectIter it = RectIterFactory.create(image, null);
        if (!it.finishedLines()) do {
            if (!it.finishedPixels()) do {
                if (!it.finishedBands()) do {
                    final double value = it.getSampleDouble();
                    assertBetween("Sample value", minimum, maximum, value);
                } while (!it.nextBandDone());
                it.startBands();
            } while (!it.nextPixelDone());
            it.startPixels();
        } while (!it.nextLineDone());
    }

    /**
     * Tests if the given {@code outer} shape contains the given {@code inner} rectangle.
     * This method will also verify class consistency by invoking the {@code intersects}
     * method, and by interchanging the arguments. This method can be used for testing
     * the {@code outer} implementation - it should not be needed for standard implementations.
     *
     * @param outer The shape which is expected to contains the given rectangle.
     * @param inner The rectangle which should be contained by the shape.
     *
     * @since 3.20
     */
    public static void assertContains(final RectangularShape outer, final Rectangle2D inner) {
        assertTrue("outer.contains(inner)",   outer.contains  (inner));
        assertTrue("outer.intersects(inner)", outer.intersects(inner));
        if (outer instanceof Rectangle2D) {
            assertTrue ("inner.intersects(outer)", inner.intersects((Rectangle2D) outer));
            assertFalse("inner.contains(outer)",   inner.contains  ((Rectangle2D) outer));
        }
        assertTrue("outer.contains(centerX, centerY)",
                outer.contains(inner.getCenterX(), inner.getCenterY()));
    }

    /**
     * Tests if the given {@code r1} shape is disjoint with the given {@code r2} rectangle.
     * This method will also verify class consistency by invoking the {@code contains}
     * method, and by interchanging the arguments. This method can be used for testing
     * the {@code r1} implementation - it should not be needed for standard implementations.
     *
     * @param r1 The first shape to test.
     * @param r2 The second rectangle to test.
     *
     * @since 3.20
     */
    public static void assertDisjoint(final RectangularShape r1, final Rectangle2D r2) {
        assertFalse("r1.intersects(r2)", r1.intersects(r2));
        assertFalse("r1.contains(r2)",   r1.contains(r2));
        if (r1 instanceof Rectangle2D) {
            assertFalse("r2.intersects(r1)", r2.intersects((Rectangle2D) r1));
            assertFalse("r2.contains(r1)",   r2.contains  ((Rectangle2D) r1));
        }
        for (int i=0; i<9; i++) {
            final double x, y;
            switch (i % 3) {
                case 0: x = r2.getMinX();    break;
                case 1: x = r2.getCenterX(); break;
                case 2: x = r2.getMaxX();    break;
                default: throw new AssertionError(i);
            }
            switch (i / 3) {
                case 0: y = r2.getMinY();    break;
                case 1: y = r2.getCenterY(); break;
                case 2: y = r2.getMaxY();    break;
                default: throw new AssertionError(i);
            }
            assertFalse("r1.contains(" + x + ", " + y + ')', r1.contains(x, y));
        }
    }
}
