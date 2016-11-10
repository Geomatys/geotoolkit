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

import java.util.Random;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.apache.sis.measure.Units;

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
     * Sets the buffered image to a raster filled with random value using the specified random
     * number generator. This method can be used for testing purpose, or for adding noise to a
     * coverage.
     *
     * @param random The random number generator to use for generating pixel values.
     */
    private static void setBufferedImage(final GridCoverageBuilder builder, final Random random) {
        builder.setRenderedImage((BufferedImage) null); // Will forces the creation of a new BufferedImage.
        final BufferedImage image = (BufferedImage) builder.getRenderedImage();
        final WritableRaster raster = image.getRaster();
        final ColorModel model = image.getColorModel();
        final int size;
        if (model instanceof IndexColorModel) {
            size = ((IndexColorModel) model).getMapSize();
        } else {
            size = 1 << Short.SIZE;
        }
        for (int i=raster.getWidth(); --i>=0;) {
            for (int j=raster.getHeight(); --j>=0;) {
                raster.setSample(i,j,0, random.nextInt(size));
            }
        }
    }

    /**
     * Tests "Piecewise" operation using a simple transform.
     */
    @Test
    public void testPiecewise() {
        // Initialize...
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        final GridCoverageBuilder.Variable variable = builder.variable(0);
        variable.setName("Elevation");
        variable.setUnit(Units.METRE);
        variable.addNodataValue("No data", 0, null);
        variable.setSampleRange(1, 40000);
        builder.setExtent(360, 180);
        builder.setEnvelope(-180, -90, 180, 90);
        builder.setCoordinateReferenceSystem("CRS:84");
        setBufferedImage(builder, random);
        final WritableRaster raster = ((BufferedImage) builder.getRenderedImage()).getRaster();
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
        final GridCoverageBuilder.Variable variable = builder.variable(0);
        variable.setName("Temperature");
        variable.setUnit(Units.CELSIUS);
        variable.addNodataValue("No data", 32767, null);
        variable.setSampleRange(-20000, 23000);
        builder.setExtent(360, 180);
        builder.setEnvelope(-180, -90, 180, 90);
        builder.setCoordinateReferenceSystem("CRS:84");
        setBufferedImage(builder, random);
        final WritableRaster raster = ((BufferedImage) builder.getRenderedImage()).getRaster();
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
