/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Collection;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.factory.web.AutoCRSFactoryTest;
import org.geotoolkit.referencing.factory.web.WebCRSFactoryTest;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.internal.jdbc.DefaultDataSource;
import org.geotoolkit.factory.Hints;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link AllAuthoritiesFactory} implementation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 2.4
 */
@Depend({WebCRSFactoryTest.class, AutoCRSFactoryTest.class, AuthorityFactoryProxyTest.class})
public final class AllAuthoritiesFactoryTest {
    /**
     * Tests the {@link AllAuthoritiesFactory#getAuthorityCodes} method.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testAuthorityCodes() throws FactoryException {
        final CRSAuthorityFactory all = AllAuthoritiesFactory.getInstance(null);
        final Collection<String> codes = all.getAuthorityCodes(CoordinateReferenceSystem.class);
        assertFalse(codes.isEmpty());
        assertTrue(codes.contains("CRS:84"));
        assertTrue(codes.contains("AUTO:42001") || codes.contains("AUTO2:42001"));
    }

    /**
     * Tests the {@link AllAuthoritiesFactory#createCoordinateReferenceSystem} method.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCreateCRS() throws FactoryException {
        final CRSAuthorityFactory auto = AuthorityFactoryFinder.getCRSAuthorityFactory("AUTO", null);
        final CRSAuthorityFactory crs  = AuthorityFactoryFinder.getCRSAuthorityFactory("CRS",  null);
        final CRSAuthorityFactory all  = AllAuthoritiesFactory.getInstance(null);
        CoordinateReferenceSystem actual, expected;

        actual   = all.createCoordinateReferenceSystem("CRS:84");
        expected = crs.createCoordinateReferenceSystem(    "84");
        assertSame(expected, actual);
        assertSame(expected, all.createObject("CRS:84"));

        actual   = all .createCoordinateReferenceSystem("AUTO:42001,0,0");
        expected = auto.createCoordinateReferenceSystem(     "42001,0,0");
        assertSame(expected, actual);
        assertSame(expected, all.createObject("AUTO:42001,0,0"));

        actual   = all.createCoordinateReferenceSystem("CRS:27");
        expected = crs.createCoordinateReferenceSystem(    "27");
        assertSame(expected, actual);
        assertSame(expected, all.createObject("CRS:27"));

        try {
            all.createCoordinateReferenceSystem("84");
            fail("Should not work without authority.");
        } catch (NoSuchAuthorityCodeException exception) {
            // This is the expected exception.
            assertEquals("84", exception.getAuthorityCode());
        }

        try {
            all.createCoordinateReferenceSystem("FOO:84");
            fail("Should not work with unknown authority.");
        } catch (NoSuchAuthorityCodeException exception) {
            // This is the expected exception.
            assertEquals("FOO", exception.getAuthority());
        }
    }

    /**
     * Tests the {@code "http://www.opengis.net/gml/srs/"} name space. This requires special
     * processing by {@link AllAuthoritiesFactory}, since the separator character is not the
     * usual {@code ':'}.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testHttp() throws FactoryException {
        final CRSAuthorityFactory crs = AuthorityFactoryFinder.getCRSAuthorityFactory("CRS", null);
        final CRSAuthorityFactory all = AllAuthoritiesFactory.getInstance(null);
        CoordinateReferenceSystem actual, expected;

        actual   = all.createCoordinateReferenceSystem("http://www.opengis.net/gml/srs/CRS#84");
        expected = crs.createCoordinateReferenceSystem("84");
        assertSame(expected, actual);

        actual = all.createCoordinateReferenceSystem("HTTP://WWW.OPENGIS.NET/GML/SRS/crs#84");
        assertSame(expected, actual);

        actual = all.createCoordinateReferenceSystem("http://www.opengis.net/gml/srs/CRS.xml#84");
        assertSame(expected, actual);

        try {
            all.createCoordinateReferenceSystem("http://www.dummy.net/gml/srs/CRS#84");
            fail("Expected a NoSuchAuthorityCodeException");
        } catch (NoSuchAuthorityCodeException e) {
            assertEquals("http://www.dummy.net", e.getAuthority());
        }

        try {
            all.createCoordinateReferenceSystem("http://www.opengis.net/gml/dummy/CRS#84");
            fail("Expected a NoSuchAuthorityCodeException");
        } catch (NoSuchAuthorityCodeException e) {
            assertEquals("http://www.opengis.net/gml/srs/", e.getAuthority());
        }
    }

    /**
     * Tests the {@link IdentifiedObjectFinder#find} method.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testFind() throws FactoryException {
        final AbstractAuthorityFactory all = AllAuthoritiesFactory.getInstance(null);
        final IdentifiedObjectFinder finder = all.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        finder.setFullScanAllowed(false);
        assertNull("Should not find the CRS without a scan.", finder.find(DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(true);
        final IdentifiedObject find = finder.find(DefaultGeographicCRS.WGS84);
        assertNotNull("With scan allowed, should find the CRS.", find);
        assertTrue(CRS.equalsIgnoreMetadata(find, DefaultGeographicCRS.WGS84));
        assertSame(all.createCoordinateReferenceSystem("CRS:84"), find);
        assertEquals("CRS:84", finder.findIdentifier(DefaultGeographicCRS.WGS84));
    }

    /**
     * Tests that an appropriate error message is produced when the EPSG database is
     * not available. The purpose of this test is to ensure that the message contains
     * enough information for a diagnostic of the problem.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testConnectionFailure() throws FactoryException {
        final Hints hints = new Hints(Hints.EPSG_DATA_SOURCE, new DefaultDataSource("jdbc:inexistant:dummy"));
        hints.put(Hints.CRS_AUTHORITY_FACTORY, ThreadedAuthorityFactory.class);
        AllAuthoritiesFactory factory = AllAuthoritiesFactory.getInstance(hints);
        assertTrue("Because we asked for an inexistant driver, and because we have specified " +
                "a hint that exclude other factories (WMS, etc.), we should get an empty set.",
                factory.getFactories().isEmpty());
        try {
            assertNotNull(factory.createGeographicCRS("EPSG:4326"));
            fail("The \"inexistant\" JDBC driver should not be found.");
        } catch (NoSuchAuthorityCodeException e) {
            // This is the expected exception. This exception is expected to have a cause.
            Throwable cause = e.getCause();
            assertTrue(cause instanceof FactoryNotFoundException);
        }
    }
}
