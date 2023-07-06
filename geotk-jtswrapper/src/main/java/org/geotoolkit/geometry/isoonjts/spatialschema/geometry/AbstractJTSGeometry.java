/*******************************************************************************
 * $Id$
 * $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/GeometryImpl.java,v $
 * Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved.
 * http://www.opengis.org/Legal/
 ******************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;

import java.io.Serializable;
import java.util.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurveBoundary;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.geotoolkit.internal.jaxb.CoordinateReferenceSystemAdapter;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.util.Cloneable;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.Boundary;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.Ring;

/**
 * Base class for our JTS-based implementation of the various ISO 19107 geometry classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractJTSGeometry implements Geometry, Serializable, Cloneable, JTSGeometry {

    /**
     * True if we're allowing changes to the geometry.  False if not.
     */
    private boolean mutable;
    /**
     * CRS for this geometry.
     */
    @XmlAttribute(name="srsName")
    @XmlJavaTypeAdapter(CoordinateReferenceSystemAdapter.class)
    private CoordinateReferenceSystem coordinateReferenceSystem;
    /**
     * The JTS equivalent of this geometry.  This gets set to null whenever we
     * make changes to the geometry so that we can recompute it.
     */
    private org.locationtech.jts.geom.Geometry jtsPeer;
    /**
     * If this object is part of a composite, this this member should hold a
     * pointer to that composite so that when our JTS geometry is invalidated,
     * we can also invalidate that of our parent.
     */
    private JTSGeometry parent;

    /**
     * Creates a new mutable {@code GeometryImpl} with a null CRS.
     */
    public AbstractJTSGeometry() {
        this(null);
    }

    /**
     * Creates a new mutable {@code GeometryImpl}.
     * @param coordinateReferenceSystem CRS for this geometry's vertices.
     */
    public AbstractJTSGeometry(final CoordinateReferenceSystem coordinateReferenceSystem) {
        this(coordinateReferenceSystem, true);
    }

    /**
     * Creates a new {@code GeometryImpl}.
     *
     * @param coordinateReferenceSystem CRS for this geometry's vertices.
     * @param mutable Whether or not changes will be allowed.
     */
    public AbstractJTSGeometry(final CoordinateReferenceSystem coordinateReferenceSystem, final boolean mutable) {
        this.coordinateReferenceSystem = coordinateReferenceSystem;
        this.mutable = mutable;
    }

    public void setParent(final JTSGeometry parent) {
        this.parent = parent;
    }

    /**
     * Subclasses must override this method to compute the JTS equivalent of
     * this geometry.
     */
    protected abstract org.locationtech.jts.geom.Geometry computeJTSPeer();

    /**
     * This method must be called by subclasses whenever the user makes a change
     * to the geometry so that the cached JTS object can be recreated.
     */
    @Override
    public final void invalidateCachedJTSPeer() {
        jtsPeer = null;
        if (parent != null) {
            parent.invalidateCachedJTSPeer();
        }
    }

    /**
     * This method is meant to be invoked by the JTSUtils utility class when it
     * creates a Geometry from a JTS geometry.  This prevents the Geometry from
     * having to recompute the JTS peer the first time.
     */
    protected final void setJTSPeer(final org.locationtech.jts.geom.Geometry g) {
        jtsPeer = g;
    }

    /**
     * Returns the JTS version of this geometry.  If the geometry has not
     * changed since the last time this method was called, it will return the
     * exact same object.
     */
    @Override
    public final org.locationtech.jts.geom.Geometry getJTSGeometry() {
        if (jtsPeer == null) {
            jtsPeer = computeJTSPeer();
        }
        return jtsPeer;
    }

    /**
     * Returns the CRS that was given to the constructor.
     */
    @Override
    public final CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return coordinateReferenceSystem;
    }

    public final void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        this.coordinateReferenceSystem = crs;
    }

    /**
     * Returns a Geometry that represents the minimum bounding region of this
     * geometry.
     */
    @Override
    public final Geometry getMbRegion() {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        return JTSUtils.toISO(jtsGeom.getEnvelope(), getCoordinateReferenceSystem());
    }

    /**
     * Returns a point interior to the geometry.
     */
    @Override
    public final DirectPosition getRepresentativePoint() {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        org.locationtech.jts.geom.Point p = jtsGeom.getInteriorPoint();
        return JTSUtils.pointToDirectPosition(p, getCoordinateReferenceSystem());
    }

    /**
     * Returns the boundary of this geometry.  Returns null if the boundary is
     * empty.
     */
    @Override
    public Boundary getBoundary() {
        // PENDING(CSD):
        // Need to find out if MultiPrimitives are handled correctly.  (I think
        // they are, but 19107's boundary semantics for multi-primitives are
        // not well-specified.)
        // Need to find out if GeometryCollections are handled correctly.  (I
        // don't think they are, but it's not clear what it would mean, nor is
        // it obvious why anyone would call it in the first place.)

        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();

        // PENDING(CSD):
        // As far as I could tell, it's not defined what it would mean to
        // compute the boundary of a collection object in 19107.
        if (jtsGeom instanceof org.locationtech.jts.geom.GeometryCollection) {
            throw new UnsupportedOperationException(
                    "Boundary cannot be computed for multi-primitives.");
        }

        org.locationtech.jts.geom.Geometry jtsBoundary = jtsGeom.getBoundary();
        int d = jtsGeom.getDimension();
        if (d == 0) {
            // If d is zero, then our geometry is a point.  So the boundary is
            // empty.  ISO 19107 defines the boundary of a point to
            // be NULL.
            return null;
        } else if (d == 1) {
            // If d is 1, then the boundary is either empty (if it's a ring) or
            // it's two points at either end of the curve.
            // We've ruled out the possibility of multi-primitives (see the
            // instanceof check above), so we know that the boundary can't be
            // more than 2 points.

            org.locationtech.jts.geom.Coordinate[] coords = jtsBoundary.getCoordinates();
            // If coords is emtpy, then this geometry is a ring.  So we return
            // an empty CurveBoundary object (i.e. one with both points set to
            // null).
            if ((coords == null) || (coords.length == 0)) {
                JTSCurveBoundary result = new JTSCurveBoundary(
                        getCoordinateReferenceSystem(), null, null);
                return result;
            } else {
                // If it wasn't empty, then return a CurveBoundary with the two
                // endpoints.
                if (coords.length != 2) {
                    // Should this be an assert instead?
                    throw new RuntimeException("ERROR: One dimensional " +
                            "primitive had wrong number of boundary points (" +
                            coords.length + ")");
                }
                CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
                JTSCurveBoundary result = new JTSCurveBoundary(crs,
                        new JTSPoint(JTSUtils.coordinateToDirectPosition(
                        coords[0], crs)),
                        new JTSPoint(JTSUtils.coordinateToDirectPosition(
                        coords[1], crs)));
                return result;
            }
        } else if (d == 2) {
            // If d == 2, then the boundary is a collection of rings.
            // In particular, the JTS tests indicate that it'll be a
            // MultiLineString.
            org.locationtech.jts.geom.MultiLineString mls =
                    (org.locationtech.jts.geom.MultiLineString) jtsBoundary;
            int n = mls.getNumGeometries();
            CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
            Ring exteriorRing = JTSUtils.linearRingToRing(
                    (org.locationtech.jts.geom.LineString) mls.getGeometryN(0),
                    crs);
            Ring[] interiorRings = new Ring[n - 1];
            for (int i = 1; i < n; i++) {
                interiorRings[n - 1] = JTSUtils.linearRingToRing(
                        (org.locationtech.jts.geom.LineString) mls.getGeometryN(i),
                        crs);
            }
            JTSSurfaceBoundary result = new JTSSurfaceBoundary(crs,
                    exteriorRing, interiorRings);
            return result;
        } else {
            throw new UnsupportedOperationException("Computing the boundary " +
                    "for geometries of dimension larger than 2 is not " +
                    "supported.");
        }
    }

    /**
     * This method is not implemented.  Always throws an
     * UnsupportedOperationException.
     */
    @Override
    public final Complex getClosure() {
        throw new UnsupportedOperationException("Closure not supported");
    }

    /**
     * Returns true if this object does not cross itself.
     */
    @Override
    public final boolean isSimple() {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        return jtsGeom.isSimple();
    }

    @Override
    public final boolean isCycle() {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsBoundary = jtsGeom.getBoundary();
        return jtsBoundary.isEmpty();
    }

    /**
     * Returns the distance between the given geometry and this geometry.  Note
     * that this distance is in units the same as the units of the coordinate
     * reference system, and thus may not have any physical meaning (such as
     * when the coordinate system is a latitude/longitude system).
     */
    public final double getDistance(final Geometry geometry) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) geometry).getJTSGeometry();
        return JTSUtils.distance(jtsGeom1, jtsGeom2);
    }

    /**
     * Returns the manifold dimension of the geometry at the given point.  The
     * point must lie on the geometry.
     *
     * For geometries that consist of multiple parts, this returns the dimension
     * of the part intersecting the given point.  When multiple parts coincide
     * at the given point, this returns the least dimension of those geometries.
     * Returns Integer.MAX_VALUE if the given point is not on this geometry.
     */
    @Override
    public final int getDimension(final DirectPosition point) {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        if (jtsGeom instanceof org.locationtech.jts.geom.GeometryCollection) {
            org.locationtech.jts.geom.Point p =
                    JTSUtils.directPositionToPoint(point);
            return getDimension(p, (org.locationtech.jts.geom.GeometryCollection) jtsGeom);
        } else {
            return jtsGeom.getDimension();
        }
    }

    private static final int getDimension(
            final org.locationtech.jts.geom.Point p,
            final org.locationtech.jts.geom.GeometryCollection gc) {
        int min = Integer.MAX_VALUE;
        int n = gc.getNumGeometries();
        for (int i = 0; i < n; i++) {
            int d = Integer.MAX_VALUE;
            org.locationtech.jts.geom.Geometry g = gc.getGeometryN(i);
            if (g instanceof org.locationtech.jts.geom.GeometryCollection) {
                // If it was a nested GeometryCollection, then just recurse
                // until we get down to non-collections.
                d = getDimension(p, (org.locationtech.jts.geom.GeometryCollection) g);
            } else {
                if (g.intersects(p)) {
                    d = g.getDimension();
                }
            }
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    /**
     * Returns the dimension of the coordinates in this geometry.  This
     * delegates to the coordinate reference system, so it may throw a null
     * pointer exception if this geometry has no coordinate reference system.
     */
    @Override
    public final int getCoordinateDimension() {
        return getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
    }

    /**
     * This impementation of geometry does not support traversing this
     * association in this direction as it would require every geometry to know
     * about all of the larger geometries of which it is a part.  This would
     * add some memory usage and bookkeeping headaches for functionality that
     * will rarely, if ever, be used.  This this method always returns null.
     */
    @Override
    public final Set getMaximalComplex() {
        return null;
    }

    /**
     * Attempts to find a transform from the current CRS to the new CRS and
     * creates a new geometry by invoking that transform on each control point
     * of this geometry.
     */
    @Override
    public final Geometry transform(final CoordinateReferenceSystem newCRS) throws TransformException {
        try {
            MathTransform mt = CRS.findOperation(getCoordinateReferenceSystem(), newCRS, null).getMathTransform();
            return transform(newCRS, mt);
        } catch (OperationNotFoundException e) {
            throw new TransformException("Unable to find an operation", e);
        } catch (FactoryException e) {
            throw new TransformException("Factory exception", e);
        }
    }

    /**
     * Creates a new Geometry out of this one by invoking the given transform
     * on each control point of this geometry.
     */
    public final Geometry transform(final CoordinateReferenceSystem newCRS,
            final MathTransform transform) throws TransformException {
        // Get the JTS geometry
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        // Make a copy since we're going to modify its values
        jtsGeom = (org.locationtech.jts.geom.Geometry) jtsGeom.clone();
        // Get a local variable that has the src CRS
        CoordinateReferenceSystem oldCRS = getCoordinateReferenceSystem();
        // Do the actual work of transforming the vertices
        jtsGeom.apply(new MathTransformFilter(transform, oldCRS, newCRS));
        // Then convert back to a GO1 geometry
        return JTSUtils.toISO(jtsGeom, getCoordinateReferenceSystem());
    }

    /**
     * @inheritDoc
     * @see org.opengis.geometry.coordinate.#getEnvelope()
     */
    @Override
    public final Envelope getEnvelope() {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        org.locationtech.jts.geom.Envelope jtsEnv = jtsGeom.getEnvelopeInternal();
        CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        DirectPosition2D lower = new DirectPosition2D(jtsEnv.getMinX(), jtsEnv.getMinY());
        lower.setCoordinateReferenceSystem(crs);
        DirectPosition2D upper = new DirectPosition2D(jtsEnv.getMaxX(), jtsEnv.getMaxY());
        upper.setCoordinateReferenceSystem(crs);
        Envelope result = new JTSEnvelope(lower, upper);
        return result;
    }

    /**
     * Returns the centroid of this geometry.
     */
    @Override
    public final DirectPosition getCentroid() {
        org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        org.locationtech.jts.geom.Point jtsCentroid = jtsGeom.getCentroid();
        return JTSUtils.pointToDirectPosition(jtsCentroid,
                getCoordinateReferenceSystem());
    }

    /**
     * Returns the geometric convex hull of this geometry.
     */
    @Override
    public final Geometry getConvexHull() {
        final org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        final org.locationtech.jts.geom.Geometry jtsHull = jtsGeom.convexHull();
        return JTSUtils.toISO(jtsHull, getCoordinateReferenceSystem());
    }

    /**
     * Returns an approximate buffer around this object.
     */
    @Override
    public final Geometry getBuffer(final double distance) {
        final org.locationtech.jts.geom.Geometry jtsGeom = getJTSGeometry();
        final org.locationtech.jts.geom.Geometry jtsBuffer = jtsGeom.buffer(distance);
        return JTSUtils.toISO(jtsBuffer, getCoordinateReferenceSystem());
    }

    /**
     * Returns true if this geometry can be changed.
     */
    public final boolean isMutable() {
        return mutable;
    }

    /**
     * Creates an immutable copy of this object or just returns this object if
     * it's already immutable.
     */
    public final Geometry toImmutable() {
        if (isMutable()) {
            AbstractJTSGeometry result = clone();
            result.mutable = false;
            return result;
        } else {
            return this;
        }
    }

    /**
     * Returns a deep copy of this geometric object.  Subclasses must override
     * to make deep copies of members that are themselves mutable objects.  Note
     * that all of the (private) members of GeometryImpl are already immutable
     * so this method simply delegates to the superclass (Object) clone.
     */
    @Override
    public AbstractJTSGeometry clone() {
        try {
            return (AbstractJTSGeometry) super.clone();
        } catch (CloneNotSupportedException cnse) {
            throw new AssertionError(cnse);
        }
    }

    /**
     * Returns true if the given position lies in this geometry within the
     * tolerance of the floating point representation.
     */
    @Override
    public boolean contains(final DirectPosition point) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                JTSUtils.directPositionToPoint(point);
        return JTSUtils.contains(jtsGeom1, jtsGeom2);
    }

    /**
     * Returns true if this geometry completely contains the given geometry.
     */
    @Override
    public boolean contains(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.contains(jtsGeom1, jtsGeom2);
    }

    @Override
    public double distance(final Geometry otherGeometry) {
        return getDistance(otherGeometry);
    }

    @Override
    public TransfiniteSet difference(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.toISO(JTSUtils.difference(jtsGeom1, jtsGeom2),
                getCoordinateReferenceSystem());
    }

    @Override
    public boolean equals(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.equals(jtsGeom1, jtsGeom2);
    }

    @Override
    public TransfiniteSet intersection(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.toISO(jtsGeom1.intersection(jtsGeom2),
                getCoordinateReferenceSystem());
    }

    @Override
    public boolean intersects(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.intersects(jtsGeom1, jtsGeom2);
    }

    @Override
    public TransfiniteSet symmetricDifference(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.toISO(JTSUtils.symmetricDifference(jtsGeom1, jtsGeom2),
                getCoordinateReferenceSystem());
    }

    @Override
    public TransfiniteSet union(final TransfiniteSet pointSet) {
        org.locationtech.jts.geom.Geometry jtsGeom1 = getJTSGeometry();
        org.locationtech.jts.geom.Geometry jtsGeom2 =
                ((JTSGeometry) pointSet).getJTSGeometry();
        return JTSUtils.toISO(JTSUtils.union(jtsGeom1, jtsGeom2),
                getCoordinateReferenceSystem());
    }

    public static Set listAsSet(final List list) {
        return new Set() {

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public boolean isEmpty() {
                return list.isEmpty();
            }

            @Override
            public Object[] toArray() {
                return list.toArray();
            }

            @Override
            public boolean add(Object o) {
                return list.add(o);
            }

            @Override
            public boolean contains(Object o) {
                return list.contains(o);
            }

            @Override
            public boolean remove(Object o) {
                return list.remove(o);
            }

            @Override
            public boolean addAll(Collection c) {
                return list.addAll(c);
            }

            @Override
            public boolean containsAll(Collection c) {
                return list.containsAll(c);
            }

            @Override
            public boolean removeAll(Collection c) {
                return list.removeAll(c);
            }

            @Override
            public boolean retainAll(Collection c) {
                return list.retainAll(c);
            }

            @Override
            public Iterator iterator() {
                return list.iterator();
            }

            @Override
            public Object[] toArray(Object[] a) {
                return list.toArray(a);
            }
        };
    }

    /**
     * This class implements JTS's CoordinateFilter interface using a Types
     * MathTransform object to actually perform the work.
     */
    public static class MathTransformFilter implements org.locationtech.jts.geom.CoordinateFilter {

        private MathTransform transform;
        private DirectPosition src;
        private DirectPosition dst;

        public MathTransformFilter(final MathTransform transform,
                final CoordinateReferenceSystem oldCRS,
                final CoordinateReferenceSystem newCRS) {
            this.transform = transform;
            src = new GeneralDirectPosition(oldCRS);
            dst = new GeneralDirectPosition(newCRS);
        }

        @Override
        public void filter(final org.locationtech.jts.geom.Coordinate coord) {
            // Load the input into a DirectPosition
            JTSUtils.coordinateToDirectPosition(coord, src);
            try {
                // Do the transform math.
                transform.transform(src, dst);
            } catch (MismatchedDimensionException e) {
                throw new RuntimeException(e);
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
            // Load the result back into the Coordinate.
            JTSUtils.directPositionToCoordinate(dst, coord);
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this)
            return true;

        if (object instanceof AbstractJTSGeometry) {
            AbstractJTSGeometry that = (AbstractJTSGeometry) object;
            return Objects.equals(this.coordinateReferenceSystem, that.coordinateReferenceSystem) &&
                   Objects.equals(this.parent,                    that.parent);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.coordinateReferenceSystem != null ? this.coordinateReferenceSystem.hashCode() : 0);
        hash = 41 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getName()).append(']');
        sb.append('\n');
        if (coordinateReferenceSystem != null)
            sb.append("crs: ").append(coordinateReferenceSystem).append('\n');
        if (jtsPeer != null)
            sb.append("jtspeer: ").append(jtsPeer).append('\n');
        sb.append("mutable: ").append(mutable).append('\n');
        if (parent != null)
            sb.append("parent:").append(parent).append('\n');
        return sb.toString();
    }
}
