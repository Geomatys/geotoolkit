package org.geotoolkit.processing.vector.clusterhull;

import com.fasterxml.jackson.core.JsonEncoding;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
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
import org.geotoolkit.storage.geojson.GeoJSONStreamWriter;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Feature;
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

    @Test
    public void mainTest0() throws DataStoreException, NoSuchIdentifierException, ProcessException, URISyntaxException {
        final FeatureSet featureSet = buildFeatureSet("camp-test.json");
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:clusterhull");
        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_set_in").setValue(featureSet);
        in.parameter("tolerance_value").setValue(1000);
        in.parameter("smoothing_epsilon").setValue(10);
        //in.parameter("tolerance_unit").setValue(unit);
        org.geotoolkit.process.Process proc = desc.createProcess(in);
        //Feature set out
        final FeatureSet out = (FeatureSet) proc.call().parameter("feature_set_out").getValue();
        write(out, "camp-test_result.json");
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
        write(out, filename_expected);
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

    private void write(FeatureSet fs, String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (GeoJSONStreamWriter featureWriter = new GeoJSONStreamWriter(baos, fs.getType(), JsonEncoding.UTF8, 4, true);
             Stream<Feature> stream = fs.features(false)) {
            Iterator<Feature> iterator = stream.iterator();
            while (iterator.hasNext()) {
                Feature next = iterator.next();
                Feature neww = featureWriter.next();
                FeatureExt.copy(next, neww, false);
                featureWriter.write();
            }
        } catch (DataStoreException ex) {
            System.out.println(ex.toString());
        }

        try(OutputStream outputStream = new FileOutputStream(file)) {
            baos.writeTo(outputStream);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
