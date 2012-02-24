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

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ListIterator;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.internal.sql.table.QueryType.*;


/**
 * Tests {@code Query}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.09 (derived from Seagis)
 */
public final strictfp class QueryTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public QueryTest() {
        super(Query.class);
    }

    /**
     * Tests a case similar to the SQL query used in the format table.
     * This is a relatively simple case.
     *
     * @throws SQLException If the connection to the database failed.
     */
    @Test
    public void testSimple() throws SQLException {
        final Database database = getDatabase();
        final QueryType[] uses  = new QueryType[] {SELECT, LIST};
        final Query  query      = new Query(database, "Formats");
        final Column name       = query.addMandatoryColumn("name", SELECT, LIST, EXISTS);
        final Column plugin     = query.addOptionalColumn ("plugin",   null, uses);
        final Column packMode   = query.addOptionalColumn ("packMode", null, uses);

        final LocalCache lc = getDatabase().getLocalCache();
        synchronized (lc) {
            final String SQL = query.select(lc, SELECT);
            /*
             * Tests the dynamic creation of SQL statements. We try two use cases: one SELECT for
             * creating a new entry, and an other for checking only if at least one entry exists.
             */
            assertEquals(1, name    .indexOf(SELECT));
            assertEquals(2, plugin  .indexOf(SELECT));
            assertEquals(3, packMode.indexOf(SELECT));
            assertEquals(Arrays.asList(name, plugin, packMode), query.getColumns(SELECT));
            assertEquals("SELECT \"name\", \"plugin\", \"packMode\" FROM \"coverages\".\"Formats\"", SQL);
            assertEquals("SELECT \"name\" FROM \"coverages\".\"Formats\"", query.select(lc, EXISTS));
            trySelectStatement(SQL);
            /*
             * Tests the iterator over the columns that we declared at the beginning of this method.
             * This iterator should filter the returned values, in order to returns only the one
             * present for the kind of query we asked.
             */
            ListIterator<Column>  iterator = query.getColumns(SELECT).listIterator(2);
            assertTrue (          iterator.hasNext());
            assertSame (packMode, iterator.next());
            assertFalse(          iterator.hasNext());
            assertTrue (          iterator.hasPrevious());
            assertSame (packMode, iterator.previous());
            assertSame (plugin,   iterator.previous());
            assertSame (name,     iterator.previous());
            assertFalse(          iterator.hasPrevious());
            assertEquals(-1,      iterator.previousIndex());

            iterator = query.getColumns(EXISTS).listIterator();
            assertTrue (      iterator.hasNext());
            assertSame (name, iterator.next());
            assertFalse(      iterator.hasNext());
            /*
             * Tests a query involving a non-existent column. The Query class should detect
             * that this column does not exist and replace it by the default value.
             */
            final Column dummy = query.addOptionalColumn("dummy", 10, uses);
            final String SQL2 = query.select(lc, SELECT);
            assertEquals("SELECT \"name\", \"plugin\", \"packMode\", 10 AS \"dummy\" FROM \"coverages\".\"Formats\"", SQL2);
            assertEquals(4, dummy.indexOf(SELECT));
            trySelectStatement(SQL);
        }
    }

    /**
     * Tests a case similar to the SQL query used in the grid coverage table.
     * This is a more complex case involving joins.
     *
     * @throws SQLException If the connection to the database failed.
     */
    @Test
    public void testWithJoins() throws SQLException {
        final Database database = getDatabase();
        final QueryType[] uses  = new QueryType[] {SELECT};
        final Query  query      = new Query(database, "GridCoverages");
        final Column layer      = query.addForeignerColumn("layer",    "Series",       uses);
        final Column pathname   = query.addForeignerColumn("pathname", "Series",       uses);
        final Column filename   = query.addMandatoryColumn("filename",                 uses);
        final Column startTime  = query.addOptionalColumn ("startTime", null,          uses);
        final Column endTime    = query.addOptionalColumn ("endTime",   null,          uses);
        final Column width      = query.addForeignerColumn("width",  "GridGeometries", uses);
        final Column height     = query.addForeignerColumn("height", "GridGeometries", uses);
        final Column format     = query.addForeignerColumn("format", "Series",         uses);

        final LocalCache lc = getDatabase().getLocalCache();
        synchronized (lc) {
            final String SQL = query.select(lc, SELECT);
            assertEquals(1, layer    .indexOf(SELECT));
            assertEquals(2, pathname .indexOf(SELECT));
            assertEquals(3, filename .indexOf(SELECT));
            assertEquals(4, startTime.indexOf(SELECT));
            assertEquals(5, endTime  .indexOf(SELECT));
            assertEquals(6, width    .indexOf(SELECT));
            assertEquals(7, height   .indexOf(SELECT));
            assertEquals(8, format   .indexOf(SELECT));
            assertEquals(Arrays.asList(layer, pathname, filename, startTime, endTime, width, height, format),
                    query.getColumns(SELECT));
            assertEquals("SELECT \"layer\", \"pathname\", \"filename\", \"startTime\", \"endTime\", " +
                    "\"width\", \"height\", \"format\" FROM \"coverages\".\"GridCoverages\" " +
                    "JOIN \"coverages\".\"Series\" ON \"GridCoverages\".\"series\"=\"Series\".\"identifier\" " +
                    "JOIN \"coverages\".\"GridGeometries\" ON \"GridCoverages\".\"extent\"=\"GridGeometries\".\"identifier\"", SQL);
            trySelectStatement(SQL);
        }
    }

    /**
     * Tests a case involving parameters.
     *
     * @throws SQLException If the connection to the database failed.
     */
    @Test
    public void testParameters() throws SQLException {
        final Database database  = getDatabase();
        final QueryType[] uses   = new QueryType[] {SELECT, LIST};
        final Query     query    = new Query(database, "Categories");
        final Column    format   = query.addMandatoryColumn("format", SELECT, LIST, EXISTS);
        final Column    band     = query.addOptionalColumn ("band",   null, uses);
        final Column    colors   = query.addOptionalColumn ("colors", null, uses);
        final Parameter byFormat = new Parameter(query, format, SELECT, EXISTS);
        final Parameter byBand   = new Parameter(query, band, SELECT);
        byBand.setComparator("IS NULL OR >=");

        assertEquals(1, format  .indexOf(SELECT));
        assertEquals(1, format  .indexOf(EXISTS));
        assertEquals(2, band    .indexOf(SELECT));
        assertEquals(0, band    .indexOf(EXISTS));
        assertEquals(3, colors  .indexOf(SELECT));
        assertEquals(0, colors  .indexOf(EXISTS));
        assertEquals(1, byFormat.indexOf(SELECT));
        assertEquals(1, byFormat.indexOf(EXISTS));
        assertEquals(2, byBand  .indexOf(SELECT));
        assertEquals(0, byBand  .indexOf(EXISTS));
        assertEquals(Arrays.asList(format, band, colors), query.getColumns(SELECT));
        assertEquals(Arrays.asList(format),               query.getColumns(EXISTS));

        final LocalCache lc = getDatabase().getLocalCache();
        synchronized (lc) {
            String actual = query.select(lc, LIST);
            String expectedAll = "SELECT \"format\", \"band\", \"colors\" FROM \"coverages\".\"Categories\"";
            assertEquals(expectedAll, actual);
            trySelectStatement(actual);

            actual = query.select(lc, LIST);
            assertEquals(expectedAll, actual);

            actual = query.select(lc, SELECT);
            String expected = expectedAll + " WHERE (\"format\" = ?) AND (\"band\" IS NULL OR \"band\" >= ?)";
            assertEquals(expected, actual);

            actual = query.select(lc, EXISTS);
            expected = "SELECT \"format\" FROM \"coverages\".\"Categories\" WHERE (\"format\" = ?)";
            assertEquals(expected, actual);
        }
    }

    /**
     * Tries to executes the specified query statement and to read one row.
     *
     * @param  query the statement to test.
     * @throws SQLException if an query error occurred.
     */
    private static void trySelectStatement(final String query) throws SQLException {
        final LocalCache lc = getDatabase().getLocalCache();
        assertTrue("Lock is required.", Thread.holdsLock(lc));
        try (Statement s = lc.connection().createStatement();
             ResultSet r = s.executeQuery(query))
        {
            if (r.next()) {
                final ResultSetMetaData metadata = r.getMetaData();
                final int num = metadata.getColumnCount();
                for (int i=1; i<=num; i++) {
                    final String value = r.getString(i);
                    if (metadata.isNullable(i) == ResultSetMetaData.columnNoNulls) {
                        assertNotNull(value);
                    }
                }
            }
        }
    }
}
