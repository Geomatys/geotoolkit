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

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import net.jcip.annotations.ThreadSafe;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.operation.MathTransform2D;

import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;
import org.geotoolkit.referencing.operation.MathTransforms;

import static org.apache.sis.util.ArgumentChecks.ensureBetween;


/**
 * A factory for {@link MathTransform2D} backed by a <cite>grid of localization</cite>. A grid of
 * localization is a two-dimensional array of coordinate points. The grid size is {@code width}
 * &times {@code height}. Input coordinates are (<var>i</var>,<var>j</var>) index in the grid,
 * where <var>i</var> must be in the range {@code [0..width-1]} and <var>j</var> in the range
 * {@code [0..height-1]} inclusive. Output coordinates are the values stored in the grid of
 * localization at the specified index.
 * <p>
 * The {@code LocalizationGrid} class is useful when the "{@linkplain GridGeometry#getGridToCRS
 * grid to CRS}" transform for a coverage is not some kind of global mathematical relationship
 * like an {@linkplain AffineTransform affine transform}. Instead, the "real world" coordinates
 * are explicitly specified for each pixels. If the real world coordinates are know only for some
 * pixels at a fixed interval, then a transformation can be constructed by the concatenation of
 * an affine transform with a grid of localization.
 * <p>
 * After a {@code LocalizationGrid} object has been fully constructed (i.e. real world coordinates
 * have been specified for all grid cells), a transformation from grid coordinates to "real world"
 * coordinates can be obtained with the {@link #getMathTransform} method. If this transformation is
 * close enough to an affine transform, then an instance of {@link AffineTransform} is returned.
 * Otherwise, a transform backed by the localization grid is returned.
 * <p>
 * The example below goes through the steps of constructing a coordinate reference system for a grid
 * coverage from its grid of localization. This example assumes that the "real world" coordinates
 * are longitudes and latitudes on the {@linkplain DefaultGeodeticDatum#WGS84 WGS84} ellipsoid.
 *
 * {@preformat java
 *     //
 *     // Constructs a localization grid of size 10 x 10.
 *     //
 *     LocalizationGrid grid = new LocalizationGrid(10, 10);
 *     for (int j=0; j<10; j++) {
 *         for (int i=0; i<10; i++) {
 *             double x = ...; // Set the longitude here
 *             double y = ...; // Set the latitude here
 *             grid.setLocalizationPoint(i, j, x, y);
 *         }
 *     }
 *     //
 *     // Code below constructs the grid coordinate reference system. "degree" is the polynomial
 *     // degree (e.g. 2) for a math transform that approximately map the grid of localization.
 *     // For a more accurate (but not always better) math transform backed by the whole grid,
 *     // invoke getMathTransform() instead, or use the special value of 0 for the degree argument.
 *     //
 *     MathTransform2D        realToGrid = grid.getPolynomialTransform(degree).inverse();
 *     CoordinateReferenceSystem realCRS = CommonCRS.WGS84.normalizedGeographic();
 *     CoordinateReferenceSystem gridCRS = new DefaultDerivedCRS("The grid CRS",
 *             new DefaultOperationMethod(realToGrid),
 *             realCRS,     // The target ("real world") CRS
 *             realToGrid,  // How the grid CRS relates to the "real world" CRS
 *             DefaultCartesianCS.GRID);
 *     //
 *     // Constructs the grid coverage using the grid coordinate system (not the "real world"
 *     // one). It is useful to display the coverage in its native CRS before we resample it.
 *     // Note that if the grid of localization does not define the geographic location for
 *     // all pixels, then we need to specify some affine transform in place of the call to
 *     // IdentityTransform. For example if the grid of localization defines the location of
 *     // 1 pixel, then skip 3, then defines the location of 1 pixel, etc., then the affine
 *     // transform should be AffineTransform.getScaleInstance(0.25, 0.25).
 *     //
 *     WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, width, height, 1, null);
 *     for (int y=0; y<height; y++) {
 *         for (int x=0; x<width; x++) {
 *             raster.setSample(x, y, 0, some_value);
 *         }
 *     }
 *     GridCoverageFactory factory = FactoryFinder.getGridCoverageFactory(null);
 *     GridCoverage coverage = factory.create("My grayscale coverage", raster, gridCRS,
 *             IdentityTransform.create(2), null, null, null, null, null);
 *     coverage.show();
 *     //
 *     // Projects the coverage from its current 'gridCS' to the 'realCS'. If the grid of
 *     // localization was built from the orbit of some satellite, then the projected
 *     // coverage will tpypically have a curved aspect.
 *     //
 *     coverage = (Coverage2D) Operations.DEFAULT.resample(coverage, realCRS);
 *     coverage.show();
 * }
 *
 * @todo <code>LocalizationGridBuilder</code> would be a better name.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Alessio Fabiani (Geosolutions)
 * @version 3.00
 *
 * @see org.opengis.referencing.crs.DerivedCRS
 *
 * @since 2.0
 * @module
 */
@ThreadSafe
public class LocalizationGrid {
    /**
     * Number of grid's columns.
     */
    private final int width;

    /**
     * Number of grid's rows.
     */
    private final int height;

    /**
     * Grid of coordinate points.
     */
    private double[] gridX, gridY;

    /**
     * A global affine transform for the whole grid. This affine transform
     * will be computed when first requested using a "least squares" fitting.
     */
    private transient AffineTransform global;

    /**
     * Math transforms from grid to "real world" data for various degrees. By convention,
     * {@code transforms[0]} is the transform backed by the whole grid. Other index are fittings
     * using different polynomial degrees ({@code transforms[1]} for affine, {@code transforms[2]}
     * for quadratic, <i>etc.</i>). Will be computed only when first needed.
     */
    private transient MathTransform2D[] transforms;

    /**
     * Constructs an initially empty localization grid. All "real worlds"
     * coordinates are initially set to {@code (NaN,NaN)}.
     *
     * @param width  Number of grid's columns.
     * @param height Number of grid's rows.
     */
    public LocalizationGrid(final int width, final int height) {
        if (width < 1) {
            throw new IllegalArgumentException(String.valueOf(width));
        }
        if (height < 1) {
            throw new IllegalArgumentException(String.valueOf(height));
        }
        this.width  = width;
        this.height = height;
        final int length = width * height;
        gridX = new double[length];
        gridY = new double[length];
        Arrays.fill(gridX, Double.NaN);
        Arrays.fill(gridY, Double.NaN);
    }

    /**
     * Computes the index of a record in the grid.
     *
     * @param  row  x coordinate of a point.
     * @param  col  y coordinate of a point.
     * @return The record index in the grid.
     */
    private int computeOffset(final int col, final int row) {
        if (col < 0 || col >= width) {
            throw new IndexOutOfBoundsException(String.valueOf(col));
        }
        if (row < 0 || row >= height) {
            throw new IndexOutOfBoundsException(String.valueOf(row));
        }
        return (col + row * width);
    }

    /**
     * Returns the grid size. Grid coordinates are always in the range
     * <code>x<sub>input</sub>&nbsp;=&nbsp;[0..width-1]</code> and
     * <code>y<sub>input</sub>&nbsp;=&nbsp;[0..height-1]</code> inclusive.
     *
     * @return The grid size.
     */
    public Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * Returns the "real world" coordinates for the specified grid coordinates.
     * Grid coordinates must be integers inside this grid's range.  For general
     * transformations involving non-integer grid coordinates and/or coordinates
     * outside this grid's range, use {@link #getMathTransform} instead.
     *
     * @param  source The point in grid coordinates.
     * @return target The corresponding point in "real world" coordinates.
     * @throws IndexOutOfBoundsException If the source point is not in this grid's range.
     */
    public synchronized Point2D getLocalizationPoint(final Point source) {
        final int offset = computeOffset(source.x, source.y);
        return new Point2D.Double(gridX[offset], gridY[offset]);
    }

    /**
     * Sets a point in this localization grid.
     *
     * @param  source The point in grid coordinates.
     * @param  target The corresponding point in "real world" coordinates.
     * @throws IndexOutOfBoundsException If the source point is not in this grid's range.
     */
    public void setLocalizationPoint(final Point source, final Point2D target) {
        setLocalizationPoint(source.x, source.y, target.getX(), target.getY());
    }

    /**
     * Sets a point in this localization grid.
     *
     * @param sourceX  <var>x</var> coordinates in grid coordinates,
     *                 in the range {@code [0..width-1]} inclusive.
     * @param sourceY  <var>y</var> coordinates in grid coordinates.
     *                 in the range {@code [0..height-1]} inclusive.
     * @param targetX  <var>x</var> coordinates in "real world" coordinates.
     * @param targetY  <var>y</var> coordinates in "real world" coordinates.
     * @throws IndexOutOfBoundsException If the source coordinates is not in this grid's range.
     */
    public synchronized void setLocalizationPoint(int    sourceX, int    sourceY,
                                                  double targetX, double targetY)
    {
        final int offset = computeOffset(sourceX, sourceY);
        notifyChange();
        global = null;
        gridX[offset] = targetX;
        gridY[offset] = targetY;
    }

    /**
     * Applies a transformation to every "real world" coordinate points in a sub-region
     * of this grid.
     *
     * @param transform The transform to apply.
     * @param region The bounding rectangle (in grid coordinate) for region where to
     *        apply the transform, or {@code null} to transform the whole grid.
     */
    public synchronized void transform(final AffineTransform transform, Rectangle region) {
        final Point2D.Double buffer = new Point2D.Double();
        if (region == null) {
            region = new Rectangle(width, height);
        }
        computeOffset(region.x, region.y); // Range check.
        int j = region.y + region.height;
        while (--j >= region.y) {
            int i = region.x + region.width;
            while (--i >= region.x) {
                final int offset = computeOffset(i, j);
                notifyChange();
                buffer.x = gridX[offset];
                buffer.y = gridY[offset];
                transform.transform(buffer, buffer);
                gridX[offset] = buffer.x;
                gridY[offset] = buffer.y;
            }
        }
        global = null;
    }

    /**
     * Returns {@code true} if this localization grid contains at least one {@code NaN} value.
     *
     * @return {@code true} if this localization grid contains at least one {@code NaN} value.
     */
    public synchronized boolean isNaN() {
        for (int i=gridY.length; --i>=0;) {
            if (Double.isNaN(gridY[i])) {
                return true;
            }
        }
        for (int i=gridX.length; --i>=0;) {
            if (Double.isNaN(gridX[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if all coordinates in this grid are increasing or decreasing.
     * More specifically, returns {@code true} if the following conditions are meets:
     * <p>
     * <ul>
     *   <li>Coordinates in a row must be increasing or decreasing. If {@code strict} is
     *       {@code true}, then coordinates must be strictly increasing or decreasing (i.e.
     *       equals value are not accepted). {@code NaN} values are always ignored.</li>
     *   <li>Coordinates in all rows must be increasing, or coordinates in all rows must be
     *       decreasing.</li>
     *   <li>Idem for columns (Coordinates in a columns must be increasing or decreasing,
     *       etc.).</li>
     * </ul>
     * <p>
     * <var>x</var> and <var>y</var> coordinates are tested independently.
     *
     * @param  strict {@code true} to require strictly increasing or decreasing order,
     *         or {@code false} to accept values that are equals.
     * @return {@code true} if coordinates are increasing or decreasing in the same
     *         direction for all rows and columns.
     */
    public synchronized boolean isMonotonic(final boolean strict) {
        int orderX = INCREASING | DECREASING;
        int orderY = INCREASING | DECREASING;
        if (!strict) {
            orderX |= EQUALS;
            orderY |= EQUALS;
        }
        for (int i=0; i<width; i++) {
            final int offset = computeOffset(i,0);
            if ((orderX = testOrder(gridX, offset, height, width, orderX)) == 0) return false;
            if ((orderY = testOrder(gridY, offset, height, width, orderY)) == 0) return false;
        }
        orderX = INCREASING | DECREASING;
        orderY = INCREASING | DECREASING;
        if (!strict) {
            orderX |= EQUALS;
            orderY |= EQUALS;
        }
        for (int j=0; j<height; j++) {
            final int offset = computeOffset(0,j);
            if ((orderX = testOrder(gridX, offset, width, 1, orderX)) == 0) return false;
            if ((orderY = testOrder(gridY, offset, width, 1, orderY)) == 0) return false;
        }
        return true;
    }

    /** Constant for {@link #testOrder}. */ private static final int INCREASING = 1;
    /** Constant for {@link #testOrder}. */ private static final int DECREASING = 2;
    /** Constant for {@link #testOrder}. */ private static final int EQUALS     = 4;

    /**
     * Checks the ordering of elements in a sub-array. {@link Double#NaN} values are ignored.
     *
     * @param grid   The {@link #grid} array.
     * @param offset The first element to test.
     * @param num    The number of elements to test.
     * @param step   The amount to increment {@code offset} in order to reach the next element.
     * @param flags  A combination of {@link #INCREASING}, {@link #DECREASING} and {@link #EQUALS}
     *               that specify which ordering are accepted.
     * @return       0 if the array is unordered. Otherwise, returns {@code flags} with maybe
     *               one of {@link #INCREASING} or {@link #DECREASING} flags cleared.
     */
    private static int testOrder(final double[] grid, int offset, int num, final int step, int flags) {
        // We will check (num-1) combinations of coordinates.
        for (--num; --num>=0; offset += step) {
            final double v1 = grid[offset];
            if (Double.isNaN(v1)) continue;
            while (true) {
                final double v2 = grid[offset + step];
                final int required, clear;
                if (v1 == v2) {
                    required =  EQUALS;      // "equals" must be accepted.
                    clear    = ~0;           // Do not clear anything.
                } else if (v2 > v1) {
                    required =  INCREASING;  // "increasing" must be accepted.
                    clear    = ~DECREASING;  // do not accepts "decreasing" anymore.
                } else if (v2 < v1) {
                    required =  DECREASING;  // "decreasing" must be accepted.
                    clear    = ~INCREASING;  // do not accepts "increasing" anymore.
                } else {
                    // 'v2' is NaN. Search for the next element.
                    if (--num < 0) {
                        return flags;
                    }
                    offset += step;
                    continue; // Mimic the "goto" statement.
                }
                if ((flags & required) == 0) {
                    return 0;
                }
                flags &= clear;
                break;
            }
        }
        return flags;
    }

    /**
     * Makes sure that the grid doesn't contains identical consecutive ordinates. If many
     * consecutive ordinates are found to be identical in a row or in a column, then
     * the first one is left unchanged and the other ones are linearly interpolated.
     */
    public void removeSingularities() {
        removeSingularities(gridX, false);
        removeSingularities(gridX, true );
        removeSingularities(gridY, false);
        removeSingularities(gridY, true );
    }

    /**
     * Applies a linear interpolation on consecutive identical ordinates.
     *
     * @param grid The grid to process, either {@link #gridX} or {@link #gridY}.
     * @param vertical {@code true} to scan the grid vertically, or {@code false}
     *        to scan the grid horizontally.
     */
    private void removeSingularities(final double[] grid, final boolean vertical) {
        final int step, val1, val2;
        if (vertical) {
            step = width;
            val1 = width;
            val2 = height;
        } else {
            step = 1;
            val1 = height;
            val2 = width;
        }
        for (int i=0; i<val1; i++) {
            final int offset;
            if (vertical) {
                offset = computeOffset(i,0);
            } else {
                offset = computeOffset(0,i);
            }
            int singularityOffset = -1;
            for (int j=1; j<val2 ; j++) {
                final int previousOffset = offset+step*(j-1);
                final int currentOffset  = previousOffset + step;
                if (grid[previousOffset] == grid[currentOffset]) {
                    if (singularityOffset == -1) {
                        singularityOffset = previousOffset;
                        if (previousOffset != offset) {
                            singularityOffset -= step;
                        }
                    }
                } else if (singularityOffset != -1) {
                    final int num = (currentOffset - singularityOffset) / step + 1;
                    replaceSingularity(grid, singularityOffset, num, step);
                    singularityOffset = -1;
                }
            }
            if (singularityOffset != -1) {
                final int currentOffset = offset+step*(val2-1);
                final int num = (currentOffset - singularityOffset) / step + 1;
                replaceSingularity(grid, singularityOffset, num, step);
            }
        }
    }

    /**
     * Replaces consecutive singularity by linear values in sub-array.
     *
     * Example (we consider a grid of five element with singularity) :
     *
     *                  before
     *              ┌──┬──┬──┬──┬──┐
     *              │07│08│08│08│11│
     *              └──┴──┴──┴──┴──┘
     *
     * Params are : offset = 0, num = 5, step = 1
     *
     *                  after
     *              ┌──┬──┬──┬──┬──┐
     *              │07│08│09│10│11│
     *              └──┴──┴──┴──┴──┘
     *                ↑           ↑
     *              linear values are computed with these values
     *
     * @param grid   The {@link #gridX} or {@link #gridY} array.
     * @param offset The first element.
     * @param num    The number of element.
     * @param step   The amount to increment {@code offset} in order to reach the next element.
     */
    private static void replaceSingularity(final double[] grid, int offset, int num, final int step) {
        final double increment = (grid[offset+(num-1)*step] - grid[offset])/((double)(num-1));
        final double value = grid[offset];
        offset+= step;
        for (int i=0; i<(num-2); i++, offset += step) {
            grid[offset] = value + (increment * (i+1));
        }
    }

    /**
     * Returns an affine transform for the whole grid. This transform is only an approximation
     * for this localization grid.  It is fitted (like "curve fitting") to grid data using the
     * "least squares" method.
     *
     * @return A global affine transform as an approximation for the whole localization grid.
     */
    public synchronized AffineTransform getAffineTransform() {
        if (global == null) {
            final double[] matrix = new double[6];
            fitPlane(gridX, 0, matrix);
            fitPlane(gridY, 1, matrix);
            global = new AffineTransform(matrix);
        }
        return new AffineTransform(global);
    }

    /**
     * Fits a plane through the longitude or latitude values. More specifically, find
     * coefficients <var>c</var>, <var>cx</var> and <var>cy</var> for the following
     * equation:
     *
     * {@preformat math
     *     [longitude or latitude] = c + cx*x + cy*y
     * }
     *
     * where <var>x</var> and <var>cx</var> are grid coordinates.
     * Coefficients are computed using the least-squares method.
     *
     *
     * @param grid   The grid to process, either {@link #gridX} or {@link #gridY}.
     * @param offset 0 for fitting longitude values, or 1 for fitting latitude values
     *               (assuming that "real world" coordinates are longitude and latitude values).
     * @param coeff  An array of length 6 in which to store plane's coefficients.
     *               Coefficients will be store in the following order:
     *               {@code coeff[0 + offset] = cx;}
     *               {@code coeff[2 + offset] = cy;}
     *               {@code coeff[4 + offset] = c;}
     */
    private void fitPlane(final double[] grid, final int offset, final double[] coeff) {
        /*
         * Computes the sum of x, y and z values. Computes also the sum of x*x, y*y, x*y, z*x
         * and z*y values. When possible, we will avoid to compute the sum inside the loop and
         * use the following identities instead:
         *
         *           1 + 2 + 3 ... + n    =    n*(n+1)/2              (arithmetic series)
         *        1² + 2² + 3² ... + n²   =    n*(n+0.5)*(n+1)/3
         */
        double x,y,z, xx,yy, xy, zx,zy;
        z = zx = zy = 0; // To be computed in the loop.
        int n = 0;
        for (int yi=0; yi<height; yi++) {
            for (int xi=0; xi<width; xi++) {
                assert computeOffset(xi,yi) == n : n;
                final double zi = grid[n];
                z  += zi;
                zx += zi*xi;
                zy += zi*yi;
                n++;
            }
        }
        assert n == width * height : n;
        x  = (n * (double) (width -1))            / 2;
        y  = (n * (double) (height-1))            / 2;
        xx = (n * (width -0.5) * (width -1))      / 3;
        yy = (n * (height-0.5) * (height-1))      / 3;
        xy = (n * (double)((height-1)*(width-1))) / 4;
        /*
         * Solves the following equations for cx and cy:
         *
         *    ( zx - z*x )  =  cx*(xx - x*x) + cy*(xy - x*y)
         *    ( zy - z*y )  =  cx*(xy - x*y) + cy*(yy - y*y)
         */
        zx -= z*x/n;
        zy -= z*y/n;
        xx -= x*x/n;
        xy -= x*y/n;
        yy -= y*y/n;
        final double den= (xy*xy - xx*yy);
        final double cy = (zx*xy - zy*xx) / den;
        final double cx = (zy*xy - zx*yy) / den;
        final double c  = (z - (cx*x + cy*y)) / n;
        coeff[0 + offset] = cx;
        coeff[2 + offset] = cy;
        coeff[4 + offset] = c;
    }

    /**
     * Returns this localization grid and its inverse as warp objects. This method tries to fit
     * a {@linkplain javax.media.jai.WarpPolynomial polynomial warp} to the gridded coordinates.
     * The resulting Warp is wrapped into a {@link WarpTransform2D}.
     */
    private MathTransform2D fitWarps(final int degree) {
        final float[] srcCoords = new float[width*height*2];
        final float[] dstCoords = new float[srcCoords.length];
        int gridOffset = 0;
        int warpOffset = 0;
        for (int yi=0; yi<height; yi++) {
            for (int xi=0; xi<width; xi++) {
                assert gridOffset == computeOffset(xi, yi);
                final float x = (float) gridX[gridOffset];
                final float y = (float) gridY[gridOffset];
                if (!Float.isNaN(x) && !Float.isNaN(y)) {
                    srcCoords[warpOffset  ] = xi;
                    srcCoords[warpOffset+1] = yi;
                    dstCoords[warpOffset  ] = x;
                    dstCoords[warpOffset+1] = y;
                    warpOffset += 2;
                }
                gridOffset++;
            }
        }
        return new WarpTransform2D(null, srcCoords, 0, null, dstCoords, 0, warpOffset/2, degree);
    }

    /**
     * Returns a math transform from grid to "real world" coordinates using a polynomial fitting
     * of the specified degree. By convention, a {@code degree} of 0 will returns the
     * {@linkplain #getMathTransform() math transform backed by the whole grid}. Greater values
     * will use a fitted polynomial ({@linkplain #getAffineTransform affine transform} for
     * degree 1, quadratic transform for degree 2, cubic transform for degree 3, etc.).
     *
     * @param  degree The polynomial degree for the fitting, or 0 for a transform backed by the
     *         whole grid.
     * @return A math transform from grid to "real world" coordinates.
     */
    public synchronized MathTransform2D getPolynomialTransform(final int degree) {
        ensureBetween("degree", 0, WarpTransform2D.MAX_DEGREE, degree);
        if (transforms == null) {
            transforms = new MathTransform2D[WarpTransform2D.MAX_DEGREE + 1];
        }
        if (transforms[degree] == null) {
            final MathTransform2D tr;
            switch (degree) {
                case 0: {
                    // NOTE: 'grid' is not cloned. This GridLocalization's grid will need to be
                    // cloned if a "set" method is invoked after the math transform creation.
                    tr = new LocalizationGridTransform2D(width, height, gridX, gridY, getAffineTransform());
                    break;
                }
                case 1:  {
                    tr = (MathTransform2D) MathTransforms.linear(getAffineTransform());
                    break;
                }
                default: {
                    tr = fitWarps(degree);
                    break;
                }
            }
            transforms[degree] = tr;
        }
        return transforms[degree];
    }

    /**
     * Returns a math transform from grid to "real world" coordinates. The math transform is backed
     * by the full grid of localization. In terms of JAI's {@linkplain javax.media.jai.Warp image warp}
     * operations, this math transform is backed by a {@link javax.media.jai.WarpGrid} while the
     * previous methods return math transforms backed by {@link javax.media.jai.WarpPolynomial}.
     *
     * @return A math transform from grid to "real world" coordinates
     */
    public final MathTransform2D getMathTransform() {
        return getPolynomialTransform(0);
    }

    /**
     * Notifies this localization grid that a coordinate is about to be changed. This method
     * invalidate any transforms previously created.
     */
    private void notifyChange() {
        if (transforms != null) {
            if (transforms[0] != null) {
                // Clones is required only for the grid-backed transform.
                gridX = gridX.clone();
                gridY = gridY.clone();
            }
            // Signal that all transforms need to be recomputed.
            transforms = null;
        }
    }
}
