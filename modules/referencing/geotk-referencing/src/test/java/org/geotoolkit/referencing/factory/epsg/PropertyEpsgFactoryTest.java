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
import java.io.IOException;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;

import org.geotoolkit.factory.Hints;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.geotoolkit.referencing.factory.wkt.PropertyAuthorityFactoryTest;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assert.*;


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
    private final PropertyEpsgFactory factory;

    /**
     * Gets the authority factory for ESRI.
     */
    public PropertyEpsgFactoryTest() throws IOException {
        factory = new PropertyEpsgFactory();
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
        assertTrue (ids.contains(new ImmutableIdentifier(Citations.EPSG, "EPSG", "27572")));
        assertFalse(ids.contains(new ImmutableIdentifier(Citations.ESRI, "EPSG", "27572")));
        assertSame("Should be able to trim the authority namespace",
                crs, factory.createCoordinateReferenceSystem("EPSG:27572"));
    }
}
