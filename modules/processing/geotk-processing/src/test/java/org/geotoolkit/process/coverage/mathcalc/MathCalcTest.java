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

package org.geotoolkit.process.coverage.mathcalc;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Collections;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.memory.MPCoverageStore;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.BufferedImageUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcTest {
    
    private static final float DELTA = 0.000000001f;
    
    /**
     * This test is expected to copy values from one coverage to the other.
     */
    @Test
    public void passthroughtTest() throws Exception{
        final int width = 512;
        final int height = 300;
        
        final CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);
        
        //create base coverage
        final GridCoverage2D baseCoverage = createCoverage2D(env, width, height, 15.5f, -3.0f);
        
        
        //create output coverage ref
        final Name n = new DefaultName("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageReference outRef = (PyramidalCoverageReference) store.create(n);
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new GridSampleDimension("data")));
        outRef.setSampleModel(baseCoverage.getRenderedImage().getSampleModel());
        final Pyramid pyramid = outRef.createPyramid(crs);
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(env.getMinimum(0), env.getMaximum(1));
        outRef.createMosaic(pyramid.getId(), new Dimension(1, 1), new Dimension(width, height), corner, 0.1);
        
        
        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new Coverage[]{baseCoverage}, 
                "A",
                new String[]{"A"}, 
                outRef);
        process.call();
        
        final GridCoverage2D result = (GridCoverage2D) outRef.acquireReader().read(0, null);
        final Raster resultRaster = result.getRenderedImage().getData();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                float v = resultRaster.getSampleFloat(x, y, 0);
                Assert.assertEquals( (y<height/2) ? 15.5f : -3.0f, v, DELTA);
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
        
        final CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);
        
        //create base coverage
        final GridCoverage2D baseCoverage = createCoverage2D(env, width, height, 15.5f, -2.0f);
        
        
        //create output coverage ref
        final Name n = new DefaultName("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageReference outRef = (PyramidalCoverageReference) store.create(n);
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new GridSampleDimension("data")));
        outRef.setSampleModel(baseCoverage.getRenderedImage().getSampleModel());
        final Pyramid pyramid = outRef.createPyramid(crs);
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(env.getMinimum(0), env.getMaximum(1));
        outRef.createMosaic(pyramid.getId(), new Dimension(1, 1), new Dimension(width, height), corner, 0.1);
        
        
        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new Coverage[]{baseCoverage}, 
                "A+10",
                new String[]{"A"}, 
                outRef);
        process.call();
        
        final GridCoverage2D result = (GridCoverage2D) outRef.acquireReader().read(0, null);
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
        
        final CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);
        
        //create base coverage
        final GridCoverage2D baseCoverage1 = createCoverage2D(env, width, height, 15.5f,  3.0f);
        final GridCoverage2D baseCoverage2 = createCoverage2D(env, width, height, -9.0f, 20.0f);
        
        
        //create output coverage ref
        final Name n = new DefaultName("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageReference outRef = (PyramidalCoverageReference) store.create(n);
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new GridSampleDimension("data")));
        outRef.setSampleModel(baseCoverage1.getRenderedImage().getSampleModel());
        final Pyramid pyramid = outRef.createPyramid(crs);
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setCoordinate(env.getMinimum(0), env.getMaximum(1));
        outRef.createMosaic(pyramid.getId(), new Dimension(1, 1), new Dimension(width, height), corner, 0.1);
        
        
        //run math calc process
        final MathCalcProcess process = new MathCalcProcess(
                new Coverage[]{baseCoverage1, baseCoverage2}, 
                "(A+B)*5",
                new String[]{"A","B"}, 
                outRef);
        process.call();
        
        final GridCoverage2D result = (GridCoverage2D) outRef.acquireReader().read(0, null);
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
     * @throws Exception 
     */
    //@Test
    public void coverage4DTest() throws Exception{
        //TODO
    }
    
    /**
     * 
     * @param env
     * @param width
     * @param height
     * @param fillValue1 value used in first vertical half of the image
     * @param fillValue2 value used in secong vertical half of the image
     * @return 
     */
    private static GridCoverage2D createCoverage2D(Envelope env, int width, int height, float fillValue1, float fillValue2){
        final BufferedImage baseImage1 = BufferedImageUtilities.createImage(width, height, 1 , DataBuffer.TYPE_FLOAT);
        final WritableRaster baseRaster1 = baseImage1.getRaster();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                baseRaster1.setSample(x, y, 0, (y<height/2) ? fillValue1 : fillValue2 );
            }
        }
        
        final GridCoverageBuilder baseGcb1 = new GridCoverageBuilder();
        baseGcb1.setName("base");
        baseGcb1.setRenderedImage(baseImage1);
        baseGcb1.setEnvelope(env);
        return baseGcb1.getGridCoverage2D();
    }
    
}
