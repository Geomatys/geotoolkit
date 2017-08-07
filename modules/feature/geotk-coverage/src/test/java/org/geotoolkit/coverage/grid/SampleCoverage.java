/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.media.jai.RasterFactory;
import java.io.IOException;

import org.opengis.metadata.content.TransferFunctionType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.opengis.referencing.operation.MathTransform1D;
import org.geotoolkit.image.SampleImage;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;

import org.apache.sis.referencing.operation.transform.TransferFunction;
import static java.awt.Color.decode;
import static org.apache.sis.measure.Units.*;
import static org.apache.sis.measure.NumberRange.create;
import static org.junit.Assert.*;


/**
 * Enumeration of sample grid coverages.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.02
 */
public strictfp enum SampleCoverage {
    /**
     * Sea Surface Temperature.
     * This is a raster from Earth observations using a relatively straightforward
     * conversion formula to geophysics values (a linear transform using the usual
     * scale and offset parameters, in this case 0.1 and 10 respectively). The
     * interesting part of this example is that it contains a lot of nodata values.
     *
     * {@preformat text
     *   Thematic           :  Sea Surface Temperature (SST) in °C
     *   Data packaging     :  Indexed 8-bits
     *   Nodata values      :  [0 .. 29] and [240 .. 255] inclusive.
     *   Conversion formula :  (°C) = (packed value)/10 + 10
     *   Geographic extent  :  (41°S, 35°E) - (5°N, 80°E)
     *   Image size         :  (450 x 460) pixels
     * }
     */
    SST(SampleImage.INDEXED, CommonCRS.WGS84.normalizedGeographic(), new Rectangle(35, -41, 45, 46),
            new GridSampleDimension("Measure", new Category[] {
                new Category("Coast line", decode("#000000"), create(  0, true,   0, true)),
                new Category("Cloud",      decode("#C3C3C3"), create(  1, true,   9, true)),
                new Category("Unused",     decode("#822382"), create( 10, true,  29, true)),
                new Category("Sea Surface Temperature", null, create( 30, true, 219, true), 0.1, 10.0),
                new Category("Unused",     decode("#A0505C"), create(220, true, 239, true)),
                new Category("Land",       decode("#D2C8A0"), create(240, true, 254, true)),
                new Category("No data",    decode("#FFFFFF"), create(255, true, 255, true)),
            }, CELSIUS)),

    /**
     * Chlorophyl-a concentration.
     * This is a raster from Earth observations using a more complex conversion
     * formula to geophysics values (an exponential one). The usual scale and
     * offset parameters are not enough in this case.
     *
     * {@preformat text
     *   Thematic           :  Chlorophyle-a concentration in mg/m³
     *   Data packaging     :  Indexed 8-bits
     *   Nodata values      :  0 and 255
     *   Conversion formula :  (mg/m³) = 10 ^ ((packed value)*0.015 - 1.985)
     *   Geographic extent  :  (34°N, 07°W) - (45°N, 12°E)
     *   Image size         :  (300 x 175) pixels
     * }
     */
    CHL(SampleImage.INDEXED_LOGARITHMIC, CommonCRS.WGS84.normalizedGeographic(), new Rectangle(-7, 34, 19, 11),
            new GridSampleDimension("Measure", new Category[] {
                new Category("Land",    decode("#000000"), create(255, true, 255, true)),
                new Category("No data", decode("#FFFFFF"), create(  0, true,   0, true)),
                new Category("Chl-a",   null,              create(  1, true, 254, true),
                        MathTransforms.concatenate(
                        (MathTransform1D) MathTransforms.linear(0.015, -1.985),
                        exp()))
        }, KILOGRAM.divide(1E6).divide(CUBIC_METRE))),

    /**
     * A float coverage. Because we use only one tile with one band, the code below
     * is pretty similar to the code we would have if we were just setting the values
     * in a matrix.
     */
    FLOAT(null, CommonCRS.WGS84.normalizedGeographic(), new Rectangle(35, -41, 45, 46)) {
        @Override final WritableRaster raster() {
            final int width  = 500;
            final int height = 500;
            final WritableRaster raster =
                    RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, width, height, 1, null);
            for (int y=0; y<height; y++) {
                for (int x=0; x<width; x++) {
                    raster.setSample(x, y, 0, x+y);
                }
            }
            return raster;
        }

        @Override final GridCoverage2D load() {
            final Color[] colors = new Color[] {
                Color.BLUE, Color.CYAN, Color.WHITE, Color.YELLOW, Color.RED
            };

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName("Float coverage");
            gcb.setRenderedImage(raster());
            gcb.setEnvelope( new Envelope2D(this.crs, this.bounds));
            gcb.setSampleDimensions(null, null, null, colors);
            return gcb.getGridCoverage2D();
        }
    };

    private static MathTransform1D exp() {
        final TransferFunction f = new TransferFunction();
        f.setType(TransferFunctionType.EXPONENTIAL);
        return f.getTransform();
    }

    /**
     * The enum for the image to load, or {@code null} if the image is computed rather
     * than loaded.
     */
    private final SampleImage image;

    /**
     * The coordinate reference system for the coverage.
     */
    final CoordinateReferenceSystem crs;

    /**
     * The envelope in CRS coordinates.
     */
    final Rectangle2D bounds;

    /**
     * The sample dimensions to be given to the coverage.
     */
    private final GridSampleDimension[] bands;

    /**
     * Creates a new enum loading the given image.
     */
    private SampleCoverage(final SampleImage image, CoordinateReferenceSystem crs,
            final Rectangle2D bounds, GridSampleDimension... bands)
    {
        this.image  = image;
        this.crs    = crs;
        this.bounds = bounds;
        this.bands  = bands;
    }

    /**
     * Loads or computes the raster.
     *
     * @return The sample raster.
     * @throws IOException If the raster can not be read.
     *
     * @since 3.20
     */
    WritableRaster raster() throws IOException {
        return image.load().getRaster();
    }

    /**
     * Loads the sample coverage.
     *
     * @return The sample coverage.
     * @throws IOException If the image can not be read.
     */
    GridCoverage2D load() throws IOException {
        final RenderedImage image = this.image.load();
        final GeneralEnvelope envelope = new GeneralEnvelope(
                new double[] {bounds.getMinX(), bounds.getMinY()},
                new double[] {bounds.getMaxX(), bounds.getMaxY()});
        envelope.setCoordinateReferenceSystem(crs);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName(this.image.filename);
        gcb.setRenderedImage(image);
        gcb.setEnvelope(envelope);
        gcb.setSampleDimensions(bands);
        return gcb.getGridCoverage2D();
    }

    /**
     * Verifies that the grid geometry of the given coverage is conform to the expected one.
     *
     * @param coverage The coverage to verify.
     * @param eps Tolerance threshold for comparisons of floating point numbers.
     *
     * @since 3.20
     */
    void verifyGridGeometry(final GridCoverage2D coverage, final double eps) {
        assertSame(crs, coverage.getCoordinateReferenceSystem2D());
        assertArrayEquals(new double[] {bounds.getMinX(), bounds.getMinY()}, coverage.getEnvelope2D().getLowerCorner().getCoordinate(), eps);
        assertArrayEquals(new double[] {bounds.getMaxX(), bounds.getMaxY()}, coverage.getEnvelope2D().getUpperCorner().getCoordinate(), eps);
    }
}
