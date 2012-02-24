/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.awt.image.WritableRaster;
import java.util.Random;
import javax.measure.unit.SI;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link ViewsManager} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.5
 */
public final strictfp class ViewsManagerTest extends GridCoverageTestBase {
    /**
     * The random number generator to use in this test suite.
     */
    private static final Random random = new Random(7667138224618831007L);

    /**
     * Creates a new test suite.
     */
    public ViewsManagerTest() {
        super(ViewsManager.class);
    }

    /**
     * Tests "Piecewise" operation using a simple transform.
     */
    @Test
    public void testPiecewise() {
        // Initialize...
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        final GridCoverageBuilder.Variable variable = builder.newVariable("Elevation", SI.METRE);
        variable.addNodataValue("No data", 0, null);
        builder.setSampleRange(1, 40000);
        builder.setImageSize(360, 180);
        builder.setEnvelope(-180, -90, 180, 90);
        builder.setCoordinateReferenceSystem("CRS:84");
        builder.setBufferedImage(random);
        final WritableRaster raster = builder.getBufferedImage().getRaster();
        raster.setSample(0,0,0,0); // For testing NaN value.
        raster.setSample(1,2,0,0);

        // Sanity check...
        assertEquals(360, raster.getWidth());
        assertEquals(180, raster.getHeight());

        // Tests...
        GridCoverage2D packed = builder.getGridCoverage2D();
        GridCoverage2D geophysics = packed.view(ViewType.GEOPHYSICS);
        show(geophysics);
        // TODO: complete the tests...
    }

    /**
     * Tests "Piecewise" operation using setting found in IFREMER's Coriolis data.
     */
    @Test
    public void testCoriolis() {
        final double scale  = 0.001;
        final double offset = 20.0;

        // Initialize...
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        final GridCoverageBuilder.Variable variable = builder.newVariable("Temperature", SI.CELSIUS);
        variable.addNodataValue("No data", 32767, null);
        builder.setSampleRange(-20000, 23000);
        builder.setImageSize(360, 180);
        builder.setEnvelope(-180, -90, 180, 90);
        builder.setCoordinateReferenceSystem("CRS:84");
        builder.setBufferedImage(random);
        final WritableRaster raster = builder.getBufferedImage().getRaster();
        raster.setSample(0,0,0,32767); // For testing NaN value.
        raster.setSample(1,2,0,32767);

        // Sanity check...
        assertEquals(360, raster.getWidth());
        assertEquals(180, raster.getHeight());

        // Tests without "sample to geophysics" transform...
        GridCoverage2D packed = builder.getGridCoverage2D();
        GridCoverage2D geophysics = packed.view(ViewType.GEOPHYSICS);
        show(geophysics);

        variable.setLinearTransform(scale, offset);
        packed = builder.getGridCoverage2D();
    }
}
