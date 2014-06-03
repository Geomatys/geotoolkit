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

import java.util.List;
import java.io.Serializable;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;
import net.jcip.annotations.ThreadSafe;

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
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.apache.sis.util.Classes;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.util.Utilities.hash;
import org.apache.sis.internal.referencing.WKTUtilities;


/**
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.operation.transform.AbstractMathTransform}.
 *
 * {@section Two-dimensional transforms}
 * {@code AbstractMathTransform} implements also the methods required by the {@link MathTransform2D} interface,
 * but <strong>does not</strong> implements that interface directly. Subclasses must add the
 * "{@code implements MathTransform2D}" clause themselves, or extend the {@link AbstractMathTransform2D} base class,
 * if they know to map two-dimensional coordinate systems.
 */
@ThreadSafe
@Deprecated
public abstract class AbstractMathTransform extends org.apache.sis.referencing.operation.transform.AbstractMathTransform
        implements org.geotoolkit.io.wkt.Formattable
{
    /**
     * Copy of SIS constant.
     */
    static final int MAXIMUM_BUFFER_SIZE = 512;

    /**
     * Copy of SIS constant.
     */
    static final int MAXIMUM_FAILURES = 32;

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
        return isIdentity() ? shape : createTransformedShape(shape, null, null, false);
    }

    /**
     * Transforms a geometric shape. This method always copy transformed coordinates in a new
     * object. The new object is often a {@link Path2D}, but may also be a {@link Line2D} or a
     * {@link QuadCurve2D} if such simplification is possible.
     *
     * @param  shape         The geometric shape to transform.
     * @param  preTransform  An optional affine transform to apply <em>before</em> the
     *                       transformation using {@code this}, or {@code null} if none.
     * @param  postTransform An optional affine transform to apply <em>after</em> the transformation
     *                       using {@code this}, or {@code null} if none.
     * @param  horizontal    {@code true} for forcing parabolic equation.
     *
     * @return The transformed geometric shape.
     * @throws MismatchedDimensionException if this transform doesn't is not two-dimensional.
     * @throws TransformException If a transformation failed.
     */
    final Shape createTransformedShape(final Shape           shape,
                                       final AffineTransform preTransform,
                                       final AffineTransform postTransform,
                                       final boolean         horizontal)
            throws TransformException
    {
        int dim;
        if ((dim = getSourceDimensions()) != 2 || (dim = getTargetDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("shape", 2, dim));
        }
        final PathIterator     it = shape.getPathIterator(preTransform);
        final Path2D.Double  path = new Path2D.Double(it.getWindingRule());
        final double[]     buffer = new double[6];

        double ax=0, ay=0;  // Coordinate of the last point before transform.
        double px=0, py=0;  // Coordinate of the last point after  transform.
        for (; !it.isDone(); it.next()) {
            switch (it.currentSegment(buffer)) {
                default: {
                    throw new IllegalPathStateException();
                }
                case PathIterator.SEG_CLOSE: {
                    /*
                     * Closes the geometric shape and continues the loop. We use the 'continue'
                     * instruction here instead of 'break' because we don't want to execute the
                     * code after the switch (addition of transformed points into the path - there
                     * is no such point in a SEG_CLOSE).
                     */
                    path.closePath();
                    continue;
                }
                case PathIterator.SEG_MOVETO: {
                    /*
                     * Transforms the single point and adds it to the path. We use the 'continue'
                     * instruction here instead of 'break' because we don't want to execute the
                     * code after the switch (addition of a line or a curve - there is no such
                     * curve to add here; we are just moving the cursor).
                     */
                    ax = buffer[0];
                    ay = buffer[1];
                    transform(buffer, 0, buffer, 0, 1);
                    px = buffer[0];
                    py = buffer[1];
                    path.moveTo(px, py);
                    continue;
                }
                case PathIterator.SEG_LINETO: {
                    /*
                     * Inserts a new control point at 'buffer[0,1]'. This control point will
                     * be initialised with coordinates in the middle of the straight line:
                     *
                     *  x = 0.5*(x1+x2)
                     *  y = 0.5*(y1+y2)
                     *
                     * This point will be transformed after the 'switch', which is why we use
                     * the 'break' statement here instead of 'continue' as in previous case.
                     */
                    buffer[0] = 0.5*(ax + (ax=buffer[0]));
                    buffer[1] = 0.5*(ay + (ay=buffer[1]));
                    buffer[2] = ax;
                    buffer[3] = ay;
                    break;
                }
                case PathIterator.SEG_QUADTO: {
                    /*
                     * Replaces the control point in 'buffer[0,1]' by a new control point lying
                     * on the quadratic curve. Coordinates for a point in the middle of the curve
                     * can be computed with:
                     *
                     *  x = 0.5*(ctrlx + 0.5*(x1+x2))
                     *  y = 0.5*(ctrly + 0.5*(y1+y2))
                     *
                     * There is no need to keep the old control point because it was not lying
                     * on the curve.
                     */
                    buffer[0] = 0.5*(buffer[0] + 0.5*(ax + (ax=buffer[2])));
                    buffer[1] = 0.5*(buffer[1] + 0.5*(ay + (ay=buffer[3])));
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    /*
                     * Replaces the control point in 'buffer[0,1]' by a new control point lying
                     * on the cubic curve. Coordinates for a point in the middle of the curve
                     * can be computed with:
                     *
                     *  x = 0.25*(1.5*(ctrlx1+ctrlx2) + 0.5*(x1+x2));
                     *  y = 0.25*(1.5*(ctrly1+ctrly2) + 0.5*(y1+y2));
                     *
                     * There is no need to keep the old control point because it was not lying
                     * on the curve.
                     *
                     * NOTE: Le point calculé est bien sur la courbe, mais n'est pas
                     *       nécessairement représentatif. Cet algorithme remplace les
                     *       deux points de contrôles par un seul, ce qui se traduit par
                     *       une perte de souplesse qui peut donner de mauvais résultats
                     *       si la courbe cubique était bien tordue. Projeter une courbe
                     *       cubique ne me semble pas être un problème simple, mais ce
                     *       cas devrait être assez rare. Il se produira le plus souvent
                     *       si on essaye de projeter un cercle ou une ellipse, auxquels
                     *       cas l'algorithme actuel donnera quand même des résultats
                     *       tolérables.
                     */
                    buffer[0] = 0.25*(1.5*(buffer[0]+buffer[2]) + 0.5*(ax + (ax=buffer[4])));
                    buffer[1] = 0.25*(1.5*(buffer[1]+buffer[3]) + 0.5*(ay + (ay=buffer[5])));
                    buffer[2] = ax;
                    buffer[3] = ay;
                    break;
                }
            }
            /*
             * Applies the transform on the point in the buffer, and append the transformed points
             * to the general path. Try to add them as a quadratic line, or as a straight line if
             * the computed control point is colinear with the starting and ending points.
             */
            transform(buffer, 0, buffer, 0, 2);
            final Point2D ctrlPoint = ShapeUtilities.parabolicControlPoint(px, py,
                                                     buffer[0], buffer[1],
                                                     buffer[2], buffer[3],
                                                     horizontal);
            px = buffer[2];
            py = buffer[3];
            if (ctrlPoint != null) {
                path.quadTo(ctrlPoint.getX(), ctrlPoint.getY(), px, py);
            } else {
                path.lineTo(px, py);
            }
        }
        /*
         * La projection de la forme géométrique est terminée. Applique
         * une transformation affine si c'était demandée, puis retourne
         * une version si possible simplifiée de la forme géométrique.
         */
        if (postTransform != null) {
            path.transform(postTransform);
        }
        return ShapeUtilities.toPrimitive(path);
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
     * Copy of Apache SIS method. See super-class.
     */
    MathTransform concatenate(final MathTransform other, final boolean applyOtherFirst) {
        return null;
    }

    /**
     * Copy of Apache SIS method. See super-class.
     */
    static boolean equals(final LinearTransform t1, final Object t2, final ComparisonMode mode) {
        if (t2 instanceof LinearTransform) {
            final Matrix m1 = t1.getMatrix();
            if (m1 != null) {
                final Matrix m2 = ((LinearTransform) t2).getMatrix();
                if (m1 instanceof org.apache.sis.util.LenientComparable) {
                    return ((org.apache.sis.util.LenientComparable) m1).equals(m2, mode);
                }
                return Matrices.equals(m1, m2, mode);
            }
        }
        return false;
    }

    /**
     * Copy of Apache SIS method. See super-class.
     */
    int beforeFormat(List<Object> transforms, int index, boolean inverse) {
        return index;
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
     * Ensures that the specified longitude stay within the [-<var>bound</var> &hellip;
     * <var>bound</var>] range. This method is typically invoked before to project geographic
     * coordinates. It may add or subtract some amount of 2&times;<var>bound</var>
     * from <var>x</var>.
     * <p>
     * The <var>bound</var> value is typically 180 if the longitude is express in degrees,
     * or {@link Math#PI} it the longitude is express in radians. But it can also be some
     * other value if the longitude has already been multiplied by a scale factor before
     * this method is invoked.
     *
     * @param  x The longitude.
     * @param  bound The absolute value of the minimal and maximal allowed value, or
     *         {@link Double#POSITIVE_INFINITY} if no rolling should be applied.
     * @return The longitude between &plusmn;<var>bound</var>.
     */
    protected static double rollLongitude(double x, final double bound) {
        /*
         * Note: we could do the same than the code below with this single line
         * (assuming bound == PI):
         *
         *     return x - (2*PI) * floor(x / (2*PI) + 0.5);
         *
         * However the code below tries to reduce the amount of floating point operations: only
         * a division followed by a cast to (long) in the majority of cases. The multiplication
         * happen only if there is effectively a rolling to apply. All the remaining operations
         * are using integer arithmetic, so it should be fast.
         *
         * Note: usage of long instead of int is necessary, otherwise overflows do occur.
         */
        long n = (long) (x / bound); // Really want rounding toward zero.
        if (n != 0) {
            if (n < 0) {
                if ((n &= ~1) == -2) { // If odd number, decrement to the previous even number.
                    if (x == -bound) { // Special case for this one: don't rool to +180°.
                        return x;
                    }
                }
            } else if ((n & 1) != 0) {
                n++; // If odd number, increment to the next even number.
            }
            x -= n * bound;
        }
        return x;
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
            return Matrices.invert(AbstractMathTransform.this.derivative(this.transform(point, null)));
        }

        /**
         * Gets the derivative of this transform at a point. The default
         * implementation computes the inverse of the matrix returned by
         * the enclosing math transform.
         */
        @Override
        public Matrix derivative(final DirectPosition point) throws TransformException {
            return Matrices.invert(AbstractMathTransform.this.derivative(this.transform(point, null)));
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
                WKTUtilities.appendName(parameters.getDescriptor(), formatter, null);
                WKTUtilities.append(parameters, formatter);
                return "PARAM_MT";
            } else {
                formatter.append((FormattableObject) AbstractMathTransform.this);
                return "INVERSE_MT";
            }
        }
    }
}
