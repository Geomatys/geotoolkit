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
import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link GridGeometryTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 */
public final class GridGeometryTableTest extends CatalogTestBase {
    /**
     * The identifier of the geometry to be tested.
     * We use the Coriolis (IFREMER) layer.
     */
    public static final Integer CORIOLIS_ID = 200;

    /**
     * Tests the {@link GridGeometryTable#getEntry}.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testGetEntry() throws SQLException {
        final GridGeometryTable table = new GridGeometryTable(getDatabase());
        final GridGeometryEntry entry = table.getEntry(CORIOLIS_ID);
        assertEquals("horizontal SRID", 3395, entry.getHorizontalSRID());
        assertEquals("vertical SRID",   5714, entry.getVerticalSRID());

        final GridEnvelope gridRange = entry.geometry.getGridRange();
        assertEquals("Image width",  720, gridRange.getSpan(0));
        assertEquals("Image height", 499, gridRange.getSpan(1));
        assertEquals("Num. depths",   59, gridRange.getSpan(2));

        final GeographicBoundingBox box = entry.getGeographicBoundingBox();
        assertEquals("West bound",  -180, box.getWestBoundLongitude(), 1E-10);
        assertEquals("East bound",  +180, box.getEastBoundLongitude(), 1E-10);
        assertEquals("South bouth",  -77, box.getSouthBoundLatitude(), 0.5);
        assertEquals("North bound",  +77, box.getNorthBoundLatitude(), 0.5);

        final Envelope envelope = entry.geometry.getEnvelope();
        assertEquals("West bound",  -2.00E+7, envelope.getMinimum(0), 5E+5);
        assertEquals("East bound",  +2.00E+7, envelope.getMaximum(0), 5E+5);
        assertEquals("South bound", -1.38E+7, envelope.getMinimum(1), 5E+5);
        assertEquals("North bound", +1.38E+7, envelope.getMaximum(1), 5E+5);

        final Dimension size = entry.getSize();
        assertEquals("Image width",  720, size.width);
        assertEquals("Image height", 499, size.height);

        final double[] depths = entry.getVerticalOrdinates();
        assertNotNull("Expected an array of depths.", depths);
        assertEquals("Test the second depth.", 10, depths[1], 0.0);
        assertEquals("Test finding depth index.", 9, entry.indexOfNearestAltitude(100));

        final AffineTransform gridToCRS  = entry.gridToCRS;
        assertEquals("Scale X",      55659.75, gridToCRS.getScaleX(),     0.01);
        assertEquals("Scale Y",     -55381.10, gridToCRS.getScaleY(),     0.01);
        assertEquals("Translate X", -20037508, gridToCRS.getTranslateX(), 1.0);
        assertEquals("Translate Y",  13817585, gridToCRS.getTranslateY(), 1.0);
        assertEquals("Shear X",             0, gridToCRS.getShearX(),     0.0);
        assertEquals("Shear Y",             0, gridToCRS.getShearY(),     0.0);

        assertSame("Expected cached entry.", entry, table.getEntry(CORIOLIS_ID));
    }

    /**
     * Tests the {@link GridGeometryTable#find} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFind() throws SQLException {
        final GridGeometryTable table = new GridGeometryTable(getDatabase());
        final GridGeometryEntry entry = table.getEntry(CORIOLIS_ID);
        final double[] depths = entry.getVerticalOrdinates();

        assertEquals("Search the existing entry.", CORIOLIS_ID, table.find(entry.getSize(),
                entry.gridToCRS, entry.getHorizontalSRID(), depths, entry.getVerticalSRID()));

        assertNull("Wrong horizontal SRID.", table.find(entry.getSize(),
                entry.gridToCRS, 4326, depths, entry.getVerticalSRID()));

        depths[1] = 12.8; // Tries a non-existant altitude.
        assertNull("Wrong depth.", table.find(entry.getSize(),
                entry.gridToCRS, entry.getHorizontalSRID(), depths, entry.getVerticalSRID()));
    }

    /**
     * Tests the {@link GridGeometryTable#findOrCreate} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFindOrCreate() throws SQLException {
        final GridGeometryTable table = new GridGeometryTable(getDatabase());
        final GridGeometryEntry entry = table.getEntry(CORIOLIS_ID);
        final double[] depths = entry.getVerticalOrdinates();

        depths[1] = 12.8; // Non-existant altitude.
        final int id = table.findOrCreate(entry.getSize(), entry.gridToCRS,
                entry.getHorizontalSRID(), depths, entry.getVerticalSRID());
        assertFalse("Should not be the existing ID.", id == CORIOLIS_ID.intValue());
        assertEquals("Should find the existing entry.", Integer.valueOf(id),
                table.find(entry.getSize(), entry.gridToCRS,
                entry.getHorizontalSRID(), depths, entry.getVerticalSRID()));
        assertEquals("Should have deleted the entry.", 1, table.delete(id));
    }
}
