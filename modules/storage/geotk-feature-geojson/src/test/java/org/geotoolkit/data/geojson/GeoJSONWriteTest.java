package org.geotoolkit.data.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.*;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.apache.sis.referencing.CommonCRS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.data.AbstractFileFeatureStoreFactory.PATH;
import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.PARAMETERS_DESCRIPTOR;
import org.geotoolkit.storage.DataStores;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;

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

        final Path pointFile = Files.createTempFile("point", ".json");

        final ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toUri());

        final FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);
        assertEquals(0, store.getNames().size());
        final String typeName = pointFile.getFileName().toString().replace(".json", "");


        //test creating an unvalid feature type
        final FeatureType unvalidFeatureType = buildGeometryFeatureType("test", Point.class);
        try {
            store.createFeatureType(unvalidFeatureType);
            fail();
        } catch (DataStoreException ex) {
            //normal exception
        }
        assertEquals(0, store.getNames().size());


        //test writing and reading a feature
        final FeatureType validFeatureType = buildGeometryFeatureType(typeName, Point.class);
        store.createFeatureType(validFeatureType);
        assertEquals(1, store.getNames().size());
        assertTrue(Files.exists(pointFile));

        final Point expectedPoint = GF.createPoint(new Coordinate(-105.01621, 39.57422));
        try (FeatureWriter fw = store.getFeatureWriter(QueryBuilder.filtered(validFeatureType.getName().toString(),Filter.EXCLUDE))) {
            final Feature feature = fw.next();
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), expectedPoint);
            feature.setPropertyValue("type","simple");
            fw.write();
        }

        assertTrue(Files.exists(pointFile));

        final FeatureReader reader = store.getFeatureReader(QueryBuilder.all(validFeatureType.getName()));
        assertTrue(reader.hasNext());
        final Feature f = reader.next();
        assertEquals("simple", f.getPropertyValue("type"));
        assertEquals(expectedPoint, f.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString()));
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

        store.createFeatureType(validFeatureType);
        assertEquals(1, store.getNames().size());
        assertTrue(Files.exists(geomsFile));

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));
        MultiPoint mpt = (MultiPoint)WKT_READER.read(PROPERTIES.getProperty("multipoint"));
        LineString line = (LineString)WKT_READER.read(PROPERTIES.getProperty("linestring"));
        MultiLineString mline = (MultiLineString)WKT_READER.read(PROPERTIES.getProperty("multilinestring"));
        Polygon poly = (Polygon)WKT_READER.read(PROPERTIES.getProperty("polygon"));
        MultiPolygon mpoly = (MultiPolygon)WKT_READER.read(PROPERTIES.getProperty("multipolygon"));
        GeometryCollection coll = (GeometryCollection)WKT_READER.read(PROPERTIES.getProperty("geometrycollection"));

        try (FeatureWriter fw = store.getFeatureWriter(QueryBuilder.filtered(validFeatureType.getName().toString(),Filter.EXCLUDE))) {
            Feature feature = fw.next();
            feature.setPropertyValue("type","Point");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","MultiPoint");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), mpt);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","LineString");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), line);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","MultiLineString");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), mline);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","Polygon");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), poly);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","MultiPolygon");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), mpoly);
            fw.write();

            feature = fw.next();
            feature.setPropertyValue("type","GeometryCollection");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), coll);
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

        store.createFeatureType(complexFT);
        assertEquals(1, store.getNames().size());
        assertTrue(Files.exists(complexFile));

        Point pt = (Point)WKT_READER.read(PROPERTIES.getProperty("point"));
        Feature expected = null;
        try (FeatureWriter fw = store.getFeatureWriter(QueryBuilder.filtered(complexFT.getName().toString(),Filter.EXCLUDE))) {
            Feature feature = fw.next();
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
            expected = FeatureExt.copy(feature);
            fw.write();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        assertTrue(Files.exists(complexFile));

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(complexFT.getName()));

        assertEquals(1, fcoll.size());

        try (FeatureIterator ite = fcoll.iterator()) {
            while (ite.hasNext()) {
                Feature candidate = ite.next();
                assertEquals(expected, candidate);
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
            Feature feature = validFeatureType.newInstance();
            feature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-0");
            feature.setPropertyValue("type","feat1");
            feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), pt);
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
                "{\"type\":\"Feature\",\"id\":\"id-0\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"array\":[[0.0,1.0,2.0,3.0,4.0],[1.0,2.0,3.0,4.0,5.0],[2.0,3.0,4.0,5.0,6.0],[3.0,4.0,5.0,6.0,7.0],[4.0,5.0,6.0,7.0,8.0]]}}\n" +
                ",{\"type\":\"Feature\",\"id\":\"id-1\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-105.0162,39.5742]},\"properties\":{\"array\":[[0.0,-1.0,-2.0,-3.0,-4.0],[1.0,0.0,-1.0,-2.0,-3.0],[2.0,1.0,0.0,-1.0,-2.0],[3.0,2.0,1.0,0.0,-1.0],[4.0,3.0,2.0,1.0,0.0]]}}\n" +
                "]}";
        assertEquals(expected, outputJSON);
    }

    private FeatureType buildGeometryFeatureType(String name, Class geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(geomClass).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private FeatureType buildPropertyArrayFeatureType(String name, Class geomClass) {
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

}
