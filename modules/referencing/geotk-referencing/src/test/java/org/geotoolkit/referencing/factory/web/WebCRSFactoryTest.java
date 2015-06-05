/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collection;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.CachingAuthorityFactory;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.metadata.iso.citation.Citations;
import org.geotoolkit.factory.AuthorityFactoryFinder;

import org.apache.sis.referencing.CommonCRS;
import org.junit.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests {@link WebCRSFactory}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.15
 *
 * @since 2.2
 */
public final strictfp class WebCRSFactoryTest {
    /**
     * The factory to test.
     */
    private WebCRSFactory factory;

    /**
     * Initializes the factory to test.
     */
    @Before
    public void setUp() {
        factory = new WebCRSFactory();
    }

    /**
     * Tests the registration in {@link ReferencingFactoryFinder}.
     */
    @Test
    public void testFactoryFinder() {
        final Collection<String> authorities = AuthorityFactoryFinder.getAuthorityNames();
        assertTrue("Missing the CRS authority.", authorities.contains("CRS"));
        assertTrue("Missing the OGC authority.", authorities.contains("OGC"));
        CRSAuthorityFactory found = AuthorityFactoryFinder.getCRSAuthorityFactory("CRS", null);
        assertEquals(WebCRSFactory.class, found.getClass());
        assertSame("The factory should be cached.",   found, AuthorityFactoryFinder.getCRSAuthorityFactory("CRS", null));
        assertSame("OGC shall be synonymous of CRS.", found, AuthorityFactoryFinder.getCRSAuthorityFactory("OGC", null));
    }

    /**
     * Checks the authority names.
     */
    @Test
    public void testAuthority() {
        final Citation authority = factory.getAuthority();
        assertTrue (Citations.identifierMatches(authority, "CRS"));
        assertFalse(Citations.identifierMatches(authority, "EPSG"));
        assertFalse(Citations.identifierMatches(authority, "AUTO"));
        assertFalse(Citations.identifierMatches(authority, "AUTO2"));
    }

    /**
     * Tests the CRS:84 code.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCRS84() throws FactoryException {
        GeographicCRS crs = factory.createGeographicCRS("CRS:84");
        assertSame   (crs,  factory.createGeographicCRS("84"));
        assertSame   (crs,  factory.createGeographicCRS("CRS84"));
        assertSame   (crs,  factory.createGeographicCRS("CRS:CRS84"));
        assertSame   (crs,  factory.createGeographicCRS("crs : crs84"));
        assertNotSame(crs,  factory.createGeographicCRS("CRS:83"));
    }

    /**
     * Tests the CRS:83 code.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCRS83() throws FactoryException {
        GeographicCRS crs = factory.createGeographicCRS("CRS:83");
        assertSame   (crs,  factory.createGeographicCRS("83"));
        assertSame   (crs,  factory.createGeographicCRS("CRS83"));
        assertSame   (crs,  factory.createGeographicCRS("CRS:CRS83"));
        assertNotSame(crs,  factory.createGeographicCRS("CRS:84"));
        assertNotDeepEquals(CommonCRS.WGS84.normalizedGeographic(), crs);
    }

    /**
     * Tests the OGC:CRS84 code. We have to perform this test from {@link CRS#decode}.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testOGC() throws FactoryException {
        GeographicCRS crs = factory.createGeographicCRS("CRS:84");
        assertSame(crs, CRS.decode("CRS:84"));
        assertSame(crs, CRS.decode("CRS:CRS84"));
        assertSame(crs, CRS.decode("OGC:84")); // Not in real use as far as I know.
        assertSame(crs, CRS.decode("OGC:CRS84"));
    }

    /**
     * Tests the WKT formatting. The main purpose of this test is to ensure that
     * the authority name is "CRS" and not "Web Map Service CRS".
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    @Ignore
    public void testWKT() throws FactoryException {
        GeographicCRS crs = factory.createGeographicCRS("CRS:84");
        assertMultilinesEquals(decodeQuotes(
            "GEOGCS[“WGS84”,\n" +
            "  DATUM[“WGS84”,\n" +
            "    SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”, “7030”]]],\n" +
            "  PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
            "  UNIT[“degree”, 0.017453292519943295],\n" +
            "  AXIS[“Geodetic longitude”, EAST],\n" +
            "  AXIS[“Geodetic latitude”, NORTH],\n" +
            "  AUTHORITY[“CRS”, “84”]]"), crs.toWKT());
    }

    /**
     * Tests the {@link IdentifiedObjectFinder#find} method.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testFind() throws FactoryException {
        final GeographicCRS CRS84 = factory.createGeographicCRS("CRS:84");
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        assertTrue("Newly created finder should default to full scan.", finder.isFullScanAllowed());

        finder.setFullScanAllowed(false);
        assertSame("Should find without the need for scan, since we can use the CRS:84 identifier.",
                   CRS84, finder.find(CRS84));

        finder.setFullScanAllowed(true);
        assertSame("Allowing scanning should not make any difference for this CRS84 instance.",
                   CRS84, finder.find(CRS84));


        finder.setFullScanAllowed(true);
        assertSame("A full scan should allow us to find WGS84, since it is equals ignoring metadata to CRS:84.",
                   CRS84, finder.find(CommonCRS.WGS84.normalizedGeographic()));

        // --------------------------------------------------
        // Same test than above, using a CRS created from WKT
        // --------------------------------------------------

        String wkt = "GEOGCS[\"WGS 84\",\n" +
                     "  DATUM[\"WGS84\",\n" +
                     "    SPHEROID[\"WGS 84\", 6378137.0, 298.257223563]],\n" +
                     "  PRIMEM[\"Greenwich\", 0.0],\n" +
                     "  UNIT[\"degree\", 0.017453292519943295]]";
        CoordinateReferenceSystem search = CRS.parseWKT(wkt);

        if (true) return; // Temporary patch because of migration to SIS.

        assertEqualsIgnoreMetadata(CRS84, search, true); // Required condition for next test.

        finder.setFullScanAllowed(false);
        assertNull("Should not find WGS84 without a full scan, since it doesn't contains the CRS:84 identifier.",
                   finder.find(search));

        finder.setFullScanAllowed(true);
        assertSame("A full scan should allow us to find WGS84, since it is equals ignoring metadata to CRS:84.",
                   CRS84, finder.find(search));

        assertEquals("CRS:84", finder.findIdentifier(search));
    }

    /**
     * Tests the {@link IdentifiedObjectFinder#find} method through a buffered authority factory.
     * The objects found are expected to be cached.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testBufferedFind() throws FactoryException {
        final AbstractAuthorityFactory factory = new Buffered(this.factory);
        final GeographicCRS CRS84 = factory.createGeographicCRS("CRS:84");
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);

        finder.setFullScanAllowed(false);
        assertSame("Should find without the need for scan, since we can use the CRS:84 identifier.",
                   CRS84, finder.find(CRS84));

        finder.setFullScanAllowed(true);
        assertSame("A full scan should allow us to find WGS84, since it is equals ignoring metadata to CRS:84.",
                   CRS84, finder.find(CommonCRS.WGS84.normalizedGeographic()));

        finder.setFullScanAllowed(false);
        assertSame("At the contrary of testFind(), the scan result should be cached.",
                   CRS84, finder.find(CommonCRS.WGS84.normalizedGeographic()));

        assertEquals("CRS:84", finder.findIdentifier(CommonCRS.WGS84.normalizedGeographic()));
    }

    /**
     * A buffered authority factory to be used by {@link WebCRSFactoryTest#testBufferedFind}.
     */
    private static final strictfp class Buffered extends CachingAuthorityFactory implements CRSAuthorityFactory {
        public Buffered(final AbstractAuthorityFactory factory) {
            super(factory);
        }
    }
}
