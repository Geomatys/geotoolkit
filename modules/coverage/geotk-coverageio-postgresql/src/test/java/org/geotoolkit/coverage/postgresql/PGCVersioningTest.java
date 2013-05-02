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
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.coverage.postgresql.PGCoverageStoreFactory.*;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;

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
            CRS_4326 = CRS.decode("EPSG:4326",true);
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException("Failed to load CRS");
        } catch (FactoryException ex) {
            throw new RuntimeException("Failed to load CRS");
        } 
    }
    
    private CoverageStore store;
    
    public PGCVersioningTest(){
    }
    
    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.dispose();
        }
        
        final ParameterValueGroup params = PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(DATABASE, params).setValue("junit");
        Parameters.getOrCreate(PORT, params).setValue(5432);
        Parameters.getOrCreate(SCHEMA, params).setValue("pgcoverage");
        Parameters.getOrCreate(USER, params).setValue("postgres");
        Parameters.getOrCreate(PASSWORD, params).setValue("postgres");
        Parameters.getOrCreate(NAMESPACE, params).setValue("no namespace");
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
        upperLeft.setOrdinate(0, +90);
        upperLeft.setOrdinate(1, -180);
        List<Version> versions;
        Version version;
        PyramidalModel cref;
        Pyramid pyramid;
        GridMosaic mosaic;
        
        final Name name = new DefaultName(null, "versLayer");        
        store.create(name);
        final VersionControl vc = store.getVersioning(name);
        versions = vc.list();
        assertTrue(versions.isEmpty());
        
        //create version 1 -----------------------------------------------------
        calendar.setTimeInMillis(0);
        final Date date1 = calendar.getTime();        
        version = vc.createVersion(date1);
        cref = (PyramidalModel) store.getCoverageReference(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = cref.createPyramid(CRS_4326);
        mosaic = cref.createMosaic(pyramid.getId(), dimension, dimension, upperLeft, 1);
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(dimension, Color.RED));
        
        versions = vc.list();
        assertEquals(1, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(date1.getTime(),versions.get(0).getDate().getTime());
        
        //create version 2 -----------------------------------------------------
        calendar.setTimeInMillis(50000);
        final Date date2 = calendar.getTime();
        version = vc.createVersion(date2);
        cref = (PyramidalModel) store.getCoverageReference(name, version);
        assertNotNull(cref);
        //we need to create a pyramid otherwise the version not really be created
        pyramid = cref.createPyramid(CRS_4326);
        mosaic = cref.createMosaic(pyramid.getId(), dimension, dimension, upperLeft, 1);
        cref.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, createImage(dimension, Color.BLUE));
        
        versions = vc.list();
        assertEquals(2, versions.size());
        assertEquals(versions.get(0).getDate().getTime(),0);
        assertEquals(versions.get(1).getDate().getTime(),50000);
        assertTrue(versions.get(0).getDate().compareTo(versions.get(1).getDate()) < 0);
        
        
        
        
        
        
    }
        
    
    private static BufferedImage createImage(Dimension tileSize, Color color){
        final BufferedImage image = new BufferedImage(tileSize.width, tileSize.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, tileSize.width, tileSize.height);
        return image;
    }
    
    private static void assertImageColor(BufferedImage image, Color color){
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int refargb = color.getRGB();;
        
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int argb = image.getRGB(x, y);
                assertEquals(refargb, argb);
            }
        }
    }
    
}
