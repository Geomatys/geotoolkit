/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.builder;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.util.Arrays;
import java.util.Objects;
import java.io.Serializable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.transform.GridType;
import org.geotoolkit.referencing.operation.transform.GridTransform;
import org.apache.sis.referencing.operation.transform.IterationStrategy;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.Utilities.hash;


/**
 * Transforms a set of coordinate points using a grid of localization. This class extends
 * {@link GridTransform} with the additional requirement that the grid values must be
 * the target coordinates, not an offset to apply on source coordinates like NADCON grids.
 * This additional requirement allows this class to implement inverse transforms.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
final class LocalizationGridTransform2D extends GridTransform implements MathTransform2D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1067560328828441295L;

    /**
     * Maximal number of iterations to try before to fail
     * during an inverse transformation.
     */
    private static final int MAX_ITER = 40;

    /**
     * Set to {@code true} for a conservative (and maybe slower) algorithm
     * in {@link #inverseTransform}.
     */
    private static final boolean CONSERVATIVE = true;

    /**
     * Set to {@code true} for forcing {@link #inverseTransform} to returns
     * a value instead of throwing an exception if the transform do not converge.
     * This is a temporary flag until we find why the inverse transform fails to
     * converge in some case.
     */
    private static final boolean MASK_NON_CONVERGENCE;
    static {
        String property;
        try {
            property = System.getProperty("org.geotoolkit.referencing.forceConvergence", "false");
        } catch (SecurityException exception) {
            // We are running in an applet.
            property = "false";
        }
        MASK_NON_CONVERGENCE = property.equalsIgnoreCase("true");
    }

    /**
     * A global affine transform for the whole grid.
     */
    private final AffineTransform global;

    /**
     * The inverse math transform. Will be constructed only when first requested.
     */
    private MathTransform2D inverse;

    /**
     * Constructs a localization grid using the specified data.
     *
     * @param width  Number of grid's columns.
     * @param height Number of grid's rows.
     * @param gridX  The localization grid for <var>x</var> ordinates.
     * @param gridY  The localization grid for <var>y</var> ordinates.
     * @param global A global affine transform for the whole grid.
     */
    protected LocalizationGridTransform2D(final int width, final int height,
            final double[] gridX, final double[] gridY, final AffineTransform global)
    {
        this(width, height, new DataBufferDouble(new double[][] {gridX, gridY}, width*height), global);
    }

    /**
     * Constructs a localization grid using the specified data.
     *
     * @param width  Number of grid's columns.
     * @param height Number of grid's rows.
     * @param grid   The localization grid.
     * @param global A global affine transform for the whole grid.
     */
    protected LocalizationGridTransform2D(final int width, final int height, final DataBuffer grid,
                                          final AffineTransform global)
    {
        super(GridType.LOCALIZATION, grid, new Dimension(width, height), null);
        this.global = global;
    }

    /**
     * Returns an estimation of the affine transform at the given position.
     *
     * @param  col  The <var>x</var> ordinate value where to evaluate.
     * @param  row  The <var>y</var> ordinate value where to evaluate.
     * @param dest  The affine transform where to write the result.
     */
    private void getAffineTransform(double x, double y, final AffineTransform dest) {
        int col = (int) x;
        int row = (int) y;
        if (col > width -2) col = width -2;
        if (row > height-2) row = height-2;
        if (col < 0)        col = 0;
        if (row < 0)        row = 0;
        final int sgnCol;
        final int sgnRow;
        if (x-col > 0.5) {
            sgnCol = -1;
            col++;
        } else sgnCol = +1;
        if (y-row > 0.5) {
            sgnRow = -1;
            row++;
        } else sgnRow = +1;
        /*
         * Calculation of affine transform has 6 unknown terms.     P00──────P10
         * Consequently its solution requierts 6 equations. We       │  .     │
         * get them by using the 3 nearest points, each point        │        │
         * having 2 ordinates. Example: (. is the pt to eval)       P01────(ignored)
         */
        final int offset00 = (col + row*width);
        final int offset01 = offset00 + sgnRow*width;
        final int offset10 = offset00 + sgnCol;
        x = grid.getElemDouble(0, offset00);
        y = grid.getElemDouble(1, offset00);
        final double dxCol = (grid.getElemDouble(0, offset10) - x) * sgnCol;
        final double dyCol = (grid.getElemDouble(1, offset10) - y) * sgnCol;
        final double dxRow = (grid.getElemDouble(0, offset01) - x) * sgnRow;
        final double dyRow = (grid.getElemDouble(1, offset01) - y) * sgnRow;
        dest.setTransform(dxCol, dyCol, dxRow, dyRow,
                          x - dxCol*col - dxRow*row,
                          y - dyCol*col - dyRow*row);
        /*
         * Si l'on transforme les 3 points qui ont servit à déterminer la transformation
         * affine, on devrait obtenir un résultat identique (aux erreurs d'arrondissement
         * près) peu importe que l'on utilise la transformation affine ou la grille de
         * localisation.
         */
        assert distance(new Point(col,        row       ), dest) < 1E-5;
        assert distance(new Point(col+sgnCol, row       ), dest) < 1E-5;
        assert distance(new Point(col,        row+sgnRow), dest) < 1E-5;
    }

    /**
     * Transforms a point using the localization grid, transform it back using the inverse
     * of the specified affine transform, and returns the distance between the source and
     * the resulting point. This is used for assertions only.
     *
     * @param  index The source point to test.
     * @param  tr The affine transform to test.
     * @return The distance in grid coordinate. Should be close to 0.
     */
    private double distance(final Point2D index, final AffineTransform tr) {
        try {
            Point2D geoCoord = transform(index, null);
            geoCoord = tr.inverseTransform(geoCoord, geoCoord);
            return geoCoord.distance(index);
        } catch (TransformException | NoninvertibleTransformException exception) {
            // TransformException should not happen, but NoninvertibleTransformException is not so sure...
            throw new AssertionError(exception);
        }
    }

    /**
     * Applies the inverse transform to a set of points. More specifically, this method transform
     * "real world" coordinates to grid coordinates. This method use an iterative algorithm for
     * that purpose. A {@link TransformException} is thrown in the computation do not converge.
     * The algorithm applied by this method and its callers is:
     *
     * <ol>
     *   <li><p>Transform the first point using a "global" affine transform (i.e. the affine
     *       transformed computed using the "least squares" method in LocalizationGrid).
     *       Other points will be transformed using the last successful affine transform,
     *       since we assume that the points to transform are close to each other.</p></li>
     *
     *   <li><p>Next, compute a local affine transform and use if for transforming the point
     *       again. Recompute again the local affine transform and continue until the cell
     *       (x0,y0) doesn't change.</p></li>
     * </ol>
     *
     * @param source The "real world" coordinate to transform.
     * @param target A pre-allocated destination point. <strong>This point
     *               can't be the same than {@code source}!<strong>
     * @param tr In input, the affine transform to use for the first step.
     *        In output, the last affine transform used for the transformation.
     *
     */
    final void inverseTransform(final Point2D source, final Point2D.Double target,
                                final AffineTransform tr) throws TransformException
    {
        if (CONSERVATIVE) {
            // In an optimal approach, we should reuse the same affine transform than the one used
            // in the last transformation, since it is likely to converge faster for a point close
            // to the previous one. However, it may lead to strange and hard to predict
            // discontinuity in transformations.
            tr.setTransform(global);
        }
        try {
            tr.inverseTransform(source, target);
            int previousX = (int) target.x;
            int previousY = (int) target.y;
            for (int iter=0; iter<MAX_ITER; iter++) {
                getAffineTransform(target.x, target.y, tr);
                tr.inverseTransform(source, target);
                final int ix = (int) target.x;
                final int iy = (int) target.y;
                if (previousX == ix && previousY == iy) {
                    // Computation converged.
                    if (target.x >= 0 && target.x < width &&
                        target.y >= 0 && target.y < height)
                    {
                        // Point is inside the grid. Check the precision.
                        assert transform(target, null).distanceSq(source) < 1E-3 : target;
                    } else {
                        // Point is outside the grid. Use the global transform for uniformity.
                        inverseTransform(source, target);
                    }
                    return;
                }
                previousX = ix;
                previousY = iy;
            }
            /*
             * No convergence found in the "ordinary" loop. The following code checks if
             * we are stuck in a never-ending loop. If yes, then it will try to minimize
             * the following function:
             *
             *     {@code transform(target).distance(source)}.
             */
            final int x0 = previousX;
            final int y0 = previousY;
            global.inverseTransform(source, target);
            double x,y;
            double bestX = x = target.x;
            double bestY = y = target.y;
            double minSq = Double.POSITIVE_INFINITY;
            for (int iter=1-MAX_ITER; iter<MAX_ITER; iter++) {
                previousX = (int)x;
                previousY = (int)y;
                getAffineTransform(x, y, tr);
                tr.inverseTransform(source, target);
                x = target.x;
                y = target.y;
                final int ix = (int) x;
                final int iy = (int) y;
                if (previousX == ix && previousY == iy) {
                    // Computation converged.
                    assert iter >= 0;
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        // Point is inside the grid. Check the precision.
                        assert transform(target, null).distanceSq(source) < 1E-3 : target;
                    } else {
                        // Point is outside the grid. Use the global transform for uniformity.
                        inverseTransform(source, target);
                    }
                    return;
                }
                if (iter == 0) {
                    assert x0 == ix && y0 == iy;
                } else if (x0 == ix && y0 == iy) {
                    // Loop detected.
                    if (bestX >= 0 && bestX < width && bestY >= 0 && bestY < height) {
                        target.x = bestX;
                        target.y = bestY;
                    } else {
                        inverseTransform(source, target);
                    }
                    return;
                }
                transform(target, target);
                final double distanceSq = target.distanceSq(source);
                if (distanceSq < minSq) {
                    minSq = distanceSq;
                    bestX = x;
                    bestY = y;
                }
            }
            /*
             * The transformation didn't converge, and no loop has been found.
             * If the following block is enabled (true), then the best point
             * will be returned. It may not be the best approach since we don't
             * know if this point is valid. Otherwise, an exception is thrown.
             */
            if (MASK_NON_CONVERGENCE) {
                Logging.getLogger(LocalizationGridTransform2D.class)
                       .fine(Errors.format(Errors.Keys.NoConvergence));
                if (bestX >= 0 && bestX < width && bestY >= 0 && bestY < height) {
                    target.x = bestX;
                    target.y = bestY;
                } else {
                    inverseTransform(source, target);
                }
                return;
            }
        } catch (NoninvertibleTransformException exception) {
            throw new TransformException(Errors.format(Errors.Keys.NoninvertibleTransform), exception);
        }
        throw new TransformException(Errors.format(Errors.Keys.NoConvergence));
    }

    /**
     * Inverse transforms a point using the {@link #global} affine transform, and
     * make sure that the result point is outside the grid. This method is used
     * for the transformation of a point which shouldn't be found in the grid.
     *
     * @param  source The source coordinate point.
     * @param  target The target coordinate point (should not be {@code null}).
     * @throws NoninvertibleTransformException if the transform is non-invertible.
     *
     * @todo Current implementation projects an inside point on the nearest border.
     *       Could we do something better?
     */
    private void inverseTransform(final Point2D source, final Point2D.Double target)
            throws NoninvertibleTransformException
    {
        if (global.inverseTransform(source, target) != target) {
            throw new AssertionError(); // Should not happen.
        }
        double x = target.x;
        double y = target.y;
        if (x >= 0 && x < width && y >= 0 && y < height) {
            // Project on the nearest border. TODO: Could we do something better here?
            x -= 0.5 * width;
            y -= 0.5 * height;
            if (Math.abs(x) < Math.abs(y)) {
                target.x = (x > 0) ? width  : -1;
            } else {
                target.y = (y > 0) ? height : -1;
            }
        }
    }

    /**
     * Returns the inverse transform.
     */
    @Override
    public MathTransform2D inverse() {
        if (inverse == null) {
            inverse = new Inverse();
        }
        return inverse;
    }

    /**
     * The inverse transform. This inner class is the inverse of the enclosing math transform.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    private final class Inverse extends GridTransform.Inverse
            implements MathTransform2D, Serializable
    {
        /**
         * Serial number for inter-operability with different versions.
         */
        private static final long serialVersionUID = 4876426825123740986L;

        /**
         * Default constructor.
         */
        public Inverse() {
            LocalizationGridTransform2D.this.super();
        }

        /**
         * Transforms a "real world" coordinate into a grid coordinate.
         */
        @Override
        public Point2D transform(final Point2D ptSrc, final Point2D ptDst) throws TransformException {
            final AffineTransform tr = new AffineTransform(global);
            if (ptDst == null) {
                final Point2D.Double target = new Point2D.Double();
                inverseTransform(ptSrc, target, tr);
                return target;
            }
            if (ptDst!=ptSrc && (ptDst instanceof Point2D.Double)) {
                inverseTransform(ptSrc, (Point2D.Double) ptDst, tr);
                return ptDst;
            }
            final Point2D.Double target = new Point2D.Double();
            inverseTransform(ptSrc, target, tr);
            ptDst.setLocation(target);
            return ptDst;
        }

        /**
         * Applies the inverse transform to a points. More specifically, this method transforms
         * "real world" coordinates to grid coordinates. This method use an iterative algorithm
         * for that purpose. A {@link TransformException} is thrown in the computation does not
         * converge.
         *
         * @param srcPts the array containing the source point coordinates.
         * @param srcOff the offset to the first point to be transformed in the source array.
         * @param dstPts the array into which the transformed point coordinates are returned.
         *               May be the same than {@code srcPts}.
         * @param dstOff the offset to the location of the first transformed
         *               point that is stored in the destination array.
         * @throws TransformException if a point can't be transformed.
         */
        @Override
        public Matrix transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final boolean derivate) throws TransformException
        {
            final Matrix derivative = derivate ? derivative(
                    new Point2D.Double(srcPts[srcOff], srcPts[srcOff+1])) : null;
            transform(srcPts, srcOff, dstPts, dstOff, 1);
            return derivative;
        }

        /**
         * Applies the inverse transform to a set of points.
         */
        @Override
        public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
                throws TransformException
        {
            int postIncrement = 0;
            if (srcPts == dstPts) {
                switch (IterationStrategy.suggest(srcOff, srcOff, dstOff, dstOff, numPts)) {
                    case ASCENDING: {
                        break;
                    }
                    case DESCENDING: {
                        srcOff += (numPts-1) * 2;
                        dstOff += (numPts-1) * 2;
                        postIncrement = -4;
                        break;
                    }
                    default: {
                        srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*2);
                        srcOff = 0;
                        break;
                    }
                }
            }
            final Point2D.Double source = new Point2D.Double();
            final Point2D.Double target = new Point2D.Double();
            final AffineTransform tr = new AffineTransform(global);
            while (--numPts >= 0) {
                source.x = srcPts[srcOff++];
                source.y = srcPts[srcOff++];
                inverseTransform(source, target, tr);
                dstPts[dstOff++] = target.x;
                dstPts[dstOff++] = target.y;
                srcOff += postIncrement;
                dstOff += postIncrement;
            }
        }

        /**
         * Applies the inverse transform to a set of points.
         */
        @Override
        public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
                throws TransformException
        {
            int postIncrement = 0;
            if (srcPts == dstPts) {
                switch (IterationStrategy.suggest(srcOff, srcOff, dstOff, dstOff, numPts)) {
                    case ASCENDING: {
                        break;
                    }
                    case DESCENDING: {
                        srcOff += (numPts-1) * 2;
                        dstOff += (numPts-1) * 2;
                        postIncrement = -4;
                        break;
                    }
                    default: {
                        srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*2);
                        srcOff = 0;
                        break;
                    }
                }
            }
            final Point2D.Double source = new Point2D.Double();
            final Point2D.Double target = new Point2D.Double();
            final AffineTransform tr = new AffineTransform(global);
            while (--numPts >= 0) {
                source.x = srcPts[srcOff++];
                source.y = srcPts[srcOff++];
                inverseTransform(source, target, tr);
                dstPts[dstOff++] = (float) target.x;
                dstPts[dstOff++] = (float) target.y;
                srcOff += postIncrement;
                dstOff += postIncrement;
            }
        }

        /**
         * Applies the inverse transform to a set of points.
         */
        @Override
        public void transform(final double[] srcPts, int srcOff,
                              final float [] dstPts, int dstOff, int numPts)
                throws TransformException
        {
            final Point2D.Double source = new Point2D.Double();
            final Point2D.Double target = new Point2D.Double();
            final AffineTransform tr = new AffineTransform(global);
            while (--numPts >= 0) {
                source.x = srcPts[srcOff++];
                source.y = srcPts[srcOff++];
                inverseTransform(source, target, tr);
                dstPts[dstOff++] = (float) target.x;
                dstPts[dstOff++] = (float) target.y;
            }
        }

        /**
         * Applies the inverse transform to a set of points.
         */
        @Override
        public void transform(final float [] srcPts, int srcOff,
                              final double[] dstPts, int dstOff, int numPts)
                throws TransformException
        {
            final Point2D.Double source = new Point2D.Double();
            final Point2D.Double target = new Point2D.Double();
            final AffineTransform tr = new AffineTransform(global);
            while (--numPts >= 0) {
                source.x = srcPts[srcOff++];
                source.y = srcPts[srcOff++];
                inverseTransform(source, target, tr);
                dstPts[dstOff++] = target.x;
                dstPts[dstOff++] = target.y;
            }
        }

        /**
         * Returns the original localization grid transform.
         */
        @Override
        public MathTransform2D inverse() {
            return (MathTransform2D) super.inverse();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(global, super.computeHashCode());
    }

    /**
     * Compares this transform with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object, mode)) {
            final LocalizationGridTransform2D that = (LocalizationGridTransform2D) object;
            return Objects.equals(this.global, that.global);
        }
        return false;
    }
}
