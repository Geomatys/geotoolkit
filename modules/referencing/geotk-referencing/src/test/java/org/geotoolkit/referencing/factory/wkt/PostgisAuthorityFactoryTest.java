/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;

import org.geotoolkit.test.Depend;
import org.geotoolkit.io.wkt.WKTFormatTest;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;
import org.postgresql.ds.PGSimpleDataSource;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Tests {@link PostgisAuthorityFactory}. This test case requires the test configuration
 * described in the {@code geotk-coverage-sql} module, otherwise the test will be skipped.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
@Depend(WKTFormatTest.class)
public class PostgisAuthorityFactoryTest {
    /**
     * Tests a few CRS using the test database of the {@code geotk-coverage-sql} module,
     * if this database is found.
     *
     * @throws FactoryException Should not happen.
     * @throws IOException If an error occured while reading the properties file.
     * @throws SQLException If an error occured while reading the PostGIS tables.
     */
    @Test
    public void testUsingCoverageSQL() throws FactoryException, IOException, SQLException {
        /*
         * Get the connection parameter.
         */
        final File pf = new File(Installation.TESTS.directory(true), "coverage-sql.properties");
        assumeTrue(pf.isFile()); // The test will be skipped if the above resource is not found.
        final Properties properties = new Properties();
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(pf));
        properties.load(in);
        in.close();
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName  (properties.getProperty("server"));
        ds.setDatabaseName(properties.getProperty("database"));
        ds.setUser        (properties.getProperty("user"));
        ds.setPassword    (properties.getProperty("password"));
        /*
         * Now test the factory.
         */
        final PostgisAuthorityFactory factory = new PostgisAuthorityFactory(null, ds.getConnection());
        try {
            /*
             * Test general information.
             */
            assertEquals("EPSG", Citations.getIdentifier(factory.getAuthority()));
            assertTrue(factory.getBackingStoreDescription().contains("PostgreSQL"));
            /*
             * Test fetching a few CRS.
             */
            assertEquals("WGS 84", String.valueOf(factory.getDescriptionText("EPSG:4326")));
            final GeographicCRS geoCRS = factory.createGeographicCRS("EPSG:4326");
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, geoCRS));
            final ProjectedCRS projCRS = factory.createProjectedCRS("EPSG:3395");
            assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, projCRS.getBaseCRS()));
            /*
             * Test the list of authority codes.
             */
            final Set<String> all        = factory.getAuthorityCodes(null);
            final Set<String> geographic = factory.getAuthorityCodes(GeographicCRS.class);
            final Set<String> projected  = factory.getAuthorityCodes(ProjectedCRS.class);
            assertTrue (all       .contains("4326"));
            assertTrue (all       .contains("3395"));
            assertTrue (geographic.contains("4326"));
            assertTrue (projected .contains("3395"));
            assertFalse(projected .contains("4326"));
            assertFalse(geographic.contains("3395"));
            assertTrue(all.containsAll(geographic));
            assertTrue(all.containsAll(projected));
            assertTrue(Collections.disjoint(geographic, projected));
        } finally {
            factory.dispose(false);
        }
    }
}
