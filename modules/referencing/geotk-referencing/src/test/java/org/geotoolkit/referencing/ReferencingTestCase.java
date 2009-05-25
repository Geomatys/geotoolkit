/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.io.wkt.FormattableObject;

import org.junit.Assert;
import static org.geotoolkit.test.Commons.*;


/**
 * Base class for referencing tests. Provides the assertions methods that we are
 * going to use.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public abstract class ReferencingTestCase extends Assert {
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
}
