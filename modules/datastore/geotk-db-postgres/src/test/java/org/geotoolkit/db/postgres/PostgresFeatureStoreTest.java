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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
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
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresFeatureStoreTest {
    
    private static final double DELTA = 0.00000001;    
    private static final FeatureType FTYPE_SIMPLE;
    private static final FeatureType FTYPE_ARRAY;
    private static final FeatureType FTYPE_GEOMETRY;
    private static final FeatureType FTYPE_COMPLEX;
    
    private static final CoordinateReferenceSystem CRS_4326;
    
    static{
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
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
        
        try {
            CRS_4326 = CRS.decode("EPSG:4326",true);
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException("Failed to load CRS");
        } catch (FactoryException ex) {
            throw new RuntimeException("Failed to load CRS");
        }
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
        
        
        ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();

        ftb.setName("Stop");
        ftb.add("location", Point.class, CRS_4326);
        ftb.add("time", Date.class);
        final ComplexType trackPointType = ftb.buildType();

        ftb.reset();
        ftb.setName("Driver");
        ftb.add("name", String.class);
        ftb.add("code", String.class);
        final ComplexType fishType = ftb.buildType();

        ftb.reset();
        ftb.setName("Voyage");
        ftb.add("identifier", Long.class);
        AttributeDescriptor elementDesc = adb.create(fishType, DefaultName.valueOf("driver"),1,1,false,null);
        AttributeDescriptor stepDesc = adb.create(trackPointType, DefaultName.valueOf("stops"),0,Integer.MAX_VALUE,false,null);
        ftb.add(elementDesc);
        ftb.add(stepDesc);
        FTYPE_COMPLEX = ftb.buildFeatureType();
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
    
    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.dispose();
        }
        
        store = FeatureStoreFinder.open(params);
        
        for(Name n : store.getNames()){
            VersionControl vc = store.getVersioning(n);
            vc.dropVersioning();
            store.deleteSchema(n);
        }
        assertTrue(store.getNames().isEmpty());
    }
    
    @Test
    public void testSimpleTypeCreation() throws DataStoreException, VersioningException {
        reload();
        
        final FeatureType refType = FTYPE_SIMPLE;        
        store.createSchema(refType.getName(), refType);        
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
        reload();
        
        final FeatureType refType = FTYPE_ARRAY;        
        store.createSchema(refType.getName(), refType);        
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
        reload();
                
        final FeatureType refType = FTYPE_GEOMETRY;        
        store.createSchema(refType.getName(), refType);        
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
        
    @Ignore
    @Test
    public void testComplexTypeCreation() throws DataStoreException, VersioningException{
        reload();
        
        final FeatureType refType = FTYPE_COMPLEX;        
        store.createSchema(refType.getName(), refType);        
        assertEquals(3, store.getNames().size());
    }
    
    @Test
    public void testSimpleInsert() throws DataStoreException, VersioningException{
        reload();
            
        store.createSchema(FTYPE_SIMPLE.getName(), FTYPE_SIMPLE);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next());
        
        final Feature feature = FeatureUtilities.defaultFeature(resType, "0");
        feature.getProperty("boolean").setValue(true);
        feature.getProperty("byte").setValue(45);
        feature.getProperty("short").setValue(963);
        feature.getProperty("integer").setValue(123456);
        feature.getProperty("long").setValue(456789l);
        feature.getProperty("float").setValue(7.3f);
        feature.getProperty("double").setValue(14.5);
        feature.getProperty("string").setValue("a string");
        
        store.addFeatures(resType.getName(), Collections.singleton(feature));
        
        final Session session = store.createSession(false);
        final FeatureCollection<Feature> col = session.getFeatureCollection(QueryBuilder.all(resType.getName()));
        assertEquals(1, col.size());
        
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
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
        
    }
        
    @Test
    public void testArrayInsert() throws DataStoreException, VersioningException{
        reload();
            
        store.createSchema(FTYPE_ARRAY.getName(), FTYPE_ARRAY);
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
    public void testGeometryInsert() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, VersioningException{
        reload();
            
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
        
        
        store.createSchema(FTYPE_GEOMETRY.getName(), FTYPE_GEOMETRY);
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
    
    
}
