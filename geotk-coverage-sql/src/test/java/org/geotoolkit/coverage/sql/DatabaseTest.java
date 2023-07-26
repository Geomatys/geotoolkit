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
package org.geotoolkit.coverage.sql;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.opengis.util.FactoryException;
import org.postgresql.ds.PGSimpleDataSource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.internal.io.Installation;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests {@link Database}.
 * This tests assumes a database named "SpatialMetadata" on the local machine with no password.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class DatabaseTest {
    /**
     * Gets the connection parameters to the coverage database.
     */
    static DataSource getCoverageDataSource() {
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName("SpatialMetadata");
        return ds;
    }

    @Test
    @Ignore
    public void testProducts() throws FactoryException, SQLException, DataStoreException {
        final Database db = new Database(getCoverageDataSource(),
                Installation.TESTS.directory(true).resolve("coverage-sql"));
        try (Transaction t = new Transaction(db, db.source.getConnection())) {
            try (ProductTable products = new ProductTable(t)) {
                final ProductEntry entry = products.getEntry("SST (World - 8 days)");
                assertEquals(8*24*60*60, entry.temporalResolution.getSeconds());
                assertTrue(entry.components().isEmpty());
            }
        }
    }
}
