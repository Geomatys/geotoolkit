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
 */
package org.geotoolkit.coverage.grid;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;


/**
 * A grid coverage using an {@linkplain Interpolation interpolation} for evaluating points. This
 * interpolator is not used for {@linkplain InterpolationNearest nearest-neighbor interpolation}
 * (use the plain {@link GridCoverage2D} class for that). It should work for other kinds of
 * interpolation however.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public final class Interpolator2D extends Calculator2D {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 9028980295030908004L;

    /**
     * The greatest value smaller than 1 representable as a {@code float} number.
     * This value can be obtained with {@code -Math.nextUp(-1f)}.
     */
    private static final float ONE_EPSILON = 0.99999994f;

    /**
     * Default interpolations, in preference order. Will be constructed only when first needed.
     */
    private static Interpolation[] DEFAULTS;

    /**
     * Transform from "real world" coordinates to grid coordinates.
     * This transform maps coordinates to pixel <em>centers</em>.
     */
    private final MathTransform2D toGrid;

    /**
     * The interpolation method.
     */
    private final Interpolation interpolation;

    /**
     * Second interpolation method to use if this one failed. May be {@code null} if there
     * is no fallback. By convention, {@code this} means that interpolation should fallback
     * on {@code super.evaluate(...)} (i.e. nearest neighbor).
     */
    private final Interpolator2D fallback;

    /**
     * Image bounds. Bounds have been reduced by {@link Interpolation}'s padding.
     */
    private final int xmin, ymin, xmax, ymax;

    /**
     * Interpolation padding.
     */
    private final int top, left;

    /**
     * The interpolation bounds. Interpolation will use pixel inside this rectangle.
     * This rectangle is passed as an argument to {@link RectIterFactory}.
     */
    private final Rectangle bounds;

    /**
     * Arrays to use for passing arguments to interpolation.
     * This array will be constructed only when first needed.
     */
    private transient double[][] doubles;

    /**
     * Arrays to use for passing arguments to interpolation.
     * This array will be constructed only when first needed.
     */
    private transient float[][] floats;

    /**
     * Arrays to use for passing arguments to interpolation.
     * This array will be constructed only when first needed.
     */
    private transient int[][] ints;

    /**
     * Image iterator, initialized at first interpolation.
     */
    private transient PixelIterator iterator;

    /**
     * Constructs a new interpolator using default interpolations.
     *
     * @param  coverage The coverage to interpolate.
     * @return A grid coverage using the default interpolations.
     */
    public static GridCoverage2D create(final GridCoverage2D coverage) {
        // No need to synchronize: not a big deal if two arrays are created.
        if (DEFAULTS == null) {
            DEFAULTS = new Interpolation[] {
                Interpolation.getInstance(Interpolation.INTERP_BICUBIC),
                Interpolation.getInstance(Interpolation.INTERP_BILINEAR),
                Interpolation.getInstance(Interpolation.INTERP_NEAREST)
            };
        }
        return create(coverage, DEFAULTS);
    }

    /**
     * Constructs a new interpolator for an interpolation and its fallbacks. This method is
     * typically invoked with only one interpolation method. If more than one interpolation
     * is given, then the supplemental interpolations are <cite>fallbacks</cite>. The fallbacks
     * are used if the primary interpolation failed because of {@linkplain Float#NaN NaN} values
     * in the interpolated point neighbor.
     *
     * @param  coverage The coverage to interpolate.
     * @param  interpolations The interpolation to use and its fallback (if any).
     * @return A grid coverage using the given interpolations.
     */
    public static GridCoverage2D create(GridCoverage2D coverage, final Interpolation... interpolations) {
        while (coverage instanceof Calculator2D) {
            coverage = ((Calculator2D) coverage).source;
        }
        if (interpolations.length == 0 || (interpolations[0] instanceof InterpolationNearest)) {
            return coverage;
        }
        return new Interpolator2D(coverage, interpolations, 0);
    }

    /**
     * Constructs a new interpolator for the specified interpolation.
     *
     * @param  coverage The coverage to interpolate.
     * @param  interpolations The interpolations to use and its fallback
     *         (if any). This array must have at least 1 element.
     * @param  index The index of interpolation to use in the {@code interpolations} array.
     */
    private Interpolator2D(final GridCoverage2D  coverage,
                           final Interpolation[] interpolations,
                           final int             index)
    {
        super(null, coverage);
        this.interpolation = interpolations[index];
        if (index+1 < interpolations.length) {
            if (interpolations[index+1] instanceof InterpolationNearest) {
                // By convention, 'fallback==this' is for 'super.evaluate(...)'
                // (i.e. "NearestNeighbor").
                this.fallback = this;
            } else {
                this.fallback = new Interpolator2D(coverage, interpolations, index+1);
            }
        } else {
            this.fallback = null;
        }
        /*
         * Computes the affine transform from "real world" coordinates  to grid coordinates.
         * This transform maps coordinates to pixel <em>centers</em>. If this transform has
         * already be created during fallback construction, reuse the fallback's instance
         * instead of creating a new identical one.
         */
        if (fallback!=null && fallback!=this) {
            this.toGrid = fallback.toGrid;
        } else try {
            final MathTransform2D transform = gridGeometry.getGridToCRS2D();
            // Note: If we want nearest-neighbor interpolation, we need to add the
            //       following line (assuming the transform is an 'AffineTransform'):
            //
            //       transform.translate(-0.5, -0.5);
            //
            //       This is because we need to cancel the last 'translate(0.5, 0.5)' that appears
            //       in GridGeometry's constructor (we must remember that OpenGIS's transform maps
            //       pixel CENTER, while JAI transforms maps pixel UPPER LEFT corner). For exemple
            //       the (12.4, 18.9) coordinates still lies on the [12,9] pixel.  Since the JAI's
            //       nearest-neighbor interpolation use 'Math.floor' operation instead of
            //       'Math.round', we must follow this convention.
            //
            //       For other kinds of interpolation, we want to maps pixel values to pixel center.
            //       For example, coordinate (12.5, 18.5) (in floating-point coordinates) lies at
            //       the center of pixel [12,18] (in integer coordinates); the evaluated value
            //       should be the exact pixel's value. On the other hand, coordinate (12.5, 19)
            //       (in floating-point coordinates) lies exactly at the edge between pixels
            //       [12,19] and [12,20]; the evaluated value should be a mid-value between those
            //       two pixels. If we want center of mass located at pixel centers, we must keep
            //       the (0.5, 0.5) translation provided by 'GridGeometry' for interpolation other
            //       than nearest-neighbor.
            toGrid = transform.inverse();
        } catch (NoninvertibleTransformException exception) {
            throw new IllegalArgumentException(exception);
        }

        final int left   = interpolation.getLeftPadding();
        final int right  = interpolation.getRightPadding();
        final int top    = interpolation.getTopPadding();
        final int bottom = interpolation.getBottomPadding();

        this.top  = top;
        this.left = left;

        final int x = image.getMinX();
        final int y = image.getMinY();

        this.xmin = x + left;
        this.ymin = y + top;
        this.xmax = x + image.getWidth()  - right;
        this.ymax = y + image.getHeight() - bottom;

        bounds = new Rectangle(0, 0, interpolation.getWidth(), interpolation.getHeight());
    }

    private void initIterator() {
        if(iterator==null){
            iterator = PixelIteratorFactory.createDefaultIterator(image);
        }
    }

    /**
     * Invoked by <code>{@linkplain #view view}(type)</code> when the {@linkplain ViewType#PACKED
     * packed}, {@linkplain ViewType#GEOPHYSICS geophysics} or {@linkplain ViewType#PHOTOGRAPHIC
     * photographic} view of this grid coverage needs to be created. This method applies to the
     * new grid coverage the same {@linkplain #getInterpolations interpolations} than this grid
     * coverage.
     *
     * @param  view A view derived from the {@linkplain #source source} coverage.
     * @return The grid coverage to be returned by {@link #view view}.
     *
     * @since 2.5
     */
    @Override
    protected GridCoverage2D specialize(final GridCoverage2D view) {
        return create(view, getInterpolations());
    }

    /**
     * Returns the class of the view returned by {@link #specialize}.
     */
    @Override
    Class<Interpolator2D> getViewClass() {
        return Interpolator2D.class;
    }

    /**
     * Returns the interpolations. The first array's element is the interpolation for
     * this grid coverage. Other elements (if any) are fallbacks.
     *
     * @return The interpolations. The returned array contains at least one element.
     */
    public Interpolation[] getInterpolations() {
        final List<Interpolation> interp = new ArrayList<>(4);
        Interpolator2D scan = this;
        do {
            interp.add(interpolation);
            if (scan.fallback == scan) {
                interp.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
                break;
            }
            scan = scan.fallback;
        }
        while (scan != null);
        return interp.toArray(new Interpolation[interp.size()]);
    }

    /**
     * Returns the primary interpolation used by this {@code Interpolator2D}.
     */
    @Override
    public Interpolation getInterpolation() {
        return interpolation;
    }

    /**
     * Returns a sequence of integer values for a given two-dimensional point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public int[] evaluate(final Point2D coord, int[] dest) throws CannotEvaluateException {
        if (fallback != null) {
            dest = super.evaluate(coord, dest);
        }
        try {
            final Point2D pixel = toGrid.transform(coord, null);
            final double x = pixel.getX();
            final double y = pixel.getY();
            if (!Double.isNaN(x) && !Double.isNaN(y)) {
                dest = interpolate(x, y, dest, 0, image.getSampleModel().getNumBands());
                if (dest != null) {
                    return dest;
                }
            }
        } catch (TransformException exception) {
            throw new CannotEvaluateException(formatEvaluateError(coord, false), exception);
        }
        throw new PointOutsideCoverageException(formatEvaluateError(coord, true));
    }

    /**
     * Returns a sequence of float values for a given two-dimensional point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public float[] evaluate(final Point2D coord, float[] dest) throws CannotEvaluateException {
        if (fallback != null) {
            dest = super.evaluate(coord, dest);
        }
        try {
            final Point2D pixel = toGrid.transform(coord, null);
            final double x = pixel.getX();
            final double y = pixel.getY();
            if (!Double.isNaN(x) && !Double.isNaN(y)) {
                dest = interpolate(x, y, dest, 0, image.getSampleModel().getNumBands());
                if (dest != null) {
                    return dest;
                }
            }
        } catch (TransformException exception) {
            throw new CannotEvaluateException(formatEvaluateError(coord, false), exception);
        }
        throw new PointOutsideCoverageException(formatEvaluateError(coord, true));
    }

    /**
     * Returns a sequence of double values for a given two-dimensional point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public double[] evaluate(final Point2D coord, double[] dest) throws CannotEvaluateException {
        if (fallback != null) {
            dest = super.evaluate(coord, dest);
        }
        try {
            final Point2D pixel = toGrid.transform(coord, null);
            final double x = pixel.getX();
            final double y = pixel.getY();
            if (!Double.isNaN(x) && !Double.isNaN(y)) {
                dest = interpolate(x, y, dest, 0, image.getSampleModel().getNumBands());
                if (dest != null) {
                    return dest;
                }
            }
        } catch (TransformException exception) {
            throw new CannotEvaluateException(formatEvaluateError(coord, false), exception);
        }
        throw new PointOutsideCoverageException(formatEvaluateError(coord, true));
    }

    /**
     * Interpolates at the specified position. If {@code fallback!=null},
     * then {@code dest} <strong>must</strong> have been initialized with
     * {@code super.evaluate(...)} prior to invoking this method.
     *
     * @param x      The x position in pixel's coordinates.
     * @param y      The y position in pixel's coordinates.
     * @param dest   The destination array, or null.
     * @param band   The first band's index to interpolate.
     * @param bandUp The last band's index+1 to interpolate.
     * @return {@code null} if point is outside grid coverage.
     */
    private synchronized int[] interpolate(final double x, final double y,
                                           int[] dest, int band, final int bandUp)
    {
        final double x0 = Math.floor(x);
        final double y0 = Math.floor(y);
        final int    ix = (int)x0;
        final int    iy = (int)y0;
        if (!(ix>=xmin && ix<xmax && iy>=ymin && iy<ymax)) {
            if (fallback == null) return null;
            if (fallback == this) return dest; // super.evaluate(...) succeed prior to this call.
            return fallback.interpolate(x, y, dest, band, bandUp);
        }
        /*
         * Creates buffers, if not already created.
         */
        int[][] samples = ints;
        if (samples == null) {
            final int rowCount = interpolation.getHeight();
            final int colCount = interpolation.getWidth();
            ints = samples = new int[rowCount][];
            for (int i=0; i<rowCount; i++) {
                samples[i] = new int[colCount];
            }
        }
        if (dest == null) {
            dest = new int[bandUp];
        }
        /*
         * Builds up a RectIter and use it for interpolating all bands.
         * There is very few points, so the cost of creating a RectIter
         * may be important. But it seems to still lower than query tiles
         * many time (which may involve more computation than necessary).
         */
        bounds.x = ix - left;
        bounds.y = iy - top;
        initIterator();
        for (; band<bandUp; band++) {
            for(int py=bounds.y,yn=bounds.y+bounds.height,j=0; py<yn; py++,j++){
                for(int px=bounds.x,xn=bounds.x+bounds.width,i=0; px<xn; px++,i++){
                    iterator.moveTo(px,py,band);
                    samples[j][i] = iterator.getSample();
                }
            }

            final int xfrac = (int) ((x-x0) * (1 << interpolation.getSubsampleBitsH()));
            final int yfrac = (int) ((y-y0) * (1 << interpolation.getSubsampleBitsV()));
            dest[band] = interpolation.interpolate(samples, xfrac, yfrac);
        }
        return dest;
    }

    /**
     * Interpolates at the specified position. If {@code fallback!=null},
     * then {@code dest} <strong>must</strong> have been initialized with
     * {@code super.evaluate(...)} prior to invoking this method.
     *
     * @param x      The x position in pixel's coordinates.
     * @param y      The y position in pixel's coordinates.
     * @param dest   The destination array, or null.
     * @param band   The first band's index to interpolate.
     * @param bandUp The last band's index+1 to interpolate.
     * @return {@code null} if point is outside grid coverage.
     */
    private synchronized float[] interpolate(final double x, final double y,
                                             float[] dest, int band, final int bandUp)
    {
        final double x0 = Math.floor(x);
        final double y0 = Math.floor(y);
        final int    ix = (int)x0;
        final int    iy = (int)y0;
        if (!(ix>=xmin && ix<xmax && iy>=ymin && iy<ymax)) {
            if (fallback == null) return null;
            if (fallback == this) return dest; // super.evaluate(...) succeed prior to this call.
            return fallback.interpolate(x, y, dest, band, bandUp);
        }
        /*
         * Create buffers, if not already created.
         */
        float[][] samples = floats;
        if (samples == null) {
            final int rowCount = interpolation.getHeight();
            final int colCount = interpolation.getWidth();
            floats = samples = new float[rowCount][];
            for (int i=0; i<rowCount; i++) {
                samples[i] = new float[colCount];
            }
        }
        if (dest == null) {
            dest = new float[bandUp];
        }
        /*
         * Builds up a RectIter and use it for interpolating all bands.
         * There is very few points, so the cost of creating a RectIter
         * may be important. But it seems to still lower than query tiles
         * many time (which may involve more computation than necessary).
         */
        bounds.x = ix - left;
        bounds.y = iy - top;
        initIterator();
        for (; band<bandUp; band++) {
            for(int py=bounds.y,yn=bounds.y+bounds.height,j=0; py<yn; py++,j++){
                for(int px=bounds.x,xn=bounds.x+bounds.width,i=0; px<xn; px++,i++){
                    iterator.moveTo(px,py,band);
                    samples[j][i] = iterator.getSampleFloat();
                }
            }

            float dx = (float)(x-x0); if (dx==1) dx=ONE_EPSILON;
            float dy = (float)(y-y0); if (dy==1) dy=ONE_EPSILON;
            final float value = interpolation.interpolate(samples, dx, dy);
            if (Float.isNaN(value)) {
                if (fallback == this) continue; // 'dest' was set by 'super.evaluate(...)'.
                if (fallback != null) {
                    fallback.interpolate(x, y, dest, band, band+1);
                    continue;
                }
                // If no fallback was specified, then 'dest' is not required to
                // have been initialized. It may contains random value.  Set it
                // to the NaN value...
            }
            dest[band] = value;
        }
        return dest;
    }

    /**
     * Interpolate at the specified position. If {@code fallback!=null},
     * then {@code dest} <strong>must</strong> have been initialized with
     * {@code super.evaluate(...)} prior to invoking this method.
     *
     * @param x      The x position in pixel's coordinates.
     * @param y      The y position in pixel's coordinates.
     * @param dest   The destination array, or null.
     * @param band   The first band's index to interpolate.
     * @param bandUp The last band's index+1 to interpolate.
     * @return {@code null} if point is outside grid coverage.
     */
    private synchronized double[] interpolate(final double x, final double y,
                                              double[] dest, int band, final int bandUp)
    {
        final double x0 = Math.floor(x);
        final double y0 = Math.floor(y);
        final int    ix = (int)x0;
        final int    iy = (int)y0;
        if (!(ix>=xmin && ix<xmax && iy>=ymin && iy<ymax)) {
            if (fallback == null) return null;
            if (fallback == this) return dest; // super.evaluate(...) succeed prior to this call.
            return fallback.interpolate(x, y, dest, band, bandUp);
        }
        /*
         * Creates buffers, if not already created.
         */
        double[][] samples = doubles;
        if (samples == null) {
            final int rowCount = interpolation.getHeight();
            final int colCount = interpolation.getWidth();
            doubles = samples = new double[rowCount][];
            for (int i=0; i<rowCount; i++) {
                samples[i] = new double[colCount];
            }
        }
        if (dest == null) {
            dest = new double[bandUp];
        }
        /*
         * Builds up a RectIter and use it for interpolating all bands.
         * There is very few points, so the cost of creating a RectIter
         * may be important. But it seems to still lower than query tiles
         * many time (which may involve more computation than necessary).
         */
        bounds.x = ix - left;
        bounds.y = iy - top;
        initIterator();
        for (; band<bandUp; band++) {
            for(int py=bounds.y,yn=bounds.y+bounds.height,j=0; py<yn; py++,j++){
                for(int px=bounds.x,xn=bounds.x+bounds.width,i=0; px<xn; px++,i++){
                    iterator.moveTo(px,py,band);
                    samples[j][i] = iterator.getSampleDouble();
                }
            }

            float dx = (float)(x-x0); if (dx==1) dx=ONE_EPSILON;
            float dy = (float)(y-y0); if (dy==1) dy=ONE_EPSILON;
            final double value = interpolation.interpolate(samples, dx, dy);
            if (Double.isNaN(value)) {
                if (fallback == this) continue; // 'dest' was set by 'super.evaluate(...)'.
                if (fallback != null) {
                    fallback.interpolate(x, y, dest, band, band+1);
                    continue;
                }
                // If no fallback was specified, then 'dest' is not required to
                // have been initialized. It may contains random value.  Set it
                // to the NaN value...
            }
            dest[band] = value;
        }
        return dest;
    }
}
