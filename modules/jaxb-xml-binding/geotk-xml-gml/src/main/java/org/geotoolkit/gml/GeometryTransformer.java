package org.geotoolkit.gml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.xml.AbstractCurveSegment;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractRing;
import org.geotoolkit.gml.xml.AbstractRingProperty;
import org.geotoolkit.gml.xml.AbstractSurface;
import org.geotoolkit.gml.xml.Coordinates;
import org.geotoolkit.gml.xml.Curve;
import org.geotoolkit.gml.xml.CurveProperty;
import org.geotoolkit.gml.xml.CurveSegmentArrayProperty;
import org.geotoolkit.gml.xml.DirectPosition;
import org.geotoolkit.gml.xml.DirectPositionList;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.GeometryProperty;
import org.geotoolkit.gml.xml.LineStringProperty;
import org.geotoolkit.gml.xml.MultiCurve;
import org.geotoolkit.gml.xml.MultiGeometry;
import org.geotoolkit.gml.xml.MultiSurface;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.gml.xml.PointProperty;
import org.geotoolkit.gml.xml.PolygonProperty;
import org.geotoolkit.gml.xml.Ring;
import org.geotoolkit.gml.xml.SurfaceProperty;
import org.geotoolkit.gml.xml.WithCoordinates;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Note : NOT THREAD-SAFE !
 * @author Alexis Manin (Geomatys)
 */
public class GeometryTransformer implements Supplier<Geometry> {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gml");

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * Keep previously decoded CRS in memory. We do so because it's a heavy
     * operation, and it could be used a lot when creating a geometry collection.
     */
    private static final Cache<Map.Entry<String, Boolean>, CoordinateReferenceSystem> CRS_CACHE = new Cache<>(12, 0, false);

    /**
     * The GML geometry to transform to JTS API.
     */
    private final AbstractGeometry source;

    /**
     * The GML transformer from which we've been created. It allows to read
     * information from upper GML level. A concrete use-case is for MultiGeometries.
     * The MultiGeometry markup can define attributes (srsName, srsDimension, etc.)
     * which won't be repeated in inner geometries. So, when reading a geometry
     * composing a collection, we need to access parent attributes, and can do
     * so from the current transformer. For practical use-case, see {@link #getCoordinateDimension() }.
     */
    private final GeometryTransformer parent;

    /**
     * A parameter which can be set by user to force {@link AxesConvention#RIGHT_HANDED}
     * convention on referencing information. If null, parameter has not been set
     * and we should check parent configuration. For more information, see {@link #isLongitudeFirst() }.
     */
    Boolean isLongitudeFirst;

    /**
     * Prepare conversion of a GML geometry.
     * @param source The geometry to convert to JTS.
     */
    public GeometryTransformer(final AbstractGeometry source) {
        this(source, null);
    }

    /**
     * Prepare conversion of a GML geometry.
     * @param source The geometry to convert to JTS.
     * @param parent The transformer from which this one is created. It's useful
     * to access information (like srs name or dimension) from an higher-level
     * geometry.
     */
    public GeometryTransformer(final AbstractGeometry source, final GeometryTransformer parent) {
        ArgumentChecks.ensureNonNull("GML Geometry to convert", source);
        this.source = source;
        this.parent = parent;
    }

    /**
     * Procede to conversion from GML to JTS.
     * @return Created JTS geometry. Never null, but can be an emptry geometry.
     * @throws UnconvertibleObjectException If we don't know how to process
     * source GML geometry.
     */
    @Override
    public Geometry get() throws UnconvertibleObjectException {
        /*
         * SIMPLE CASES
         */
        if (source instanceof org.geotoolkit.gml.xml.Point) {
            return accumulateAndBuild(coords -> (coords.length > 0 ? GF.createPoint(coords[0]) : GF.createPoint((Coordinate) null)));
        } else if (source instanceof org.geotoolkit.gml.xml.LineString) {
            return accumulateAndBuild(GF::createLineString);
        } else if (source instanceof org.geotoolkit.gml.xml.LinearRing) {
            return accumulateAndBuild(GF::createLinearRing);
        } else if (source instanceof Curve) {
            return convertCurve((Curve) source);
        } else if (source instanceof Envelope) {
            return convertEnvelope((Envelope) source);

            /*
             * COMPOSED GEOMETRIES
             */
        } else if (source instanceof org.geotoolkit.gml.xml.Ring) {
            return convertRing((Ring) source);
        } else if (source instanceof org.geotoolkit.gml.xml.Polygon) {
            return convertPolygon((org.geotoolkit.gml.xml.Polygon) source);
        } else if (source instanceof AbstractSurface) {
            // TODO : complex case

            /*
             * GEOMETRY COLLECTIONS
             */
        } else if (source instanceof org.geotoolkit.gml.xml.MultiPoint) {
            return convertMultiPoint((org.geotoolkit.gml.xml.MultiPoint) source);
        } else if (source instanceof org.geotoolkit.gml.xml.MultiLineString) {
            return convertMultiLineString((org.geotoolkit.gml.xml.MultiLineString) source);
        } else if (source instanceof MultiCurve) {
            return convertMultiCurve((MultiCurve) source);
        } else if (source instanceof org.geotoolkit.gml.xml.MultiPolygon) {
            return convertMultiPolygon((org.geotoolkit.gml.xml.MultiPolygon) source);
        } else if (source instanceof MultiSurface) {
            return convertMultiSurface((MultiSurface) source);
        } else if (source instanceof MultiGeometry) {
            return convertMultiGeometry((MultiGeometry) source);
        }

        throw new IllegalArgumentException("Unssupported geometry type : " + source.getClass());
    }

    /**
     * Add {@link #source} geometry points into given list.
     * @param target The list to fill with coordinates from prepared GML geometry.
     * @param checkFirst If the given list is not empty and this parameter is set
     * to true, we'll ensure first read point is not equals to given list last point.
     * If they're equal, we'll evict the doublon.
     */
    protected void accumulate(final List<Coordinate> target, boolean checkFirst) {
        final Spliterator<Coordinate> it = getCoordinates(source);

        if (checkFirst && !target.isEmpty()) {
            it.tryAdvance(coord -> {
                if (!target.get(target.size() - 1).equals(coord)) {
                    target.add(coord);
                }
            });
        }

        boolean advanced;
        do {
            advanced = it.tryAdvance(target::add);
        } while (advanced);
    }

    /**
     * Check that given geometry is a primitive geometry (implements {@link WithCoordinates}), and
     * get its points.
     * @param source The geometry to extract points from.
     * @return Found points, never null, but can be empty.
     * @throws UnconvertibleObjectException If the given geometry does not implement {@link WithCoordinates}.
     */
    private Spliterator<Coordinate> getCoordinates(final AbstractGeometry source) throws UnconvertibleObjectException {
        List<Double> values = null;
        if (source instanceof WithCoordinates) {
            final Coordinates coords = ((WithCoordinates) source).getCoordinates();
            if (coords != null) {
                /* HACK : In GML 3, coordinates are just a list of decimal values.
                 * The grouping by coordinate is done using "srsDimension" attribute
                 * on parent geometry type. However, with GML 2, there's another
                 * possibility : Coordinates use two distinct separators : one for
                 * decimal value separation, and another for coordinate separation.
                 * To manage both ways, we first check if coordinates object has
                 * succeeded in splitting underlying decimals in coordinates. If
                 * it does (return arrays with more than one element), we use
                 * that approach. Otherwise, we fallback on standard way which
                 * will try to determine manually the number of dimensions.
                 */
                Iterator<double[]> it = coords.points().iterator();
                if (it.hasNext() && it.next().length > 1) {
                    return coords.points().map(GeometryTransformer::toCoordinate).spliterator();
                } else {
                    values = coords.getValues();
                }

            } else if (source instanceof Point) {
                DirectPosition dp = ((Point) source).getPos();
                if (dp != null) {
                    return Stream.of(convertDirectPosition(dp)).spliterator();
                } else return Spliterators.emptySpliterator(); // recognized object, but no value. Empty geometry
            }
        }

        if (values == null) {
            if (source instanceof org.geotoolkit.gml.xml.LineString) {
                final org.geotoolkit.gml.xml.LineString ls = (org.geotoolkit.gml.xml.LineString) (source);
                final DirectPositionList posList = ls.getPosList();
                if (posList != null) {
                    values = posList.getValue();
                } else if (ls.getPos() != null) {
                    return ls.getPos().stream()
                            .map(GeometryTransformer::convertDirectPosition)
                            .filter(Objects::nonNull)
                            .spliterator();
                } else {
                    values = Collections.EMPTY_LIST;// We've identified a line, but there's no data in it
                }
            } else if (source instanceof org.geotoolkit.gml.xml.LinearRing) {
                final org.geotoolkit.gml.xml.LinearRing lr = (org.geotoolkit.gml.xml.LinearRing) source;
                final DirectPositionList posList = lr.getPosList();
                if (posList != null) {
                    values = posList.getValue();
                } else {
                    values = Collections.EMPTY_LIST;// We've identified a ring, but there's no data in it
                }
            }
        }

        if (values != null) {
            if (values.isEmpty()) {
                return Spliterators.emptySpliterator();
            }
            return new CoordinateSpliterator(values, getCoordinateDimension());
        }

        throw new UnconvertibleObjectException("Cannot extract coordinates from source geometry.");
    }

    private static Coordinate convertDirectPosition(final org.opengis.geometry.DirectPosition dp) {
        return toCoordinate(dp.getCoordinate());
    }

    private static Coordinate toCoordinate(final double[] values) {
        if (values.length <= 0) {
            return null;
        } else if (values.length == 2) {
            return new Coordinate(values[0], values[1]);
        } else if (values.length == 3) {
            return new Coordinate(values[0], values[1], values[2]);

        } else throw new UnconvertibleObjectException("Only 2D and 3D positions accepted, but received dimension: "+values.length);
    }

    protected int getCoordinateDimension() {
        // Special case : if source is a point, we do not need the srsDimension attribute.
        // The point dimension is equal to the number of dimensions in it.
        if (source instanceof Point) {
            Point pt = (Point) source;
            int dim = 0;
            if (pt.getCoordinates() != null)
                dim = pt.getCoordinates().getValues().size();
            else if (pt.getPos() != null) {
                dim = pt.getPos().getValue().size();
            }

            if (dim > 0)
                return dim; // otherwise, fallback on common analysis.
        }

        return familyTree()
                .map(gt -> gt.source.getSrsDimension())
                .filter(dim -> dim != null && dim > 0)
                .findFirst()
                // Default value. We assume we've got a 2D geometry, because it's 95% of encountered cases.
                .orElseGet(() -> {
                    LOGGER.fine("Arbitrary choice: GML geometry does not define any \"srsDimension\" attribute. Fallback on 2D.");
                    return 2;
                });
    }

    protected Optional<String> getSrsName() {
        return familyTree()
                .map(gt -> gt.source.getSrsName())
                .filter(Objects::nonNull)
                .findFirst();
    }

    public boolean isLongitudeFirst() {
        return familyTree()
                .map(gt -> gt.isLongitudeFirst)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(false);
    }

    public void setLongitudeFirst(final Boolean forceLongitudeFirst) {
        isLongitudeFirst = forceLongitudeFirst;
    }

    protected Stream<GeometryTransformer> familyTree() {
        final Stream<GeometryTransformer> self = Stream.of(this);
        if (parent == null)
            return self;
        else return Stream.concat(self, parent.familyTree());
    }

    /**
     * Affect referencing system on given geometry. CRS is discovered from the
     * source GML information (see {@link AbstractGeometry#getSrsName() }).
     *
     * @param target The geometry to put a CRS for.
     */
    protected void applyCRS(final Geometry target) {
        getSrsName()
                .map(this::findCRS)
                .ifPresent(crs -> JTS.setCRS(target, crs));
    }

    protected CoordinateReferenceSystem findCRS(final String srsName) {
        final boolean longitudeFirst = isLongitudeFirst();
        try {
            return CRS_CACHE.getOrCreate(new AbstractMap.SimpleImmutableEntry<>(srsName, longitudeFirst), () -> GeometryTransformer.loadCRS(srsName, longitudeFirst));
        } catch (Exception ex) {
            throw new UnconvertibleObjectException("Referencing information cannot be read."+ex.getMessage(), ex);
        }
    }

    private static CoordinateReferenceSystem loadCRS(final String name, final boolean longitudeFirst) {
        CoordinateReferenceSystem crs;
        try {
            try {
                crs = CRS.forCode(name);
            } catch (NoSuchAuthorityCodeException e) {
                // HACK : sometimes, we've got a malformed URN of the form urn:...EPSG:CODE
                // instead of urn:...EPSG:VERSION:CODE or urn:...EPSG::CODE
                final Matcher matcher = Pattern.compile("\\w+:\\d+$").matcher(name);
                if (matcher.find()) {
                    crs = CRS.forCode(matcher.group());
                } else {
                    throw e;
                }
            }

        } catch (FactoryException ex) {
            throw new UnconvertibleObjectException("Impossible to find a coordinate reference system for code " + name, ex);
        }

        if (longitudeFirst) {
            crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
        }

        return crs;
    }

    private Polygon convertEnvelope(final Envelope source) {
        final com.vividsolutions.jts.geom.Envelope e = new com.vividsolutions.jts.geom.Envelope(
                convertDirectPosition(source.getLowerCorner()),
                convertDirectPosition(source.getUpperCorner())
        );

        final Polygon geom = JTS.toGeometry(e);
        applyCRS(geom);
        return geom;
    }

    private LinearRing convertRing(final Ring source) {
        final List<Coordinate> coords = extractCurves(source.getCurveMember().stream())
                .reduce(new ArrayList<>(), (target, gt) -> {
                    gt.accumulate(target, true);
                    return target;
                }, null);
        final LinearRing ring = GF.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
        applyCRS(ring);

        return ring;
    }

    private Stream<GeometryTransformer> extractCurves(Stream<? extends CurveProperty> curves) {
        return curves
                .map(CurveProperty::getAbstractCurve)
                .map(ac -> {
                    if (ac instanceof AbstractGeometry) {
                        return new GeometryTransformer((AbstractGeometry) ac, this);
                    } else {
                        throw new UnconvertibleObjectException("Geometry type not supported yet: " + ac.getClass());
                    }
                });
    }

    private Polygon convertPolygon(final org.geotoolkit.gml.xml.Polygon polygon) {
        final AbstractRing exterior = polygon.getExterior().getAbstractRing();
        final Geometry extRing = new GeometryTransformer(exterior, this).get();
        // Check now to avoid costly parsing of interior geometries
        if (!(extRing instanceof LinearRing)) {
            throw new UnconvertibleObjectException("Cannot create a polygon, because its exterior is not a ring");
        }

        final LinearRing[] interiors;
        try {
            interiors = polygon.getInterior().stream()
                    .map(AbstractRingProperty::getAbstractRing)
                    .map(ring -> new GeometryTransformer(ring, this).get())
                    .map(LinearRing.class::cast)
                    .toArray(size -> new LinearRing[size]);
        } catch (ClassCastException e) {
            throw new UnconvertibleObjectException("Cannot create a polygon, because some of its interior geometries are not rings", e);
        }

        final Polygon poly = GF.createPolygon((LinearRing) extRing, interiors);
        applyCRS(poly);
        return poly;
    }

    private MultiLineString convertCurve(final Curve mc) {
        CurveSegmentArrayProperty segments = mc.getSegments();
        final List<LineString> lines = new ArrayList<>();
        for (AbstractCurveSegment cs : segments.getAbstractCurveSegment()) {

            final DirectPositionList posList;
            if (cs instanceof org.geotoolkit.gml.xml.v321.GeodesicStringType) {
                posList = ((org.geotoolkit.gml.xml.v321.GeodesicStringType)cs).getPosList();
            } else if (cs instanceof org.geotoolkit.gml.xml.v311.GeodesicStringType) {
                posList = ((org.geotoolkit.gml.xml.v311.GeodesicStringType)cs).getPosList();
            } else {
                throw new UnconvertibleObjectException("Unsupported curve segment "+cs.getClass());
            }

            final List<Double> values = posList.getValue();
            final Coordinate[] coordinates = new Coordinate[values.size()/2];
            for (int i=0,x=0; i<coordinates.length; i++) {
                coordinates[i] = new Coordinate(values.get(x++), values.get(x++));
            }

            lines.add(GF.createLineString(coordinates));
        }

        final MultiLineString mls = GF.createMultiLineString(lines.toArray(new LineString[lines.size()]));
        applyCRS(mls);
        return mls;
    }

    private MultiPoint convertMultiPoint(final org.geotoolkit.gml.xml.MultiPoint mp) {
        final List<Coordinate> points = new ArrayList<>();
        for (final PointProperty pt : mp.getPointMember()) {
            new GeometryTransformer(pt.getPoint(), this).accumulate(points, false);
        }

        final MultiPoint result = GF.createMultiPoint(points.toArray(new Coordinate[points.size()]));
        applyCRS(result);
        return result;
    }

    private MultiLineString convertMultiLineString(final org.geotoolkit.gml.xml.MultiLineString mls) {
        final LineString[] lss;
        try {
            lss = mls.getLineStringMember().stream()
                    .map(LineStringProperty::getLineString)
                    .map(ls -> new GeometryTransformer(ls, this).get())
                    .map(LineString.class::cast)
                    .toArray(size -> new LineString[size]);
        } catch (ClassCastException e) {
            throw new UnconvertibleObjectException("Cannot create a multi-line string, because some of its components are not lines", e);
        }

        final MultiLineString result = GF.createMultiLineString(lss);
        applyCRS(result);
        return result;
    }

    private MultiLineString convertMultiCurve(final MultiCurve mc) {
        final LineString[] lines;
        try {
            lines = extractCurves(mc.getCurveMember().stream())
                    .map(GeometryTransformer::get)
                    .map(LineString.class::cast)
                    .toArray(size -> new LineString[size]);
        } catch (ClassCastException e) {
            throw new UnconvertibleObjectException("Cannot create a multi-curve, because some of its components are not lines", e);
        }

        final MultiLineString mls = GF.createMultiLineString(lines);
        applyCRS(mls);
        return mls;
    }

    private MultiPolygon convertMultiPolygon(final org.geotoolkit.gml.xml.MultiPolygon mp) {
        Polygon[] polys;
        try {
            polys = mp.getPolygonMember().stream()
                    .map(PolygonProperty::getPolygon)
                    .map(p -> new GeometryTransformer(p, this).get())
                    .map(Polygon.class::cast)
                    .toArray(size -> new Polygon[size]);
        } catch (ClassCastException e) {
            throw new UnconvertibleObjectException("Cannot create a multi-polygon, because some of its components are not polygons", e);
        }

        final MultiPolygon result = GF.createMultiPolygon(polys);
        applyCRS(result);
        return result;
    }

    private MultiPolygon convertMultiSurface(final MultiSurface mp) {
        Polygon[] polys;
        try {
            polys = mp.getSurfaceMember().stream()
                    .map(SurfaceProperty::getAbstractSurface)
                    .map(surface -> {
                        if (surface instanceof AbstractGeometry) {
                            return (AbstractGeometry) surface;
                        }
                        throw new UnconvertibleObjectException("We cannot convert a non-geometric object: " + surface.getClass());
                    })
                    .map(p -> new GeometryTransformer(p, this).get())
                    .map(Polygon.class::cast)
                    .toArray(size -> new Polygon[size]);
        } catch (ClassCastException e) {
            throw new UnconvertibleObjectException("Cannot create a multi-surface, because some of its components are not polygons", e);
        }

        final MultiPolygon result = GF.createMultiPolygon(polys);
        applyCRS(result);
        return result;
    }

    private GeometryCollection convertMultiGeometry(MultiGeometry mg) {
        Geometry[] geometries = mg.getGeometryMember().stream()
                .map(GeometryProperty::getAbstractGeometry)
                .map(geom -> new GeometryTransformer(geom, this).get())
                .toArray(size -> new Geometry[size]);

        final GeometryCollection result = GF.createGeometryCollection(geometries);
        applyCRS(result);
        return result;
    }

    /**
     * Extract points in source GML geometry, then send them to a function which
     * role is to create a JTS geometry from them. Note : we'll try to affect a
     * CRS to created JTS geometry. The CRS is found using information of
     * current geometry transformer.
     *
     * @param <U> Type of JTS geometry to return.
     * @param builder The function designed to create JTS geometry.
     * @return JTS geometry built by input function.
     */
    protected <U extends Geometry> U accumulateAndBuild(final Function<Coordinate[], U> builder) {
        final ArrayList<Coordinate> coords = new ArrayList<>();
        accumulate(coords, false);

        final U jtsGeometry = builder.apply(coords.toArray(new Coordinate[coords.size()]));
        applyCRS(jtsGeometry);
        return jtsGeometry;
    }

    /**
     * Provide a view of a list of numbers as coordinates of queried dimension.
     * Note that for now, only 2D/3D objects are managed.
     */
    protected static class CoordinateSpliterator implements Spliterator<Coordinate> {

        private final List<? extends Number> source;
        private final int dimension;

        private int idx = 0;

        /**
         *
         * @param source List to extract coordinates from.
         * @param dimension Dimension of coordinates to return, either 2 or 3.
         */
        public CoordinateSpliterator(List<? extends Number> source, int dimension) {
            ArgumentChecks.ensureNonNull("Source data", source);
            ArgumentChecks.ensureSizeBetween("Batch size", 2, 3, dimension);
            if (source.size() % dimension != 0) {
                throw new IllegalArgumentException(String.format(
                        "Source list size is not a multiple of queried dimension.%nSource size: %d%nQueried dimension: %d",
                        source.size(), dimension
                ));
            }
            this.source = source;
            this.dimension = dimension;
        }

        @Override
        public Spliterator<Coordinate> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return (source.size() - idx) / dimension;
        }

        @Override
        public int characteristics() {
            return IMMUTABLE | ORDERED |SIZED;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Coordinate> action) {
            if (idx > source.size() - dimension) {
                return false;
            }

            final Coordinate c;
            switch (dimension) {
                case 2:
                    c = new Coordinate(source.get(idx++).doubleValue(), source.get(idx++).doubleValue());
                    break;
                case 3:
                    c = new Coordinate(source.get(idx++).doubleValue(), source.get(idx++).doubleValue(), source.get(idx++).doubleValue());
                    break;
                default: throw new IllegalStateException("Dimension value is invalid");
            }

            action.accept(c);
            return true;
        }
    }
}
