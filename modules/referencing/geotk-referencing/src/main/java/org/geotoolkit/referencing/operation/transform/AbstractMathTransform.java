/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation.transform;

import java.io.Serializable;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import org.opengis.metadata.Identifier;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.io.wkt.FormattableObject;
import org.apache.sis.util.Classes;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.internal.referencing.WKTUtilities;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.Accessor;

import static org.geotoolkit.util.Utilities.hash;


/**
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.operation.transform.AbstractMathTransform}.
 *
 * {@section Two-dimensional transforms}
 * {@code AbstractMathTransform} implements also the methods required by the {@link MathTransform2D} interface,
 * but <strong>does not</strong> implements that interface directly. Subclasses must add the
 * "{@code implements MathTransform2D}" clause themselves, or extend the {@link AbstractMathTransform2D} base class,
 * if they know to map two-dimensional coordinate systems.
 */
@Deprecated
public abstract class AbstractMathTransform extends org.apache.sis.referencing.operation.transform.AbstractMathTransform
        implements org.geotoolkit.io.wkt.Formattable
{
    /**
     * Constructs a math transform.
     */
    protected AbstractMathTransform() {
    }

    /**
     * Returns a name for this math transform (never {@code null}). This convenience methods
     * returns the name of the {@linkplain #getParameterDescriptors parameter descriptors} if
     * any, or the short class name otherwise.
     *
     * @return A name for this math transform (never {@code null}).
     *
     * @since 2.5
     */
    public String getName() {
        final ParameterDescriptorGroup descriptor = getParameterDescriptors();
        if (descriptor != null) {
            final Identifier identifier = descriptor.getName();
            if (identifier != null) {
                final String code = identifier.getCode();
                if (code != null) {
                    return code;
                }
            }
        }
        return Classes.getShortClassName(this);
    }

    /**
     * Constructs an error message for the {@link MismatchedDimensionException}.
     *
     * CAUTION: Argument order is different than in Apache SIS.
     *
     * @param argument  The argument name with the wrong number of dimensions.
     * @param dimension The wrong dimension.
     * @param expected  The expected dimension.
     */
    static String mismatchedDimension(final String argument, final int dimension, final int expected) {
        return Errors.format(Errors.Keys.MISMATCHED_DIMENSION_3, argument, dimension, expected);
    }

    /**
     * Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}.
     * The default implementation performs the following steps:
     * <p>
     * <ul>
     *   <li>Ensures that the {@linkplain #getSourceDimensions() source} and
     *       {@linkplain #getTargetDimensions() target dimensions} of this math
     *       transform are equal to 2.</li>
     *   <li>Delegates to the {@link #transform(double[],int,double[],int,boolean)}
     *       method using a temporary array of doubles.</li>
     * </ul>
     *
     * @param  ptSrc The coordinate point to be transformed.
     * @param  ptDst The coordinate point that stores the result of transforming {@code ptSrc},
     *               or {@code null} if a new point should be created.
     * @return The coordinate point after transforming {@code ptSrc} and storing the result in
     *         {@code ptDst}, or in a new point if {@code ptDst} was null.
     * @throws MismatchedDimensionException If this transform doesn't map two-dimensional
     *         coordinate systems.
     * @throws TransformException If the point can't be transformed.
     *
     * @see MathTransform2D#transform(Point2D,Point2D)
     */
    public Point2D transform(final Point2D ptSrc, final Point2D ptDst) throws TransformException {
        int dim;
        if ((dim = getSourceDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("ptSrc", 2, dim));
        }
        if ((dim = getTargetDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("ptDst", 2, dim));
        }
        final double[] ord = new double[] {ptSrc.getX(), ptSrc.getY()};
        transform(ord, 0, ord, 0, false);
        if (ptDst != null) {
            ptDst.setLocation(ord[0], ord[1]);
            return ptDst;
        } else {
            return new Point2D.Double(ord[0], ord[1]);
        }
    }

    /**
     * Transform the specified shape. The default implementation computes quadratic curves
     * using three points for each line segment in the shape. The returned object is often
     * a {@link Path2D}, but may also be a {@link Line2D} or a {@link QuadCurve2D} if such
     * simplification is possible.
     *
     * @param  shape Shape to transform.
     * @return Transformed shape, or {@code shape} if this transform is the identity transform.
     * @throws IllegalStateException if this transform doesn't map 2D coordinate systems.
     * @throws TransformException if a transform failed.
     *
     * @see MathTransform2D#createTransformedShape(Shape)
     */
    public Shape createTransformedShape(final Shape shape) throws TransformException {
        int dim;
        if ((dim = getSourceDimensions()) != 2 || (dim = getTargetDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("shape", 2, dim));
        }
        return isIdentity() ? shape : Accessor.createTransformedShape((MathTransform2D) this, shape, null, null, false);
    }

    /**
     * Gets the derivative of this transform at a point.
     * The default implementation performs the following steps:
     * <p>
     * <ul>
     *   <li>Ensure that this math transform {@linkplain #getSourceDimensions() source dimensions}
     *       is equals to 2. Note that the {@linkplain #getTargetDimensions() target dimensions}
     *       can be anything, not necessarily 2 (so this transform doesn't need to implement the
     *       {@link MathTransform2D} interface).</li>
     *   <li>Copy the coordinate in a temporary array and pass that array to the
     *       {@link #transform(double[], int, double[], int, boolean)} method,
     *       with the {@code derivate} boolean argument set to {@code true}.</li>
     *   <li>If the later method returned a non-null matrix, returns that matrix.
     *       Otherwise throws {@link TransformException}.</li>
     * </ul>
     *
     * @param  point The coordinate point where to evaluate the derivative.
     * @return The derivative at the specified point as a 2&times;2 matrix.
     * @throws MismatchedDimensionException if the input dimension is not 2.
     * @throws TransformException if the derivative can't be evaluated at the specified point.
     *
     * @see MathTransform2D#derivative(Point2D)
     */
    public Matrix derivative(final Point2D point) throws TransformException {
        final int dimSource = getSourceDimensions();
        if (dimSource != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("point", 2, dimSource));
        }
        final double[] coordinate = new double[] {point.getX(), point.getY()};
        final Matrix derivative = transform(coordinate, 0, null, 0, true);
        if (derivative == null) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        return derivative;
    }

    /**
     * Makes sure that an argument is non-null. This is a
     * convenience method for subclass constructors.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws InvalidParameterValueException if {@code object} is null.
     */
    protected static void ensureNonNull(final String name, final Object object)
            throws InvalidParameterValueException
    {
        if (object == null) {
            throw new InvalidParameterValueException(Errors.format(
                        Errors.Keys.NULL_ARGUMENT_1, name), name, object);
        }
    }

    /**
     * Default implementation for inverse math transform. This inner class is the inverse of
     * the enclosing {@link AbstractMathTransform}. It is serializable only if the enclosing
     * math transform is also serializable.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    protected abstract class Inverse extends AbstractMathTransform implements Serializable {
        /**
         * Serial number for inter-operability with different versions. This serial number is
         * especilly important for inner classes, since the default {@code serialVersionUID}
         * computation will not produce consistent results across implementations of different
         * Java compiler. This is because different compilers may generate different names for
         * synthetic members used in the implementation of inner classes. See:
         *
         * http://developer.java.sun.com/developer/bugParade/bugs/4211550.html
         */
        private static final long serialVersionUID = 3528274816628012283L;

        /**
         * Constructs an inverse math transform.
         */
        protected Inverse() {
        }

        /**
         * Returns a name for this math transform (never {@code null}). The default implementation
         * returns the direct transform name concatenated with localized flavor (when available)
         * of "(Inverse transform)".
         *
         * @return A name for this math transform (never {@code null}).
         *
         * @since 2.5
         */
        @Override
        public String getName() {
            return AbstractMathTransform.this.getName() +
                    " (" + Vocabulary.format(Vocabulary.Keys.INVERSE_TRANSFORM) + ')';
        }

        /**
         * Gets the dimension of input points. The default
         * implementation returns the dimension of output
         * points of the enclosing math transform.
         */
        @Override
        public int getSourceDimensions() {
            return AbstractMathTransform.this.getTargetDimensions();
        }

        /**
         * Gets the dimension of output points. The default
         * implementation returns the dimension of input
         * points of the enclosing math transform.
         */
        @Override
        public int getTargetDimensions() {
            return AbstractMathTransform.this.getSourceDimensions();
        }

        /**
         * Gets the derivative of this transform at a point. The default
         * implementation computes the inverse of the matrix returned by
         * the enclosing math transform.
         */
        @Override
        public Matrix derivative(final Point2D point) throws TransformException {
            return MatrixSIS.castOrCopy(AbstractMathTransform.this.derivative(this.transform(point, null))).inverse();
        }

        /**
         * Gets the derivative of this transform at a point. The default
         * implementation computes the inverse of the matrix returned by
         * the enclosing math transform.
         */
        @Override
        public Matrix derivative(final DirectPosition point) throws TransformException {
            return MatrixSIS.castOrCopy(AbstractMathTransform.this.derivative(this.transform(point, null))).inverse();
        }

        /**
         * Returns the enclosing class as a private method for protecting from user override.
         */
        private AbstractMathTransform enclosing() {
            return AbstractMathTransform.this;
        }

        /**
         * Returns the inverse of this math transform, which is the enclosing math transform.
         * This behavior should not be changed since some implementation assume that the inverse
         * of {@code this} is always {@code AbstractMathTransform.this}.
         */
        @Override
        public MathTransform inverse() {
            return AbstractMathTransform.this;
        }

        /**
         * Tests whether this transform does not move any points.
         * The default implementation delegates this tests to the
         * enclosing math transform.
         */
        @Override
        public boolean isIdentity() {
            return AbstractMathTransform.this.isIdentity();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected int computeHashCode() {
            return hash(AbstractMathTransform.this.hashCode(), super.computeHashCode());
        }

        /**
         * Compares the specified object with this inverse math transform for equality. The
         * default implementation tests if {@code object} in an instance of the same class
         * than {@code this}, and if so compares their enclosing {@code AbstractMathTransform}.
         */
        @Override
        public boolean equals(final Object object, final ComparisonMode mode) {
            if (object == this) {
                // Slight optimization
                return true;
            }
            if (object != null && object.getClass() == getClass()) {
                final Inverse that = (Inverse) object;
                return AbstractMathTransform.this.equals(that.enclosing(), mode);
            } else {
                return false;
            }
        }

        /**
         * Formats the inner part of a
         * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#INVERSE_MT"><cite>Well
         * Known Text</cite> (WKT)</A> element. If this inverse math transform
         * has any parameter values, then this method format the WKT as in the
         * {@linkplain AbstractMathTransform#formatWKT super-class method}. Otherwise
         * this method format the math transform as an <code>"INVERSE_MT"</code> entity.
         *
         * @param  formatter The formatter to use.
         * @return The WKT element name, which is {@code "PARAM_MT"} or
         *         {@code "INVERSE_MT"} in the default implementation.
         */
        @Override
        public String formatTo(final Formatter formatter) {
            final ParameterValueGroup parameters = getParameterValues();
            if (parameters != null) {
                WKTUtilities.appendParamMT(parameters, formatter);
                return "PARAM_MT";
            } else {
                formatter.append((FormattableObject) AbstractMathTransform.this);
                return "INVERSE_MT";
            }
        }
    }

    @Override
    public String formatTo(Formatter formatter) {
        return super.formatTo(formatter);
    }
}
