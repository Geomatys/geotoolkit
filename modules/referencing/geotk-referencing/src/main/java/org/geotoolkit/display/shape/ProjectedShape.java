/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.display.shape;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.io.Serializable;
import org.apache.sis.geometry.Shapes2D;

import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;


/**
 * Applies an arbitrary {@link MathTransform2D} (typically a projection) on a {@link Shape}.
 * A {@code ProjectedShape} instance is a <em>view</em> over a shape, i.e. the shape coordinates
 * are transformed on the fly, never copied. Consequently this class consumes very few memory,
 * but may consume more CPU if the same shape is rendered many time.
 *
 * {@section Straight lines and curves}
 * Straight line segments may become curves after the transform. This class can create quadratic
 * or cubic curves approximating the original line segments.
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see TransformedShape
 *
 * @since 3.20
 * @module
 */
public class ProjectedShape implements Shape, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -583674918489345612L;

    /**
     * The original shape to transform.
     */
    protected final Shape shape;

    /**
     * The operation to apply on the original shape.
     */
    protected final MathTransform2D projection;

    /**
     * The inverse of {@link #projection}, computed when first needed.
     */
    private transient MathTransform2D inverse;

    /**
     * A temporary point.
     */
    private transient Point2D.Double point;

    /**
     * A temporary rectangle.
     */
    private transient Rectangle2D.Double rectangle;

    /**
     * Creates a new shape as a transform of the given shape. This constructor is for subclasses
     * only. Users should invoke the static {@link #wrap(Shape, MathTransform2D)} method instead.
     *
     * @param shape      The original shape to transform.
     * @param projection The operation to apply on the given shape.
     */
    protected ProjectedShape(final Shape shape, final MathTransform2D projection) {
        ArgumentChecks.ensureNonNull("shape", shape);
        ArgumentChecks.ensureNonNull("projection", projection);
        this.shape = shape;
        this.projection = projection;
    }

    /**
     * Returns a new shape wrapping the given shape and applying the given transform on the fly.
     * If the transform is {@code null} or an {@linkplain MathTransform2D#isIdentity() identity}
     * transform, then this method returns the given shape unchanged. Otherwise if the transform
     * is {@linkplain AffineTransform affine}, then this method wraps the given shape in an
     * {@link TransformedShape}. Otherwise this method wraps the given shape in a
     * {@code ProjectedShape}.
     *
     * @param  shape      The original shape to transform.
     * @param  projection The operation to apply on the given shape.
     * @return A view over the given shape, transformed by the given transform.
     */
    public static Shape wrap(final Shape shape, final MathTransform2D projection) {
        if (projection == null || projection.isIdentity()) {
            return shape;
        }
        if (projection instanceof AffineTransform) {
            return new TransformedShape(shape, (AffineTransform) projection);
        }
        return new ProjectedShape(shape, projection);
    }

    /**
     * Returns the inverse of {@link #projection}. This method initializes also the
     * {@link #point} and {@link #rectangle} fields as a side-effect, since they will
     * be needed together with the inverse transform.
     */
    private MathTransform2D inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            inverse = projection.inverse();
            if (point     == null) point     = new Point2D    .Double();
            if (rectangle == null) rectangle = new Rectangle2D.Double();
        }
        return inverse;
    }

    /**
     * Tests if the specified coordinate is inside the boundary of this shape.
     * This method might conservatively return {@code false} if the point can
     * not be transformed.
     * <p>
     * The default implementation delegates to {@link #contains(Point2D)}.
     *
     * @param  x The <var>x</var> ordinate of the point to be tested.
     * @param  y The <var>y</var> ordinate of the point to be tested.
     * @return {@code true} if this shape contains the given point.
     */
    @Override
    public boolean contains(final double x, final double y) {
        Point2D.Double p = point;
        if (p == null) {
            point = p = new Point2D.Double();
        }
        p.x = x;
        p.y = y;
        return contains(p);
    }

    /**
     * Tests if a specified {@link Point2D} is inside the boundary of this shape.
     * This method might conservatively return {@code false} if the point can not
     * be transformed.
     *
     * @param  p The point to be tested.
     * @return {@code true} if this shape contains the given point.
     */
    @Override
    public boolean contains(final Point2D p) {
        try {
            return shape.contains(inverse().transform(p, point));
        } catch (TransformException exception) {
            Logging.recoverableException(null, ProjectedShape.class, "contains", exception);
            return false;
        }
    }

    /**
     * Tests if the interior of this shape entirely contains the specified rectangular area.
     * This method might conservatively return {@code false} if some points can not be
     * transformed.
     * <p>
     * The default implementation delegates to {@link #contains(Rectangle2D)}.
     *
     * @param  x The minimal <var>x</var> ordinate of the rectangle to be tested.
     * @param  y The minimal <var>y</var> ordinate of the rectangle to be tested.
     * @param  width The width of the rectangle to be tested.
     * @param  height The height of the rectangle to be tested.
     * @return {@code true} if this shape contains the given rectangle.
     */
    @Override
    public boolean contains(final double x, final double y, final double width, final double height) {
        Rectangle2D.Double r = rectangle;
        if (r == null) {
            rectangle = r = new Rectangle2D.Double();
        }
        r.x = x;
        r.y = y;
        r.width = width;
        r.height = height;
        return contains(r);
    }

    /**
     * Tests if the interior of this shape entirely contains the specified rectangle.
     * This method might conservatively return {@code false} if some points can not
     * be transformed.
     *
     * @param  r The rectangle to be tested.
     * @return {@code true} if this shape contains the given rectangle.
     */
    @Override
    public boolean contains(final Rectangle2D r) {
        try {
            return shape.contains(Shapes2D.transform(inverse(), r, rectangle));
        } catch (TransformException exception) {
            Logging.recoverableException(null, ProjectedShape.class, "contains", exception);
            return false; // Consistent with the Shape interface contract.
        }
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangular area.
     * This method might conservatively return {@code true} if some points can not be transformed.
     * <p>
     * The default implementation delegates to {@link #intersects(Rectangle2D)}.
     *
     * @param  x The minimal <var>x</var> ordinate of the rectangle to be tested.
     * @param  y The minimal <var>y</var> ordinate of the rectangle to be tested.
     * @param  width The width of the rectangle to be tested.
     * @param  height The height of the rectangle to be tested.
     * @return {@code true} if this shape intersects the given rectangle.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        Rectangle2D.Double r = rectangle;
        if (r == null) {
            rectangle = r = new Rectangle2D.Double();
        }
        r.x = x;
        r.y = y;
        r.width = width;
        r.height = height;
        return intersects(r);
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangle.
     * This method might conservatively return {@code true} if some points can not be transformed.
     *
     * @param  r The rectangle to be tested.
     * @return {@code true} if this shape intersects the given rectangle.
     */
    @Override
    public boolean intersects(final Rectangle2D r) {
        try {
            return shape.intersects(Shapes2D.transform(inverse(), r, rectangle));
        } catch (TransformException exception) {
            Logging.recoverableException(null, ProjectedShape.class, "intersects", exception);
            return true; // Consistent with the Shape interface contract.
        }
    }

    /**
     * Returns an integer rectangle that completely encloses this shape.
     * <p>
     * The default implementation delegates to {@link #getBounds2D()} and cast the resulting
     * rectangle.
     */
    @Override
    public Rectangle getBounds() {
        final Rectangle bounds = new Rectangle();
        bounds.setRect(bounds);
        return bounds;
    }

    /**
     * Returns a high precision and more accurate bounding box of the shape than the
     * {@link #getBounds} method. This method returns an infinite rectangle if some
     * points can not be transformed.
     */
    @Override
    public Rectangle2D getBounds2D() {
        try {
            return Shapes2D.transform(projection, shape.getBounds2D(), null);
        } catch (TransformException exception) {
            Logging.recoverableException(null, ProjectedShape.class, "getBounds2D", exception);
            return XRectangle2D.INFINITY;
        }
    }

    /**
     * Returns the concatenation of the {@linkplain #projection} with the given affine transform.
     *
     * @param at The affine transform to concatenate, or {@code null} if none.
     */
    private MathTransform2D concatenate(final AffineTransform at) {
        MathTransform2D concatenated = projection;
        if (at != null && !at.isIdentity()) {
            concatenated = MathTransforms.concatenate(concatenated, new AffineTransform2D(at));
        }
        return concatenated;
    }

    /**
     * Returns an iterator object that iterates along the shape boundary
     * and provides access to the geometry of the shape outline.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {
        final MathTransform2D concatenated = concatenate(at);
        if (concatenated.isIdentity()) {
            return shape.getPathIterator(at);
        }
        return new ProjectedPathIterator(shape.getPathIterator(null), concatenated);
    }

    /**
     * Returns an iterator object that iterates along the shape boundary and provides
     * access to a flattened view of the shape outline geometry.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
        final MathTransform2D concatenated = concatenate(at);
        if (concatenated.isIdentity()) {
            return shape.getPathIterator(at, flatness);
        }
        return new FlatteningPathIterator(new ProjectedPathIterator(shape.getPathIterator(null), concatenated), flatness);
    }

    /**
     * Returns a hash code value for this shape.
     */
    @Override
    public int hashCode() {
        return shape.hashCode() ^ projection.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Compares this shape with the given object for equality.
     *
     * @param object The object to compare with this shape.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof ProjectedShape) {
            final ProjectedShape other = (ProjectedShape) object;
            return shape.equals(other.shape) && projection.equals(other.projection);
        }
        return false;
    }
}
