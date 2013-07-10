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
package org.geotoolkit.referencing.factory.epsg;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.opengis.util.FactoryException;
import org.geotoolkit.internal.sql.Dialect;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.internal.sql.DefaultDataSource;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the automatic installation of the EPSG database. Testers may want to clear
 * their {@code "Geotoolkit.org/EPSG"} directory before to run this test. This is not
 * done automatically because we don't want to recreate the ESPG database during every
 * build, since it is costly.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
@DependsOn(EpsgInstallerTest.class)
public final strictfp class AutomaticInstallationTest {
    /**
     * Tests the {@link ThreadedEpsgFactory#getDefaultURL()} method.
     * Since the both the Derby and HSQL drivers are available on the
     * classpath, the Derby driver should be given precedence.
     */
    @Test
    public void testDefaultURL() {
        final String url = ThreadedEpsgFactory.getDefaultURL();
        assertTrue(url, url.startsWith("jdbc:derby:"));
    }

    /**
     * Tests the {@link ThreadedEpsgFactory#getDefaultURL()} method without the
     * Derby driver. The method should fallback on the HSQL driver.
     *
     * @throws SQLException Should never happen.
     */
    @Test
    public void testDefaultURL_HSQL() throws SQLException {
        final Driver derbyDriver = DriverManager.getDriver("jdbc:derby:EPSG");
        assertNotNull(derbyDriver);
        DriverManager.deregisterDriver(derbyDriver);
        try {
            final String url = ThreadedEpsgFactory.getDefaultURL();
            assertTrue(url, url.startsWith(Dialect.HSQL.protocol));
        } finally {
            DriverManager.registerDriver(derbyDriver);
        }
        assertSame(derbyDriver, DriverManager.getDriver("jdbc:derby:EPSG"));
    }

    /**
     * Tests the creation of a {@link ThreadedEpsgFactory} using HSQL,
     * which should create a database if it doesn't already exist.
     *
     * @throws SQLException Should never happen.
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testInstall_HSQL() throws SQLException, FactoryException {
        final Driver derbyDriver = DriverManager.getDriver("jdbc:derby:EPSG");
        assertNotNull(derbyDriver);
        DriverManager.deregisterDriver(derbyDriver);
        try {
            testInstall("hsqldb");
        } finally {
            DriverManager.registerDriver(derbyDriver);
        }
        assertSame(derbyDriver, DriverManager.getDriver("jdbc:derby:EPSG"));
    }

    /**
     * Tests the creation of a {@link ThreadedEpsgFactory} using JavaDB,
     * which should create a database if it doesn't already exist.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testInstall() throws FactoryException {
        testInstall("derby");
    }

    /**
     * Implementation of the {@link #testInstall()} and similar methods.
     *
     * @param  driver The expected driver of the JDBC URL.
     * @throws FactoryException Should not happen.
     */
    private static void testInstall(final String driver) throws FactoryException {
        final ThreadedEpsgFactory factory = new ThreadedEpsgFactory();
        final DataSource datasource = factory.getDataSource();
        assertTrue(datasource instanceof DefaultDataSource);
        final String url = ((DefaultDataSource) datasource).url;
        if (datasource instanceof EmbeddedDataSource) {
            /*
             * Just make sure that the factory is the expected one and works. However we
             * can do that only if there is no Geotoolkit.org/EPSG/DataSource.properties
             * file redirecting the connection to an other database, for example a local
             * PostgreSQL database.
             */
            assertTrue(url, url.startsWith("jdbc:" + driver + ':'));
            assertNotNull(factory.createProjectedCRS("3395"));
        }
        factory.dispose(false);
    }
}
