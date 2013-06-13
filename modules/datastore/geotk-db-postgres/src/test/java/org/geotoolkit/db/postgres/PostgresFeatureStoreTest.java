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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.PropertyType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.feature.Feature;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.*;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresFeatureStoreTest {
    
    private static final double DELTA = 0.00000001;    
    /** basic field types */
    private static final FeatureType FTYPE_SIMPLE;
    /** 1 dimension arrays */
    private static final FeatureType FTYPE_ARRAY;    
    /** 2 dimensions arrays */
    private static final FeatureType FTYPE_ARRAY2;
    /** geometric fields */
    private static final FeatureType FTYPE_GEOMETRY;
    /** 1 depth feature type */
    private static final FeatureType FTYPE_COMPLEX;
    /** 2 depth feature type */
    private static final FeatureType FTYPE_COMPLEX2;
    /** multiple properties of same complex type */
    private static final FeatureType FTYPE_COMPLEX3;
    
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
        AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.add("boolean",  Boolean.class);
        ftb.add("byte",     Byte.class);
        ftb.add("short",    Short.class);
        ftb.add("integer",  Integer.class);
        ftb.add("long",     Long.class);
        ftb.add("float",    Float.class);
        ftb.add("double",   Double.class);
        ftb.add("string",   String.class);
        FTYPE_SIMPLE = ftb.buildFeatureType();
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.add("boolean",  Boolean[].class);
        ftb.add("byte",     Byte[].class);
        ftb.add("short",    Short[].class);
        ftb.add("integer",  Integer[].class);
        ftb.add("long",     Long[].class);
        ftb.add("float",    Float[].class);
        ftb.add("double",   Double[].class);
        ftb.add("string",   String[].class);
        FTYPE_ARRAY = ftb.buildFeatureType();
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.add("boolean",  Boolean[][].class);
        ftb.add("byte",     Byte[][].class);
        ftb.add("short",    Short[][].class);
        ftb.add("integer",  Integer[][].class);
        ftb.add("long",     Long[][].class);
        ftb.add("float",    Float[][].class);
        ftb.add("double",   Double[][].class);
        ftb.add("string",   String[][].class);
        FTYPE_ARRAY2 = ftb.buildFeatureType();
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.add("geometry",         Geometry.class, CRS_4326);
        ftb.add("point",            Point.class, CRS_4326);
        ftb.add("multipoint",       MultiPoint.class, CRS_4326);
        ftb.add("linestring",       LineString.class, CRS_4326);
        ftb.add("multilinestring",  MultiLineString.class, CRS_4326);
        ftb.add("polygon",          Polygon.class, CRS_4326);
        ftb.add("multipolygon",     MultiPolygon.class, CRS_4326);
        ftb.add("geometrycollection",GeometryCollection.class, CRS_4326);
        FTYPE_GEOMETRY = ftb.buildFeatureType();
        
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();

        ftb.setName("Stop");
        ftb.add("location", Point.class, CRS_4326);
        ftb.add("time", Date.class);
        final ComplexType stopType = ftb.buildType();

        ftb.reset();
        ftb.setName("Driver");
        ftb.add("name", String.class);
        ftb.add("code", String.class);
        final ComplexType driverType = ftb.buildType();

        ftb.reset();
        ftb.setName("Voyage");
        ftb.add("identifier", Long.class);
        AttributeDescriptor elementDesc = adb.create(driverType, DefaultName.valueOf("driver"),1,1,false,null);
        AttributeDescriptor stepDesc = adb.create(stopType, DefaultName.valueOf("stops"),0,Integer.MAX_VALUE,false,null);
        ftb.add(elementDesc);
        ftb.add(stepDesc);
        FTYPE_COMPLEX = ftb.buildFeatureType();
        
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        adb = new AttributeDescriptorBuilder();

        ftb.reset();
        ftb.setName("Data");
        ftb.add("values", Float[].class);
        final ComplexType dataType = ftb.buildType();
        
        ftb.reset();
        ftb.setName("Record");
        ftb.add("time", Date.class);
        AttributeDescriptor dataDesc = adb.create(dataType, DefaultName.valueOf("datas"),0,Integer.MAX_VALUE,false,null);
        ftb.add(dataDesc);
        final ComplexType recordType = ftb.buildType();

        ftb.reset();
        ftb.setName("Sounding");
        ftb.add("identifier", Long.class);
        AttributeDescriptor recordDesc = adb.create(recordType, DefaultName.valueOf("records"),0,Integer.MAX_VALUE,false,null);
        ftb.add(recordDesc);
        FTYPE_COMPLEX2 = ftb.buildFeatureType();
        
        
        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        adb = new AttributeDescriptorBuilder();

        ftb.reset();
        ftb.setName("Data");
        ftb.add("value", Float.class);
        final ComplexType sdataType = ftb.buildType();
        
        ftb.reset();
        ftb.setName("Record");
        ftb.add("identifier", Long.class);
        AttributeDescriptor sdata1Desc = adb.create(sdataType, DefaultName.valueOf("data1"),0,1,false,null);
        AttributeDescriptor sdata2Desc = adb.create(sdataType, DefaultName.valueOf("data2"),0,1,false,null);
        AttributeDescriptor sdata3Desc = adb.create(sdataType, DefaultName.valueOf("data3"),0,1,false,null);
        ftb.add(sdata1Desc);
        ftb.add(sdata2Desc);
        ftb.add(sdata3Desc);
        FTYPE_COMPLEX3 = ftb.buildFeatureType();
        
        
    }
    
    private FeatureStore store;
    
    public PostgresFeatureStoreTest(){
    }
    
    private static ParameterValueGroup params;
    
    /**
     * <p>Find JDBC connection parameters in specified file at 
     * "/home/.geotoolkit.org/test-pgfeature.properties".<br/>
     * If properties file doesn't find all tests are skipped.</p>
     * 
     * <p>To lunch tests user should create file with this architecture<br/>
     * for example : <br/>
     * database   = junit    (table name)<br/>
     * port       = 5432     (port number)<br/>
     * schema     = public   (schema name)<br/>
     * user       = postgres (user login)<br/>
     * password   = postgres (user password)<br/>
     * simpletype = false <br/>
     * namespace  = no namespace</p>
     * @throws IOException 
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = System.getProperty("user.home");
        path += "/.geotoolkit.org/test-pgfeature.properties";        
        final File f = new File(path);
        Assume.assumeTrue(f.exists());        
        final Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        params = FeatureUtilities.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false);
    }
    
    private void reload(boolean simpleType) throws DataStoreException, VersioningException {
        if(store != null){
            store.dispose();
        }
        
        //open in complex type to delete all types
        ParametersExt.getOrCreateValue(params, PostgresFeatureStoreFactory.SIMPLETYPE.getName().getCode()).setValue(false);
        store = FeatureStoreFinder.open(params);        
        for(Name n : store.getNames()){
            VersionControl vc = store.getVersioning(n);
            vc.dropVersioning();
            store.deleteFeatureType(n);
        }
        assertTrue(store.getNames().isEmpty());
        store.dispose();
        
        //reopen the way it was asked
        ParametersExt.getOrCreateValue(params, PostgresFeatureStoreFactory.SIMPLETYPE.getName().getCode()).setValue(simpleType);
        store = FeatureStoreFinder.open(params);
        assertTrue(store.getNames().isEmpty());
    }
    
    @Test
    public void testSimpleTypeCreation() throws DataStoreException, VersioningException {
        reload(true);
        
        final FeatureType refType = FTYPE_SIMPLE;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        assertEquals(resType.getName().getLocalPart(), refType.getName().getLocalPart());
        //we expect one more field for id
        final List<PropertyDescriptor> descs = new ArrayList<PropertyDescriptor>(resType.getDescriptors());
        
        int index=1;
        PropertyDescriptor desc;
        desc = descs.get(index++);
        assertEquals("boolean", desc.getName().getLocalPart()); 
        assertEquals(Boolean.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("byte", desc.getName().getLocalPart()); 
        assertEquals(Short.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("short", desc.getName().getLocalPart()); 
        assertEquals(Short.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("integer", desc.getName().getLocalPart()); 
        assertEquals(Integer.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("long", desc.getName().getLocalPart()); 
        assertEquals(Long.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("float", desc.getName().getLocalPart()); 
        assertEquals(Float.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("double", desc.getName().getLocalPart()); 
        assertEquals(Double.class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("string", desc.getName().getLocalPart()); 
        assertEquals(String.class, desc.getType().getBinding());
        
    }
    
    @Test
    public void testArrayTypeCreation() throws DataStoreException, VersioningException {
        reload(true);
        
        final FeatureType refType = FTYPE_ARRAY;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        assertEquals(resType.getName().getLocalPart(), refType.getName().getLocalPart());
        //we expect one more field for id
        final List<PropertyDescriptor> descs = new ArrayList<PropertyDescriptor>(resType.getDescriptors());
        
        //Postgis allow NULL in arrays, so returned array are not primitive types
        int index=1;
        PropertyDescriptor desc;
        desc = descs.get(index++);
        assertEquals("boolean", desc.getName().getLocalPart()); 
        assertEquals(Boolean[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("byte", desc.getName().getLocalPart()); 
        assertEquals(Short[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("short", desc.getName().getLocalPart()); 
        assertEquals(Short[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("integer", desc.getName().getLocalPart()); 
        assertEquals(Integer[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("long", desc.getName().getLocalPart()); 
        assertEquals(Long[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("float", desc.getName().getLocalPart()); 
        assertEquals(Float[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("double", desc.getName().getLocalPart()); 
        assertEquals(Double[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("string", desc.getName().getLocalPart()); 
        assertEquals(String[].class, desc.getType().getBinding());
        
    }
    
    @Test
    public void testArray2TypeCreation() throws DataStoreException, VersioningException {
        reload(true);
        
        final FeatureType refType = FTYPE_ARRAY2;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        assertEquals(resType.getName().getLocalPart(), refType.getName().getLocalPart());
        //we expect one more field for id
        final List<PropertyDescriptor> descs = new ArrayList<PropertyDescriptor>(resType.getDescriptors());
        
        //Postgis allow NULL in arrays, so returned array are not primitive types
        int index=1;
        PropertyDescriptor desc;
        desc = descs.get(index++);
        assertEquals("boolean", desc.getName().getLocalPart()); 
        assertEquals(Boolean[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("byte", desc.getName().getLocalPart()); 
        assertEquals(Short[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("short", desc.getName().getLocalPart()); 
        assertEquals(Short[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("integer", desc.getName().getLocalPart()); 
        assertEquals(Integer[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("long", desc.getName().getLocalPart()); 
        assertEquals(Long[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("float", desc.getName().getLocalPart()); 
        assertEquals(Float[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("double", desc.getName().getLocalPart()); 
        assertEquals(Double[].class, desc.getType().getBinding());
        desc = descs.get(index++);
        assertEquals("string", desc.getName().getLocalPart()); 
        assertEquals(String[].class, desc.getType().getBinding());
        
    }
    
    @Test
    public void testGeometryTypeCreation() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, VersioningException {
        reload(true);
                
        final FeatureType refType = FTYPE_GEOMETRY;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        assertEquals(resType.getName().getLocalPart(), refType.getName().getLocalPart());
        //we expect one more field for id
        final List<PropertyDescriptor> descs = new ArrayList<PropertyDescriptor>(resType.getDescriptors());
        
        int index=1;
        PropertyDescriptor desc;
        desc = descs.get(index++);
        assertEquals("geometry", desc.getName().getLocalPart()); 
        assertEquals(Geometry.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("point", desc.getName().getLocalPart()); 
        assertEquals(Point.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("multipoint", desc.getName().getLocalPart()); 
        assertEquals(MultiPoint.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("linestring", desc.getName().getLocalPart()); 
        assertEquals(LineString.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("multilinestring", desc.getName().getLocalPart()); 
        assertEquals(MultiLineString.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("polygon", desc.getName().getLocalPart()); 
        assertEquals(Polygon.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("multipolygon", desc.getName().getLocalPart()); 
        assertEquals(MultiPolygon.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
        desc = descs.get(index++);
        assertEquals("geometrycollection", desc.getName().getLocalPart()); 
        assertEquals(GeometryCollection.class, desc.getType().getBinding());
        assertTrue(desc instanceof GeometryDescriptor);
        assertEquals(CRS_4326, ((GeometryDescriptor)desc).getCoordinateReferenceSystem());
    }
    
    @Test
    public void testComplexTypeCreation() throws DataStoreException, VersioningException{
        reload(false);
        
        final FeatureType refType = FTYPE_COMPLEX;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
         
        final Name name = store.getNames().iterator().next();
        final FeatureType created = store.getFeatureType(name);
        lazyCompare(refType, created);
        
    }
    
    @Test
    public void testComplexType2Creation() throws DataStoreException, VersioningException{
        reload(false);
        
        final FeatureType refType = FTYPE_COMPLEX2;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        final Name name = store.getNames().iterator().next();
        final FeatureType created = store.getFeatureType(name);
        lazyCompare(refType, created);
        
    }
    
    @Test
    public void testComplexType3Creation() throws DataStoreException, VersioningException{
        reload(false);
        
        final FeatureType refType = FTYPE_COMPLEX3;        
        store.createFeatureType(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        final Name name = store.getNames().iterator().next();
        final FeatureType created = store.getFeatureType(name);
        lazyCompare(refType, created);
    }
    
    private void lazyCompare(final PropertyType refType, final PropertyType candidate){
        final Name name = refType.getName();
        assertEquals(refType.getName().getLocalPart(), name.getLocalPart());
        
        if(refType instanceof ComplexType){
            final ComplexType ct = (ComplexType) refType;
            final ComplexType cct = (ComplexType) candidate;
            assertEquals(ct.getDescriptors().size()+1, cct.getDescriptors().size());// +1 for generated fid field

            for(PropertyDescriptor desc : ct.getDescriptors()){
                final PropertyDescriptor cdesc = cct.getDescriptor(desc.getName().getLocalPart());
                assertEquals(desc.getMaxOccurs(), cdesc.getMaxOccurs());
                assertNotNull(cdesc);
                lazyCompare(desc.getType(), cdesc.getType());
            }
            
        }else{
            final AttributeType at = (AttributeType) refType;
            final AttributeType cat = (AttributeType) candidate;
            if(at.getBinding()==Date.class){
                assertEquals(Timestamp.class, cat.getBinding());
            }else{
                assertEquals(at.getBinding(), cat.getBinding());
            }
        }
    }
    
    @Test
    public void testSimpleInsert() throws DataStoreException, VersioningException{
        reload(true);
            
        store.createFeatureType(FTYPE_SIMPLE.getName(), FTYPE_SIMPLE);
        FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        Feature feature = FeatureUtilities.defaultFeature(resType, "0");
        feature.getProperty("boolean").setValue(true);
        feature.getProperty("byte").setValue(45);
        feature.getProperty("short").setValue(963);
        feature.getProperty("integer").setValue(123456);
        feature.getProperty("long").setValue(456789l);
        feature.getProperty("float").setValue(7.3f);
        feature.getProperty("double").setValue(14.5);
        feature.getProperty("string").setValue("a string");
        
        store.addFeatures(resType.getName(), Collections.singleton(feature));
        
        Session session = store.createSession(false);
        FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(resFeature instanceof SimpleFeature);
            assertEquals(true, resFeature.getProperty("boolean").getValue());
            assertEquals(45, resFeature.getProperty("byte").getValue());
            assertEquals(963, resFeature.getProperty("short").getValue());
            assertEquals(123456, resFeature.getProperty("integer").getValue());
            assertEquals(456789l, resFeature.getProperty("long").getValue());
            assertEquals(7.3f, resFeature.getProperty("float").getValue());
            assertEquals(14.5d, resFeature.getProperty("double").getValue());
            assertEquals("a string", resFeature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        
        // SECOND TEST for NAN values ------------------------------------------
        reload(true);            
        store.createFeatureType(FTYPE_SIMPLE.getName(), FTYPE_SIMPLE);
        resType = store.getFeatureType(store.getNames().iterator().next());
        
        feature = FeatureUtilities.defaultFeature(resType, "0");
        feature.getProperty("boolean").setValue(true);
        feature.getProperty("byte").setValue(45);
        feature.getProperty("short").setValue(963);
        feature.getProperty("integer").setValue(123456);
        feature.getProperty("long").setValue(456789l);
        feature.getProperty("float").setValue(Float.NaN);
        feature.getProperty("double").setValue(Double.NaN);
        feature.getProperty("string").setValue("a string");
        
        store.addFeatures(resType.getName(), Collections.singleton(feature));
        
        session = store.createSession(false);
        col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(resFeature instanceof SimpleFeature);
            assertEquals(true, resFeature.getProperty("boolean").getValue());
            assertEquals(45, resFeature.getProperty("byte").getValue());
            assertEquals(963, resFeature.getProperty("short").getValue());
            assertEquals(123456, resFeature.getProperty("integer").getValue());
            assertEquals(456789l, resFeature.getProperty("long").getValue());
            assertEquals(Float.NaN, resFeature.getProperty("float").getValue());
            assertEquals(Double.NaN, resFeature.getProperty("double").getValue());
            assertEquals("a string", resFeature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
        
        
    }
        
    @Test
    public void testArrayInsert() throws DataStoreException, VersioningException{
        reload(true);
            
        store.createFeatureType(FTYPE_ARRAY.getName(), FTYPE_ARRAY);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        final Feature feature = FeatureUtilities.defaultFeature(resType, "0");
        feature.getProperty("boolean").setValue(new boolean[]{true,false,true});
        feature.getProperty("byte").setValue(new short[]{3,6,9});
        feature.getProperty("short").setValue(new short[]{-5,12,-50});
        feature.getProperty("integer").setValue(new int[]{123,456,789});
        feature.getProperty("long").setValue(new long[]{111l,222l,333l});
        feature.getProperty("float").setValue(new float[]{1.2f,-5.9f,8.1f});
        feature.getProperty("double").setValue(new double[]{78.3d,41.23d,-99.66d});
        feature.getProperty("string").setValue(new String[]{"marc","hubert","samy"});
        
        store.addFeatures(resType.getName(), Collections.singleton(feature));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(resFeature instanceof SimpleFeature);
            assertArrayEquals(new Boolean[]{true,false,true},       (Boolean[])resFeature.getProperty("boolean").getValue());
            assertArrayEquals(new Short[]{3,6,9},                   (Short[])resFeature.getProperty("byte").getValue());
            assertArrayEquals(new Short[]{-5,12,-50},               (Short[])resFeature.getProperty("short").getValue());
            assertArrayEquals(new Integer[]{123,456,789},           (Integer[])resFeature.getProperty("integer").getValue());
            assertArrayEquals(new Long[]{111l,222l,333l},           (Long[])resFeature.getProperty("long").getValue());
            assertArrayEquals(new Float[]{1.2f,-5.9f,8.1f},         (Float[])resFeature.getProperty("float").getValue());
            assertArrayEquals(new Double[]{78.3d,41.23d,-99.66d},   (Double[])resFeature.getProperty("double").getValue());
            assertArrayEquals(new String[]{"marc","hubert","samy"}, (String[])resFeature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
    }
    
    @Test
    public void testArray2Insert() throws DataStoreException, VersioningException{
        reload(true);
            
        store.createFeatureType(FTYPE_ARRAY2.getName(), FTYPE_ARRAY2);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        final Feature feature = FeatureUtilities.defaultFeature(resType, "0");
        feature.getProperty("boolean").setValue(new Boolean[][]{{true,false,true},{false,true,false},{false,false,false}});
        feature.getProperty("byte").setValue(new Short[][]{{1,2,3},{4,5,6},{7,8,9}});
        feature.getProperty("short").setValue(new Short[][]{{1,2,3},{4,5,6},{7,8,9}});
        feature.getProperty("integer").setValue(new Integer[][]{{1,2,3},{4,5,6},{7,8,9}});
        feature.getProperty("long").setValue(new Long[][]{{1l,2l,3l},{4l,5l,6l},{7l,8l,9l}});
        feature.getProperty("float").setValue(new Float[][]{{1f,2f,3f},{4f,5f,6f},{7f,8f,9f}});
        feature.getProperty("double").setValue(new Double[][]{{1d,2d,3d},{4d,5d,6d},{7d,8d,9d}});
        feature.getProperty("string").setValue(new String[][]{{"1","2","3"},{"4","5","6"},{"7","8","9"}});
        
        store.addFeatures(resType.getName(), Collections.singleton(feature));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(resFeature instanceof SimpleFeature);
            assertArrayEquals((Boolean[][])feature.getProperty("boolean").getValue(),       (Boolean[][])resFeature.getProperty("boolean").getValue());
            assertArrayEquals((Short[][])feature.getProperty("byte").getValue(),       (Short[][])resFeature.getProperty("byte").getValue());
            assertArrayEquals((Short[][])feature.getProperty("short").getValue(),       (Short[][])resFeature.getProperty("short").getValue());
            assertArrayEquals((Integer[][])feature.getProperty("integer").getValue(),       (Integer[][])resFeature.getProperty("integer").getValue());
            assertArrayEquals((Long[][])feature.getProperty("long").getValue(),       (Long[][])resFeature.getProperty("long").getValue());
            assertArrayEquals((Float[][])feature.getProperty("float").getValue(),       (Float[][])resFeature.getProperty("float").getValue());
            assertArrayEquals((Double[][])feature.getProperty("double").getValue(),       (Double[][])resFeature.getProperty("double").getValue());
            assertArrayEquals((String[][])feature.getProperty("string").getValue(),       (String[][])resFeature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
        
    }
    
    @Test
    public void testGeometryInsert() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, VersioningException{
        reload(true);
            
        ////////////////////////////////////////////////////////////////////////
        final GeometryFactory gf = new GeometryFactory();
        //creating a point -----------------------------------------------
        final Point point = gf.createPoint(new Coordinate(56, 45));

        //creating a multipoint ------------------------------------------
        final MultiPoint mp = gf.createMultiPoint(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94)});

        //creating a linestring ------------------------------------------
        final LineString ls = gf.createLineString(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94)});

        //creating a multilinestring -------------------------------------
        final LineString ls1 = gf.createLineString(new Coordinate[]{
                                    new Coordinate(30, 45),new Coordinate(56, 29)});
        final LineString ls2 = gf.createLineString(new Coordinate[]{
                                    new Coordinate(98,12),new Coordinate(19, 87)});
        final MultiLineString mls = gf.createMultiLineString(new LineString[]{
                                    ls1,ls2});

        //creating a polygon ---------------------------------------------
        final LinearRing ring = gf.createLinearRing(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94),
                                    new Coordinate(23, 78)});
        final Polygon polygon = gf.createPolygon(ring, new LinearRing[0]);

        //creating a multipolygon ----------------------------------------
        final MultiPolygon mpolygon = gf.createMultiPolygon(new Polygon[]{polygon});
        
        //creating a geometry collection ----------------------------------------
        final GeometryCollection gc = gf.createGeometryCollection(new Geometry[]{point,ls,polygon});
        ////////////////////////////////////////////////////////////////////////
        
        
        store.createFeatureType(FTYPE_GEOMETRY.getName(), FTYPE_GEOMETRY);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        final Feature feature = FeatureUtilities.defaultFeature(resType, "0");
        feature.getProperty("geometry").setValue(point);
        feature.getProperty("point").setValue(point);
        feature.getProperty("multipoint").setValue(mp);
        feature.getProperty("linestring").setValue(ls);
        feature.getProperty("multilinestring").setValue(mls);
        feature.getProperty("polygon").setValue(polygon);
        feature.getProperty("multipolygon").setValue(mpolygon);
        feature.getProperty("geometrycollection").setValue(gc);
        
        store.addFeatures(resType.getName(), Collections.singleton(feature));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(resFeature instanceof SimpleFeature);
            Geometry geom;
            geom = (Geometry)resFeature.getProperty("geometry").getValue();
            assertEquals(point,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));        
            geom = (Geometry)resFeature.getProperty("point").getValue();
            assertEquals(point,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("multipoint").getValue();
            assertEquals(mp,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("linestring").getValue();
            assertEquals(ls,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("multilinestring").getValue();
            assertEquals(mls,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("polygon").getValue();
            assertEquals(polygon,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("multipolygon").getValue();
            assertEquals(mpolygon,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("geometrycollection").getValue();
            assertEquals(gc,geom);
            assertEquals(CRS_4326, JTS.findCoordinateReferenceSystem(geom));
        }finally{
            ite.close();
        }
    }
    
    /**
     * 2 level depths feature test.
     */
    @Test
    public void testComplexInsert() throws DataStoreException, VersioningException{
        reload(false);
        final GeometryFactory gf = new GeometryFactory();
            
        store.createFeatureType(FTYPE_COMPLEX.getName(), FTYPE_COMPLEX);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        final Feature voyage = FeatureUtilities.defaultFeature(resType, "0");
        voyage.getProperty("identifier").setValue(120);
        
        final ComplexAttribute driver = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("driver"));
        driver.getProperty("name").setValue("jean-michel");
        driver.getProperty("code").setValue("BHF:123456");
        voyage.getProperties().add(driver);
        
        final ComplexAttribute stop1 = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("stops"));
        stop1.getProperty("location").setValue(gf.createPoint(new Coordinate(-10, 60)));
        stop1.getProperty("time").setValue(new Date(5000000));
        voyage.getProperties().add(stop1);
        
        final ComplexAttribute stop2 = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("stops"));
        stop2.getProperty("location").setValue(gf.createPoint(new Coordinate(30, 15)));
        stop2.getProperty("time").setValue(new Date(6000000));
        voyage.getProperties().add(stop2);
        
        final ComplexAttribute stop3 = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("stops"));
        stop3.getProperty("location").setValue(gf.createPoint(new Coordinate(40, -70)));
        stop3.getProperty("time").setValue(new Date(7000000));
        voyage.getProperties().add(stop3);
        
        store.addFeatures(resType.getName(), Collections.singleton(voyage));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(!(resFeature instanceof SimpleFeature));
            
            assertEquals(120l, resFeature.getProperty("identifier").getValue());
            
            final ComplexAttribute resDriver = (ComplexAttribute) resFeature.getProperty("driver");
            assertEquals("jean-michel", resDriver.getProperty("name").getValue());
            assertEquals("BHF:123456", resDriver.getProperty("code").getValue());
            
            final Collection<Property> stops = resFeature.getProperties("stops");
            assertEquals(3, stops.size());
            final boolean[] found = new boolean[3];
            for(Property stop : stops){
                assertTrue(stop instanceof ComplexAttribute);
                final ComplexAttribute ca = (ComplexAttribute) stop;
                final Timestamp time = (Timestamp) ca.getProperty("time").getValue();
                final Point location = (Point) ca.getProperty("location").getValue();
                if(time.getTime() == 5000000){
                    assertEquals(stop1.getProperty("location").getValue(), location);
                    found[0] = true;
                }else if(time.getTime() == 6000000){
                    assertEquals(stop2.getProperty("location").getValue(), location);
                    found[1] = true;
                }else if(time.getTime() == 7000000){
                    assertEquals(stop3.getProperty("location").getValue(), location);
                    found[2] = true;
                }else{
                    fail("Unexpected property \n"+ca);
                }
            }
            
            for(boolean b : found) assertTrue(b);
            
        }finally{
            ite.close();
        }
        
    }
    
    /**
     * 3 level depths feature test.
     */
    @Test
    public void testComplex2Insert() throws DataStoreException, VersioningException{
        reload(false);
            
        store.createFeatureType(FTYPE_COMPLEX2.getName(), FTYPE_COMPLEX2);
        final FeatureType soundingType = store.getFeatureType(store.getNames().iterator().next());
        final PropertyDescriptor recordType = soundingType.getDescriptor("records");
        final PropertyDescriptor dataType = ((ComplexType)recordType.getType()).getDescriptor("datas");
        
        final Feature sounding = FeatureUtilities.defaultFeature(soundingType, "0");
        sounding.getProperty("identifier").setValue(120);
        
        final ComplexAttribute record1 = (ComplexAttribute)FeatureUtilities.defaultProperty(recordType);
        record1.getProperty("time").setValue(new Date(5000000));        
        final ComplexAttribute data11 = (ComplexAttribute)FeatureUtilities.defaultProperty(dataType);
        data11.getProperty("values").setValue(new Float[]{1f,2f,3f});
        record1.getProperties().add(data11);
        final ComplexAttribute data12 = (ComplexAttribute)FeatureUtilities.defaultProperty(dataType);
        data12.getProperty("values").setValue(new Float[]{4f,5f,6f});
        record1.getProperties().add(data12);
        
        final ComplexAttribute record2 = (ComplexAttribute)FeatureUtilities.defaultProperty(recordType);
        record2.getProperty("time").setValue(new Date(6000000));
        final ComplexAttribute data21 = (ComplexAttribute)FeatureUtilities.defaultProperty(dataType);
        data21.getProperty("values").setValue(new Float[]{7f,8f,9f});
        record2.getProperties().add(data21);
        
        
        sounding.getProperties().add(record1);
        sounding.getProperties().add(record2);
        
                
        store.addFeatures(soundingType.getName(), Collections.singleton(sounding));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(soundingType.getName()));
        assertEquals(1, col.size());
        
        final FeatureIterator ite = store.getFeatureReader(QueryBuilder.all(soundingType.getName()));
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(!(resFeature instanceof SimpleFeature));
            
            assertEquals(120l, resFeature.getProperty("identifier").getValue());
            
            
            final Collection<Property> records = resFeature.getProperties("records");
            assertEquals(2, records.size());
            final boolean[] found = new boolean[2];
            for(Property record : records){
                assertTrue(record instanceof ComplexAttribute);
                assertNotNull(record.getDescriptor());
                final ComplexAttribute ca = (ComplexAttribute) record;
                final Timestamp time = (Timestamp) ca.getProperty("time").getValue();
                if(time.getTime() == 5000000){
                    found[0] = true;
                    
                    final Collection<Property> datas = ((ComplexAttribute)record).getProperties("datas");
                    assertEquals(2, datas.size());
                    final boolean[] dfound = new boolean[2];
                    for(Property data : datas){
                        assertTrue(data instanceof ComplexAttribute);
                        final ComplexAttribute dca = (ComplexAttribute) data;
                         assertNotNull(dca.getDescriptor());
                        final Float[] values = (Float[]) dca.getProperty("values").getValue();
                        if(Arrays.equals(values, new Float[]{1f,2f,3f})){
                            dfound[0] = true;
                        }else if(Arrays.equals(values, new Float[]{4f,5f,6f})){
                            dfound[1] = true;
                        }else{
                            fail("Unexpected property \n"+dca);
                        }
                    }
                    for(boolean b : dfound) assertTrue(b);
                    
                }else if(time.getTime() == 6000000){
                    found[1] = true;
                    
                    final Collection<Property> datas = ((ComplexAttribute)record).getProperties("datas");
                    assertEquals(1, datas.size());
                    final boolean[] dfound = new boolean[1];
                    for(Property data : datas){
                        assertTrue(data instanceof ComplexAttribute);
                        final ComplexAttribute dca = (ComplexAttribute) data;
                         assertNotNull(dca.getDescriptor());
                        final Float[] values = (Float[]) dca.getProperty("values").getValue();
                        if(Arrays.equals(values, new Float[]{7f,8f,9f})){
                            dfound[0] = true;
                        }else{
                            fail("Unexpected property \n"+dca);
                        }
                    }
                    for(boolean b : dfound) assertTrue(b);
                    
                    
                }else{
                    fail("Unexpected property \n"+ca);
                }
            }
            
            for(boolean b : found) assertTrue(b);
            
        }finally{
            ite.close();
        }
    }
    
    /**
     * multiple complex properties of same type
     */
    @Test
    public void testComplex3Insert() throws DataStoreException, VersioningException{
        reload(false);
            
        store.createFeatureType(FTYPE_COMPLEX3.getName(), FTYPE_COMPLEX3);
        final FeatureType recordType = store.getFeatureType(store.getNames().iterator().next());
        final PropertyDescriptor data1Type = recordType.getDescriptor("data1");
        final PropertyDescriptor data2Type = recordType.getDescriptor("data2");
        final PropertyDescriptor data3Type = recordType.getDescriptor("data3");
        
        final Feature record = FeatureUtilities.defaultFeature(recordType, "0");
        record.getProperty("identifier").setValue(120);
        
        final ComplexAttribute data1 = (ComplexAttribute)FeatureUtilities.defaultProperty(data1Type);
        data1.getProperty("value").setValue(5f);
        final ComplexAttribute data2 = (ComplexAttribute)FeatureUtilities.defaultProperty(data2Type);
        data2.getProperty("value").setValue(10f);
        final ComplexAttribute data3 = (ComplexAttribute)FeatureUtilities.defaultProperty(data3Type);
        data3.getProperty("value").setValue(15f);
        
        record.getProperties().add(data1);
        record.getProperties().add(data2);
        record.getProperties().add(data3);
                        
        store.addFeatures(recordType.getName(), Collections.singleton(record));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(recordType.getName()));
        assertEquals(1, col.size());
        
        final FeatureIterator ite = store.getFeatureReader(QueryBuilder.all(recordType.getName()));
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertTrue(!(resFeature instanceof SimpleFeature));
            
            assertEquals(120l, resFeature.getProperty("identifier").getValue());
            
            assertNotNull(resFeature.getProperty("data1"));
            assertNotNull(resFeature.getProperty("data2"));
            assertNotNull(resFeature.getProperty("data3"));
            assertEquals(5f, ((ComplexAttribute)resFeature.getProperty("data1")).getProperty("value").getValue());
            assertEquals(10f, ((ComplexAttribute)resFeature.getProperty("data2")).getProperty("value").getValue());
            assertEquals(15f, ((ComplexAttribute)resFeature.getProperty("data3")).getProperty("value").getValue());
        }finally{
            ite.close();
        }
        
    }
    
    
    /**
     * Test hand made query.
     * 
     * @throws DataStoreException
     * @throws VersioningException 
     */
    @Test
    public void testHandMadeSQLQuery() throws Exception{
        reload(false);
        final GeometryFactory gf = new GeometryFactory();
            
        store.createFeatureType(FTYPE_COMPLEX.getName(), FTYPE_COMPLEX);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        final Feature voyage = FeatureUtilities.defaultFeature(resType, "0");
        voyage.getProperty("identifier").setValue(120);
        
        final ComplexAttribute driver = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("driver"));
        driver.getProperty("name").setValue("jean-michel");
        driver.getProperty("code").setValue("BHF:123456");
        voyage.getProperties().add(driver);
        
        final ComplexAttribute stop1 = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("stops"));
        stop1.getProperty("location").setValue(gf.createPoint(new Coordinate(-10, 60)));
        stop1.getProperty("time").setValue(new Date(5000000));
        voyage.getProperties().add(stop1);
        
        final ComplexAttribute stop2 = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("stops"));
        stop2.getProperty("location").setValue(gf.createPoint(new Coordinate(30, 15)));
        stop2.getProperty("time").setValue(new Date(6000000));
        voyage.getProperties().add(stop2);
        
        final ComplexAttribute stop3 = (ComplexAttribute)FeatureUtilities.defaultProperty(resType.getDescriptor("stops"));
        stop3.getProperty("location").setValue(gf.createPoint(new Coordinate(40, -70)));
        stop3.getProperty("time").setValue(new Date(7000000));
        voyage.getProperties().add(stop3);
        
        store.addFeatures(resType.getName(), Collections.singleton(voyage));
        
        final Query query = QueryBuilder.language(JDBCFeatureStore.CUSTOM_SQL, "SELECT * FROM \"Stop\"", new DefaultName("s1"));        
        final FeatureReader ite = store.getFeatureReader(query);
        final boolean[] found = new boolean[3];
        try{
            while(ite.hasNext()){
                final Feature feature = ite.next();
                final Timestamp time = (Timestamp) feature.getProperty("time").getValue();
                final Point location = (Point) feature.getProperty("location").getValue();
                if(time.getTime() == 5000000){
                    assertEquals(stop1.getProperty("location").getValue(), location);
                    found[0] = true;
                }else if(time.getTime() == 6000000){
                    assertEquals(stop2.getProperty("location").getValue(), location);
                    found[1] = true;
                }else if(time.getTime() == 7000000){
                    assertEquals(stop3.getProperty("location").getValue(), location);
                    found[2] = true;
                }else{
                    fail("Unexpected property \n"+feature);
                }
                assertNotNull(JTS.findCoordinateReferenceSystem((Geometry)feature.getDefaultGeometryProperty().getValue()));
            }
        }finally{
            ite.close();
        }
        
        for(boolean b : found) assertTrue(b);        
        
    }
    
    
}
