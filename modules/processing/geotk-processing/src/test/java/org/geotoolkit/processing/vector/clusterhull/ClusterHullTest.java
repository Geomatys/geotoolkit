package org.geotoolkit.processing.vector.clusterhull;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.storage.memory.InMemoryStore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

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
    public void testClusterHull1() throws NoSuchIdentifierException, ProcessException {
        // Inputs
        final FeatureSet featureSet = buildFeatureSet();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:clusterhull");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_set_in").setValue(featureSet);
        in.parameter("tolerance_value").setValue(100000.0);
        in.parameter("tolerance_unit").setValue(Units.METRE);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Feature set out
        final FeatureSet resultGeom = (FeatureSet) proc.call().parameter("feature_set_out").getValue();

        //Expected Features out
        final FeatureSet featureSetResult1 = buildFeatureSetResult1();
        assertTrue(featureSetResult1.equals(resultGeom));
    }

    private FeatureSet buildFeatureSet() {
        List<Geometry> geometries = new ArrayList<Geometry>();

        Geometry geom1 = geometryFactory.createPoint(new Coordinate(-6.6, 47.3));
        Geometry geom2 = geometryFactory.createPoint(new Coordinate(-7.6, 47.7));
        Geometry geom3 = geometryFactory.createPoint(new Coordinate(-6.4, 48.0));
        Geometry geom4 = geometryFactory.createPoint(new Coordinate(-6.0, 47.6));
        Geometry geom5 = geometryFactory.createPoint(new Coordinate(-4.9, 47.0));
        Geometry geom6 = geometryFactory.createPoint(new Coordinate(-4.9, 47.5));
        Geometry geom7 = geometryFactory.createPoint(new Coordinate(-4.5, 46.7));
        Geometry geom8 = geometryFactory.createPoint(new Coordinate(-6.2, 46.5));
        Geometry geom9 = geometryFactory.createPoint(new Coordinate(-7.8, 45.4));
        Geometry geom10 = geometryFactory.createPoint(new Coordinate(-6.0, 46.0));
        Geometry geom11 = geometryFactory.createPoint(new Coordinate(-7.7, 46.6));
        geometries.add(geom1);
        geometries.add(geom2);
        geometries.add(geom3);
        geometries.add(geom4);
        geometries.add(geom5);
        geometries.add(geom6);
        geometries.add(geom7);
        geometries.add(geom8);
        geometries.add(geom9);
        geometries.add(geom10);
        geometries.add(geom11);
        try {
            FeatureSet fs = createFeatureSetFromGeometryList(geometries);

            return fs;
        } catch (DataStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    private FeatureSet buildFeatureSetResult1() {
        List<Geometry> geometries = new ArrayList<Geometry>();

        Geometry geom1 = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-6.6, 47.3),
                new Coordinate(-7.6, 47.7),
                new Coordinate(-6.4, 48),
                new Coordinate(-4.9, 47.5),
                new Coordinate(-6.6, 47.3),
        });
        Geometry geom2 = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-4.5, 46.7),
                new Coordinate(-4.9, 47),
                new Coordinate(-4.9, 47.5),
                new Coordinate(-4.5, 46.7),
        });
        Geometry geom3 = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(-6.2, 46.5),
                new Coordinate(-6, 46)
        });
        Geometry geom4 = geometryFactory.createPoint(new Coordinate(-7.8, 45.4));
        Geometry geom5 = geometryFactory.createPoint(new Coordinate(-7.7, 46.6));
        geometries.add(geom1);
        geometries.add(geom2);
        geometries.add(geom3);
        geometries.add(geom4);
        geometries.add(geom5);
        try {
            FeatureSet fs = createFeatureSetFromGeometryList(geometries);

            return fs;
        } catch (DataStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    private FeatureSet createFeatureSetFromGeometryList(final List<Geometry> geometries) throws DataStoreException {
        final InMemoryStore store = new InMemoryStore();
        final FeatureType type = createSimpleType();
        List<Feature> features = new ArrayList<>();

        // create the featureStore (ref: featureStoreWritingDemo)
        WritableFeatureSet resource;
        resource = (WritableFeatureSet) store.add(new DefiningFeatureSet(type, null));
        // adding records
        for (Geometry geometry: geometries) {
            features.add(createDefaultFeatureFromGeometry(geometry, type));
        }
        // store them
        resource.add(features.iterator());

        return resource;
    }

    private static FeatureType createSimpleType() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("Simple type");
        ftb.addAttribute(Geometry.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);

        return ftb.build();
    }

    private Feature createDefaultFeatureFromGeometry(final Geometry geometry, final FeatureType type) {
        final Feature feature = type.newInstance();

        feature.setPropertyValue("geom", geometryFactory.createGeometry(geometry));

        return feature;
    }
}
