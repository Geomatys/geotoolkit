package org.geotoolkit.data.geojson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.data.FeatureStore;
import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.*;
import org.geotoolkit.data.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONReadTest extends org.geotoolkit.test.TestBase {

    @Test
    public void readPointTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/point.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("point"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("point", Point.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readMultiPointTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/multipoint.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("multipoint"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("multipoint", MultiPoint.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readLineStringTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/linestring.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("linestring"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("linestring", LineString.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readMultiLineStringTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/multilinestring.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("multilinestring"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("multilinestring", MultiLineString.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readPolygonTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/polygon.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("polygon"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("polygon", Polygon.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readMultiPolygonTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/multipolygon.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("multipolygon"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("multipolygon", MultiPolygon.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readGeometryCollectionTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/geometrycollection.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("geometrycollection"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildGeometryFeatureType("geometrycollection", GeometryCollection.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());

    }

    @Test
    public void readFeatureTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/feature.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("feature"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildSimpleFeatureType("feature"), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(1, fcoll.features(false).count());
    }

    @Test
    public void readFeatureCollectionTest() throws DataStoreException, URISyntaxException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/featurecollection.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(pointFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("featurecollection"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildFCFeatureType("featurecollection"), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(7, fcoll.features(false).count());
    }

    /**
     * Test reading of Features with array as properties value
     * @throws DataStoreException
     */
    @Test
    public void readPropertyArrayTest() throws DataStoreException, URISyntaxException {
        URL arrayFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/f_prop_array.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(arrayFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();
        assertEquals(NamesExt.create("f_prop_array"), name);

        FeatureType ft = store.getFeatureType(name.toString());
        testFeatureTypes(buildPropertyArrayFeatureType("f_prop_array", Geometry.class), ft);

        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(2, fcoll.features(false).count());

        Double[][] array1 = new Double[5][5];
        Double[][] array2 = new Double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                array1[i][j] = (double) (i + j);
                array2[i][j] = (double) (i - j);
            }
        }

        Iterator<Feature> ite = fcoll.features(false).iterator();
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
        URL jsonFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/sample_with_null_properties.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(jsonFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();

        FeatureType ft = store.getFeatureType(name.toString());
        Session session = store.createSession(false);
        FeatureSet fcoll = session.getFeatureCollection(QueryBuilder.all(name.toString()));
        assertEquals(15, fcoll.features(false).count());
    }

    /**
     * This test ensure integer types over Integer.MAX_VALUE are converted to Long.
     * @throws DataStoreException
     */
    @Test
    public void readLongTest() throws DataStoreException, URISyntaxException {
        URL jsonFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/longValue.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(jsonFile.toURI());
        FeatureStore store = (FeatureStore) DataStores.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        GenericName name = store.getNames().iterator().next();

        FeatureSet featureSet = (FeatureSet) store.findResource(name.toString());
        Feature feature = featureSet.features(false).findFirst().get();
        assertEquals(853555090789l, feature.getPropertyValue("size"));
    }

    /**
     * Test GeoJSONParser full and lazy reading on FeatureCollection
     */
    @Test
    public void parserTest() throws URISyntaxException, IOException {
        URL fcFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/featurecollection.json");
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

    private FeatureType buildPropertyArrayFeatureType(String name, Class geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(Double[][].class).setName("array");
        ftb.addAttribute(geomClass).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private FeatureType buildGeometryFeatureType(String name, Class geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(geomClass).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
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
        for(PropertyType desc : expected.getProperties(true)){
            PropertyType td = result.getProperty(desc.getName().toString());
            assertNotNull(td);
            if(td instanceof AttributeType){
                assertEquals(((AttributeType) td).getValueClass(), ((AttributeType)desc).getValueClass());
            }
        }
    }
}
