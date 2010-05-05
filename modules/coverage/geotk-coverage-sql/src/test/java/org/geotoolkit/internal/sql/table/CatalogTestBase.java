/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;

import org.postgresql.ds.PGSimpleDataSource;

import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Base classe for every tests requerying a connection to a coverage database.
 * This test requires a connection to a PostgreSQL database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.09 (derived from Seagis)
 */
public class CatalogTestBase {
    /**
     * The connection to the database.
     */
    private static SpatialDatabase database;

    /**
     * The {@code "rootDirectory"} property, or {@code null} if undefined.
     */
    private static String rootDirectory;

    /**
     * For subclass constructors only.
     */
    protected CatalogTestBase() {
    }

    /**
     * Creates the database when first needed.
     *
     * @return The database.
     */
    protected static synchronized SpatialDatabase getDatabase() {
        if (database == null) try {
            final File pf = new File(Installation.TESTS.directory(true), "coverage-sql.properties");
            assumeTrue(pf.isFile()); // All tests will be skipped if the above resources is not found.
            final Properties properties = TestData.readProperties(pf);
            final PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setServerName(properties.getProperty("server"));
            ds.setDatabaseName(properties.getProperty("database"));
            /*
             * Use reflection for instantiating the database, because it is defined
             * as a package-privated class.
             */
            if (true) {
                final Class<? extends SpatialDatabase> databaseClass =
                        Class.forName("org.geotoolkit.coverage.sql.TableFactory").asSubclass(SpatialDatabase.class);
                final Constructor<? extends SpatialDatabase> c = databaseClass.getConstructor(DataSource.class, Properties.class);
                c.setAccessible(true);
                database = c.newInstance(ds, properties);
            } else {
                database = new SpatialDatabase(ds, properties);
            }
            rootDirectory = properties.getProperty(ConfigurationKey.ROOT_DIRECTORY.key);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError(e); // This will cause a JUnit failure.
        }
        return database;
    }

    /**
     * Closes the connection to the database.
     *
     * @throws SQLException If an error occured while closing the connection.
     */
    @AfterClass
    public static synchronized void shutdown() throws SQLException {
        final Database db = database;
        database = null;
        if (db != null) {
            db.reset();
        }
    }

    /**
     * Subclasses shall invoke this method if the remaining code in a method requires
     * the image files. Typically, this method is invoked right before the first call
     * to {@link org.geotoolkit.coverage.sql.GridCoverageEntry#read}.
     *
     * @since 3.10
     */
    protected static synchronized void requireImageData() {
        assertNotNull("This method can be invoked only after getDatabase().", database);
        assumeNotNull(rootDirectory);
    }

    /**
     * Returns the path to the given image. This method can be invoked only if
     * {@link #requireImageData()} has been successfully invoked.
     *
     * @param  path The path to the image, relative to the data root directory.
     * @return The absolute path to the image.
     *
     * @since 3.12
     */
    protected static synchronized File toImageFile(final String path) {
        assertNotNull("requireImageData() must be invoked first.", rootDirectory);
        final File file = new File(rootDirectory, path);
        assertTrue("Not a file: " + file, file.isFile());
        return file;
    }

    /**
     * Shows the given coverage for a few seconds.
     * This is used for debugging purpose only.
     *
     * @param coverage The coverage to show.
     */
    protected static void show(final GridCoverage2D coverage) {
        try {
            coverage.view(ViewType.RENDERED).show();
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }
}
