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
package org.geotoolkit.internal.sql;

import java.sql.Statement;
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.internal.sql.IdentifierGenerator.SEPARATOR;


/**
 * Creates an empty database and insert automatically-generated key.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
public final strictfp class IdentifierGeneratorTest {
    /**
     * The name of the table to be created for testing purpose.
     */
    private static final String TABLE = "Dummy";

    /**
     * The generator being tested.
     */
    private IdentifierGenerator<?,?> generator;

    /**
     * A statement to be used for various usage.
     */
    private Statement stmt;

    /**
     * Tests the creation of identifiers with sequence numbers.
     *
     * @throws SQLException Should never happen.
     */
    @Test
    public void testSequence() throws SQLException {
        final DefaultDataSource ds = new DefaultDataSource("jdbc:derby:memory:Test;create=true");
        final StatementPool<String,StatementEntry> pool = new StatementPool<>(1, ds);
        synchronized (pool) {
            try {
                generator = new IdentifierGenerator.Simple(pool, "ID");
                stmt = pool.connection().createStatement();
                stmt.executeUpdate("CREATE TABLE \"" + TABLE + "\" (ID VARCHAR(6) NOT NULL PRIMARY KEY)");
                addRecords("TD", 324);
                removeAndAddRecords("TD");
                addRecords("OT", 30);
                stmt.executeUpdate("DROP TABLE \"" + TABLE + '"');
                stmt.close();
            } finally {
                generator = null;
                pool.close();
                ds.shutdown();
            }
        }
    }

    /**
     * Adds a single record.
     *
     * @param  prefix The prefix of the record to add.
     * @return The identifier of the record added.
     */
    private String addRecord(final String prefix) throws SQLException {
        final String identifier = generator.identifier(null, TABLE, prefix);
        assertEquals(1, stmt.executeUpdate("INSERT INTO \"" + TABLE + "\" VALUES ('" + identifier + "')"));
        return identifier;
    }

    /**
     * Tests the creation of identifiers with sequence numbers.
     *
     * @param prefix The prefix of the records to add.
     * @param count The number of records to add (in addition of the "main" one).
     */
    private void addRecords(final String prefix, final int count) throws SQLException {
        assertEquals("The very first record added should not have any suffix.", prefix, addRecord(prefix));
        for (int i=1; i<=count; i++) {
            assertEquals("Any record added after the first one should have a sequential number in suffix.",
                    prefix + SEPARATOR + i, addRecord(prefix));
        }
    }

    /**
     * Tries to remove a few pre-selected record, then add them again.
     */
    private void removeAndAddRecords(final String prefix) throws SQLException {
        assertEquals(5, stmt.executeUpdate("DELETE FROM \"" + TABLE + "\" WHERE " +
                "ID='" + prefix + SEPARATOR +   "4' OR " +
                "ID='" + prefix + SEPARATOR +  "12' OR " +
                "ID='" + prefix + SEPARATOR +  "32' OR " +
                "ID='" + prefix + SEPARATOR + "125' OR " +
                "ID='" + prefix + SEPARATOR + "224'"));
        assertEquals("12 is before 4 in alphabetical order.",    prefix+"-12",  addRecord(prefix));
        assertEquals("125 is next to 12 in alphabetical order.", prefix+"-125", addRecord(prefix));
        assertEquals("224 is before 32 in alphabetical order.",  prefix+"-224", addRecord(prefix));
        assertEquals("32 is before 4 in alphabetical order.",    prefix+"-32",  addRecord(prefix));
        assertEquals("4 is last in alphabetical order.",         prefix+"-4",   addRecord(prefix));
    }
}
