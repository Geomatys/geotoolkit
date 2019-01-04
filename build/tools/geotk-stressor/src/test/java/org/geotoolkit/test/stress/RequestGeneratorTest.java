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
package org.geotoolkit.test.stress;

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import static org.geotoolkit.referencing.crs.PredefinedCRS.CARTESIAN_2D;
import org.junit.*;
import static org.junit.Assert.*;
import org.opengis.geometry.Envelope;
import static org.opengis.referencing.datum.PixelInCell.CELL_CORNER;


/**
 * Tests {@link RequestGenerator}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
public final strictfp class RequestGeneratorTest {
    /**
     * Tests the distribution of {@link RequestGenerator#getRandomGrid()}.
     */
    @Test
    public void testGridDistribution() {
        final AffineTransform2D gridToCRS = new AffineTransform2D(0.02, 0, 0, 0.01, 0, 0);
        final GridExtent    range         = new GridExtent(null, new long[]{-1000, -2000}, new long[]{5000-1000, 7000-2000}, false);
        final GridGeometry2D    geometry  = new GridGeometry2D(range, CELL_CORNER, gridToCRS, CARTESIAN_2D, null);
        final GeneralEnvelope   envelope  = new GeneralEnvelope(geometry.getEnvelope());
        final RequestGenerator  generator = new RequestGenerator(geometry);
        final int[] minimalGridSize = generator.getMinimalGridSize();
        final int[] maximalGridSize = generator.getMaximalGridSize();
        assertEquals( 100, minimalGridSize[0]);
        assertEquals( 100, minimalGridSize[1]);
        assertEquals(2000, maximalGridSize[0]);
        assertEquals(2000, maximalGridSize[1]);
        assertEquals(50, generator.getMaximumScale(), 0.00001);

        generator.random.setSeed(74652428);
        for (int i=range.getDimension(); --i>=0;) {
            minimalGridSize[i] = 400;
            maximalGridSize[i] = 800;
        }
        generator.setMinimalGridSize(minimalGridSize);
        generator.setMaximalGridSize(maximalGridSize);
        final int[] distribution = new int[6];
        final double distributionScale = distribution.length / generator.getMaximumScale();

        for (int t=0; t<5000; t++) {
            final GridGeometry sg = generator.getRandomGrid();
            final GridExtent          sr = sg.getExtent();
            final Envelope            se = sg.getEnvelope();
            assertTrue("Grid envelope out of bounds.", GridGeometryIterator.toRectangle(range)
                    .contains(sr.getLow(0), sr.getLow(1), sr.getSize(0), sr.getSize(1)));
            assertTrue("Geodetic envelope out of bounds.", envelope.contains(se));
            for (int i=sr.getDimension(); --i>=0;) {
                final long span = sr.getSize(i);
                assertTrue("Min", span >= minimalGridSize[i]);
                assertTrue("Max", span <= maximalGridSize[i]);
                for (final double scale : generator.getScale(sg)) {
                    distribution[((int) ((scale - 1) * distributionScale))]++;
                }
            }
        }
        for (int i=1; i<distribution.length; i++) {
            assertTrue("Expected decreasing frequency for higher scales.",
                    distribution[i] < distribution[i-1]);
        }
    }
}
