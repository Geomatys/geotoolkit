/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.GeographicCRS;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link EpsgInstaller}. Current implementation merely tests
 * that the operation success without any exception being thrown.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
public final class EpsgInstallerTest {
    /**
     * Tests the creation of an EPSG database.
     *
     * @throws FactoryException Should never happen.
     * @throws SQLException Should never happen.
     */
    @Test
    public void testCreation() throws FactoryException, SQLException {
        final EpsgInstaller installer = new EpsgInstaller();
        installer.setDatabase("jdbc:derby:memory:EPSG;create=true");
        try {
            final EpsgInstaller.Result result = installer.call();
            assertTrue(result.numRows > 0);
            /*
             * At this point the EPSG database has been fully created.
             * Now test the creation of a few CRS objects from it.
             */
            final Connection connection = DriverManager.getConnection("jdbc:derby:memory:EPSG");
            final AnsiDialectEpsgFactory factory = new AnsiDialectEpsgFactory(null, connection);
            factory.setSchema("EPSG", true);
            factory.useOriginalTableNames();
            assertTrue(factory.createCoordinateReferenceSystem("4326") instanceof GeographicCRS);
            assertTrue(factory.createCoordinateReferenceSystem("7402") instanceof CompoundCRS);
            factory.dispose(false);
            connection.close();
        } finally {
            try {
                DriverManager.getConnection("jdbc:derby:memory:EPSG;shutdown=true");
            } catch (SQLException e) {
                // This is the expected exception.
                assertEquals("08006", e.getSQLState());
            }
        }
    }
}
