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

import java.io.IOException;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.referencing.operation.TransformException;


import org.junit.*;
import static org.opengis.test.Assert.*;
import static org.geotoolkit.image.io.plugin.CoriolisFormatTest.getTestFile;
import static org.geotoolkit.test.image.ImageTestBase.getLocallyInstalledFile;


/**
 * Tests the {@link NetcdfAxis} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.08)
 */
public final strictfp class NetcdfAxisTest {
    /**
     * Small tolerance factor for floating point comparison.
     */
    private static final double EPS = 1E-10;

    /**
     * Tests the {@link NetcdfAxis#getOrdinateValue(double[], int)} method.
     * This method uses the Coriolis depth axis for testing purpose.
     * The first values are 5 10 20 30 40 50 60 80.
     *
     * @throws IOException If an error occurred while reading the test file.
     * @throws TransformException If an error occurred while interpolating.
     */
    @Test
    public void testGetOrdinateValue1D() throws IOException, TransformException {
        final NetcdfDataset data = NetcdfDataset.openDataset(getTestFile().getPath());
        try {
            final NetcdfCRS crs = NetcdfCRS.wrap(data.getCoordinateSystems().get(0), null, data, null);
            final NetcdfAxis axis = ((NetcdfAxis) crs.getCoordinateSystem().getAxis(2));
            assertInstanceOf("Expected a one-dimensional axis", NetcdfAxis1D.class, axis);
            final double[] c = new double[4];
            c[2] = 0.0; assertEquals( 5.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 0.5; assertEquals( 7.5, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 1.0; assertEquals(10.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 1.5; assertEquals(15.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 2.0; assertEquals(20.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 2.2; assertEquals(22.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 3.0; assertEquals(30.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 3.8; assertEquals(38.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 4.0; assertEquals(40.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 5.0; assertEquals(50.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 5.5; assertEquals(55.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 6.0; assertEquals(60.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 6.5; assertEquals(70.0, axis.getOrdinateValue(c, 0), EPS);
            c[2] = 7.0; assertEquals(80.0, axis.getOrdinateValue(c, 0), EPS);
        } finally {
            data.close();
        }
    }

    /**
     * Tests the {@link NetcdfAxis#getOrdinateValue(double[], int)} method.
     * The domain size is 570 × 380 pixels.
     *
     * @throws IOException If an error occurred while reading the test file.
     * @throws TransformException If an error occurred while interpolating.
     */
    @Test
    public void testGetOrdinateValue2D() throws IOException, TransformException {
        final NetcdfDataset data = NetcdfDataset.openDataset(getLocallyInstalledFile("Norway/norkyst_800m_avg_0234.nc").getPath());
        try {
            // Do not provide the 'data' NetcdfFile to the wrapper in
            // order to prevent it from "regularizing" the CRS axes.
            final NetcdfCRS crs = NetcdfCRS.wrap(data.getCoordinateSystems().get(0));
            final NetcdfAxis axis = ((NetcdfAxis) crs.getCoordinateSystem().getAxis(0));
            assertInstanceOf("Expected a two-dimensional axis", NetcdfAxis2D.class, axis);
            final double[] c = new double[4];
            c[0] =  0.00; c[1] =  0.00; assertEquals(55.8092058703564, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 16.00; c[1] =  0.00; assertEquals(55.9075870900714, axis.getOrdinateValue(c, 0), EPS);
            c[0] =  0.00; c[1] = 10.00; assertEquals(55.8435078120776, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 16.00; c[1] = 10.00; assertEquals(55.9420123984359, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 17.00; c[1] = 10.00; assertEquals(55.9481675390145, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 16.50; c[1] = 10.00; assertEquals(55.9450899687252, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 16.25; c[1] = 10.00; assertEquals(55.9435511835806, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 16.25; c[1] = 11.00; assertEquals(55.9469877681986, axis.getOrdinateValue(c, 0), EPS);
            c[0] = 16.25; c[1] = 10.25; assertEquals(55.9444103297350, axis.getOrdinateValue(c, 0), EPS);
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
                    crs = NetcdfCRS.wrap(cs, null, data, null);
                } else {
                    crs = NetcdfCRS.wrap(cs);
                }
                final CoordinateAxis axis = ((NetcdfAxis) crs.getCoordinateSystem().getAxis(3)).delegate();
                assertEquals("CoordinateAxis1DTime check", useFile, axis instanceof CoordinateAxis1DTime);
            } while ((useFile = !useFile) == true);
        } finally {
            data.close();
        }
    }
}
