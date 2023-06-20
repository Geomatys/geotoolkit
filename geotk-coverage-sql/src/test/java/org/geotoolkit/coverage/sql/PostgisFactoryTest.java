/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.VerticalCRS;

import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.CommonCRS;

import org.junit.*;

import static org.junit.Assert.*;
import static org.apache.sis.referencing.IdentifiedObjects.getIdentifier;
import static org.geotoolkit.test.Assert.assertEqualsIgnoreMetadata;


/**
 * Tests {@link PostgisFactory}.
 * This tests assume a database named "SpatialMetadata" on the local machine with no password.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class PostgisFactoryTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests a few CRS using the test database of the {@code geotk-coverage-sql} module,
     * if this database is found.
     *
     * @throws FactoryException should not happen.
     * @throws SQLException if an error occurred while reading the PostGIS tables.
     */
    @Test
    @Ignore
    public void testUsingCoverageSQL() throws FactoryException, SQLException {
        final Connection connection = DatabaseTest.getCoverageDataSource().getConnection();
        final PostgisFactory factory = new PostgisFactory(connection);
        try {
            /*
             * Test general information.
             */
            assertEquals("EPSG", org.apache.sis.metadata.iso.citation.Citations.getIdentifier(factory.getAuthority()));
            assertTrue(factory.getBackingStoreDescription().contains("PostgreSQL"));
            /*
             * Test fetching a few CRS.
             */
            assertEquals("WGS 84", String.valueOf(factory.getDescriptionText("EPSG:4326")));

            final GeographicCRS geoCRS = factory.createGeographicCRS("EPSG:4326");
            assertEquals("EPSG:4326",    getIdentifier(geoCRS, Citations.EPSG).toString());
//          assertEquals("PostGIS:4326", getIdentifier(geoCRS, Citations.POSTGIS).toString());
            assertEqualsIgnoreMetadata(CommonCRS.WGS84.normalizedGeographic(), geoCRS, false);

            final ProjectedCRS projCRS = factory.createProjectedCRS("EPSG:3395");
            assertEquals("EPSG:3395",    getIdentifier(projCRS, Citations.EPSG).toString());
//          assertEquals("PostGIS:3395", getIdentifier(projCRS, Citations.POSTGIS).toString());
            assertEqualsIgnoreMetadata(CommonCRS.WGS84.normalizedGeographic(), projCRS.getBaseCRS(), false);

            final VerticalCRS vertCRS = factory.createVerticalCRS("EPSG:57150");
            assertEquals("EPSG:57150",   getIdentifier(vertCRS, Citations.EPSG).toString());
//          assertEquals("PostGIS:6000", getIdentifier(vertCRS, Citations.POSTGIS).toString());
            /*
             * Test the list of authority codes.
             */
            final Set<String> all        = factory.getAuthorityCodes(null);
            final Set<String> geographic = factory.getAuthorityCodes(GeographicCRS.class);
            final Set<String> projected  = factory.getAuthorityCodes(ProjectedCRS.class);
            final Set<String> vertical   = factory.getAuthorityCodes(VerticalCRS .class);
            assertTrue (all       .contains("4326"));
            assertTrue (all       .contains("3395"));
            assertTrue (geographic.contains("4326"));
            assertTrue (projected .contains("3395"));
            assertFalse(projected .contains("4326"));
            assertFalse(geographic.contains("3395"));
            assertTrue(all.containsAll(geographic));
            assertTrue(all.containsAll(projected));
            assertTrue(all.containsAll(vertical));
            assertTrue(Collections.disjoint(geographic, projected));
            assertTrue(Collections.disjoint(geographic, vertical));
            assertTrue(Collections.disjoint(projected,  vertical));
            /*
             * Test the cache.
             */
            assertSame(geoCRS,  factory.createGeographicCRS("EPSG:4326"));
            assertSame(projCRS, factory.createProjectedCRS ("EPSG:3395"));
            assertSame(vertCRS, factory.createVerticalCRS  ("EPSG:57150"));
        } finally {
            factory.dispose();
        }
        assertTrue("Connection should be closed.", connection.isClosed());
    }
}
