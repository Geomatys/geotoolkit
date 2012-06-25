/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.datum.PixelInCell;

import org.geotoolkit.util.Range;
import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.cs.DiscreteReferencingFactory;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.internal.image.io.IrregularAxesConverterTest;
import org.geotoolkit.referencing.operation.matrix.Matrices;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.Double.NaN;
import static org.geotoolkit.image.io.plugin.CoriolisFormatTest.GRID_SIZE;
import static org.geotoolkit.image.io.plugin.CoriolisFormatTest.getTestFile;
import static org.geotoolkit.image.io.plugin.CoriolisFormatTest.assertExpectedAxes;


/**
 * Tests the {@link NetcdfCRS} class using the same test file than {@link CoriolisFormatTest}.
 * In addition, this class inherits all tests defined in the {@code geoapi-netcdf} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.08
 */
@Depend(IrregularAxesConverterTest.class)
public final strictfp class NetcdfCRSTest extends org.opengis.wrapper.netcdf.NetcdfCRSTest {
    /**
     * Small tolerance factor for floating point comparison.
     */
    private static final double EPS = 1E-10;

    /**
     * Wraps the given NetCDF file into the CRS object to test.
     * This method is invoked by the tests inherited from the {@code geoapi-test} module.
     *
     * @param  cs   The NetCDF coordinate system to wrap.
     * @param  file The originating dataset file, or {@code null} if none.
     * @return A CRS implementation created from the given NetCDF coordinate system.
     * @throws IOException If an error occurred while wrapping the given NetCDF coordinate system.
     */
    @Override
    protected CoordinateReferenceSystem wrap(final CoordinateSystem cs, final NetcdfDataset file) throws IOException {
        return NetcdfCRS.wrap(cs, file, null);
    }

    /**
     * Tests the creation of a geographic CRS from the Coriolis format.
     * This is a "geographic" CRS with non-regular axes.
     *
     * @throws IOException If an error occurred while reading the test file.
     */
    @Test
    public void testCoriolisCRS() throws IOException {
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
     *
     * @since 3.15
     */
    @Test
    public void testRegularCRS() throws IOException {
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
    private static void assertValid(final CoordinateReferenceSystem crs, final boolean isProjected, final boolean hasTimeAxis) {
        final CoordinateReferenceSystem NULL = null; // Only for avoiding a NetBeans warning.
        assertEquals("The CRS shall be equals to itself.", crs, crs);
        assertFalse ("The CRS shall not be equals to null.", crs.equals(NULL));
        assertValidAxes(crs.getCoordinateSystem(), isProjected, hasTimeAxis);
        assertValidGridGeometry(crs, isProjected);
    }

    /**
     * Checks that the given coordinate system has the expected axes.
     *
     * @param cs The coordinate system to test.
     * @param isProjected {@code true} if the CRS axes are expected to be projected.
     * @param hasTimeAxis {@code true} if the 4th dimension is expected to wraps an
     *        instance of {@link CoordinateAxis1DTime}.
     */
    private static void assertValidAxes(final org.opengis.referencing.cs.CoordinateSystem cs,
            final boolean isProjected, final boolean hasTimeAxis)
    {
        assertEquals("Expected a 4-dimensional CRS.", GRID_SIZE.length, cs.getDimension());
        assertExpectedAxes(cs, isProjected);
        for (int i=0; i<GRID_SIZE.length; i++) {
            /*
             * For each axis, check the consistency of ordinate values.
             */
            final CoordinateSystemAxis axis = cs.getAxis(i);
            assertTrue("Expected a discrete axis.", axis instanceof DiscreteCoordinateSystemAxis<?>);
            final DiscreteCoordinateSystemAxis<?> discreteAxis = (DiscreteCoordinateSystemAxis<?>) axis;
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
    }

    /**
     * Checks that the given CRS has the expected grid geometry.
     *
     * @param crs The NetCDF wrapper to test.
     * @param isProjected {@code true} if the CRS axes are expected to be projected.
     */
    private static void assertValidGridGeometry(final CoordinateReferenceSystem crs, final boolean isProjected) {
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
        assertTrue("Expected a Vertical CRS.", components.get(1) instanceof VerticalCRS);
        assertTrue("Expected a Temporal CRS.", components.get(2) instanceof TemporalCRS);
        /*
         * Check the epoch of the temporal CRS.
         */
        final DefaultTemporalCRS timeCS = DefaultTemporalCRS.castOrCopy((TemporalCRS) components.get(2));
        assertEquals("Expected the 1950-01-01 origin", -20L * 365250 * 24 * 60 * 60,
                timeCS.getDatum().getOrigin().getTime());
        /*
         * Check the grid geometry.
         */
        assertTrue("Expected a grid geometry.", crs instanceof GridGeometry);
        final GridGeometry gg = (GridGeometry) crs;
        final GridEnvelope ge = gg.getExtent();
        final int[] high = GRID_SIZE.clone();
        for (int i=0; i<high.length; i++) {
            high[i]--;
        }
        assertArrayEquals(new int[high.length], ge.getLow() .getCoordinateValues());
        assertArrayEquals(high,                 ge.getHigh().getCoordinateValues());
        final MathTransform gridToCRS = gg.getGridToCRS();
        final Matrix matrix = DiscreteReferencingFactory.getAffineTransform(crs);
        if (isProjected) {
            assertTrue(gridToCRS instanceof LinearTransform);
            /*
             * The first two lines of the above matrix contain the same offset and scale factors
             * than the ones in IrregularAxesConverterTest, except for a slight southing offset.
             * The error (3 metres in the translation term of the y axis) is assumed to be caused
             * by slightly different input values.
             */
            assertTrue("GridToCRS of a ProjectedCRS", new GeneralMatrix(
                    new double[] {55597,     0,     0,     0, -19959489},
                    new double[] {    0, 55597,     0,     0, -13843768},
                    new double[] {    0,     0,   NaN,     0,         5},
                    new double[] {    0,     0,     0,   NaN,     20975},
                    new double[] {    0,     0,     0,     0,         1}).equals(matrix, 1));
        } else {
            assertNull(gridToCRS);
            assertTrue("GridToCRS of a GeographicCRS", new GeneralMatrix(
                    new double[] {NaN,   0,   0,   0,  -179.5}, // Actually, the scale should be 0.5
                    new double[] {  0, NaN,   0,   0,   -77.0105},
                    new double[] {  0,   0, NaN,   0,     5.0},
                    new double[] {  0,   0,   0, NaN, 20975.0},
                    new double[] {  0,   0,   0,   0,     1.0}).equals(matrix, 1));
        }
        /*
         * Ask again the affine transform, this time from the grid geometry.
         */
        assertTrue("getAffineTransform(GridGeometry, CELL_CENTER) should give the same result.",
                Matrices.equals(matrix, DiscreteReferencingFactory.getAffineTransform(
                (GridGeometry) crs, PixelInCell.CELL_CENTER), 0, false));
        ((GeneralMatrix) matrix).sub((GeneralMatrix) DiscreteReferencingFactory.getAffineTransform(
                (GridGeometry) crs, PixelInCell.CELL_CORNER));
        if (isProjected) {
            assertTrue("CELL_CENTER - CELL_CORNER should be half of a pixel size.", new GeneralMatrix(
                    new double[] {0, 0,   0,   0, 27799},
                    new double[] {0, 0,   0,   0, 27799},
                    new double[] {0, 0, NaN,   0,   NaN},
                    new double[] {0, 0,   0, NaN,   NaN},
                    new double[] {0, 0,   0,   0,     0}).equals(matrix, 1));
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
