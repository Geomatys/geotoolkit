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
package org.geotoolkit.referencing.factory.epsg;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.apache.sis.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.factory.wkt.PropertyAuthorityFactoryTest;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.Commons.*;


/**
 * Tests {@link FactoryUsingWKT}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.00
 *
 * @since 2.4
 */
@DependsOn(PropertyAuthorityFactoryTest.class)
public final strictfp class PropertyEpsgFactoryTest extends ReferencingTestBase {
    /**
     * The factory to test.
     */
    private PropertyEpsgFactory factory;

    /**
     * Gets the authority factory for ESRI.
     */
    @Before
    public void setUp() {
        factory = (PropertyEpsgFactory) AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG",
                new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyEpsgFactory.class));
    }

    /**
     * Tests the setting {@link Hints#CRS_AUTHORITY_EXTRA_DIRECTORY}.
     */
    @Test
    public void testCrsAuthorityExtraDirectoryHint() {
        Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyEpsgFactory.class);
        try {
           hints.put(Hints.CRS_AUTHORITY_EXTRA_DIRECTORY, "invalid");
           fail("Should of been tossed out as an invalid hint");
        } catch (IllegalArgumentException expected) {
            // This is the expected exception.
        }
        String directory = new File(".").getAbsolutePath();
        hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyEpsgFactory.class);
        hints.put(Hints.CRS_AUTHORITY_EXTRA_DIRECTORY, directory);
        // We can't do much more tests since the directory we have was arbitrary.
    }

    /**
     * Tests the vendor.
     */
    @Test
    public void testVendor() {
        final Citation vendor = factory.getVendor();
        assertNotNull(vendor);
        assertEquals("Geotoolkit.org", vendor.getTitle().toString());
    }

    /**
     * Tests the authority citation.
     */
    @Test
    public void testAuthority() {
        final Citation authority = factory.getAuthority();
        assertNotNull(authority);
//      assertEquals("European Petroleum Survey Group", authority.getTitle().toString());
        assertTrue (org.apache.sis.metadata.iso.citation.Citations.identifierMatches(authority, "EPSG"));
        assertFalse(org.apache.sis.metadata.iso.citation.Citations.identifierMatches(authority, "ESRI"));
        assertTrue(factory instanceof PropertyEpsgFactory);
    }

    /**
     * Tests the authority codes.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testAuthorityCodes() throws FactoryException {
        final Set<String> expected = new HashSet<>(4);
        assertTrue(expected.add("27572"));
        assertTrue(expected.add("3035"));
        Set<String> codes = factory.getAuthorityCodes(null);
        assertEquals(expected, codes);

        codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
        assertEquals(expected, codes);

        codes = factory.getAuthorityCodes(ProjectedCRS.class);
        assertEquals(expected, codes);

        codes = factory.getAuthorityCodes(GeographicCRS.class);
        assertTrue(codes.isEmpty());
    }

    /**
     * Tests the {@code 27572} code.
     *
     * @throws FactoryException If the CRS can't be created.
     */
    @Test
    public void test27572() throws FactoryException {
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("27572");
        assertTrue(crs instanceof ProjectedCRS);
        final Collection<Identifier> ids = crs.getIdentifiers();
        assertTrue (ids.contains(new NamedIdentifier(Citations.EPSG, "27572")));
        assertFalse(ids.contains(new NamedIdentifier(Citations.ESRI, "27572")));
        assertSame("Should be able to trim the authority namespace",
                crs, factory.createCoordinateReferenceSystem("EPSG:27572"));
    }

    /**
     * Tests the search of a factory using various hints.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testHints() throws FactoryException {
        /*
         * Firsts make sure that the factory is really handling axis as we expect.
         */
        CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("3035");
        CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(AxisDirection.NORTH, cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.EAST,  cs.getAxis(1).getDirection());
        /*
         * Now tests fetching factories...
         */
        assertEquals("Expected FORCE_LONGITUDE_FIRST_AXIS_ORDER hint set.", Boolean.FALSE,
                factory.getImplementationHints().get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));

        final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyEpsgFactory.class);
        assertSame("Expected same factory since we have not FORCE_LONGITUDE_FIRST_AXIS_ORDER.",
                factory, AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints));

        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        assertSame("Expected same factory since we have set the hint to its default value.",
                factory, AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints));

        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        final CRSAuthorityFactory xyFactory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        assertNotSame(factory, xyFactory);
        /*
         * This factory should force XY axis order.
         */
        crs = xyFactory.createCoordinateReferenceSystem("3035");
        cs = crs.getCoordinateSystem();
//      assertEquals(AxisDirection.EAST,  cs.getAxis(0).getDirection());
//      assertEquals(AxisDirection.NORTH, cs.getAxis(1).getDirection());
    }

    /**
     * Tests the {@link CRS#decode} method.
     *
     * @throws FactoryException If the CRS can't be created.
     */
    @Test
    public void testCrsDecode() throws FactoryException {
        CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("3035");
        if (isEpsgFactoryAvailable()) {
            /*
             * If an EPSG database is available, the referencing module will use it for
             * fetching the CRS (which is the intended behavior) instead than using the
             * PropertyEpsgFactory.
             */
//          assertNotSame(crs, CRS.decode("EPSG:3035"));
        } else {
//          assertSame(crs, CRS.decode("EPSG:3035"));
//          assertSame(crs, CRS.decode("EPSG:3035", false));
        }
        CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(AxisDirection.NORTH, cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.EAST,  cs.getAxis(1).getDirection());
        /*
         * Now expects XY axis order.
         */
        crs = CRS.decode("EPSG:3035", true);
        cs = crs.getCoordinateSystem();
//      assertEquals(AxisDirection.EAST,  cs.getAxis(0).getDirection());
//      assertEquals(AxisDirection.NORTH, cs.getAxis(1).getDirection());
    }
}
