/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.referencing.adapters;

import java.io.IOException;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.NetcdfDataset;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.image.io.plugin.NetcdfTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link NetcdfCRS} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 */
public final class NetcdfCRSTest extends NetcdfTestBase {
    /**
     * Tests the creation of a geographic CRS.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testGeographicCRS() throws IOException {
        final NetcdfDataset data = NetcdfDataset.openDataset(getTestFile().getPath());
        assertNotNull("NetcdfDataset shall not be null.", data);
        try {
            final List<CoordinateSystem> cs = data.getCoordinateSystems();
            assertNotNull("List of CoordinateSystem shall not be null.", cs);
            assertEquals(1, cs.size());
            final NetcdfCRS crs = NetcdfCRS.wrap(cs.get(0));
            assertEquals(crs, crs);
            assertFalse(crs.equals(null));
            /*
             * Check the axes and compare with the expected values.
             */
            final String[] names = {"longitude", "latitude", "depth", "time"};
            final String[] abbreviations = {"λ", "φ", "d", "t"};
            final AxisDirection[] directions = new AxisDirection[] {
                AxisDirection.EAST,
                AxisDirection.NORTH,
                AxisDirection.DOWN,
                AxisDirection.FUTURE
            };
            final Unit<?>[] units = new Unit<?>[] {
                NonSI.DEGREE_ANGLE,
                NonSI.DEGREE_ANGLE,
                SI.METRE,
                NonSI.DAY
            };
            assertTrue("Expected a Geographic CRS.", crs instanceof GeographicCRS);
            assertEquals("Expected a 4-dimensional CRS.", names.length, crs.getDimension());
            for (int i=0; i<names.length; i++) {
                final NetcdfAxis axis = crs.getAxis(i);
                assertEquals(names[i], axis.getName().getCode());
                assertEquals("NetCDF:" + names[i], axis.toString());
                assertEquals(abbreviations[i], axis.getAbbreviation());
                assertEquals(directions[i], axis.getDirection());
                assertEquals(units[i], axis.getUnit());
            }
            /*
             * Checks the grid geometry.
             */
            assertArrayEquals(new int[4], crs.getGridRange().getLow().getCoordinateValues());
            assertArrayEquals(new int[] {719, 498, 58, 0},
                    crs.getGridRange().getHigh().getCoordinateValues());
            final MathTransform gridToCRS = crs.getGridToCRS();
            // TODO: Test with a regular grid.
        } finally {
            data.close();
        }
    }
}
