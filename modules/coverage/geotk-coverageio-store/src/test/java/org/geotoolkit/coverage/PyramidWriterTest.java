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
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.memory.MPCoverageStore;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.referencing.CRS;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.*;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.type.Name;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

/**
 * Test pyramid coverage writer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PyramidWriterTest {
    
    private static final Name NAME = new DefaultName("test");
    private static final CoordinateReferenceSystem CRS84 = WGS84;
    private static final CoordinateReferenceSystem EPSG4326;
    private static final GeneralDirectPosition UL84;
    private static final GeneralDirectPosition UL4326;
    static {
        try {
            EPSG4326 = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException(ex);
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }
        UL84 = new GeneralDirectPosition(CRS84);
        UL84.setOrdinate(0, -180);
        UL84.setOrdinate(1, 90);
        
        UL4326 = new GeneralDirectPosition(EPSG4326);
        UL4326.setOrdinate(0, -90);
        UL4326.setOrdinate(1, 180);
    }
    
    /**
     * Test writing over an existing mosaic 1x1.
     */
    @Test
    public void testSingleGridOverride() throws DataStoreException{
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalModel ref = (PyramidalModel) store.create(NAME);
        final Pyramid pyramid = ref.createPyramid(CRS84);
        final GridMosaic mosaic = ref.createMosaic(pyramid.getId(), new Dimension(1, 1), new Dimension(360, 180), UL84, 1);
        ref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(360, 180, Color.BLACK));
        
        //sanity check
        RenderedImage candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 360, 180, Color.BLACK);
        
        //write over the tile
        final GridCoverageWriter writer = ref.createWriter();
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        final GeneralEnvelope env = new GeneralEnvelope(CRS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        param.setEnvelope(env);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setGridToCRS(new AffineTransform(1, 0, 0, -1, -180, 90));
        gcb.setCoordinateReferenceSystem(CRS84);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(createImage(360, 180, Color.RED));
        writer.write(gcb.build(), param);
        
        //image should be red
        candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 360, 180, Color.RED);
    }
    
    /**
     * Test writing over an existing mosaic 4x4.
     */
    @Test
    public void testQuadGridOverride() throws DataStoreException{
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalModel ref = (PyramidalModel) store.create(NAME);
        final Pyramid pyramid = ref.createPyramid(CRS84);
        final GridMosaic mosaic = ref.createMosaic(pyramid.getId(), new Dimension(4, 2), new Dimension(9, 9), UL84, 10);
        for(int y=0;y<2;y++){
            for(int x=0;x<4;x++){
                ref.writeTile(pyramid.getId(), mosaic.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        
        //sanity check
        RenderedImage candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 36, 18, Color.BLACK);
        
        //write over the tile
        final GridCoverageWriter writer = ref.createWriter();
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        final GeneralEnvelope env = new GeneralEnvelope(CRS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        param.setEnvelope(env);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setGridToCRS(new AffineTransform(10, 0, 0, -10, -180, 90));
        gcb.setCoordinateReferenceSystem(CRS84);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(createImage(36, 18, Color.RED));
        writer.write(gcb.build(), param);
        
        //image should be red
        candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 36, 18, Color.RED);
    }
    
    /**
     * Test writing only a piece over an existing mosaic 4x2.
     */
    @Test
    public void testPartialQuadGridOverride() throws DataStoreException{
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalModel ref = (PyramidalModel) store.create(NAME);
        final Pyramid pyramid = ref.createPyramid(CRS84);
        final GridMosaic mosaic = ref.createMosaic(pyramid.getId(), new Dimension(4, 2), new Dimension(9, 9), UL84, 10);
        for(int y=0;y<2;y++){
            for(int x=0;x<4;x++){
                ref.writeTile(pyramid.getId(), mosaic.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        
        //sanity check
        RenderedImage candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 36, 18, Color.BLACK);
        
        //write over the tile
        final GridCoverageWriter writer = ref.createWriter();
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        final GeneralEnvelope env = new GeneralEnvelope(CRS84);
        env.setRange(0, -120, +70);
        env.setRange(1, -30, +60);
        param.setEnvelope(env);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setGridToCRS(new AffineTransform(10, 0, 0, -10, -120, 60));
        gcb.setCoordinateReferenceSystem(CRS84);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(createImage(19, 9, Color.RED));
        writer.write(gcb.build(), param);
        
        //image should be black/red
        candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        final Raster data = candidate.getData();
        
        final int[] black = new int[]{Color.BLACK.getRed(),Color.BLACK.getGreen(),Color.BLACK.getBlue(),Color.BLACK.getAlpha()};
        final int[] red = new int[]{Color.RED.getRed(),Color.RED.getGreen(),Color.RED.getBlue(),Color.RED.getAlpha()};
        final int[] buffer = new int[4];
        for(int y=0;y<18;y++){
            for(int x=0;x<36;x++){
                data.getPixel(x, y, buffer);
                if(x>=6 && x<25 && y>=3 && y<12){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
                
            }
        }
    }
    
    /**
     * Test writing only a piece over two existing mosaic 4x2 and 2x1.
     */
    @Test
    public void testPartialQuadGridOverride2() throws DataStoreException, IOException{
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalModel ref = (PyramidalModel) store.create(NAME);
        final Pyramid pyramid = ref.createPyramid(CRS84);
        final GridMosaic mosaic1 = ref.createMosaic(pyramid.getId(), new Dimension(4, 2), new Dimension(9, 9), UL84, 10);
        for(int y=0;y<2;y++){
            for(int x=0;x<4;x++){
                ref.writeTile(pyramid.getId(), mosaic1.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        final GridMosaic mosaic2 = ref.createMosaic(pyramid.getId(), new Dimension(2, 1), new Dimension(9, 9), UL84, 20);
        for(int y=0;y<1;y++){
            for(int x=0;x<2;x++){
                ref.writeTile(pyramid.getId(), mosaic2.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        
        //sanity check
        RenderedImage candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 36, 18, Color.BLACK);
        
        //write over the tile
        final GridCoverageWriter writer = ref.createWriter();
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        final GeneralEnvelope env = new GeneralEnvelope(CRS84);
        env.setRange(0, -120, +70);
        env.setRange(1, -30, +60);
        param.setEnvelope(env);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setGridToCRS(new AffineTransform(10, 0, 0, -10, -120, 60));
        gcb.setCoordinateReferenceSystem(CRS84);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(createImage(19, 9, Color.RED));
        writer.write(gcb.build(), param);
        
        //lower image should be black/red---------------------------------------
        candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        Raster data = candidate.getData();
        
        final int[] black = new int[]{Color.BLACK.getRed(),Color.BLACK.getGreen(),Color.BLACK.getBlue(),Color.BLACK.getAlpha()};
        final int[] red = new int[]{Color.RED.getRed(),Color.RED.getGreen(),Color.RED.getBlue(),Color.RED.getAlpha()};
        final int[] buffer = new int[4];
        for(int y=0;y<18;y++){
            for(int x=0;x<36;x++){
                data.getPixel(x, y, buffer);
                if(x>=6 && x<25 && y>=3 && y<12){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
        //the higher tiles------------------------------------------------------
        final BufferedImage top1 = mosaic2.getTile(0, 0, null).getImageReader().read(0);
        final BufferedImage top2 = mosaic2.getTile(1, 0, null).getImageReader().read(0);
        try {
            ImageIO.write(top1, "png", new File("/home/jsorel/top1.png"));
            ImageIO.write(top2, "png", new File("/home/jsorel/top2.png"));
        } catch (IOException ex) {
            Logger.getLogger(PyramidWriterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        data = top1.getData();
        for(int y=0;y<9;y++){
            for(int x=0;x<9;x++){
                data.getPixel(x, y, buffer);
                if(x>=3 && y>=2 && y<6){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
        data = top2.getData();
        for(int y=0;y<9;y++){
            for(int x=0;x<9;x++){
                data.getPixel(x, y, buffer);
                if(x>=0 & x<3 && y>=2 && y<6){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
    }
    
    /**
     * Test writing only a piece over two existing mosaic 4x2 and 2x1.
     * Source coverage in not in same crs (axis flip) 
     * CRS:84 > EPSG:4326
     */
    @Test
    public void testPartialQuadGridOverrideFlip() throws DataStoreException, IOException, NoSuchAuthorityCodeException, FactoryException{
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalModel ref = (PyramidalModel) store.create(NAME);
        final Pyramid pyramid = ref.createPyramid(EPSG4326);
        final GridMosaic mosaic1 = ref.createMosaic(pyramid.getId(), new Dimension(2, 4), new Dimension(9, 9), UL4326, 10);
        for(int y=0;y<4;y++){
            for(int x=0;x<2;x++){
                ref.writeTile(pyramid.getId(), mosaic1.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        final GridMosaic mosaic2 = ref.createMosaic(pyramid.getId(), new Dimension(1, 2), new Dimension(9, 9), UL4326, 20);
        for(int y=0;y<2;y++){
            for(int x=0;x<1;x++){
                ref.writeTile(pyramid.getId(), mosaic2.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        
        //sanity check
        RenderedImage candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 18, 36, Color.BLACK);
        
       //write over the tile
        final GridCoverageWriter writer = ref.createWriter();
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        final GeneralEnvelope env = new GeneralEnvelope(CRS84);
        env.setRange(0, -120, +70);
        env.setRange(1, -30, +60);
        param.setEnvelope(env);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setGridToCRS(new AffineTransform(10, 0, 0, -10, -120, 60));
        gcb.setCoordinateReferenceSystem(CRS84);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(createImage(19, 9, Color.RED));
        writer.write(gcb.build(), param);
        
        //lower image should be black/red---------------------------------------
        candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        Raster data = candidate.getData();
        try {
            ImageIO.write(candidate, "png", new File("/home/jsorel/all.png"));
        } catch (IOException ex) {
            Logger.getLogger(PyramidWriterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        final int[] black = new int[]{Color.BLACK.getRed(),Color.BLACK.getGreen(),Color.BLACK.getBlue(),Color.BLACK.getAlpha()};
        final int[] red = new int[]{Color.RED.getRed(),Color.RED.getGreen(),Color.RED.getBlue(),Color.RED.getAlpha()};
        final int[] buffer = new int[4];
        for(int y=0;y<36;y++){
            for(int x=0;x<18;x++){
                data.getPixel(x, y, buffer);
                if(y>=11 && y<30 && x>=6 && x<15){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
        //the higher tiles------------------------------------------------------
        final BufferedImage top1 = mosaic2.getTile(0, 0, null).getImageReader().read(0);
        final BufferedImage top2 = mosaic2.getTile(0, 1, null).getImageReader().read(0);
        try {
            ImageIO.write(top1, "png", new File("/home/jsorel/top1.png"));
            ImageIO.write(top2, "png", new File("/home/jsorel/top2.png"));
        } catch (IOException ex) {
            Logger.getLogger(PyramidWriterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        data = top1.getData();
        for(int y=0;y<9;y++){
            for(int x=0;x<9;x++){
                data.getPixel(x, y, buffer);
                if(y>=6 && x>=3 && x<7){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
        data = top2.getData();
        for(int y=0;y<9;y++){
            for(int x=0;x<9;x++){
                data.getPixel(x, y, buffer);
                if(y>=0 & y<6 && x>=3 && x<7){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
    }
    
    /**
     * Test writing only a piece over two existing mosaic 4x2 and 2x1.
     * Source coverage in not in same crs (axis flip)
     * EPSG:4326 > CRS:84
     */
    @Test
    public void testPartialQuadGridOverrideFlip2() throws DataStoreException, IOException, NoSuchAuthorityCodeException, FactoryException{
        final MPCoverageStore store = new MPCoverageStore();
        final PyramidalModel ref = (PyramidalModel) store.create(NAME);
        final Pyramid pyramid = ref.createPyramid(CRS84);
        final GridMosaic mosaic1 = ref.createMosaic(pyramid.getId(), new Dimension(4, 2), new Dimension(9, 9), UL84, 10);
        for(int y=0;y<2;y++){
            for(int x=0;x<4;x++){
                ref.writeTile(pyramid.getId(), mosaic1.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        final GridMosaic mosaic2 = ref.createMosaic(pyramid.getId(), new Dimension(2, 1), new Dimension(9, 9), UL84, 20);
        for(int y=0;y<1;y++){
            for(int x=0;x<2;x++){
                ref.writeTile(pyramid.getId(), mosaic2.getId(), x, y, createImage(9, 9, Color.BLACK));
            }
        }
        
        //sanity check
        RenderedImage candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        testImage(candidate, 36, 18, Color.BLACK);
        
        //write over the tile
        final GridCoverageWriter writer = ref.createWriter();
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        final GeneralEnvelope env = new GeneralEnvelope(EPSG4326);
        env.setRange(0, -30, +60);
        env.setRange(1, -120, +70);
        param.setEnvelope(env);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setGridToCRS(new AffineTransform(-10, 0, 0, 10, 60,-120));
        gcb.setCoordinateReferenceSystem(EPSG4326);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(createImage(9, 19, Color.RED));
        writer.write(gcb.build(), param);
        
        //lower image should be black/red---------------------------------------
        candidate = ((GridCoverage2D)ref.createReader().read(0, null)).getRenderedImage();
        Raster data = candidate.getData();
        
        final int[] black = new int[]{Color.BLACK.getRed(),Color.BLACK.getGreen(),Color.BLACK.getBlue(),Color.BLACK.getAlpha()};
        final int[] red = new int[]{Color.RED.getRed(),Color.RED.getGreen(),Color.RED.getBlue(),Color.RED.getAlpha()};
        final int[] buffer = new int[4];
        for(int y=0;y<18;y++){
            for(int x=0;x<36;x++){
                data.getPixel(x, y, buffer);
                if(x>=6 && x<25 && y>=3 && y<12){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
        //the higher tiles------------------------------------------------------
        final BufferedImage top1 = mosaic2.getTile(0, 0, null).getImageReader().read(0);
        final BufferedImage top2 = mosaic2.getTile(1, 0, null).getImageReader().read(0);
        try {
            ImageIO.write(top1, "png", new File("/home/jsorel/top1.png"));
            ImageIO.write(top2, "png", new File("/home/jsorel/top2.png"));
        } catch (IOException ex) {
            Logger.getLogger(PyramidWriterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        data = top1.getData();
        for(int y=0;y<9;y++){
            for(int x=0;x<9;x++){
                data.getPixel(x, y, buffer);
                if(x>=3 && y>=2 && y<6){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
        data = top2.getData();
        for(int y=0;y<9;y++){
            for(int x=0;x<9;x++){
                data.getPixel(x, y, buffer);
                if(x>=0 & x<3 && y>=2 && y<6){
                    assertArrayEquals(""+y+" "+x,red, buffer);
                }else{
                    assertArrayEquals(""+y+" "+x,black, buffer);
                }
            }
        }
        
    }
    
    
    private static void testImage(RenderedImage img, int width, int height, Color fill){
        assertNotNull(img);
        assertEquals(img.getWidth(), width);
        assertEquals(img.getHeight(), height);
        final int[] color = new int[]{fill.getRed(),fill.getGreen(),fill.getBlue(),fill.getAlpha()};
        final int[] buffer = new int[4];
        final Raster data = img.getData();
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                data.getPixel(x, y, buffer);
                assertArrayEquals(color, buffer);
            }
        }
        
    }
    
    private static BufferedImage createImage(int width, int height, Color color){
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        return image;
    }
    
}
