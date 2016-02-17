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
package org.geotoolkit.referencing.factory.wkt;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.VerticalCRS;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.io.wkt.WKTFormatTest;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.CommonCRS;

import org.junit.*;
import org.postgresql.ds.PGSimpleDataSource;

import static org.junit.Assume.*;
import static org.geotoolkit.referencing.Assert.*;
import static org.apache.sis.referencing.IdentifiedObjects.getIdentifier;


/**
 * Tests {@link DirectPostgisFactory}. This test case requires the test configuration
 * described in the {@code geotk-coverage-sql} module, otherwise the test will be skipped.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
@DependsOn(WKTFormatTest.class)
public final strictfp class DirectPostgisFactoryTest extends org.geotoolkit.test.TestBase {
    /**
     * Gets the connection parameters to the coverage database.
     */
    private static DataSource getCoverageDataSource() throws IOException {
        final File pf = new File(Installation.TESTS.directory(true), "coverage-sql.properties");
        assumeTrue(pf.isFile()); // The test will be skipped if the above resource is not found.
        final Properties properties = new Properties();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(pf))) {
            properties.load(in);
        }
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName  (properties.getProperty("server"));
        ds.setDatabaseName(properties.getProperty("database"));
        ds.setUser        (properties.getProperty("user"));
        ds.setPassword    (properties.getProperty("password"));
        return ds;
    }

    /**
     * Tests a few CRS using the test database of the {@code geotk-coverage-sql} module,
     * if this database is found.
     *
     * @throws FactoryException Should not happen.
     * @throws IOException If an error occurred while reading the properties file.
     * @throws SQLException If an error occurred while reading the PostGIS tables.
     */
    @Test
    @Ignore
    public void testUsingCoverageSQL() throws FactoryException, IOException, SQLException {
        final Connection connection = getCoverageDataSource().getConnection();
        final DirectPostgisFactory factory = new DirectPostgisFactory(null, connection);
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
            assertEquals("PostGIS:4326", getIdentifier(geoCRS, Citations.POSTGIS).toString());
            assertEqualsIgnoreMetadata(CommonCRS.WGS84.normalizedGeographic(), geoCRS, false);

            final ProjectedCRS projCRS = factory.createProjectedCRS("EPSG:3395");
            assertEquals("EPSG:3395",    getIdentifier(projCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:3395", getIdentifier(projCRS, Citations.POSTGIS).toString());
            assertEqualsIgnoreMetadata(CommonCRS.WGS84.normalizedGeographic(), projCRS.getBaseCRS(), false);

            final VerticalCRS vertCRS = factory.createVerticalCRS("EPSG:57150");
            assertEquals("EPSG:57150",   getIdentifier(vertCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:6000", getIdentifier(vertCRS, Citations.POSTGIS).toString());
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
        } finally {
            factory.dispose(false);
        }
        assertTrue("Connection should be closed.", connection.isClosed());
    }

    /**
     * Tests the wrapping of {@link DirectPostgisFactory} in {@link CachingPostgisFactory}.
     *
     * @throws FactoryException Should not happen.
     * @throws IOException If an error occurred while reading the properties file.
     */
    @Test
    @Ignore
    public void testCaching() throws FactoryException, IOException {
        final CachingPostgisFactory factory = new CachingPostgisFactory(getCoverageDataSource());
        try {
            /*
             * Test general information.
             */
            assertEquals("EPSG", org.apache.sis.metadata.iso.citation.Citations.getIdentifier(factory.getAuthority()));
            /*
             * Test fetching a few CRS.
             */
            assertEquals("WGS 84", String.valueOf(factory.getDescriptionText("EPSG:4326")));

            final GeographicCRS geoCRS = factory.createGeographicCRS("EPSG:4326");
            assertEquals("EPSG:4326",    getIdentifier(geoCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:4326", getIdentifier(geoCRS, Citations.POSTGIS).toString());
            assertEqualsIgnoreMetadata(CommonCRS.WGS84.normalizedGeographic(), geoCRS, false);

            final ProjectedCRS projCRS = factory.createProjectedCRS("EPSG:3395");
            assertEquals("EPSG:3395",    getIdentifier(projCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:3395", getIdentifier(projCRS, Citations.POSTGIS).toString());
            assertEqualsIgnoreMetadata(CommonCRS.WGS84.normalizedGeographic(), projCRS.getBaseCRS(), false);

            final VerticalCRS vertCRS = factory.createVerticalCRS("EPSG:57150");
            assertEquals("EPSG:57150",   getIdentifier(vertCRS, Citations.EPSG).toString());
            assertEquals("PostGIS:6000", getIdentifier(vertCRS, Citations.POSTGIS).toString());
            /*
             * Test the cache.
             */
            assertSame(geoCRS,  factory.createGeographicCRS("EPSG:4326"));
            assertSame(projCRS, factory.createProjectedCRS ("EPSG:3395"));
            assertSame(vertCRS, factory.createVerticalCRS  ("EPSG:57150"));
        } finally {
            factory.close();
        }
    }
}
