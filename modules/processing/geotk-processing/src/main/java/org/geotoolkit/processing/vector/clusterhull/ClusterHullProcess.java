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
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.memory.InMemoryStore;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.precision.GeometryPrecisionReducer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.Duration;
import java.time.Instant;

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
import org.locationtech.jts.geom.PrecisionModel;


/**
 * Compute the cluster hull from a FeatureSet. The result is
 * a feature set of polygons that represent the footprints of the set of elements
 * according to tolerance distance.
 * The algorithm takes as input a geometry set and a tolerance parameter. It's about gradually building a clustered geometry set.
 * At the start we consider a set of Cluster, that contains a set of geometry which are a geometry and his projection by Lambert CRS projection.
 * During each iteration, we split the set into the first Cluster and the rest of the set.
 * Then we iterate on the rest of the set:
 *      - if we found a cluster that intersects the first, we merge them and interrupt the loop.
 *      - if not we consider this one as a complete cluster and send it to the final result.
 * This procedure is apply until the input set is empty.
 *
 * @author Maxime Gavens - décembre 2019
 */
public class ClusterHullProcess extends AbstractProcess {

    private int increment = 0;

    private int geomSize;

    private int[] roadmap;

    /**
     * Measure of tolerance used to compute cluster hull.
     * It determines the minimum length between two cluster and
     * the buffer width.
     */
    private Double tolerance;

    /**
     * Tolerance used for the DouglasPeucker smoothing algorithm.
     */
    private Double epsilon = null;

    /**
     * Contains the result after processing.
     */
    private Set<Geometry> clusters = new HashSet<>();

    /**
     * Geometric transformer update at each cluster hull computing,
     * according with central meridian and origin of latitude for the Lambert CRS projection.
     */
    private GeometryCSTransformer trs;

    /**
     * Geometric transformer update at each cluster hull computing,
     * according with central meridian and origin of latitude for the inverse of Lambert CRS projection.
     */
    private GeometryCSTransformer inv;

    /**
     * Used to build the featureSet result.
     */
    private static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Default constructor.
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
        final Double smoothing_epsilon                  = inputParameters.getValue(ClusterHullDescriptor.SMOOTHING_EPSILON);
        final Unit<Length> unit                         = inputParameters.getValue(ClusterHullDescriptor.TOLERANCE_UNIT);
        initTransformation(inputFeatureSet);
        initConversion(tolerance_value, smoothing_epsilon, unit);
        final FeatureSet hull                           = computeClusterHull(inputFeatureSet);
        outputParameters.getOrCreate(ClusterHullDescriptor.FEATURE_SET_OUT).setValue(hull);
    }

    /**
     * The WorkGeometry contains a geometry and especially the projection by the Lambert CRS projection.
     * Its interest is to limit the number of projection performs during the process (useful for distance, buffer, Douglas-Peucker)
     */
    private class WorkGeometry {
        int index;
        //Geometry source;
        Geometry proj;
        
        WorkGeometry (final Geometry source) {
            this(source, false);
        }

        WorkGeometry (final Geometry source, final boolean isProj) {
            if (isProj) {
                this.proj = source;
            } else {
                try {
                    this.proj = trs.transform(source);
                } catch (TransformException ex) {
                    throw new RuntimeException(ex);
                }
            }            
        }
    }

    private class Cluster {
        Set<WorkGeometry> group;
        Geometry out;

        Cluster (final Set<WorkGeometry> group, Geometry o) {
            this.group = group;
            this.out = o;
        }

        Cluster (final WorkGeometry geom) {
            this.group = new HashSet<>();
            this.group.add(geom);
            this.out = geom.proj.buffer(tolerance);
        }

        boolean isIntersect(final Cluster clust) {
            for (WorkGeometry wg1: this.group) {
                for (WorkGeometry wg2: clust.group) {
                    if (roadmap[toFlat(wg1.index, wg2.index)] == 1) {
                        return true;
                    }
                }
            }
            return false;
        }

        Cluster merge(final Cluster clust) {
            final Set<WorkGeometry> result = new HashSet<>();
            result.addAll(this.group);
            result.addAll(clust.group);
            try {
                Geometry newout = OverlayOp.overlayOp(this.out, clust.out, OverlayOp.UNION);
                return new Cluster(result, newout);
            } catch (TopologyException ex) {
                PrecisionModel pm = new PrecisionModel(10.0);
                GeometryPrecisionReducer gpr = new GeometryPrecisionReducer(pm);
                Geometry rp1 = gpr.reduce(this.out);
                Geometry rp2 = gpr.reduce(clust.out);
                Geometry newout = OverlayOp.overlayOp(rp1, rp2, OverlayOp.UNION);
                return new Cluster(result, newout);
            }
        }
        
        Geometry getResult() {
            try {
                return inv.transform(this.out);
            } catch (TransformException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Compute the cluster hull from a feature set according to the measure of tolerance.
     *
     * @param inputFeatureSet input feature set
     * @param tolerance distance minimum between two cluster and buffer width
     * @param epsilon tolerance used to simplify/smoothing geometries
     * @param unit unit of tolerance and epsilon
     * @return the cluster hull
     */
    private FeatureSet computeClusterHull(final FeatureSet inputFeatureSet) throws ProcessException {
        try {
            Instant start = Instant.now();
            System.out.println(start.toString());
            // Extract geometries set from featureSet and smooth geometries with Douglas Peucker algorithm
            Set<WorkGeometry> wgeometries = extractWorkGeometrySetAndApplyDouglasPeucker(inputFeatureSet);
            
            System.out.println("Init roadmap");

            initRoadmap(wgeometries);
            
            System.out.println("cast to cluster");

            Set<Cluster> clusters = castToClusterSet(wgeometries);

            System.out.println("Apply the process");
            
            // Apply the process
            ApplyClusterHull(clusters);
            
            System.out.println("Build result");

            // Build feature set result
            final FeatureType type = inputFeatureSet.getType();
            CoordinateReferenceSystem crs = FeatureExt.getCRS(type);
            Instant end = Instant.now();
            System.out.println(end.toString());
            System.out.println(Duration.between(start, end));
            return toFeatureSet(this.clusters, crs);
        // Here OutOfMemoryError is catch cause input and treatment use a lot of memory
        } catch (DataStoreException | TransformException | OutOfMemoryError e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }
    
    private Set<Cluster> castToClusterSet(final Set<WorkGeometry> toCast) {
        Set<Cluster> result = new HashSet<>();

        for (WorkGeometry wg: toCast) {
            result.add(new Cluster(wg));
        }
        return result;
    }

    private void initRoadmap(final Set<WorkGeometry> geoms) {
        this.geomSize = geoms.size();
        int s2 = this.geomSize * this.geomSize;
        this.roadmap = new int[s2];

        for (WorkGeometry wg1: geoms) {
            for (WorkGeometry wg2: geoms) {
                fillCellRoadmap(wg1, wg2);
            }
        }
    }

    private void fillCellRoadmap(final WorkGeometry wg1, final WorkGeometry wg2) {
        final int i = wg1.index;
        final int j = wg2.index;
        if (i == j) return;
        final int flat = toFlat(i, j);
        final int revFlat = toFlat(j, i);

        if (isSameCluster(wg1, wg2)) {
            this.roadmap[flat] = 1;
            this.roadmap[revFlat] = 1;
        }
    }

    private int toFlat(final int i, final int j) {
        return i + j * this.geomSize;
    }

    private void initConversion(final Double tolerance, final Double epsilon, Unit<Length> unit) {
        // Convert the current tolerance and epsilon to meter according to the unit specified
        final UnitConverter uc = unit.getConverterTo(Units.METRE);
        this.tolerance = uc.convert((double)tolerance);
        if (epsilon != null) this.epsilon = uc.convert((double)epsilon);
    }

    private void initTransformation(final FeatureSet inputFeatureSet) throws ProcessException {
        // Initialise the GeometryCSTransformer (Lambert projection)
        try {
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
        } catch (FactoryException | DataStoreException | TransformException e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

    private Set<WorkGeometry> extractWorkGeometrySetAndApplyDouglasPeucker(final FeatureSet fs) throws DataStoreException, TransformException {
        String geomName = FeatureExt.getDefaultGeometry(fs.getType()).getName().toString();
        try (Stream<Feature> stream = fs.features(false)) {
            Set<WorkGeometry> geometries = stream
                    .map(f -> f.getPropertyValue(geomName))
                    .filter(value -> value instanceof Geometry)
                    .map(value -> {
                        WorkGeometry wgeom = new WorkGeometry((Geometry) value);
                        wgeom = customDouglasPeucker(wgeom);
                        return giveIndex(wgeom);
                    })
                    .collect(Collectors.toSet());
            return geometries;
        }
    }

    private WorkGeometry giveIndex(final WorkGeometry notIndexed) {
        notIndexed.index = this.increment;
        this.increment += 1;
        return notIndexed;
    }

    private WorkGeometry customDouglasPeucker(final WorkGeometry toSmooth) {
        if (this.epsilon == null) return toSmooth;
        final Geometry smoothed = DouglasPeuckerSimplifier.simplify(toSmooth.proj, this.epsilon);
        return new WorkGeometry(smoothed, true);
    }

    private Double[] getMedianPoint(final FeatureSet fs) throws DataStoreException {
        final Double[] median = {0.0, 0.0};
        final Envelope envelope = FeatureStoreUtilities.getEnvelope(fs,true);
        median[0] = (envelope.getMinimum(0) + envelope.getMaximum(0)) / 2;
        median[1] = (envelope.getMinimum(1) + envelope.getMaximum(1)) / 2;
        return median;
    }

    /**
     * On each recurrence:
     *      - Extract the first one cluster "first" from the "clustersRaw"
     *      - Iterate on "clusterRaw":
     *          - if current cluster intersect the cluster "first", merge them and break the loop.
     *          - if not, "first" is a cluster, apply buffer on it, send it to the final result.
     * This procedure is apply until the input set is empty.
     * @param clusterRaw set of workGeometry not yet clustered.
     * @throws TransformException
     */
    private void ApplyClusterHull(Set<Cluster> clustersRaw) throws TransformException {
        if (clustersRaw.isEmpty()) return;
        Cluster first = clustersRaw.iterator().next();
        clustersRaw.remove(first);
        Cluster merged = null;

        // find a geometry who are in a same cluster
        for (Cluster wg: clustersRaw) {
            // check if same cluster
            if (first.isIntersect(wg)) {
                merged = first.merge(wg);
                // update the element with the result of the merge
                clustersRaw.remove(wg);
                clustersRaw.add(merged);
                break;
            }
        }

        if (merged == null) {
            // first is a cluster on his own
            // apply buffer
            // add it within the global result
            set(first);
        }
        ApplyClusterHull(clustersRaw);
    }

    private boolean isSameCluster(final WorkGeometry wg1, final WorkGeometry wg2) {
        return wg1.proj.distance(wg2.proj) <= this.tolerance;
    }

    private void set(final Cluster clust) {
        this.clusters.add(clust.getResult());
    }

    private FeatureSet toFeatureSet(final Set<Geometry> geometries, final CoordinateReferenceSystem crs) throws DataStoreException {
        final InMemoryStore store = new InMemoryStore();
        final FeatureType type = createSimpleType(crs);
        List<Feature> features = new ArrayList<>();

        WritableFeatureSet resource = (WritableFeatureSet) store.add(new DefiningFeatureSet(type, null));
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
