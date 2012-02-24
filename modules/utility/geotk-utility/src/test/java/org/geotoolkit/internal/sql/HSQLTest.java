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

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests the {@link HSQL} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
public final strictfp class HSQLTest {
    /**
     * Tests the {@link HSQL#createURL} method.
     */
    @Test
    public void testCreateURL() {
        final String databaseURL = HSQL.createURL(new File("Geotoolkit.org/EPSG/HSQL"));
        assertTrue(databaseURL, databaseURL.startsWith(HSQL.PROTOCOL + "file:"));
        assertTrue(databaseURL, databaseURL.endsWith("/Geotoolkit.org/EPSG/HSQL"));
    }

    /**
     * Tests the {@link HSQL#getFile} method.
     */
    @Test
    public void testGetFile() {
        assertEquals(new File("Geotoolkit.org/EPSG/HSQL"),
                HSQL.getFile("jdbc:hsqldb:file:Geotoolkit.org/EPSG/HSQL"));
        assertEquals(new File("Geotoolkit.org/EPSG/HSQL"),
                HSQL.getFile("JDBC:HSQLDB:FILE:Geotoolkit.org/EPSG/HSQL"));
        assertEquals(new File("Geotoolkit.org/EPSG/HSQL"),
                HSQL.getFile("jdbc:hsqldb:Geotoolkit.org/EPSG/HSQL"));
        assertNull(HSQL.getFile("jdbc:hsqldb:mem:Geotoolkit.org/EPSG/HSQL"));
        assertNull(HSQL.getFile("jdbc:derby:file:Geotoolkit.org/EPSG/HSQL"));
    }
}
