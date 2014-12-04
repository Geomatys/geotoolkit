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
import java.util.Arrays;
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
     * Interpolation results table.
     */
    protected final double[] result;
    
    /**
     * Contains value use when pixel transformation is out of source image boundary.
     */
    protected final double[] fillValue; 
    
    /**
     * Define comportement of the destination image border. 
     */
    protected final ResampleBorderComportement borderChoice;
    
    /**
     * Define boundary which accept extrapolation.
     */
    protected final double bminX, bminY, bmaxX, bmaxY;
    
    /**
     * Define boundary which don't accept extrapolation.
     */
    protected final int boundMinX, boundMinY, boundMaxX, boundMaxY;
    
    /**
     * Build an Interpolation object initialize by the given parameter.
     * 
     * @param pixelIterator {@link PixelIterator} which travel source image use to interpolate.
     * @param windowSize define width and height from needed area to interpolate value in function of interpolation instance. 
     * @param borderChoice define comportement of the destination image border. 
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @see BilinearInterpolation#BilinearInterpolation(org.geotoolkit.image.iterator.PixelIterator) 
     * @see BiCubicInterpolation#BiCubicInterpolation(org.geotoolkit.image.iterator.PixelIterator) 
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int) 
     */
    public Interpolation(PixelIterator pixelIterator, int windowSize, ResampleBorderComportement borderChoice, double[] fillValue) {
        this.pixelIterator = pixelIterator;
        this.numBands   = pixelIterator.getNumBands();
        this.boundary   = pixelIterator.getBoundary(false);
        if (windowSize > boundary.width || windowSize > boundary.height)
            throw new IllegalArgumentException("windowSide argument is more "
                    + "larger than iterate object boundary side. boundary = "
                    +boundary+" windowSide = "+windowSize);
        
        bminX = boundary.x - 0.5; 
        bminY = boundary.y - 0.5;
        bmaxX = boundary.x + boundary.width - 0.5;
        bmaxY = boundary.y + boundary.height - 0.5;
        
        boundMinX = boundary.x; 
        boundMinY = boundary.y;
        boundMaxX = boundary.x + boundary.width  - 1;
        boundMaxY = boundary.y + boundary.height - 1;
        
        this.minMax     = null;
        this.windowSide = windowSize;
        this.data       = new double[windowSize * windowSize * numBands];
        result          = new double[numBands];
        this.borderChoice = borderChoice;
        if (fillValue == null) {
            this.fillValue = new double[numBands];
            Arrays.fill(this.fillValue, Double.NaN);
        } else {
            this.fillValue = fillValue;
        }
    }

    public Interpolation(Interpolation source) {
        this.pixelIterator = source.pixelIterator;
        this.numBands   = source.getNumBands();
        this.boundary   = source.getBoundary();
        bminX = boundary.x - 0.5; 
        bminY = boundary.y - 0.5;
        bmaxX = boundary.x + boundary.width - 0.5;
        bmaxY = boundary.y + boundary.height - 0.5;
        boundMinX = boundary.x; 
        boundMinY = boundary.y;
        boundMaxX = boundary.x + boundary.width  - 1;
        boundMaxY = boundary.y + boundary.height - 1;
        
        this.minMax     = source.minMax;
        this.windowSide = source.windowSide;
        this.data       = new double[windowSide * windowSide * numBands];
        result          = new double[numBands];
        this.fillValue  = source.fillValue;
        this.borderChoice = source.borderChoice;
    }

    /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @param band index of band where sample are interpolate.
     * @return interpolate value from x, y pixel coordinate.
     */
    public abstract double interpolate(double x, double y, int band);

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
     * Note : This code is adapted for BiLinear and Neighbor interpolation.
     * About another interpolation min and max values method isn't implemented yet.
     *
     * @param area area within search min and max values.
     * @return double array witch represent minimum and maximum pixels values for each band.
     */
    public double[] getMinMaxValue(Rectangle area) {
        if (minMax != null) {
            if ((area == null && precMinMax.equals(boundary))
              || area.equals(precMinMax)) return minMax;
        }
        
        //-- compute minMax values
        minMax = new double[6 * numBands];
        
        //-- initialize min and max value for each band 
        for (int band = 0;band<numBands; band++) {
            final int minBandOrdinate = 6 * band;
            //-- min value, x, y coordinates
            minMax[minBandOrdinate]     = Double.POSITIVE_INFINITY;
            //-- max value, x, y coordinates
            minMax[minBandOrdinate + 3] = Double.NEGATIVE_INFINITY;
        }
        
        /*
         * If area is null iterate on all image area.
         */
        if (area == null) {
            int band = 0;
            double value;
            while(pixelIterator.next()) {
                value = pixelIterator.getSampleDouble();
                        final int minBandOrdinate = 6 * band;
                        if (value < minMax[minBandOrdinate]) {
                            //-- min value, x, y coordinates
                            minMax[minBandOrdinate] = value;
                            minMax[minBandOrdinate + 1] = pixelIterator.getX();
                            minMax[minBandOrdinate + 2] = pixelIterator.getY();
                        }
                        if (value > minMax[minBandOrdinate + 3]) {
                            //-- max value, x, y coordinates
                            minMax[minBandOrdinate + 3] = value;
                            minMax[minBandOrdinate + 4] = pixelIterator.getX();
                            minMax[minBandOrdinate + 5] = pixelIterator.getY();
                        }
                if (++band >= numBands) {
                    band = 0;
                }
            }
        } else if (!getBoundary().contains(area)) {
                throw new IllegalArgumentException("impossible to define min and max in area out of Iterate object boundary");
        } else {
            double value;
            final int maxAreaX = area.x + area.width;
            final int maxAreaY = area.y + area.height;
            for (int y = area.y; y < maxAreaY; y++) {
                for (int x = area.x; x < maxAreaX; x++) {
                    /*
                     * Call moveTo at each pixel coordinates because we don't know Iterator implementation.
                     * Iterate row by row or raster by raster has different comportements.
                     */
                    pixelIterator.moveTo(x, y, 0);
                    for (int band = 0; band < numBands; band++) {
                        value = pixelIterator.getSampleDouble();
                        final int minBandOrdinate = 6 * band;
                        if (value < minMax[minBandOrdinate]) {
                            //-- min value, x, y coordinates
                            minMax[minBandOrdinate] = value;
                            minMax[minBandOrdinate + 1] = x;
                            minMax[minBandOrdinate + 2] = y;
                        }
                        if (value > minMax[minBandOrdinate + 3]) {
                            //-- max value, x, y coordinates
                            minMax[minBandOrdinate + 3] = value;
                            minMax[minBandOrdinate + 4] = x;
                            minMax[minBandOrdinate + 5] = y;
                        }
                        pixelIterator.next();
                    }
                }
            }
        }
        precMinMax = (area == null) ? boundary : area;
        return minMax;
    }
    
    /**
     * Verify coordinates are within iterate area boundary.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return {@code true} if value should be interpolate at the given coordinates else {@code false}.
     * @throws IllegalArgumentException if pixel coordinates are out of iterate area boundary.
     */
     protected boolean checkInterpolate(double x, double y) {
        //-- accept extrapolation
        if (borderChoice.equals(ResampleBorderComportement.EXTRAPOLATION)) return true;
        if (x < bminX || x > bmaxX || y < bminY || y > bmaxY) return false; //-- no interpolation available 
//        if (borderChoice.equals(ResampleBorderComportement.EXTRAPOLATION)) return true;
        return (!(x < boundMinX || x > boundMaxX
               || y < boundMinY || y > boundMaxY));
    }

    /**
     * Return appropriate interpolation minX and minY coordinates from x, y interpolate coordinates.
     * MinX and minY represent lower corner coordinates from interpolation area.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @throws IllegalArgumentException if there are necessary pixels out of boundary.
     */
    protected void setInterpolateMin(double x, double y) {
        final int boundW = boundary.width;
        final int boundH = boundary.height;
        final int bx     = boundary.x;
        final int by     = boundary.y;
        minX  = (int) x;
        minY  = (int) y;

        //-- Adjust truncation.
        if (x < minX) minX--;
        if (y < minY) minY--;

        //-- Adjust area interpolation on x, y center.
        minX -= (windowSide >>> 1) - 1;
        minY -= (windowSide >>> 1) - 1;

        //-- Adjust area from lower corner.
        minX = Math.max(minX, bx);
        minY = Math.max(minY, by);

        //-- Adjust area from upper corner.
        final int maxDiffX = minX + windowSide - (bx + boundW);
        final int maxDiffY = minY + windowSide - (by + boundH);
        if (maxDiffX > 0) minX -= maxDiffX;
        if (maxDiffY > 0) minY -= maxDiffY;
    }

    /**
     * Returns {@code Rectangle} which is Image or Raster boundary within this Interpolator.
     *
     * @return {@code Rectangle} which is Image or Raster boundary within this Interpolator.
     * @see Resample#getSourcePixelValue(double, double)
     */
    Rectangle getBoundary() {
        return boundary;
    }

    /**
     * Return number of bands from object that iterate.
     *
     * @return number of bands from object that iterate.
     * @see Resample#getSourcePixelValue(double, double)
     */
    public int getNumBands() {
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
    public static Interpolation create(PixelIterator pixelIterator, InterpolationCase interpolationCase, int lanczosWindow) {
        return create(pixelIterator, interpolationCase, lanczosWindow, ResampleBorderComportement.FILL_VALUE, null);
    }
    
    /**
     * <p>Return Interpolation object.<br/><br/>
     *
     * Note : if lanczos interpolation doesn't choose lanczosWindow parameter has no impact.</p>
     *
     * @param pixelIterator Iterator which iterate to compute interpolation.
     * @param interpolationCase case of interpolation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @param rbc comportement of the destination image border. 
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @return interpolation asked by caller.
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int)
     */
    public static Interpolation create(PixelIterator pixelIterator, InterpolationCase interpolationCase, int lanczosWindow, 
                                       ResampleBorderComportement rbc, double[] fillValue) {
        switch (interpolationCase) {
            case NEIGHBOR : return new NeighborInterpolation(pixelIterator, fillValue);
            case BILINEAR : return new BilinearInterpolation(pixelIterator, rbc, fillValue);
            case BICUBIC  : return new BiCubicInterpolation1(pixelIterator, rbc, fillValue);
            case BICUBIC2 : return new BiCubicInterpolation2(pixelIterator, rbc, fillValue);
            case LANCZOS  : return new LanczosInterpolation(pixelIterator, lanczosWindow, rbc, fillValue);
            default       : throw  new IllegalArgumentException("interpolation not supported yet");
        }
    }
}
