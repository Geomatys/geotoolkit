/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.type.DefaultName;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.BufferedImageUtilities;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;

/**
 * Pyramid store read and write tests.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractPyramidalModelStoreTest {

    private static final double DELTA = 0.00000001;

    private CoverageStore store;
    private DirectPosition corner;
    private CoordinateReferenceSystem crs;
    
    //RGBA reference
    private PyramidalCoverageReference rgbaCoverageRef;
    private ColorModel rgbaColorModel;
    
    //Float 1 band reference
    private PyramidalCoverageReference float1bCoverageRef;
    private ColorModel float1bColorModel;

    protected abstract CoverageStore createStore() throws Exception ;

    private CoverageStore getCoverageStore() throws Exception {

        if(store != null){
            return store;
        }
        store = createStore();
        crs = CRS.decode("EPSG:3395");

        ////////////////////////////////////////////////////////////////////////
        //create a small RGBA pyramid //////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        final DefaultName rgbaName = new DefaultName("rgba");
        rgbaCoverageRef = (PyramidalCoverageReference) store.create(rgbaName);
        rgbaCoverageRef.setPackMode(ViewType.RENDERED);
        
        //define the coverage informations
        rgbaColorModel = createRGBA(Color.RED).getColorModel();
        rgbaCoverageRef.setColorModel(rgbaColorModel);
        
        corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, 100);
        corner.setOrdinate(1, 20);

        final Pyramid rgbaPyramid = rgbaCoverageRef.createPyramid(crs);
        final GridMosaic rgbaMosaic1 = rgbaCoverageRef.createMosaic(rgbaPyramid.getId(),
                new Dimension(2, 2),
                new Dimension(10, 10) ,
                corner,
                1);
        final GridMosaic rgbaMosaic2 = rgbaCoverageRef.createMosaic(rgbaPyramid.getId(),
                new Dimension(4, 3),
                new Dimension(10, 10) ,
                corner,
                0.5);

        //insert tiles
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic1.getId(), 0, 0, createRGBA(Color.RED));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic1.getId(), 1, 0, createRGBA(Color.GREEN));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic1.getId(), 0, 1, createRGBA(Color.BLUE));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic1.getId(), 1, 1, createRGBA(Color.BLACK));

        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 0, 0, createRGBA(Color.RED));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 1, 0, createRGBA(Color.GREEN));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 2, 0, createRGBA(Color.BLUE));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 3, 0, createRGBA(Color.BLACK));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 0, 1, createRGBA(Color.CYAN));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 1, 1, createRGBA(Color.MAGENTA));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 2, 1, createRGBA(Color.YELLOW));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 3, 1, createRGBA(Color.PINK));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 0, 2, createRGBA(Color.DARK_GRAY));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 1, 2, createRGBA(Color.LIGHT_GRAY));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 2, 2, createRGBA(Color.WHITE));
        rgbaCoverageRef.writeTile(rgbaPyramid.getId(), rgbaMosaic2.getId(), 3, 2, createRGBA(Color.BLACK));
        
        ////////////////////////////////////////////////////////////////////////
        //create a small Float 1 band pyramid //////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        final DefaultName float1bName = new DefaultName("float1b");
        float1bCoverageRef = (PyramidalCoverageReference) store.create(float1bName);
        float1bCoverageRef.setPackMode(ViewType.GEOPHYSICS);
        
        //define the coverage informations
        float1bColorModel = createFloat(1.1f).getColorModel();
        float1bCoverageRef.setColorModel(float1bColorModel);
        
        corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, 100);
        corner.setOrdinate(1, 20);

        final Pyramid float1bPyramid = float1bCoverageRef.createPyramid(crs);
        final GridMosaic float1bMosaic1 = float1bCoverageRef.createMosaic(float1bPyramid.getId(),
                new Dimension(2, 2),
                new Dimension(10, 10) ,
                corner,
                1);
        final GridMosaic float1bMosaic2 = float1bCoverageRef.createMosaic(float1bPyramid.getId(),
                new Dimension(4, 3),
                new Dimension(10, 10) ,
                corner,
                0.5);

        //insert tiles
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic1.getId(), 0, 0, createFloat(1.1f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic1.getId(), 1, 0, createFloat(2.2f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic1.getId(), 0, 1, createFloat(3.3f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic1.getId(), 1, 1, createFloat(4.4f));

        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 0, 0, createFloat(-1.1f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 1, 0, createFloat(-2.2f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 2, 0, createFloat(-3.3f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 3, 0, createFloat(-4.4f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 0, 1, createFloat(-5.5f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 1, 1, createFloat(-6.6f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 2, 1, createFloat(-7.7f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 3, 1, createFloat(-8.8f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 0, 2, createFloat(-9.9f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 1, 2, createFloat(-10.10f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 2, 2, createFloat(-11.11f));
        float1bCoverageRef.writeTile(float1bPyramid.getId(), float1bMosaic2.getId(), 3, 2, createFloat(-12.12f));
        

        return store;
    }

    private static BufferedImage createFloat(final float val){
        final BufferedImage buffer = BufferedImageUtilities.createImage(10, 10, 1, DataBuffer.TYPE_FLOAT);
        final WritableRaster raster = buffer.getRaster();
        for(int y=0;y<10;y++){
            for(int x=0;x<10;x++){
                raster.setPixel(x,y,new float[]{val});
            }
        }
        return buffer;
    }
    
    private static BufferedImage createRGBA(final Color color){
        final BufferedImage buffer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = buffer.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 10, 10);
        return buffer;
    }

    /**
     * Read the full RGBA image.
     * @throws Exception
     */
    @Test
    public void readRGBANoArgumentTest() throws Exception{
        //load the coverage store
        getCoverageStore();
        final GridCoverageReader reader = rgbaCoverageRef.acquireReader();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, null);
        rgbaCoverageRef.recycle(reader);

        //check defined color model
        //testColorModel(rgbaColorModel, rgbaCoverageRef.getColorModel());
        
        //check coverage informations
        final CoordinateReferenceSystem covcrs = coverage.getCoordinateReferenceSystem();
        assertTrue(CRS.equalsIgnoreMetadata(crs,  covcrs));
        final Envelope env = coverage.getEnvelope();
        assertEquals(corner.getOrdinate(0), env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(3*10)*0.5, env.getMinimum(1), DELTA);
        assertTrue(CRS.equalsIgnoreMetadata(crs,  env.getCoordinateReferenceSystem()));

        //check tile aggregation
        final RenderedImage img = coverage.getRenderedImage();
        final Raster raster = img.getData();

        //check defined color model, do not test the colorspace
        //testColorModel(rgbaColorModel, img.getColorModel());
        
        assertEquals(4*10,img.getWidth());
        assertEquals(3*10,img.getHeight());

        //we should have a different color each 10pixel
        final int[] buffer1 = new int[4];
        final int[] buffer2 = new int[4];
        for(int x=5;x<img.getWidth();x+=10){
            for(int y=5;y<img.getHeight();y+=10){
                raster.getPixel(x, y, buffer2);
                assertFalse(Arrays.equals(buffer1, buffer2));
                System.arraycopy(buffer2, 0, buffer1, 0, 4);
            }
        }

    }
    
    /**
     * Read the full RGBA image.
     * @throws Exception
     */
    @Test
    public void readFloat1BNoArgumentTest() throws Exception{
        //load the coverage store
        getCoverageStore();
        final GridCoverageReader reader = float1bCoverageRef.acquireReader();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, null);
        float1bCoverageRef.recycle(reader);

        //check defined color model, do not test the colorspace
        //testColorModel(float1bColorModel, float1bCoverageRef.getColorModel());
        
        
        //check coverage informations
        final CoordinateReferenceSystem covcrs = coverage.getCoordinateReferenceSystem();
        assertTrue(CRS.equalsIgnoreMetadata(crs,  covcrs));
        final Envelope env = coverage.getEnvelope();
        assertEquals(corner.getOrdinate(0), env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(3*10)*0.5, env.getMinimum(1), DELTA);
        assertTrue(CRS.equalsIgnoreMetadata(crs,  env.getCoordinateReferenceSystem()));

        //check tile aggregation
        final RenderedImage img = coverage.getRenderedImage();
        final Raster raster = img.getData();
        
        //check defined color model, do not test the colorspace
        //testColorModel(float1bColorModel, img.getColorModel());

        assertEquals(4*10,img.getWidth());
        assertEquals(3*10,img.getHeight());

        //we should have a different color each 10pixel
        final int[] buffer1 = new int[4];
        final int[] buffer2 = new int[4];
        for(int x=5;x<img.getWidth();x+=10){
            for(int y=5;y<img.getHeight();y+=10){
                raster.getPixel(x, y, buffer2);
                assertFalse(Arrays.equals(buffer1, buffer2));
                System.arraycopy(buffer2, 0, buffer1, 0, 4);
            }
        }

    }

    /**
     * Read and image subset.
     * @throws Exception
     */
    @Test
    public void reduceAreaTest() throws Exception{

        //load the coverage store
        getCoverageStore();
        final GridCoverageReader reader = rgbaCoverageRef.acquireReader();

        final GeneralEnvelope paramEnv = new GeneralEnvelope(crs);
        paramEnv.setRange(0, corner.getOrdinate(0) +(1*10)*1, corner.getOrdinate(0) +(2*10)*1);
        paramEnv.setRange(1, corner.getOrdinate(1) -(2*10)*1, corner.getOrdinate(1));
        //we should obtain tiles [1,0] and [1,1]

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setCoordinateReferenceSystem(crs);
        param.setResolution(1.2,1.2);
        param.setEnvelope(paramEnv);

        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        rgbaCoverageRef.recycle(reader);

        //check coverage informations
        assertTrue(CRS.equalsApproximatively(crs,  coverage.getCoordinateReferenceSystem()));
        final Envelope env = coverage.getEnvelope();
        assertEquals(corner.getOrdinate(0) +(1*10)*1, env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(1*10)*1+(1*10)*1, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(2*10)*1, env.getMinimum(1), DELTA);
        assertTrue(CRS.equalsApproximatively(crs,  env.getCoordinateReferenceSystem()));


        //check tile aggregation
        final RenderedImage img = coverage.getRenderedImage();
        final Raster raster = img.getData();

        assertEquals(1*10,img.getWidth());
        assertEquals(2*10,img.getHeight());

        //we should have a different color each 10pixel
        final int[] buffer1 = new int[4];
        final int[] buffer2 = new int[4];
        for(int x=5;x<img.getWidth();x+=10){
            for(int y=5;y<img.getHeight();y+=10){
                raster.getPixel(x, y, buffer2);
                assertFalse(Arrays.equals(buffer1, buffer2));
                System.arraycopy(buffer2, 0, buffer1, 0, 4);
            }
        }

    }

    private void testColorModel(ColorModel expected, ColorModel candidate){
        if(expected instanceof DirectColorModel){
            final DirectColorModel edm = (DirectColorModel) expected;
            final DirectColorModel cdm = (DirectColorModel) candidate;
            assertEquals(edm.getPixelSize(), cdm.getPixelSize());
            assertEquals(edm.getRedMask(), cdm.getRedMask());
            assertEquals(edm.getGreenMask(), cdm.getGreenMask());
            assertEquals(edm.getBlueMask(), cdm.getBlueMask());
            assertEquals(edm.getAlphaMask(), cdm.getAlphaMask());
            assertEquals(edm.hasAlpha(), cdm.hasAlpha());
            assertEquals(edm.isAlphaPremultiplied(), cdm.isAlphaPremultiplied());
            assertEquals(edm.getTransferType(), cdm.getTransferType());
            
        }else if(expected instanceof ComponentColorModel){
            final ComponentColorModel edm = (ComponentColorModel) expected;
            final ComponentColorModel cdm = (ComponentColorModel) candidate;
            //assertArrayEquals(edm.getComponentSize(), cdm.getComponentSize()); this is modified by the colorspace
            assertEquals(edm.hasAlpha(), cdm.hasAlpha());
            assertEquals(edm.isAlphaPremultiplied(), cdm.isAlphaPremultiplied());
            assertEquals(edm.getTransparency(), cdm.getTransparency());
            assertEquals(edm.getTransferType(), cdm.getTransferType());
            
        }else{
            assertEquals(float1bColorModel, candidate);
        }
    }
    

}
