/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import javax.media.jai.Warp;
import javax.media.jai.WarpGrid;
import javax.media.jai.WarpAffine;
import javax.media.jai.operator.WarpDescriptor;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.logging.Logging;

import static org.apache.sis.util.ArgumentChecks.ensurePositive;


/**
 * Creates image {@link Warp} objects for the given {@link MathTransform2D}. The
 * {@link Warp#warpPoint(int,int,float[])} method transforms coordinates from <cite>source</cite>
 * to <cite>target</cite> CRS. Note that the JAI {@linkplain WarpDescriptor warp operation} needs
 * a warp object with the opposite semantic (i.e. the image warp shall transform coordinates from
 * target to source CRS). Consequently, consider invoking {@code transform.inverse()} if the warp
 * object is going to be used in an image reprojection.
 *
 * {@section Mapping pixels corner or center}
 * The semantic of <cite>Java Advanced Imaging</cite> {@code Warp} operation is to apply the
 * transforms as below:
 * <p>
 * <ul>
 *   <li>Offset all input ordinates by 0.5 in order to get the coordinates of pixel centers.</li>
 *   <li>Apply the transform.</li>
 *   <li>Offset all output ordinates by -0.5 in order to compensate for the input offset.</li>
 * </ul>
 * <p>
 * This semantic implies that the {@linkplain GridGeometry#getGridToCRS() grid to CRS} transforms
 * were computed using {@link PixelOrientation#UPPER_LEFT}, as in Java2D usage.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public class WarpFactory {
    /**
     * The default factory instance.
     */
    public static final WarpFactory DEFAULT = new WarpFactory(0.25);

    /**
     * The minimal size, in pixels. If the cell size is lower than this threshold,
     * we will abandon the attempt to create a {@link WarpGrid}.
     */
    private static final int MIN_SIZE = 4;

    /**
     * A small tolerance factor for comparisons of floating point numbers. We use the smallest
     * accuracy possible for the {@code float} type for integer numbers different than zero,
     * as computed by:
     *
     * {@preformat java
     *     Math.nextUp(1f) - 1f;
     * }
     *
     * Since {@link WarpAffine} will convert the {@code double} coefficients to {@code float},
     * a tolerance factor not greater than this value should avoid the lost of information. We
     * still use it in order to fix the coefficients that are close to zero, especially shear
     * factors. (e.g. 1E-13 for a shear factor that was expected to be zero).
     */
    private static final double EPS = 1.1920929E-7;

    /**
     * The maximal error allowed, in units of destination CRS (usually pixels). This is
     * the maximal difference allowed between a coordinate transformed using the original
     * transform, and the same coordinate transformed using the warp.
     */
    private final double tolerance;

    /**
     * The cache of {@link Warp} objects previously created.
     */
    private final WarpCache cache;

    /**
     * Creates a new factory.
     *
     * @param tolerance The maximal error allowed, in units of destination CRS (usually pixels).
     *        This is the maximal difference allowed between a coordinate transformed using the
     *        original transform, and the same coordinate transformed using the warp created by
     *        this factory.
     */
    public WarpFactory(final double tolerance) {
        ensurePositive("tolerance", tolerance);
        this.tolerance = tolerance;
        cache = new WarpCache();
    }

    /**
     * Work around for rounding error. The tolerance threshold is arbitrary. This method is
     * mostly for catching the cases were we expected an identity transform from the source
     * grid to the target grid (for example the <var>x</var> axis in a Mercator projection).
     */
    private static double roundIfAlmostInteger(final double value) {
        final double rounded = Math.round(value);
        if (rounded != 0 && Math.abs(rounded - value) <= EPS) {
            return rounded;
        }
        return value;
    }

    /**
     * Creates a {@link WarpAffine} for the given transform. This method may round the affine
     * transform coefficients, because integer scale factors can make the rendering much faster
     * by allowing JAI to use optimized code path (for example using integer arithmetic).
     * <p>
     * A tolerance factor of 1E-6 should not make any visible difference for image
     * having a width or height of less than 0.5 million pixels. For larger image,
     * it is not sure that the unrounded transform is the accurate one. Typically,
     * the transform was really expected to have integer scale factors.
     * <p>
     * {@note The 0.5 offset to apply before and after the transform is performed by the
     * <code>WarpAffine</code> implementation, and consequently doesn't need to be applied
     * in this method.}
     */
    private static Warp create(final AffineTransform transform) {
        final AffineTransform tr = new AffineTransform(transform);
        XAffineTransform.roundIfAlmostInteger(tr, EPS);
        return new WarpAffine(tr);
    }

    /**
     * Creates an image warp applicable to the whole domain of validity of the given transform.
     *
     * @param  name The image or {@linkplain GridCoverage2D coverage} name, or {@code null}.
     * @param  transform The transform to returns as an image warp.
     * @return The warp for the given transform.
     */
    public Warp create(CharSequence name, final MathTransform2D transform) {
        if (transform instanceof WarpTransform2D) {
            return ((WarpTransform2D) transform).getWarp();
        }
        if (transform instanceof AffineTransform) {
            return create((AffineTransform) transform);
        }
        if (name == null) {
            name = Vocabulary.formatInternational(Vocabulary.Keys.UNKNOWN);
        }
        return new WarpAdapter(name, transform);
    }

    /**
     * Creates an image warp applicable to the given domain of validity. This method will typically
     * create more efficient warps than the {@linkplain #create(CharSequence, MathTransform2D)
     * unbounded method}.
     *
     * @param  name The image or {@linkplain GridCoverage2D coverage} name, or {@code null}.
     * @param  transform The transform to returns as an image warp.
     * @param  domain The domain of validity in source coordinates.
     * @return The warp for the given transform.
     * @throws TransformException If at least one point in the given domain can not be transformed.
     */
    public Warp create(final CharSequence name, final MathTransform2D transform, final Rectangle domain)
            throws TransformException
    {
        if (transform instanceof WarpTransform2D) {
            return ((WarpTransform2D) transform).getWarp();
        }
        if (transform instanceof AffineTransform) {
            return create((AffineTransform) transform);
        }
        final WarpKey key = new WarpKey(transform, domain);
        Warp warp = cache.peek(key);
        if (warp == null) {
            WarpCache.Handler<Warp> handler = cache.lock(key);
            try {
                warp = handler.peek();
                if (warp == null) {
                    warp = create(name, transform, key);
                }
            } finally {
                handler.putAndUnlock(warp);
            }
        }
        return warp;
    }

    /**
     * Implementation of the public {@link #create(CharSequence name, MathTransform2D, Rectangle)}
     * method, invoked only if the Warp object was not found in the cache.
     */
    private Warp create(final CharSequence name, final MathTransform2D transform, final WarpKey domain)
            throws TransformException
    {
        final double xmin = domain.getMinX();
        final double xmax = domain.getMaxX();
        final double ymin = domain.getMinY();
        final double ymax = domain.getMaxY();
        final Point2D.Double point = new Point2D.Double(); // Multi-purpose buffer.
        final Matrix2 upperLeft, upperRight, lowerLeft, lowerRight;
        try {
            point.x = xmin; point.y = ymax; upperLeft  = derivative(transform, point);
            point.x = xmax; point.y = ymax; upperRight = derivative(transform, point);
            point.x = xmin; point.y = ymin; lowerLeft  = derivative(transform, point);
            point.x = xmax; point.y = ymin; lowerRight = derivative(transform, point);
        } catch (TransformException e) {
            /*
             * Typically happen when the transform does not support the derivative function,
             * in which case we will fallback on the generic (but slow) adapter.
             */
            Logging.recoverableException(WarpFactory.class, "create", e);
            return create(name, transform);
        }
        /*
         * The tolerance factor is scaled as below. The explanation below is for
         * a one-dimensional case, but the two dimensional case works on the same
         * principle.
         *
         * Let assume that we computed the derivative of y=f(x) at two locations: x₁ and x₃.
         * The derivative values (the slopes of the y=f(x) function) at those locations are
         * m₁ and m₃.
         *
         *          /          _/
         *         / x₁      _/ x₂      ─── x₃
         *        / m₁=1    /  m₂≈½        m₃=0
         *
         * WarpGrid will interpolate the y values between x₁ and x₃. The interpolated results
         * should be exact at locations x₁ and x₃ and have some errors between those two end
         * points.
         *
         * HYPOTHESIS:
         *  1) We presume that the greatest error will be located mid-way between x₁ and x₃.
         *     The x₂ point above represents that location.
         *  2) We presume that the derivative between x₁ and x₃ varies continuously from m₁ to m₃.
         *     The derivative at x₂ may be something close to m₂ ≈ (m₁ + m₃) / 2, but we don't
         *     actually known.
         *
         * Let compute linear approximations of y=f(x) using the two slopes m₁ and m₃. If
         * the hypothesis #2 is true, then the real y values are somewhere between the two
         * approximations. The formulas below use the x₁ point, but we would get the same
         * final equation if we used the x₃ instead (we don't use both x₁ and x₃ since
         * solving such equation produce 0=0).
         *
         * Given f₁(x) = y₁ + (x - x₁)*m₁
         *   and f₃(x) = y₁ + (x - x₁)*m₃
         *
         * then the error ε = f₃(x) - f₁(x) at location x=x₂ is (x₂-x₁) * (m₃-m₁).
         * Given x₂ = (x₁+x₃)/2, we get ε = (x₃-x₁)/2 * (m₃-m₁).
         *
         * If we rearange the terms, we get:  (m₃-m₁) = 2*ε / (x₃-x₁).
         * The (m₃ - m₁) value is the maximal difference to be accepted
         * in the coefficients of the derivative matrix to be compared.
         */
        final Dimension depth;
        try {
            final double tol = 2 * tolerance;
            depth = depth(transform, point,
                    new DoubleDimension2D(tol / (xmax - xmin), tol / (ymax - ymin)),
                    xmin, xmax, ymin, ymax, upperLeft, upperRight, lowerLeft, lowerRight);
        } catch (ArithmeticException e) {
            /*
             * The method does not converge.
             */
            Logging.recoverableException(WarpFactory.class, "create", e);
            return create(name, transform);
        }
        if (depth.width == 0 && depth.height == 0) {
            /*
             * The transform is approximatively affine. Compute the matrix coefficients using
             * the points projected on the four borders of the domain, in order to get a kind
             * of average coefficient values. We don't use the derivative matrix in the center
             * location, because it may not be the best "average" value and some map projection
             * implementations use approximation derived from spherical formulas. The difference
             * is big enough for causing test failure.
             */
            final double xcnt = domain.getCenterX();
            final double ycnt = domain.getCenterY();
            double m00, m10, m01, m11;
            Point2D p;
            // TODO: we must check for the cases where xmin > xmax after transformation,
            //       and handle that as an Envelope the cross the anti-meridian.
            point.x=xmax; point.y=ycnt; p=transform.transform(point, point); m00  = p.getX(); m10  = p.getY();
            point.x=xmin; point.y=ycnt; p=transform.transform(point, point); m00 -= p.getX(); m10 -= p.getY();
            point.x=xcnt; point.y=ymax; p=transform.transform(point, point); m01  = p.getX(); m11  = p.getY();
            point.x=xcnt; point.y=ymin; p=transform.transform(point, point); m01 -= p.getX(); m11 -= p.getY();
            point.x=xcnt; point.y=ycnt; p=transform.transform(point, point);
            final double width  = domain.getWidth();
            final double height = domain.getHeight();
            final AffineTransform tr = new AffineTransform(
                    roundIfAlmostInteger(m00 / width),  roundIfAlmostInteger(m10 / width),
                    roundIfAlmostInteger(m01 / height), roundIfAlmostInteger(m11 / height),
                    roundIfAlmostInteger(p.getX()),     roundIfAlmostInteger(p.getY()));
            /*
             * Note: we rounded the scale and shear factors because they will impact
             * the translation computation below (we may get a number like 1E-13 when
             * the expected value is zero).
             */
            tr.translate(-xcnt, -ycnt);
            XAffineTransform.roundIfAlmostInteger(tr, EPS);
            return new WarpAffine(tr);
        }
        /*
         * Non-affine transform. Create a grid using the cell size computed (indirectly)
         * by the 'depth' method.
         */
        final int xStep     =  domain.width  / (1 << depth.width);
        final int yStep     =  domain.height / (1 << depth.height);
        final int xNumCells = (domain.width  + xStep-1) / xStep;
        final int yNumCells = (domain.height + yStep-1) / yStep;
        final float[] warpPositions = new float[2 * (xNumCells+1) * (yNumCells+1)];
        final int xup = domain.x + xNumCells * xStep;
        final int yup = domain.y + yNumCells * yStep;
        int p = 0;
        for (int y=domain.y; y <= yup; y += yStep) {
            for (int x=domain.x; x <= xup; x += xStep) {
                warpPositions[p++] = x;
                warpPositions[p++] = y;
            }
        }
        /*
         * Note: The 0.5 offset is handled by WarpGrid implementation,
         * so we don't need to apply it ourself in 'warpPositions'.
         */
        transform.transform(warpPositions, 0, warpPositions, 0, p/2);
        return new WarpGrid(
                domain.x, xStep, xNumCells,
                domain.y, yStep, yNumCells, warpPositions);
    }

    /**
     * Computes the number of subdivisions (in power of 2) to apply in order to get a good
     * {@code WarpGrid} approximation. The {@code width} and {@code height} fields in the
     * returned value have the following meaning:
     * <p>
     * <ul>
     *   <li>0 means that the transform is approximatively affine in the region of interest.</li>
     *   <li>1 means that we should split the grid in two parts horizontally and/or vertically.</li>
     *   <li>2 means that we should split the grid in four parts horizontally and/or vertically.</li>
     *   <li>etc.</li>
     * </ul>
     *
     * @param transform The transform for which to compute the depth.
     * @param point Any {@code Point2D.Double} instance, to be overwritten by this method.
     *        This is provided in argument only to reduce the amount of object allocations.
     * @param tolerance The tolerance value to use in comparisons of matrix coefficients,
     *        along the X axis and along the Y axis. The distance between the location of
     *        the matrix being compared is half the size of the region of interest.
     * @param xmin The minimal <var>x</var> ordinate.
     * @param xmax The maximal <var>x</var> ordinate.
     * @param ymin The minimal <var>y</var> ordinate.
     * @param ymax The maximal <var>y</var> ordinate.
     * @param upperLeft  The transform derivative at {@code (xmin,ymax)}.
     * @param upperRight The transform derivative at {@code (xmax,ymax)}.
     * @param lowerLeft  The transform derivative at {@code (xmin,ymin)}.
     * @param lowerRight The transform derivative at {@code (xmax,ymin)}.
     * @return The number of subdivision along each axes.
     * @throws TransformException If a derivative can not be computed.
     * @throws ArithmeticException If this method does not converge.
     */
    private static Dimension depth(final MathTransform2D transform, final Point2D.Double point,
            final DoubleDimension2D tolerance,
            final double xmin,       final double xmax,
            final double ymin,       final double ymax,
            final Matrix2 upperLeft, final Matrix2 upperRight,
            final Matrix2 lowerLeft, final Matrix2 lowerRight)
            throws TransformException, ArithmeticException
    {
        if (!(xmax - xmin >= MIN_SIZE) || !(ymax - ymin >= MIN_SIZE)) { // Use ! for catching NaN.
            throw new ArithmeticException(Errors.format(Errors.Keys.NO_CONVERGENCE));
        }
        /*
         * All derivatives will be compared to the derivative at (centerX, centerY).
         * Consequently, the distance between the derivatives are half the distance
         * between [x|y]min and [x|y]max (approximatively - we ignore the diagonal).
         * Consequently, the tolerance threshold can be augmented by the same factor.
         */
        final double oldTolX = tolerance.width;
        final double oldTolY = tolerance.height;
        tolerance.width  *= 2;
        tolerance.height *= 2;
        final double centerX = point.x = 0.5 * (xmin + xmax);
        final double centerY = point.y = 0.5 * (ymin + ymax);
        final Matrix2 center = derivative(transform, point);
        point.x = xmin;    point.y = centerY; final Matrix2 centerLeft  = derivative(transform, point);
        point.x = xmax;    point.y = centerY; final Matrix2 centerRight = derivative(transform, point);
        point.x = centerX; point.y = ymin;    final Matrix2 centerLower = derivative(transform, point);
        point.x = centerX; point.y = ymax;    final Matrix2 centerUpper = derivative(transform, point);
        final boolean cl = equals(center, centerLeft,  tolerance);
        final boolean cr = equals(center, centerRight, tolerance);
        final boolean cb = equals(center, centerLower, tolerance);
        final boolean cu = equals(center, centerUpper, tolerance);
        int nx=0, ny=0;
        /*
         *   upperLeft  ┌──────┬─ centerUpper
         *              │      │
         *   centerLeft ├──────┼─ center
         */
        if (!cl || !cu || !equals(center, upperLeft, tolerance)) {
            final Dimension depth = depth(transform, point, tolerance,
                    xmin, centerX, centerY, ymax,
                    upperLeft, centerUpper, centerLeft, center);
            incrementNonAffineDimension(cl, cu, depth);
            nx = depth.width;
            ny = depth.height;
        }
        /*
         *   centerUpper ─┬──────┐ upperRight
         *                │      │
         *   center      ─┼──────┤ centerRight
         */
        if (!cr || !cu || !equals(center, upperRight, tolerance)) {
            final Dimension depth = depth(transform, point, tolerance,
                    centerX, xmax, centerY, ymax,
                    centerUpper, upperRight, center, centerRight);
            incrementNonAffineDimension(cr, cu, depth);
            nx = Math.max(nx, depth.width);
            ny = Math.max(ny, depth.height);
        }
        /*
         *   centerLeft ├──────┼─ center
         *              │      │
         *   lowerLeft  └──────┴─ centerLower
         */
        if (!cl || !cb || !equals(center, lowerLeft, tolerance)) {
            final Dimension depth = depth(transform, point, tolerance,
                    xmin, centerX, ymin, centerY,
                    centerLeft, center, lowerLeft, centerLower);
            incrementNonAffineDimension(cl, cb, depth);
            nx = Math.max(nx, depth.width);
            ny = Math.max(ny, depth.height);
        }
        /*
         *   center      ─┼──────┤ centerRight
         *                │      │
         *   centerLower ─┴──────┘ lowerRight
         */
        if (!cr || !cb || !equals(center, lowerRight, tolerance)) {
            final Dimension depth = depth(transform, point, tolerance,
                    centerX, xmax, ymin, centerY,
                    center, centerRight, centerLower, lowerRight);
            incrementNonAffineDimension(cr, cb, depth);
            nx = Math.max(nx, depth.width);
            ny = Math.max(ny, depth.height);
        }
        tolerance.width  = oldTolX;
        tolerance.height = oldTolY;
        return new Dimension(nx, ny);
    }

    /**
     * Increments the width, the height or both values in the given dimension, depending on which
     * dimension are not affine. This method <strong>most</strong> be invoked using the following
     * pattern, where {@code center} is the matrix of the transform derivative in the center of
     * the region of interest. Note: the order of operations in the {@code if} statement matter!
     *
     * {@code java
     *     he = center.equals(matrixOnTheSameHorizontalLine,  tolerance);
     *     ve = center.equals(matrixOnTheSameVerticalLine,    tolerance);
     *     if (!he || !ve || center.equals(matrixOnADiagonal, tolerance)) {
     *         incrementNonAffineDimension(he, ve, depth);
     *     }
     * }
     *
     * @param he    {@code true} if the matrix on the horizontal line are equal.
     * @param ve    {@code true} if the matrix on the vertical line are equal.
     * @param depth The dimension in which to increment the width, height or both.
     */
    private static void incrementNonAffineDimension(boolean he, boolean ve, Dimension depth) {
        if (he == ve) {
            // Both dimensions are not affine: either (he, ve)==false (the obvious case),
            // or (he,ve) == true in which case this method have been invoked only if the
            // last center.equals(...) test in the 'if' statement returned false.
            depth.width++;
            depth.height++;
        } else if (ve) {
            // Implies (he == false): horizontal dimension is not affine.
            // Don't touch to the vertical dimension since it is affine.
            depth.width++;
        } else {
            // Implies (he == true): horizontal dimension is affine, don't touch it.
            depth.height++;
        }
    }

    /**
     * Computes the derivative of the given transform at the given location, and returns the result
     * as a 2&times;2 matrix. This method invokes the {@link MathTransform2D#derivative(Point2D)}
     * and convert or cast the result to a {@link Matrix2} instance.
     * <p>
     * In the Geotk implementation, the matrix returned by the {@code derivative(Point2D)} methods
     * are already instances of {@link Matrix2}. Consequently in most cases this method will just
     * cast the result.
     *
     * @param  transform The transform for which to compute the derivative.
     * @param  point     The location where to compute the derivative.
     * @return The derivative at the given location as a 2&times;2 matrix.
     * @throws TransformException If the derivative can not be computed.
     */
    private static Matrix2 derivative(final MathTransform2D transform, final Point2D point)
            throws TransformException
    {
        final Matrix matrix = transform.derivative(point);
        return (matrix instanceof Matrix2) ? (Matrix2) matrix : new Matrix2(matrix);
    }

    /**
     * Returns {@code true} if the given matrix are equals, up to the given tolerance threshold.
     * The threshold can be different for the X and Y axis. This allows the condition to break
     * the loop sooner (resulting in smaller grids) inside the {@code depth} method.
     */
    private static boolean equals(final Matrix2 center, final Matrix2 corner, final DoubleDimension2D tolerance) {
        return Math.abs(center.m00 - corner.m00) <= tolerance.width  &&
               Math.abs(center.m01 - corner.m01) <= tolerance.width  &&
               Math.abs(center.m10 - corner.m10) <= tolerance.height &&
               Math.abs(center.m11 - corner.m11) <= tolerance.height;
    }
}
