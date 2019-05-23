/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.geometry.jts;

import java.awt.Shape;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceTransformer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.GeodeticCalculator;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.util.Classes;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.factory.HintsPending;
import org.apache.sis.util.ArgumentChecks;

import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.algorithm.Orientation;
import java.awt.Rectangle;
import javax.vecmath.Vector3d;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;


/**
 * JTS Geometry utility methods, bringing GeotoolKit to JTS. <p> Offers
 * GeotoolKit based services such as reprojection. <p> Responsibilities: <ul>
 * <li>transformation</li> <li>coordinate sequence editing</li> <li>common
 * coordinate sequence implementations for specific uses</li> </ul>
 *
 * @module
 * @since 2.2
 * @author Jody Garnett
 * @author Martin Desruisseaux
 * @author Simone Giannecchini, GeoSolutions
 * @author Quentin Boileau (Geomatys).
 */
public final class JTS {
    /**
     * A pool of direct positions for use in {@link #orthodromicDistance}.
     */
    private static final GeneralDirectPosition[] POSITIONS = new GeneralDirectPosition[4];

    static {
        for (int i = 0; i < POSITIONS.length; i++) {
            POSITIONS[i] = new GeneralDirectPosition(i);
        }
    }

    /**
     * Geodetic calculators already created for a given coordinate reference
     * system. For use in {@link #orthodromicDistance}.
     *
     * Note: We would like to use
     * {@link org.geotoolkit.util.collection.CanonicalSet}, but we can't because
     * {@link GeodeticCalculator} keep a reference to the CRS which is used as
     * the key.
     */
    private static final Map<CoordinateReferenceSystem, GeodeticCalculator> CALCULATORS = new HashMap<CoordinateReferenceSystem, GeodeticCalculator>();

    /**
     * Do not allow instantiation of this class.
     */
    private JTS() {
    }

    /**
     * Makes sure that an argument is non-null.
     *
     * @param name Argument name.
     * @param object User argument.
     * @throws IllegalArgumentException if {@code object} is null.
     */
    private static void ensureNonNull(final String name, final Object object)
            throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NullArgument_1, name));
        }
    }

    /**
     * Check that the first and last points have the same 2d coordinate. If not,
     * override the last coordinate value to close the ring.
     */
    public static void ensureClosed(final Coordinate[] array) {
        if (!array[0].equals2D(array[array.length - 1])) {
            array[array.length - 1].setCoordinate(array[0]);
        }
    }

    /**
     * Transforms the envelope using the specified math transform. Note that
     * this method can not handle the case where the envelope contains the North
     * or South pole, or when it cross the &plusmn;180ï¿½ longitude, because {@linkplain MathTransform
     * math transforms} do not carry suffisient informations.
     *
     * @param envelope The envelope to transform.
     * @param transform The transform to use.
     * @return The transformed Envelope
     * @throws TransformException if at least one coordinate can't be
     * transformed.
     */
    public static Envelope transform(final Envelope envelope, final MathTransform transform)
            throws TransformException {
        return transform(envelope, null, transform, 5);
    }

    /**
     * Transforms the densified envelope using the specified math transform. The
     * envelope is densified (extra points put around the outside edge) to
     * provide a better new envelope for high deformed situations. <p> If an
     * optional target envelope is provided, this envelope will be
     * {@linkplain Envelope#expandToInclude expanded} with the transformation
     * result. It will <strong>not</strong> be
     * {@linkplain Envelope#setToNull nullified} before the expansion. <p> Note
     * that this method can not handle the case where the envelope contains the
     * North or South pole, or when it cross the &plusmn;180ï¿½ longitude,
     * because {@linkplain MathTransform
     * math transforms} do not carry suffisient informations.
     *
     * @param sourceEnvelope The envelope to transform.
     * @param targetEnvelope An envelope to expand with the transformation
     * result, or {@code null} for returning an new envelope.
     * @param transform The transform to use.
     * @param npoints Densification of each side of the rectange.
     * @return {@code targetEnvelope} if it was non-null, or a new envelope
     * otherwise. In all case, the returned envelope fully contains the
     * transformed envelope.
     * @throws TransformException if a coordinate can't be transformed.
     */
    public static Envelope transform(final Envelope sourceEnvelope, Envelope targetEnvelope,
            final MathTransform transform, int npoints) throws TransformException {
        ensureNonNull("sourceEnvelope", sourceEnvelope);
        ensureNonNull("transform", transform);

        if ((transform.getSourceDimensions() != 2) || (transform.getTargetDimensions() != 2)) {
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.IllegalTransformForType_1,
                    Classes.getShortClassName(transform)));
        }

        npoints++; // for the starting point.

        final double[] coordinates = new double[(4 * npoints) * 2];
        final double xmin = sourceEnvelope.getMinX();
        final double xmax = sourceEnvelope.getMaxX();
        final double ymin = sourceEnvelope.getMinY();
        final double ymax = sourceEnvelope.getMaxY();
        final double scaleX = (xmax - xmin) / npoints;
        final double scaleY = (ymax - ymin) / npoints;

        int offset = 0;

        for (int t = 0; t < npoints; t++) {
            final double dx = scaleX * t;
            final double dy = scaleY * t;
            coordinates[offset++] = xmin; // Left side, increasing toward top.
            coordinates[offset++] = ymin + dy;
            coordinates[offset++] = xmin + dx; // Top side, increasing toward right.
            coordinates[offset++] = ymax;
            coordinates[offset++] = xmax; // Right side, increasing toward bottom.
            coordinates[offset++] = ymax - dy;
            coordinates[offset++] = xmax - dx; // Bottom side, increasing toward left.
            coordinates[offset++] = ymin;
        }
        assert offset == coordinates.length;
        xform(transform, coordinates, coordinates);

        // Now find the min/max of the result
        if (targetEnvelope == null) {
            targetEnvelope = new Envelope();
        }
        for (int t = 0; t < offset;) {
            targetEnvelope.expandToInclude(coordinates[t++], coordinates[t++]);
        }
        return targetEnvelope;
    }

    /**
     * Transforms the geometry to given crs.
     * Id the geometry has no crs, it is assumed to be already in the given crs.
     *
     * @param geom The geom to transform
     * @param crs target crs.
     * @return the transformed geometry. It will be a new geometry.
     * @throws MismatchedDimensionException if the geometry doesn't have the
     * expected dimension for the specified transform.
     * @throws TransformException if a point can't be transformed.
     * @throws org.opengis.util.FactoryException
     */
    public static Geometry transform(final Geometry geom, final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException, TransformException, FactoryException {
        ArgumentChecks.ensureNonNull("crs", crs);
        final CoordinateReferenceSystem geomCrs = findCoordinateReferenceSystem(geom);
        if(geomCrs==null) return geom;
        final MathTransform trs = CRS.findOperation(geomCrs, crs, null).getMathTransform();
        final Geometry result = transform(geom, trs);
        setCRS(result, crs);
        return result;
    }

    /**
     * Transforms the geometry using the default transformer.
     *
     * @param geom The geom to transform
     * @param transform the transform to use during the transformation.
     * @return the transformed geometry. It will be a new geometry.
     * @throws MismatchedDimensionException if the geometry doesn't have the
     * expected dimension for the specified transform.
     * @throws TransformException if a point can't be transformed.
     */
    public static Geometry transform(final Geometry geom, final MathTransform transform)
            throws MismatchedDimensionException, TransformException {
        if (geom.isEmpty()) {
            Geometry g = (Geometry) geom.clone();
            g.setSRID(0);
            g.setUserData(null);
            return g;
        }
        final CoordinateSequenceTransformer cstrs = new CoordinateSequenceMathTransformer(transform);
        final GeometryCSTransformer transformer = new GeometryCSTransformer(cstrs);
        return transformer.transform(geom);
    }

    /**
     * Transforms the coordinate using the provided math transform.
     *
     * @param source the source coordinate that will be transformed
     * @param dest the coordinate that will be set. May be null or the source
     * coordinate (or new coordinate of course).
     * @return the destination coordinate if not null or a new Coordinate.
     * @throws TransformException if the coordinate can't be transformed.
     */
    public static Coordinate transform(final Coordinate source, Coordinate dest,
            final MathTransform transform) throws TransformException {
        ensureNonNull("source", source);
        ensureNonNull("transform", transform);

        if (dest == null) {
            dest = new Coordinate();
        }

        final double[] array = new double[transform.getSourceDimensions()];
        copy(source, array);
        transform.transform(array, 0, array, 0, 1);

        switch (transform.getTargetDimensions()) {
            case 3:
                dest.z = array[2]; // Fall through

            case 2:
                dest.y = array[1]; // Fall through

            case 1:
                dest.x = array[0]; // Fall through

            case 0:
                break;
        }
        return dest;
    }

    /**
     * Transforms the envelope from its current crs to WGS84 coordinate
     * reference system. If the specified envelope is already in WGS84, then it
     * is returned unchanged.
     *
     * @param envelope The envelope to transform.
     * @param crs The CRS the envelope is currently in.
     * @return The envelope transformed to be in WGS84 CRS.
     * @throws TransformException If at least one coordinate can't be
     * transformed.
     */
    public static Envelope toGeographic(final Envelope envelope, final CoordinateReferenceSystem crs)
            throws TransformException {
        if (Utilities.equalsIgnoreMetadata(crs, CommonCRS.WGS84.normalizedGeographic())) {
            return envelope;
        }
        final MathTransform transform;
        try {
            transform = CRS.findOperation(crs, CommonCRS.WGS84.normalizedGeographic(), null).getMathTransform();
        } catch (FactoryException exception) {
            throw new TransformException(Errors.format(Errors.Keys.CantTransformEnvelope, exception));
        }
        return transform(envelope, transform);
    }

    /**
     * Like a transform but eXtreme!
     *
     * Transforms an array of coordinates using the provided math transform.
     * Each coordinate is transformed separately. In case of a transform
     * exception then the new value of the coordinate is the last coordinate
     * correctly transformed.
     *
     * @param transform The math transform to apply.
     * @param src The source coordinates.
     * @param dest The destination array for transformed coordinates.
     * @throws TransformException if this method failed to transform any of the
     * points.
     */
    public static void xform(final MathTransform transform, final double[] src, final double[] dest)
            throws TransformException {
        ensureNonNull("transform", transform);
        final int sourceDim = transform.getSourceDimensions();
        final int targetDim = transform.getTargetDimensions();
        if (targetDim != sourceDim) {
            throw new MismatchedDimensionException();
        }
        TransformException firstError = null;
        boolean startPointTransformed = false;
        for (int i = 0; i < src.length; i += sourceDim) {
            try {
                transform.transform(src, i, dest, i, 1);

                if (!startPointTransformed) {
                    startPointTransformed = true;

                    for (int j = 0; j < i; j++) {
                        System.arraycopy(dest, j, dest, i, targetDim);
                    }
                }
            } catch (TransformException e) {
                if (firstError == null) {
                    firstError = e;
                }

                if (startPointTransformed) {
                    System.arraycopy(dest, i - targetDim, dest, i, targetDim);
                }
            }
        }
        if (!startPointTransformed && (firstError != null)) {
            throw firstError;
        }
    }

    /**
     * Computes the orthodromic distance between two points. This method: <p>
     * <ol> <li>Transforms both points to geographic coordinates
     * (<var>latitude</var>,<var>longitude</var>).</li> <li>Computes the
     * orthodromic distance between the two points using ellipsoidal
     * calculations.</li> </ol> <p> The real work is performed by
     * {@link GeodeticCalculator}. This convenience method simply manages a pool
     * of pre-defined geodetic calculators for the given coordinate reference
     * system in order to avoid repetitive object creation. If a large amount of
     * orthodromic distances need to be computed, direct use of
     * {@link GeodeticCalculator} provides better performance than this
     * convenience method.
     *
     * @param p1 First point
     * @param p2 Second point
     * @param crs Reference system the two points are in.
     * @return Orthodromic distance between the two points, in meters.
     * @throws TransformException if the coordinates can't be transformed from
     * the specified CRS to a
     * {@linkplain org.opengis.referencing.crs.GeographicCRS geographic CRS}.
     */
    public static synchronized double orthodromicDistance(final Coordinate p1, final Coordinate p2,
            final CoordinateReferenceSystem crs) throws TransformException {
        ensureNonNull("p1", p1);
        ensureNonNull("p2", p2);
        ensureNonNull("crs", crs);
        /*
         * Need to synchronize because we use a single instance of a Map (CALCULATORS) as well as
         * shared instances of GeodeticCalculator and GeneralDirectPosition (POSITIONS). None of
         * them are thread-safe.
         */
        GeodeticCalculator gc = CALCULATORS.get(crs);
        if (gc == null) {
            gc = GeodeticCalculator.create(crs);
            CALCULATORS.put(crs, gc);
        }
        assert crs.equals(gc.getPositionCRS()) : crs;

        final GeneralDirectPosition pos = POSITIONS[Math.min(POSITIONS.length - 1,
                crs.getCoordinateSystem().getDimension())];
        pos.setCoordinateReferenceSystem(crs);
        copy(p1, pos.ordinates);
        gc.setStartPoint(pos);
        copy(p2, pos.ordinates);
        gc.setEndPoint(pos);
        return gc.getGeodesicDistance();
    }

    /**
     * Copies the ordinates values from the specified JTS coordinates to the
     * specified array. The destination array can have any length. Only the
     * relevant field of the source coordinate will be copied. If the array
     * length is greater than 3, then all extra dimensions will be set to
     * {@link Double#NaN NaN}.
     *
     * @param point The source coordinate.
     * @param ordinates The destination array.
     */
    public static void copy(final Coordinate point, final double[] ordinates) {
        ensureNonNull("point", point);
        ensureNonNull("ordinates", ordinates);

        switch (ordinates.length) {
            default:
                Arrays.fill(ordinates, 3, ordinates.length, Double.NaN); // Fall through

            case 3:
                ordinates[2] = point.z; // Fall through

            case 2:
                ordinates[1] = point.y; // Fall through

            case 1:
                ordinates[0] = point.x; // Fall through

            case 0:
                break;
        }
    }

    /**
     * Converts an arbitrary Java2D shape into a JTS geometry. The created JTS
     * geometry may be any of {@link LineString}, {@link LinearRing} or
     * {@link MultiLineString}.
     *
     * @param shape The Java2D shape to create.
     * @param factory The JTS factory to use for creating geometry.
     * @return The JTS geometry.
     */
    public static Geometry shapeToGeometry(final Shape shape, final GeometryFactory factory) {
        if (shape instanceof JTSGeometryJ2D) {
            final JTSGeometryJ2D jtsgeom = (JTSGeometryJ2D) shape;
            Geometry geometry = jtsgeom.getGeometry();
            final MathTransform transform = jtsgeom.getTransform();
            if (!transform.isIdentity()) {
                try {
                    geometry = JTS.transform(geometry, transform);
                } catch (MismatchedDimensionException | TransformException ex) {
                    throw new BackingStoreException(ex.getMessage(), ex);
                }
            }
            return geometry;
        }
        ensureNonNull("shape", shape);
        ensureNonNull("factory", factory);

        final PathIterator iterator = shape.getPathIterator(null, ShapeUtilities.getFlatness(shape));
        final double[] buffer = new double[6];
        final List<Coordinate> coords = new ArrayList<Coordinate>();
        final List<LineString> lines = new ArrayList<LineString>();
        while (!iterator.isDone()) {
            switch (iterator.currentSegment(buffer)) {
                /*
                 * Close the polygon: the last point is equal to
                 * the first point, and a LinearRing is created.
                 */
                case PathIterator.SEG_CLOSE: {
                    if (!coords.isEmpty()) {
                        coords.add(coords.get(0));
                        lines.add(factory.createLinearRing(
                                (Coordinate[]) coords.toArray(new Coordinate[coords.size()])));
                        coords.clear();
                    }
                    break;
                }
                /*
                 * General case: A LineString is created from previous
                 * points, and a new LineString begin for next points.
                 */
                case PathIterator.SEG_MOVETO: {
                    if (!coords.isEmpty()) {
                        lines.add(factory.createLineString(
                                (Coordinate[]) coords.toArray(new Coordinate[coords.size()])));
                        coords.clear();
                    }

                    // Fall through
                }
                case PathIterator.SEG_LINETO: {
                    coords.add(new Coordinate(buffer[0], buffer[1]));

                    break;
                }
                default:
                    throw new IllegalPathStateException();
            }
            iterator.next();
        }
        /*
         * End of loops: create the last LineString if any, then create the MultiLineString.
         */
        if (!coords.isEmpty()) {
            lines.add(factory.createLineString(
                    (Coordinate[]) coords.toArray(new Coordinate[coords.size()])));
        }
        switch (lines.size()) {
            case 0:
                return null;
            case 1:
                return (LineString) lines.get(0);
            default:
                return factory.createMultiLineString(GeometryFactory.toLineStringArray(lines));
        }
    }

    /**
     * Converts a JTS 2D envelope in an {@link Envelope2D} for interoperability
     * with the referencing package. <p> If the provided envelope is a
     * {@link JTSEnvelope2D} we check that the provided CRS and the implicit CRS
     * are similar.
     *
     * @param envelope The JTS envelope to convert.
     * @param crs The coordinate reference system for the specified envelope.
     * @return The Types envelope.
     * @throws MismatchedDimensionException if a two-dimensional envelope can't
     * be created from an envelope with the specified CRS.
     *
     * @since 2.3
     */
    public static Envelope2D getEnvelope2D(final Envelope envelope,
            final CoordinateReferenceSystem crs) throws MismatchedDimensionException {
        // Initial checks
        ensureNonNull("envelope", envelope);
        ensureNonNull("crs", crs);

        // Ensure the CRS is 2D and retrieve the new envelope
        final CoordinateReferenceSystem crs2D = CRS.getHorizontalComponent(crs);
        if (crs2D == null) {
            throw new MismatchedDimensionException(
                    Errors.format(
                    Errors.Keys.CantSeparateCrs_1, crs));
        }
        return new Envelope2D(crs2D, envelope.getMinX(), envelope.getMinY(), envelope.getWidth(),
                envelope.getHeight());
    }

    public static Polygon toGeometry(final Rectangle envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }

    /**
     * Converts an envelope to a polygon. <p> The resulting polygon contains an
     * outer ring with verticies: (x1,y1),(x2,y1),(x2,y2),(x1,y2),(x1,y1)
     *
     * @param envelope The original envelope.
     * @return The envelope as a polygon.
     *
     * @since 2.4
     */
    public static Polygon toGeometry(final Envelope envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }

    /**
     * This method is not correct when dealing with antemeridian.
     *
     * @deprecated Use GeometricUtilities.toGeometryJTS(Envelope, WrapResolution.NONE) for exact same behavior
     */
    @Deprecated
    public static Polygon toGeometry(final org.opengis.geometry.Envelope env){
        final GeometryFactory gf = new GeometryFactory();
        final Coordinate[] coordinates = new Coordinate[]{
            new Coordinate(env.getMinimum(0), env.getMinimum(1)),
            new Coordinate(env.getMinimum(0), env.getMaximum(1)),
            new Coordinate(env.getMaximum(0), env.getMaximum(1)),
            new Coordinate(env.getMaximum(0), env.getMinimum(1)),
            new Coordinate(env.getMinimum(0), env.getMinimum(1)),
        };
        final LinearRing ring = gf.createLinearRing(coordinates);
        return gf.createPolygon(ring, new LinearRing[0]);
    }

    /**
     * Create a ReferencedEnvelope from the provided geometry, we will do our
     * best to guess the CoordinateReferenceSystem making use of getUserData()
     * and getSRID() as needed.
     *
     * @param geom Provided Geometry
     * @return RefernecedEnveleope describing the bounds of the provided
     * Geometry
     */
    public static JTSEnvelope2D toEnvelope(final Geometry geom) {
        if (geom == null) {
            return null; //return new ReferencedEnvelope(); // very empty!
        }
        String srsName = null;
        Object userData = geom.getUserData();
        if (userData != null && userData instanceof String) {
            srsName = (String) userData;
        } else if (geom.getSRID() > 0) {
            srsName = "EPSG:" + geom.getSRID();
        }
        CoordinateReferenceSystem crs = null;
        if (userData != null && userData instanceof CoordinateReferenceSystem) {
            crs = (CoordinateReferenceSystem) userData;
        } else if (srsName != null) {
            try {
                crs = CRS.forCode(srsName);
            } catch (NoSuchAuthorityCodeException e) {
                // e.printStackTrace();
            } catch (FactoryException e) {
                // e.printStackTrace();
            }
        }
        return new JTSEnvelope2D(geom.getEnvelopeInternal(), crs);
    }

    /**
     * Create an empty geometry of given type.
     */
    public static <T extends Geometry> T emptyGeometry(Class<T> geomClass, CoordinateReferenceSystem crs, GeometryFactory factory) {
        ArgumentChecks.ensureNonNull("geometry class", geomClass);
        if(factory==null) factory = new GeometryFactory();

        final T geometry;
        if(Point.class.equals(geomClass)){
            geometry = (T) factory.createPoint((Coordinate)null);
        }else if(LineString.class.equals(geomClass)){
            geometry = (T) factory.createLineString((CoordinateSequence)null);
        }else if(Polygon.class.equals(geomClass)){
            geometry = (T) factory.createPolygon((CoordinateSequence)null);
        }else if(MultiPoint.class.equals(geomClass)){
            geometry = (T) factory.createMultiPoint((CoordinateSequence)null);
        }else if(MultiLineString.class.equals(geomClass)){
            geometry = (T) factory.createMultiLineString(null);
        }else if(MultiPolygon.class.equals(geomClass)){
            geometry = (T) factory.createMultiPolygon(null);
        }else if(GeometryCollection.class.equals(geomClass)){
            geometry = (T) factory.buildGeometry(null);
        }else{
            throw new IllegalArgumentException("Unknown geometry class "+geomClass.getName());
        }
        if (crs!=null) JTS.setCRS(geometry, crs);
        return geometry;
    }

    /**
     * Converts a {@link BoundingBox} to a polygon. <p> The resulting polygon
     * contains an outer ring with verticies:
     * (x1,y1),(x2,y1),(x2,y2),(x1,y2),(x1,y1)
     *
     * @param envelope The original envelope.
     * @return The envelope as a polygon.
     *
     * @since 2.4
     */
    public static Polygon toGeometry(final BoundingBox envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }

    /**
     * Checks a Geometry coordinates are within the area of validity of the
     * specified reference system. If a coordinate falls outside the area of
     * validity a {@link ProjectionException} is thrown
     *
     * @param geom the geometry to check
     * @param crs that defines the are of validity (must not be null)
     * @throws ProjectionException
     * @since 2.4
     */
    public static void checkCoordinatesRange(final Geometry geom, final CoordinateReferenceSystem crs)
            throws ProjectionException {
        // named x,y, but could be anything
        CoordinateSystemAxis x = crs.getCoordinateSystem().getAxis(0);
        CoordinateSystemAxis y = crs.getCoordinateSystem().getAxis(1);

        // check if unbounded, many projected systems are, in this case no check
        // is needed
        boolean xUnbounded = Double.isInfinite(x.getMinimumValue()) && Double.isInfinite(x.getMaximumValue());
        boolean yUnbounded = Double.isInfinite(y.getMinimumValue()) && Double.isInfinite(y.getMaximumValue());
        if (xUnbounded && yUnbounded) {
            return;
        }
        // check each coordinate
        Coordinate[] c = geom.getCoordinates();
        for (int i = 0; i < c.length; i++) {
            if (!xUnbounded && ((c[i].x < x.getMinimumValue()) || (c[i].x > x.getMaximumValue()))) {
                throw new ProjectionException(c[i].x + " outside of (" + x.getMinimumValue() + "," + x.getMaximumValue() + ")");
            }
            if (!yUnbounded && ((c[i].y < y.getMinimumValue()) || (c[i].y > y.getMaximumValue()))) {
                throw new ProjectionException(c[i].y + " outside of (" + y.getMinimumValue() + "," + y.getMaximumValue() + ")");
            }
        }
    }

    /**
     * Set a crs to a geometry. 1 - if user data is a CRS, set with the crs 2 -
     * if user data is a Map, add an entry with the crs
     *
     * @param geom, should not be null
     * @param crs if null method has no effect
     */
    public static void setCRS(Geometry geom, final CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureNonNull("geometry", geom);
        if (crs == null) {
            return;
        }
        int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
        Object userData = geom.getUserData();
        if (userData instanceof CoordinateReferenceSystem) {
            userData = crs;
        } else {
            if (userData instanceof Map) {
                Map values = (Map) userData;
                values.put(HintsPending.JTS_GEOMETRY_CRS, crs);
                userData = values;
            }
        }
        if (userData == null) {
            userData = crs;
        }
        geom.setUserData(userData);
        geom.setSRID(srid);
    }

    /**
     * Find the crs of the geometry. 1 - search if the user data is a CRS 2 -
     * search if the user data is a Map and has key JTSGeometryCRS 3 - try to
     * rebuild CRS from the srid.
     *
     * @param geom a Geometry.
     * @return null if none where successful or if geometry is null.
     * @throws NoSuchAuthorityCodeException, FactoryException
     */
    public static CoordinateReferenceSystem findCoordinateReferenceSystem(final Geometry geom)
            throws NoSuchAuthorityCodeException, FactoryException {
        //chexk if geometry is defined and prevent NullPointerException
        if (geom == null) {
            return null;
        }
        //we don't know in which crs it is, try to find it
        CoordinateReferenceSystem crs = null;
        final Object userData = geom.getUserData();
        if (userData instanceof CoordinateReferenceSystem) {
            crs = (CoordinateReferenceSystem) userData;
        } else if (userData instanceof Map) {
            final Map values = (Map) userData;
            final Object candidate = values.get(HintsPending.JTS_GEOMETRY_CRS);
            if (candidate instanceof CoordinateReferenceSystem) {
                crs = (CoordinateReferenceSystem) candidate;
            }
        }
        //not found yet, try to rebuild it from the srid
        if (crs == null) {
            final int srid = geom.getSRID();
            if (srid != 0 && srid != -1) {
                final String srs = SRIDGenerator.toSRS(srid, SRIDGenerator.Version.V1);
                crs = CRS.forCode(srs);
            }
        }
        return crs;
    }

    /**
     * Determine the min and max "z" values in an array of Coordinates.
     *
     * @param cs The array to search.
     * @param target array with at least two elements where to hold the min and
     * max zvalues. target[0] will be filled with the minimum zvalue, target[1]
     * with the maximum. The array current values, if not NaN, will be taken
     * into acount in the computation.
     */
    public static void zMinMax(final CoordinateSequence cs, final double[] target) {
        if (cs.getDimension() < 3) {
            return;
        }
        double zmin;
        double zmax;
        boolean validZFound = false;

        zmin = Double.NaN;
        zmax = Double.NaN;

        double z;
        final int size = cs.size();
        for (int t = size - 1; t >= 0; t--) {
            z = cs.getOrdinate(t, 2);
            if (!(Double.isNaN(z))) {
                if (validZFound) {
                    if (z < zmin) {
                        zmin = z;
                    }
                    if (z > zmax) {
                        zmax = z;
                    }
                } else {
                    validZFound = true;
                    zmin = z;
                    zmax = z;
                }
            }
        }
        if (!Double.isNaN(zmin)) {
            target[0] = zmin;
        }
        if (!Double.isNaN(zmax)) {
            target[1] = zmax;
        }
    }

    /**
     * Does what it says, reverses the order of the Coordinates in the ring.
     *
     * @param lr The ring to reverse.
     * @return A new ring with the reversed Coordinates.
     */
    public static LinearRing reverseRing(final LinearRing lr) {
        final GeometryFactory gf = new GeometryFactory();
        final int numPoints = lr.getNumPoints() - 1;
        final Coordinate[] newCoords = new Coordinate[numPoints + 1];
        for (int t = numPoints; t >= 0; t--) {
            newCoords[t] = lr.getCoordinateN(numPoints - t);
        }
        return gf.createLinearRing(newCoords);
    }

    /**
     * Create a clockwise Polygon from the given Polygon. Will ensure that
     * shells are clockwise and holes are counter-clockwise. Handle only
     * <b>2D</b> Polygon and MultiPolygon Geometry. This method use the
     * CGAlgorithm.isCCW() method form JTS.
     *
     * @param p The Polygon to make CW.
     * @return The "nice" Polygon.
     */
    public static <T extends Geometry> T ensureClockWise(final T g) {
        if (!(g instanceof MultiPolygon) && !(g instanceof Polygon)) {
            return g;
        }
        final GeometryFactory gf = new GeometryFactory();

        boolean isMultiPolygon = false;
        int nbPolygon = 1;

        if (g instanceof MultiPolygon) {
            nbPolygon = g.getNumGeometries();
            isMultiPolygon = true;
        }
        final Polygon[] ps = new Polygon[nbPolygon];
        for (int i = 0; i < nbPolygon; i++) {
            final Polygon p;
            if (isMultiPolygon) {
                p = (Polygon) g.getGeometryN(i);
            } else {
                p = (Polygon) g;
            }
            final LinearRing outer;
            final LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
            Coordinate[] coords;
            coords = p.getExteriorRing().getCoordinates();
            if (Orientation.isCCW(coords)) {
                outer = reverseRing((LinearRing) p.getExteriorRing());
            } else {
                outer = (LinearRing) p.getExteriorRing();
            }
            for (int t = 0, tt = p.getNumInteriorRing(); t < tt; t++) {
                coords = p.getInteriorRingN(t).getCoordinates();
                if (!(Orientation.isCCW(coords))) {
                    holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
                } else {
                    holes[t] = (LinearRing) p.getInteriorRingN(t);
                }
            }
            ps[i] = gf.createPolygon(outer, holes);
        }
        if (isMultiPolygon) {
            return (T) gf.createMultiPolygon(ps);
        } else {
            return (T) ps[0];
        }
    }

    /**
     * Create a counter clockwise Polygon from the given Polygon. Will ensure
     * that shells are counter-clockwise and holes are clockwise. Handle only
     * <b>2D</b> Polygon and MultiPolygon Geometry. This method use the
     * CGAlgorithm.isCCW() method form JTS.
     *
     * @param p The Polygon to make CCW.
     * @return The "nice" Polygon.
     */
    public static <T extends Geometry> T ensureCounterClockWise(final T g) {
        if (!(g instanceof MultiPolygon) && !(g instanceof Polygon)) {
            return g;
        }
        final GeometryFactory gf = new GeometryFactory();

        boolean isMultiPolygon = false;
        int nbPolygon = 1;

        if (g instanceof MultiPolygon) {
            nbPolygon = g.getNumGeometries();
            isMultiPolygon = true;
        }
        final Polygon[] ps = new Polygon[nbPolygon];
        for (int i = 0; i < nbPolygon; i++) {
            final Polygon p;
            if (isMultiPolygon) {
                p = (Polygon) g.getGeometryN(i);
            } else {
                p = (Polygon) g;
            }
            final LinearRing outer;
            final LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
            Coordinate[] coords;
            coords = p.getExteriorRing().getCoordinates();
            if (Orientation.isCCW(coords)) {
                outer = (LinearRing) p.getExteriorRing();
            } else {
                outer = reverseRing((LinearRing) p.getExteriorRing());
            }
            for (int t = 0, tt = p.getNumInteriorRing(); t < tt; t++) {
                coords = p.getInteriorRingN(t).getCoordinates();
                if (!(Orientation.isCCW(coords))) {
                    holes[t] = (LinearRing) p.getInteriorRingN(t);
                } else {
                    holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
                }
            }
            ps[i] = gf.createPolygon(outer, holes);
        }
        if (isMultiPolygon) {
            return (T) gf.createMultiPolygon(ps);
        } else {
            return (T) ps[0];
        }
    }

    /**
     * Create a clockwise Polygon from the given Polygon. Will ensure that
     * shells are clockwise and holes are counter-clockwise. Handle 2D and 3D
     * Polygon and MultiPolygon Geometry.
     *
     * @param p The Polygon to make CW.
     * @return The "nice" Polygon.
     */
    public static <T extends Geometry> T ensureClockWise3D(final T g) {
        if (!(g instanceof MultiPolygon) && !(g instanceof Polygon)) {
            return g;
        }
        final GeometryFactory gf = new GeometryFactory();

        boolean isMultiPolygon = false;
        int nbPolygon = 1;

        if (g instanceof MultiPolygon) {
            nbPolygon = g.getNumGeometries();
            isMultiPolygon = true;
        }
        final Polygon[] ps = new Polygon[nbPolygon];
        for (int i = 0; i < nbPolygon; i++) {
            final Polygon p;
            if (isMultiPolygon) {
                p = (Polygon) g.getGeometryN(i);
            } else {
                p = (Polygon) g;
            }
            final LinearRing outer;
            final LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
            Coordinate[] coords;
            coords = p.getExteriorRing().getCoordinates();
            if (isCCW3D(coords)) {
                outer = reverseRing((LinearRing) p.getExteriorRing());
            } else {
                outer = (LinearRing) p.getExteriorRing();
            }
            for (int t = 0, tt = p.getNumInteriorRing(); t < tt; t++) {
                coords = p.getInteriorRingN(t).getCoordinates();
                if (!(isCCW3D(coords))) {
                    holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
                } else {
                    holes[t] = (LinearRing) p.getInteriorRingN(t);
                }
            }
            ps[i] = gf.createPolygon(outer, holes);
        }
        if (isMultiPolygon) {
            return (T) gf.createMultiPolygon(ps);
        } else {
            return (T) ps[0];
        }
    }

    /**
     * Create a counter clockwise Polygon from the given Polygon. Will ensure
     * that shells are counter-clockwise and holes are clockwise. Handle 2D and
     * 3D Polygon and MultiPolygon Geometry.
     *
     * @param p The Polygon to make CCW.
     * @return The "nice" Polygon.
     */
    public static <T extends Geometry> T ensureCounterClockWise3D(final T g) {
        if (!(g instanceof MultiPolygon) && !(g instanceof Polygon)) {
            return g;
        }
        final GeometryFactory gf = new GeometryFactory();
        boolean isMultiPolygon = false;
        int nbPolygon = 1;
        if (g instanceof MultiPolygon) {
            nbPolygon = g.getNumGeometries();
            isMultiPolygon = true;
        }
        final Polygon[] ps = new Polygon[nbPolygon];
        for (int i = 0; i < nbPolygon; i++) {
            final Polygon p;
            if (isMultiPolygon) {
                p = (Polygon) g.getGeometryN(i);
            } else {
                p = (Polygon) g;
            }
            final LinearRing outer;
            final LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
            Coordinate[] coords;
            coords = p.getExteriorRing().getCoordinates();
            if (isCCW3D(coords)) {
                outer = (LinearRing) p.getExteriorRing();
            } else {
                outer = reverseRing((LinearRing) p.getExteriorRing());
            }
            for (int t = 0, tt = p.getNumInteriorRing(); t < tt; t++) {
                coords = p.getInteriorRingN(t).getCoordinates();
                if (!(isCCW3D(coords))) {
                    holes[t] = (LinearRing) p.getInteriorRingN(t);
                } else {
                    holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
                }
            }
            ps[i] = gf.createPolygon(outer, holes);
        }
        if (isMultiPolygon) {
            return (T) gf.createMultiPolygon(ps);
        } else {
            return (T) ps[0];
        }
    }

    /**
     * Test if a ring is counter clockwise oriended or not in 3D.
     *
     * @param ring The ring to test.
     * @return true if ring is CCW, false if ring is CW.
     */
    public static boolean isCCW3D(Coordinate[] ring) {
        final int nbCoords = ring.length - 1;
        double buff = 0.0;
        for (int i = 1; i < nbCoords; i++) {
            final Vector3d v1 = new Vector3d((ring[i].x - ring[i - 1].x), (ring[i].y - ring[i - 1].y), (ring[i].z - ring[i - 1].z));
            final Vector3d v2 = new Vector3d((ring[i + 1].x - ring[i].x), (ring[i + 1].y - ring[i].y), (ring[i + 1].z - ring[i].z));
            v1.cross(v1, v2);
            v1.normalize();
            buff += (v1.x + v1.y + v1.z);
        }
        if (buff > 0.0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method return the common CoordinateReferenceSystem of two
     * geometries. If first geometry has a CRS, it'll be returned. If second
     * geometry has a CRS AND the first one CRS is null, it's the second
     * geometry CRS that will be returned. If first and second geometries CRS
     * are null, null CRS will be returned.
     *
     * @return the CRS keeped for the geometries.
     */
    public static CoordinateReferenceSystem getCommonCRS(final Geometry geom1, final Geometry geom2) throws FactoryException, TransformException {
        CoordinateReferenceSystem resultCRS = null;

        //get geometies CRS
        final CoordinateReferenceSystem crs1 = findCoordinateReferenceSystem(geom1);
        final CoordinateReferenceSystem crs2 = findCoordinateReferenceSystem(geom2);

        //crs1 exist
        if (crs1 != null) {
            resultCRS = crs1;
        } else {
            //crs1 == null and crs2 exist
            if (crs2 != null) {
                resultCRS = crs2;
            }
        }
        return resultCRS;
    }

    /**
     * This utility method convert a geometry into a different CoordinateReferenceSystem.
     * If the geometry crs is not defined, the geometry will be returned without transformation.
     * @param geom a geometry to convert. (Not null)
     * @param crsTarget the target CoordinateReferenceSystem (Not null)
     * @return the geometry converted with targetCRS as geometry CRS.
     */
    public static Geometry convertToCRS(final Geometry geom, final CoordinateReferenceSystem crsTarget)
            throws MismatchedDimensionException, TransformException, FactoryException {
        ArgumentChecks.ensureNonNull("geometry", geom);
        ArgumentChecks.ensureNonNull("crsTarget", crsTarget);

        //get geometry CRS
        final CoordinateReferenceSystem crsGeom = findCoordinateReferenceSystem(geom);
        if (crsGeom == null) {
            return geom;
        }

        //convert geometry
        final MathTransform mt = CRS.findOperation(crsGeom, crsTarget, null).getMathTransform();
        final Geometry result = transform(geom, mt);
        setCRS(result, crsTarget);

        return result;
    }

    /**
     * This method check if two geometries have a different CRS.
     *
     * @return true if geom1 and geom2 have different CRS.
     */
    public static boolean isConversionNeeded(final Geometry geom1, final Geometry geom2) throws FactoryException {
        final CoordinateReferenceSystem crs1 = findCoordinateReferenceSystem(geom1);
        final CoordinateReferenceSystem crs2 = findCoordinateReferenceSystem(geom2);
        return crs1 != null && crs2 != null && (!crs1.equals(crs2));
    }
}
