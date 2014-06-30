/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.web;

import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactory;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactoryTest;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assume.*;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.Commons.*;
import static org.geotoolkit.referencing.factory.web.HTTP_AuthorityFactory.forceAxisOrderHonoring;
import static org.geotoolkit.factory.AuthorityFactoryFinder.*;


/**
 * Tests the {@link HTTP_AuthorityFactory} class backed by WMS or AUTO factories.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @since 2.4
 */
@DependsOn(AllAuthoritiesFactoryTest.class)
public final strictfp class HTTP_AuthorityFactoryTest extends ReferencingTestBase {
    /**
     * Tests the {@link HTTP_AuthorityFactory#forceAxisOrderHonoring} method.
     */
    @Test
    public void testForceAxisOrderHonoring() {
        // The following are required for proper execution of the remaining of this test.
        assertNull(Hints.getSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        assertNull(Hints.getSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING));
        Hints hints = new Hints();
        assertTrue(hints.isEmpty());

        // Standard behavior should be to set FORCE_LONGITUDE_FIRST_AXIS_ORDER to false.
        assertFalse(forceAxisOrderHonoring(null,  "http"));
        assertFalse(forceAxisOrderHonoring(hints, "http"));

        try {
            // The hints should be ignored.
            Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
            assertFalse(forceAxisOrderHonoring(new Hints(), "http"));

            // The hints should be honored.
            Hints.putSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING, "http");
            assertTrue(forceAxisOrderHonoring(new Hints(), "http"));

            // The hints should be ignored.
            Hints.putSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING, "urn");
            assertFalse(forceAxisOrderHonoring(new Hints(), "http"));

            // The hints should be honored.
            Hints.putSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING, "http, urn");
            assertTrue(forceAxisOrderHonoring(new Hints(), "http"));

            // The hints should be honored.
            Hints.putSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING, "urn, http");
            assertTrue(forceAxisOrderHonoring(new Hints(), "http"));
        } finally {
            Hints.removeSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER);
            Hints.removeSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING);
        }
    }

    /**
     * Returns the factory for the given hints.
     */
    static HTTP_AuthorityFactory getFactory(final Hints hints) {
        return (HTTP_AuthorityFactory) getCRSAuthorityFactory("http://www.opengis.net", hints);
    }

    /**
     * Ensures that {@link FactoryFinder} returns the proper factory instance for the
     * given hints.
     */
    @Test
    public void testFactoryFinder() {
        final CRSAuthorityFactory defaultFactory = getFactory(null);
        final Hints hints = new Hints();
        assertSame("Asking for a factory with an empty set of hints should result in the default one.",
                defaultFactory, getFactory(hints));

        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        assertSame("By default, the factory should ignore the FORCE_LONGITUDE_FIRST_AXIS_ORDER hint.",
                defaultFactory, getFactory(hints));

        hints.put(Hints.FORCE_AXIS_ORDER_HONORING, "urn");
        assertSame("The factory should not be concerned by the URN factory configuration.",
                defaultFactory, getFactory(hints));

        hints.put(Hints.FORCE_AXIS_ORDER_HONORING, "http");
        final CRSAuthorityFactory xyFactory = getFactory(hints);
        assertNotSame("The factory should honors the XY axis order.", defaultFactory, xyFactory);
        assertFalse(xyFactory.equals(defaultFactory));
        assertFalse(defaultFactory.equals(xyFactory));

        assertSame("The factory should be cached.", xyFactory, getFactory(hints));
    }

    /**
     * Ensures that the backing factories contains some of the expected ones (CRS, AUTO2...),
     * and do not contains some that are not desired (URN...).
     */
    @Test
    public void testBackingFactories() {
        final Object af = getFactory(null).getImplementationHints().get(Hints.CRS_AUTHORITY_FACTORY);
        assertTrue(af instanceof AllAuthoritiesFactory);
        boolean foundCRS = false, foundAUTO = false;
        for (final AuthorityFactory factory : ((AllAuthoritiesFactory) af).getFactories()) {
            assertFalse(factory instanceof HTTP_AuthorityFactory);
            assertFalse(factory instanceof  URN_AuthorityFactory);
            if (factory instanceof WebCRSFactory) {
                foundCRS = true;
            }
            if (factory instanceof AutoCRSFactory) {
                foundAUTO = true;
            }
        }
        assertTrue(foundCRS);
        assertTrue(foundAUTO);
    }

    /**
     * Tests fetching a CRS from the "CRS" authority.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCRS() throws FactoryException {
        CRSAuthorityFactory factory = getFactory(null);
        GeographicCRS crs;
        try {
            crs = factory.createGeographicCRS("CRS:84");
            fail();
        } catch (NoSuchAuthorityCodeException exception) {
            // This is the expected exception.
            assertEquals("CRS:84", exception.getAuthorityCode());
        }
        crs = factory.createGeographicCRS("http://www.opengis.net/gml/srs/crs.xml#84");
        assertSame(crs, CRS.decode("http://www.opengis.net/gml/srs/crs.xml#84"));
        assertSame(crs, CRS.decode("CRS:84"));
        assertNotSame(crs, DefaultGeographicCRS.WGS84);
        assertEqualsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs, true);

        // Test CRS:83
        crs = factory.createGeographicCRS("http://www.opengis.net/gml/srs/crs.xml#83");
        assertSame(crs, CRS.decode("CRS:83"));
        assertNotDeepEquals(DefaultGeographicCRS.WGS84, crs);
    }

    /**
     * Tests identifier lookup. Note that a test involving the EPSG database if provided by
     * {@link org.geotoolkit.referencing.CRS_WithEpsgTest#testLookupIdentifierWithURN()}.
     * The later has the advantage of testing the concatenation of EPSG database version.
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.07
     */
    @Test
    public void testLookup() throws FactoryException {
        final CRSAuthorityFactory factory = getCRSAuthorityFactory("http://www.opengis.net", null);
        assertTrue("The correct working of IdentifiedObjects.lookupIdentifier(authority, crs) requires " +
                   "that the URN_AuthorityFactory can been found from the Citations.URN_OGC constant.",
                   Citations.identifierMatches(factory.getAuthority(), Citations.HTTP_OGC));
        assertTrue(factory instanceof AbstractAuthorityFactory);

        final IdentifiedObjectFinder finder = ((AbstractAuthorityFactory) factory)
                .getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        final CoordinateReferenceSystem crs =
                factory.createCoordinateReferenceSystem("http://www.opengis.net/gml/srs/crs.xml#84");
        assertEquals("CRS:84", IdentifiedObjects.getIdentifierOrName(crs));
        assertEquals("http://www.opengis.net/gml/srs/crs.xml#84", finder.findIdentifier(crs));
    }

    /**
     * Tests a URL in the EPSG namespace. This test requires the EPSG database to be available.
     * We just make sure that no exception has been thrown, and opportunistly compare the result
     * with the one produced by the {@link CRS} facade.
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.08
     */
    @Test
    public void testEPSG() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        final CRSAuthorityFactory factory = getCRSAuthorityFactory("http://www.opengis.net", null);
        CoordinateReferenceSystem crs;
        crs = factory.createGeographicCRS("http://www.opengis.net/gml/srs/epsg.xml#4326");
        assertNotNull(crs);
        assertSame(crs, CRS.decode("http://www.opengis.net/gml/srs/epsg.xml#4326"));
        assertSame(crs, CRS.decode("urn:x-ogc:def:crs:EPSG:4326"));
        assertSame(crs, CRS.decode("EPSG:4326"));
    }
}
