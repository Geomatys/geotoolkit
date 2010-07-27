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

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.image.io.plugin.NetcdfTestBase;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link NetcdfCRS} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.08
 */
public final class NetcdfCRSTest extends NetcdfTestBase {
    /**
     * Tests the creation of a geographic CRS.
     *
     * @throws IOException If an error occurred while reading the test file.
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
             * Check the CRS types.
             */
            assertTrue("Expected a Compound CRS.", crs instanceof CompoundCRS);
            final List<CoordinateReferenceSystem> components = ((CompoundCRS) crs).getComponents();
            assertEquals(3, components.size());
            assertTrue("Expected a Geographic CRS.", components.get(0) instanceof GeographicCRS);
            assertTrue("Expected a Vertical CRS.",   components.get(1) instanceof VerticalCRS);
            assertTrue("Expected a Temporal CRS.",   components.get(2) instanceof TemporalCRS);
            /*
             * Check the temporal CRS.
             */
            final DefaultTemporalCRS timeCS = DefaultTemporalCRS.wrap((TemporalCRS) components.get(2));
            assertEquals("Expected the 1950-01-01 origin", -20L * 365250 * 24 * 60 * 60,
                    timeCS.getDatum().getOrigin().getTime());
            /*
             * Check the grid geometry.
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

    /**
     * Tests whatever the patch provided by {@link NetcdfCRS.Temporal#complete}
     * is still necessary. If this method fails in a future version of the UCAR
     * library, then we may consider removing the patch.
     *
     * @throws IOException If an error occurred while reading the test file.
     */
    @Test
    public void testCoordinateAxis1DTimePatch() throws IOException {
        final NetcdfDataset data = NetcdfDataset.openDataset(getTestFile().getPath());
        try {
            final CoordinateSystem cs = data.getCoordinateSystems().get(0);
            boolean useFile = false;
            do {
                final NetcdfCRS crs;
                if (useFile) {
                    crs = NetcdfCRS.wrap(cs, data, null);
                } else {
                    crs = NetcdfCRS.wrap(cs);
                }
                final CoordinateAxis1D axis = ((NetcdfAxis) crs.getCoordinateSystem().getAxis(3)).delegate();
                assertEquals("CoordinateAxis1DTime check", useFile, axis instanceof CoordinateAxis1DTime);
            } while ((useFile = !useFile) == true);
        } finally {
            data.close();
        }
    }
}
