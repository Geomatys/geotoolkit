/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
     * Table to keep all pixels values used to interpolate.
     */
    protected final double[] data;

    /**
     * Lower corner X coordinates from interpolation area.
     */
    protected int minX;

    /**
     * Lower corner Y coordinates from interpolation area.
     */
    protected int minY;

    /**
     * Side of area interpolation.
     */
    protected final int windowSide;

    /**
     * Build an Interpolate object.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public Interpolation(PixelIterator pixelIterator, int windowSide) {
        this.pixelIterator = pixelIterator;
        this.numBands = pixelIterator.getNumBands();
        this.boundary = pixelIterator.getBoundary();
        this.minMax = null;
        this.windowSide = windowSide;
        this.data = new double[windowSide * windowSide * numBands];
    }

    /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return interpolate value from x, y pixel coordinate.
     */
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int[] mins = getInterpolateMin(x, y, windowSide, windowSide);
        minX = mins[0];
        minY = mins[1];
        int compteur = 0;
        int band;
        for (int dy = minY; dy < minY + windowSide; dy++) {
            for (int dx = minX; dx < minX + windowSide; dx++) {
                pixelIterator.moveTo(dx, dy);
                band = 0;
                while (band++ != numBands) {
                    pixelIterator.next();
                    data[compteur++] = pixelIterator.getSampleDouble();
                }
            }
        }
        return null;
    }

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
    public double[] getMinMaxValue(Rectangle area) {
        if (minMax != null) {
            if ((area == null && precMinMax == null)
              || area.equals(precMinMax)) return minMax;
        }
        //compute minMax values
        minMax = new double[6*numBands];
        int band = 0;
        double value;
        if (area == null) {//iterate on all image
            pixelIterator.rewind();
            //first iteration
            for (;band<numBands; band++) {
                pixelIterator.next();
                value = pixelIterator.getSampleDouble();
                //min value, x, y coordinates
                minMax[6*band]     = value;
                minMax[6*band + 1] = pixelIterator.getX();
                minMax[6*band + 2] = pixelIterator.getX();
                //max value, x, y coordinates
                minMax[6*band + 3]     = value;
                minMax[6*band + 4] = pixelIterator.getX();
                minMax[6*band + 5] = pixelIterator.getX();
            }
            band = 0;
            while (pixelIterator.next()) {
                value = pixelIterator.getSampleDouble();
                if (value < minMax[6*band]) {
                    //min value, x, y coordinates
                    minMax[6*band]     = value;
                    minMax[6*band + 1] = pixelIterator.getX();
                    minMax[6*band + 2] = pixelIterator.getX();
                }
                if (value > minMax[6*band + 3]) {
                    //max value, x, y coordinates
                    minMax[6*band + 3] = value;
                    minMax[6*band + 4] = pixelIterator.getX();
                    minMax[6*band + 5] = pixelIterator.getX();
                }
                if (++band == numBands) band = 0;
            }
        } else {//iterate within rectangle.
            if (!getBoundary().contains(area))
                throw new IllegalArgumentException("impossible to define min and max in area out of Iterate object boundary");
            pixelIterator.moveTo(area.x, area.y);
            for (;band<numBands; band++) {
                pixelIterator.next();
                value = pixelIterator.getSampleDouble();
                //min value, x, y coordinates
                minMax[6*band]     = value;
                minMax[6*band + 1] = pixelIterator.getX();
                minMax[6*band + 2] = pixelIterator.getX();
                //max value, x, y coordinates
                minMax[6*band + 3] = value;
                minMax[6*band + 4] = pixelIterator.getX();
                minMax[6*band + 5] = pixelIterator.getX();
            }
            band = 0;
            for (int y = area.y; y<area.y + area.height; y++) {
                for (int x = area.x; x<area.x + area.width; x++) {
                    pixelIterator.moveTo(x, y);
                    for (;band<numBands; band++) {
                        pixelIterator.next();
                        value = pixelIterator.getSampleDouble();
                        if (value < minMax[6*band]) {
                            //min value, x, y coordinates
                            minMax[6*band]     = value;
                            minMax[6*band + 1] = pixelIterator.getX();
                            minMax[6*band + 2] = pixelIterator.getX();
                        }
                        if (value > minMax[6*band + 3]) {
                            //max value, x, y coordinates
                            minMax[6*band + 3] = value;
                            minMax[6*band + 4] = pixelIterator.getX();
                            minMax[6*band + 5] = pixelIterator.getX();
                        }
                    }
                }
            }
        }
        precMinMax = area;
        return minMax;
    }

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
     * Return appropriate interpolation minX and minY coordinates from x, y interpolate coordinates.
     * MinX and minY represent lower corner coordinates from interpolation area.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @param width interpolate area width.
     * @param height interpolate area height.
     * @throws IllegalArgumentException if there are necessary pixels out of boundary.
     * @return appropriate interpolation minX and minY coordinates.
     */
    protected int[] getInterpolateMin(double x, double y, int width, int height) {
        final int boundW = boundary.width;
        final int boundH = boundary.height;
        final int bx = boundary.x;
        final int by = boundary.y;
        assert (width <= boundW && height <= boundH) : "area dimensions are out of boundary";
        int minx = (int) x;
        int miny = (int) y;
        if (x<minx) minx--;
        if (y<miny) miny--;

        //ajust area interpolation on x, y center.
        for (int i = 0; i<width/2-1;i++) {
            minx--;
        }
        for (int i = 0; i<height/2-1;i++) {
            miny--;
        }
        minx = Math.max(minx, bx);
        miny = Math.max(miny, by);
        while(minx+width > bx+boundW) {
            minx--;
        }
        while(miny+height > by+boundH) {
            miny--;
        }
        return new int[]{minx, miny};
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
