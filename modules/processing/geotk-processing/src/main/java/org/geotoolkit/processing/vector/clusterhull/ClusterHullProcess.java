package org.geotoolkit.processing.vector.clusterhull;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.*;
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
    private static ArrayList<Unit<Length>> unitArrayList = new ArrayList<>();

    /**
     * Build the featureSet result
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
        unitArrayList.add(Units.METRE);
        unitArrayList.add(Units.KILOMETRE);
        unitArrayList.add(Units.STATUTE_MILE);
        unitArrayList.add(Units.NAUTICAL_MILE);
        unitArrayList.add(Units.INCH);
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
     * Compute the cluster hull from a feature collection with a measure of tolerance.
     *
     * @return the cluster hull
     */
    public FeatureSet computeClusterHull(final FeatureSet inputFeatureSet, final Double tolerance, Unit<Length> unit) {
        try {
            Double toMeter;
            if (!unitArrayList.contains(unit)) unit = Units.METRE; //TODO or throw error "unit not permitted"
            final UnitConverter uc = unit.getConverterTo(Units.METRE);
            toMeter = uc.convert((double)tolerance);
            initTransformation(inputFeatureSet);
            List<Geometry> result = new ArrayList<>();
            List<Geometry> geometries = extractGeometryListFromFeatureSet(inputFeatureSet);
            List<List<Geometry>> partition = applyGeometryPartitioning(geometries, toMeter);
            for (List<Geometry> part : partition) {
                result.add(applyConvexHullOnGeometryList(part));
            }
            FeatureSet clusterHull = createFeatureSetFromGeometryList(result);

            /*clusterHull.setSRID(SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1));*/
            return clusterHull;
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (DataStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initTransformation(final FeatureSet inputFeatureSet) throws FactoryException {
        Double[] median = getMedianPoint(inputFeatureSet);
        final CoordinateReferenceSystem crs1 = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem crs2 = getLocalLambertCRS(median[0], median[1]);
        final MathTransform mt = CRS.findOperation(crs1, crs2, null).getMathTransform();
        final CoordinateSequenceTransformer cst = new CoordinateSequenceMathTransformer(mt);
        trs = new GeometryCSTransformer(cst);
    }

    private Double customDistance(final Geometry geom1, final Geometry geom2) {
        try {
            Geometry trans1 = trs.transform(geom1);
            Geometry trans2 = trs.transform(geom2);

            return trans1.distance(trans2);
        } catch (TransformException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Geometry> extractGeometryListFromFeatureSet(final FeatureSet fs) {
        List<Geometry> geometries   = new ArrayList<Geometry>();

        try {
            geometries = fs.features(false)
                    .map(f -> f.getProperty("geometry"))
                    .map(p -> p.getValue())
                    .filter(value -> value instanceof Geometry)
                    .map(value -> (Geometry) value)
                    .collect(Collectors.toList());
        } catch(DataStoreException e) {
            e.printStackTrace();
        }
        return geometries;
    }

    private Double[] getMedianPoint(final FeatureSet fs) {
        Double[] median = {0.0, 0.0};
        try {
            Optional<Envelope> envelope = fs.features(false)
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
        } catch(DataStoreException e) {
            e.printStackTrace();
        }

        return median;
    }

    private List<List<Geometry>> applyGeometryPartitioning(final List<Geometry> geometries, final Double tolerance) {
        if (geometries.isEmpty()) return null;
        Map<Integer, List<Geometry>> partition    = new HashMap<>();
        Geometry firstGeom                         = geometries.get(0);

        // Init partition
        partition.put(1, new ArrayList<>());
        partition.get(1).add(firstGeom);

        // Sort geometry according to tolerance
        for(int i = 1; i < geometries.size(); i++) {
            Iterator it = partition.entrySet().iterator();
            boolean found = false;
            while (it.hasNext()) {
                Map.Entry<Integer, List<Geometry>> pair = (Map.Entry<Integer, List<Geometry>>)it.next();
                Integer part = pair.getKey();
                List<Geometry> current = pair.getValue();

                for(Geometry c: current) {
                    System.out.println(computeDistance.apply(geometries.get(i), c));
                    if (computeDistance.apply(geometries.get(i), c) <= tolerance) {
                        partition.get(part).add(geometries.get(i));
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                partition.put(partition.size() + 1, new ArrayList<>());
                partition.get(partition.size()).add(geometries.get(i));
            }
        }
        return new ArrayList(partition.values());
    }

    private Geometry applyConvexHullOnGeometryList(final List<Geometry> geometries) {
        Geometry convexHull = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        for(Geometry g: geometries) {
            convexHull = convexHull.union(g);
            convexHull = convexHull.convexHull();
        }
        return convexHull;
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

    private Feature createDefaultFeatureFromGeometry(final Geometry geometry, final FeatureType type) {
        final Feature feature = type.newInstance();

        feature.setPropertyValue("geom", geometryFactory.createGeometry(geometry));

        return feature;
    }

    private static FeatureType createSimpleType() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("Simple type");
        ftb.addAttribute(Geometry.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
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
