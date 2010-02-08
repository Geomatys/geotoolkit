/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import javax.vecmath.GVector;
import javax.vecmath.GMatrix;

import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;


/**
 * Computes values at the location of grid cells from a set of values at random locations.
 * This class is typically used for computing values on a regular grid (the output) from a
 * set of values at random locations (the input). However the class can also be used for
 * creating non-regular grids. For creating a non-regular grid, user should subclass
 * {@code ObjectiveAnalysis} and override the {@link #getLocation(int, Point2D.Double)}
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
     * Minimal <var>y</var> ordinate of grid cells.
     */
    private final double ymin;

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
     * Creates a new instance for interpolating values in the given region.
     *
     * @param gridRegion The grid bounding box.
     * @param nx The number of grid cells along the <var>x</var> axis.
     * @param ny The number of grid cells along the <var>y</var> axis.
     * @param cellLocation The position to evaluate in each grid cell.
     */
    public ObjectiveAnalysis(final Rectangle2D gridRegion, final int nx, final int ny,
            final PixelOrientation cellLocation)
    {
        if (gridRegion.isEmpty()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.BAD_RECTANGLE_$1, gridRegion));
        }
        ensurePositive("nx", nx);
        ensurePositive("ny", ny);
        final double dx = gridRegion.getWidth()  / (nx - 1);
        final double dy = gridRegion.getHeight() / (ny - 1);
        final PixelTranslation loc = PixelTranslation.getPixelTranslation(cellLocation);
        this.nx = nx;
        this.ny = ny;
        this.dx = dx;
        this.dy = dy;
        xmin = gridRegion.getX() + dx * (loc.dx + 0.5);
        ymin = gridRegion.getY() + dy * (loc.dy + 0.5);
    }

    /**
     * Ensures that the given parameter is greater than zero.
     */
    private static void ensurePositive(final String name, final int n) {
        if (n < 1) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, name, n));
        }
    }

    /**
     * Returns the number of points to be computed by this instance. This is the number
     * of grid cells. The {@link #interpolate interpolate(...)} method will return an array
     * of this length.
     *
     * @return The number of points to be computed.
     */
    public int getLength() {
        return nx * ny;
    }

    /**
     * Returns the (<var>x</var>,<var>y</var>) coordinate of the point evaluated at the given
     * index. This is the spatial location of {@code values[index]} where {@code values} is the
     * array returned by the {@link #interpolate(Vector, Vector, Vector) interpolate(...)} method.
     * <p>
     * If the {@code dest} argument is non-null, then the result will be writen in the given
     * {@code Point2D} instance and this method returns {@code dest}. Otherwise this method
     * returns a newly allocated {@code Point2D} instance.
     *
     * @param  index Index (in the <code>[0 &hellip; {@linkplain #getLength length}-1]</code>
     *         range) of an interpolated value.
     * @param  dest A pre-allocated {@code Point2D} to reuse, or {@code null} if none.
     * @return The (<var>x</var>,<var>y</var>) coordinate of the value at the index.
     */
    public Point2D.Double getLocation(final int index, Point2D.Double dest) {
        if (dest == null) {
            dest = new Point2D.Double();
        }
        dest.x = xmin + dx * (index % ny);
        dest.y = ymin + dy * (index / ny);
        return dest;
    }

    /**
     * Uses the values at the given points for interpolating new values at the locations defined by
     * {@link #getLocation(int, Point2D.Double)}. This convenience method wraps the given arrays in
     * {@link Vector} objects and delegates to {@link #interpolate(Vector, Vector, Vector)}.
     *
     * @param xp The <var>x</var> ordinates of a random set of points.
     * @param yp The <var>y</var> ordinates of a random set of points.
     * @param zp The <var>z</var> values at the (<var>x</var>,<var>y</var>) coordinates
     *           defined by the {@code xp} and {@code yp} arguments.
     * @return The interpolated values as an array of length {@link #getLength}.
     */
    public double[] interpolate(final double[] xp, final double[] yp, final double[] zp) {
        return interpolate(new ArrayVector.Double(xp),
                           new ArrayVector.Double(yp),
                           new ArrayVector.Double(zp));
    }

    /**
     * Uses the values at the given points for interpolating new values at the locations defined by
     * {@link #getLocation(int, Point2D.Double)}. This convenience method wraps the given arrays in
     * {@link Vector} objects and delegates to {@link #interpolate(Vector, Vector, Vector)}.
     *
     * @param xp The <var>x</var> ordinates of a random set of points.
     * @param yp The <var>y</var> ordinates of a random set of points.
     * @param zp The <var>z</var> values at the (<var>x</var>,<var>y</var>) coordinates
     *           defined by the {@code xp} and {@code yp} arguments.
     * @return The interpolated values as an array of length {@link #getLength}.
     */
    public double[] interpolate(final float[] xp, final float[] yp, final float[] zp) {
        return interpolate(new ArrayVector.Float(xp),
                           new ArrayVector.Float(yp),
                           new ArrayVector.Float(zp));
    }

    /**
     * Uses the values at the given points for interpolating new values at the locations defined by
     * {@link #getLocation(int, Point2D.Double)}. This method performs the following steps:
     * <p>
     * <ol>
     *   <li>An array is created that way: <code>new double[{@linkplain #getLength()}]</code>.</li>
     *   <li>For each element <var>i</var> in the above array, the spatial location (used for the
     *       interpolation in the next step) is defined by {@code getLocation(i, ...)}.</li>
     *   <li>The value at the above spatial location is interpolated from the values
     *       given by the {@code xp}, {@code yp} and {@code zp} vector using the
     *       <cite>Objective Analysis</cite> algorithm.
     * </ol>
     *
     * @param xp The <var>x</var> ordinates of a random set of points.
     * @param yp The <var>y</var> ordinates of a random set of points.
     * @param zp The <var>z</var> values at the (<var>x</var>,<var>y</var>) coordinates
     *           defined by the {@code xp} and {@code yp} arguments.
     * @return The interpolated values as an array of length {@link #getLength}.
     */
    public double[] interpolate(final Vector xp, final Vector yp, final Vector zp) {
        /*
         * Compute a regression plane P of Z(x,y). The object P
         * will contains internaly the plane's coefficients.
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
        final double values[] = new double[getLength()];
        for (int i=0; i<values.length; i++) {
            final Point2D.Double loc = getLocation(i, P1);
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
            values[i] = value;
        }
        return values;
    }

    /**
     * Returns the correlation between the values at the two given points. For example if
     * {@code P1} and {@code P2} are the location of measurement stations and time series
     * are available for those stations, then this method shall returns the correlation
     * coefficient between the data at station 1 and the data at station 2.
     * <p>
     * This method should be overriden by subclasses, typically with a correlation estimated
     * from the distance between the two stations. The value returned by this method must be
     * in the [0&hellip;1] range.
     *
     * {@section Default implementation}
     * Current implementation assumes that the correlation has a gaussian distribution,
     * with a value approaching zero as the distance between the two stations increase.
     * The calibation coefficients in current implementation are totally arbitrary and
     * will surely change in a future version.
     *
     * @param  P1 The first location. Implementors shall not modify this value.
     * @param  P2 The second location. Implementors shall not modify this value.
     * @return The correlation coefficient (in the [0&hellip;1] range) between the values
     *         at the two given locations.
     *
     * @todo Improves the default implementation with coefficients computed from the
     *       bounding box given to the constructor.
     */
    protected double correlation(final Point2D.Double P1, final Point2D.Double P2) {
        double distance = Math.hypot(P1.x - P2.x, P1.y - P2.y);
        distance = ((distance / 1000) - 1) / 150; // Similar to the basic program DISPWX
        if (distance < 0) {
            return 1 - 15*distance;
        }
        return Math.exp(-distance * distance);
    }
}
