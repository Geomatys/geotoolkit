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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.AttributeConvention;
import org.geotoolkit.test.feature.FeatureComparator;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import org.geotoolkit.storage.geojson.GeoJSONProvider;
import org.geotoolkit.storage.geojson.GeoJSONStore;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public class GeoJSONReadTest {

    @Test
    public void readPointTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/point.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "point"), name);

        testFeatureTypes(buildGeometryFeatureType("point", Point.class), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readMultiPointTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/multipoint.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "multipoint"), name);

        testFeatureTypes(buildGeometryFeatureType("multipoint", MultiPoint.class), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readLineStringTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/linestring.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "linestring"), name);

        testFeatureTypes(buildGeometryFeatureType("linestring", LineString.class), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readMultiLineStringTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/multilinestring.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "multilinestring"), name);

        testFeatureTypes(buildGeometryFeatureType("multilinestring", MultiLineString.class), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readPolygonTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/polygon.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "polygon"), name);

        testFeatureTypes(buildGeometryFeatureType("polygon", Polygon.class), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readMultiPolygonTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/multipolygon.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "multipolygon"), name);

        testFeatureTypes(buildGeometryFeatureType("multipolygon", MultiPolygon.class), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readGeometryCollectionTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/geometrycollection.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "geometrycollection"), name);

        testFeatureTypes(buildGeometryFeatureType("geometrycollection", GeometryCollection.class), ft);

        assertEquals(1l, store.features(false).count());

    }

    @Test
    public void readFeatureTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/feature.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "feature"), name);

        testFeatureTypes(buildSimpleFeatureType("feature"), ft);

        assertEquals(1l, store.features(false).count());
    }

    @Test
    public void readFeatureCollectionTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/featurecollection.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "featurecollection"), name);

        testFeatureTypes(buildFCFeatureType("featurecollection"), ft);

        assertEquals(7l, store.features(false).count());
    }

    /**
     * Test reading of Features with array as properties value
     * @throws DataStoreException
     */
    @Test
    public void readPropertyArrayTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/f_prop_array.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();
        assertEquals(Names.createLocalName(null, null, "f_prop_array"), name);

        testFeatureTypes(buildPropertyArrayFeatureType("f_prop_array", Geometry.class), ft);

        assertEquals(2l, store.features(false).count());

        double[][] array1 = new double[5][5];
        double[][] array2 = new double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                array1[i][j] = i + j;
                array2[i][j] = i - j;
            }
        }

        Iterator<Feature> ite = store.features(false).iterator();
        Feature feat1 = ite.next();
        assertArrayEquals(array1, (double[][]) feat1.getProperty("array").getValue());

        Feature feat2 = ite.next();
        assertArrayEquals(array2, (double[][]) feat2.getProperty("array").getValue());

    }

    /**
     * This test ensure that properties fields with null value doesn't rise NullPointerException
     * @throws DataStoreException
     */
    @Test
    public void readNullPropsTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/sample_with_null_properties.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();

        assertEquals(15l, store.features(false).count());
    }

    /**
     * This test ensure integer types over Integer.MAX_VALUE are converted to Long.
     * @throws DataStoreException
     */
    @Test
    public void readLongTest() throws DataStoreException, URISyntaxException {
        URL file = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/longValue.json");

        WritableFeatureSet store = (WritableFeatureSet) DataStores.open(file);
        assertNotNull(store);

        FeatureType ft = store.getType();
        GenericName name = ft.getName();

        Feature feature = store.features(false).findFirst().get();
        assertEquals(853555090789l, feature.getPropertyValue("size"));
    }

    /**
     * Test GeoJSONParser full and lazy reading on FeatureCollection
     */
    @Test
    public void parserTest() throws URISyntaxException, IOException {
        URL fcFile = GeoJSONReadTest.class.getResource("/org/apache/sis/internal/storage/geojson/featurecollection.json");
        Path fcPath = Paths.get(fcFile.toURI());

        // test with full reading
        GeoJSONObject geoJSONObject = GeoJSONParser.parse(fcPath, false);
        assertTrue(geoJSONObject instanceof GeoJSONFeatureCollection);
        GeoJSONFeatureCollection geojsonFC = (GeoJSONFeatureCollection) geoJSONObject;
        assertFalse(geojsonFC.isLazyMode());
        assertEquals(7, geojsonFC.getFeatures().size());

        for (int i = 0; i < 7; i++) {
            assertTrue(geojsonFC.hasNext());
            assertNotNull(geojsonFC.next());
        }
        assertFalse(geojsonFC.hasNext()); //end of collection


        // test in lazy reading
        geoJSONObject = GeoJSONParser.parse(fcPath, true);
        assertTrue(geoJSONObject instanceof GeoJSONFeatureCollection);
        geojsonFC = (GeoJSONFeatureCollection) geoJSONObject;
        assertTrue(geojsonFC.isLazyMode());
        assertEquals(0, geojsonFC.getFeatures().size()); //lazy don't know number of features

        for (int i = 0; i < 7; i++) {
            assertTrue(geojsonFC.hasNext());
            assertNotNull(geojsonFC.next());
        }
        assertFalse(geojsonFC.hasNext()); //end of collection

    }

    @Test
    public void mixed_arrays() throws Exception {
        try (final InputStream resource = GeoJSONReadTest.class.getResourceAsStream("/org/apache/sis/internal/storage/geojson/mixed_arrays.json")) {
            if (resource == null) throw new IllegalStateException("Cannot find a test resource");
            final GeoJSONObject readValue = GeoJSONParser.parse(resource);
            assertNotNull(readValue);
            assertTrue(readValue instanceof GeoJSONFeature);
            final GeoJSONFeature feature = (GeoJSONFeature) readValue;
            // Ensure no side-effect would break other parts of the feature reading
            final GeoJSONGeometry geom = feature.getGeometry();
            assertTrue("Read feature should contain a point, but we've read: "+geom, geom instanceof GeoJSONGeometry.GeoJSONPoint);

            // Now, we can check our arrays have been well-parsed
            final Map<String, Object> properties = feature.getProperties();
            assertPropertyIs(new double[]{2., 3., 4.}, "numberMix1", properties);
            assertPropertyIs(new double[]{2., 3., 4.}, "numberMix2", properties);
            assertPropertyIs(new int[]{42, 51}, "intArray", properties);
            assertPropertyIs(new long[]{1, 7_000_000_000l}, "longArray", properties);
            assertPropertyIs(new Double[]{2., null, 4.}, "numbersWithNullValues", properties);
            assertPropertyIs(new Object[]{null, null}, "onlyNullValues", properties);
            assertPropertyIs(new Object[0], "emptyArray", properties);
            assertPropertyIs(new Object[]{2.0, "I'm a text", null}, "arbitraryMix", properties);
        }
    }

    private static void assertPropertyIs(final Object expectedValue, final String propertyName, final Map<String, Object> properties) {
        final Object value = properties.get(propertyName);
        if (expectedValue == null) assertNull(value);
        else {
            assertNotNull(value);
            final String msg = "Property %s should contain %s, but we read: %s";
            final Class<?> expectedType = expectedValue.getClass();
            final Class<?> valueClass = value.getClass();
            assertTrue(String.format(msg, propertyName, expectedType, valueClass), expectedType.isAssignableFrom(valueClass));
            if (double[].class.equals(expectedType)) {
                assertArrayEquals((double[])expectedValue, (double[]) value, 1e-2);
            } else if (int[].class.equals(expectedType)) {
                assertArrayEquals((int[])expectedValue, (int[]) value);
            } else if (long[].class.equals(expectedType)) {
                assertArrayEquals((long[])expectedValue, (long[]) value);
            } else if (expectedType.isArray()) {
                assertArrayEquals((Object[]) expectedValue, (Object[]) value);
            } else assertEquals(expectedValue, value);
        }
    }

    /**
     * Same as {@link #readFeatureExtraAttibuteTest3()}, but for an array at the end of the feature.
     */
    @Test
    public void readFeatureExtraAttibuteTest() throws DataStoreException, URISyntaxException {
        try (GeoJSONStore store = fromResource("/org/apache/sis/internal/storage/geojson/extraAttribute.json")) {
            GenericName name = store.getIdentifier().orElseThrow(() -> new AssertionError("An identifier was expected for input file"));
            assertEquals(Names.createLocalName(null, ":", "extraAttribute"), name);

            FeatureType ft = store.getType();
            testFeatureTypes(buildSimpleFeatureType("extraAttribute", Point.class), ft);

            final List<Feature> content;
            try (Stream<Feature> features = store.features(false)) {
                content = features.collect(Collectors.toList());
            }
            assertEquals(1, content.size());
            final Feature first = content.get(0);
            assertNotNull(first);
            assertEquals("Plaza Road Park", first.getPropertyValue("name"));
        }
    }

    /**
     * Same as {@link #readFeatureExtraAttibuteTest3()}, but for an array in the middle of the feature.
     */
    @Test
    public void readFeatureExtraAttibute2Test() throws DataStoreException, URISyntaxException {
        try (GeoJSONStore store = fromResource("/org/apache/sis/internal/storage/geojson/extraAttribute2.json")) {
            GenericName name = store.getIdentifier().orElseThrow(() -> new AssertionError("An identifier was expected for input file"));
            assertEquals(Names.createLocalName(null, ":", "extraAttribute2"), name);

            FeatureType ft = store.getType();
            testFeatureTypes(buildSimpleFeatureType("extraAttribute2", Point.class), ft);

            final List<Feature> content;
            try (Stream<Feature> features = store.features(false)) {
                content = features.collect(Collectors.toList());
            }
            assertEquals(1, content.size());
            final Feature first = content.get(0);
            assertNotNull(first);
            assertEquals("Plaza Road Park", first.getPropertyValue("name"));
        }
    }

    /**
     * When GeoJSON feature contains json attributes not defined in the standard, ensure we ignore them by default, and
     * proceed to the proper reading of the feature.
     *
     * This method ensure that any additional json object is ignored.
     */
    @Test
    public void readFeatureExtraAttibuteTest3() throws Exception {
        try (GeoJSONStore store = fromResource("/org/apache/sis/internal/storage/geojson/extraAttribute3.json")) {
            GenericName name = store.getIdentifier().orElseThrow(() -> new AssertionError("An identifier was expected for input file"));
            assertEquals(Names.createLocalName(null, ":", "extraAttribute3"), name);

            FeatureType ft = store.getType();
            testFeatureTypes(buildSimpleFeatureType("extraAttribute3", Point.class), ft);

            final List<Feature> content;
            try (Stream<Feature> features = store.features(false)) {
                content = features.collect(Collectors.toList());
            }
            assertEquals(1, content.size());
            final Feature first = content.get(0);
            assertNotNull(first);
            assertEquals("Plaza Road Park", first.getPropertyValue("name"));
        }
    }

    private GeoJSONStore fromResource(final String resourcePath) throws URISyntaxException, DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource(resourcePath);
        assertNotNull("Bad test resource location", pointFile);

        return new GeoJSONStore(new GeoJSONProvider(), pointFile.toURI(), null);
    }

    private FeatureType buildPropertyArrayFeatureType(String name, Class<?> geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(Double[][].class).setName("array");
        ftb.addAttribute(geomClass).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private FeatureType buildGeometryFeatureType(String name, Class<?> geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(String.class).setName("fid").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(geomClass).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private FeatureType buildSimpleFeatureType(String name) {
        return buildSimpleFeatureType(name, Polygon.class);
    }

    private FeatureType buildSimpleFeatureType(String name, Class<? extends Geometry> geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(geomClass).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("name");
        return ftb.build();
    }

    private FeatureType buildFCFeatureType(String name) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("address");
        return ftb.build();
    }

    private void testFeatureTypes(FeatureType expected, FeatureType result) {
        final FeatureComparator comparator = new FeatureComparator(expected, result);
        comparator.compare();
    }

    @Test
    public void parsingOrderBugTest() throws IOException {
        String geoJsonPolygonString = "" +
                "         {\n" +
                "           \"coordinates\": [\n" +
                "             [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0],\n" +
                "               [100.0, 1.0], [100.0, 0.0] ]\n" +
                "           ],\n" +
                "           \"type\": \"Polygon\"\n" +
                "         }" ;
        GeoJSONObject obj = GeoJSONParser.parse(new ByteArrayInputStream( geoJsonPolygonString.getBytes()));
        Assert.assertTrue("A geometry should have been decoded", obj instanceof GeoJSONGeometry.GeoJSONPolygon);
    }

    /**
     * Verify IDs are read. We use two files, to ensure that whatever id field (feature id or property id) is present on
     * the first feature in the file, we detect an identifier.
     * We also ensure that feature id has priority over property id.
     */
    @Test
    public void fetchId() throws Exception {
        List<Feature> list;
        try (
                var store = fromResource("/org/apache/sis/internal/storage/geojson/id_management.json");
                var features = store.features(false)
        ) {
            list = features.toList();
        }

        assertEquals(2, list.size());
        assertEquals("id-0", list.get(0).getPropertyValue(AttributeConvention.IDENTIFIER));
        assertEquals("id-1", list.get(1).getPropertyValue(AttributeConvention.IDENTIFIER));

        try (
                var store = fromResource("/org/apache/sis/internal/storage/geojson/id_management2.json");
                var features = store.features(false)
        ) {
            list = features.toList();
        }

        assertEquals(2, list.size());
        assertEquals("id-1", list.get(0).getPropertyValue(AttributeConvention.IDENTIFIER));
        assertEquals("id-0", list.get(1).getPropertyValue(AttributeConvention.IDENTIFIER));
    }

    /**
     * When both feature id and a property named id are set, we automatically erase the property with the id.
     * <em>However</em>, if the fields have incompatible data type, we must raise an error, because their not mergeable.
     */
    @Test
    public void errorOnAmbiguousId() throws Exception {
        final Feature f;
        try (
                var store = fromResource("/org/apache/sis/internal/storage/geojson/id_conflict.json");
                var features = store.features(false)
        ) {
            var list = features.toList();
            fail("We should not accept identifiers with incompatible type");
        } catch (DataStoreException | IllegalArgumentException e) {
            // expected behaviour
        }
    }
}
