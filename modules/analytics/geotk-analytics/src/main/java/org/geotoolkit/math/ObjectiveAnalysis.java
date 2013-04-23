/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferFloat;
import java.awt.image.ComponentColorModel;
import java.awt.color.ColorSpace;
import java.awt.Transparency;
import javax.vecmath.GVector;
import javax.vecmath.GMatrix;
import javax.media.jai.RasterFactory;

import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.internal.image.ScaledColorSpace;

import static org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive;


/**
 * Computes values at the location of grid cells from a set of values at random locations.
 * This class is typically used for computing values on a regular grid (the output) from a
 * set of values at random locations (the input). However the class can also be used for
 * creating non-regular grids. For creating a non-regular grid, user should subclass
 * {@code ObjectiveAnalysis} and override the {@link #getOutputLocation getOutputLocation(...)}
 * method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Howard Freeland (MPO, for algorithmic inspiration)
 * @version 3.09
 *
 * @since 3.09 (derived from 1.0)
 * @module
 */
public class ObjectiveAnalysis {
    /**
     * Minimal <var>x</var> ordinate of grid cells.
     */
    private final double xmin;

    /**
     * Maximal <var>y</var> ordinate of grid cells.
     */
    private final double ymax;

    /**
     * Size of grid cells along the <var>x</var> axis.
     */
    private final double dx;

    /**
     * Size of grid cells along the <var>y</var> axis.
     */
    private final double dy;

    /**
     * Number of grid cells along the <var>x</var> axis.
     */
    private final int nx;

    /**
     * Number of grid cells along the <var>y</var> axis.
     */
    private final int ny;

    /**
     * Arbitrary scale factor computed from the grid region,
     * and used by the default implementation of {@link #correlation}
     */
    private final double scale;

    /**
     * The input vectors defined by the last call to {@link #setInputs(Vector, Vector, Vector)}.
     */
    private Vector xp, yp, zp;

    /**
     * Creates a new instance for interpolating values in the given region.
     *
     * @param gridRegion The grid bounding box. The maximal ordinates are inclusive.
     * @param nx The number of grid cells along the <var>x</var> axis.
     * @param ny The number of grid cells along the <var>y</var> axis.
     * @param cellLocation The position to evaluate in each grid cell.
     */
    public ObjectiveAnalysis(final Rectangle2D gridRegion, final int nx, final int ny,
            final PixelOrientation cellLocation)
    {
        if (gridRegion.isEmpty()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_RECTANGLE_1, gridRegion));
        }
        ensureStrictlyPositive("nx", nx);
        ensureStrictlyPositive("ny", ny);
        final double width  = gridRegion.getWidth();
        final double height = gridRegion.getHeight();
        final double dx = width  / (nx - 1);
        final double dy = height / (ny - 1);
        final PixelTranslation loc = PixelTranslation.getPixelTranslation(cellLocation);
        this.nx = nx;
        this.ny = ny;
        this.dx = dx;
        this.dy = dy;
        xmin  = gridRegion.getMinX() + dx * (loc.dx + 0.5);
        ymax  = gridRegion.getMaxY() + dy * (loc.dy + 0.5);
        scale = Math.hypot(width, height) * 0.5;
    }

    /**
     * Sets the input values at the given points. Those values will be used by the
     * {@link #interpolate(double[]) interpolate} methods for interpolating new values
     * at the locations defined by {@link #getOutputLocation(int, Point2D.Double)}.
     * <p>
     * This convenience method wraps the given arrays in {@link Vector} objects
     * and delegates to {@link #setInputs(Vector, Vector, Vector)}.
     *
     * @param xp The <var>x</var> ordinates of a random set of points.
     * @param yp The <var>y</var> ordinates of a random set of points.
     * @param zp The <var>z</var> values at the (<var>x</var>,<var>y</var>) coordinates
     *           defined by the {@code xp} and {@code yp} arguments.
     */
    public void setInputs(final double[] xp, final double[] yp, final double[] zp) {
        setInputs(new ArrayVector.Double(xp),
                  new ArrayVector.Double(yp),
                  new ArrayVector.Double(zp));
    }

    /**
     * Sets the input values at the given points. Those values will be used by the
     * {@link #interpolate(double[]) interpolate} methods for interpolating new values
     * at the locations defined by {@link #getOutputLocation(int, Point2D.Double)}.
     * <p>
     * This convenience method wraps the given arrays in {@link Vector} objects
     * and delegates to {@link #setInputs(Vector, Vector, Vector)}.
     *
     * @param xp The <var>x</var> ordinates of a random set of points.
     * @param yp The <var>y</var> ordinates of a random set of points.
     * @param zp The <var>z</var> values at the (<var>x</var>,<var>y</var>) coordinates
     *           defined by the {@code xp} and {@code yp} arguments.
     */
    public void setInputs(final float[] xp, final float[] yp, final float[] zp) {
        setInputs(new ArrayVector.Float(xp),
                  new ArrayVector.Float(yp),
                  new ArrayVector.Float(zp));
    }

    /**
     * Sets the input values at the given points. Those values will be used by the
     * {@link #interpolate(double[]) interpolate} methods for interpolating new values
     * at the locations defined by {@link #getOutputLocation(int, Point2D.Double)}.
     *
     * @param xp The <var>x</var> ordinates of a random set of points.
     * @param yp The <var>y</var> ordinates of a random set of points.
     * @param zp The <var>z</var> values at the (<var>x</var>,<var>y</var>) coordinates
     *           defined by the {@code xp} and {@code yp} arguments.
     */
    public void setInputs(final Vector xp, final Vector yp, final Vector zp) {
        this.xp = xp;
        this.yp = yp;
        this.zp = zp;
    }

    /**
     * Returns the number of points to be computed by this instance. This is the number
     * of grid cells. The {@link #interpolate(double[]) interpolate(...)} method will
     * return an array of this length.
     *
     * @return The number of points to be computed.
     */
    public int getOutputLength() {
        return nx * ny;
    }

    /**
     * Returns the (<var>x</var>,<var>y</var>) coordinate of the point evaluated at the given
     * index. This is the spatial location of {@code values[index]} where {@code values} is the
     * array returned by the {@link #interpolate(double[]) interpolate(...)} method.
     * <p>
     * If the {@code dest} argument is non-null, then the result will be written in the given
     * {@code Point2D} instance and this method returns {@code dest}. Otherwise this method
     * returns a newly allocated {@code Point2D} instance.
     *
     * @param  index Index (in the <code>[0 &hellip; {@linkplain #getOutputLength length}-1]</code>
     *         range) of an interpolated value.
     * @param  dest A pre-allocated {@code Point2D} to reuse, or {@code null} if none.
     * @return The (<var>x</var>,<var>y</var>) coordinate of the value at the index.
     */
    public Point2D.Double getOutputLocation(final int index, Point2D.Double dest) {
        if (dest == null) {
            dest = new Point2D.Double();
        }
        dest.x = xmin + dx * (index % ny);
        dest.y = ymax - dy * (index / ny); // NOSONAR
        return dest;
    }

    /**
     * Uses the values given to the {@link #setInputs(Vector, Vector, Vector) setInputs(...)}
     * method for interpolating new values at the locations defined by the
     * {@link #getOutputLocation getOutputLocation(...)} method. The results are stored in
     * the given array if it is not-null, or in a newly allocated array otherwise.
     * <p>
     * This method performs the following steps:
     * <p>
     * <ol>
     *   <li>If {@code dest} is null, then a new array is allocated with
     *       <code>new double[{@linkplain #getOutputLength()}]</code>.</li>
     *   <li>For each element <var>i</var> in the above array, the spatial location (used for the
     *       interpolation in the next step) is defined by {@code getOutputLocation(i, ...)}.</li>
     *   <li>The value at the above spatial location is interpolated from the values
     *       given by the {@code xp}, {@code yp} and {@code zp} vector using the
     *       <cite>Objective Analysis</cite> algorithm.
     * </ol>
     *
     * @param  dest A pre-allocated array, or {@code null} if none.
     * @return The interpolated values as an array of length {@link #getOutputLength}.
     */
    public double[] interpolate(double[] dest) {
        if (dest == null) {
            dest = new double[getOutputLength()];
        }
        interpolate(null, dest);
        return dest;
    }

    /**
     * Uses the values given to the {@link #setInputs(Vector, Vector, Vector) setInputs(...)}
     * method for interpolating new values at the locations defined by the
     * {@link #getOutputLocation getOutputLocation(...)} method. This method performs the
     * same work than {@link #interpolate(double[])}, but using single-precision numbers
     * instead than double-precision.
     *
     * @param  dest A pre-allocated array, or {@code null} if none.
     * @return The interpolated values as an array of length {@link #getOutputLength}.
     */
    public float[] interpolate(float[] dest) {
        if (dest == null) {
            dest = new float[getOutputLength()];
        }
        interpolate(dest, null);
        return dest;
    }

    /**
     * Ensures that the given input is non-null.
     */
    private static void ensureInputSet(final String name, final Vector value) {
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
    }

    /**
     * Uses the values given to the {@link #setInputs(Vector, Vector, Vector) setInputs(...)}
     * method for interpolating new values at the locations defined by the
     * {@link #getOutputLocation getOutputLocation(...)} method.
     *
     * @param dest1 If non-null, the results will be stored in this provided array.
     * @param dest2 If non-null, the results will be stored in this provided array.
     */
    private void interpolate(final float[] dest1, final double[] dest2) {
        final Vector xp = this.xp;
        final Vector yp = this.yp;
        final Vector zp = this.zp;
        ensureInputSet("xp", xp);
        ensureInputSet("yp", yp);
        ensureInputSet("zp", zp);
        /*
         * Compute a regression plane P of Z(x,y). The object P
         * will contains internally the plane's coefficients.
         */
        final Plane P = new Plane();
        P.fit(xp, yp, zp);
        /*
         * Create a matrix A(N,N) where N is the number of input data.
         * Note: the object 'GMatrix' is provided with Java3D.
         */
        final int N = zp.size();
        final GMatrix A = new GMatrix(N,N);
        final GVector X = new GVector(N);
        /*
         * Set the matrix elements. The square part A(i,j) is
         * the matrix of correlations among observations.
         */
        final Point2D.Double P1 = new Point2D.Double();
        final Point2D.Double P2 = new Point2D.Double();
        for (int i=0; i<N; i++){
            P1.x = xp.doubleValue(i);
            P1.y = yp.doubleValue(i);
            for (int j=0; j<N; j++) {
                P2.x = xp.doubleValue(j);
                P2.y = yp.doubleValue(j);
                A.setElement(i, j, correlation(P1, P2));
            }
            X.setElement(i, zp.doubleValue(i) - P.z(P1.x, P1.y));
        }
        /*
         * Compute (A⁻¹) × (X) and stores the result into X.
         */
        A.invert(); // A = A⁻¹
        X.mul(A,X); // X = A*X
        /*
         * Now compute values.
         */
        final int n = getOutputLength();
        for (int i=0; i<n; i++) {
            final Point2D.Double loc = getOutputLocation(i, P1);
            double value = P.z(loc.x, loc.y);
            double lowBits = 0;
            for (int k=0; k<N; k++) {
                P2.x = xp.doubleValue(k);
                P2.y = yp.doubleValue(k);
                double toAdd = X.getElement(k) * correlation(loc, P2);
                /*
                 * Compute value += toAdd
                 * using Kahan summation algorithm.
                 */
                toAdd += lowBits;
                lowBits = toAdd + (value - (value += toAdd));
            }
            if (dest1 != null) dest1[i] = (float) value;
            if (dest2 != null) dest2[i] = value;
        }
    }

    /**
     * Creates an image from the values {@linkplain #interpolate(float[]) interpolated} at the
     * locations defined by the {@link #getOutputLocation getOutputLocation(...)} method. The
     * default implementation assumes that the locations are defined in a row-major fashion,
     * with the row on the top of the image first and the row at the bottom of the image last.
     *
     * @return The image created from interpolated values.
     */
    public RenderedImage createImage() {
        final WritableRaster raster = RasterFactory.createBandedRaster(DataBufferFloat.TYPE_FLOAT, nx, ny, 1, null);
        final float[] data = ((DataBufferFloat) raster.getDataBuffer()).getData();
        final float[] result = interpolate(data);
        if (result != data) {
            // Should never happen, but done anyway in case the
            // user override the interpolate method in a bad way.
            System.arraycopy(result, 0, data, 0, data.length);
        }
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;
        for (final float v : data) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        final ColorSpace cs;
        if (min < max) {
            cs = new ScaledColorSpace(1, 0, min, max);
        } else {
            cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        }
        return new BufferedImage(new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE, DataBufferFloat.TYPE_FLOAT), raster, false, null);
    }

    /**
     * Returns the correlation between the values at the two given points. For example if
     * {@code P1} and {@code P2} are the location of measurement stations and time series
     * are available for those stations, then this method shall returns the correlation
     * coefficient between the data at station 1 and the data at station 2.
     * <p>
     * This method should be overridden by subclasses, typically with a correlation estimated
     * from the distance between the two stations. The value returned by this method must be
     * in the [0&hellip;1] range.
     *
     * {@section Default implementation}
     * Current implementation assumes that the correlation has a gaussian distribution,
     * with a value approaching zero as the distance between the two stations increase.
     * The calibration coefficients in current implementation are totally arbitrary and
     * may change in any future version.
     *
     * @param  P1 The first location. Implementors shall not modify this value.
     * @param  P2 The second location. Implementors shall not modify this value.
     * @return The correlation coefficient (in the [0&hellip;1] range) between the values
     *         at the two given locations.
     */
    protected double correlation(final Point2D.Double P1, final Point2D.Double P2) {
        double distance = Math.hypot(P1.x - P2.x, P1.y - P2.y);
        distance = distance / scale - 1./150; // Similar to the basic program DISPWX
        if (distance < 0) {
            return 1 - 15*distance;
        }
        return Math.exp(-distance * distance);
    }
}
