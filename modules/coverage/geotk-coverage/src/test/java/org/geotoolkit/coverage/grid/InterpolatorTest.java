/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.io.IOException;
import javax.media.jai.Interpolation;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link Interpolator2D} implementation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.1
 */
public final strictfp class InterpolatorTest extends GridCoverageTestBase {
    /**
     * Creates a new test suite.
     */
    public InterpolatorTest() {
        super(Interpolator2D.class);
    }

    /**
     * Tests bilinear intersection at pixel edges. It should be equals
     * to the average of the four pixels around.
     */
    @Test
    public void testInterpolationAtEdges() {
        // Following constant is pixel size (in degrees).
        // This constant must be identical to the one defined in 'createRandomCoverage()'
        final double PIXEL_SIZE = 0.25;
        createRandomCoverage();
        coverage = coverage.view(ViewType.GEOPHYSICS);
        coverage = Interpolator2D.create(coverage, Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
        assertTrue(coverage instanceof Interpolator2D);
        assertTrue(coverage.view(ViewType.GEOPHYSICS) instanceof Interpolator2D);
        assertTrue(coverage.view(ViewType.PACKED)     instanceof Interpolator2D);
        final int  band = 0; // Band to test.
        double[] buffer = null;
        final Raster          data = coverage.getRenderedImage().getData();
        final Envelope    envelope = coverage.getEnvelope();
        final GridEnvelope   range = coverage.getGridGeometry().getExtent();
        final double          left = envelope.getMinimum(0);
        final double         upper = envelope.getMaximum(1);
        final Point2D.Double point = new Point2D.Double(); // Will maps to pixel upper-left corner
        for (int j=range.getSpan(1); --j>=1;) {
            for (int i=range.getSpan(0); --i>=1;) {
                point.x  = left  + PIXEL_SIZE*i;
                point.y  = upper - PIXEL_SIZE*j;
                buffer   = coverage.evaluate(point, buffer);
                double t = buffer[band];

                // Computes the expected value:
                double r00 = data.getSampleDouble(i-0, j-0, band);
                double r01 = data.getSampleDouble(i-0, j-1, band);
                double r10 = data.getSampleDouble(i-1, j-0, band);
                double r11 = data.getSampleDouble(i-1, j-1, band);
                double r = (r00 + r01 + r10 + r11) / 4;
                assertEquals(r, t, SAMPLE_TOLERANCE);
            }
        }
    }

    /**
     * Tests the serialization of a grid coverage.
     *
     * @throws IOException if an I/O operation was needed and failed.
     * @throws ClassNotFoundException Should never happen.
     */
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final int[] types = {
            Interpolation.INTERP_BICUBIC,
            Interpolation.INTERP_BILINEAR,
            Interpolation.INTERP_NEAREST
        };
        final Interpolation[] interpolations = new Interpolation[types.length];
        for (int i=0; i<interpolations.length; i++) {
            interpolations[i] = Interpolation.getInstance(types[i]);
        }
        loadSampleCoverage(SampleCoverage.SST);
        coverage = Interpolator2D.create(coverage, interpolations);
        GridCoverage2D serial = serialize();
        assertNotSame(coverage, serial);
        assertEquals(Interpolator2D.class, serial.getClass());
        // Compares the geophysics view for working around the
        // conversions of NaN values which may be the expected ones.
        coverage = coverage.view(ViewType.GEOPHYSICS);
        serial   = serial  .view(ViewType.GEOPHYSICS);
        assertRasterEquals(coverage, serial);
    }
}
