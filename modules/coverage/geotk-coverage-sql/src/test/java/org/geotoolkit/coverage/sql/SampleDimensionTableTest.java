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

import java.sql.SQLException;
import javax.measure.unit.SI;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link SampleDimensionTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.09 (derived from Seagis)
 */
@DependsOn(CategoryTableTest.class)
public final strictfp class SampleDimensionTableTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public SampleDimensionTableTest() {
        super(SampleDimensionTable.class);
    }

    /**
     * Tests the {@link SampleDimensionTable#getSampleDimensions} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testSelect() throws SQLException {
        final SampleDimensionTable table = getDatabase().getTable(SampleDimensionTable.class);
        final CategoryEntry entry = table.getSampleDimensions(FormatTableTest.TEMPERATURE);
        assertEquals("rainbow", entry.paletteName);
        checkTemperatureDimension(entry.sampleDimensions);
        table.release();
    }

    /**
     * Checks the sample dimensions of the {@code "PNG Temperature [-3 … 32.25]°C"} format.
     */
    static void checkTemperatureDimension(final GridSampleDimension... dimensions) {
        assertNotNull("The SampleDimension array can't be null.", dimensions);
        assertEquals("The format should have exactly 1 band.", 1, dimensions.length);
        final GridSampleDimension dim = dimensions[0];
        assertEquals("SST [-3 … 32.25°C]", dim.getDescription().toString());
        assertEquals(SI.CELSIUS, dim.getUnits());
        CategoryTableTest.checkTemperatureCategories(dim.getCategories().toArray(new Category[0]));
    }
}
