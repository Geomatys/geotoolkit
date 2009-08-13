/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.metadata.sql;

import java.io.File;
import java.util.Collection;
import java.sql.SQLException;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;

import org.geotoolkit.test.TestData;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.internal.jdbc.DefaultDataSource;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Creates a metadata database, stores a few elements and read them back.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
public final class MetadataWriterTest {
    /**
     * The data source providing connections to the database.
     */
    private MetadataWriter source;

    /**
     * Runs all tests in the required order.
     *
     * @throws SQLException If an error occured while writing or reading the database.
     */
    @Test
    public void test() throws SQLException {
        final File directory = new File(System.getProperty("java.io.tmpdir", "/tmp"), "Geotoolkit.org").getAbsoluteFile();
        final DefaultDataSource ds = new DefaultDataSource("jdbc:derby:" + directory.getPath().replace('\\','/') + ";create=true");
        source = new MetadataWriter(MetadataStandard.ISO_19115, ds, "public");
        try {
            write();
            search();
            read();
        } finally {
            source.close();
            source = null;
            ds.shutdown();
            assertTrue(TestData.deleteRecursively(directory));
        }
    }

    /**
     * Creates a new temporary database and write elements in it.
     *
     * @throws SQLException If an error occured while writing or reading the database.
     */
    private void write() throws SQLException {
        assertEquals("OGC",        source.add(Citations.OGC));
        assertEquals("EPSG",       source.add(Citations.EPSG));
        assertEquals("Geotoolkit", source.add(Citations.GEOTOOLKIT));
        assertEquals("JAI",        source.add(Citations.JAI));
    }

    /**
     * Searchs known entries in the database.
     *
     * @throws SQLException If an error occured while reading the database.
     */
    private void search() throws SQLException {
        assertNull(source.search(Citations.ORACLE));
        assertEquals("JAI",        source.search(Citations.JAI));
        assertEquals("EPSG",       source.search(Citations.EPSG));
        assertEquals("OGC",        source.search(Citations.OGC));
        assertEquals("Geotoolkit", source.search(Citations.GEOTOOLKIT));
        assertNull(source.search(Citations.ESRI));
        assertNull(source.search(DefaultResponsibleParty.ESRI));
        assertEquals("EPSG", source.search(DefaultResponsibleParty.EPSG));
    }

    /**
     * Reads known entries in the database.
     *
     * @throws SQLException If an error occured while reading the database.
     */
    private void read() throws SQLException {
        Citation c = source.getEntry(Citation.class, "EPSG");
        assertEquals("European Petroleum Survey Group", c.getTitle().toString());
        assertEquals("EPSG", extract(c.getAlternateTitles()).toString());
        assertEquals(PresentationForm.TABLE_DIGITAL, extract(c.getPresentationForms()));
        /*
         * Try an indirect dependency.
         */
        assertEquals("http://www.epsg.org", extract(c.getCitedResponsibleParties())
                .getContactInfo().getOnLineResource().getLinkage().toString());
        /*
         * Ask columns that are known to not exist.
         */
        assertNull(c.getCollectiveTitle());
        assertTrue(c.getDates().isEmpty());
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
