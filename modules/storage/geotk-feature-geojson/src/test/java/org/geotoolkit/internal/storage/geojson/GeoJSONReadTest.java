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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.apache.sis.feature.FeatureComparator;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.test.TestCase;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public class GeoJSONReadTest extends TestCase {

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

        Double[][] array1 = new Double[5][5];
        Double[][] array2 = new Double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                array1[i][j] = (double) (i + j);
                array2[i][j] = (double) (i - j);
            }
        }

        Iterator<Feature> ite = store.features(false).iterator();
        Feature feat1 = ite.next();
        assertArrayEquals(array1, (Double[][]) feat1.getProperty("array").getValue());

        Feature feat2 = ite.next();
        assertArrayEquals(array2, (Double[][]) feat2.getProperty("array").getValue());

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

    private FeatureType buildPropertyArrayFeatureType(String name, Class<?> geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
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
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(Polygon.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
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
}
