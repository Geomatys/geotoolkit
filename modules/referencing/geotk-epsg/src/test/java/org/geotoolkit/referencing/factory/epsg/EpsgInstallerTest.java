/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.opengis.util.FactoryException;

import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.sql.Dialect;
import org.geotoolkit.internal.io.Installation;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link EpsgInstaller}. Every databases created by this test suite exists only in
 * memory. This class does not write anything to disk (except maybe some temporary files).
 * <p>
 * Current implementation merely tests that the operation success without any exception being thrown.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.05
 */
public final strictfp class EpsgInstallerTest {
    /**
     * Tests the creation of an EPSG database on HSQL. The test is available in two modes:
     * the database can be created in memory only, or it can be created on disk. We perform
     * the test on disk only occasionally.
     *
     * @throws FactoryException Should never happen.
     * @throws SQLException If an error occurred while accessing the database.
     * @throws IOException If an error occurred while reading or writing to disk.
     *
     * @since 3.10
     */
    @Test
    @Ignore
    public void testCreationOnHSQL() throws FactoryException, SQLException, IOException {
        try {
            // Need explicit registration as of HSQL 1.8.0.10.
            Class.forName(Dialect.HSQL.driverClass);
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(Dialect.HSQL.driverClass, e);
        }
        final EpsgInstaller installer = new EpsgInstaller();
        if (true) {
            installer.setDatabase(Dialect.HSQL.protocol + "mem:EPSG");
            final EpsgInstaller.Result result = installer.call();
            assertTrue(result.numRows > 0);
        } else {
            final File directory = new File(Installation.TESTS.validDirectory(true), "CRS");
            try {
                final File dbpath = new File(directory, "EPSG");
                final String databaseURL = Dialect.HSQL.createURL(dbpath);
                installer.setDatabase(databaseURL);
                assertFalse("Database exists?", installer.exists());
                final EpsgInstaller.Result result = installer.call();
                assertTrue(result.numRows > 0);
                /*
                 * Following test is costly because it reloads the database, and shutdowns it
                 * immediately. For the purpose of a test suite executed only occasionally,
                 * we ignore that issue for now.
                 */
                assertTrue("Database exists?", installer.exists());
                /*
                 * At this point the EPSG database has been fully created. Now tests the creation
                 * of a few CRS objects from it. Note that at the opposite of the test performed
                 * for the Derby database, we don't invoke 'useOriginalTableNames()' because the
                 * database that we just created has no schema.
                 */
                try (Connection connection = DriverManager.getConnection(databaseURL)) {
//                  final HsqlDialectEpsgFactory factory = new HsqlDialectEpsgFactory(null, connection);
//                  assertTrue(factory.createCoordinateReferenceSystem("4326") instanceof GeographicCRS);
//                  assertTrue(factory.createCoordinateReferenceSystem("7402") instanceof CompoundCRS);
                    Dialect.HSQL.shutdown(connection, null, false);
//                  factory.dispose(false);
                }
            } finally {
                TestData.deleteRecursively(directory);
            }
        }
    }
}
