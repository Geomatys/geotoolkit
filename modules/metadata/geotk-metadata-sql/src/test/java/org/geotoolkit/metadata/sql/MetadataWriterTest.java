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
package org.geotoolkit.metadata.sql;

import java.util.Collection;
import java.sql.SQLException;
import org.postgresql.ds.PGSimpleDataSource;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;

import org.opengis.metadata.citation.ResponsibleParty;
import org.apache.sis.metadata.MetadataStandard;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.internal.referencing.PositionalAccuracyConstant;
import org.geotoolkit.internal.sql.DefaultDataSource;

import org.junit.*;
import static org.junit.Assert.*;
import static org.apache.sis.test.TestUtilities.getSingleton;


/**
 * Creates a metadata database, stores a few elements and read them back.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
public final strictfp class MetadataWriterTest {
    /**
     * The data source providing connections to the database.
     */
    private MetadataWriter source;

    /**
     * Runs all tests on JavaDB in the required order.
     *
     * @throws SQLException If an error occurred while writing or reading the database.
     */
    @Test
    @Ignore("Need to be revisited because of change in ISO standard.")
    public void testDerby() throws SQLException {
        final DefaultDataSource ds = new DefaultDataSource("jdbc:derby:memory:Test;create=true");
        source = new MetadataWriter(MetadataStandard.ISO_19115, ds, null);
        try {
            write();
            search();
            read();
        } finally {
            source.close();
            source = null;
            ds.shutdown();
        }
    }

    /**
     * Runs all tests on PostgreSQL in the required order. This test is disabled by default
     * because it requires manual setup of a test database.
     *
     * @throws SQLException If an error occurred while writing or reading the database.
     */
    @Test
    @Ignore
    public void testPostgreSQL() throws SQLException {
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("some.server.name");
        ds.setDatabaseName("metadata-test");
        ds.setUser("");
        ds.setPassword("");
        source = new MetadataWriter(MetadataStandard.ISO_19115, ds, "metadata");
        try {
            write();
            writeHierarchical();
            search();
            read();
        } finally {
            source.close();
            source = null;
        }
    }

    /**
     * Creates a new temporary database and write elements in it.
     *
     * @throws SQLException If an error occurred while writing or reading the database.
     */
    private void write() throws SQLException {
        assertEquals("OGC",   source.add(Citations.OGC));
        assertEquals("EPSG",  source.add(Citations.EPSG));
        assertEquals("Geotk", source.add(Citations.GEOTOOLKIT));
        assertEquals("JAI",   source.add(Citations.JAI));
    }

    /**
     * Writes more complex elements, which require the support of table inheritance.
     * PostgreSQL supports this feature but Derby does not.
     *
     * @throws SQLException If an error occurred while writing or reading the database.
     */
    private void writeHierarchical() throws SQLException {
        assertNotNull(source.add(PositionalAccuracyConstant.DATUM_SHIFT_APPLIED));
        assertNotNull(source.add(PositionalAccuracyConstant.DATUM_SHIFT_OMITTED));
    }

    /**
     * Searches known entries in the database.
     *
     * @throws SQLException If an error occurred while reading the database.
     */
    private void search() throws SQLException {
        assertNull(source.search(Citations.ORACLE));
        assertEquals("JAI",   source.search(Citations.JAI));
        assertEquals("EPSG",  source.search(Citations.EPSG));
        assertEquals("OGC",   source.search(Citations.OGC));
        assertEquals("Geotk", source.search(Citations.GEOTOOLKIT));
        assertNull(source.search(Citations.ESRI));
        assertNull(source.search(getSingleton(Citations.ESRI.getCitedResponsibleParties())));
        assertEquals("EPSG", source.search(getSingleton(Citations.EPSG.getCitedResponsibleParties())));
    }

    /**
     * Reads known entries in the database.
     *
     * @throws SQLException If an error occurred while reading the database.
     */
    private void read() throws SQLException {
        Citation c = source.getEntry(Citation.class, "EPSG");
        assertEquals("European Petroleum Survey Group", c.getTitle().toString());
        assertEquals("EPSG", extract(c.getAlternateTitles()).toString());
        assertEquals(PresentationForm.TABLE_DIGITAL, extract(c.getPresentationForms()));
        /*
         * Try an indirect dependency.
         */
        assertEquals("http://www.epsg.org", ((ResponsibleParty) extract(c.getCitedResponsibleParties()))
                .getContactInfo().getOnlineResource().getLinkage().toString());
        /*
         * Ask columns that are known to not exist.
         */
        assertNull(c.getCollectiveTitle());
        assertTrue(c.getDates().isEmpty());
        /*
         * Test the cache.
         */
        assertSame   (c, source.getEntry(Citation.class, "EPSG"));
        assertNotSame(c, source.getEntry(Citation.class, "OGC" ));
        /*
         * Should return the identifier with no search. Actually the real test is the call
         * to "proxy", since there is no way to ensure that the call to "search" tooks the
         * short path (except by looking at the debugger). But if "proxy" succeed, then
         * "search" should be okay.
         */
        assertEquals("EPSG", source.proxy (c));
        assertEquals("EPSG", source.search(c));
    }

    /**
     * Returns the single element in the given collection,
     * which is expected to be non-null.
     */
    private static <T> T extract(final Collection<T> collection) {
        assertNotNull("Collection can not be null.", collection);
        assertEquals("Expected a singleton.", 1, collection.size());
        final T element = collection.iterator().next();
        assertNotNull("Element can not be null.", element);
        return element;
    }
}
