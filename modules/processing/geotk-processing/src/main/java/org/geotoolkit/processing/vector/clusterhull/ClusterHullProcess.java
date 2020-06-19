package org.geotoolkit.processing.vector.clusterhull;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultDerivedCRS;
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
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;

/**
 * Compute the cluster hull from a FeatureSet. The result is
 * a feature set of polygons that represent the footprints of the set of elements
 * according to tolerance distance.
 * The algorithm takes as input a geometry set and a tolerance parameter. It's about gradually building a clustered geometry set.
 * At the start we consider a current geometry and the result of the process is the empty set.
 * During each iteration, we found the neighbors geometry and we add them in a separate package.
 * Then we find the neighboring geometries of the previous neighboring geometries.
 * The update takes place as follows:
 *      - the current geometry become the neighbors found.
 *      - the current geometry is store in a separate package and removed from the starting package
 * When no more neighbors are found, we apply the clusterhull process in all geometry found, and store the result in a output package
 * we continue until the original package is empty.
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
     * Geometric transformer update at each cluster hull computing,
     * according with central meridian and origin of latitude for the inverse of Lambert CRS projection
     */
    private GeometryCSTransformer inv;

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

    private void initTransformation(final FeatureSet inputFeatureSet) throws FactoryException, DataStoreException, NoninvertibleTransformException {
        Double[] median = getMedianPoint(inputFeatureSet);
        final FeatureType type = inputFeatureSet.getType();
        CoordinateReferenceSystem crs1 = FeatureExt.getCRS(type);
        if (crs1 == null) {
            fireWarningOccurred("No CRS referenced in the input data. WGS84 CRS is provided by default.", 0, null);
            crs1 = CommonCRS.WGS84.normalizedGeographic();
        }
        final CoordinateReferenceSystem crs2 = getLocalLambertCRS(median[0], median[1]);
        final MathTransform mt  = CRS.findOperation(crs1, crs2, null).getMathTransform();
        final MathTransform mtInv = mt.inverse();
        final CoordinateSequenceTransformer cst = new CoordinateSequenceMathTransformer(mt);
        final CoordinateSequenceTransformer cstInv = new CoordinateSequenceMathTransformer(mtInv);
        trs = new GeometryCSTransformer(cst);
        inv = new GeometryCSTransformer(cstInv);
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

    /**
     * On each recurrence:
     *      - we are looking for the neighbors of "CURRENT" in "IN" (if "IN" is empty stop the process)
     *      - If neighbors is not empty:
     *          - Remove geometries found from "IN"
     *          - Put the contain of "CURRENT" in "STORE"
     *          - "CURRENT" become the geometries found
     *      - If neighbors is empty:
     *          - Remove geometries found from "IN"
     *          - Put the contain of "STORE" in "CURRENT" and apply the convexhull process on it
     *          - Put the result in "OUT"
     *          - Empty the "STORE", get a new random geometry from "IN" in "CURRENT"
     *
     * @param in        Initial bundle of geometry, while this is not empty, the algorithm continue to process.
     * @param out       Contains the process result.
     * @param current   Contains geometries used to find neighboring geometries
     * @param store     Contains geometries belonging to the same cluster
     * @param tolerance Define the distance which will determine if the geometries are in the same cluster.
     * @return
     * @throws TransformException
     */
    private Set<Geometry> applyClusterHull(Set<Geometry> in, Set<Geometry> out, Set<Geometry> current, Set<Geometry> store, final Double tolerance) throws TransformException {
        if (in.isEmpty()) {
            current.addAll(store);
            out.add(applyOnSetBuffer(current, tolerance));
            return out;
        } else {
            Set<Geometry> K = neighborhood(in, current, tolerance);
            if (K.isEmpty()) {
                in.removeAll(current);
                current.addAll(store);
                out.add(applyOnSetBuffer(current, tolerance));
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

    private Geometry applyOnSetBuffer(Set<Geometry> geometries, double tolerance) throws TransformException {
        Geometry target = geometries.iterator().next();

        for (Geometry geom: geometries) {
            target = target.union(geom);
        }
        target = trs.transform(target);
        target = target.buffer(tolerance);
        target = inv.transform(target);
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

    private static ProjectedCRS getLocalLambertCRS(final double central_meridian, final double latitude_of_origin) throws FactoryException {
        final MathTransformFactory mtFactory = DefaultFactories.forBuildin(MathTransformFactory.class);;
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert_Conformal_Conic_1SP");
        parameters.parameter("central_meridian").setValue(central_meridian);
        parameters.parameter("latitude_of_origin").setValue(latitude_of_origin);

        final CoordinateOperationFactory coFactory = DefaultFactories.forClass(CoordinateOperationFactory.class);
        final OperationMethod operationMethod = coFactory.getOperationMethod("Lambert_Conformal_Conic_1SP");
        final Map<String,?> nameConversion = Collections.singletonMap("name", "My conversion");
        final Conversion conversion = coFactory.createDefiningConversion(nameConversion, operationMethod, parameters);

        final CRSFactory crsFactory = DefaultFactories.forBuildin(CRSFactory.class);
        final Map<String, Object> properties = new HashMap<>();
        final String name = String.format("LambertCC_%d_%d", (int) latitude_of_origin, (int) central_meridian);
        properties.put(ProjectedCRS.NAME_KEY, name);
        final ProjectedCRS targetCRS = crsFactory.createProjectedCRS(properties, CommonCRS.WGS84.normalizedGeographic(), conversion, PredefinedCS.PROJECTED);
        return targetCRS;
    }
}
