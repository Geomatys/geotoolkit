/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.awt.image.BufferedImage;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link GridCoverageBuilder} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.20
 *
 * @since 2.1
 */
public final strictfp class GridCoverageBuilderTest extends GridCoverageTestBase {
    /**
     * Creates a new test suite.
     */
    public GridCoverageBuilderTest() {
        super(GridCoverageBuilder.class);
    }

    /**
     * Tests creating a grid coverage with a plain RGB image.
     */
    @Test
    public void testRGB() {
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setRenderedImage(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB));
        builder.setEnvelope(new Envelope2D(null, 0, 0, 2, 2));
        builder.setCoordinateReferenceSystem(PredefinedCRS.GRID_2D);
        coverage = builder.getGridCoverage2D();
        assertTrue(coverage.getViewTypes().contains(ViewType.PHOTOGRAPHIC));
    }

    /**
     * Tests the creation from a raster using byte data type.
     *
     * @throws IOException If an error occurred while reading the test image.
     */
    @Test
    public void testByteFromRaster() throws IOException {
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setName("SampleCoverage.SST");
        builder.setEnvelope(new Envelope2D(null, SampleCoverage.SST.bounds));
        builder.setCoordinateReferenceSystem(SampleCoverage.SST.crs);
        builder.variable(0).setName("Temperature");
        builder.variable(0).setSampleRange(30, 220);
        builder.variable(0).setLinearTransform(0.1, 10);
        builder.variable(0).setColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        builder.variable(0).addNodataValue("Missing values", 255, Color.GRAY);
        builder.setRenderedImage(SampleCoverage.SST.raster());
        coverage = builder.getGridCoverage2D();
        SampleCoverage.SST.verifyGridGeometry(coverage, 0);
        assertTrue  ("Expected ViewType.PACKED", coverage.getViewTypes().contains(ViewType.PACKED));
        assertFalse ("Expected ViewType.PACKED", coverage.getViewTypes().contains(ViewType.GEOPHYSICS));
        assertEquals("Wrong scale factor for X", 0.1, builder.getAffineGridToCRS().getScaleX(), 1E-10);
        final Graphics2D gr = (Graphics2D) builder.createGraphics(true);
        assertEquals("Wrong scale factor for X", 10, gr.getTransform().getScaleX(), 1E-10);
        gr.dispose();
        show(coverage);
    }

    /**
     * Tests the creation from a raster using float data type.
     *
     * @throws IOException Should never occurs.
     */
    @Test
    public void testFloatFromRaster() throws IOException {
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setName("SampleCoverage.FLOAT");
        builder.setEnvelope(new Envelope2D(SampleCoverage.FLOAT.crs, SampleCoverage.FLOAT.bounds));
        builder.variable(0).setSampleRange(0, 256);
        builder.variable(0).setGeophysicsRange(0, 1000);
        builder.variable(0).setColors(Color.RED, Color.WHITE, Color.BLUE);
        builder.setRenderedImage(SampleCoverage.FLOAT.raster());
        coverage = builder.getGridCoverage2D();
        SampleCoverage.SST.verifyGridGeometry(coverage, 0);
        assertTrue ("Expected ViewType.GEOPHYSICS", coverage.getViewTypes().contains(ViewType.GEOPHYSICS));
        assertFalse("Expected ViewType.GEOPHYSICS", coverage.getViewTypes().contains(ViewType.PACKED));
        show(coverage);
    }

    /**
     * Tests a change of CRS.
     *
     * @see <a href="http://www.geotoolkit.org/modules/coverage/faq.html#changeCRS">How do I change the CRS of an existing GridCoverage2D?</a>
     *
     * @since 3.20
     */
    @Test
    public void testChangeCRS() {
        loadSampleCoverage(SampleCoverage.SST);
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setGridCoverage(coverage);
        builder.setCoordinateReferenceSystem(PredefinedCRS.CARTESIAN_2D);
        final GridCoverage2D newGC = builder.getGridCoverage2D();
        assertNotSame("Expected a new grid coverage.", coverage, newGC);
        assertSame(PredefinedCRS.CARTESIAN_2D, newGC.getCoordinateReferenceSystem());
        assertEquals(coverage.getGridGeometry().getGridToCRS(),
                     newGC   .getGridGeometry().getGridToCRS());
    }
}
