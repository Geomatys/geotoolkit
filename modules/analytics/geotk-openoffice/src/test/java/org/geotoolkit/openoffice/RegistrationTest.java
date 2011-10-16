/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.openoffice;

import java.sql.Connection;
import javax.sql.DataSource;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;

import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the reflexion methods used in {@link Registration}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public final strictfp class RegistrationTest {
    /**
     * Tests {@link Registration#setEpsgDataSource}.
     *
     * @throws Exception To many exceptions for declaring all of them.
     */
    @Test
    public void testSetEpsgDataSource() throws Exception {
        assertNull(Hints.getSystemDefault(Hints.EPSG_DATA_SOURCE));
        Registration.setEpsgDataSource(null);
        final DataSource ds = (DataSource) Hints.getSystemDefault(Hints.EPSG_DATA_SOURCE);
        assertNotNull(ds);
        /*
         * Try creating a CRS with the registered data source.
         */
        final Connection c = ds.getConnection();
        assertNotNull("No connection provided.", c);
        c.close();
        assertNotNull(CRS.decode("EPSG:4326"));
    }
}
