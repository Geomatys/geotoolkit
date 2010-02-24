/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
 */
public class CategoryTableTest extends CatalogTestBase {
    /**
     * Tests the {@link CategoryTable#getCategories} method.
     *
     * @throws SQLException     If the test can't connect to the database.
     * @throws CatalogException Should never happen in normal test execution.
     */
    @Test
    public void testSelect() throws CatalogException, SQLException {
        final CategoryTable table = new CategoryTable(getDatabase());
        final Map<Integer,Category[]> map = table.getCategories(FormatTableTest.TEMPERATURE);
        assertEquals("The format should define only one band.", 1, map.size());
        checkTemperatureCategories(map.get(1));
    }

    /**
     * Checks the categories in the band #1 of the
     * {@code "PNG Temperature [-3 … 32.25]°C"} format.
     */
    static void checkTemperatureCategories(final Category[] categories) {
        assertNotNull("The band #1 should exists.", categories);
        assertEquals("The band is expected to have 2 categories.", 2, categories.length);
        assertEquals("Missing value", categories[0].getName().toString());
        assertEquals("Temperature",   categories[1].getName().toString());
    }
}
