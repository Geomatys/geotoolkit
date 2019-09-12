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
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.data.multires.DefiningMosaic;
import org.geotoolkit.data.multires.DefiningPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Pyramid store features and write tests.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractPyramidalModelStoreTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.00000001;

    private DataStore store;
    private DirectPosition corner;
    private CoordinateReferenceSystem crs;

    //RGBA reference
    private PyramidalCoverageResource rgbaCoverageRef;
    private ColorModel rgbaColorModel;

    //Float 1 band reference
    private PyramidalCoverageResource float1bCoverageRef;
    private ColorModel float1bColorModel;

    protected abstract DataStore createStore() throws Exception ;

    private DataStore getCoverageStore() throws Exception {

        if(store != null){
            return store;
        }
        store = createStore();
        final WritableAggregate agg = (WritableAggregate) store;
        crs = CRS.forCode("EPSG:3395");

        ////////////////////////////////////////////////////////////////////////
        //create a small RGBA pyramid //////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        final GenericName rgbaName = NamesExt.create("rgba");
        rgbaCoverageRef = (PyramidalCoverageResource) agg.add(new DefiningCoverageResource(rgbaName));
        rgbaCoverageRef.setPackMode(ViewType.RENDERED);

        //define the coverage informations
        rgbaColorModel = createRGBA(Color.RED).getColorModel();
//        rgbaCoverageRef.setColorModel(rgbaColorModel);//-- temporary in comment in attempt to update TiffImageReader

        corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, 100);
        corner.setOrdinate(1, 20);

        final Pyramid rgbaPyramid = (Pyramid) rgbaCoverageRef.createModel(new DefiningPyramid(crs));
        final Mosaic rgbaMosaic1 = rgbaPyramid.createMosaic(new DefiningMosaic(null, corner, 1, new Dimension(10, 10), new Dimension(2, 2)));
        final Mosaic rgbaMosaic2 = rgbaPyramid.createMosaic(new DefiningMosaic(null, corner, 0.5, new Dimension(10, 10), new Dimension(4, 3)));

        //insert tiles
        rgbaMosaic1.writeTiles(Stream.of(
                new DefaultImageTile(createRGBA(Color.RED  ), 0, 0),
                new DefaultImageTile(createRGBA(Color.GREEN), 1, 0),
                new DefaultImageTile(createRGBA(Color.BLUE ), 0, 1),
                new DefaultImageTile(createRGBA(Color.BLACK), 1, 1)
            ), null);

        rgbaMosaic2.writeTiles(Stream.of(
                new DefaultImageTile(createRGBA(Color.RED       ), 0, 0),
                new DefaultImageTile(createRGBA(Color.GREEN     ), 1, 0),
                new DefaultImageTile(createRGBA(Color.BLUE      ), 2, 0),
                new DefaultImageTile(createRGBA(Color.BLACK     ), 3, 0),
                new DefaultImageTile(createRGBA(Color.CYAN      ), 0, 1),
                new DefaultImageTile(createRGBA(Color.MAGENTA   ), 1, 1),
                new DefaultImageTile(createRGBA(Color.YELLOW    ), 2, 1),
                new DefaultImageTile(createRGBA(Color.PINK      ), 3, 1),
                new DefaultImageTile(createRGBA(Color.DARK_GRAY ), 0, 2),
                new DefaultImageTile(createRGBA(Color.LIGHT_GRAY), 1, 2),
                new DefaultImageTile(createRGBA(Color.WHITE     ), 2, 2),
                new DefaultImageTile(createRGBA(Color.BLACK     ), 3, 2)
            ), null);

        ////////////////////////////////////////////////////////////////////////
        //create a small Float 1 band pyramid //////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        final GenericName float1bName = NamesExt.create("float1b");
        float1bCoverageRef = (PyramidalCoverageResource) agg.add(new DefiningCoverageResource(float1bName));
        float1bCoverageRef.setPackMode(ViewType.GEOPHYSICS);

        //define the coverage informations
        float1bColorModel = createFloat(1.1f).getColorModel();
//        float1bCoverageRef.setColorModel(float1bColorModel);//-- temporary in comment in attempt to update TiffImageReader

        corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, 100);
        corner.setOrdinate(1, 20);

        final Pyramid float1bPyramid = (Pyramid) float1bCoverageRef.createModel(new DefiningPyramid(crs));
        final Mosaic float1bMosaic1 = float1bPyramid.createMosaic(new DefiningMosaic(null, corner, 1, new Dimension(10, 10), new Dimension(2, 2)));
        final Mosaic float1bMosaic2 = float1bPyramid.createMosaic(new DefiningMosaic(null, corner, 0.5, new Dimension(10, 10), new Dimension(4, 3)));

        //insert tiles
        float1bMosaic1.writeTiles(Stream.of(
                new DefaultImageTile(createFloat(1.1f), 0, 0),
                new DefaultImageTile(createFloat(2.2f), 1, 0),
                new DefaultImageTile(createFloat(3.3f), 0, 1),
                new DefaultImageTile(createFloat(4.4f), 1, 1)
            ), null);

        float1bMosaic2.writeTiles(Stream.of(
                new DefaultImageTile(createFloat(-1.1f  ), 0, 0),
                new DefaultImageTile(createFloat(-2.2f  ), 1, 0),
                new DefaultImageTile(createFloat(-3.3f  ), 2, 0),
                new DefaultImageTile(createFloat(-4.4f  ), 3, 0),
                new DefaultImageTile(createFloat(-5.5f  ), 0, 1),
                new DefaultImageTile(createFloat(-6.6f  ), 1, 1),
                new DefaultImageTile(createFloat(-7.7f  ), 2, 1),
                new DefaultImageTile(createFloat(-8.8f  ), 3, 1),
                new DefaultImageTile(createFloat(-9.9f  ), 0, 2),
                new DefaultImageTile(createFloat(-10.10f), 1, 2),
                new DefaultImageTile(createFloat(-11.11f), 2, 2),
                new DefaultImageTile(createFloat(-12.12f), 3, 2)
            ), null);

        return store;
    }

    private static BufferedImage createFloat(final float val){
        final BufferedImage buffer = BufferedImages.createImage(10, 10, 1, DataBuffer.TYPE_FLOAT);
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
        final GridCoverage coverage = rgbaCoverageRef.read(null);

        //check defined color model
        //testColorModel(rgbaColorModel, rgbaCoverageRef.getColorModel());

        //check coverage informations
        final CoordinateReferenceSystem covcrs = coverage.getCoordinateReferenceSystem();
        assertTrue(Utilities.equalsIgnoreMetadata(crs,  covcrs));
        final Envelope env = coverage.getGridGeometry().getEnvelope();
        assertEquals(corner.getOrdinate(0), env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(3*10)*0.5, env.getMinimum(1), DELTA);
        assertTrue(Utilities.equalsIgnoreMetadata(crs,  env.getCoordinateReferenceSystem()));

        //check tile aggregation
        final RenderedImage img = coverage.render(null);
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
        final GridCoverage coverage = float1bCoverageRef.read(null);

        //check defined color model, do not test the colorspace
        //testColorModel(float1bColorModel, float1bCoverageRef.getColorModel());


        //check coverage informations
        final CoordinateReferenceSystem covcrs = coverage.getCoordinateReferenceSystem();
        assertTrue(Utilities.equalsIgnoreMetadata(crs,  covcrs));
        final Envelope env = coverage.getGridGeometry().getEnvelope();
        assertEquals(corner.getOrdinate(0), env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(3*10)*0.5, env.getMinimum(1), DELTA);
        assertTrue(Utilities.equalsIgnoreMetadata(crs,  env.getCoordinateReferenceSystem()));

        //check tile aggregation
        final RenderedImage img = coverage.render(null);
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
     * Read and image features.
     * @throws Exception
     */
    @Test
    public void reduceAreaTest() throws Exception{

        //load the coverage store
        getCoverageStore();

        final GeneralEnvelope paramEnv = new GeneralEnvelope(crs);
        paramEnv.setRange(0, corner.getOrdinate(0) +(1*10)*1, corner.getOrdinate(0) +(2*10)*1);
        paramEnv.setRange(1, corner.getOrdinate(1) -(2*10)*1, corner.getOrdinate(1));
        //we should obtain tiles [1,0] and [1,1]

        final GridCoverage coverage = rgbaCoverageRef.read(rgbaCoverageRef.getGridGeometry().derive().subgrid(paramEnv, 1.2, 1.2).build());

        //check coverage informations
        assertTrue(Utilities.equalsApproximately(crs,  coverage.getCoordinateReferenceSystem()));
        final Envelope env = coverage.getGridGeometry().getEnvelope();
        assertEquals(corner.getOrdinate(0) +(1*10)*1, env.getMinimum(0), DELTA);
        assertEquals(corner.getOrdinate(1), env.getMaximum(1), DELTA);
        assertEquals(corner.getOrdinate(0) +(1*10)*1+(1*10)*1, env.getMaximum(0), DELTA);
        assertEquals(corner.getOrdinate(1) -(2*10)*1, env.getMinimum(1), DELTA);
        assertTrue(Utilities.equalsApproximately(crs,  env.getCoordinateReferenceSystem()));


        //check tile aggregation
        final RenderedImage img = coverage.render(null);
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
