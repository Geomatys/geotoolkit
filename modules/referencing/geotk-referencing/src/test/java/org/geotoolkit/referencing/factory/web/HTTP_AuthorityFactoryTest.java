/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.web;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactory;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactoryTest;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.Hints;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.factory.web.HTTP_AuthorityFactory.forceAxisOrderHonoring;


/**
 * Tests the {@link HTTP_AuthorityFactory} class backed by WMS or AUTO factories.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
@Depend(AllAuthoritiesFactoryTest.class)
public final class HTTP_AuthorityFactoryTest {
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
    static final HTTP_AuthorityFactory getFactory(final Hints hints) {
        return (HTTP_AuthorityFactory) AuthorityFactoryFinder.getCRSAuthorityFactory("http://www.opengis.net", hints);
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
        assertFalse(DefaultGeographicCRS.WGS84.equals(crs));
        assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));

        // Test CRS:83
        crs = factory.createGeographicCRS("http://www.opengis.net/gml/srs/crs.xml#83");
        assertSame(crs, CRS.decode("CRS:83"));
        assertFalse(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
    }
}
