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
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.operation.union.UnaryUnionOp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.locationtech.jts.geom.Coordinate;


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

    /**
     * Used to build the featureSet result.
     */
    private static GeometryFactory GEOMETRY_FACTORY = org.geotoolkit.geometry.jts.JTS.getFactory();

    private int[][] roadmap;

    private List<WorkGeometry> geomList;

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
        Set<Integer> group;
        Geometry out;

        Cluster (final Integer index) throws ProcessException {
            this.group = new HashSet<>();
            this.group.add(index);
            this.out = customBuffer(geomList.get(index).proj);
        }

        Cluster (final Set<Integer> group, final Geometry out) {
            this.group = group;
            this.out = out;
        }

        boolean isIntersect(final Cluster clust) {
            for (int i1: this.group) {
                for (int i2: clust.group) {
                    if (computeDistance(i1, i2)) {
                        return true;
                    }
                }
            }
            return false;
        }

        boolean computeDistance(final int i1, final int i2) {
            WorkGeometry g1 = geomList.get(i1);
            WorkGeometry g2 = geomList.get(i2);

            // mémoïsation of distance calculation
            if (roadmap[i1][i2] == -1) {
                if (isSameCluster(g1, g2)) {
                    roadmap[i1][i2] = 1;
                    roadmap[i2][i1] = 1;
                } else {
                    roadmap[i1][i2] = 0;
                    roadmap[i2][i1] = 0;
                }
            }
            return roadmap[i1][i2] == 1;
        }

        Cluster merge(final Cluster clust) {
            final Set<Integer> groups = new HashSet<>();
            final Set<Geometry> united = new HashSet<>();

            groups.addAll(this.group);
            groups.addAll(clust.group);

            united.add(this.out);
            united.add(clust.out);

            return new Cluster(groups, UnaryUnionOp.union(united));
        }

        Geometry getResult() {
            // retrieve geospatial coordinates
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
            // Extract geometries set from featureSet and smooth geometries with Douglas Peucker algorithm
            extractAndFormat(inputFeatureSet);

            // Compute all distances between WorkingGeometry, and keep if they are under the tolerance distance
            initRoadmap();

            // Apply the process
            ApplyClusterHull(initCluster());

            // Build feature set result
            final FeatureType type = inputFeatureSet.getType();
            CoordinateReferenceSystem crs = FeatureExt.getCRS(type);
            return toFeatureSet(this.clusters, crs);
        // Here OutOfMemoryError is catch cause input and treatment use a lot of memory
        } catch (DataStoreException | TransformException | OutOfMemoryError e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

    private Set<Cluster> initCluster() throws ProcessException {
        Set<Cluster> clusters = new HashSet<>();

        for (int i = 0; i < this.geomList.size(); i++) {
            this.stopIfDismissed();
            clusters.add(new Cluster(i));
        }
        return clusters;
    }

    private void initRoadmap() {
        // fill the roadmap with the value -1
        int len = this.geomList.size();
        this.roadmap = new int[len][len];
        for (int i = 0; i < len; i++) {
            Arrays.fill(roadmap[i], -1);
        }
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

    private void extractAndFormat(final FeatureSet fs) throws DataStoreException, TransformException {
        String geomName = FeatureExt.getDefaultGeometry(fs.getType()).getName().toString();
        try (Stream<Feature> stream = fs.features(false)) {
            Stream<WorkGeometry> tmp = stream
                    .map(f -> f.getPropertyValue(geomName))
                    .filter(value -> value instanceof Geometry)
                    .map(value -> (Geometry) value)
                    .map(WorkGeometry::new);
            if (epsilon != null && epsilon > 1e-7) {
                tmp = tmp.map(wg -> this.douglasPeucker(wg, epsilon));
            }
            this.geomList = tmp.collect(Collectors.toList());
        }
    }

    /**
     * It seems that the jts operation buffer is very expensive in memory when it is applied to large geometries.
     * In order too reduce memory used, if a geometry contains at least one thousand coordinates, it is roughly
     * splitted, buffered and merged together, while preserving the original topology.
     *
     * @param geom
     * @return
     * @throws ProcessException
     */
    private Geometry customBuffer(Geometry geom) throws ProcessException {
        if (geom.getCoordinates().length < 1000) {
            return geom.buffer(tolerance, 4);
        } else {
            if (geom instanceof GeometryCollection) {
                return this.customBufferCollection((GeometryCollection) geom);
            } else {
                return this.customBufferSimple(geom);
            }
        }
    }

    private Geometry customBufferCollection(GeometryCollection collection) throws ProcessException {
        Geometry result = GEOMETRY_FACTORY.createEmpty(2);

        for (int i = 0; i < collection.getNumGeometries(); i++) {
            this.stopIfDismissed();
            Geometry g = collection.getGeometryN(i);
            Geometry buff = g.getCoordinates().length < 1000 ? g.buffer(tolerance, 4) : this.customBufferSimple(g);

            // union
            final List<Geometry> u = new ArrayList<>();
            u.add(result);
            u.add(buff);
            result = UnaryUnionOp.union(u);
        }
        return result;
    }

    private Geometry customBufferSimple(Geometry geom) throws ProcessException {
        if (geom instanceof Point) {
            return geom.buffer(tolerance, 4);
        } else if (geom instanceof LineString) {
            return this.bufferLargeLineString((LineString) geom);
        } else if (geom instanceof Polygon) {
            return geom.buffer(tolerance, 4); // Not supported yet
        } else {
            throw new ProcessException("Unsupported geometry type for custom buffer operation: " + geom.getGeometryType(), this);
        }
    }

    private Geometry bufferLargeLineString(LineString tooLarge) {
        final Coordinate[] coords = tooLarge.getCoordinates();
        // first fragment
        final Coordinate[] ff = new Coordinate[]{coords[0], coords[1]};
        Geometry r = GEOMETRY_FACTORY.createLineString(ff).buffer(tolerance, 4);

        for (int i = 1; i < coords.length - 1; i++) {
            final Coordinate[] fragment = new Coordinate[]{ coords[i], coords[i + 1] };
            final Geometry buff = GEOMETRY_FACTORY.createLineString(fragment).buffer(tolerance, 4);

            // union
            final List<Geometry> u = new ArrayList<>();
            u.add(r);
            u.add(buff);
            r = UnaryUnionOp.union(u);
        }
        return r;
    }

    private WorkGeometry douglasPeucker(final WorkGeometry toSmooth, final double eps) {
        final Geometry smoothed = DouglasPeuckerSimplifier.simplify(toSmooth.proj, eps);
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
    private void ApplyClusterHull(Set<Cluster> clustersRaw) throws TransformException, ProcessException {
        if (clustersRaw.isEmpty()) return;
        Cluster first = clustersRaw.iterator().next();
        clustersRaw.remove(first);
        Cluster merged = null;

        // find a geometry who are in a same cluster
        for (Cluster wg: clustersRaw) {
            this.stopIfDismissed();
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
        final FeatureType type = createSimpleType(crs);
        final List<Feature> features = new ArrayList<>();
        for (Geometry geometry: geometries) {
            features.add(createDefaultFeatureFromGeometry(geometry, type));
        }

        return new InMemoryFeatureSet(type, features);
    }

    private Feature createDefaultFeatureFromGeometry(final Geometry geometry, final FeatureType type) {
        final Feature feature = type.newInstance();

        feature.setPropertyValue("geometry", GEOMETRY_FACTORY.createGeometry(geometry));
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
