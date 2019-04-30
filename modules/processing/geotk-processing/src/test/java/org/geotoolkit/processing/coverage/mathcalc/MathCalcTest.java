/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.coverage.mathcalc;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.memory.MPCoverageStore;
import org.geotoolkit.data.multires.DefiningMosaic;
import org.geotoolkit.data.multires.DefiningPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcTest extends org.geotoolkit.test.TestBase {

    private static final float DELTA = 0.000000001f;

    /**
     * This test is expected to copy values from one coverage to the other.
     */
    @Test
    public void passthroughtTest() throws Exception{
        final int width = 512;
        final int height = 300;

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.defaultGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);

        //create base coverage
        final GridCoverage2D baseCoverage = createCoverage2D(env, width, height, 15.5f, -3.0f);


        //create output coverage ref
        final GenericName n = NamesExt.create("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageResource outRef = (PyramidalCoverageResource) store.add(new DefiningCoverageResource(n));
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new SampleDimension(Names.createLocalName(null, null, "data"), null, Collections.emptyList())));
        outRef.setSampleModel(baseCoverage.getRenderedImage().getSampleModel());
        outRef.setColorModel(baseCoverage.getRenderedImage().getColorModel());
        final Pyramid pyramid = (Pyramid) outRef.createModel(new DefiningPyramid(crs));
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(env.getMinimum(0), env.getMaximum(1));
        pyramid.createMosaic(
                new DefiningMosaic(null, corner, 0.1, new Dimension(width, height), new Dimension(1, 1)));


        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new GridCoverage[]{baseCoverage},
                "A",
                new String[]{"A"},
                outRef);
        process.call();

        final GridCoverageReader reader = outRef.acquireReader();
        final GridCoverage2D result = (GridCoverage2D)reader.read(null);
        outRef.recycle(reader);
        final Raster resultRaster = result.getRenderedImage().getData();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                float v = resultRaster.getSampleFloat(x, y, 0);
                Assert.assertEquals("at ("+x+" "+y+")", (y<height/2) ? 15.5f : -3.0f, v, DELTA);
            }
        }

    }

    /**
     * This test is expected to copy values with and addition from one coverage to the other.
     */
    @Test
    public void incrementTest() throws Exception{
        final int width = 512;
        final int height = 300;

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.defaultGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);

        //create base coverage
        final GridCoverage2D baseCoverage = createCoverage2D(env, width, height, 15.5f, -2.0f);


        //create output coverage ref
        final GenericName n = NamesExt.create("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageResource outRef = (PyramidalCoverageResource) store.add(new DefiningCoverageResource(n));
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new SampleDimension(Names.createLocalName(null, null, "data"), null, Collections.emptyList())));
        outRef.setSampleModel(baseCoverage.getRenderedImage().getSampleModel());
        outRef.setColorModel(baseCoverage.getRenderedImage().getColorModel());
        final Pyramid pyramid = (Pyramid) outRef.createModel(new DefiningPyramid(crs));
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(env.getMinimum(0), env.getMaximum(1));
        pyramid.createMosaic(
                new DefiningMosaic(null, corner, 0.1, new Dimension(width, height), new Dimension(1, 1)));


        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new GridCoverage[]{baseCoverage},
                "A+10",
                new String[]{"A"},
                outRef);
        process.call();

        final GridCoverageReader reader = outRef.acquireReader();
        final GridCoverage2D result = (GridCoverage2D)reader.read(null);
        outRef.recycle(reader);
        final Raster resultRaster = result.getRenderedImage().getData();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                float v = resultRaster.getSampleFloat(x, y, 0);
                Assert.assertEquals( (y<height/2) ? 25.5f : 8.0f, v, DELTA);
            }
        }

    }

    /**
     * This test is expected to copy values with and addition from one coverage to the other.
     */
    @Test
    public void expression2CoverageTest() throws Exception{
        final int width = 512;
        final int height = 300;

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.defaultGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);

        //create base coverage
        final GridCoverage2D baseCoverage1 = createCoverage2D(env, width, height, 15.5f,  3.0f);
        final GridCoverage2D baseCoverage2 = createCoverage2D(env, width, height, -9.0f, 20.0f);


        //create output coverage ref
        final GenericName n = NamesExt.create("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageResource outRef = (PyramidalCoverageResource) store.add(new DefiningCoverageResource(n));
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new SampleDimension(Names.createLocalName(null, null, "data"), null, Collections.emptyList())));
        outRef.setSampleModel(baseCoverage1.getRenderedImage().getSampleModel());
        outRef.setColorModel(baseCoverage1.getRenderedImage().getColorModel());
        final Pyramid pyramid = (Pyramid) outRef.createModel(new DefiningPyramid(crs));
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(env.getMinimum(0), env.getMaximum(1));
        pyramid.createMosaic(
                new DefiningMosaic(null, corner, 0.1, new Dimension(width, height), new Dimension(1, 1)));


        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new GridCoverage[]{baseCoverage1, baseCoverage2},
                "(A+B)*5",
                new String[]{"A","B"},
                outRef);
        process.call();

        final GridCoverageReader reader = outRef.acquireReader();
        final GridCoverage2D result = (GridCoverage2D)reader.read(null);
        outRef.recycle(reader);
        final Raster resultRaster = result.getRenderedImage().getData();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                float v = resultRaster.getSampleFloat(x, y, 0);
                Assert.assertEquals( (y<height/2) ? 32.5f : 115.0f, v, DELTA);
            }
        }

    }

    /**
     * 4D calc test
     */
    @Test
    public void coverage4DTest() throws Exception{

        //create a small pyramid
        final MPCoverageStore store = new MPCoverageStore();
        final CoordinateReferenceSystem horizontal = CommonCRS.defaultGeographic();
        final CoordinateReferenceSystem vertical = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        final CoordinateReferenceSystem temporal = CommonCRS.Temporal.JAVA.crs();
        Map<String, Object> props = new HashMap<>();
        props.put("name", "4dcrs");
        final CoordinateReferenceSystem crs = new GeodeticObjectBuilder().addName("4dcrs")
                                                                         .createCompoundCRS(horizontal, vertical, temporal);
        final int width = 28;
        final int height = 13;

        final PyramidalCoverageResource ref1 = (PyramidalCoverageResource) store.add(new DefiningCoverageResource("test1"));
        create4DPyramid(ref1, crs, width, height, new double[][]{
                {-5,-9,  12},
                {-5, 0,  -7},
                {-5,21,  51},
                {62,-9,  -3},
                {62, 0,   5},
                {62,21,  17},
        });

        final PyramidalCoverageResource ref2 = (PyramidalCoverageResource) store.add(new DefiningCoverageResource("test2"));
        create4DPyramid(ref2, crs, width, height, new double[][]{
                {-5,-9,  -4},
                {-5, 0,  32},
                {-5,21,  90},
                {62,-9, -87},
                {62, 0,  -6},
                {62,21,  41},
        });

        final PyramidalCoverageResource outRef = (PyramidalCoverageResource) store.add(new DefiningCoverageResource("result"));
        create4DPyramid(outRef, crs, width, height, new double[][]{
                {-5,-9,   0},
                {-5, 0,   0},
                {-5,21,   0},
                {62,-9,   0},
                {62, 0,   0},
                {62,21,   0},
        });


        //create base coverage
        final GridCoverageReader reader1 = ref1.acquireReader();
        final GridCoverageReader reader2 = ref2.acquireReader();
        final GridCoverage baseCoverage1 = reader1.read(null);
        final GridCoverage baseCoverage2 = reader2.read(null);
        ref1.recycle(reader1);
        ref2.recycle(reader2);

        outRef.setSampleModel(createRenderedImage(1,1,0,0).getSampleModel());
        outRef.setColorModel(createRenderedImage(1,1,0,0).getColorModel());

        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new GridCoverage[]{baseCoverage1, baseCoverage2},
                "(A+B)*5",
                new String[]{"A","B"},
                outRef);
        process.call();

        final GridCoverageReader outReader = outRef.acquireReader();
        final GridCoverage result = outReader.read(null);
        outRef.recycle(outReader);
        Assert.assertTrue(result instanceof GridCoverageStack);
        final GridCoverageStack stackT = (GridCoverageStack) result;
        final GridCoverageStack stackT0 = (GridCoverageStack) stackT.coveragesAt(-9).get(0);
        final GridCoverageStack stackT1 = (GridCoverageStack) stackT.coveragesAt( 0).get(0);
        final GridCoverageStack stackT2 = (GridCoverageStack) stackT.coveragesAt(21).get(0);
        final GridCoverage2D stackT0Z0 = (GridCoverage2D) stackT0.coveragesAt(-5).get(0);
        final GridCoverage2D stackT0Z1 = (GridCoverage2D) stackT0.coveragesAt(62).get(0);
        final GridCoverage2D stackT1Z0 = (GridCoverage2D) stackT1.coveragesAt(-5).get(0);
        final GridCoverage2D stackT1Z1 = (GridCoverage2D) stackT1.coveragesAt(62).get(0);
        final GridCoverage2D stackT2Z0 = (GridCoverage2D) stackT2.coveragesAt(-5).get(0);
        final GridCoverage2D stackT2Z1 = (GridCoverage2D) stackT2.coveragesAt(62).get(0);

        testImageContent(stackT0Z0.getRenderedImage(), width, height,  40,  40); // (12 -  4)*5
        testImageContent(stackT1Z0.getRenderedImage(), width, height, 125, 125); // (-7 + 32)*5
        testImageContent(stackT2Z0.getRenderedImage(), width, height, 705, 705); // (51 + 90)*5
        testImageContent(stackT0Z1.getRenderedImage(), width, height,-450,-450); // (-3 - 87)*5
        testImageContent(stackT1Z1.getRenderedImage(), width, height,  -5,  -5); // ( 5 -  6)*5
        testImageContent(stackT2Z1.getRenderedImage(), width, height, 290, 290); // (17 + 41)*5


    }

    /**
     *
     * @param geovalues [0...n slices][Z coord, T coord, sample value]
     */
    private static void create4DPyramid(PyramidalCoverageResource ref, CoordinateReferenceSystem crs,
                                        int width, int height, double[][] geovalues) throws DataStoreException {

        final List<SampleDimension> dimensions = new ArrayList<>();
        final SampleDimension dim = new SampleDimension(Names.createLocalName(null, null, "samples"), null, Collections.emptyList());
        dimensions.add(dim);

        final Pyramid pyramid = (Pyramid) ref.createModel(new DefiningPyramid(crs));
        ref.setSampleDimensions(dimensions);

        final Dimension gridSize = new Dimension(4, 3);
        final Dimension tilePixelSize = new Dimension(width, height);

        for(double[] slice : geovalues){
            final GeneralDirectPosition upperLeft = new GeneralDirectPosition(crs);
            upperLeft.setCoordinate(-50, 60, slice[0], slice[1]);
            final Mosaic mosaic = pyramid.createMosaic(
                    new DefiningMosaic(null, upperLeft, 1, tilePixelSize, gridSize));

            final float sample = (float)slice[2];
            for(int x=0;x<gridSize.width;x++){
                for(int y=0;y<gridSize.height;y++){
                    mosaic.writeTiles(
                            Stream.of(new DefaultImageTile(
                                    createRenderedImage(tilePixelSize.width, tilePixelSize.height, sample, sample)
                                    , new Point(x, y))),null);
                }
            }
        }

    }

    /**
     *
     * @param fillValue1 value used in first vertical half of the image
     * @param fillValue2 value used in secong vertical half of the image
     */
    private static GridCoverage2D createCoverage2D(Envelope env, int width, int height, float fillValue1, float fillValue2){
        final BufferedImage baseImage = createRenderedImage(width, height, fillValue1, fillValue2);
        final GridCoverageBuilder baseGcb1 = new GridCoverageBuilder();
        baseGcb1.setName("base");
        baseGcb1.setRenderedImage(baseImage);
        baseGcb1.setEnvelope(env);
        return baseGcb1.getGridCoverage2D();
    }

    private static BufferedImage createRenderedImage(int width, int height, float fillValue1, float fillValue2){
        final BufferedImage baseImage = BufferedImages.createImage(width, height, 1 , DataBuffer.TYPE_FLOAT);
        final WritableRaster baseRaster1 = baseImage.getRaster();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                baseRaster1.setSample(x, y, 0, (y<height/2) ? fillValue1 : fillValue2 );
            }
        }
        return baseImage;
    }

    private static void testImageContent(RenderedImage image, int width, int height, float value1, float value2){
        final Raster resultRaster = image.getData();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                float v = resultRaster.getSampleFloat(x, y, 0);
                Assert.assertEquals( (y<height/2) ? value1 : value2, v, DELTA);
            }
        }
    }

}
