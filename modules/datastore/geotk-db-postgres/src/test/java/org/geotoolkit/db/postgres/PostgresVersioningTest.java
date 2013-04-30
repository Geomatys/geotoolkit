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
package org.geotoolkit.db.postgres;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.*;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresVersioningTest {
    
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static final GeometryFactory GF = new GeometryFactory();
    private static final FeatureType FTYPE_SIMPLE;
    private static final CoordinateReferenceSystem CRS_4326;
    
    static{
        try {
            CRS_4326 = CRS.decode("EPSG:4326",true);
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException("Failed to load CRS");
        } catch (FactoryException ex) {
            throw new RuntimeException("Failed to load CRS");
        }
        
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.add("boolean",  Boolean.class);
        ftb.add("integer",  Integer.class);
        ftb.add("point",    Point.class, CRS_4326);
        ftb.add("string",   String.class);
        FTYPE_SIMPLE = ftb.buildFeatureType();
        
    }
    
    private PostgresFeatureStore store;
    
    public PostgresVersioningTest(){
    }
    
    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.dispose();
        }
        
        final ParameterValueGroup params = PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(DATABASE, params).setValue("junit");
        Parameters.getOrCreate(PORT, params).setValue(5432);
        Parameters.getOrCreate(SCHEMA, params).setValue("public");
        Parameters.getOrCreate(USER, params).setValue("postgres");
        Parameters.getOrCreate(PASSWORD, params).setValue("postgres");
        Parameters.getOrCreate(SIMPLETYPE, params).setValue(false);
        Parameters.getOrCreate(NAMESPACE, params).setValue("no namespace");
        store = (PostgresFeatureStore) FeatureStoreFinder.open(params);
        
        for(Name n : store.getNames()){
            store.deleteSchema(n);
        }
        assertTrue(store.getNames().isEmpty());
        
        //delete historisation functions, he must create them himself
        store.dropHSFunctions();
        
    }
    
    @Test
    public void testSimpleTypeVersioning() throws DataStoreException, VersioningException {
        reload();
        List<Version> versions;
        Version version;
        Feature feature;
        FeatureId fid;
        Version v1;
        Version v2;
        Version v3;
        FeatureIterator ite;
        final QueryBuilder qb = new QueryBuilder();
        
        final FeatureType refType = FTYPE_SIMPLE;        
        store.createSchema(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        assertNotNull(store.getQueryCapabilities());
        assertTrue(store.getQueryCapabilities().handleVersioning());
        
        final VersionControl vc = store.getVersioning(refType.getName());
        assertNotNull(vc);
        assertTrue(vc.isEditable());
        assertFalse(vc.isVersioned());
        
        ////////////////////////////////////////////////////////////////////////
        //start versioning /////////////////////////////////////////////////////
        vc.startVersioning();
        assertTrue(vc.isVersioned());        
        versions = vc.list();
        assertTrue(versions.isEmpty());
        
        //create an insert------------------------------------------------------
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = FeatureUtilities.defaultFeature(refType, "0");
        feature.getProperty("boolean").setValue(Boolean.TRUE);
        feature.getProperty("integer").setValue(14);
        feature.getProperty("point").setValue(firstPoint);
        feature.getProperty("string").setValue("someteststring");        
        store.addFeatures(refType.getName(), Collections.singleton(feature));
                
        //we should have one version
        versions = vc.list();
        assertEquals(1, versions.size());        
        version = versions.get(0);
        Date date = version.getDate();
        
        //ensure normal reading is correct without version----------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(14,                feature.getProperty("integer").getValue());
            assertEquals(firstPoint,        feature.getProperty("point").getValue());
            assertEquals("someteststring",  feature.getProperty("string").getValue());        
            fid = feature.getIdentifier();
        }finally{
            ite.close();
        }
        
        //ensure normal reading is correct with version-------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(version.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(14,                feature.getProperty("integer").getValue());
            assertEquals(firstPoint,        feature.getProperty("point").getValue());
            assertEquals("someteststring",  feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
        
        
        ////////////////////////////////////////////////////////////////////////
        //make an update ///////////////////////////////////////////////////////
        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        final Map<PropertyDescriptor,Object> updates = new HashMap<PropertyDescriptor, Object>();
        updates.put(feature.getProperty("boolean").getDescriptor(), Boolean.FALSE);
        updates.put(feature.getProperty("integer").getDescriptor(), -3);
        updates.put(feature.getProperty("point").getDescriptor(), secondPoint);
        updates.put(feature.getProperty("string").getDescriptor(), "anothertextupdated");
        
        store.updateFeatures(refType.getName(), FF.id(Collections.singleton(fid)), updates);
        
        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());        
        v1 = versions.get(0);
        v2 = versions.get(1);
        //should be ordered starting from the oldest
        assertTrue(v1.getDate().compareTo(v2.getDate()) < 0);
        
        //ensure normal reading is correct without version----------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,      feature.getProperty("boolean").getValue());
            assertEquals(-3,                feature.getProperty("integer").getValue());
            assertEquals(secondPoint,             feature.getProperty("point").getValue());
            assertEquals("anothertextupdated",feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        //ensure normal reading is correct with version-------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v2.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,      feature.getProperty("boolean").getValue());
            assertEquals(-3,                feature.getProperty("integer").getValue());
            assertEquals(secondPoint,             feature.getProperty("point").getValue());
            assertEquals("anothertextupdated",feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        //ensure reading a previous version works ------------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(14,                feature.getProperty("integer").getValue());
            assertEquals(firstPoint,        feature.getProperty("point").getValue());
            assertEquals("someteststring",  feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        //ensure reading a previous version using not exact date----------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionDate(new Date(v1.getDate().getTime()+400));
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(14,                feature.getProperty("integer").getValue());
            assertEquals(firstPoint,        feature.getProperty("point").getValue());
            assertEquals("someteststring",  feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        ////////////////////////////////////////////////////////////////////////
        //delete record ////////////////////////////////////////////////////////
        
        store.removeFeatures(refType.getName(), FF.id(Collections.singleton(fid)));
        qb.reset();
        qb.setTypeName(refType.getName());
        assertEquals(0, store.getCount(qb.buildQuery()));
        
        //we should have three versions
        versions = vc.list();
        assertEquals(3, versions.size());        
        v1 = versions.get(0);
        v2 = versions.get(1);
        v3 = versions.get(2);
        //should be ordered starting from the oldest
        assertTrue(v2.getDate().compareTo(v1.getDate()) > 0);
        assertTrue(v3.getDate().compareTo(v2.getDate()) > 0);
        
        //ensure we have nothing if no version set -----------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        assertTrue(store.createSession(true).getFeatureCollection(qb.buildQuery()).isEmpty());
                
        //ensure we have nothing if latest version set -------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v3.getLabel());
        assertTrue(store.createSession(true).getFeatureCollection(qb.buildQuery()).isEmpty());
        
        //ensure we have nothing with date after deletion ----------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionDate(new Date(v3.getDate().getTime()+400));
        assertTrue(store.createSession(true).getFeatureCollection(qb.buildQuery()).isEmpty());
        
        //ensure reading version 1 works ---------------------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(14,                feature.getProperty("integer").getValue());
            assertEquals(firstPoint,        feature.getProperty("point").getValue());
            assertEquals("someteststring",  feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        //ensure reading version 2 works ---------------------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v2.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,      feature.getProperty("boolean").getValue());
            assertEquals(-3,                 feature.getProperty("integer").getValue());
            assertEquals(secondPoint,        feature.getProperty("point").getValue());
            assertEquals("anothertextupdated",feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
    }
    
}
