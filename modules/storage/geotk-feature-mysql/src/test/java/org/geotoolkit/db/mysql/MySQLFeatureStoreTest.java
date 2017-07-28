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
package org.geotoolkit.db.mysql;

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
import java.util.Optional;
import java.util.Properties;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.parameter.Parameters;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.JDBCFeatureStore;
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

import static org.geotoolkit.db.mysql.MySQLFeatureStoreFactory.*;
import org.geotoolkit.storage.DataStores;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MySQLFeatureStoreTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.00000001;
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
    /** geometric fields */
    private static final FeatureType FTYPE_GEOMETRY;
    /** 1 depth feature type */
    private static final FeatureType FTYPE_COMPLEX;
    /** 2 depth feature type */
    private static final FeatureType FTYPE_COMPLEX2;
    /** multiple properties of same complex type */
    private static final FeatureType FTYPE_COMPLEX3;

    static {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();
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
        final CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
        ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.addAttribute(Geometry.class).setName("geometry").setCRS(crs);
        ftb.addAttribute(Point.class).setName("point").setCRS(crs);
        ftb.addAttribute(MultiPoint.class).setName("multipoint").setCRS(crs);
        ftb.addAttribute(LineString.class).setName("linestring").setCRS(crs);
        ftb.addAttribute(MultiLineString.class).setName("multilinestring").setCRS(crs);
        ftb.addAttribute(Polygon.class).setName("polygon").setCRS(crs);
        ftb.addAttribute(MultiPolygon.class).setName("multipolygon").setCRS(crs);
        ftb.addAttribute(GeometryCollection.class).setName("geometrycollection").setCRS(crs);
        FTYPE_GEOMETRY = ftb.build();


        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();

        ftb.setName("Stop");
        ftb.addAttribute(Point.class).setName("location").setCRS(crs);
        ftb.addAttribute(Date.class).setName("time");
        FTYPE_STOP = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Driver");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("code");
        FTYPE_DRIVER = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Voyage");
        ftb.addAttribute(Long.class).setName("identifier").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAssociation(FTYPE_DRIVER).setName("driver");
        ftb.addAssociation(FTYPE_STOP).setName("stops").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        FTYPE_COMPLEX = ftb.build();


        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();

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
        ftb.addAttribute(Long.class).setName("identifier").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAssociation(FTYPE_RECORD).setName("records").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        FTYPE_COMPLEX2 = ftb.build();


        ////////////////////////////////////////////////////////////////////////
        ftb = new FeatureTypeBuilder();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Data");
        ftb.addAttribute(Float.class).setName("value");
        FTYPE_SDATA = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("Record");
        ftb.addAttribute(Long.class).setName("identifier").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAssociation(FTYPE_SDATA).setName("data1").setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAssociation(FTYPE_SDATA).setName("data2").setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAssociation(FTYPE_SDATA).setName("data3").setMinimumOccurs(0).setMaximumOccurs(1);
        FTYPE_COMPLEX3 = ftb.build();


    }

    private MySQLFeatureStore store;

    public MySQLFeatureStoreTest(){
    }

    private static Parameters params;

    /**
     * <p>Find JDBC connection parameters in specified file at
     * "/home/.geotoolkit.org/test-msfeature.properties".<br/>
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
        path += "/.geotoolkit.org/test-msfeature.properties";
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
        params.getOrCreate(MySQLFeatureStoreFactory.SIMPLETYPE).setValue(false);
        store = (MySQLFeatureStore) DataStores.open(params);
        for(GenericName n : store.getNames()){
            VersionControl vc = store.getVersioning(n.toString());
            vc.dropVersioning();
            store.deleteFeatureType(n.toString());
        }
        assertTrue(store.getNames().isEmpty());
        store.close();

        //reopen the way it was asked
        params.getOrCreate(MySQLFeatureStoreFactory.SIMPLETYPE).setValue(simpleType);
        store = (MySQLFeatureStore) DataStores.open(params);
        assertTrue(store.getNames().isEmpty());
    }

    @Ignore
    @Test
    public void testSimpleTypeCreation() throws DataStoreException, VersioningException {
        reload(true);

        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), refType.getName().tip().toString());
        //we expect one more field for id
        final List<PropertyType> descs = new ArrayList<PropertyType>(resType.getProperties(true));

        int index=1;
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

    @Ignore
    @Test
    public void testGeometryTypeCreation() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, VersioningException {
        reload(true);

        final FeatureType refType = FTYPE_GEOMETRY;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), refType.getName().tip().toString());
        //we expect one more field for id
        final List<? extends PropertyType> descs = new ArrayList<>(resType.getProperties(true));

        int index=1;
        PropertyType desc;
        desc = descs.get(index++);
        assertEquals("geometry", desc.getName().tip().toString());
        assertEquals(Geometry.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("point", desc.getName().tip().toString());
        assertEquals(Point.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("multipoint", desc.getName().tip().toString());
        assertEquals(MultiPoint.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("linestring", desc.getName().tip().toString());
        assertEquals(LineString.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("multilinestring", desc.getName().tip().toString());
        assertEquals(MultiLineString.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("polygon", desc.getName().tip().toString());
        assertEquals(Polygon.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("multipolygon", desc.getName().tip().toString());
        assertEquals(MultiPolygon.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
        desc = descs.get(index++);
        assertEquals("geometrycollection", desc.getName().tip().toString());
        assertEquals(GeometryCollection.class, ((AttributeType)desc).getValueClass());
        assertTrue(AttributeConvention.isGeometryAttribute(desc));
        assertEquals(CommonCRS.defaultGeographic(), FeatureExt.getCRS(desc));
    }

    @Ignore
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

    @Ignore
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

    @Ignore
    @Test
    public void testFeatureType3Creation() throws DataStoreException, VersioningException{
        reload(false);

        final FeatureType refType = FTYPE_COMPLEX3;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        final GenericName name = store.getNames().iterator().next();
        final FeatureType created = store.getFeatureType(name.toString());
        lazyCompare(refType, created);
    }

    private void lazyCompare(final FeatureType refType, final FeatureType candidate){
        final GenericName name = refType.getName();
        assertEquals(refType.getName().tip().toString(), name.tip().toString());

        final FeatureType ct = (FeatureType) refType;
        final FeatureType cct = (FeatureType) candidate;
        assertEquals(ct.getProperties(true).size()+1, cct.getProperties(true).size());// +1 for generated fid field

        for(PropertyType desc : ct.getProperties(true)){
            final PropertyType cdesc = cct.getProperty(desc.getName().tip().toString());
            assertEquals(desc, cdesc);
        }

    }

    @Ignore
    @Test
    public void testSimpleInsert() throws DataStoreException, VersioningException{
        reload(true);

        store.createFeatureType(FTYPE_SIMPLE);
        FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        Feature feature = resType.newInstance();
        feature.setPropertyValue("boolean",true);
        feature.setPropertyValue("byte",45);
        feature.setPropertyValue("short",963);
        feature.setPropertyValue("integer",123456);
        feature.setPropertyValue("long",456789l);
        feature.setPropertyValue("float",7.3f);
        feature.setPropertyValue("double",14.5);
        feature.setPropertyValue("string","a string");

        store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        Session session = store.createSession(false);
        FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertEquals(true, resFeature.getPropertyValue("boolean"));
            assertEquals(45, resFeature.getPropertyValue("byte"));
            assertEquals(963, resFeature.getPropertyValue("short"));
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
        feature.setPropertyValue("boolean",true);
        feature.setPropertyValue("byte",45);
        feature.setPropertyValue("short",963);
        feature.setPropertyValue("integer",123456);
        feature.setPropertyValue("long",456789l);
        feature.setPropertyValue("float",Float.NaN);
        feature.setPropertyValue("double",Double.NaN);
        feature.setPropertyValue("string","a string");

        store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        session = store.createSession(false);
        col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            assertEquals(true, resFeature.getPropertyValue("boolean"));
            assertEquals(45, resFeature.getPropertyValue("byte"));
            assertEquals(963, resFeature.getPropertyValue("short"));
            assertEquals(123456, resFeature.getPropertyValue("integer"));
            assertEquals(456789l, resFeature.getPropertyValue("long"));
            assertEquals(Float.NaN, resFeature.getPropertyValue("float"));
            assertEquals(Double.NaN, resFeature.getPropertyValue("double"));
            assertEquals("a string", resFeature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

    }

    @Ignore
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
        feature.setPropertyValue("geometry",point);
        feature.setPropertyValue("point",point);
        feature.setPropertyValue("multipoint",mp);
        feature.setPropertyValue("linestring",ls);
        feature.setPropertyValue("multilinestring",mls);
        feature.setPropertyValue("polygon",polygon);
        feature.setPropertyValue("multipolygon",mpolygon);
        feature.setPropertyValue("geometrycollection",gc);

        store.addFeatures(resType.getName().toString(), Collections.singleton(feature));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        //Postgis allow NULL in arrays, so returned array are not primitive types
        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);
            Geometry geom;
            geom = (Geometry)resFeature.getProperty("geometry").getValue();
            assertEquals(point,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("point").getValue();
            assertEquals(point,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("multipoint").getValue();
            assertEquals(mp,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("linestring").getValue();
            assertEquals(ls,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("multilinestring").getValue();
            assertEquals(mls,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("polygon").getValue();
            assertEquals(polygon,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("multipolygon").getValue();
            assertEquals(mpolygon,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
            geom = (Geometry)resFeature.getProperty("geometrycollection").getValue();
            assertEquals(gc,geom);
            assertEquals(CommonCRS.defaultGeographic(), JTS.findCoordinateReferenceSystem(geom));
        }finally{
            ite.close();
        }
    }

    /**
     * 2 level depths feature test.
     */
    @Ignore
    @Test
    public void testComplexInsert() throws DataStoreException, VersioningException{
        reload(false);
        final GeometryFactory gf = new GeometryFactory();

        store.createFeatureType(FTYPE_COMPLEX);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature voyage = resType.newInstance();
        voyage.setPropertyValue("identifier",120);

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

        store.addFeatures(resType.getName().toString(), Collections.singleton(voyage));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(resType.getName().toString()));
        assertEquals(1, col.size());

        final FeatureIterator ite = col.iterator();
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);

            assertEquals(120l, resFeature.getProperty("identifier").getValue());

            final Feature resDriver = (Feature) resFeature.getProperty("driver");
            assertEquals("jean-michel", resDriver.getProperty("name").getValue());
            assertEquals("BHF:123456", resDriver.getProperty("code").getValue());

            final Collection<Feature> stops = (Collection<Feature>) resFeature.getPropertyValue("stops");
            assertEquals(3, stops.size());
            final boolean[] found = new boolean[3];
            for(Feature stop : stops){
                final Timestamp time = (Timestamp) stop.getProperty("time").getValue();
                final Point location = (Point) stop.getProperty("location").getValue();
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
    @Ignore
    @Test
    public void testComplex2Insert() throws DataStoreException, VersioningException{
        reload(false);

        store.createFeatureType(FTYPE_COMPLEX2);
        final FeatureType soundingType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature sounding = soundingType.newInstance();
        sounding.setPropertyValue("identifier",120);

        final Feature record1 = FTYPE_RECORD.newInstance();
        record1.setPropertyValue("time",new Date(5000000));
        final Feature data11 = FTYPE_DATA.newInstance();
        data11.setPropertyValue("values",new Float[]{1f,2f,3f});
        final Feature data12 = FTYPE_DATA.newInstance();
        data12.setPropertyValue("values",new Float[]{4f,5f,6f});
        record1.setPropertyValue("datas", Arrays.asList(data11,data12));

        final Feature record2 = FTYPE_RECORD.newInstance();
        record2.setPropertyValue("time",new Date(6000000));
        final Feature data21 = FTYPE_DATA.newInstance();
        data21.setPropertyValue("values",new Float[]{7f,8f,9f});
        record2.setPropertyValue("datas", Arrays.asList(data21));

        sounding.setPropertyValue("records", Arrays.asList(record1,record2));


        store.addFeatures(soundingType.getName().toString(), Collections.singleton(sounding));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(soundingType.getName().toString()));
        assertEquals(1, col.size());

        final FeatureIterator ite = store.getFeatureReader(QueryBuilder.all(soundingType.getName().toString()));
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);

            assertEquals(120l, resFeature.getProperty("identifier").getValue());


            final Collection<Feature> records = (Collection<Feature>) resFeature.getPropertyValue("records");
            assertEquals(2, records.size());
            final boolean[] found = new boolean[2];
            for(Feature record : records){
                final Timestamp time = (Timestamp) record.getProperty("time").getValue();
                if(time.getTime() == 5000000){
                    found[0] = true;

                    final Collection<Feature> datas = (Collection<Feature>) record.getPropertyValue("datas");
                    assertEquals(2, datas.size());
                    final boolean[] dfound = new boolean[2];
                    for(Feature data : datas){
                        final Float[] values = (Float[]) data.getProperty("values").getValue();
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
                        final Float[] values = (Float[]) data.getProperty("values").getValue();
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
     */
    @Ignore
    @Test
    public void testComplex3Insert() throws DataStoreException, VersioningException{
        reload(false);

        store.createFeatureType(FTYPE_COMPLEX3);
        final FeatureType recordType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature record = recordType.newInstance();
        record.setPropertyValue("identifier",120);

        final Feature data1 = FTYPE_SDATA.newInstance();
        data1.setPropertyValue("value",5f);
        final Feature data2 = FTYPE_SDATA.newInstance();
        data2.setPropertyValue("value",10f);
        final Feature data3 = FTYPE_SDATA.newInstance();
        data3.setPropertyValue("value",15f);

        record.setPropertyValue("datas", Arrays.asList(data1,data2,data3));

        store.addFeatures(recordType.getName().toString(), Collections.singleton(record));

        final Session session = store.createSession(false);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(recordType.getName().toString()));
        assertEquals(1, col.size());

        final FeatureIterator ite = store.getFeatureReader(QueryBuilder.all(recordType.getName().toString()));
        try{
            final Feature resFeature = ite.next();
            assertNotNull(resFeature);

            assertEquals(120l, resFeature.getProperty("identifier").getValue());

            assertNotNull(resFeature.getProperty("data1"));
            assertNotNull(resFeature.getProperty("data2"));
            assertNotNull(resFeature.getProperty("data3"));
            assertEquals(5f, ((Feature)resFeature.getProperty("data1")).getProperty("value").getValue());
            assertEquals(10f, ((Feature)resFeature.getProperty("data2")).getProperty("value").getValue());
            assertEquals(15f, ((Feature)resFeature.getProperty("data3")).getProperty("value").getValue());
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
    @Ignore
    @Test
    public void testHandMadeSQLQuery() throws Exception{
        reload(false);
        final GeometryFactory gf = new GeometryFactory();

        store.createFeatureType(FTYPE_COMPLEX);
        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());

        final Feature voyage = resType.newInstance();
        voyage.setPropertyValue("identifier",120);

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

        store.addFeatures(resType.getName().toString(), Collections.singleton(voyage));

        final Query query = QueryBuilder.language(JDBCFeatureStore.CUSTOM_SQL, "SELECT * FROM \"Stop\"", "s1");
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

                final Optional<Geometry> geom = FeatureExt.getDefaultGeometryValue(feature)
                        .filter(Geometry.class::isInstance)
                        .map(Geometry.class::cast);
                Assert.assertTrue(geom.isPresent());

                assertNotNull(JTS.findCoordinateReferenceSystem(geom.get()));
            }
        }finally{
            ite.close();
        }

        for(boolean b : found) assertTrue(b);

    }


}
