/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.util.Version;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.web.URN_AuthorityFactory;

import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.geotoolkit.referencing.Commons.*;


/**
 * Tests the {@link org.geotoolkit.referencing.factory.URN_AuthorityFactory} with EPSG codes.
 *
 * @author Justin Deoliveira (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
public final strictfp class URN_EPSG_Test extends ReferencingTestBase {
    /**
     * A custom class for testing versioning.
     */
    private static final strictfp class Versioned extends URN_AuthorityFactory {
        Version lastVersion;

        @Override
        protected AuthorityFactory createVersionedFactory(final Version version) throws FactoryException {
            lastVersion = version;
            return super.createVersionedFactory(version);
        }
    }

    /**
     * Tests {@link AuthorityFactoryAdapter#isCodeMethodOverridden}.
     */
    @Test
    public void testMethodOverridden() {
        assumeTrue(isEpsgFactoryAvailable());

        final AuthorityFactoryAdapter test = new Versioned();
        assertTrue(test.isCodeMethodOverridden());
    }

    /**
     * Tests the 4326 code.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test4326() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        CoordinateReferenceSystem expected = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem actual   = CRS.decode("urn:ogc:def:crs:EPSG:6.8:4326");
        assertSame(expected, actual);
        actual = CRS.decode("urn:x-ogc:def:crs:EPSG:6.8:4326");
        assertSame(expected, actual);
        actual = CRS.decode("urn:ogc:def:crs:EPSG:6.11:4326");
        assertSame(expected, actual);
    }

    /**
     * Tests versioning.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testVersion() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        CoordinateReferenceSystem expected = CRS.decode("EPSG:4326");
        final String version = String.valueOf(CRS.getVersion("EPSG"));
        final String urn = "urn:ogc:def:crs:EPSG:" + version + ":4326";
        final Versioned test = new Versioned();
        final int failureCount = FallbackAuthorityFactory.getFailureCount();
        assertNull(test.lastVersion);
        assertSame(expected, test.createCoordinateReferenceSystem(urn));
        assertEquals(version, test.lastVersion.toString());
        assertEquals("Primary factory should not fail.",
                failureCount, FallbackAuthorityFactory.getFailureCount());

        test.lastVersion = null;
        assertSame(expected, test.createCoordinateReferenceSystem(urn));
        assertNull("Should not create a new factory.", test.lastVersion);
        assertEquals("Primary factory should not fail.",
                failureCount, FallbackAuthorityFactory.getFailureCount());

        assertSame(expected, test.createCoordinateReferenceSystem("urn:ogc:def:crs:EPSG:6.11:4326"));
        assertEquals("6.11", test.lastVersion.toString());
        assertEquals("Should use the fallback factory.",
                failureCount + 1, FallbackAuthorityFactory.getFailureCount());
    }
}
