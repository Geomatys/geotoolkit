/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.processing.coverage.resample;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.stream.IntStream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.AbstractProcessTest;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampleTest extends AbstractProcessTest {

    public static final double DELTA = 0.00000001;
    public static final double NN = Double.NaN;

    static final int WIDTH = 10;
    static final int HEIGHT = 10;
    static final int SIZE = WIDTH*HEIGHT;

    public ResampleTest() {
        super(ResampleDescriptor.NAME);
    }

    @Test
    public void testProcess() throws Exception {

        final float[][] matrix = new float[][]{
            {0,1,2,3},
            {4,5,6,7},
            {0,1,2,3},
            {4,5,6,7}
        };

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -80, +80);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(BufferedImages.toDataBuffer1D(matrix), null);
        gcb.setDomain(new GridGeometry(new GridExtent(4, 4), env));
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final GridCoverage coverage = gcb.build();


        //get the description of the process we want
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, ResampleDescriptor.NAME);

        //create a process
        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("Source").setValue(coverage);
        input.parameter("CoordinateReferenceSystem").setValue(CRS.forCode("EPSG:3395"));
        final Process p = desc.createProcess(input);
        //get the result
        ParameterValueGroup output = p.call();

        assertNotNull(output);
        Object res = output.parameter("result").getValue();

        assertNotNull(res);
        assertTrue(res instanceof GridCoverage);
        GridCoverage toTest = (GridCoverage) res;

        assertEquals(toTest.getCoordinateReferenceSystem(), CRS.forCode("EPSG:3395"));
    }

    @Test
    public void resampleOffset() throws ProcessException{

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final float[][] array = {
            {0,1,2},
            {3,4,5},
            {6,7,8}
        };

        MathTransform gridToCrs = new AffineTransform2D(1,0,0,-1,20,60);
        gridToCrs = PixelTranslation.translate(gridToCrs, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 3), PixelInCell.CELL_CENTER, gridToCrs, crs));
        gcb.setValues(BufferedImages.toDataBuffer1D(array), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final GridCoverage coverage = gcb.build();


        //output layout
        MathTransform gridToCrsOut = new AffineTransform2D(0.1,0,0,-0.1,20,60);
        gridToCrsOut = PixelTranslation.translate(gridToCrsOut, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        final GridExtent gridenv = new GridExtent(60, 60);
        final GridGeometry outGridGeom = new GridGeometry(gridenv, PixelInCell.CELL_CENTER, gridToCrsOut, crs);
        GridCoverage result = new ResampleProcess(coverage, crs, outGridGeom, InterpolationCase.NEIGHBOR, new double[]{Double.NaN}).executeNow();

        RenderedImage res = result.render(null);
        final Raster raster = res.getData();
        assertEquals(60, raster.getWidth());
        assertEquals(60, raster.getHeight());

        testPart(raster,  0, 10,  0, 10, 0);testPart(raster, 10, 20,  0, 10, 1);testPart(raster, 20, 30,  0, 10, 2);
        testPart(raster,  0, 10, 10, 20, 3);testPart(raster, 10, 20, 10, 20, 4);testPart(raster, 20, 30, 10, 20, 5);
        testPart(raster,  0, 10, 20, 30, 6);testPart(raster, 10, 20, 20, 30, 7);testPart(raster, 20, 30, 20, 30, 8);

    }

    @Test
    public void resampleBorder() throws ProcessException{

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final float[][] array = {
            {0,1,2},
            {3,4,5},
            {6,7,8}
        };

        MathTransform gridToCrs = new AffineTransform2D(1,0,0,-1,20,60);
        gridToCrs = PixelTranslation.translate(gridToCrs, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 3), PixelInCell.CELL_CENTER, gridToCrs, crs));
        gcb.setValues(BufferedImages.toDataBuffer1D(array), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final GridCoverage coverage = gcb.build();


        //output layout
        MathTransform gridToCrsOut = new AffineTransform2D(0.1,0,0,-0.1,19,61);
        gridToCrsOut = PixelTranslation.translate(gridToCrsOut, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        final GridExtent gridenv = new GridExtent(60, 60);
        final GridGeometry outGridGeom = new GridGeometry(gridenv, PixelInCell.CELL_CENTER, gridToCrsOut, crs);

        GridCoverage result = new ResampleProcess(coverage, crs, outGridGeom, InterpolationCase.NEIGHBOR, new double[]{Double.NaN}).executeNow();

        RenderedImage res = result.render(null);
        final Raster raster = res.getData();
        assertEquals(60, raster.getWidth());
        assertEquals(60, raster.getHeight());

        testPart(raster,  0, 60,  0, 10,NN);
        testPart(raster,  0, 10, 10, 20,NN);testPart(raster,  10, 20, 10, 20, 0);testPart(raster, 20, 30, 10, 20, 1);testPart(raster, 30, 40, 10, 20, 2);testPart(raster, 40, 50, 10, 20,NN);
        testPart(raster,  0, 10, 20, 30,NN);testPart(raster,  10, 20, 20, 30, 3);testPart(raster, 20, 30, 20, 30, 4);testPart(raster, 30, 40, 20, 30, 5);testPart(raster, 40, 50, 20, 30,NN);
        testPart(raster,  0, 10, 30, 40,NN);testPart(raster,  10, 20, 30, 40, 6);testPart(raster, 20, 30, 30, 40, 7);testPart(raster, 30, 40, 30, 40, 8);testPart(raster, 40, 50, 30, 40,NN);
        testPart(raster,  0, 60, 40, 60,NN);
    }

    /**
     * The aim of this test is to ensure resample is able to handle badly defined data where longitude is specified as a
     * 0..360 value, but grid transform works in -180..+180. This is a corner-case where resample should analyze input
     * grids and detect that wrap-around is not needed.
     */
    @Test
    public void resampleFalse0_360() throws Exception {
            /*
            Create a grid geometry based on a 0-360 CRS but whose longitude coordinates cross the greenwich meridian
            using negative values.
            GridGeometry
              ├─Grid extent
              │   ├─Dimension 0: [0 … 200] (201 cells)
              │   └─Dimension 1: [0 … 300] (301 cells)
              ├─Geographic extent
              │   ├─Upper bound:  53°01′30″N  12°03′01″E
              │   └─Lower bound:  37°58′29″N  08°03′00″W
              ├─Envelope
              │   ├─Geodetic longitude:              -8.05 … 12.05   ∆Lon = 0.1°
              │   └─Geodetic latitude:  37.974999999999994 … 53.025  ∆Lat = 0.05°
              ├─Coordinate reference system
              │   └─grib-lonlat-crs
              └─Conversion (origin in a cell center)
                  └─┌                  ┐
                    │ 0.1   0     -8.0 │
                    │ 0    -0.05  53.0 │
                    │ 0     0      1   │
                    └                  ┘
            */
            final GridGeometry source = new GridGeometry(
                    new GridExtent(200, 300),
                    PixelInCell.CELL_CENTER,
                    new AffineTransform2D(0.1, 0, 0, -0.05, -8.0, 53.0),
                    DefaultGeographicCRS
                            .castOrCopy(CommonCRS.WGS84.normalizedGeographic())
                            .forConvention(AxesConvention.POSITIVE_RANGE)
            );

        final GridGeometry target = new GridGeometry(
                new GridExtent(17, 10),
                PixelInCell.CELL_CENTER,
                new AffineTransform2D(1, 0, 0, -1, -7, 50),
                CommonCRS.WGS84.normalizedGeographic()
        );

        final OutputGridBuilder builder = new OutputGridBuilder(source, target);
        final MathTransform trs = builder.forDefaultRendering();

        //transform point should be in the left side of source image
        double[] crd = new double[]{0,0};
        trs.transform(crd, 0, crd, 0, 1);
        assertTrue(crd[0] < 50);
    }

    /**
     * Ensures that resample is capable of applying a wrap-around transform when processing from a 0..360 data to a
     * -180..+180 one.
     *
     * @throws Exception If anything unexpected append.
     */
    @Test
    public void testTrue0_360() throws Exception {
        // Create a 0-360 source
        final CoordinateReferenceSystem crs360 = DefaultGeographicCRS
                .castOrCopy(CommonCRS.WGS84.normalizedGeographic())
                .forConvention(AxesConvention.POSITIVE_RANGE);

        final GridGeometry source = new GridGeometry(
                new GridExtent(32, 32),
                PixelInCell.CELL_CORNER,
                new AffineTransform2D(1, 0, 0, -1, 164, 0),
                crs360
        );

        final GridGeometry target = new GridGeometry(
                new GridExtent(7, 4),
            PixelInCell.CELL_CORNER,
                    new AffineTransform2D(1, 0, 0, -1, -173, 0),
            CommonCRS.WGS84.normalizedGeographic()
        );

        final BufferedImage sourceImage = BufferedImages.createImage(32, 32, 1, DataBuffer.TYPE_DOUBLE);
        sourceImage.getRaster().setPixels(0, 0, 16, 32, IntStream.range(0, 16*32).map(idx -> -1).toArray());
        sourceImage.getRaster().setPixels(16, 0, 16, 32, IntStream.range(0, 16*32).map(idx -> 1).toArray());
        final ResampleProcess.NoConversionCoverage sourceCvg = new ResampleProcess.NoConversionCoverage(
                source,
                Collections.singletonList(
                        new SampleDimension.Builder()
                                .addQuantitative("values", -1, 1, Units.UNITY)
                                .build()
                ),
                sourceImage, false
        );
        final ResampleProcess process = new ResampleProcess(sourceCvg, null, target, InterpolationCase.NEIGHBOR, new double[]{Double.NaN});
        final GridCoverage result = process.executeNow();

        assertArrayEquals(
                "Output image",
                IntStream.range(0, 7*4).mapToDouble(idx -> 1).toArray(),
                result.render(null).getData().getPixels(0, 0, 7, 4, (double[])null),
                1e-9
        );
    }

    private static void testPart(Raster raster, int minx, int maxx, int miny, int maxy, double value){
        for (int x=minx;x<maxx;x++) {
            for (int y=miny; y<maxy;y++) {
                assertEquals(value, raster.getPixel(x, y, (double[])null)[0], DELTA);
            }
        }
    }
}
