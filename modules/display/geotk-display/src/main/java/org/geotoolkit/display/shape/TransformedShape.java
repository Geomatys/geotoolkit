/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.geom.NoninvertibleTransformException;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;


/**
 * Applies an arbitrary {@link AffineTransform} on a {@link Shape}. A {@code TransformedShape}
 * instance is a <em>view</em> over a shape, i.e. the shape coordinates are transformed on the
 * fly, never copied.
 * <p>
 * {@code TransformedShape}s are mutable: the same instance can be recycled for different affine
 * transforms and different shapes. This class extends {@link AffineTransform} as a convenience,
 * to allow direct invocation of any {@code AffineTransform} methods, which take immediate effect.
 *
 * {@note This class is final because extending directly <code>AffineTransform</code> is not a
 *        good example of object-oriented programming - it is just a little convenience utility
 *        class that shoud not be replicated.}
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
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
     * Initializes the transient fields. Invoked on construction and on deserialization.
     */
    private void initTransientFields() {
        point = new Point2D.Double();
        rectangle = new Rectangle2D.Double();
    }

    /**
     * Ensures that the given argument value is not null.
     */
    private static void ensureNonNull(final String name, final Object value) {
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
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
     * Sets the shape for which this {@code TransformedShape} will be a views.
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
     *
     * @param  x The <var>x</var> ordinate of the point to be tested.
     * @param  y The <var>y</var> ordinate of the point to be tested.
     * @return {@code true} if this shape contains the given point.
     */
    @Override
    public boolean contains(final double x, final double y) {
        point.x = x;
        point.y = y;
        return contains(point);
    }

    /**
     * Tests if a specified {@link Point2D} is inside the boundary of this shape.
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
     *
     * @param  x The minimal <var>x</var> ordinate of the rectangle to be tested.
     * @param  y The minimal <var>y</var> ordinate of the rectangle to be tested.
     * @param  width The width of the rectangle to be tested.
     * @param  height The height of the rectangle to be tested.
     * @return {@code true} if this shape contains the given rectangle.
     */
    @Override
    public boolean contains(final double x, final double y, final double width, final double height) {
        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = width;
        rectangle.height = height;
        return contains(rectangle);
    }

    /**
     * Tests if the interior of this shape entirely contains the specified rectangle.
     * This method might conservatively return {@code false}.
     *
     * @param  r The rectangle to be tested.
     * @return {@code true} if this shape contains the given rectangle.
     */
    @Override
    public boolean contains(final Rectangle2D r) {
        try {
            return shape.contains(XAffineTransform.inverseTransform(this, r, rectangle));
        } catch (NoninvertibleTransformException exception) {
            Logging.recoverableException(TransformedShape.class, "contains", exception);
            return false;
        }
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangular area.
     *
     * @param  x The minimal <var>x</var> ordinate of the rectangle to be tested.
     * @param  y The minimal <var>y</var> ordinate of the rectangle to be tested.
     * @param  width The width of the rectangle to be tested.
     * @param  height The height of the rectangle to be tested.
     * @return {@code true} if this shape intersects the given rectangle.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = width;
        rectangle.height = height;
        return intersects(rectangle);
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangle.
     * This method might conservatively return {@code true}.
     *
     * @param  r The rectangle to be tested.
     * @return {@code true} if this shape intersects the given rectangle.
     */
    @Override
    public boolean intersects(final Rectangle2D r) {
        try {
            return shape.intersects(XAffineTransform.inverseTransform(this, r, rectangle));
        } catch (NoninvertibleTransformException exception) {
            Logging.recoverableException(TransformedShape.class, "intersects", exception);
            return false;
        }
    }

    /**
     * Returns an integer rectangle that completely encloses this shape.
     */
    @Override
    public Rectangle getBounds() {
        // Delegates to getBounds2D(), not getBounds(), because a scale greater than 1
        // will use the fraction digits of a number that would be otherwise rounded.
        return (Rectangle) XAffineTransform.transform(this, shape.getBounds2D(), new Rectangle());
    }

    /**
     * Returns a high precision and more accurate bounding box of the
     * shape than the {@link #getBounds} method.
     */
    @Override
    public Rectangle2D getBounds2D() {
        return XAffineTransform.transform(this, shape.getBounds2D(), null);
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
     * Invoked on deserialization.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readObject();
        initTransientFields();
    }
}
