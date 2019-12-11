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
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.storage.memory.InMemoryStore;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.geotoolkit.processing.AbstractProcess;

import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
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
 * @author Maxime Gavens
 */
public class ClusterHullProcess extends AbstractProcess {

    /**
     * Geometric transformer update at each cluster hull computing,
     * according with central meridian and origin of latitude for the Lambert CRS projection
     */
    private GeometryCSTransformer trs;

    /**
     * Tolerance units authorized
     */
    private static ArrayList<Unit<Length>> unitList = new ArrayList<>();

    /**
     * Used to build the featureSet result
     */
    private static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Interface that computing distance between two geometries.
     * First step, two geometries are projected by Lambert projection,
     * then the
     */
    private BiFunction<Geometry, Geometry, Double> computeDistance = this::customDistance;

    /**
     * Default constructor
     */
    public ClusterHullProcess(final ParameterValueGroup input) {
        super(ClusterHullDescriptor.INSTANCE,input);
        unitList.add(Units.METRE);
        unitList.add(Units.KILOMETRE);
        unitList.add(Units.STATUTE_MILE);
        unitList.add(Units.NAUTICAL_MILE);
        unitList.add(Units.INCH);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureSet inputFeatureSet                = inputParameters.getValue(ClusterHullDescriptor.FEATURE_SET_IN);
        final Double tolerance_value                    = inputParameters.getValue(ClusterHullDescriptor.TOLERANCE_VALUE);
        final Unit<Length> tolerance_unit               = inputParameters.getValue(ClusterHullDescriptor.TOLERANCE_UNIT);
        final FeatureSet hull                           = computeClusterHull(inputFeatureSet, tolerance_value, tolerance_unit);
        outputParameters.getOrCreate(ClusterHullDescriptor.FEATURE_SET_OUT).setValue(hull);
    }

    /**
     * Compute the cluster hull from a feature set according to the measure of tolerance.
     *
     * @return the cluster hull
     */
    public FeatureSet computeClusterHull(final FeatureSet inputFeatureSet, final Double tolerance, Unit<Length> unit) {
        try {
            // Convert the current tolerance to meter according to the unit specified
            Double toMeter;
            if (!unitList.contains(unit)) unit = Units.METRE;
            final UnitConverter uc = unit.getConverterTo(Units.METRE);
            toMeter = uc.convert((double)tolerance);
            // Initialise the GeometryCSTransformer (Lambert projection)
            initTransformation(inputFeatureSet);
            // Apply the process
            Set<Geometry> geometries = extractGeometrySet(inputFeatureSet);
            Set<Geometry> current = new HashSet<>();
            current.add(geometries.iterator().next());
            Set<Geometry> clusterHullSet = applyGeometricPartioning(geometries, new HashSet<Geometry>(), current, new HashSet<Geometry>(), toMeter);
            // Extract the initial CRS
            CoordinateReferenceSystem crs = getCRSFromFeatureSet(inputFeatureSet);
            // Build the feature set
            FeatureSet clusterHull = createFeatureSetFromGeometrySet(clusterHullSet, crs);
            return clusterHull;
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (DataStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<Geometry> extractGeometrySet(final FeatureSet fs) {
        Set<Geometry> geometries   = new HashSet<Geometry>();

        try {
            geometries = fs.features(false)
                    .map(f -> f.getProperty("geometry"))
                    .map(p -> p.getValue())
                    .filter(value -> value instanceof Geometry)
                    .map(value -> (Geometry) value)
                    .collect(Collectors.toSet());
        } catch(DataStoreException e) {
            e.printStackTrace();
        }
        return geometries;
    }

    private void initTransformation(final FeatureSet inputFeatureSet) throws FactoryException, DataStoreException {
        Double[] median = getMedianPoint(inputFeatureSet);
        CoordinateReferenceSystem crs1 = getCRSFromFeatureSet(inputFeatureSet);
        if (crs1 == null) crs1 = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem crs2 = getLocalLambertCRS(median[0], median[1]);
        final MathTransform mt = CRS.findOperation(crs1, crs2, null).getMathTransform();
        final CoordinateSequenceTransformer cst = new CoordinateSequenceMathTransformer(mt);
        trs = new GeometryCSTransformer(cst);
    }

    private CoordinateReferenceSystem getCRSFromFeatureSet(FeatureSet fs) throws DataStoreException {
        final FeatureType type = fs.getType();
        final PropertyType geometryType = type.getProperty("geometry");
        final CoordinateReferenceSystem crs = FeatureExt.getCRS(geometryType);

        return crs;
    }

    private Double customDistance(final Geometry geom1, final Geometry geom2) {
        Geometry trans1 = null;
        Geometry trans2 = null;

        try {
            trans1 = trs.transform(geom1);
            trans2 = trs.transform(geom2);
        } catch (TransformException e) {
            e.printStackTrace();
        }

        return trans1.distance(trans2);
    }

    private Double[] getMedianPoint(final FeatureSet fs) throws DataStoreException {
        Double[] median = {0.0, 0.0};
        Optional<Envelope> envelope;

        envelope = fs.features(false)
                .map(f -> f.getProperty("geometry"))
                .map(p -> p.getValue())
                .filter(value -> value instanceof Geometry)
                .map(value -> (Geometry)value)
                .map(Geometry::getEnvelopeInternal)
                .reduce((e1, e2) -> {
                    e1.expandToInclude(e2);
                    return e1;
                });
        if(envelope.isPresent()){
            median[0] = (envelope.get().getMinX() + envelope.get().getMaxX()) / 2;
            median[1] = (envelope.get().getMinY() + envelope.get().getMaxY()) / 2;
        }
        return median;
    }

    private Set<Geometry> applyGeometricPartioning(Set<Geometry> in, Set<Geometry> out, Set<Geometry> current, Set<Geometry> store, final Double tolerance) {
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
                    return applyGeometricPartioning(in, out, newCurrent, new HashSet<Geometry>(), tolerance);
                }
            } else {
                in.removeAll(current);
                in.removeAll(K);
                store.addAll(current);
                return applyGeometricPartioning(in, out, K, store, tolerance);
            }
        }
    }

    private Set<Geometry> neighborhood(Set<Geometry> in, Set<Geometry> current, Double tolerance) {
        Set<Geometry> target = new HashSet<>();
        Set<Geometry> copyIn = new HashSet<>();
        copyIn.addAll(in);

        for (Geometry geomIn: copyIn) {
            for (Geometry geomCurrent: current) {
                if (computeDistance.apply(geomIn, geomCurrent) <= tolerance) {
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

    private Geometry applyConvexHullOnGeometryList(final List<Geometry> geometries) {
        Geometry convexHull = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);

        for(Geometry g: geometries) {
            convexHull = convexHull.union(g);
            convexHull = convexHull.convexHull();
        }
        return convexHull;
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

    private static ProjectedCRS getLocalLambertCRS(final double central_meridan, final double latitude_of_origin) {
        try {
            MathTransformFactory mtFactory = DefaultFactories.forBuildin(MathTransformFactory.class);;
            ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert_Conformal_Conic_1SP");
            parameters.parameter("central_meridian").setValue(central_meridan);
            parameters.parameter("latitude_of_origin").setValue(latitude_of_origin);
            String scentralMeridian = ((Integer) ((int) (Math.floor(central_meridan)))).toString();
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
