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
        
        //create base coverage
        final BufferedImage baseImage = BufferedImageUtilities.createImage(width, height, 1 , DataBuffer.TYPE_FLOAT);
        final WritableRaster baseRaster = baseImage.getRaster();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                baseRaster.setSample(x, y, 0, 15.5f);
            }
        }
        
        final CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);
        final GridCoverageBuilder baseGcb = new GridCoverageBuilder();
        baseGcb.setName("base");
        baseGcb.setRenderedImage(baseImage);
        baseGcb.setEnvelope(env);
        final GridCoverage2D baseCoverage = baseGcb.getGridCoverage2D();
        
        
        //create output coverage ref
        final Name n = new DefaultName("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageReference outRef = (PyramidalCoverageReference) store.create(n);
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new GridSampleDimension("data")));
        outRef.setSampleModel(baseImage.getSampleModel());
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
                Assert.assertEquals(15.5f,v,DELTA);
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
        
        //create base coverage
        final BufferedImage baseImage = BufferedImageUtilities.createImage(width, height, 1 , DataBuffer.TYPE_FLOAT);
        final WritableRaster baseRaster = baseImage.getRaster();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                baseRaster.setSample(x, y, 0, 15.5f);
            }
        }
        
        final CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, 0, 51.2);
        env.setRange(1, 0, 30.0);
        final GridCoverageBuilder baseGcb = new GridCoverageBuilder();
        baseGcb.setName("base");
        baseGcb.setRenderedImage(baseImage);
        baseGcb.setEnvelope(env);
        final GridCoverage2D baseCoverage = baseGcb.getGridCoverage2D();
        
        
        //create output coverage ref
        final Name n = new DefaultName("test");
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalCoverageReference outRef = (PyramidalCoverageReference) store.create(n);
        outRef.setPackMode(ViewType.GEOPHYSICS);
        outRef.setSampleDimensions(Collections.singletonList(new GridSampleDimension("data")));
        outRef.setSampleModel(baseImage.getSampleModel());
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
                Assert.assertEquals(25.5f,v,DELTA);
            }
        }
        
    }
    
}
