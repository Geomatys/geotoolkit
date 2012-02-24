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

import java.util.TreeSet;
import java.util.SortedSet;
import java.sql.SQLException;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link GridGeometryTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.10 (derived from Seagis)
 */
public final strictfp class GridGeometryTableTest extends CatalogTestBase {
    /**
     * The identifier of the geometry to be tested.
     * We use the Coriolis (IFREMER) layer.
     */
    public static final Integer CORIOLIS_ID = 200;

    /**
     * The identifier of the BlueMarble geometry.
     * Used for testing envelope rounding.
     */
    public static final Integer BLUEMARBLE_ID = 333;

    /**
     * Creates a new test suite.
     */
    public GridGeometryTableTest() {
        super(GridGeometryTable.class);
    }

    /**
     * Gets the BlueMarble geometry and check its envelope.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws TransformException Should not happen.
     */
    @Test
    public void testEnvelope() throws SQLException, TransformException {
        final GridGeometryTable table = getDatabase().getTable(GridGeometryTable.class);
        final GridGeometryEntry entry = table.getEntry(BLUEMARBLE_ID);
        assertEquals("horizontal SRID", 4326, entry.getHorizontalSRID());
        final Envelope envelope = entry.geometry.getEnvelope();
        /*
         * Following assertions require an exact match (tolerance == 0), which should work if
         * GeneralGridGeometry constructor invoked GeneralEnvelope.roundIfAlmostInteger(360, 16).
         */
        assertEquals(-180, envelope.getMinimum(0), 0);
        assertEquals(+180, envelope.getMaximum(0), 0);
        assertEquals( -90, envelope.getMinimum(1), 0);
        assertEquals( +90, envelope.getMaximum(1), 0);
        table.release();
    }

    /**
     * Tests the {@link GridGeometryTable#getEntry}.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws TransformException Should not happen.
     */
    @Test
    public void testGetEntry() throws SQLException, TransformException {
        final GridGeometryTable table = getDatabase().getTable(GridGeometryTable.class);
        final GridGeometryEntry entry = table.getEntry(CORIOLIS_ID);
        assertEquals("horizontal SRID", 3395, entry.getHorizontalSRID());
        assertEquals("vertical SRID",   5714, entry.getVerticalSRID());

        final GridEnvelope gridExtent = entry.geometry.getExtent();
        assertEquals("Image width",  720, gridExtent.getSpan(0));
        assertEquals("Image height", 499, gridExtent.getSpan(1));
        assertEquals("Num. depths",   59, gridExtent.getSpan(2));

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

        final Dimension size = entry.getImageSize();
        assertEquals("Image width",  720, size.width);
        assertEquals("Image height", 499, size.height);

        final double[] depths = entry.getVerticalOrdinates();
        assertNotNull("Expected an array of depths.", depths);
        assertEquals("Test the second depth.", 10, depths[1], 0.0);
        assertEquals("Test finding depth index.", 9, entry.indexOfNearestAltitude(100));
        final SortedSet<Number> ds = new TreeSet<>();
        for (final double depth : depths) {
            assertTrue(ds.add(depth));
        }
        checkCoriolisElevations(ds);

        final AffineTransform gridToCRS  = entry.gridToCRS;
        assertEquals("Scale X",      55659.75, gridToCRS.getScaleX(),     0.01);
        assertEquals("Scale Y",     -55381.10, gridToCRS.getScaleY(),     0.01);
        assertEquals("Translate X", -20037508, gridToCRS.getTranslateX(), 1.0);
        assertEquals("Translate Y",  13817585, gridToCRS.getTranslateY(), 1.0);
        assertEquals("Shear X",             0, gridToCRS.getShearX(),     0.0);
        assertEquals("Shear Y",             0, gridToCRS.getShearY(),     0.0);

        assertSame("Expected cached entry.", entry, table.getEntry(CORIOLIS_ID));
        table.release();
    }

    /**
     * Tests the {@link GridGeometryTable#find} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFind() throws SQLException {
        final GridGeometryTable table = getDatabase().getTable(GridGeometryTable.class);
        final GridGeometryEntry entry = table.getEntry(CORIOLIS_ID);
        final double[] depths = entry.getVerticalOrdinates();

        assertEquals("Search the existing entry.", CORIOLIS_ID, table.find(entry.getImageSize(),
                entry.gridToCRS, entry.getHorizontalSRID(), depths, entry.getVerticalSRID()));

        assertNull("Wrong horizontal SRID.", table.find(entry.getImageSize(),
                entry.gridToCRS, 4326, depths, entry.getVerticalSRID()));

        depths[1] = 12.8; // Tries a non-existent altitude.
        assertNull("Wrong depth.", table.find(entry.getImageSize(),
                entry.gridToCRS, entry.getHorizontalSRID(), depths, entry.getVerticalSRID()));
        table.release();
    }

    /**
     * Tests the {@link GridGeometryTable#findOrCreate} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFindOrCreate() throws SQLException {
        final GridGeometryTable table = getDatabase().getTable(GridGeometryTable.class);
        final GridGeometryEntry entry = table.getEntry(CORIOLIS_ID);
        final double[] depths = entry.getVerticalOrdinates();

        depths[1] = 12.8; // Non-existent altitude.
        final int id = table.findOrCreate(entry.getImageSize(), entry.gridToCRS,
                entry.getHorizontalSRID(), depths, entry.getVerticalSRID());
        assertFalse("Should not be the existing ID.", id == CORIOLIS_ID.intValue());
        assertEquals("Should find the existing entry.", Integer.valueOf(id),
                table.find(entry.getImageSize(), entry.gridToCRS,
                entry.getHorizontalSRID(), depths, entry.getVerticalSRID()));
        assertEquals("Should have deleted the entry.", 1, table.delete(id));
        table.release();
    }

    /**
     * Ensures that the given elevations from the Coriolis layer are equal to the expected values.
     */
    static void checkCoriolisElevations(final SortedSet<Number> elevations) {
        final Double[] expected = {
            5d, 10d, 20d, 30d, 40d, 50d, 60d, 80d, 100d, 120d, 140d, 160d, 180d, 200d, 220d, 240d,
            260d, 280d, 300d, 320d, 360d, 400d, 440d, 480d, 520d, 560d, 600d, 640d, 680d, 720d,
            760d, 800d, 840d, 880d, 920d, 960d, 1000d, 1040d, 1080d, 1120d, 1160d, 1200d, 1240d,
            1280d, 1320d, 1360d, 1400d, 1440d, 1480d, 1520d, 1560d, 1600d, 1650d, 1700d, 1750d,
            1800d, 1850d, 1900d, 1950d
        };
        assertArrayEquals(expected, elevations.toArray(new Double[elevations.size()]));
    }
}
