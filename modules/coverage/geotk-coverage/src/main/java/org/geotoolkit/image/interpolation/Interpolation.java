/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.interpolation;

import java.awt.Rectangle;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * <p>Define standard interpolation.<br/><br/>
 *
 * Regardless interpolation type, each interpolation is computing from sample center.</p>
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class Interpolation {

    /**
     * Current {@code PixelIterator} which is interpolate.
     */
    protected final PixelIterator pixelIterator;

    /**
     * Number of bands from object that iterate.
     */
    protected final int numBands;

    /**
     * Boundary from object that iterate.
     */
    protected final Rectangle boundary;

    /**
     * last search of min max value area.
     */
    protected Rectangle precMinMax;

    /**
     * <p>Double table which contain minimum and maximum value, and x, y associate coordinates for each image band.</p>
     */
    protected double[] minMax;

    /**
     * Build an Interpolate object.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public Interpolation(PixelIterator pixelIterator) {
        this.pixelIterator = pixelIterator;
        this.numBands = pixelIterator.getNumBands();
        this.boundary = pixelIterator.getBoundary();
        this.minMax = null;
    }

    /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return interpolate value from x, y pixel coordinate.
     */
    public abstract double[] interpolate(double x, double y);

    /**
     * <p>Find minimum and maximum pixels values for each band.<br/>
     * Moreover double table result has length equal to 6*band number.<br/><br/>
     * <var>min<sub>0</sub></var>  : min from band 0.<br/>
     * <var>minX<sub>0</sub></var> : x coordinate from min value from band 0.<br/>
     * <var>minY<sub>0</sub></var> : y coordinate from min value from band 0.<br/>
     * <var>max<sub>0</sub></var>  : max from band 0.<br/>
     * <var>maxX<sub>0</sub></var> : x coordinate from max value from band 0.<br/>
     * <var>maxY<sub>0</sub></var> : y coordinate from max value from band 0.<br/>
     * <var>min<sub>n</sub></var>  : min from nth band.<br/>
     * <var>minX<sub>n</sub></var> : x coordinate from min value from nth band.<br/>
     * <var>minY<sub>n</sub></var> : y coordinate from min value from nth band.<br/>
     * <var>max<sub>n</sub></var>  : max from nth band.<br/>
     * <var>maxX<sub>n</sub></var> : x coordinate from max value from nth band.<br/>
     * <var>maxY<sub>n</sub></var> : y coordinate from max value from nth band.<br/><br/>
     * Table is organize like this : <br/>
     * [<var>min<sub>0</sub></var>, <var>minX<sub>0</sub></var>, <var>minY<sub>0</sub></var>,
     * <var>max<sub>0</sub></var>, <var>maxX<sub>0</sub></var>, <var>maxY<sub>0</sub></var>
     * ...
     * <var>min<sub>n</sub></var>, <var>minX<sub>n</sub></var>, <var>minY<sub>n</sub></var>,
     * <var>max<sub>n</sub></var>, <var>maxX<sub>n</sub></var>, <var>maxY<sub>n</sub></var>]<br/><br/>
     *
     * If Rectangle area parameter is {@code null} method will search minimum
     * and maximum on all iterate object.</p>
     *
     * @param area area within search min and max values.
     * @return double array witch represent minimum and maximum pixels values for each band.
     */
    public abstract double[] getMinMaxValue(Rectangle area);

    /**
     * <p>Return side of Interpolation area.<br/>
     * For example Bilinear interpolation compute from 4 pixel values.<br/>
     * Its interpolation area is a square of side 2.<p>
     *
     * @return side of interpolation area.
     */
    abstract int getWindowSide();

    /**
     * Verify coordinates are within iterate area boundary.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @throws IllegalArgumentException if pixel coordinates are out of iterate area boundary.
     */
    protected void checkInterpolate(double x, double y) {
        final int mx = boundary.x;
        final int my = boundary.y;
        if (x < mx || x >= mx + boundary.width || y < my || y >= my + boundary.height)
            throw new IllegalArgumentException("coordinates out of iterate area boundary : "+boundary);
    }

    /**
     * Returns {@code Rectangle} which is Image or Raster boundary within this Interpolator.
     *
     * @return {@code Rectangle} which is Image or Raster boundary within this Interpolator.
     */
    Rectangle getBoundary() {
        return boundary;
    }

    /**
     * Return number of bands from object that iterate.
     *
     * @return number of bands from object that iterate.
     */
    public int getNumBands(){
        return numBands;
    }

    /**
     * <p>Return Interpolation object.<br/><br/>
     *
     * Note : if lanczos interpolation doesn't choose lanczosWindow parameter has no impact.</p>
     *
     * @param pixelIterator Iterator which iterate to compute interpolation.
     * @param interpolationCase case of interpolation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @return interpolation asked by caller.
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int)
     */
    public static Interpolation create(PixelIterator pixelIterator, InterpolationCase interpolationCase, int lanczosWindow){
        switch (interpolationCase) {
            case NEIGHBOR : return new NeighborInterpolation(pixelIterator);
            case BILINEAR : return new BilinearInterpolation(pixelIterator);
            case BICUBIC  : return new BiCubicInterpolation1(pixelIterator);
            case BICUBIC2 : return new BiCubicInterpolation2(pixelIterator);
            case LANCZOS  : return new LanczosInterpolation(pixelIterator, lanczosWindow);
            default       : throw  new IllegalArgumentException("interpolation not supported yet");
        }
    }
}
