/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Arrays;
import org.geotoolkit.coverage.filestore.XMLCoverageStoreFactory;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PyramidalModelReaderTest {
 
    private static final double DELTA = 0.00000001;
    
    private final DirectPosition corner;
    private final CoordinateReferenceSystem crs;
    private final CoverageReference ref;

    public PyramidalModelReaderTest() throws Exception {
        
        final File tempFolder = File.createTempFile("mosaic", "");
        tempFolder.delete();
        tempFolder.mkdirs();
        
        //create a small pyramid
        final ParameterValueGroup params = XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(XMLCoverageStoreFactory.PATH, params).setValue(tempFolder.toURI().toURL());
        
        crs = CRS.decode("EPSG:3395");
        
        final CoverageStore store = CoverageStoreFinder.open(params);
        final DefaultName name = new DefaultName("test");
        ref = store.create(name);
        final PyramidalModel model = (PyramidalModel) ref;
        
        corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, 100);
        corner.setOrdinate(1, 20);
        
        final Pyramid pyramid = model.createPyramid(crs);
        final GridMosaic mosaic1 = model.createMosaic(pyramid.getId(), 
                new Dimension(2, 2), 
                new Dimension(10, 10) , 
                corner, 
                1);
        final GridMosaic mosaic2 = model.createMosaic(pyramid.getId(), 
                new Dimension(4, 3), 
                new Dimension(10, 10) , 
                corner, 
                0.5);
        
        //insert tiles
        final BufferedImage buffer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = buffer.createGraphics();
        
        g.setColor(Color.RED); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic1.getId(), 0, 0, buffer);
        g.setColor(Color.GREEN); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic1.getId(), 1, 0, buffer);
        g.setColor(Color.BLUE); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic1.getId(), 0, 1, buffer);
        g.setColor(Color.BLACK); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic1.getId(), 1, 1, buffer);
        
        g.setColor(Color.RED); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 0, 0, buffer);
        g.setColor(Color.GREEN); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 1, 0, buffer);
        g.setColor(Color.BLUE); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 2, 0, buffer);
        g.setColor(Color.BLACK); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 3, 0, buffer);
        g.setColor(Color.CYAN); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 0, 1, buffer);
        g.setColor(Color.MAGENTA); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 1, 1, buffer);
        g.setColor(Color.YELLOW); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 2, 1, buffer);
        g.setColor(Color.PINK); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 3, 1, buffer);
        g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 0, 2, buffer);
        g.setColor(Color.LIGHT_GRAY); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 1, 2, buffer);
        g.setColor(Color.WHITE); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 2, 2, buffer);
        g.setColor(Color.BLACK); g.fillRect(0, 0, 10, 10);        
        model.writeTile(pyramid.getId(), mosaic2.getId(), 3, 2, buffer);
        
    }
        
    /**
     * Read the full image.
     * @throws Exception 
     */
    @Test
    public void noArgumentTest() throws Exception{
        
        final GridCoverageReader reader = new PyramidalModelReader();
        reader.setInput(ref);
        
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, null);
                
        //check coverage informations
        assertTrue(CRS.equalsIgnoreMetadata(crs,  coverage.getCoordinateReferenceSystem()));
        final Envelope env = coverage.getEnvelope();
        assertEquals(corner.getOrdinate(0), env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(3*10)*0.5, env.getMinimum(1), DELTA);
        assertTrue(CRS.equalsIgnoreMetadata(crs,  env.getCoordinateReferenceSystem()));
        
        
        //check tile aggregation
        final RenderedImage img = coverage.getRenderedImage();
        final Raster raster = img.getData();
        //ImageIO.write(img, "PNG", new File("sanity.png"));
        
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
        
        final GridCoverageReader reader = new PyramidalModelReader();
        reader.setInput(ref);
        
        final GeneralEnvelope paramEnv = new GeneralEnvelope(crs);
        paramEnv.setRange(0, corner.getOrdinate(0) +(1*10)*1, corner.getOrdinate(0) +(2*10)*1);
        paramEnv.setRange(1, corner.getOrdinate(1) -(2*10)*1, corner.getOrdinate(1));
        //we should obtain tiles [1,0] and [1,1]
        
        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setCoordinateReferenceSystem(crs);
        param.setResolution(1.2,1.2);
        param.setEnvelope(paramEnv);
        
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
                
        //check coverage informations
        assertTrue(CRS.equalsIgnoreMetadata(crs,  coverage.getCoordinateReferenceSystem()));
        final Envelope env = coverage.getEnvelope();
        assertEquals(corner.getOrdinate(0) +(1*10)*1, env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(1*10)*1+(1*10)*1, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(2*10)*1, env.getMinimum(1), DELTA);
        assertTrue(CRS.equalsIgnoreMetadata(crs,  env.getCoordinateReferenceSystem()));
        
        
        //check tile aggregation
        final RenderedImage img = coverage.getRenderedImage();
        final Raster raster = img.getData();
        //ImageIO.write(img, "PNG", new File("sanity.png"));
        
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
    
    
}
