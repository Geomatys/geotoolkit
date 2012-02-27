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
package org.geotoolkit.coverage.sql;

import java.util.Map;
import java.sql.SQLException;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link CategoryTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.09 (derived from Seagis)
 */
public final strictfp class CategoryTableTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public CategoryTableTest() {
        super(CategoryTable.class);
    }

    /**
     * Tests the {@link CategoryTable#getCategories} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testSelect() throws SQLException {
        final CategoryTable table = getDatabase().getTable(CategoryTable.class);
        final CategoryEntry entry = table.getCategories(FormatTableTest.TEMPERATURE);
        assertEquals("rainbow", entry.paletteName);
        final Map<Integer,Category[]> map = entry.categories;
        assertEquals("The format should define only one band.", 1, map.size());
        checkTemperatureCategories(map.get(1));
        table.release();
    }

    /**
     * Checks the categories in the band #1 of the
     * {@code "PNG Temperature [-3 … 32.25]°C"} format.
     */
    static void checkTemperatureCategories(final Category... categories) {
        assertNotNull("The band #1 should exists.", categories);
        assertEquals("The band is expected to have 2 categories.", 2, categories.length);
        assertEquals("Missing value", categories[0].getName().toString());
        assertEquals("Temperature",   categories[1].getName().toString());
    }
}
