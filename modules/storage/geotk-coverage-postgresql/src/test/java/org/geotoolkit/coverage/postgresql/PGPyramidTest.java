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
package org.geotoolkit.coverage.postgresql;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.*;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.storage.coverage.*;
import org.junit.Test;
import org.geotoolkit.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.coverage.postgresql.PGCoverageStoreFactory.*;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assume;
import org.junit.BeforeClass;

import javax.measure.unit.SI;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGPyramidTest {
    
    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");
    private static final double DELTA = 0.00000001;
    private static final CoordinateReferenceSystem CRS_4326;
    
    static{
        try {
            CRS_4326 = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException("Failed to load CRS");
        } catch (FactoryException ex) {
            throw new RuntimeException("Failed to load CRS");
        } 
    }
    
    private CoverageStore store;
    
    private static ParameterValueGroup params;
    
    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = System.getProperty("user.home");
        path += "/.geotoolkit.org/test-pgcoverage.properties";        
        final File f = new File(path);
        Assume.assumeTrue(f.exists());        
        final Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        params = FeatureUtilities.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false);
    }
    
    public PGPyramidTest(){
    }
    
    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }
        
        final CoverageStoreFactory factory = CoverageStoreFinder.getFactoryById("pgraster");
        
        try{
            store = factory.create(params);
        }catch(DataStoreException ex){
            //it may already exist
            store = factory.open(params);
        }
        
        for(Name n : store.getNames()){
            VersionControl vc = store.getVersioning(n);
            store.delete(n);
        }
        assertTrue(store.getNames().isEmpty());
    }
    
    @Test
    public void testInsertUpdateDelete() throws DataStoreException, VersioningException, IOException {
        reload();
        
        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(CRS_4326);
        final Dimension dimension = new Dimension(20, 20);
        upperLeft.setOrdinate(0, -90);
        upperLeft.setOrdinate(1, +180);
        PyramidalCoverageReference cref;
        Pyramid pyramid;
        GridMosaic mosaic;
        BufferedImage image;
        
        final Name name = new DefaultName(null, "versLayer");        
        store.create(name);
        
        //create version 1 -----------------------------------------------------
        cref = (PyramidalCoverageReference) store.getCoverageReference(name);
        assertNotNull(cref);
        
        //test create pyramid
        pyramid = cref.createPyramid(CRS_4326);
        assertEquals(1,cref.getPyramidSet().getPyramids().size());
        
        //test create mosaic
        mosaic = cref.createMosaic(pyramid.getId(), new Dimension(1, 4), dimension, upperLeft, 1);
        pyramid = cref.getPyramidSet().getPyramid(pyramid.getId());
        assertEquals(1,pyramid.getMosaics().size());
        
        //test insert tile
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(dimension, Color.RED));
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 1, createImage(dimension, Color.GREEN));
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 2, createImage(dimension, Color.BLUE));
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 3, createImage(dimension, Color.YELLOW));                
        image = mosaic.getTile(0, 0, null).getImageReader().read(0);
        assertImageColor(image, Color.RED);
        image = mosaic.getTile(0, 1, null).getImageReader().read(1);
        assertImageColor(image, Color.GREEN);
        image = mosaic.getTile(0, 2, null).getImageReader().read(2);
        assertImageColor(image, Color.BLUE);
        image = mosaic.getTile(0, 3, null).getImageReader().read(3);
        assertImageColor(image, Color.YELLOW);
        
        //test delete tile
        cref.deleteTile(pyramid.getId(), mosaic.getId(), 0, 1);
        assertNotNull(mosaic.getTile(0, 2, null).getInput());
        cref.deleteTile(pyramid.getId(), mosaic.getId(), 0, 2);
        assertNull(mosaic.getTile(0, 2, null).getInput());
        
        //test update tile
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 3, createImage(dimension, Color.PINK));        
        image = mosaic.getTile(0, 3, null).getImageReader().read(3);
        assertImageColor(image, Color.PINK);
        
        //test delete mosaic
        cref.deleteMosaic(pyramid.getId(), mosaic.getId());
        pyramid = cref.getPyramidSet().getPyramid(pyramid.getId());
        assertTrue(pyramid.getMosaics().isEmpty());
        
        //test delete pyramid
        cref.deletePyramid(pyramid.getId());
        assertTrue(cref.getPyramidSet().getPyramids().isEmpty());
        
    }

    @Test
    public void testSampleDimensions() throws DataStoreException, VersioningException, IOException, TransformException {
        reload();

        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(CRS_4326);
        final Dimension dimension = new Dimension(20, 20);
        upperLeft.setOrdinate(0, -90);
        upperLeft.setOrdinate(1, +180);
        PyramidalCoverageReference cref;
        Pyramid pyramid;
        GridMosaic mosaic;
        BufferedImage image;

        final Name name = new DefaultName(null, "sampleTestLayer");
        store.create(name);

        //create version 1 -----------------------------------------------------
        cref = (PyramidalCoverageReference) store.getCoverageReference(name);
        assertNotNull(cref);

        //test create pyramid
        pyramid = cref.createPyramid(CRS_4326);
        assertEquals(1,cref.getPyramidSet().getPyramids().size());

        final List<GridSampleDimension> dimensions = new LinkedList<>();
        // dim 1
        final Category dataCat = new Category("data", new Color[]{Color.WHITE, Color.BLACK},
                NumberRange.create(1, true, 100, true), NumberRange.create(-50.0, true, 45.6, true));
        final Category nodataCat = new Category(
                Vocabulary.formatInternational(Vocabulary.Keys.NODATA), new Color(0,0,0,0), Double.NaN);
        final GridSampleDimension dim1 = new GridSampleDimension("dim0",new Category[]{dataCat,nodataCat}, SI.CELSIUS);
        dimensions.add(0, dim1);

        // dim 2
        final Category dataCat2 = new Category("data", new Color[]{Color.WHITE, Color.BLACK}, 1, 55, 2.0, 0.0);
        final Category nodataCat2 = Category.NODATA;
        final GridSampleDimension dim2 = new GridSampleDimension("dim1",new Category[]{dataCat2,nodataCat2}, SI.METRE);
        dimensions.add(1, dim2);

        //test create SampleDimensions
        cref.setSampleDimensions(dimensions);

        List<GridSampleDimension> resultSamples = cref.getSampleDimensions();
        assertNotNull(resultSamples);
        assertEquals(2, resultSamples.size());

        GridSampleDimension resultDim1 = resultSamples.get(0);
        GridSampleDimension resultDim2 = resultSamples.get(1);
        assertNotNull(resultDim1);
        assertNotNull(resultDim2);

        assertEquals("dim0", resultDim1.getDescription().toString());
        assertEquals("dim1", resultDim2.getDescription().toString());
        assertEquals(SI.CELSIUS, resultDim1.getUnits());
        assertEquals(SI.METRE, resultDim2.getUnits());


        List<Category> resultCat1 = resultDim1.getCategories();
        List<Category> resultCat2 = resultDim2.getCategories();
        assertEquals(2, resultCat1.size());
        assertEquals(2, resultCat2.size());
        assertCategoryEquals(dataCat, resultCat1);
        assertCategoryEquals(nodataCat, resultCat1);
        assertCategoryEquals(dataCat2, resultCat2);
        assertCategoryEquals(nodataCat2, resultCat2);
    }

    private void assertCategoryEquals(Category expected, List<Category> result) {
        boolean found = false;
        for (Category resultCategory : result) {
            if (resultCategory.getName().toString().equals(expected.getName().toString())) {
                assertArrayEquals(expected.getColors(), resultCategory.getColors());
                assertEquals(expected.getSampleToGeophysics(), resultCategory.getSampleToGeophysics());

                NumberRange<?> expectedRange = expected.getRange();
                NumberRange<?> resultRange = resultCategory.getRange();

                assertEquals(expectedRange.getMinDouble(), resultRange.getMinDouble(), 0.00000001);
                assertEquals(expectedRange.getMaxDouble(), resultRange.getMaxDouble(), 0.00000001);
                found = true;
            }
        }

        if (!found) {
            fail("Category "+expected.getName().toString()+" not found.");
        }
    }

    private static BufferedImage createImage(Dimension tileSize, Color color){
        final BufferedImage image = new BufferedImage(tileSize.width, tileSize.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, tileSize.width, tileSize.height);
        return image;
    }
    
    private static void assertImageColor(RenderedImage image, Color color){
        final BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.createGraphics().drawRenderedImage(image, new AffineTransform());
        image = img;
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int refargb = color.getRGB();;
        
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int argb = ((BufferedImage)image).getRGB(x, y);
                assertEquals(refargb, argb);
            }
        }
    }
    
}
