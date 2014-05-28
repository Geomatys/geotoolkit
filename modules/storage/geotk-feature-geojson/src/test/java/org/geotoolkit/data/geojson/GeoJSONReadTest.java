package org.geotoolkit.data.geojson;

import com.vividsolutions.jts.geom.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.*;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONReadTest {

    @Test
    public void readPointTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/point.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("point"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("point", Point.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readMultiPointTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/multipoint.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("multipoint"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("multipoint", MultiPoint.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readLineStringTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/linestring.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("linestring"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("linestring", LineString.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readMultiLineStringTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/multilinestring.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("multilinestring"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("multilinestring", MultiLineString.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readPolygonTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/polygon.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("polygon"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("polygon", Polygon.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readMultiPolygonTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/multipolygon.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("multipolygon"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("multipolygon", MultiPolygon.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readGeometryCollectionTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/geometrycollection.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("geometrycollection"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildGeometryFeatureType("geometrycollection", GeometryCollection.class), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());

    }

    @Test
    public void readFeatureTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/feature.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("feature"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildSimpleFeatureType("feature"), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(1, fcoll.size());
    }

    @Test
    public void readFeatureCollectionTest() throws DataStoreException {
        URL pointFile = GeoJSONReadTest.class.getResource("/org/geotoolkit/geojson/featurecollection.json");

        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(URLP.getName().getCode()).setValue(pointFile);
        FeatureStore store = FeatureStoreFinder.open(param);
        assertNotNull(store);

        assertEquals(1, store.getNames().size());
        Name name = store.getNames().iterator().next();
        assertEquals(new DefaultName("featurecollection"), name);

        FeatureType ft = store.getFeatureType(name);
        testFeatureTypes(buildFCFeatureType("featurecollection"), ft);

        Session session = store.createSession(false);
        FeatureCollection fcoll = session.getFeatureCollection(QueryBuilder.all(name));
        assertEquals(7, fcoll.size());
    }

    private FeatureType buildGeometryFeatureType(String name, Class geomClass) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.add("geometry", geomClass, DefaultGeographicCRS.WGS84);
        return ftb.buildSimpleFeatureType();
    }

    private FeatureType buildSimpleFeatureType(String name) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.add("geometry", Polygon.class, DefaultGeographicCRS.WGS84);
        ftb.add("name", String.class);
        return ftb.buildSimpleFeatureType();
    }

    private FeatureType buildFCFeatureType(String name) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.add("geometry", Geometry.class, DefaultGeographicCRS.WGS84);
        ftb.add("name", String.class);
        ftb.add("address", String.class);
        return ftb.buildSimpleFeatureType();
    }

    private void testFeatureTypes(FeatureType expected, FeatureType result) {
        for(PropertyDescriptor desc : expected.getDescriptors()){
            PropertyDescriptor td = result.getDescriptor(desc.getName().getLocalPart());
            assertNotNull(td);
            assertEquals(td.getType().getBinding(), desc.getType().getBinding());
        }
    }
}
