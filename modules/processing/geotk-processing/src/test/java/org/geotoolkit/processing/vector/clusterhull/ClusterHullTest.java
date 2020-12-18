package org.geotoolkit.processing.vector.clusterhull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.apache.sis.measure.Units;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geotoolkit.storage.geojson.GeoJSONProvider;
import org.geotoolkit.storage.geojson.GeoJSONStore;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of ClusterHull process
 *
 * @author Maxime Gavens - juin 2019
 * @module
 */
public class ClusterHullTest extends AbstractProcessTest {

    private static final double TOLERANCE = 1e-4;

    private static GeometryFactory geometryFactory = new GeometryFactory();

    public ClusterHullTest() {
        super("vector:clusterhull");
    }

    @Test
    public void testClusterHull1() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "2_points.json",
                "2_points_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull2() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "4_points.json",
                "4_points_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "4_points.json",
                "4_points_80km_expected.json",
                80.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull3() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "3_linestring.json",
                "3_linestring_30km_expected.json",
                30.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "3_linestring.json",
                "3_linestring_71km_expected.json",
                71.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull4() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "multipoint.json",
                "multipoint_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "multipoint.json",
                "multipoint_90km_expected.json",
                90.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "multipoint.json",
                "multipoint_120km_expected.json",
                120.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "multipoint.json",
                "multipoint_150km_expected.json",
                150.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull5() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "complexe.json",
                "complexe_40km_expected.json",
                40.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "complexe.json",
                "complexe_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
    }

    private void testClusterHullBasic(String filename_in, String filename_expected, Double tolerance, Unit<Length> unit) throws URISyntaxException, DataStoreException, NoSuchIdentifierException, ProcessException {
        testClusterHullBasic(filename_in, filename_expected, tolerance, null, unit);
    }

    private void testClusterHullBasic(String filename_in, String filename_expected, Double tolerance, Double epsilon, Unit<Length> unit) throws URISyntaxException, DataStoreException, NoSuchIdentifierException, ProcessException {
        final FeatureSet featureSet = buildFeatureSet(filename_in);
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:clusterhull");
        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_set_in").setValue(featureSet);
        in.parameter("tolerance_value").setValue(tolerance);
        in.parameter("smoothing_epsilon").setValue(epsilon);
        in.parameter("tolerance_unit").setValue(unit);
        org.geotoolkit.process.Process proc = desc.createProcess(in);
        //Feature set out
        final FeatureSet out = (FeatureSet) proc.call().parameter("feature_set_out").getValue();
        //Feature set expected
        final FeatureSet expected = buildFeatureSet(filename_expected);
        //Test
        GeometryCollection gc1 = extractGeometryCollectionFromFeatureSet(out);
        GeometryCollection gc2 = extractGeometryCollectionFromFeatureSet(expected);
        // is same geometry
        assertTrue(gc1.equalsExact(gc2, TOLERANCE));
        FeatureType type1 = out.getType();
        FeatureType type2 = expected.getType();
        // is same feature
        assertEquals(FeatureExt.getCRS(type1), FeatureExt.getCRS(type2));
    }

    private GeometryCollection extractGeometryCollectionFromFeatureSet(final FeatureSet fs) throws DataStoreException {
        Geometry[] geometries = fs.features(false)
                .map(f -> f.getProperty("geometry"))
                .map(p -> p.getValue())
                .filter(value -> value instanceof Geometry)
                .map(value -> (Geometry) value)
                .toArray(size -> new Geometry[size]);
        final GeometryCollection gc = geometryFactory.createGeometryCollection(geometries);
        gc.normalize();
        return gc;
    }

    private FeatureSet buildFeatureSet(String filename) throws URISyntaxException, DataStoreException {
        final String resourceName = "geojson/" + filename;
        final URL resource = ClusterHullTest.class.getResource(resourceName);
        if (resource == null) throw new RuntimeException("No resource found for "+resourceName);
        final URI uri = resource.toURI();
        return new GeoJSONStore(new GeoJSONProvider(), uri, 7);
    }
}
