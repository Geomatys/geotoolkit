/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.storage.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.feature.FeatureComparator;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.test.TestCase;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.geojson.GeoJSONProvider;
import org.geotoolkit.storage.geojson.GeoJSONStore;
import org.geotoolkit.storage.geojson.GeoJSONStreamWriter;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public class GeoJSONWriteTest extends TestCase {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final WKTReader WKT_READER = new WKTReader();
    private static final Properties PROPERTIES = new Properties();

    @BeforeClass
    public static void init() throws IOException {
        PROPERTIES.load(GeoJSONWriteTest.class.getResourceAsStream("/org/apache/sis/internal/storage/geojson/geometries.properties"));
    }

    @Test
    public void writeSimpleFTTest() throws Exception {

        final Path file = Files.createTempFile("point", ".json");

        final WritableFeatureSet store = new GeoJSONStore(new GeoJSONProvider(), file, 7);
        assertNotNull(store);
        final String typeName = file.getFileName().toString().replace(".json", "");


        //test creating an unvalid feature type
        final FeatureType unvalidFeatureType = buildGeometryFeatureType("test", Point.class);
        try {
            store.updateType(unvalidFeatureType);
            fail();
        } catch (DataStoreException ex) {
            //normal exception
        }


        //test writing and reading a feature
        final FeatureType validFeatureType = buildGeometryFeatureType(typeName, Point.class);
        store.updateType(validFeatureType);
        assertNotNull(store.getType());
        assertTrue(Files.exists(file));

        final Point expectedPoint = GF.createPoint(new Coordinate(-105.01621, 39.57422));
        final Feature feature = store.getType().newInstance();
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), expectedPoint);
        feature.setPropertyValue("type","simple");
        store.add(Arrays.asList(feature).iterator());

        assertTrue(Files.exists(file));


        try (Stream<Feature> stream = store.features(false)) {
            final Iterator<Feature> reader = stream.iterator();
            assertTrue(reader.hasNext());
            final Feature f = reader.next();
            assertEquals("simple", f.getPropertyValue("type"));
            assertEquals(expectedPoint, f.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString()));
        }

        Files.deleteIfExists(file);
    }

    @Test
    public void writeAbstractGeometryTest() throws Exception {

        Path file = Files.createTempFile("geoms", ".json");

        WritableFeatureSet store = new GeoJSONStore(new GeoJSONProvider(), file, 7);
        assertNotNull(store);

        String typeName = file.getFileName().toString().replace(".json", "");
        FeatureType validFeatureType = buildGeometryFeatureType(typeName, Geometry.class);

        store.updateType(validFeatureType);
        assertNotNull(store.getType());
        assertTrue(Files.exists(file));

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));
        MultiPoint mpt = (MultiPoint)WKT_READER.read(PROPERTIES.getProperty("multipoint"));
        LineString line = (LineString)WKT_READER.read(PROPERTIES.getProperty("linestring"));
        MultiLineString mline = (MultiLineString)WKT_READER.read(PROPERTIES.getProperty("multilinestring"));
        Polygon poly = (Polygon)WKT_READER.read(PROPERTIES.getProperty("polygon"));
        MultiPolygon mpoly = (MultiPolygon)WKT_READER.read(PROPERTIES.getProperty("multipolygon"));
        GeometryCollection coll = (GeometryCollection)WKT_READER.read(PROPERTIES.getProperty("geometrycollection"));

        Feature feature = store.getType().newInstance();
        feature.setPropertyValue("type","Point");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
        store.add(Arrays.asList(feature).iterator());

        feature = store.getType().newInstance();
        feature.setPropertyValue("type","MultiPoint");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), mpt);
        store.add(Arrays.asList(feature).iterator());

        feature = store.getType().newInstance();
        feature.setPropertyValue("type","LineString");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), line);
        store.add(Arrays.asList(feature).iterator());

        feature = store.getType().newInstance();
        feature.setPropertyValue("type","MultiLineString");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), mline);
        store.add(Arrays.asList(feature).iterator());

        feature = store.getType().newInstance();
        feature.setPropertyValue("type","Polygon");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), poly);
        store.add(Arrays.asList(feature).iterator());

        feature = store.getType().newInstance();
        feature.setPropertyValue("type","MultiPolygon");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), mpoly);
        store.add(Arrays.asList(feature).iterator());

        feature = store.getType().newInstance();
        feature.setPropertyValue("type","GeometryCollection");
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), coll);
        store.add(Arrays.asList(feature).iterator());

        assertTrue(Files.exists(file));

        assertEquals(7, store.features(false).count());

        try (Stream<Feature> stream = store.features(false)) {
            Iterator<Feature> ite = stream.iterator();
            while (ite.hasNext()) {
                Feature f = ite.next();
                //System.out.println(f);
                Geometry geom = (Geometry)f.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());

                if (geom instanceof Point) {
                    assertTrue(pt.equalsExact(geom, 0.0000001));
                } else if (geom instanceof MultiPoint) {
                    assertTrue(mpt.equalsExact(geom, 0.0000001));
                } else if (geom instanceof LineString) {
                    assertTrue(line.equalsExact(geom, 0.0000001));
                } else if (geom instanceof MultiLineString) {
                    assertTrue(mline.equalsExact(geom, 0.0000001));
                } else if (geom instanceof Polygon) {
                    assertTrue(poly.equalsExact(geom, 0.0000001));
                } else if (geom instanceof MultiPolygon) {
                    assertTrue(mpoly.equalsExact(geom, 0.0000001));
                } else if (geom instanceof GeometryCollection) {
                    assertTrue(coll.equalsExact(geom, 0.0000001));
                }
            }
        }

        Files.deleteIfExists(file);
    }

    @Test
    public void writeComplexFeaturesTest() throws Exception {
        Path file = Files.createTempFile("complex", ".json");

        WritableFeatureSet store = new GeoJSONStore(new GeoJSONProvider(), file, 7);
        assertNotNull(store);

        String typeName = file.getFileName().toString().replace(".json", "");

        FeatureType complexFT = buildComplexFeatureType(typeName);

        store.updateType(complexFT);
        assertNotNull(store.getType());
        assertTrue(Files.exists(file));

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));
        Feature expected = null;
        Feature feature = store.getType().newInstance();
        feature.setPropertyValue("longProp",100l);
        feature.setPropertyValue("stringProp","Some String");
        feature.setPropertyValue("integerProp",15);
        feature.setPropertyValue("booleanProp",true);

        final FeatureType level1Type = ((FeatureAssociationRole)feature.getType().getProperty("level1")).getValueType();

        final Feature level11 = level1Type.newInstance();
        level11.setPropertyValue("longProp2",66446l);

        final FeatureType level2Type = ((FeatureAssociationRole)level11.getType().getProperty("level2")).getValueType();

        final Feature level211 = level2Type.newInstance();
        level211.setPropertyValue("level2prop","text");
        final Feature level212 = level2Type.newInstance();
        level212.setPropertyValue("level2prop","text2");
        final Feature level213 = level2Type.newInstance();
        level213.setPropertyValue("level2prop","text3");

        level11.setPropertyValue("level2", Arrays.asList(level211,level212,level213));


        Feature level12 = level1Type.newInstance();
        level12.setPropertyValue("longProp2",4444444l);

        final Feature level221 = level2Type.newInstance();
        level221.setPropertyValue("level2prop","fish");
        final Feature level222 = level2Type.newInstance();
        level222.setPropertyValue("level2prop","cat");
        final Feature level223 = level2Type.newInstance();
        level223.setPropertyValue("level2prop","dog");

        level12.setPropertyValue("level2", Arrays.asList(level221,level222,level223));

        feature.setPropertyValue("level1",Arrays.asList(level11,level12));

        feature.setPropertyValue("geometry", pt);
        expected = copy(feature);
        store.add(Arrays.asList(feature).iterator());


        assertTrue(Files.exists(file));

        assertEquals(1, store.features(false).count());

        try (Stream<Feature> stream = store.features(false)) {
            Iterator<Feature> ite = stream.iterator();
            while (ite.hasNext()) {
                Feature candidate = ite.next();
                FeatureComparator comparator = new FeatureComparator(expected, candidate);
                comparator.ignoredProperties.add(AttributeConvention.IDENTIFIER);
                comparator.compare();
            }
        }
        Files.deleteIfExists(file);
    }

    @Test
    public void writeStreamTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FeatureType validFeatureType = buildGeometryFeatureType("simpleFT", Point.class);

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));

        try (GeoJSONStreamWriter fw = new GeoJSONStreamWriter(baos, validFeatureType, 4)) {
            Feature feature = fw.next();
            feature.setPropertyValue("type","feat1");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","feat2");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
            fw.write();

        }

        String outputJSON = baos.toString("UTF-8");
        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\n" +
                "\"type\":\"FeatureCollection\"\n" +
                ",\"features\":[\n" +
                "{\"type\":\"Feature\",\"id\":0,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"type\":\"feat1\"}}\n" +
                ",{\"type\":\"Feature\",\"id\":1,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"type\":\"feat2\"}}\n" +
                "]}";

        assertEquals(expected, outputJSON);
    }

    @Test
    public void writeStreamSingleFeatureTest() throws Exception {
        FeatureType validFeatureType = buildGeometryFeatureType("simpleFT", Point.class);

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));

        final String outputJSON;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Feature feature = validFeatureType.newInstance();
            feature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), 0);
            feature.setPropertyValue("type","feat1");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
            GeoJSONStreamWriter.writeSingleFeature(baos, feature, JsonEncoding.UTF8, 4, false);

            outputJSON = baos.toString("UTF-8");
        }

        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\"type\":\"Feature\",\"id\":0," +
                "\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]}," +
                "\"properties\":{\"type\":\"feat1\"}}";
        assertEquals(expected, outputJSON);
    }

    @Test
    public void writeStreamSingleGeometryTest() throws Exception {
        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));

        final String outputJSON;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            GeoJSONStreamWriter.writeSingleGeometry(baos, pt, JsonEncoding.UTF8, 4, false);

            outputJSON = baos.toString("UTF-8");
        }

        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]}";
        assertEquals(expected, outputJSON);
    }

    @Test
    public void writePropertyArrayTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FeatureType validFeatureType = buildPropertyArrayFeatureType("arrayFT", Point.class);

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));

        double[][] array1 = new double[5][5];
        double[][] array2 = new double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                array1[i][j] = i+j;
                array2[i][j] = i-j;
            }
        }

        try (GeoJSONStreamWriter fw = new GeoJSONStreamWriter(baos, validFeatureType, 4)) {
            Feature feature = fw.next();
            feature.setPropertyValue("array",array1);
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("array",array2);
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
            fw.write();

        }

        String outputJSON = baos.toString("UTF-8");
        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\n" +
                "\"type\":\"FeatureCollection\"\n" +
                ",\"features\":[\n" +
                "{\"type\":\"Feature\",\"id\":\"0\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"array\":[[0.0,1.0,2.0,3.0,4.0],[1.0,2.0,3.0,4.0,5.0],[2.0,3.0,4.0,5.0,6.0],[3.0,4.0,5.0,6.0,7.0],[4.0,5.0,6.0,7.0,8.0]]}}\n" +
                ",{\"type\":\"Feature\",\"id\":\"1\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"array\":[[0.0,-1.0,-2.0,-3.0,-4.0],[1.0,0.0,-1.0,-2.0,-3.0],[2.0,1.0,0.0,-1.0,-2.0],[3.0,2.0,1.0,0.0,-1.0],[4.0,3.0,2.0,1.0,0.0]]}}\n" +
                "]}";
        assertEquals(expected, outputJSON);
    }

    @Test
    public void writeSimpleFTCborTest() throws Exception {

        final Path file = Files.createTempFile("point", ".cbor");

        final WritableFeatureSet store = new GeoJSONStore(new GeoJSONProvider(), file, 7);
        assertNotNull(store);
        final String typeName = file.getFileName().toString().replace(".cbor", "");


        //test creating an unvalid feature type
        final FeatureType unvalidFeatureType = buildGeometryFeatureType("test", Point.class);
        try {
            store.updateType(unvalidFeatureType);
            fail();
        } catch (DataStoreException ex) {
            //normal exception
        }


        //test writing and reading a feature
        final FeatureType validFeatureType = buildGeometryFeatureType(typeName, Point.class);
        store.updateType(validFeatureType);
        assertNotNull(store.getType());
        assertTrue(Files.exists(file));

        final Point expectedPoint = GF.createPoint(new Coordinate(-105.01621, 39.57422));
        final Feature feature = store.getType().newInstance();
        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), expectedPoint);
        feature.setPropertyValue("type","simple");
        store.add(Arrays.asList(feature).iterator());

        assertTrue(Files.exists(file));


        try (Stream<Feature> stream = store.features(false)) {
            final Iterator<Feature> reader = stream.iterator();
            assertTrue(reader.hasNext());
            final Feature f = reader.next();
            assertEquals("simple", f.getPropertyValue("type"));
            assertEquals(expectedPoint, f.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString()));
        }

        Files.deleteIfExists(file);
    }

    private FeatureType buildGeometryFeatureType(String name, Class<?> geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(Integer.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(geomClass).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private FeatureType buildPropertyArrayFeatureType(String name, Class<?> geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(double[][].class).setName("array");
        ftb.addAttribute(geomClass).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    /**
     *  Build 2 level Feature complex
     */
    private FeatureType buildComplexFeatureType(String name) {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("level2");
        ftb.addAttribute(String.class).setName("level2prop");
        final FeatureType level2 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("level1");
        ftb.addAttribute(Long.class).setName("longProp2");
        ftb.addAssociation(level2).setName("level2").setMinimumOccurs(1).setMaximumOccurs(5);
        final FeatureType level1 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(Long.class).setName("longProp");
        ftb.addAttribute(String.class).setName("stringProp");
        ftb.addAttribute(Integer.class).setName("integerProp");
        ftb.addAttribute(Boolean.class).setName("booleanProp");
        ftb.addAssociation(level1).setName("level1").setMinimumOccurs(1).setMaximumOccurs(3);
        ftb.addAttribute(Point.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.setDescription(new SimpleInternationalString("Description"));
        return ftb.build();
    }

    /**
     * Create a copy of given feature.
     * This is not a deep copy, only the feature and associated feature are copied,
     * values are not copied.
     */
    public static Feature copy(Feature feature){
        return copy(feature, false);
    }

    /**
     * @param deep true for a deep copy
     */
    private static Feature copy(Feature feature, boolean deep){
        final FeatureType type = feature.getType();

        final Feature cp = type.newInstance();

        final Collection<? extends PropertyType> props = type.getProperties(true);
        for (PropertyType pt : props) {
            if (pt instanceof AttributeType ){
                final String name = pt.getName().toString();
                final Object val = feature.getPropertyValue(name);
                if(val!=null){
                    cp.setPropertyValue(name, deep ? deepCopy(val) : val);
                }
            } else if(pt instanceof FeatureAssociationRole) {
                final String name = pt.getName().toString();
                final Object val = feature.getPropertyValue(name);
                if (deep) {
                    if(val!=null){
                        cp.setPropertyValue(name, deepCopy(val));
                    }
                } else {
                    if(val instanceof Collection){
                        final Collection col = (Collection) val;
                        final Collection cpCol = new ArrayList(col.size());
                        for(Iterator ite=col.iterator();ite.hasNext();){
                            cpCol.add(copy((Feature)ite.next()));
                        }
                        cp.setPropertyValue(name, cpCol);
                    }else if(val!=null){
                        cp.setPropertyValue(name, copy((Feature)val));
                    }
                }

            }
        }
        return cp;
    }

    /**
     * Make a deep copy of given Feature.
     *
     * @param feature Feature to copy
     * @return Deep copy of the feature
     */
    public static Feature deepCopy(Feature feature){
        return copy(feature, true);
    }

    /**
     * Make a copy of given object.
     * Multiplace cases are tested to make a deep copy.
     *
     * @param candidate
     * @return copied object
     */
    public static Object deepCopy(final Object candidate) {
        if(candidate==null) return null;

        if(candidate instanceof String ||
           candidate instanceof Number ||
           candidate instanceof URL ||
           candidate instanceof URI ||
           candidate.getClass().isPrimitive() ||
           candidate instanceof Character ||
           candidate instanceof GridCoverage){
            //we consider those immutable
            return candidate;
        }else if(candidate instanceof Feature){
            return deepCopy((Feature)candidate);
        }else if(candidate instanceof Geometry){
            return ((Geometry)candidate).copy();
        }else if(candidate instanceof Date){
            return ((Date)candidate).clone();
        }else if(candidate instanceof Date){
            return ((Date)candidate).clone();
        }else if(candidate instanceof Object[]){
            final Object[] array = (Object[])candidate;
            final Object[] copy = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                copy[i] = deepCopy(array[i]);
            }
            return copy;
        }else if(candidate instanceof List){
            final List list = (List)candidate;
            final int size = list.size();
            final List cp = new ArrayList(size);
            for(int i=0;i<size;i++){
                cp.add(deepCopy(list.get(i)));
            }
            return cp;
        }else if (candidate instanceof Map) {
            final Map map = (Map) candidate;
            final Map cp = new HashMap(map.size());
            for(final Iterator<Map.Entry> ite=map.entrySet().iterator(); ite.hasNext();) {
                final Map.Entry entry = ite.next();
                cp.put(entry.getKey(), deepCopy(entry.getValue()));
            }
            return Collections.unmodifiableMap(cp);
        }

        //array type
        final Class clazz = candidate.getClass();
        if(clazz.isArray()){
            final Class compClazz = clazz.getComponentType();
            final int length = Array.getLength(candidate);
            final Object cp = Array.newInstance(compClazz, length);

            if(compClazz.isPrimitive()){
                System.arraycopy(candidate, 0, cp, 0, length);
            }else{
                for(int i=0;i<length; i++){
                    Array.set(cp, i, deepCopy(Array.get(candidate, i)));
                }
            }
            return cp;
        }

        //could not copy
        return candidate;
    }

}