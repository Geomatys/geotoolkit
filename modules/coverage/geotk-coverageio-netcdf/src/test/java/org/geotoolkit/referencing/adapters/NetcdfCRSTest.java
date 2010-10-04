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

import java.util.Date;
import java.util.List;
import java.io.IOException;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.util.Range;
import org.geotoolkit.test.Depend;
import org.geotoolkit.image.io.plugin.NetcdfTestBase;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.internal.image.io.IrregularAxesConverterTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link NetcdfCRS} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.08
 */
@Depend(IrregularAxesConverterTest.class)
public final class NetcdfCRSTest extends NetcdfTestBase {
    /**
     * Small tolerance factor for floating point comparison.
     */
    private static final double EPS = 1E-10;

    /**
     * Tests the creation of a geographic CRS.
     *
     * @throws IOException If an error occurred while reading the test file.
     * @throws TransformException Should not happen.
     */
    @Test
    public void testGeographicCRS() throws IOException, TransformException {
        final NetcdfDataset data = NetcdfDataset.openDataset(getTestFile().getPath());
        assertNotNull("NetcdfDataset shall not be null.", data);
        try {
            final List<CoordinateSystem> cs = data.getCoordinateSystems();
            assertNotNull("List of CoordinateSystem shall not be null.", cs);
            assertEquals("Expected exactly one CoordinateSystem.", 1, cs.size());
            assertValid(NetcdfCRS.wrap(cs.get(0)),             false, false);
            assertValid(NetcdfCRS.wrap(cs.get(0), data, null), false, true);
        } finally {
            data.close();
        }
    }

    /**
     * Tests the creation of a geographic CRS, which is then made regular.
     *
     * @throws IOException If an error occurred while reading the test file.
     * @throws TransformException Should not happen.
     *
     * @since 3.15
     */
    @Test
    public void testRegularCRS() throws IOException, TransformException {
        final NetcdfDataset data = NetcdfDataset.openDataset(getTestFile().getPath());
        try {
            final List<CoordinateSystem> cs = data.getCoordinateSystems();
            final NetcdfCRS geographic = NetcdfCRS.wrap(cs.get(0), data, null);
            final CoordinateReferenceSystem projected = geographic.regularize();
            assertValid(geographic, false, true);
            assertValid(projected , true,  true);
        } finally {
            data.close();
        }
    }

    /**
     * Run the test on the following NetCDF wrapper.
     *
     * @param crs The NetCDF wrapper to test.
     * @param isProjected {@code true} if the CRS axes are expected to be projected.
     * @param hasTimeAxis {@code true} if the 4th dimension is expected to wraps an
     *        instance of {@link CoordinateAxis1DTime}.
     */
    private static void assertValid(final CoordinateReferenceSystem crs, final boolean isProjected, final boolean hasTimeAxis)
            throws IOException, TransformException
    {
        assertEquals("The CRS shall be equals to itself.", crs, crs);
        assertFalse ("The CRS shall not be equals to null.", crs.equals(null));
        final org.opengis.referencing.cs.CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals("Expected a 4-dimensional CRS.", GRID_SIZE.length, cs.getDimension());
        assertExpectedAxes(cs, isProjected);
        for (int i=0; i<GRID_SIZE.length; i++) {
            /*
             * For each axis, check the consistency of ordinate values.
             */
            final CoordinateSystemAxis axis = cs.getAxis(i);
            assertTrue("Expected a discrete axis.", axis instanceof DiscreteCoordinateSystemAxis);
            final DiscreteCoordinateSystemAxis discreteAxis = (DiscreteCoordinateSystemAxis) axis;
            final int n = discreteAxis.length();
            assertEquals("Unexpected number of indices.", GRID_SIZE[i], n);
            final boolean isTimeAxis = (hasTimeAxis && i == 3);
            if (isTimeAxis) {
                final Date first = ((Date) discreteAxis.getOrdinateAt(0));
                final Date last  = (Date)  discreteAxis.getOrdinateAt(n-1);
                assertFalse("Inconsistent dates.", first.after(last));
            } else {
                final double minimum = axis.getMinimumValue();
                final double maximum = axis.getMaximumValue();
                final double first   = ((Number) discreteAxis.getOrdinateAt(0)).doubleValue();
                final double last    = ((Number) discreteAxis.getOrdinateAt(n-1)).doubleValue();
                assertTrue  ("Inconsistent first ordinate.", minimum <= first);
                assertTrue  ("Inconsistent last ordinate.",  maximum >= last);
                if (!isProjected) {
                    assertEquals("Inconsistent first ordinate.", minimum, first, EPS);
                    assertEquals("Inconsistent last ordinate.",  maximum, last,  EPS);
                }
            }
            final Range<?> r1 = discreteAxis.getOrdinateRangeAt(0);
            final Range<?> r2 = discreteAxis.getOrdinateRangeAt(n-1);
            if (n > 1) {
                assertFalse(r1.intersects(r2));
            }
            final Class<?> elementClass = isTimeAxis ? Date.class : Double.class;
            assertEquals(elementClass, r1.getElementClass());
            assertEquals(elementClass, r2.getElementClass());
        }
        /*
         * Check the CRS types. It should be a CompoundCRS. The first component shall be either
         * geographic and projected, and the last components shall be vertical and temporal.
         */
        assertTrue("Expected a Compound CRS.", crs instanceof CompoundCRS);
        final List<CoordinateReferenceSystem> components = ((CompoundCRS) crs).getComponents();
        assertEquals(3, components.size());
        if (isProjected) {
            assertTrue("Expected a Projected CRS.", components.get(0) instanceof ProjectedCRS);
        } else {
            assertTrue("Expected a Geographic CRS.", components.get(0) instanceof GeographicCRS);
        }
        assertTrue("Expected a Vertical CRS.",   components.get(1) instanceof VerticalCRS);
        assertTrue("Expected a Temporal CRS.",   components.get(2) instanceof TemporalCRS);
        /*
         * Check the epoch of the temporal CRS.
         */
        final DefaultTemporalCRS timeCS = DefaultTemporalCRS.wrap((TemporalCRS) components.get(2));
        assertEquals("Expected the 1950-01-01 origin", -20L * 365250 * 24 * 60 * 60,
                timeCS.getDatum().getOrigin().getTime());
        /*
         * Check the grid geometry.
         */
        assertTrue("Expected a grid geometry.", crs instanceof GridGeometry);
        final GridGeometry gg = (GridGeometry) crs;
        final GridEnvelope ge = gg.getGridRange();
        final int[] high = GRID_SIZE.clone();
        for (int i=0; i<high.length; i++) {
            high[i]--;
        }
        assertArrayEquals(new int[high.length], ge.getLow() .getCoordinateValues());
        assertArrayEquals(high,                 ge.getHigh().getCoordinateValues());
        final MathTransform gridToCRS = gg.getGridToCRS();
        if (!isProjected) {
            assertNull(gridToCRS);
        } else {
            // TODO: Test with a regular grid.
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
