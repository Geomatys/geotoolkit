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
package org.geotoolkit.internal.sql;

import java.io.File;
import java.sql.SQLException;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests the {@link Dialect} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.10)
 */
public final strictfp class DialectTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests the {@link Dialect#createURL(File)} method for the HSQL database.
     *
     * @throws SQLException Should never happen.
     */
    @Test
    public void testCreateURL() throws SQLException {
        final String databaseURL = Dialect.HSQL.createURL(new File("Geotoolkit.org/EPSG/HSQL").toPath());
        assertTrue(databaseURL, databaseURL.startsWith(Dialect.HSQL.protocol + "file:"));
        assertTrue(databaseURL, databaseURL.endsWith("/Geotoolkit.org/EPSG/HSQL"));
    }

    /**
     * Tests the {@link Dialect#getPath(String)} method for the HSQL database.
     */
    @Test
    public void testGetFile() {
        assertEquals(new File("Geotoolkit.org/EPSG/HSQL").toPath(),
                Dialect.HSQL.getPath("jdbc:hsqldb:file:Geotoolkit.org/EPSG/HSQL"));
        assertEquals(new File("Geotoolkit.org/EPSG/HSQL").toPath(),
                Dialect.HSQL.getPath("JDBC:HSQLDB:FILE:Geotoolkit.org/EPSG/HSQL"));
        assertEquals(new File("Geotoolkit.org/EPSG/HSQL").toPath(),
                Dialect.HSQL.getPath("jdbc:hsqldb:Geotoolkit.org/EPSG/HSQL"));
        assertNull(Dialect.HSQL.getPath("jdbc:hsqldb:mem:Geotoolkit.org/EPSG/HSQL"));
        assertNull(Dialect.HSQL.getPath("jdbc:derby:file:Geotoolkit.org/EPSG/HSQL"));
    }
}
