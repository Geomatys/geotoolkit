/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
import java.util.Properties;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;

import org.postgresql.ds.PGSimpleDataSource;

import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.internal.io.Installation;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Base class for every tests requerying a connection to a coverage database.
 * This test requires a connection to a PostgreSQL database. In addition, some
 * test suite requires the test file to be present. See the following file for
 * more information:
 * <p>
 * <a href="https://raw.githubusercontent.com/Geomatys/geotoolkit/master/modules/coverage/geotk-coverage-sql/src/test/resources/Tests/README.html">About large test files</a>
 * <p>
 * This class inherits {@link ImageTestBase} for allowing the display of images
 * by the {@link #view(Coverage)} method if the {@link #viewEnabled} field is set
 * to {@code true}. Most subclasses does not need this feature. However since
 * coverages are the final purpose of {@code geotk-coverage-sql}, this functionality
 * is provided here.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.09 (derived from Seagis)
 */
public abstract strictfp class CatalogTestBase extends ImageTestBase {
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
     *
     * @param testing The class to be tested.
     */
    protected CatalogTestBase(final Class<?> testing) {
        super(testing);
    }

    /**
     * Returns the file which should contain the configuration parameters.
     * This method does not test is the file exist.
     */
    private static File getConfigurationFile() {
        return new File(Installation.TESTS.directory(true), "coverage-sql.properties");
    }

    /**
     * Returns {@code true} if the connection parameters are found,
     * in which case the test is presumed executable.
     *
     * @return {@code true} if the test is presumed executable.
     */
    protected static boolean canTest() {
        return getConfigurationFile().isFile();
    }

    /**
     * Creates the database when first needed.
     *
     * @return The database.
     */
    protected static synchronized SpatialDatabase getDatabase() {
        if (database == null) try {
            final File pf = getConfigurationFile();
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
     * @throws SQLException If an error occurred while closing the connection.
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
     * Asserts that the two given objects are approximatively equal.
     * See {@link ComparisonMode#APPROXIMATIVE} for more information.
     *
     * @param expected  The expected object.
     * @param actual    The actual object.
     *
     * @since 3.20
     */
    public static void assertEqualsApproximatively(final Object expected, final Object actual) {
        assertTrue(Utilities.deepEquals(expected, actual, ComparisonMode.DEBUG));
        assertTrue(Utilities.deepEquals(expected, actual, ComparisonMode.APPROXIMATIVE));
    }
}
