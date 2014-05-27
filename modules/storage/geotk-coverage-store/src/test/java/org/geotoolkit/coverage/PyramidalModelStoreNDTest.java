/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;

import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class PyramidalModelStoreNDTest {

    private static final double DELTA = 0.00000001;

    private static final double corner_long = -180;
    private static final double corner_lat  = 90;
    private static final double[] corner_v   = {-15, 46.58};
    // store an integer for each tile color, used as R,G,B
    // vertical, scale, tile col, tile row
    private final int[][][][] colors = new int[2][2][0][0];

    private CoverageStore store;
    private CoordinateReferenceSystem crs;
    private PyramidalCoverageReference ref;

    protected abstract CoverageStore createStore() throws Exception ;

    private CoverageStore getCoverageStore() throws Exception {

        if(store != null){
            return store;
        }

        //create a small pyramid
        store = createStore();
        final CoordinateReferenceSystem horizontal = CRS.decode("EPSG:4326",true);
        final CoordinateReferenceSystem vertical = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
        crs = new DefaultCompoundCRS("3dcrs", horizontal,vertical);
        
        final DefaultName name = new DefaultName("test");
        ref = (PyramidalCoverageReference) store.create(name);

        //prepare expected colors
        int color = 0;
        for(int v=0;v<corner_v.length;v++){
            colors[v][0] = new int[2][2];
            colors[v][1] = new int[4][3];
            colors[v][0][0][0] = color++;
            colors[v][0][1][0] = color++;
            colors[v][0][0][1] = color++;
            colors[v][0][1][1] = color++;
            colors[v][1][0][0] = color++;
            colors[v][1][1][0] = color++;
            colors[v][1][2][0] = color++;
            colors[v][1][3][0] = color++;
            colors[v][1][0][1] = color++;
            colors[v][1][1][1] = color++;
            colors[v][1][2][1] = color++;
            colors[v][1][3][1] = color++;
            colors[v][1][0][2] = color++;
            colors[v][1][1][2] = color++;
            colors[v][1][2][2] = color++;
            colors[v][1][3][2] = color++;
        }
        
        final Pyramid pyramid = ref.createPyramid(crs);
        for(int v=0;v<corner_v.length;v++){
            final GridMosaic mosaic_s0 = ref.createMosaic(pyramid.getId(),
                    new Dimension(2, 2),new Dimension(10, 10) ,
                    createCorner(corner_long,corner_lat,corner_v[v]),1);
            final GridMosaic mosaic_s1 = ref.createMosaic(pyramid.getId(),
                    new Dimension(4, 3),new Dimension(10, 10) ,
                    createCorner(corner_long,corner_lat,corner_v[v]),0.5);
            
            //insert tiles
            ref.writeTile(pyramid.getId(), mosaic_s0.getId(), 0, 0, createImage(colors[v][0][0][0]));
            ref.writeTile(pyramid.getId(), mosaic_s0.getId(), 1, 0, createImage(colors[v][0][1][0]));
            ref.writeTile(pyramid.getId(), mosaic_s0.getId(), 0, 1, createImage(colors[v][0][0][1]));
            ref.writeTile(pyramid.getId(), mosaic_s0.getId(), 1, 1, createImage(colors[v][0][1][1]));
 
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 0, 0, createImage(colors[v][1][0][0]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 1, 0, createImage(colors[v][1][1][0]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 2, 0, createImage(colors[v][1][2][0]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 3, 0, createImage(colors[v][1][3][0]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 0, 1, createImage(colors[v][1][0][1]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 1, 1, createImage(colors[v][1][1][1]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 2, 1, createImage(colors[v][1][2][1]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 3, 1, createImage(colors[v][1][3][1]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 0, 2, createImage(colors[v][1][0][2]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 1, 2, createImage(colors[v][1][1][2]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 2, 2, createImage(colors[v][1][2][2]));
            ref.writeTile(pyramid.getId(), mosaic_s1.getId(), 3, 2, createImage(colors[v][1][3][2]));
        }
        
        

        return store;
    }

    private Envelope createEnvelope(double... coords){
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        for(int i=0;i<coords.length;i+=2){
            env.setRange(i/2, coords[i], coords[i+1]);
        }
        return env;
    }
    
    private GeneralDirectPosition createCorner(double... values) {
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        for (int i = 0; i < values.length; i++) {
            corner.setOrdinate(i, values[i]);
        }
        return corner;
    }

    private static BufferedImage createImage(int color) {
        color = color % 255;
        final BufferedImage buffer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = buffer.createGraphics();
        g.setColor(new Color(color, color, color));
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        return buffer;
    }

    /**
     * Read the full image.
     * @throws Exception
     */
    @Test
    public void checkMetaTest() throws Exception{
        //load the coverage store
        getCoverageStore();
        final GridCoverageReader reader = ref.acquireReader();
        
        //check the image size
        final GridGeometry gridGeom = reader.getGridGeometry(ref.getImageIndex());
        final GridEnvelope gridEnv = gridGeom.getExtent();
        assertEquals( 3, gridEnv.getDimension());
        assertEquals( 0, gridEnv.getLow(0));
        assertEquals(39, gridEnv.getHigh(0));
        assertEquals( 0, gridEnv.getLow(1));
        assertEquals(29, gridEnv.getHigh(1));
        assertEquals( 0, gridEnv.getLow(2));
        assertEquals( 1, gridEnv.getHigh(2));
        
        final Envelope env = ref.getPyramidSet().getEnvelope();
        assertEquals(-180, env.getMinimum(0), DELTA);
        assertEquals(-180 +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(  90, env.getMaximum(1), DELTA);
        assertEquals(  90 -(2*10)*1, env.getMinimum(1), DELTA);
        assertEquals(corner_v[0], env.getMinimum(2), DELTA);
        assertEquals(corner_v[1], env.getMaximum(2), DELTA);
        
        ref.recycle(reader);
    }
    
    /**
     * Read with no parameter, we should obtain the most accurate data
     */
    @Test
    public void readDefaultTest() throws Exception{
        getCoverageStore();
        final GridCoverageReader reader = ref.acquireReader();
        
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(ref.getImageIndex(), null);
        checkCoverage(coverage, 40, 30, colors[0][1], -180,-160,75,90,-15.5,-14.5);          
        ref.recycle(reader);
        
    }
    
    /**
     * Read special scales and dimensions.
     */
    @Test
    public void readSlicesTest() throws Exception{
        getCoverageStore();
        final GridCoverageReader reader = ref.acquireReader();
        final GridCoverageReadParam param = new GridCoverageReadParam();
        
        //expecting image from mosaic with min resolution and vertical -15
        param.setEnvelope(createEnvelope(-180,+180,-90,+90,-15,-15));
        param.setResolution(0.5,0.5,1);
        GridCoverage2D coverage = (GridCoverage2D) reader.read(ref.getImageIndex(), param);
        checkCoverage(coverage, 40, 30, colors[0][1], -180,-160,75,90,-15.5,-14.5);      
        ref.recycle(reader);
        
        //expecting image from mosaic with max resolution and vertical -15
        param.setEnvelope(createEnvelope(-180,+180,-90,+90,-15,-15));
        param.setResolution(1,1,1);
        coverage = (GridCoverage2D) reader.read(ref.getImageIndex(), param);
        checkCoverage(coverage, 20, 20, colors[0][0], -180,-160,70,90,-15.5,-14.5);      
        ref.recycle(reader);
        
        //expecting image from mosaic with min resolution and vertical 46.58
        param.setEnvelope(createEnvelope(-180,+180,-90,+90,46.58,46.58));
        param.setResolution(0.5,0.5,1);
        coverage = (GridCoverage2D) reader.read(ref.getImageIndex(), param);
        checkCoverage(coverage, 40, 30, colors[1][1], -180,-160,75,90,46.08,47.08);      
        ref.recycle(reader);
        
        //expecting image from mosaic with max resolution and vertical 46.58
        param.setEnvelope(createEnvelope(-180,+180,-90,+90,46.58,46.58));
        param.setResolution(1,1,1);
        coverage = (GridCoverage2D) reader.read(ref.getImageIndex(), param);
        checkCoverage(coverage, 20, 20, colors[1][0], -180,-160,70,90,46.08,47.08);      
        ref.recycle(reader);
        
    }
    
    /**
     * 
     * @param coverage coverage to test
     * @param width expected image width
     * @param height expected image height
     * @param colors colors to be found in the image
     * @param envelope expented envelope
     */
    private void checkCoverage(GridCoverage2D coverage, int width, int height, int[][] colors, double... envelope){
        Envelope env = coverage.getEnvelope();
        assertEquals(envelope[0], env.getMinimum(0), DELTA);
        assertEquals(envelope[1], env.getMaximum(0), DELTA);
        assertEquals(envelope[2], env.getMinimum(1), DELTA);
        assertEquals(envelope[3], env.getMaximum(1), DELTA);
        assertEquals(envelope[4], env.getMinimum(2), DELTA);
        assertEquals(envelope[5], env.getMaximum(2), DELTA);
        
        final RenderedImage img = coverage.getRenderedImage();
        final Raster raster = img.getData();
        assertEquals(width,  img.getWidth());
        assertEquals(height, img.getHeight());
        
        //we should have a different color each 10pixel
        final int[] buffer = new int[4];
        for(int x=0;x<img.getWidth()/10;x++){
            for(int y=0;y<img.getHeight()/10;y++){
                raster.getPixel(x*10+5, y*10+5, buffer);
                assertEquals(colors[x][y],buffer[0]);
            }
        }
    }
    
}