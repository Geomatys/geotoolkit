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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.coverage.postgresql.PGCoverageStoreFactory.*;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGCVersioningTest {
    
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
    
    private static ParameterValueGroup params;
    private CoverageStore store;
    
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
    
    public PGCVersioningTest(){
    }
    
    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.dispose();
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
    public void testVersioning() throws DataStoreException, VersioningException {
        reload();
        
        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(CRS_4326);
        final Dimension dimension = new Dimension(20, 20);
        final Calendar calendar = Calendar.getInstance(GMT0);
        upperLeft.setOrdinate(0, -90);
        upperLeft.setOrdinate(1, +180);
        List<Version> versions;
        Version version;
        PyramidalCoverageReference cref;
        Pyramid pyramid;
        GridMosaic mosaic;
        GridCoverage2D coverage;
        
        final Name name = new DefaultName(null, "versLayer");        
        store.create(name);
        final VersionControl vc = store.getVersioning(name);
        versions = vc.list();
        assertTrue(versions.isEmpty());
        
        //create version 1 -----------------------------------------------------
        calendar.setTimeInMillis(0);
        final Date date1 = calendar.getTime();        
        version = vc.createVersion(date1);
        cref = (PyramidalCoverageReference) store.getCoverageReference(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = cref.createPyramid(CRS_4326);
        mosaic = cref.createMosaic(pyramid.getId(), new Dimension(1, 1), dimension, upperLeft, 1);
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(dimension, Color.RED));
        
        versions = vc.list();
        assertEquals(1, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(date1.getTime(),versions.get(0).getDate().getTime());
        
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.RED);
        
        //create version 2 -----------------------------------------------------
        calendar.setTimeInMillis(50000);
        final Date date2 = calendar.getTime();
        version = vc.createVersion(date2);
        cref = (PyramidalCoverageReference) store.getCoverageReference(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = cref.createPyramid(CRS_4326);
        mosaic = cref.createMosaic(pyramid.getId(), new Dimension(1, 1), dimension, upperLeft, 1);
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(dimension, Color.BLUE));
        
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.BLUE);
        
        versions = vc.list();
        assertEquals(2, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),50000);
        
        //create version 3 -----------------------------------------------------
        calendar.setTimeInMillis(20000);
        final Date date3 = calendar.getTime();
        version = vc.createVersion(date3);
        cref = (PyramidalCoverageReference) store.getCoverageReference(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = cref.createPyramid(CRS_4326);
        mosaic = cref.createMosaic(pyramid.getId(), new Dimension(1, 1), dimension, upperLeft, 1);
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(dimension, Color.GREEN));
        
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.GREEN);
        
        versions = vc.list();
        assertEquals(3, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),20000);
        assertEquals(versions.get(2).getDate().getTime(),50000);
                
        
        //try accesing different version ---------------------------------------
        cref = (PyramidalCoverageReference) store.getCoverageReference(name);
        //we should have the blue image
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.BLUE);
        
        //grab by version
        cref = (PyramidalCoverageReference) store.getCoverageReference(name,versions.get(0));
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.RED);
        cref = (PyramidalCoverageReference) store.getCoverageReference(name,versions.get(1));
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.GREEN);
        cref = (PyramidalCoverageReference) store.getCoverageReference(name,versions.get(2));
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.BLUE);
        
        
        //drop some versions ---------------------------------------------------
        vc.dropVersion(versions.get(1));
        versions = vc.list();
        assertEquals(2, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),50000);
        
        cref = (PyramidalCoverageReference) store.getCoverageReference(name,versions.get(0));
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.RED);
        cref = (PyramidalCoverageReference) store.getCoverageReference(name,versions.get(1));
        coverage = (GridCoverage2D)cref.createReader().read(cref.getImageIndex(), null);
        assertImageColor(coverage.getRenderedImage(), Color.BLUE);
        
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
