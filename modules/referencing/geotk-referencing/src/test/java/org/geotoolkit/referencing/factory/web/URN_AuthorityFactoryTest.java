/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.test.Depend;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactoryTest;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.factory.AuthorityFactoryFinder.*;


/**
 * Tests the {@link URN_AuthorityFactory} class backed by WMS or AUTO factories.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
@Depend({AllAuthoritiesFactoryTest.class, HTTP_AuthorityFactoryTest.class})
public final class URN_AuthorityFactoryTest {
    /**
     * Makes sure that a singleton instance is registered.
     */
    @Test
    public void testRegistration() {
        String authority = "URN:OGC:DEF";
        final AuthorityFactory
                   factory = getCRSAuthorityFactory  (authority, null);
        assertSame(factory,  getCRSAuthorityFactory  (authority, null));
        assertSame(factory,  getCSAuthorityFactory   (authority, null));
        assertSame(factory,  getDatumAuthorityFactory(authority, null));
        /*
         * Tests the X-OGC namespace, which should be synonymous.
         */
        authority = "URN:X-OGC:DEF";
        assertSame(factory, getCRSAuthorityFactory  (authority, null));
        assertSame(factory, getCSAuthorityFactory   (authority, null));
        assertSame(factory, getDatumAuthorityFactory(authority, null));
    }

    /**
     * Ensures that the backing factories contains some of the expected ones (CRS, AUTO2...),
     * and do not contains some that are not desired (URN...). Actually this test is performed
     * in {@link HTTP_AuthorityFactoryTest}, so we just make sure that we get the same backing
     * factory.
     */
    @Test
    public void testBackingFactories() {
        AuthorityFactory factory = getCRSAuthorityFactory("urn:ogc:def", null);
        assertTrue(factory instanceof URN_AuthorityFactory);
        factory = (AuthorityFactory) ((URN_AuthorityFactory) factory)
                .getImplementationHints().get(Hints.CRS_AUTHORITY_FACTORY);

        final Object tested = HTTP_AuthorityFactoryTest.getFactory(null)
                .getImplementationHints().get(Hints.CRS_AUTHORITY_FACTORY);

        assertSame(tested, factory);
    }

    /**
     * Tests the CRS factory.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCRS() throws FactoryException {
        CRSAuthorityFactory factory = getCRSAuthorityFactory("URN:OGC:DEF", null);
        GeographicCRS crs;
        try {
            crs = factory.createGeographicCRS("CRS:84");
            fail();
        } catch (NoSuchAuthorityCodeException exception) {
            // This is the expected exception.
            assertEquals("CRS:84", exception.getAuthorityCode());
        }
        crs =           factory.createGeographicCRS("urn:ogc:def:crs:CRS:WMS1.3:84");
        assertSame(crs, factory.createGeographicCRS("urn:ogc:def:crs:CRS:1.3:84"));
        assertSame(crs, factory.createGeographicCRS("URN:OGC:DEF:CRS:CRS:1.3:84"));
        assertSame(crs, factory.createGeographicCRS("URN:OGC:DEF:CRS:CRS:84"));
        assertSame(crs, factory.createGeographicCRS("urn:x-ogc:def:crs:CRS:1.3:84"));
        assertSame(crs, CRS.decode("urn:ogc:def:crs:CRS:1.3:84"));
        assertSame(crs, CRS.decode("CRS:84"));
        assertNotSame(crs, DefaultGeographicCRS.WGS84);
        assertFalse(DefaultGeographicCRS.WGS84.equals(crs));
        assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));

        // Test CRS:83
        crs = factory.createGeographicCRS("urn:ogc:def:crs:CRS:1.3:83");
        assertSame(crs, CRS.decode("CRS:83"));
        assertFalse(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
    }

    /**
     * Tests fetching the URN authority when the "longitude first axis order" hint is set.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testWhenForceXY() throws FactoryException {
        try {
            // Intentionnaly put "http" and not "urn" below,
            // since we want to test this frequent setting.
            Hints.putSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING, "http");
            Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

            final URN_AuthorityFactory factory;
            factory = (URN_AuthorityFactory) getCRSAuthorityFactory("URN:OGC:DEF", null);
            assertFalse("URN_AuthorityFactory should have ignored the \"force XY\" hint.", Boolean.TRUE.equals(
                         factory.getImplementationHints().get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER)));

            CoordinateReferenceSystem crs;
            crs = factory.createCoordinateReferenceSystem("URN:OGC:DEF:CRS:CRS:84");
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
            final CoordinateReferenceSystem reference = crs;

            crs = CRS.decode("URN:OGC:DEF:CRS:CRS:84", true);
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
            assertSame(reference, crs);

            crs = CRS.decode("URN:OGC:DEF:CRS:CRS:84");
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
            assertSame(reference, crs);
        } finally {
            Hints.removeSystemDefault(Hints.FORCE_AXIS_ORDER_HONORING);
            Hints.removeSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER);
        }
    }
}
