/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.sql;

import java.sql.SQLException;
import java.util.Map;

import org.geotoolkit.coverage.Category;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link CategoryTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public class CategoryTableTest extends CatalogTestBase {
    /**
     * The name of the quantitative category to be tested.
     */
    public static final String SAMPLE_NAME = "Temperature";

    /**
     * Tests the {@link CategoryTable#getCategories} method.
     *
     * @throws SQLException     If the test can't connect to the database.
     * @throws CatalogException Should never happen in normal test execution.
     */
    @Test
    public void testSelect() throws CatalogException, SQLException {
        final CategoryTable table = new CategoryTable(getDatabase());
        final Map<Integer,Category[]> map = table.getCategories("PNG Temperature [-3 … 32.25]°C");
        assertEquals("The format should define only one band.", 1, map.size());
        final Category[] entries = map.get(1);
        assertNotNull("The band #1 should exists.", entries);
        assertEquals("The band is expected to have 2 categories.", 2, entries.length);
        assertEquals("Missing value", entries[0].getName().toString());
        assertEquals(SAMPLE_NAME,     entries[1].getName().toString());
    }
}
