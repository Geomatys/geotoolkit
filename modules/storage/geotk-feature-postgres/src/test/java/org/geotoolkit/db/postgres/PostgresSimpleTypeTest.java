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
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.parameter.Parameters;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.*;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import static org.junit.Assert.*;
import org.geotoolkit.storage.DataStores;
import org.junit.After;
import org.opengis.filter.identity.FeatureId;
import org.apache.sis.referencing.CommonCRS;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresSimpleTypeTest extends org.geotoolkit.test.TestBase {

    /** driver types */
    private static final FeatureType FTYPE_DRIVER;
    /** stop types */
    private static final FeatureType FTYPE_STOP;
    /** data types */
    private static final FeatureType FTYPE_DATA;
    /** record types */
    private static final FeatureType FTYPE_RECORD;
    /** sdata types */
    private static final FeatureType FTYPE_SDATA;
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

    static{

        ////////////////////////////////////////////////////////////////////////
        FeatureTypeBuilder  ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.addAttribute(Boolean.class).setName("boolean");
        ftb.addAttribute(Byte.class).setName("byte");
        ftb.addAttribute(Short.class).setName("short");
        ftb.addAttribute(Integer.class).setName("integer");
        ftb.addAttribute(Long.class).setName("long");
        ftb.addAttribute(Float.class).setName("float");
        ftb.addAttribute(Double.class).setName("double");
        ftb.addAttribute(String.class).setName("string");
        FTYPE_SIMPLE = ftb.build();

        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.addAttribute(Boolean[].class).setName("boolean");
        ftb.addAttribute(Byte[].class).setName("byte");
        ftb.addAttribute(Short[].class).setName("short");
        ftb.addAttribute(Integer[].class).setName("integer");
        ftb.addAttribute(Long[].class).setName("long");
        ftb.addAttribute(Float[].class).setName("float");
        ftb.addAttribute(Double[].class).setName("double");
        ftb.addAttribute(String[].class).setName("string");
        FTYPE_ARRAY = ftb.build();

        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.addAttribute(Boolean[][].class).setName("boolean");
        ftb.addAttribute(Byte[][].class).setName("byte");
        ftb.addAttribute(Short[][].class).setName("short");
        ftb.addAttribute(Integer[][].class).setName("integer");
        ftb.addAttribute(Long[][].class).setName("long");
        ftb.addAttribute(Float[][].class).setName("float");
        ftb.addAttribute(Double[][].class).setName("double");
        ftb.addAttribute(String[][].class).setName("string");
        FTYPE_ARRAY2 = ftb.build();

        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(Point.class).setName("point").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(MultiPoint.class).setName("multipoint").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(LineString.class).setName("linestring").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(MultiLineString.class).setName("multilinestring").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(Polygon.class).setName("polygon").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(MultiPolygon.class).setName("multipolygon").setCRS(CommonCRS.WGS84.normalizedGeographic());
        ftb.addAttribute(GeometryCollection.class).setName("geometrycollection").setCRS(CommonCRS.WGS84.normalizedGeographic());
        FTYPE_GEOMETRY = ftb.build();


        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("Stop");
        ftb.addAttribute(Point.class).setName("location").setCRS(CommonCRS.defaultGeographic());
        ftb.addAttribute(Date.class).setName("time");
        FTYPE_STOP = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Driver");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("code");
        FTYPE_DRIVER = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Voyage");
        ftb.addAttribute(Long.class).setName("identifier");
        ftb.addAssociation(FTYPE_DRIVER).setName("driver");
        ftb.addAssociation(FTYPE_STOP).setName("stops").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        FTYPE_COMPLEX = ftb.build();


        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("Data");
        ftb.addAttribute(Float[].class).setName("values");
        FTYPE_DATA = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Record");
        ftb.addAttribute(Date.class).setName("time");
        ftb.addAssociation(FTYPE_DATA).setName("datas").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        FTYPE_RECORD = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Sounding");
        ftb.addAttribute(Long.class).setName("identifier");
        ftb.addAssociation(FTYPE_RECORD).setName("records").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        FTYPE_COMPLEX2 = ftb.build();


        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
        ftb.setName("Data");
        ftb.addAttribute(Float.class).setName("value");
        FTYPE_SDATA = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Record");
        ftb.addAttribute(Long.class).setName("identifier");
        ftb.addAssociation(FTYPE_SDATA).setName("data1");
        ftb.addAssociation(FTYPE_SDATA).setName("data2");
        ftb.addAssociation(FTYPE_SDATA).setName("data3");
        FTYPE_COMPLEX3 = ftb.build();

    }

    private PostgresFeatureStore store;

    public PostgresSimpleTypeTest(){
    }

    private static Parameters params;

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
        params = Parameters.castOrWrap(FeatureExt.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false));
    }

    private void reload(boolean simpleType) throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        //open in complex type to delete all types
        params.getOrCreate(PostgresFeatureStoreFactory.SIMPLETYPE).setValue(false);
        store = (PostgresFeatureStore) DataStores.open(params);
        while(!store.getNames().isEmpty()){ // we get the list each type because relations may delete multiple types each time
            final GenericName n = store.getNames().iterator().next();
            final VersionControl vc = store.getVersioning(n.toString());
            vc.dropVersioning();
            store.deleteFeatureType(n.toString());
        }
        assertTrue(store.getNames().isEmpty());
        store.close();

        //reopen the way it was asked
        params.getOrCreate(PostgresFeatureStoreFactory.SIMPLETYPE).setValue(simpleType);
        store = (PostgresFeatureStore) DataStores.open(params);
        assertTrue(store.getNames().isEmpty());
    }

    @After
    public void disposeStore() {
        if (store != null) {
            store.close();
        }
    }

    @Test
    public void testSimpleTypeCreation() throws DataStoreException, VersioningException {
        reload(true);

        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), refType.getName().tip().toString());
        //we expect one more field for id
        final ArrayList<? extends PropertyType> descs = new ArrayList<>(resType.getProperties(true));

        int index=2;
        PropertyType desc;
        desc = descs.get(index++);
        assertEquals("boolean", desc.getName().tip().toString());
        assertEquals(Boolean.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("byte", desc.getName().tip().toString());
        assertEquals(Short.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("short", desc.getName().tip().toString());
        assertEquals(Short.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("integer", desc.getName().tip().toString());
        assertEquals(Integer.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("long", desc.getName().tip().toString());
        assertEquals(Long.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("float", desc.getName().tip().toString());
        assertEquals(Float.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("double", desc.getName().tip().toString());
        assertEquals(Double.class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("string", desc.getName().tip().toString());
        assertEquals(String.class, ((AttributeType)desc).getValueClass());

    }

    @Test
    public void testArrayTypeCreation() throws DataStoreException, VersioningException {
        reload(true);

        final FeatureType refType = FTYPE_ARRAY;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), refType.getName().tip().toString());
        //we expect one more field for id
        final ArrayList<? extends PropertyType> descs = new ArrayList<>(resType.getProperties(true));

        //Postgis allow NULL in arrays, so returned array are not primitive types
        int index=2;
        PropertyType desc;
        desc = descs.get(index++);
        assertEquals("boolean", desc.getName().tip().toString());
        assertEquals(Boolean[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("byte", desc.getName().tip().toString());
        assertEquals(Short[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("short", desc.getName().tip().toString());
        assertEquals(Short[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("integer", desc.getName().tip().toString());
        assertEquals(Integer[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("long", desc.getName().tip().toString());
        assertEquals(Long[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("float", desc.getName().tip().toString());
        assertEquals(Float[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("double", desc.getName().tip().toString());
        assertEquals(Double[].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("string", desc.getName().tip().toString());
        assertEquals(String[].class, ((AttributeType)desc).getValueClass());

    }

    @Test
    public void testArray2TypeCreation() throws DataStoreException, VersioningException {
        reload(true);

        final FeatureType refType = FTYPE_ARRAY2;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), refType.getName().tip().toString());
        //we expect one more field for id
        final List<PropertyType> descs = new ArrayList<PropertyType>(resType.getProperties(true));

        //Postgis allow NULL in arrays, so returned array are not primitive types
        int index=2;
        PropertyType desc;
        desc = descs.get(index++);
        assertEquals("boolean", desc.getName().tip().toString());
        assertEquals(Boolean[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("byte", desc.getName().tip().toString());
        assertEquals(Short[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("short", desc.getName().tip().toString());
        assertEquals(Short[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("integer", desc.getName().tip().toString());
        assertEquals(Integer[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("long", desc.getName().tip().toString());
        assertEquals(Long[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("float", desc.getName().tip().toString());
        assertEquals(Float[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("double", desc.getName().tip().toString());
        assertEquals(Double[][].class, ((AttributeType)desc).getValueClass());
        desc = descs.get(index++);
        assertEquals("string", desc.getName().tip().toString());
        assertEquals(String[][].class, ((AttributeType)desc).getValueClass());

    }

    @Test
    public void testGeometryTypeCreation() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, VersioningException {
        reload(true);

        final FeatureType refType = FTYPE_GEOMETRY;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), refType.getName().tip().toString());
        //we expect one more field for id
        final ArrayList<? extends PropertyType> descs = new ArrayList<>(resType.getProperties(true));

        int index=2;
        PropertyType desc;
        desc = descs.get(index++);
        assertEquals("geometry", desc.getName().tip().toString());
        assertEquals(Geometry.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("point", desc.getName().tip().toString());
        assertEquals(Point.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("multipoint", desc.getName().tip().toString());
        assertEquals(MultiPoint.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("linestring", desc.getName().tip().toString());
        assertEquals(LineString.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("multilinestring", desc.getName().tip().toString());
        assertEquals(MultiLineString.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("polygon", desc.getName().tip().toString());
        assertEquals(Polygon.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("multipolygon", desc.getName().tip().toString());
        assertEquals(MultiPolygon.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("geometrycollection", desc.getName().tip().toString());
        assertEquals(GeometryCollection.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), FeatureExt.getCRS(desc));
    }

    @Test
    public void testSimpleInsert() throws DataStoreException, VersioningException{
        reload(true);

        store.createFeatureType(FTYPE_SIMPLE);
        FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        Feature feature = resType.newInstance();
        feature.setPropertyValue("boolean",true);
        feature.setPropertyValue("byte",(short)45); //byte type do not exist, it's converted to smallint/int2
        feature.setPropertyValue("short",(short)963);
        feature.setPropertyValue("integer",123456);
        feature.setPropertyValue("long",456789l);
        feature.setPropertyValue("float",7.3f);
        feature.setPropertyValue("double",14.5);
        feature.setPropertyValue("string","a string");

        List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("1"), addedIds.get(0));

        Session session = store.createSession(false);
        FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertEquals(true, resFeature.getPropertyValue("boolean"));
            assertEquals((short)45, resFeature.getPropertyValue("byte"));
            assertEquals((short)963, resFeature.getPropertyValue("short"));
            assertEquals(123456, resFeature.getPropertyValue("integer"));
            assertEquals(456789l, resFeature.getPropertyValue("long"));
            assertEquals(7.3f, resFeature.getPropertyValue("float"));
            assertEquals(14.5d, resFeature.getPropertyValue("double"));
            assertEquals("a string", resFeature.getPropertyValue("string"));
        }finally{
            ite.close();
        }


        // SECOND TEST for NAN values ------------------------------------------
        reload(true);
        store.createFeatureType(FTYPE_SIMPLE);
        resType = store.getFeatureType(store.getNames().iterator().next().toString());

        feature = resType.newInstance();
        feature.setPropertyValue("fid",0);
        feature.setPropertyValue("boolean",true);
        feature.setPropertyValue("byte",(short)45);
        feature.setPropertyValue("short",(short)963);
        feature.setPropertyValue("integer",123456);
        feature.setPropertyValue("long",456789l);
        feature.setPropertyValue("float",Float.NaN);
        feature.setPropertyValue("double",Double.NaN);
        feature.setPropertyValue("string","a string");

        addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("1"), addedIds.get(0));

        session = store.createSession(false);
        col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertEquals(true, resFeature.getPropertyValue("boolean"));
            assertEquals((short)45, resFeature.getPropertyValue("byte"));
            assertEquals((short)963, resFeature.getPropertyValue("short"));
            assertEquals(123456, resFeature.getPropertyValue("integer"));
            assertEquals(456789l, resFeature.getPropertyValue("long"));
            assertEquals(Float.NaN, resFeature.getPropertyValue("float"));
            assertEquals(Double.NaN, resFeature.getPropertyValue("double"));
            assertEquals("a string", resFeature.getPropertyValue("string"));
        }finally{
            ite.close();
        }



    }

    @Test
    public void testArrayInsert() throws DataStoreException, VersioningException{
        reload(true);

        store.createFeatureType(FTYPE_ARRAY);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature feature = resType.newInstance();
        feature.setPropertyValue("fid",0);
        feature.setPropertyValue("boolean",new Boolean[]{true,false,true});
        feature.setPropertyValue("byte",new Short[]{3,6,9});
        feature.setPropertyValue("short",new Short[]{-5,12,-50});
        feature.setPropertyValue("integer",new Integer[]{123,456,789});
        feature.setPropertyValue("long",new Long[]{111l,222l,333l});
        feature.setPropertyValue("float",new Float[]{1.2f,-5.9f,8.1f});
        feature.setPropertyValue("double",new Double[]{78.3d,41.23d,-99.66d});
        feature.setPropertyValue("string",new String[]{"marc","hubert","samy"});

         List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("1"), addedIds.get(0));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertArrayEquals(new Boolean[]{true,false,true},       (Boolean[])resFeature.getPropertyValue("boolean"));
            assertArrayEquals(new Short[]{3,6,9},                   (Short[])resFeature.getPropertyValue("byte"));
            assertArrayEquals(new Short[]{-5,12,-50},               (Short[])resFeature.getPropertyValue("short"));
            assertArrayEquals(new Integer[]{123,456,789},           (Integer[])resFeature.getPropertyValue("integer"));
            assertArrayEquals(new Long[]{111l,222l,333l},           (Long[])resFeature.getPropertyValue("long"));
            assertArrayEquals(new Float[]{1.2f,-5.9f,8.1f},         (Float[])resFeature.getPropertyValue("float"));
            assertArrayEquals(new Double[]{78.3d,41.23d,-99.66d},   (Double[])resFeature.getPropertyValue("double"));
            assertArrayEquals(new String[]{"marc","hubert","samy"}, (String[])resFeature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

    }

    @Test
    public void testArray2Insert() throws DataStoreException, VersioningException{
        reload(true);

        store.createFeatureType(FTYPE_ARRAY2);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature feature = resType.newInstance();
        feature.setPropertyValue("fid", 0);
        feature.setPropertyValue("boolean",new Boolean[][]{{true,false,true},{false,true,false},{false,false,false}});
        feature.setPropertyValue("byte",new Short[][]{{1,2,3},{4,5,6},{7,8,9}});
        feature.setPropertyValue("short",new Short[][]{{1,2,3},{4,5,6},{7,8,9}});
        feature.setPropertyValue("integer",new Integer[][]{{1,2,3},{4,5,6},{7,8,9}});
        feature.setPropertyValue("long",new Long[][]{{1l,2l,3l},{4l,5l,6l},{7l,8l,9l}});
        feature.setPropertyValue("float",new Float[][]{{1f,2f,3f},{4f,5f,6f},{7f,8f,9f}});
        feature.setPropertyValue("double",new Double[][]{{1d,2d,3d},{4d,5d,6d},{7d,8d,9d}});
        feature.setPropertyValue("string",new String[][]{{"1","2","3"},{"4","5","6"},{"7","8","9"}});

        List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("1"), addedIds.get(0));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertArrayEquals((Boolean[][])feature.getPropertyValue("boolean"),       (Boolean[][])resFeature.getPropertyValue("boolean"));
            assertArrayEquals((Short[][])feature.getPropertyValue("byte"),       (Short[][])resFeature.getPropertyValue("byte"));
            assertArrayEquals((Short[][])feature.getPropertyValue("short"),       (Short[][])resFeature.getPropertyValue("short"));
            assertArrayEquals((Integer[][])feature.getPropertyValue("integer"),       (Integer[][])resFeature.getPropertyValue("integer"));
            assertArrayEquals((Long[][])feature.getPropertyValue("long"),       (Long[][])resFeature.getPropertyValue("long"));
            assertArrayEquals((Float[][])feature.getPropertyValue("float"),       (Float[][])resFeature.getPropertyValue("float"));
            assertArrayEquals((Double[][])feature.getPropertyValue("double"),       (Double[][])resFeature.getPropertyValue("double"));
            assertArrayEquals((String[][])feature.getPropertyValue("string"),       (String[][])resFeature.getPropertyValue("string"));
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


        store.createFeatureType(FTYPE_GEOMETRY);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature feature = resType.newInstance();
        feature.setPropertyValue("fid", 0);
        feature.setPropertyValue("geometry",point);
        feature.setPropertyValue("point",point);
        feature.setPropertyValue("multipoint",mp);
        feature.setPropertyValue("linestring",ls);
        feature.setPropertyValue("multilinestring",mls);
        feature.setPropertyValue("polygon",polygon);
        feature.setPropertyValue("multipolygon",mpolygon);
        feature.setPropertyValue("geometrycollection",gc);

        List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("1"), addedIds.get(0));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            Geometry geom;
            geom = (Geometry)resFeature.getPropertyValue("geometry");
            assertEquals(point,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("point");
            assertEquals(point,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("multipoint");
            assertEquals(mp,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("linestring");
            assertEquals(ls,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("multilinestring");
            assertEquals(mls,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("polygon");
            assertEquals(polygon,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("multipolygon");
            assertEquals(mpolygon,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getPropertyValue("geometrycollection");
            assertEquals(gc,geom);
            assertEquals(CommonCRS.WGS84.normalizedGeographic(), JTS.findCoordinateReferenceSystem(geom));
        }finally{
            ite.close();
        }
    }

    /**
     * Test ugly named table
     *
     * @throws DataStoreException
     * @throws VersioningException
     */
    @Test
    public void testUglyTableName() throws Exception{
        reload(true);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test'te#st$'test\"'test");
        ftb.addAttribute(String.class).setName("text");
        final FeatureType ft = ftb.build();

        store.createFeatureType(ft);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature record = resType.newInstance();
        record.setPropertyValue("text","un'deux'trois'quatre");

        List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(record));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("1"), addedIds.get(0));

        final Query query = QueryBuilder.all(resType.getName().toString());
        final FeatureReader ite = store.getFeatureReader(query);
        boolean found = false;
        try{
            while(ite.hasNext()){
                final Feature feature = ite.next();
                Object val = feature.getPropertyValue("text");
                assertEquals("un'deux'trois'quatre", val);
                found = true;
            }
        }finally{
            ite.close();
        }

        assertTrue(found);
    }

}
