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

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.coverage.AbstractProcessTest;
import org.apache.sis.referencing.CRS;
import org.junit.*;
import static org.junit.Assert.*;
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
        env.setRange(1, -90, +90);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(matrix);
        gcb.setEnvelope(env);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();


        //get the description of the process we want
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "Resample");

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
        assertTrue(res instanceof GridCoverage2D);
        GridCoverage2D toTest = (GridCoverage2D)res;

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
        gcb.setName("test");
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setGridToCRS(gridToCrs);
        gcb.setRenderedImage(array);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();


        //output layout
        MathTransform gridToCrsOut = new AffineTransform2D(0.1,0,0,-0.1,20,60);
        gridToCrsOut = PixelTranslation.translate(gridToCrsOut, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        final GeneralGridEnvelope gridenv = new GeneralGridEnvelope(new Rectangle(0, 0, 60, 60), 2);
        final GeneralGridGeometry outGridGeom = new GeneralGridGeometry(gridenv, gridToCrsOut, crs);
        GridCoverage2D result = new ResampleProcess(coverage, crs, new GridGeometry2D(outGridGeom), InterpolationCase.NEIGHBOR, new double[]{Double.NaN}).executeNow();

        RenderedImage res = result.getRenderedImage();
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
        gcb.setName("test");
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setGridToCRS(gridToCrs);
        gcb.setRenderedImage(array);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();


        //output layout
        MathTransform gridToCrsOut = new AffineTransform2D(0.1,0,0,-0.1,19,61);
        gridToCrsOut = PixelTranslation.translate(gridToCrsOut, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        final GeneralGridEnvelope gridenv = new GeneralGridEnvelope(new Rectangle(0, 0, 60, 60), 2);
        final GeneralGridGeometry outGridGeom = new GeneralGridGeometry(gridenv, gridToCrsOut, crs);

        GridCoverage2D result = new ResampleProcess(coverage, crs, new GridGeometry2D(outGridGeom), InterpolationCase.NEIGHBOR, new double[]{Double.NaN}).executeNow();

        RenderedImage res = result.getRenderedImage();
        final Raster raster = res.getData();
        assertEquals(60, raster.getWidth());
        assertEquals(60, raster.getHeight());

        testPart(raster,  0, 60,  0, 10,NN);
        testPart(raster,  0, 10, 10, 20,NN);testPart(raster,  10, 20, 10, 20, 0);testPart(raster, 20, 30, 10, 20, 1);testPart(raster, 30, 40, 10, 20, 2);testPart(raster, 40, 50, 10, 20,NN);
        testPart(raster,  0, 10, 20, 30,NN);testPart(raster,  10, 20, 20, 30, 3);testPart(raster, 20, 30, 20, 30, 4);testPart(raster, 30, 40, 20, 30, 5);testPart(raster, 40, 50, 20, 30,NN);
        testPart(raster,  0, 10, 30, 40,NN);testPart(raster,  10, 20, 30, 40, 6);testPart(raster, 20, 30, 30, 40, 7);testPart(raster, 30, 40, 30, 40, 8);testPart(raster, 40, 50, 30, 40,NN);
        testPart(raster,  0, 60, 40, 60,NN);
    }

    private static void testPart(Raster raster, int minx, int maxx, int miny, int maxy, double value){
        for(int x=minx;x<maxx;x++){
            for(int y=miny; y<maxy;y++){
                assertEquals(value, raster.getPixel(x, y, (double[])null)[0], DELTA);
            }
        }
    }
}