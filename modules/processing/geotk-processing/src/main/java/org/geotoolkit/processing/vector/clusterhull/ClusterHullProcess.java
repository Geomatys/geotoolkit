package org.geotoolkit.processing.vector.clusterhull;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.*;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.memory.InMemoryStore;
import org.locationtech.jts.geom.Geometry;

import java.util.*;
import java.util.stream.Collectors;

import org.geotoolkit.processing.AbstractProcess;

import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;

/**
 * Compute the cluster hull from a FeatureSet. The result is
 * a feature set of polygons that represent the footprints of the set of elements
 * according to tolerance distance.
 *
 * @author Maxime Gavens - décembre 2019
 */
public class ClusterHullProcess extends AbstractProcess {

    /**
     * Geometric transformer update at each cluster hull computing,
     * according with central meridian and origin of latitude for the Lambert CRS projection
     */
    private GeometryCSTransformer trs;

    /**
     * Used to build the featureSet result
     */
    private static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Default constructor
     */
    public ClusterHullProcess(final ParameterValueGroup input) {
        super(ClusterHullDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        final FeatureSet inputFeatureSet                = inputParameters.getMandatoryValue(ClusterHullDescriptor.FEATURE_SET_IN);
        final Double tolerance_value                    = inputParameters.getMandatoryValue(ClusterHullDescriptor.TOLERANCE_VALUE);
        final Unit<Length> tolerance_unit               = inputParameters.getValue(ClusterHullDescriptor.TOLERANCE_UNIT);
        final FeatureSet hull                           = computeClusterHull(inputFeatureSet, tolerance_value, tolerance_unit);
        outputParameters.getOrCreate(ClusterHullDescriptor.FEATURE_SET_OUT).setValue(hull);
    }

    /**
     * Compute the cluster hull from a feature set according to the measure of tolerance.
     *
     * @return the cluster hull
     */
    private FeatureSet computeClusterHull(final FeatureSet inputFeatureSet, final Double tolerance, Unit<Length> unit) throws ProcessException {
        try {
            // Convert the current tolerance to meter according to the unit specified
            Double toMeter;
            final UnitConverter uc = unit.getConverterTo(Units.METRE);
            toMeter = uc.convert((double)tolerance);
            // Initialise the GeometryCSTransformer (Lambert projection)
            initTransformation(inputFeatureSet);
            // Apply the process
            Set<Geometry> geometries = extractGeometrySet(inputFeatureSet);
            Set<Geometry> current = new HashSet<>();
            current.add(geometries.iterator().next());
            Set<Geometry> clusterHullSet = applyClusterHull(geometries, new HashSet<Geometry>(), current, new HashSet<Geometry>(), toMeter);
            // Extract the initial CRS
            final FeatureType type = inputFeatureSet.getType();
            CoordinateReferenceSystem crs = FeatureExt.getCRS(type);
            // Build the feature set
            FeatureSet clusterHull = createFeatureSetFromGeometrySet(clusterHullSet, crs);
            return clusterHull;
        } catch (FactoryException | DataStoreException | TransformException e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

    private void initTransformation(final FeatureSet inputFeatureSet) throws FactoryException, DataStoreException {
        Double[] median = getMedianPoint(inputFeatureSet);
        final FeatureType type = inputFeatureSet.getType();
        CoordinateReferenceSystem crs1 = FeatureExt.getCRS(type);
        if (crs1 == null) crs1 = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem crs2 = getLocalLambertCRS(median[0], median[1]);
        final MathTransform mt = CRS.findOperation(crs1, crs2, null).getMathTransform();
        final CoordinateSequenceTransformer cst = new CoordinateSequenceMathTransformer(mt);
        trs = new GeometryCSTransformer(cst);
    }

    private Set<Geometry> extractGeometrySet(final FeatureSet fs) throws DataStoreException {
        String geomName = FeatureExt.getDefaultGeometry(fs.getType()).getName().toString();
        Set<Geometry> geometries = fs.features(false)
                .map(f -> f.getPropertyValue(geomName))
                .filter(value -> value instanceof Geometry)
                .map(value -> (Geometry) value)
                .collect(Collectors.toSet());
        return geometries;
    }

    private Double customDistance(final Geometry geom1, final Geometry geom2) throws TransformException {
        Geometry trans1 = trs.transform(geom1);
        Geometry trans2 = trs.transform(geom2);

        return trans1.distance(trans2);
    }

    private Double[] getMedianPoint(final FeatureSet fs) throws DataStoreException {
        Double[] median = {0.0, 0.0};
        Envelope envelope = FeatureStoreUtilities.getEnvelope(fs,true);
        median[0] = (envelope.getMinimum(0) + envelope.getMaximum(0)) / 2;
        median[1] = (envelope.getMinimum(1) + envelope.getMaximum(1)) / 2;
        return median;
    }

    private Set<Geometry> applyClusterHull(Set<Geometry> in, Set<Geometry> out, Set<Geometry> current, Set<Geometry> store, final Double tolerance) throws TransformException {
        if (in.isEmpty()) {
            current.addAll(store);
            out.add(applyOnSetConvexHull(current));
            return out;
        } else {
            Set<Geometry> K = neighborhood(in, current, tolerance);
            if (K.isEmpty()) {
                in.removeAll(current);
                current.addAll(store);
                out.add(applyOnSetConvexHull(current));
                if (in.isEmpty()) {
                    return out;
                } else {
                    final Set<Geometry> newCurrent = new HashSet<>();
                    newCurrent.add(in.iterator().next());
                    return applyClusterHull(in, out, newCurrent, new HashSet<Geometry>(), tolerance);
                }
            } else {
                in.removeAll(current);
                in.removeAll(K);
                store.addAll(current);
                return applyClusterHull(in, out, K, store, tolerance);
            }
        }
    }

    private Set<Geometry> neighborhood(Set<Geometry> in, Set<Geometry> current, Double tolerance) throws TransformException {
        Set<Geometry> target = new HashSet<>();
        Set<Geometry> copyIn = new HashSet<>();
        copyIn.addAll(in);

        for (Geometry geomIn: copyIn) {
            for (Geometry geomCurrent: current) {
                if (customDistance(geomIn, geomCurrent) <= tolerance) {
                    target.add(geomIn);
                    break;
                }
            }
        }
        return target;
    }

    private Geometry applyOnSetConvexHull(Set<Geometry> geometries) {
        Geometry target = geometries.iterator().next();

        for (Geometry geom: geometries) {
            target = target.union(geom);
            target = target.convexHull();
        }
        return target;
    }

    private FeatureSet createFeatureSetFromGeometrySet(final Set<Geometry> geometries, final CoordinateReferenceSystem crs) throws DataStoreException {
        final InMemoryStore store = new InMemoryStore();
        final FeatureType type = createSimpleType(crs);
        List<Feature> features = new ArrayList<>();

        WritableFeatureSet resource;
        resource = (WritableFeatureSet) store.add(new DefiningFeatureSet(type, null));
        for (Geometry geometry: geometries) {
            features.add(createDefaultFeatureFromGeometry(geometry, type));
        }
        resource.add(features.iterator());
        return resource;
    }

    private Feature createDefaultFeatureFromGeometry(final Geometry geometry, final FeatureType type) {
        final Feature feature = type.newInstance();

        feature.setPropertyValue("geometry", geometryFactory.createGeometry(geometry));
        return feature;
    }

    private static FeatureType createSimpleType(final CoordinateReferenceSystem crs) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("Simple type");
        ftb.addAttribute(Geometry.class).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static ProjectedCRS getLocalLambertCRS(final double central_meridian, final double latitude_of_origin) {
        try {
            MathTransformFactory mtFactory = DefaultFactories.forBuildin(MathTransformFactory.class);;
            ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert_Conformal_Conic_1SP");
            parameters.parameter("central_meridian").setValue(central_meridian);
            parameters.parameter("latitude_of_origin").setValue(latitude_of_origin);
            String scentralMeridian = ((Integer) ((int) (Math.floor(central_meridian)))).toString();
            String slatitudeOfOrigin = ((Integer) ((int) (Math.floor(latitude_of_origin)))).toString();
            DefiningConversion conversion = new DefiningConversion("My conversion", parameters);
            CRSFactory crsFactory = DefaultFactories.forBuildin(CRSFactory.class);
            final Map<String, Object> properties = new HashMap<>();
            properties.put(ProjectedCRS.NAME_KEY, "LambertCC_" + slatitudeOfOrigin + "_" + scentralMeridian);
            ProjectedCRS targetCRS = crsFactory.createProjectedCRS(properties, CommonCRS.WGS84.normalizedGeographic(), conversion, PredefinedCS.PROJECTED);
            return targetCRS;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
