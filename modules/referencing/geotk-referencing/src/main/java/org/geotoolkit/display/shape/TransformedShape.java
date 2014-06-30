/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.util.Objects;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.io.ObjectInputStream;
import net.jcip.annotations.NotThreadSafe;

import org.apache.sis.util.logging.Logging;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Applies an arbitrary {@link AffineTransform} on a {@link Shape}. A {@code TransformedShape}
 * instance is a <em>view</em> over a shape, i.e. the shape coordinates are transformed on the
 * fly, never copied.
 * <p>
 * The shape to be transformed is specified by {@link #setOriginalShape(Shape)}. The transform
 * to apply is specified by the inherited {@code AffineTransform}, which can be modified at any
 * time using all its usual methods. Every changes to the original shape or to the inherited
 * transform take immediate effect since this class is only a view.
 *
 * {@note This class is final because extending directly <code>AffineTransform</code> is not a
 *        good example of object-oriented programming, since a transformed shape is not a special
 *        kind of affine transform. We did that as a convenience for allowing frequent modifications
 *        of the transform by direct access to the <code>AffineTransform</code> methods, but this is
 *        not an example that shoud be replicated.}
 *
 * {@section Performance cost}
 * When {@linkplain #getPathIterator(AffineTransform) iterating over the shape boundary}, the only
 * performance cost is a {@linkplain #concatenate(AffineTransform) matrix multiplication} applied
 * <strong>before</strong> the iterator is created - the cost of the iteration itself usually stay
 * unchanged.
 *
 * {@section Example}
 * The {@link Arrow2D} class is unconditionally oriented toward 0° arithmetic.
 * In order to draw a field of arrows in arbitrary directions, the code below can be used:
 *
 * {@preformat java
 *     protected void paint(Graphics2D graphics) {
 *         Arrow2D arrow = new Arrow2D(...);
 *         TransformedShape orientedArrow = new TransformedShape(arrow);
 *         for (int i=0; i<numArrows; i++) {
 *             double scale = ...
 *             double orientation = Math.toRadians(...);
 *             orientedArrow.setToScale(scale, scale);
 *             orientedArrow.rotate(orientation);
 *             graphics.fill(orientedArrow);
 *         }
 *     }
 * }
 *
 * An alternative to this {@code TransformedShape} class would be to change directly the
 * {@link java.awt.Graphics2D} transform. The main difference between those alternatives
 * is that when using {@code TransformedShape}, the transform does not apply to the line
 * thickness or paint texture. The preferred alternative depends on the desired rendering
 * effect, for example if line thickness needs to be specified in pixel units or in
 * "<cite>real world</cite>" units.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see ProjectedShape
 *
 * @since 2.0
 * @module
 */
@NotThreadSafe
public final class TransformedShape extends AffineTransform implements Shape {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3541606381365714951L;

    /**
     * The original shape for which this {@code TransformedShape} is a view.
     */
    private Shape shape;

    /**
     * A temporary point.
     */
    private transient Point2D.Double point;

    /**
     * A temporary rectangle.
     */
    private transient Rectangle2D.Double rectangle;

    /**
     * Constructs a {@code TransformedShape} initialized to the identity transform
     * with no original shape. The {@link #setOriginalShape(Shape)} method must be
     * invoked at least once before this object can be used.
     */
    public TransformedShape() {
        initTransientFields();
    }

    /**
     * Constructs a {@code TransformedShape} initialized to the given transform with no
     * original shape. The {@link #setOriginalShape(Shape)} method must be invoked at
     * least once before this object can be used.
     *
     * @param transform The initial affine transform.
     */
    public TransformedShape(final AffineTransform transform) {
        super(transform);
        initTransientFields();
    }

    /**
     * Constructs a view of the given shape initialized to the identity transform.
     *
     * @param shape The shape to wrap in the new {@code TransformedShape} instance.
     */
    public TransformedShape(final Shape shape) {
        ensureNonNull("shape", shape);
        this.shape = shape;
        initTransientFields();
    }

    /**
     * Constructs a {@code TransformedShape} initialized to the given transform and shape.
     *
     * @param shape The shape to wrap in the new {@code TransformedShape} instance.
     * @param transform The initial affine transform.
     *
     * @since 3.20
     */
    public TransformedShape(final Shape shape, final AffineTransform transform) {
        super(transform);
        ensureNonNull("shape", shape);
        this.shape = shape;
        initTransientFields();
    }

    /**
     * Initializes the transient fields. Invoked on construction and on deserialization.
     */
    private void initTransientFields() {
        point = new Point2D.Double();
        rectangle = new Rectangle2D.Double();
    }

    /**
     * Returns the original shape for which this {@code TransformedShape} is a view.
     *
     * @return The shape wrapped by this {@code TransformedShape} instance.
     */
    public Shape getOriginalShape() {
        return shape;
    }

    /**
     * Sets the shape for which this {@code TransformedShape} will be a view.
     *
     * @param shape The shape to wrap in this {@code TransformedShape} instance.
     */
    public void setOriginalShape(final Shape shape) {
        ensureNonNull("shape", shape);
        this.shape = shape;
    }

    /**
     * Returns the 6 coefficients values as {@code float} numbers. The coefficients are stored
     * in the same order than {@link #getMatrix(double[])}, with the first coefficient stored
     * in {@code matrix[offset]} and the last coefficient stored in {@code matrix[offset+5]}
     *
     * @param matrix The matrix where to store the coefficient values.
     * @param offset Index of the first element where to write in the {@code matrix} array.
     */
    public void getMatrix(final float[] matrix, int offset) {
        matrix[  offset] = (float) getScaleX();     // m00
        matrix[++offset] = (float) getShearY();     // m10
        matrix[++offset] = (float) getShearX();     // m01
        matrix[++offset] = (float) getScaleY();     // m11
        matrix[++offset] = (float) getTranslateX(); // m02
        matrix[++offset] = (float) getTranslateY(); // m12
    }

    /**
     * Sets the transform from a flat matrix. The coefficients are read in the same order
     * than they were stored by {@link #getMatrix}.
     *
     * @param matrix The flat matrix.
     * @param offset Index of the first element to use in {@code matrix} array.
     */
    public void setTransform(final float[] matrix, int offset) {
        setTransform(matrix[  offset], matrix[++offset], matrix[++offset],
                     matrix[++offset], matrix[++offset], matrix[++offset]);
    }

    /**
     * Tests if the specified coordinate is inside the boundary of this shape.
     * This method might conservatively return {@code false} if the transform is not invertible.
     * <p>
     * The default implementation delegates to {@link #contains(Point2D)}.
     *
     * @param  x The <var>x</var> ordinate of the point to be tested.
     * @param  y The <var>y</var> ordinate of the point to be tested.
     * @return {@code true} if this shape contains the given point.
     */
    @Override
    public boolean contains(final double x, final double y) {
        final Point2D.Double p = point;
        p.x = x;
        p.y = y;
        return contains(p);
    }

    /**
     * Tests if a specified {@link Point2D} is inside the boundary of this shape.
     * This method might conservatively return {@code false} if the transform is not invertible.
     *
     * @param  p The point to be tested.
     * @return {@code true} if this shape contains the given point.
     */
    @Override
    public boolean contains(final Point2D p) {
        try {
            return shape.contains(inverseTransform(p, point));
        } catch (NoninvertibleTransformException exception) {
            Logging.recoverableException(TransformedShape.class, "contains", exception);
            return false;
        }
    }

    /**
     * Tests if the interior of this shape entirely contains the specified rectangular area.
     * This method might conservatively return {@code false} if the transform is not invertible
     * or if the calculation of {@code originalShape.contains(...)} is too expensive.
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
        final Rectangle2D.Double r = rectangle;
        r.x = x;
        r.y = y;
        r.width = width;
        r.height = height;
        return contains(r);
    }

    /**
     * Tests if the interior of this shape entirely contains the specified rectangle.
     * This method might conservatively return {@code false} if the transform is not
     * invertible or if the calculation of {@code originalShape.contains(...)} is too
     * expensive.
     *
     * @param  r The rectangle to be tested.
     * @return {@code true} if this shape contains the given rectangle.
     */
    @Override
    public boolean contains(final Rectangle2D r) {
        try {
            return shape.contains(AffineTransforms2D.inverseTransform(this, r, rectangle));
        } catch (NoninvertibleTransformException exception) {
            Logging.recoverableException(TransformedShape.class, "contains", exception);
            return false; // Consistent with the Shape interface contract.
        }
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangular area.
     * This method might conservatively return {@code true} if the transform is not invertible or
     * if the calculation of {@code originalShape.intersects(...)} is too expensive.
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
        final Rectangle2D.Double r = rectangle;
        r.x = x;
        r.y = y;
        r.width = width;
        r.height = height;
        return intersects(r);
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangle.
     * This method might conservatively return {@code true} if the transform is not invertible
     * or if the calculation of {@code originalShape.intersects(...)} is too expensive.
     *
     * @param  r The rectangle to be tested.
     * @return {@code true} if this shape intersects the given rectangle.
     */
    @Override
    public boolean intersects(final Rectangle2D r) {
        try {
            return shape.intersects(AffineTransforms2D.inverseTransform(this, r, rectangle));
        } catch (NoninvertibleTransformException exception) {
            Logging.recoverableException(TransformedShape.class, "intersects", exception);
            return true; // Consistent with the Shape interface contract.
        }
    }

    /**
     * Returns an integer rectangle that completely encloses this shape.
     */
    @Override
    public Rectangle getBounds() {
        // Delegates to getBounds2D(), not getBounds(), because a scale greater than 1
        // will use the fraction digits of a number that would be otherwise rounded.
        return (Rectangle) AffineTransforms2D.transform(this, shape.getBounds2D(), new Rectangle());
    }

    /**
     * Returns a high precision and more accurate bounding box of the
     * shape than the {@link #getBounds} method.
     */
    @Override
    public Rectangle2D getBounds2D() {
        return AffineTransforms2D.transform(this, shape.getBounds2D(), null);
    }

    /**
     * Returns an iterator object that iterates along the shape boundary
     * and provides access to the geometry of the shape outline.
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        if (!isIdentity()) {
            if (at == null || at.isIdentity()) {
                return shape.getPathIterator(this);
            }
            at = new AffineTransform(at);
            at.concatenate(this);
        }
        return shape.getPathIterator(at);
    }

    /**
     * Returns an iterator object that iterates along the shape boundary and provides
     * access to a flattened view of the shape outline geometry.
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at, final double flatness) {
        if (!isIdentity()) {
            if (at == null || at.isIdentity()) {
                return shape.getPathIterator(this, flatness);
            }
            at = new AffineTransform(at);
            at.concatenate(this);
        }
        return shape.getPathIterator(at, flatness);
    }

    /**
     * Returns a hash code value for this shape.
     *
     * @since 3.20
     */
    @Override
    public int hashCode() {
        int code = super.hashCode() ^ (int) serialVersionUID;
        if (shape != null) {
            code ^= shape.hashCode();
        }
        return code;
    }

    /**
     * Compares this shape with the given object for equality. <strong>Do not compare
     * {@code TransformedShape} instances with plain {@link AffineTransform} instances</strong>,
     * because the "<cite>be symmetric</cite>" part of the {@link Object#equals(Object)} contract
     * can not be enforced.
     *
     * @param object The object to compare with this shape.
     *
     * @since 3.20
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof TransformedShape && super.equals(object)) {
            return Objects.equals(shape, ((TransformedShape) object).shape);
        }
        return false;
    }

    /**
     * Invoked on deserialization.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readObject();
        initTransientFields();
    }
}
