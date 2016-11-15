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
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.JDBCFeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.*;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import static org.junit.Assert.*;
import org.geotoolkit.storage.DataStores;
import org.junit.After;
import org.opengis.filter.identity.FeatureId;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Ignore;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@Ignore
public class PostgresComplexTypeTest extends org.geotoolkit.test.TestBase {

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

    public PostgresComplexTypeTest(){
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
        params = FeatureExt.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false);
    }

    private void reload(boolean simpleType) throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        //open in complex type to delete all types
        ParametersExt.getOrCreateValue(params, PostgresFeatureStoreFactory.SIMPLETYPE.getName().getCode()).setValue(false);
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
        ParametersExt.getOrCreateValue(params, PostgresFeatureStoreFactory.SIMPLETYPE.getName().getCode()).setValue(simpleType);
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
    public void testCrossSchemaRelation() throws DataStoreException, VersioningException, SQLException{
        reload(false);

        try (Connection cnx = store.getDataSource().getConnection()) {
            cnx.createStatement().executeUpdate("CREATE TABLE \"localtable\" (id serial, other integer);");
            cnx.createStatement().executeUpdate("DROP SCHEMA  IF EXISTS someothertestschema CASCADE;");
            cnx.createStatement().executeUpdate("CREATE SCHEMA someothertestschema;");
            cnx.createStatement().executeUpdate("CREATE TABLE \"someothertestschema\".\"othertable\" (ident serial PRIMARY KEY, field double precision);");
            cnx.createStatement().executeUpdate("ALTER TABLE \"localtable\" ADD FOREIGN KEY (other) REFERENCES someothertestschema.othertable(ident)");
        }

        store.refreshMetaModel();

        final FeatureType ft = store.getFeatureType("localtable");

        assertEquals("localtable", ft.getName().tip().toString());
        assertEquals(3,ft.getProperties(true).size());
        assertNotNull(ft.getProperty("id"));
        assertEquals(Integer.class, ((AttributeType)ft.getProperty("id")).getValueClass());
        final PropertyType desc = ft.getProperty("other");
        assertNotNull(desc);
        assertTrue(desc instanceof Operation);
        FeatureAssociationRole far = (FeatureAssociationRole) ((Operation)desc).getResult();
        final FeatureType ct = far.getValueType();
        assertEquals(3,ct.getProperties(true).size());
        assertNotNull(ct.getProperty("ident"));
        assertEquals(Integer.class, ((AttributeType)ct.getProperty("ident")).getValueClass());
        assertNotNull(ct.getProperty("field"));
        assertEquals(Double.class, ((AttributeType)ct.getProperty("field")).getValueClass());

    }

    @Test
    public void testFeatureTypeCreation() throws DataStoreException, VersioningException{
        reload(false);

        final FeatureType refType = FTYPE_COMPLEX;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final GenericName name = store.getNames().iterator().next();
        final FeatureType created = store.getFeatureType(name.toString());
        lazyCompare(refType, created);

    }

    @Test
    public void testFeatureType2Creation() throws DataStoreException, VersioningException{
        reload(false);

        final FeatureType refType = FTYPE_COMPLEX2;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final GenericName name = store.getNames().iterator().next();
        final FeatureType created = store.getFeatureType(name.toString());
        lazyCompare(refType, created);

    }
    
    @Test
    public void testFeatureType3Creation() throws DataStoreException, VersioningException{
        reload(false);

        final FeatureType refType = FTYPE_COMPLEX3;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType created = store.getFeatureType(FTYPE_COMPLEX3.getName().tip().toString());
        lazyCompare(refType, created);
    }

    /**
     * 2 level depths feature test.
     */
    @Test
    public void testComplexInsert() throws DataStoreException, VersioningException{
        reload(false);
        final GeometryFactory gf = new GeometryFactory();

        store.createFeatureType(FTYPE_COMPLEX);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature voyage = resType.newInstance();
        voyage.setPropertyValue("identifier",120l);

        final Feature driver = FTYPE_DRIVER.newInstance();
        driver.setPropertyValue("name","jean-michel");
        driver.setPropertyValue("code","BHF:123456");
        voyage.setPropertyValue("driver", driver);

        final Feature stop1 = FTYPE_STOP.newInstance();
        stop1.setPropertyValue("location",gf.createPoint(new Coordinate(-10, 60)));
        stop1.setPropertyValue("time",new Date(5000000));
        final Feature stop2 = FTYPE_STOP.newInstance();
        stop2.setPropertyValue("location",gf.createPoint(new Coordinate(30, 15)));
        stop2.setPropertyValue("time",new Date(6000000));
        final Feature stop3 = FTYPE_STOP.newInstance();
        stop3.setPropertyValue("location",gf.createPoint(new Coordinate(40, -70)));
        stop3.setPropertyValue("time",new Date(7000000));
        voyage.setPropertyValue("stops", Arrays.asList(stop1,stop2,stop3));

        List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(voyage));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("Voyage.1"), addedIds.get(0));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);

            assertEquals(120l, resFeature.getPropertyValue("identifier"));

            final Feature resDriver = (Feature) resFeature.getProperty("driver");
            assertEquals("jean-michel", resDriver.getPropertyValue("name"));
            assertEquals("BHF:123456", resDriver.getPropertyValue("code"));

            final Collection<Feature> stops = (Collection<Feature>) resFeature.getPropertyValue("stops");
            assertEquals(3, stops.size());
            final boolean[] found = new boolean[3];
            for(Feature stop : stops){
                final Timestamp time = (Timestamp) stop.getPropertyValue("time");
                final Point location = (Point) stop.getPropertyValue("location");
                if(time.getTime() == 5000000){
                    assertEquals(stop1.getPropertyValue("location"), location);
                    found[0] = true;
                }else if(time.getTime() == 6000000){
                    assertEquals(stop2.getPropertyValue("location"), location);
                    found[1] = true;
                }else if(time.getTime() == 7000000){
                    assertEquals(stop3.getPropertyValue("location"), location);
                    found[2] = true;
                }else{
                    fail("Unexpected property \n"+stop);
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

        store.createFeatureType(FTYPE_COMPLEX2);
        final FeatureType soundingType = store.getFeatureType(store.getNames().iterator().next().toString());
        final FeatureType recordType = ((FeatureAssociationRole)soundingType.getProperty("records")).getValueType();
        final FeatureType dataType = ((FeatureAssociationRole)recordType.getProperty("datas")).getValueType();

        final Feature sounding = soundingType.newInstance();
        sounding.setPropertyValue("identifier",120l);

        final Feature record1 = recordType.newInstance();
        record1.setPropertyValue("time",new Date(5000000));
        final Feature data11 = dataType.newInstance();
        data11.setPropertyValue("values",new Float[]{1f,2f,3f});
        final Feature data12 = dataType.newInstance();
        data12.setPropertyValue("values",new Float[]{4f,5f,6f});
        record1.setPropertyValue("datas", Arrays.asList(data11,data12));

        final Feature record2 = recordType.newInstance();
        record2.setPropertyValue("time",new Date(6000000));
        final Feature data21 = dataType.newInstance();
        data21.setPropertyValue("values",new Float[]{7f,8f,9f});
        record2.setPropertyValue("datas", Arrays.asList(data21));

        sounding.setPropertyValue("records", Arrays.asList(record1,record2));


        List<FeatureId> addedIds = store.addFeatures(soundingType.getName().toString(), Collections.singleton(sounding));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("Sounding.1"), addedIds.get(0));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(soundingType.getName().toString()));
        assertEquals(1, col.size());

        final FeatureIterator ite = store.getFeatureReader(QueryBuilder.all(soundingType.getName().toString()));
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);

            assertEquals(120l, resFeature.getPropertyValue("identifier"));


            final Collection<Feature> records = (Collection<Feature>) resFeature.getPropertyValue("records");
            assertEquals(2, records.size());
            final boolean[] found = new boolean[2];
            for(Feature record : records){
                final Timestamp time = (Timestamp) record.getPropertyValue("time");
                if(time.getTime() == 5000000){
                    found[0] = true;

                    final Collection<Feature> datas = (Collection<Feature>) record.getPropertyValue("datas");
                    assertEquals(2, datas.size());
                    final boolean[] dfound = new boolean[2];
                    for(Feature data : datas){
                        final Float[] values = (Float[]) data.getPropertyValue("values");
                        if(Arrays.equals(values, new Float[]{1f,2f,3f})){
                            dfound[0] = true;
                        }else if(Arrays.equals(values, new Float[]{4f,5f,6f})){
                            dfound[1] = true;
                        }else{
                            fail("Unexpected property \n"+data);
                        }
                    }
                    for(boolean b : dfound) assertTrue(b);

                }else if(time.getTime() == 6000000){
                    found[1] = true;

                    final Collection<Feature> datas = (Collection<Feature>) record.getPropertyValue("datas");
                    assertEquals(1, datas.size());
                    final boolean[] dfound = new boolean[1];
                    for(Feature data : datas){
                        final Float[] values = (Float[]) data.getPropertyValue("values");
                        if(Arrays.equals(values, new Float[]{7f,8f,9f})){
                            dfound[0] = true;
                        }else{
                            fail("Unexpected property \n"+data);
                        }
                    }
                    for(boolean b : dfound) assertTrue(b);


                }else{
                    fail("Unexpected property \n"+record);
                }
            }

            for(boolean b : found) assertTrue(b);

        }finally{
            ite.close();
        }
    }

    /**
     * multiple complex properties of same type
     * @throws org.apache.sis.storage.DataStoreException
     * @throws org.geotoolkit.version.VersioningException
     */
    @Test
    public void testComplex3Insert() throws DataStoreException, VersioningException{
        reload(false);

        store.createFeatureType(FTYPE_COMPLEX3);
        final FeatureType recordType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature record = FTYPE_RECORD.newInstance();
        record.setPropertyValue("identifier",120);

        final Feature data1 = FTYPE_DATA.newInstance();
        data1.setPropertyValue("value",5f);
        final Feature data2 = FTYPE_DATA.newInstance();
        data2.setPropertyValue("value",10f);
        final Feature data3 = FTYPE_DATA.newInstance();
        data3.setPropertyValue("value",15f);
        record.setPropertyValue("datas", Arrays.asList(data1,data2,data3));

        List<FeatureId> addedIds = store.addFeatures(recordType.getName().toString(), Collections.singleton(record));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("Record.1"), addedIds.get(0));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(recordType.getName().toString()));
        assertEquals(1, col.size());

        final FeatureIterator ite = store.getFeatureReader(QueryBuilder.all(recordType.getName().toString()));
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);

            assertEquals(120l, resFeature.getPropertyValue("identifier"));

            assertNotNull(resFeature.getProperty("data1"));
            assertNotNull(resFeature.getProperty("data2"));
            assertNotNull(resFeature.getProperty("data3"));
            assertEquals(5f, ((Feature)resFeature.getPropertyValue("data1")).getPropertyValue("value"));
            assertEquals(10f, ((Feature)resFeature.getPropertyValue("data2")).getPropertyValue("value"));
            assertEquals(15f, ((Feature)resFeature.getPropertyValue("data3")).getPropertyValue("value"));
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

        store.createFeatureType(FTYPE_COMPLEX);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature voyage = resType.newInstance();
        voyage.setPropertyValue("identifier",120l);

        final Feature driver = FTYPE_DRIVER.newInstance();
        driver.setPropertyValue("name","jean-michel");
        driver.setPropertyValue("code","BHF:123456");
        voyage.setPropertyValue("driver", driver);

        final Feature stop1 = FTYPE_STOP.newInstance();
        stop1.setPropertyValue("location",gf.createPoint(new Coordinate(-10, 60)));
        stop1.setPropertyValue("time",new Date(5000000));
        final Feature stop2 = FTYPE_STOP.newInstance();
        stop2.setPropertyValue("location",gf.createPoint(new Coordinate(30, 15)));
        stop2.setPropertyValue("time",new Date(6000000));
        final Feature stop3 = FTYPE_STOP.newInstance();
        stop3.setPropertyValue("location",gf.createPoint(new Coordinate(40, -70)));
        stop3.setPropertyValue("time",new Date(7000000));
        voyage.setPropertyValue("stops", Arrays.asList(stop1,stop2,stop3));

        List<FeatureId> addedIds = store.addFeatures(resType.getName().toString(), Collections.singleton(voyage));

        assertEquals(1, addedIds.size());
        assertEquals(new DefaultFeatureId("Voyage.1"), addedIds.get(0));

        final Query query = QueryBuilder.language(JDBCFeatureStore.CUSTOM_SQL, "SELECT * FROM \"Stop\"", "s1");
        final FeatureReader ite = store.getFeatureReader(query);
        final boolean[] found = new boolean[3];
        try{
            while(ite.hasNext()){
                final Feature feature = ite.next();
                final Timestamp time = (Timestamp) feature.getPropertyValue("time");
                final Point location = (Point) feature.getPropertyValue("location");
                if(time.getTime() == 5000000){
                    assertEquals(stop1.getPropertyValue("location"), location);
                    found[0] = true;
                }else if(time.getTime() == 6000000){
                    assertEquals(stop2.getPropertyValue("location"), location);
                    found[1] = true;
                }else if(time.getTime() == 7000000){
                    assertEquals(stop3.getPropertyValue("location"), location);
                    found[2] = true;
                }else{
                    fail("Unexpected property \n"+feature);
                }
                assertNotNull(JTS.findCoordinateReferenceSystem((Geometry)FeatureExt.getDefaultGeometryAttributeValue(feature)));
            }
        }finally{
            ite.close();
        }

        for(boolean b : found) assertTrue(b);

    }

    private void lazyCompare(final FeatureType refType, final FeatureType candidate){
        final GenericName name = refType.getName();
        assertEquals(refType.getName().tip().toString(), name.tip().toString());

        if(refType instanceof FeatureType){
            final FeatureType ct = (FeatureType) refType;
            final FeatureType cct = (FeatureType) candidate;
            assertEquals(ct.getProperties(true).size()+1, cct.getProperties(true).size());// +1 for generated fid field

            for(PropertyType desc : ct.getProperties(true)){
                final PropertyType cdesc = cct.getProperty(desc.getName().toString());
                assertEquals(desc, cdesc);
            }

        }else{
            final AttributeType at = (AttributeType) refType;
            final AttributeType cat = (AttributeType) candidate;
            if(at.getValueClass()==Date.class){
                assertEquals(Timestamp.class, cat.getValueClass());
            }else{
                assertEquals(at.getValueClass(), cat.getValueClass());
            }
        }
    }

}
