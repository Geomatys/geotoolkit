package org.geotoolkit.processing.vector.clusterhull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.apache.sis.measure.Units;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.geotoolkit.storage.DataStores;
import static org.geotoolkit.storage.geojson.GeoJSONProvider.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of ClusterHull process
 *
 * @author Maxime Gavens - décembre 2019
 * @module
 */
public class ClusterHullTest extends AbstractProcessTest {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    public ClusterHullTest() {
        super("vector:clusterhull");
    }

    @Test
    public void testClusterHull1() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "cluster_hull_test_1.json",
                "cluster_hull_test_1_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull2() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "cluster_hull_test_2.json",
                "cluster_hull_test_2_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "cluster_hull_test_2.json",
                "cluster_hull_test_2_80km_expected.json",
                80.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull3() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "cluster_hull_test_3.json",
                "cluster_hull_test_3_30km_expected.json",
                30.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "cluster_hull_test_3.json",
                "cluster_hull_test_3_71km_expected.json",
                71.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull4() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "cluster_hull_test_4.json",
                "cluster_hull_test_4_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "cluster_hull_test_4.json",
                "cluster_hull_test_4_90km_expected.json",
                90.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "cluster_hull_test_4.json",
                "cluster_hull_test_4_120km_expected.json",
                120.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "cluster_hull_test_4.json",
                "cluster_hull_test_4_150km_expected.json",
                150.0,
                Units.KILOMETRE
        );
    }
    @Test
    public void testClusterHull15() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        testClusterHullBasic(
                "cluster_hull_test_5.json",
                "cluster_hull_test_5_40km_expected.json",
                40.0,
                Units.KILOMETRE
        );
        testClusterHullBasic(
                "cluster_hull_test_5.json",
                "cluster_hull_test_5_60km_expected.json",
                60.0,
                Units.KILOMETRE
        );
    }

    private void testClusterHullBasic(String filename_in, String filename_expected, Double tolerance, Unit<Length> unit) throws URISyntaxException, DataStoreException, NoSuchIdentifierException, ProcessException {
        final FeatureSet featureSet = buildFeatureSet(filename_in);
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:clusterhull");
        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_set_in").setValue(featureSet);
        in.parameter("tolerance_value").setValue(tolerance);
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
        assertTrue(gc1.equalsNorm(gc2));
        FeatureType type1 = out.getType();
        FeatureType type2 = expected.getType();
        // is same feature
        assertEquals(FeatureExt.getCRS(type1), FeatureExt.getCRS(type2));
    }

    private GeometryCollection extractGeometryCollectionFromFeatureSet(final FeatureSet fs) throws DataStoreException {
        List<Geometry> geometries = new ArrayList<>();

        fs.features(false).collect(Collectors.toList());
        geometries = fs.features(false)
                .map(f -> f.getProperty("geometry"))
                .map(p -> p.getValue())
                .filter(value -> value instanceof Geometry)
                .map(value -> (Geometry) value)
                .collect(Collectors.toList());
        int size = geometries.size();
        Geometry[] geometries1 = geometries.toArray(new Geometry[size]);
        return geometryFactory.createGeometryCollection(geometries1);
    }

    public FeatureSet buildFeatureSet(String filename) throws URISyntaxException, DataStoreException {
        final URI uri = ClusterHullTest.class.getResource("/org.geotoolkit.processing.vector.clusterhull/geojson/" + filename).toURI();
        ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(uri);
        final DataStore store = DataStores.open(param);
        GenericName types = DataStores.getNames(store, true, FeatureSet.class).iterator().next();
        FeatureSet target = (FeatureSet) store.findResource(types.toString());

        return target;
    }
}
