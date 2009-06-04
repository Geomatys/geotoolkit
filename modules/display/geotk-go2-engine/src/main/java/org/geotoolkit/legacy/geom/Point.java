/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.legacy.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.math.Statistics;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.util.Utilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;


/**
 * A wrapper around a single JTS Coordinate object to adapt it to the the
 * Geometry hierarchy.
 *
 * @author Andrea Aime
 * @version $Id: Point.java 19519 2006-05-17 10:55:42Z desruisseaux $
 */
public class Point extends Geometry {
    /**
     * Last coordinate transformation used for computing {@link
     * #coordinateTransform}. Used in order to avoid the costly call to {@link
     * CoordinateSystemFactory} methods when the same transform is requested
     * many consecutive time, which is a very common situation.
     */
    private static CoordinateOperation lastCoordinateTransform = getIdentityTransform(DEFAULT_COORDINATE_SYSTEM);

    /** The wrapped JTS coordinate object. */
    private Coordinate coord;

    /**
     * The transformed coordinates if reprojection is needed, null if the
     * CoordinateTransform is an indentity one.
     */
    private float[] transformedPoint;

    /**
     * The coordinate transformation used to reproject points, if necessary.
     * Also holds references to the original and current coordinate system.
     */
    private CoordinateOperation ct;

    /**
     * Creates a new instance of Point
     *
     * @param coord The point coordinates
     * @param ct The coordinate transformation to be applied to the coordinates
     *        (which are supposed to be unprojected)
     */
    public Point(Coordinate coord, CoordinateOperation ct) {
        this.coord = coord;
        this.ct = ct;
    }

    /**
     * Creates a new instance of Point. An identity coordinate transformation
     * will be created trasparently.
     *
     * @param coord The point coordinates
     * @param cs The source coordinate system.
     */
    public Point(Coordinate coord, CoordinateReferenceSystem cs) {
        this.coord = coord;
        this.ct = getIdentityTransform(getCoordinateReferenceSystem2D(cs));
    }

    /**
     * Point compression is not supported as it makes no sense for points.
     *
     * @param level The compression level (or algorithm) to use. See the {@link
     *        CompressionLevel} javadoc for an explanation of available
     *        algorithms.
     *
     * @return A <em>estimation</em> of the compression rate. Will always be
     *         1.0 since no compression is operated
     */
    @Override
    public float compress(CompressionLevel level) {
        return 1.0f;
    }

    /**
     * This method returns true if the shape is another <code>Point</code>
     * object with  the same coordinates, false otherwise. The point's
     * coordinates must be expressed  according to the current coordinate
     * system, that is {@link #getCoordinateSystem()}.
     */
    @Override
    public boolean contains(final Shape shape) {
        if (shape instanceof Point) {
            final Point p = (Point) shape;
            return (p.getX() == getX()) && (p.getY() == getY());
        } else {
            return false;
        }
    }

    /**
     * This method returns true if <code>p</code> has the same coordinates as
     * this object,  false otherwise. The point <code>p</code> coordinates
     * must be expressed  according to the current coordinate system, that is
     * {@link #getCoordinateSystem()}.
     */
    @Override
    public boolean contains(final Point2D p) {
        return (p.getX() == getX()) && (p.getY() == getY());
    }

    /**
     * Returns the bounds of this points, that is, an immutable and empty
     * rectangle centered on the current coordinates. Point reprojection
     * will change rectangle coordinates too.
     */
    @Override
    public Rectangle2D getBounds2D() {
        return new Point.PointBound();
    }

    /**
     * Returns a path iterator for this point.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {
        return new PointPathIterator(at);
    }

    /**
     * Returns the number of coordinates contained in this geometry, as such,
     * the result will always be 1
     */
    @Override
    public int getPointCount() {
        return 1;
    }

    /**
     * Implemented for compatibility, but it makes no sense to compute
     * resolution for a single point
     */
    @Override
    public Statistics getResolution() {
        return new Statistics();
    }

    /**
     * Returns true if the shape contains the point
     */
    @Override
    public boolean intersects(final Shape shape) {
        return shape.contains(getX(), getY());
    }

    /**
     * Emtpy method, provided for compatibility with base class.
     */
    @Override
    public void setResolution(double resolution) throws TransformException, 
                                                        UnmodifiableGeometryException
    {
        // nothing to do...
    }

    /**
     * Returns the x coordinate of the point, projected to the new coordinate
     * system if  the user has specified an non identity CoordinateTransform
     */
    public float getX() {
        if (ct.getMathTransform().isIdentity()) {
            return (float) coord.x;
        } else {
            return transformedPoint[0];
        }
    }

    /**
     * Returns the y coordinate of the point, projected to the new coordinate
     * system if  the user has specified an non identity CoordinateTransform
     */
    public float getY() {
        if (ct.getMathTransform().isIdentity()) {
            return (float) coord.y;
        } else {
            return transformedPoint[1];
        }
    }

    /**
     * Always returns false, the point will never be frozen
     *
     * @see Geometry#isFrozen()
     */
    @Override
    public boolean isFrozen() {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////          C O O R D I N A T E   S Y S T E M S   S E T T I N G          ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Same as {@link CTSUtilities#getCoordinateSystem2D}, but wraps the {@link
     * TransformException} into an {@link IllegalArgumentException}. Used for
     * constructors only. Other methods still use the method throwing a
     * transform exception.
     *
     * @param cs The coordinate system
     *
     * @return The correspondent CoordinateSystem2D
     *
     * @throws IllegalArgumentException if a transformation exception occurs
     */
    private static CoordinateReferenceSystem getCoordinateReferenceSystem2D(final CoordinateReferenceSystem cs)
            throws IllegalArgumentException
    {
        try {
            return CRSUtilities.getCRS2D(cs);
        } catch (TransformException exception) {
            throw new IllegalArgumentException(exception.getLocalizedMessage());
        }
    }

    /**
     * Returns the native coordinate system of {@link #data}'s points, or
     * <code>null</code> if unknown.
     */
    private CoordinateReferenceSystem getInternalCRS() {
        // copy 'coordinateTransform' reference in order to avoid synchronization
        final CoordinateOperation coordinateTransform = this.ct;
        return (ct != null) ? ct.getSourceCRS() : null;
    }

    /**
     * Returns the polyline's coordinate system, or <code>null</code> if
     * unknown.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        // copy 'coordinateTransform' reference in order to avoid synchronization
        final CoordinateOperation ct = this.ct;
        return (ct != null) ? ct.getTargetCRS() : null;
    }

    /**
     * Returns the transform from coordinate system used by {@link #data} to
     * the specified coordinate system. If at least one of the coordinate
     * systems is unknown, this method returns <code>null</code>.
     *
     * @throws CannotCreateTransformException If the transform cannot be created.
     */
    final CoordinateOperation getTransformationFromInternalCRS(final CoordinateReferenceSystem crs) 
            throws OperationNotFoundException, FactoryException {
        // copy 'coordinateTransform' reference in order to avoid synchronization
        CoordinateOperation ct = this.ct;

        if ((crs != null) && (ct != null)) {
            if (crs.equals(ct.getTargetCRS())) {
                return ct;
            }

            final CoordinateReferenceSystem internalCRS = ct.getSourceCRS();
            ct = lastCoordinateTransform;

            if (crs.equals(ct.getTargetCRS())) {
                if (equivalents(ct.getSourceCRS(), internalCRS)) {
                    return ct;
                }
            }

            ct = getCoordinateTransformation(internalCRS, crs);
            lastCoordinateTransform = ct;

            return ct;
        }

        return null;
    }

    /**
     * Sets the polyline's coordinate system. Calling this method is equivalent
     * to reprojecting all polyline's points from the old coordinate system to
     * the new one.
     *
     * @param coordinateSystem The new coordinate system. A <code>null</code>
     *        value resets the coordinate system given at construction time.
     *
     * @throws TransformException If a transformation failed. In case of
     *         failure, the state of this object will stay unchanged (as if
     *         this method has never been invoked).
     * @throws UnmodifiableGeometryException if modifying this geometry would
     *         corrupt a container. To avoid this exception, {@linkplain
     *         #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateSystem)
        throws TransformException, UnmodifiableGeometryException {
        // Do not use 'Polyline.getCoordinateSystem2D', since
        // we want a 'TransformException' in case of failure.
        coordinateSystem = CRSUtilities.getCRS2D(coordinateSystem);

        if (coordinateSystem == null) {
            coordinateSystem = getInternalCRS();

            // May still null. Its ok.
        }

        if (Utilities.equals(coordinateSystem, getCoordinateReferenceSystem())) {
            return;
        }

        CoordinateOperation transformCandidate = null;
        try{
            transformCandidate = getTransformationFromInternalCRS(coordinateSystem);
        }catch(OperationNotFoundException ope){
            Logger.getLogger(this.getClass().toString()).log(Level.SEVERE, ope.getLocalizedMessage());
        }catch(FactoryException ope){
            Logger.getLogger(this.getClass().toString()).log(Level.SEVERE, ope.getLocalizedMessage());
        }

        if (transformCandidate == null) {
            transformCandidate = getIdentityTransform(coordinateSystem);
        }

        /*
         * Store the new coordinate transform
         * only after projection has succeeded.
         */
        this.ct = transformCandidate;

        if (ct.getMathTransform().isIdentity()) {
            transformedPoint = null;
        } else {
            float[] src = new float[] { (float) coord.x, (float) coord.y };
            transformedPoint = new float[2];
            ct.getMathTransform().transform(src, 0, transformedPoint, 0, 1);
        }

        assert Utilities.equals(coordinateSystem, getCoordinateReferenceSystem());
    }

    /**
     * Indicates whether the specified transform is the identity transform. A
     * null transform (<code>null</code>) is considered to be an identity
     * transform.
     */
    private static boolean isIdentity(
        final CoordinateOperation coordinateTransform) {
        return (coordinateTransform == null)
        || coordinateTransform.getMathTransform().isIdentity();
    }

    /**
     * Inner class that provides the bounds of a point without requiring to
     * duplicate the memory required to store coordinates. 
     */
    private class PointBound extends Rectangle2D {
        /**
         * @see Rectangle2D#createIntersection
         */
        @Override
        public Rectangle2D createIntersection(Rectangle2D r) {
            if (r.contains(Point.this.getX(), Point.this.getY())) {
                return this;
            } else {
                return new Rectangle2D.Float(0, 0, 0, 0);
            }
        }

        @Override
        public Rectangle2D createUnion(Rectangle2D r) {
            float x = Point.this.getX();
            float y = Point.this.getY();
            double x1 = Math.min(x, r.getMinX());
            double y1 = Math.min(y, r.getMinY());
            double x2 = Math.max(x, r.getMaxX());
            double y2 = Math.max(y, r.getMaxY());
            Rectangle2D dest = new Rectangle2D.Float();
            dest.setFrameFromDiagonal(x1, y1, x2, y2);

            return dest;
        }

        @Override
        public double getHeight() {
            return 0.0;
        }

        @Override
        public double getWidth() {
            return 0.0;
        }

        @Override
        public double getX() {
            return Point.this.getX();
        }

        @Override
        public double getY() {
            return Point.this.getY();
        }

        @Override
        public int outcode(double x, double y) {
            int result = 0;

            if (x < Point.this.getX()) {
                result |= Rectangle2D.OUT_LEFT;
            } else if (x > Point.this.getX()) {
                result |= Rectangle2D.OUT_RIGHT;
            }

            if (y < Point.this.getY()) {
                result |= Rectangle2D.OUT_BOTTOM;
            } else if (y > Point.this.getY()) {
                result |= Rectangle2D.OUT_TOP;
            }

            return result;
        }

        @Override
        public void setRect(double x, double y, double w, double h) {
            throw new UnsupportedOperationException("Unmodifiable rectangle");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    /**
     * Simple path iterator that wraps the Point and uses directly its coordinates
     * to avoid memory duplication
     */
    private class PointPathIterator implements PathIterator {
        private AffineTransform at;

        public PointPathIterator(AffineTransform at) {
            this.at = at;
        }

        @Override
        public int currentSegment(double[] coords) {
            coords[0] = getX();
            coords[1] = getY();
            at.transform(coords, 0, coords, 0, 1);

            return PathIterator.SEG_LINETO;
        }

        @Override
        public int currentSegment(float[] coords) {
            coords[0] = getX();
            coords[1] = getY();
            at.transform(coords, 0, coords, 0, 1);

            return PathIterator.SEG_LINETO;
        }

        @Override
        public int getWindingRule() {
            return PathIterator.WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public void next() {
            // nothing to do
        }
    }
}
