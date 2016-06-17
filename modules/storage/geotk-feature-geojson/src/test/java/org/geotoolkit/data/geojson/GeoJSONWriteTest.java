package org.geotoolkit.data.geojson;

import org.opengis.util.GenericName;
import com.fasterxml.jackson.core.JsonEncoding;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.*;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.apache.sis.referencing.CommonCRS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.*;
import org.opengis.parameter.ParameterValueGroup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.geotoolkit.data.AbstractFileFeatureStoreFactory.PATH;
import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.PARAMETERS_DESCRIPTOR;
import org.geotoolkit.storage.DataStores;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONWriteTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final WKTReader WKT_READER = new WKTReader();
    private static final Properties PROPERTIES = new Properties();

    @BeforeClass
    public static void init() {
        try {
            PROPERTIES.load(GeoJSONWriteTest.class.getResourceAsStream("/org/geotoolkit/geojson/geometries.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeSimpleFTTest() throws Exception {

        Path pointFile = Files.createTempFile("point", ".json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toUri());

        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);
        assertEquals(0, store.getNames().size());

        String typeName = pointFile.getFileName().toString().replace(".json", "");

        FeatureType validFeatureType = buildGeometryFeatureType(typeName, Point.class);
        FeatureType unvalidFeatureType = buildGeometryFeatureType("test", Point.class);

        try {
            store.createFeatureType(unvalidFeatureType.getName(), unvalidFeatureType);
            fail();
        } catch (DataStoreException ex) {
            //normal exception
        }

        assertEquals(0, store.getNames().size());

        store.createFeatureType(validFeatureType.getName(), validFeatureType);
        assertEquals(1, store.getNames().size());
        assertTrue(Files.exists(pointFile));

        Point expectedPoint =GF.createPoint(new Coordinate(-105.01621, 39.57422));
        try (FeatureWriter fw = store.getFeatureWriterAppend(validFeatureType.getName())) {
            Feature feature = fw.next();
            feature.getDefaultGeometryProperty().setValue(expectedPoint);
            feature.getProperty("type").setValue("simple");
            fw.write();
        }

        assertTrue(Files.exists(pointFile));

        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(validFeatureType.getName()));
        assertTrue(reader.hasNext());
        Feature f = reader.next();
        assertEquals("simple", f.getProperty("type").getValue());
        GeometryAttribute geom = f.getDefaultGeometryProperty();
        assertNotNull(geom);
        assertEquals(expectedPoint, geom.getValue());
        reader.close();

        Files.deleteIfExists(pointFile);
    }

    @Test
    public void writeAbstractGeometryTest() throws Exception {

        Path geomsFile = Files.createTempFile("geoms", ".json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(geomsFile.toUri());

        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);
        assertEquals(0, store.getNames().size());

        String typeName = geomsFile.getFileName().toString().replace(".json", "");
        FeatureType validFeatureType = buildGeometryFeatureType(typeName, Geometry.class);
        assertEquals(0, store.getNames().size());

        store.createFeatureType(validFeatureType.getName(), validFeatureType);
        assertEquals(1, store.getNames().size());
        assertTrue(Files.exists(geomsFile));

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));
        MultiPoint mpt = (MultiPoint)WKT_READER.read(PROPERTIES.getProperty("multipoint"));
        LineString line = (LineString)WKT_READER.read(PROPERTIES.getProperty("linestring"));
        MultiLineString mline = (MultiLineString)WKT_READER.read(PROPERTIES.getProperty("multilinestring"));
        Polygon poly = (Polygon)WKT_READER.read(PROPERTIES.getProperty("polygon"));
        MultiPolygon mpoly = (MultiPolygon)WKT_READER.read(PROPERTIES.getProperty("multipolygon"));
        GeometryCollection coll = (GeometryCollection)WKT_READER.read(PROPERTIES.getProperty("geometrycollection"));

        try (FeatureWriter fw = store.getFeatureWriterAppend(validFeatureType.getName())) {
            Feature feature = fw.next();
            feature.getProperty("type").setValue("Point");
            feature.getDefaultGeometryProperty().setValue(pt);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("MultiPoint");
            feature.getDefaultGeometryProperty().setValue(mpt);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("LineString");
            feature.getDefaultGeometryProperty().setValue(line);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("MultiLineString");
            feature.getDefaultGeometryProperty().setValue(mline);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("Polygon");
            feature.getDefaultGeometryProperty().setValue(poly);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("MultiPolygon");
            feature.getDefaultGeometryProperty().setValue(mpoly);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("GeometryCollection");
            feature.getDefaultGeometryProperty().setValue(coll);
            fw.write();
        }
        assertTrue(Files.exists(geomsFile));

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(validFeatureType.getName()));

        assertEquals(7, fcoll.size());

        try (FeatureIterator ite = fcoll.iterator()) {
            while (ite.hasNext()) {
                Feature f = ite.next();
                //System.out.println(f);
                GeometryAttribute geometryAttribute = f.getDefaultGeometryProperty();
                Geometry geom = (Geometry) geometryAttribute.getValue();

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

        Files.deleteIfExists(geomsFile);
    }

    @Test
    public void writeComplexFeaturesTest() throws Exception {
        Path complexFile = Files.createTempFile("complex", ".json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(complexFile.toUri());

        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);
        assertEquals(0, store.getNames().size());

        String typeName = complexFile.getFileName().toString().replace(".json", "");

        FeatureType complexFT = buildComplexFeatureType(typeName);
        assertEquals(0, store.getNames().size());

        store.createFeatureType(complexFT.getName(), complexFT);
        assertEquals(1, store.getNames().size());
        assertTrue(Files.exists(complexFile));

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));
        Feature expected = null;
        try (FeatureWriter fw = store.getFeatureWriterAppend(complexFT.getName())) {
            Feature feature = fw.next();
            feature.getProperty("longProp").setValue(100l);
            feature.getProperty("stringProp").setValue("Some String");
            feature.getProperty("integerProp").setValue(15);
            feature.getProperty("booleanProp").setValue(true);

            ComplexType level1Type = (ComplexType) feature.getProperty("level1").getType();
            PropertyDescriptor level1Desc = feature.getProperty("level1").getDescriptor();
            feature.getProperties().remove(feature.getProperty("level1"));

            ComplexAttribute level11 = (ComplexAttribute) FeatureUtilities.defaultProperty(level1Desc);
            level11.getProperty("longProp2").setValue(66446l);

            ComplexType level2Type = (ComplexType) level11.getProperty("level2").getType();
            PropertyDescriptor level2desc = level11.getProperty("level2").getDescriptor();
            level11.getProperties().remove(level11.getProperty("level2"));

            ComplexAttribute level211 = (ComplexAttribute) FeatureUtilities.defaultProperty(level2desc);
            level211.getProperty("level2prop").setValue("text");
            level11.getProperties().add(level211);

            ComplexAttribute level212 = (ComplexAttribute) FeatureUtilities.defaultProperty(level2desc);
            level212.getProperty("level2prop").setValue("text2");
            level11.getProperties().add(level212);

            ComplexAttribute level213 = (ComplexAttribute) FeatureUtilities.defaultProperty(level2desc);
            level213.getProperty("level2prop").setValue("text3");
            level11.getProperties().add(level213);

            feature.getProperties().add(level11);


            ComplexAttribute level12 = (ComplexAttribute) FeatureUtilities.defaultProperty(level1Desc);
            level12.getProperty("longProp2").setValue(4444444l);

            ComplexType level22Type = (ComplexType) level12.getProperty("level2").getType();
            PropertyDescriptor level22desc = level11.getProperty("level2").getDescriptor();
            level12.getProperties().remove(level12.getProperty("level2"));

            ComplexAttribute level221 = (ComplexAttribute) FeatureUtilities.defaultProperty(level22desc);
            level221.getProperty("level2prop").setValue("fish");
            level12.getProperties().add(level221);

            ComplexAttribute level222 = (ComplexAttribute) FeatureUtilities.defaultProperty(level22desc);
            level222.getProperty("level2prop").setValue("cat");
            level12.getProperties().add(level222);

            ComplexAttribute level223 = (ComplexAttribute) FeatureUtilities.defaultProperty(level22desc);
            level223.getProperty("level2prop").setValue("dog");
            level12.getProperties().add(level223);

            feature.getProperties().add(level12);

            feature.getDefaultGeometryProperty().setValue(pt);
            expected = FeatureUtilities.deepCopy(feature);
            fw.write();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertTrue(Files.exists(complexFile));

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(complexFT.getName()));

        assertEquals(1, fcoll.size());

        try (FeatureIterator ite = fcoll.iterator()) {
            while (ite.hasNext()) {
                Feature candidate = ite.next();

                for (PropertyDescriptor propDesc : expected.getType().getDescriptors()) {
                    GenericName propName = propDesc.getName();
                    Collection<Property> expectedProperties = expected.getProperties(propName);
                    Collection<Property> resultProperties = candidate.getProperties(propName);

                    assertEquals(expectedProperties.size(), resultProperties.size());

                    for (Property expProperty : expectedProperties) {
                        boolean propFound = false;
                        for (Property resProperty : resultProperties) {
                            if (resProperty.equals(expProperty)) {
                                propFound = true;
                            }
                        }
                        assertTrue("Property " + propName.tip().toString() + " not found.", propFound);
                    }
                }
            }
        }
        Files.deleteIfExists(complexFile);
    }

    @Test
    public void writeStreamTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FeatureType validFeatureType = buildGeometryFeatureType("simpleFT", Point.class);

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));

        try (FeatureWriter fw = new GeoJSONStreamWriter(baos, validFeatureType, 4)) {
            Feature feature = fw.next();
            feature.getProperty("type").setValue("feat1");
            feature.getDefaultGeometryProperty().setValue(pt);
            fw.write();

            feature = fw.next();
            feature.getProperty("type").setValue("feat2");
            feature.getDefaultGeometryProperty().setValue(pt);
            fw.write();

        }

        String outputJSON = baos.toString("UTF-8");
        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\n" +
                "\"type\":\"FeatureCollection\"\n" +
                ",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:OGC:1.3:CRS84\"}}\n" +
                ",\"features\":[\n" +
                "{\"type\":\"Feature\",\"id\":\"id-0\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"type\":\"feat1\"}}\n" +
                ",{\"type\":\"Feature\",\"id\":\"id-1\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"type\":\"feat2\"}}\n" +
                "]}";

        assertEquals(expected, outputJSON);
    }

    @Test
    public void writeStreamSingleFeatureTest() throws Exception {
        FeatureType validFeatureType = buildGeometryFeatureType("simpleFT", Point.class);

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));

        final String outputJSON;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Feature feature = FeatureUtilities.defaultFeature(validFeatureType, "id-0");
            feature.getProperty("type").setValue("feat1");
            feature.getDefaultGeometryProperty().setValue(pt);
            GeoJSONStreamWriter.writeSingleFeature(baos, feature, JsonEncoding.UTF8, 4, false);

            outputJSON = baos.toString("UTF-8");
        }

        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\"type\":\"Feature\",\"id\":\"id-0\"," +
                "\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]}," +
                "\"properties\":{\"type\":\"feat1\"}}";
        assertEquals(expected, outputJSON);
    }

    @Test
    public void writeStreamSingleGeometryTest() throws Exception {
        FeatureType validFeatureType = buildGeometryFeatureType("simpleFT", Point.class);

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

        try (FeatureWriter fw = new GeoJSONStreamWriter(baos, validFeatureType, 4)) {
            Feature feature = fw.next();
            feature.getProperty("array").setValue(array1);
            feature.getDefaultGeometryProperty().setValue(pt);
            fw.write();

            feature = fw.next();
            feature.getProperty("array").setValue(array2);
            feature.getDefaultGeometryProperty().setValue(pt);
            fw.write();

        }

        String outputJSON = baos.toString("UTF-8");
        assertNotNull(outputJSON);
        assertFalse(outputJSON.isEmpty());

        String expected = "{\n" +
                "\"type\":\"FeatureCollection\"\n" +
                ",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:OGC:1.3:CRS84\"}}\n" +
                ",\"features\":[\n" +
                "{\"type\":\"Feature\",\"id\":\"id-0\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"array\":[[0.0,1.0,2.0,3.0,4.0],[1.0,2.0,3.0,4.0,5.0],[2.0,3.0,4.0,5.0,6.0],[3.0,4.0,5.0,6.0,7.0],[4.0,5.0,6.0,7.0,8.0]]}}\n" +
                ",{\"type\":\"Feature\",\"id\":\"id-1\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"array\":[[0.0,-1.0,-2.0,-3.0,-4.0],[1.0,0.0,-1.0,-2.0,-3.0],[2.0,1.0,0.0,-1.0,-2.0],[3.0,2.0,1.0,0.0,-1.0],[4.0,3.0,2.0,1.0,0.0]]}}\n" +
                "]}";
        assertEquals(expected, outputJSON);
    }

    private FeatureType buildGeometryFeatureType(String name, Class geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.add("type", String.class);
        ftb.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME, geomClass, CommonCRS.WGS84.normalizedGeographic());
        return ftb.buildSimpleFeatureType();
    }

    private FeatureType buildPropertyArrayFeatureType(String name, Class geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.add("array", double[][].class);
        ftb.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME, geomClass, CommonCRS.WGS84.normalizedGeographic());
        return ftb.buildSimpleFeatureType();
    }

    /**
     *  Build 2 level Feature complex
     */
    private FeatureType buildComplexFeatureType(String name) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();

        ftb.setName("level2");
        ftb.add("level2prop", String.class);
        final ComplexType level2 = ftb.buildType();

        ftb.reset();
        ftb.setName("level1");
        ftb.add("longProp2", Long.class);
        AttributeDescriptor level2Desc = adb.create(level2, NamesExt.valueOf("level2"),1,5,false,null);
        ftb.add(level2Desc);
        final ComplexType level1 = ftb.buildType();

        ftb.reset();
        ftb.setName(name);
        ftb.add("longProp", Long.class);
        ftb.add("stringProp", String.class);
        ftb.add("integerProp", Integer.class);
        ftb.add("booleanProp", Boolean.class);

        AttributeDescriptor level1Desc = adb.create(level1, NamesExt.valueOf("level1"),1,3,false,null);
        ftb.add(level1Desc);
        ftb.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME, Point.class, CommonCRS.WGS84.normalizedGeographic());
        ftb.setDescription(new SimpleInternationalString("Description"));
        return ftb.buildFeatureType();
    }

}
