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
import org.opengis.filter.Filter;
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
        Point point;
        Feature feature;
        FeatureId fid;
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
        
        //start versioning------------------------------------------------------
        vc.startVersioning();
        assertTrue(vc.isVersioned());        
        versions = vc.list();
        assertTrue(versions.isEmpty());
        
        //create an insert------------------------------------------------------
        point = GF.createPoint(new Coordinate(56, 45));
        feature = FeatureUtilities.defaultFeature(refType, "0");
        feature.getProperty("boolean").setValue(Boolean.TRUE);
        feature.getProperty("integer").setValue(14);
        feature.getProperty("point").setValue(point);
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
        feature = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator().next();
        assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
        assertEquals(14,                feature.getProperty("integer").getValue());
        assertEquals(point,             feature.getProperty("point").getValue());
        assertEquals("someteststring",  feature.getProperty("string").getValue());
        
        fid = feature.getIdentifier();
        
        //ensure normal reading is correct with version-------------------------
        qb.reset();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(version.getLabel());
        feature = store.createSession(true).getFeatureCollection(qb.buildQuery()).iterator().next();
        assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
        assertEquals(14,                feature.getProperty("integer").getValue());
        assertEquals(point,             feature.getProperty("point").getValue());
        assertEquals("someteststring",  feature.getProperty("string").getValue());
        
        //make an update--------------------------------------------------------
        point = GF.createPoint(new Coordinate(-12, 21));
        final Map<PropertyDescriptor,Object> updates = new HashMap<PropertyDescriptor, Object>();
        updates.put(feature.getProperty("boolean").getDescriptor(), Boolean.FALSE);
        updates.put(feature.getProperty("integer").getDescriptor(), -3);
        updates.put(feature.getProperty("point").getDescriptor(), point);
        updates.put(feature.getProperty("string").getDescriptor(), "anothertextupdated");
        
        store.updateFeatures(refType.getName(), Filter.INCLUDE, updates);
        
        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());        
        Version v1 = versions.get(0);
        Version v2 = versions.get(1);
        //should be ordered starting from the oldest
        assertTrue(v1.getDate().compareTo(v2.getDate()) < 0);
        
    }
    
}
