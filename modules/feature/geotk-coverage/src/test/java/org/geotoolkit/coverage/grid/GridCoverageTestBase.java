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

import java.util.Random;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.test.image.ImageTestBase;
import org.apache.sis.referencing.CommonCRS;

import static org.apache.sis.measure.Units.*;
import static org.junit.Assert.*;


/**
 * Base class for grid coverage tests. This class provides a {@link GridCoverage2D} field,
 * and some convenience methods working on it.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.1
 */
public abstract strictfp class GridCoverageTestBase extends ImageTestBase {
    /**
     * Random number generator for this test.
     */
    private static final Random random = new Random(684673898634768L);

    /**
     * The coverage to be tested. An instance can be obtained by
     * {@link #loadSampleCoverage(SampleCoverage)} or {@link #createRandomCoverage()}.
     */
    protected GridCoverage2D coverage;

    /**
     * Creates a new test suite for the given class.
     *
     * @param testing The class to be tested.
     */
    protected GridCoverageTestBase(final Class<?> testing) {
        super(testing);
    }

    /**
     * Loads the given sample coverage. The result is stored in the {@link #coverage} field.
     *
     * @param  s The enum for the sample grid coverage to load.
     */
    protected final void loadSampleCoverage(final SampleCoverage s) {
        try {
            coverage = s.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        s.verifyGridGeometry(coverage, 1E-10);
    }

    /**
     * Creates a grid coverage filled with random values. The coordinate
     * reference system default to {@link DefaultGeographicCRS#WGS84}.
     */
    protected final void createRandomCoverage() {
        createRandomCoverage(CommonCRS.WGS84.normalizedGeographic());
    }

    /**
     * Creates a grid coverage filled with random values.
     * The result is stored in the {@link #coverage} field.
     *
     * @param crs The coverage coordinate reference system.
     */
    protected final void createRandomCoverage(final CoordinateReferenceSystem crs) {
        /*
         * Some constants used for the construction and tests of the grid coverage.
         */
        final double      SCALE = 0.1; // Scale factor for pixel transcoding.
        final double     OFFSET = 5.0; // Offset factor for pixel transcoding.
        final double PIXEL_SIZE = .25; // Pixel size (in degrees). Used in transformations.
        final int   BEGIN_VALID = 3;   // The minimal valid index for quantitative category.
        /*
         * Constructs the grid coverage. We will assume that the grid coverage use
         * (longitude,latitude) coordinates, pixels of 0.25 degrees and a lower
         * left corner at 10°W 30°N.
         */
        final GridCoverage2D  coverage;  // The final grid coverage.
        final BufferedImage      image;  // The GridCoverage's data.
        final WritableRaster    raster;  // The image's data as a raster.
        final Rectangle2D       bounds;  // The GridCoverage's envelope.
        final GridSampleDimension band;  // The only image's band.
        band = new GridSampleDimension("Temperature", new Category[] {
            new Category("No data",     null, 0),
            new Category("Land",        null, 1),
            new Category("Cloud",       null, 2),
            new Category("Temperature", null, BEGIN_VALID, 256, SCALE, OFFSET)
        }, CELSIUS);
        image  = new BufferedImage(120, 80, BufferedImage.TYPE_BYTE_INDEXED);
        raster = image.getRaster();
        for (int i=raster.getWidth(); --i>=0;) {
            for (int j=raster.getHeight(); --j>=0;) {
                raster.setSample(i,j,0, random.nextInt(256));
            }
        }
        bounds = new Rectangle2D.Double(-10, 30, PIXEL_SIZE*image.getWidth(),
                                                 PIXEL_SIZE*image.getHeight());
        final GeneralEnvelope envelope = new GeneralEnvelope(crs);
        envelope.setRange(0, bounds.getMinX(), bounds.getMaxX());
        envelope.setRange(1, bounds.getMinY(), bounds.getMaxY());
        for (int i=envelope.getDimension(); --i>=2;) {
            final double min = 10 * i;
            envelope.setRange(i, min, min + 5);
        }
        final Hints hints = new Hints(Hints.TILE_ENCODING, "raw");

        final GridCoverageBuilder gcb = new GridCoverageBuilder(hints);
        gcb.setName("Test");
        gcb.setRenderedImage(image);
        gcb.setEnvelope(envelope);
        gcb.setSampleDimensions(band);
        coverage = gcb.getGridCoverage2D();
        assertEquals("raw", coverage.tileEncoding);
        /*
         * Grid coverage construction finished.  Now test it.  First we test the creation of a
         * "geophysics" view. This test make sure that the 'view(type)' method does not create
         * more grid coverages than needed.
         */
        assertSame(coverage.getRenderedImage(), coverage.getRenderableImage(0,1).createDefaultRendering());
        assertSame(image.getTile(0,0), coverage.getRenderedImage().getTile(0,0));
        GridCoverage2D geophysics = coverage.view(ViewType.GEOPHYSICS);
        assertSame(coverage,        coverage.view(ViewType.PACKED));
        assertSame(coverage,      geophysics.view(ViewType.PACKED));
        assertSame(geophysics,    geophysics.view(ViewType.GEOPHYSICS));
        assertFalse( coverage.equals(geophysics));
        assertFalse( coverage.getSampleDimension(0).getSampleToGeophysics().isIdentity());
        assertTrue(geophysics.getSampleDimension(0).getSampleToGeophysics().isIdentity());
        /*
         * Compares data.
         */
        final int bandN = 0; // Band to test.
        double[] bufferCov = null;
        double[] bufferGeo = null;
        final double left  = bounds.getMinX() + (0.5*PIXEL_SIZE); // Includes translation to center
        final double upper = bounds.getMaxY() - (0.5*PIXEL_SIZE); // Includes translation to center
        final Point2D.Double point = new Point2D.Double();        // Will maps to pixel center.
        for (int j=raster.getHeight(); --j>=0;) {
            for (int i=raster.getWidth(); --i>=0;) {
                point.x = left  + PIXEL_SIZE*i;
                point.y = upper - PIXEL_SIZE*j;
                double r = raster.getSampleDouble(i,j,bandN);
                bufferCov =   coverage.evaluate(point, bufferCov);
                bufferGeo = geophysics.evaluate(point, bufferGeo);
                assertEquals(r, bufferCov[bandN], SAMPLE_TOLERANCE);

                // Compares transcoded samples.
                if (r < BEGIN_VALID) {
                    assertTrue(Double.isNaN(bufferGeo[bandN]));
                } else {
                    assertEquals(OFFSET + SCALE*r, bufferGeo[bandN], SAMPLE_TOLERANCE);
                }
            }
        }
        this.coverage = coverage;
    }

    /**
     * Tests the serialization of the packed and geophysics views of the
     * {@linkplain #coverage current coverage}.
     *
     * @return The deserialized grid coverage as packed view.
     * @throws IOException if an I/O operation was needed and failed.
     * @throws ClassNotFoundException Should never happen.
     */
    protected final GridCoverage2D serialize() throws IOException, ClassNotFoundException {
        assertNotNull("CoverageTestCase.coverage field is not assigned.", coverage);
        coverage.tileEncoding = null;
        /*
         * The previous line is not something that we should do.
         * But we want to test the default GridCoverage2D encoding.
         */
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
            out.writeObject(coverage.view(ViewType.PACKED));
            out.writeObject(coverage.view(ViewType.GEOPHYSICS));
        }
        GridCoverage2D read;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
            read = (GridCoverage2D) in.readObject(); assertSame(read, read.view(ViewType.PACKED));
            read = (GridCoverage2D) in.readObject(); assertSame(read, read.view(ViewType.GEOPHYSICS));
        }
        final GridCoverage2D view = read.view(ViewType.PACKED);
        assertNotSame(read, view);
        return view;
    }
}
