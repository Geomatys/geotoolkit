/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.projection.UnitaryProjection;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
import org.geotoolkit.io.wkt.FormattableObject;

import org.junit.Assert;
import static org.geotoolkit.test.Commons.*;


/**
 * Base class for referencing tests. Provides the assertions methods that we are
 * going to use.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.00
 */
public abstract class ReferencingTestCase extends Assert {
    /**
     * Small tolerance for comparisons of floating point values.
     */
    private static final double EPS = 1E-7;

    /**
     * Set to {@code true} for sending debugging information to the standard output stream.
     */
    protected boolean verbose = false;

    /**
     * For subclass constructors.
     */
    protected ReferencingTestCase() {
    }

    /**
     * Returns {@code true} if a factory backed by the EPSG database has been found on the
     * classpath. Some tests have different behavior depending on whatever such factory is
     * available or not.
     *
     * @return {@code true} if an EPSG-backed factory is available on the classpath.
     */
    public static boolean isEpsgFactoryAvailable() {
        final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class);
        final CRSAuthorityFactory factory;
        try {
            factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        } catch (FactoryNotFoundException e) {
            return false;
        }
        assertTrue(factory instanceof ThreadedEpsgFactory);
        return true;
    }

    /**
     * Asserts that the given transform is represented by diagonal matrix where every elements
     * on the diagonal have the given values. The matrix doesn't need to be square. The last
     * row is handled especially if the {@code affine} argument is {@code true}.
     *
     * @param tr     The transform.
     * @param affine If {@code true}, then the last row is expected to contains the value 1
     *               in the last column, and all other columns set to 0.
     * @param values The values which are expected on the diagonal. If this array length is
     *               smaller than the diagonal length, then the last element in the array
     *               is repeated for all remaining diagonal elements.
     *
     * @since 3.07
     */
    public static void assertDiagonalMatrix(final MathTransform tr, final boolean affine, final double... values) {
        assertTrue("The transform shall be linear.", tr instanceof LinearTransform);
        final Matrix matrix = ((LinearTransform) tr).getMatrix();
        final int numRows = matrix.getNumRow();
        final int numCols = matrix.getNumCol();
        for (int j=0; j<numRows; j++) {
            for (int i=0; i<numCols; i++) {
                final double expected;
                if (affine && j == numRows-1) {
                    expected = (i == numCols-1) ? 1 : 0;
                } else if (i == j) {
                    expected = values[Math.min(values.length-1, i)];
                } else {
                    expected = 0;
                }
                assertEquals("matrix(" + j + ',' + i + ')', expected, matrix.getElement(j, i), EPS);
            }
        }
    }

    /**
     * Asserts that the WKT of the given object is equal to the expected one.
     *
     * @param object The object to format in <cite>Well Known Text</cite> format.
     * @param expected The expected text, or {@code null} if {@code object} is expected to be null.
     *        If non-null, the expected text can use the format produced by
     *        {@link org.geotoolkit.test.Tools#printAsJavaCode} for easier reading.
     */
    public static void assertWktEquals(final IdentifiedObject object, final String expected) {
        if (expected == null) {
            assertNull(object);
        } else {
            assertNotNull(object);
            final String wkt;
            if (isSingleLine(expected) && (object instanceof FormattableObject)) {
                wkt = ((FormattableObject) object).toWKT(FormattableObject.SINGLE_LINE);
            } else {
                wkt = object.toWKT();
            }
            assertMultilinesEquals(object.getName().getCode(), decodeQuotes(expected), wkt);
        }
    }

    /**
     * Returns {@code true} if the following string has no carriage return or line feed.
     *
     * @param  text The text to check.
     * @return {@code true} if the given text is a single line.
     */
    private static boolean isSingleLine(final String text) {
        return text.lastIndexOf('\n') < 0 && text.lastIndexOf('\r') < 0;
    }

    /**
     * Returns the class of the projection, or {@code null} if none.
     *
     * @param  crs The crs for which to get the projection class.
     * @return The class of the projection implementation, or {@code null} if none.
     *
     * @since 3.09
     */
    public static Class<? extends UnitaryProjection> getProjectionClass(final ProjectedCRS crs) {
        return getProjectionClass(crs.getConversionFromBase().getMathTransform());
    }

    /**
     * Returns the class of the projection, or {@code null} if none.
     * This method invokes itself recursively down the concatenated transforms tree.
     */
    private static Class<? extends UnitaryProjection> getProjectionClass(final MathTransform transform) {
        if (transform instanceof UnitaryProjection) {
            return ((UnitaryProjection) transform).getClass();
        }
        if (transform instanceof ConcatenatedTransform) {
            Class<? extends UnitaryProjection> candidate;
            candidate = getProjectionClass(((ConcatenatedTransform) transform).transform1);
            if (candidate != null) {
                return candidate;
            }
            candidate = getProjectionClass(((ConcatenatedTransform) transform).transform2);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }
}
