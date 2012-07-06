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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define BiCubic Interpolation.
 *
 * BiCubic interpolation is computed from 16 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BiCubicInterpolation extends Interpolation {

    /**
     * Table to keep all 16 pixels values used to interpolate.
     */
    private final double[] data;

    /**
     * Table used to compute interpolation from rows values.
     */
    private final double[] tabInteRow;

    /**
     * Table used to interpolate values from rows interpolation result.
     */
    private final double[] tabInteCol;

    /**
     * Create an BiCubic Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator);
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller"+boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller"+boundary.height);
        data       = new double[16*numBands];
        tabInteRow = new double[4];
        tabInteCol = new double[4];
    }

    /**
     * Compute biCubic interpolation.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return pixel interpolated values for each bands.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int[] deb = getInterpolateMin(x, y, 4, 4);
        int debX = deb[0];
        int debY = deb[1];
        int compteur = 0;
        int bands;
        final double[] result = new double[numBands];
        for (int idY = debY; idY < debY + 4; idY++) {
            for (int idX = debX; idX < debX + 4; idX++) {
                pixelIterator.moveTo(idX, idY);
                bands = 0;
                while (bands++ != numBands) {
                    pixelIterator.next();
                    data[compteur++] = pixelIterator.getSampleDouble();
                }
            }
        }
        //build pixels interpolation band per band
        for (int n = 0; n < numBands; n++) {
            //16 values for each interpolation per band
            for (int idRow = 0; idRow<4; idRow++) {
                for (int idC = 0; idC<4;idC++) {
                    tabInteRow[idC] = data[n + (4*idRow + idC) * numBands];
                }
                tabInteCol[idRow] = getCubicValue(debX, x, tabInteRow);
            }
            result[n] = getCubicValue(debY, y, tabInteCol);
        }
        return result;
    }

    /**
     * <p>Find polynomials roots from BiCubic interpolation.<br/><br/>
     *
     * note : return null if : - delta (discriminant)&lt;0<br/>
     *                         - roots found are out of definition domain.</p>
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param minDf minimum of definition domain.
     * @param maxDf maximum of definition domain.
     * @param f pixel values from t = {0, 1, 2, 3}.
     * @return polynomial root(s).
     */
    double[] getCubicRoots(double t0, double minDf, double maxDf, double...f) {
        assert (f.length == 4) : "impossible to interpolate with less or more than 4 values";
        assert (minDf < maxDf) : "definition domain invalid";
        //f(t) = 3a3t*t + 2a2t + a1;
        final double a1 =  f[3]/3 - 3*f[2]/2 + 3*f[1]   - 11*f[0]/6;
        final double a2 = -f[3]/2 + 2*f[2]   - 5*f[1]/2 + f[0];
        final double a3 =  f[3]/6 - f[2]/2   + f[1]/2   - f[0]/6;///////cas ou a3 = 0
        final double delta = 4 * (a2*a2 - 3*a3*a1);

//        final double q = -0.5*(2*a2+Math.signum(a2)*delta);
//        final double xt1 = q/(3*a3);
//        final double xt2 = a1/q;
        double x, x2;
        if (Math.abs(a3) <= 1E-12) {
            x = -a1/a2/2;
            x += t0;
            if(x >= minDf && x <= maxDf) return new double[]{x};
            return null;
        }
        if (Math.abs(delta) <= 1E-12) {
            x = -a2/a3/3 + t0;
            if (x >= minDf && x <= maxDf) return new double[]{x};
            return null;
        } else if (delta > 0) {
            x  = -(2*a2 + Math.sqrt(delta)) / a3 / 6;
            x2 = (-2*a2 + Math.sqrt(delta)) / a3 / 6;
            x  += t0;
            x2 += t0;
            if (x >= minDf && x <= maxDf) {
                if (x2 >= minDf && x2 <= maxDf) return new double[]{x, x2};
                return new double[]{x};
            } else {
                if (x2 >= minDf && x2 <= maxDf) return new double[]{x2};
                return null;
            }
        } else {
            return null;
        }
    }

//    /**
//     * <p>Find polynomials roots from BiCubic interpolation.<br/><br/>
//     *
//     * note : return null if : - delta (discriminant)&lt;0<br/>
//     *                         - roots found are out of definition domain.</p>
//     *
//     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
//     * @param minDf minimum of definition domain.
//     * @param maxDf maximum of definition domain.
//     * @param f pixel values from t = {0, 1, 2, 3}.
//     * @return polynomial root(s).
//     */
//    double[] getCubicRoots(double t0, double minDf, double maxDf, double...f) {
//        assert (f.length == 4) : "impossible to interpolate with less or more than 4 values";
//        assert (minDf < maxDf) : "definition domain invalid";
//        //f(t) = 3a3t*t + 2a2t + a1;
//        final double a1 =  f[3]/3 - 3*f[2]/2 + 3*f[1]   - 11*f[0]/6;
//        final double a2 = -f[3]/2 + 2*f[2]   - 5*f[1]/2 + f[0];
//        final double a3 =  f[3]/6 - f[2]/2   + f[1]/2   - f[0]/6;
//        final double delta = 4 * (a2*a2 - 3*a3*a1);
//
//        final double q = -0.5*(2*a2+Math.signum(a2)*Math.sqrt(delta));
//        final double xt1 = q/(3*a3) + t0;
//        final double xt2 = a1/q + t0;
//        if (xt1>=minDf && xt1<=maxDf) {
//            if (xt2>=minDf && xt2<=maxDf) return new double[]{xt1,xt2};
//            return new double[]{xt1};
//        } else if (xt2>=minDf && xt2<=maxDf) {
//            return new double[]{xt2};
//        } else {
//            return null;
//        }
//    }


//    /**
//     * <p>Find min and max values from interpolation.<br/>
//     * Min and max value are find from a square with side length equal 4 necessary to compute biCubic interpolation.</p>
//     *
//     * @param minX X coordinate of biCubic interpolation area lower corner.
//     * @param minY Y coordinate of biCubic interpolation area lower corner.
//     * @return min and max value find in biCubic interpolation area.
//     */
//    double[] getMinMaxFromSubArea(int minX, int minY) {
//        assert(minX >= boundary.x) : "X ordinate out of image boundary";
//        assert(minY >= boundary.y) : "Y ordinate out of image boundary";
//        assert(minX <= boundary.x + boundary.width - 4)  : "";
//        assert(minY <= boundary.y + boundary.height - 4) : "";
//        int band;
//        double[] allValues = new double[16 * numBands];
//        int compteur = 0;
//        double currentValue;
//        double[] minAndMax = new double[2*numBands];
//        //fill min and max from first iteration.
//        pixelIterator.moveTo(minX, minY);
//        for (band = 0; band<numBands; band++) {
//            pixelIterator.next();
//            minAndMax[2*band] = minAndMax[2*band+1] = pixelIterator.getSampleDouble();
//        }
//        for (int y = minY; y<minY + 4; y++) {
//            for (int x = minX; x<minX + 4; x++) {
//                pixelIterator.moveTo(x, y);
//                for (band = 0; band<numBands; band++) {
//                    pixelIterator.next();
//                    currentValue = pixelIterator.getSampleDouble();
//                    allValues[compteur++] = currentValue;
//                    //study min and max at pixel position.
//                    if (currentValue < minAndMax[2*band])   minAndMax[2*band]   = currentValue;//ajoute les ccordonnées x et y
//                    if (currentValue > minAndMax[2*band+1]) minAndMax[2*band+1] = currentValue;
//                }
//            }
//        }
//        //on a les min et max au position entière
//        band = 0;
//        double[] minT;
//        double[][] valBand = new double[4][4];
//        for (;band < numBands; band++) {//remplir directement un tableau 2d
//            for (int j = 0; j<4; j++) {
//                for (int i = 0; i<4; i++) {
//                    valBand[j][i] = allValues[(4*j + i) * numBands + band];
//                }
//            }
//            minT = findMinMax(valBand, true, minX, minY);
//            if (minAndMax[2*band]   > minT[0]) minAndMax[2*band]   = minT[0];
//            if (minAndMax[2*band+1] < minT[1]) minAndMax[2*band+1] = minT[1];
//            minT = findMinMax(valBand, false, minX, minY);
//            if (minAndMax[2*band]   > minT[0]) minAndMax[2*band]   = minT[0];
//            if (minAndMax[2*band+1] < minT[1]) minAndMax[2*band+1] = minT[1];
//        }
//        return minAndMax;
//    }

    /**
     * <p>Find min and max values from interpolation.<br/>
     * Min and max value are find from a square with side length equal 4 necessary to compute biCubic interpolation.</p>
     *
     * @param minX X coordinate of biCubic interpolation area lower corner.
     * @param minY Y coordinate of biCubic interpolation area lower corner.
     * @return min and max value find in biCubic interpolation area.
     */
    double[] getMinMaxFromSubArea(int minX, int minY) {
        assert(minX >= boundary.x) : "X ordinate out of image boundary";
        assert(minY >= boundary.y) : "Y ordinate out of image boundary";
        assert(minX <= boundary.x + boundary.width - 4)  : "";
        assert(minY <= boundary.y + boundary.height - 4) : "";
        int band;
        double[] allValues = new double[16 * numBands];
        int compteur = 0;
        double currentValue;
        double[] minAndMax = new double[6*numBands];
        //fill min and max from first iteration.
        pixelIterator.moveTo(minX, minY);
        for (band = 0; band<numBands; band++) {
            pixelIterator.next();
            minAndMax[6*band] = minAndMax[6*band+3] = pixelIterator.getSampleDouble();
        }
        for (int y = minY; y<minY + 4; y++) {
            for (int x = minX; x<minX + 4; x++) {
                pixelIterator.moveTo(x, y);
                for (band = 0; band<numBands; band++) {
                    pixelIterator.next();
                    currentValue = pixelIterator.getSampleDouble();
                    allValues[compteur++] = currentValue;
                    //study min and max at pixel position.
                    if (currentValue < minAndMax[6*band]) {
                        minAndMax[6*band]     = currentValue;
                        minAndMax[6*band + 1] = x;
                        minAndMax[6*band + 2] = y;
                    }else if (currentValue > minAndMax[6*band+3]) {
                        minAndMax[6*band + 3] = currentValue;
                        minAndMax[6*band + 4] = x;
                        minAndMax[6*band + 5] = y;
                    }
                }
            }
        }
        //on a les min et max au position entière
        band = 0;
        double[] minT;
        double[][] valBand = new double[4][4];
        for (;band < numBands; band++) {//remplir directement un tableau 2d
            for (int j = 0; j<4; j++) {
                for (int i = 0; i<4; i++) {
                    valBand[j][i] = allValues[(4*j + i) * numBands + band];
                }
            }
            minT = findMinMax(valBand, true, minX, minY);
            if (minAndMax[6*band]   > minT[0]) {
                minAndMax[6*band]   = minT[0];
                minAndMax[6*band + 1]   = minT[1];
                minAndMax[6*band + 2]   = minT[2];
            } else if (minAndMax[6*band+3] < minT[3]) {
                minAndMax[6*band+3] = minT[3];
                minAndMax[6*band+4] = minT[4];
                minAndMax[6*band+5] = minT[5];
            }
            minT = findMinMax(valBand, false, minX, minY);
            if (minAndMax[6*band]   > minT[0]) {
                minAndMax[6*band]   = minT[0];
                minAndMax[6*band + 1]   = minT[1];
                minAndMax[6*band + 2]   = minT[2];
            } else if (minAndMax[6*band+3] < minT[3]) {
                minAndMax[6*band+3] = minT[3];
                minAndMax[6*band+4] = minT[4];
                minAndMax[6*band+5] = minT[5];
            }
        }
        return minAndMax;
    }

    /**
     * <p>Find min and max value from 16 elements table which represent data to interpolate.<br/><br/>
     *
     * Note : to get min and max value caller MUST call this method 2 time.<br/>
     * One time with boolean at true and next time with boolean at false of vice-versa
     * and get min and max value from this 2 distinct results.<br/>
     * For more use details see {@link #getMinMaxFromSubArea(int, int) }.<br/>
     * Moreover about minT table :<br/>
     * minT[0] = minimum in X direction<br/>
     * minT[1] = minimum in Y direction</p>
     *
     * @param minT contain minimum pixel index in X direction and Y direction.
     * @param biCubicData table of 16 elements which contain biCubic data necessary to interpolate.
     * @param colFlag boolean which define if caller want begin by row or column.
     * @return min and max interpolation value from biCubic data table.
     */
    private double[] findMinMax( double[][] biCubicData, boolean colFlag, int ...minT) {
        assert (biCubicData.length == 4 && biCubicData[0].length == 4) : "impossible to compute bicubic interpolation";
        assert (minT.length == 2) : "exist only 2 minimum";
        final List<Double> rowRoots = new ArrayList<Double>();
        final double[] minMaxResult = new double[6];
        minMaxResult[0] = minMaxResult[3] = Double.NaN;
        final int mx = (colFlag) ? 1 : 0;
        final int my = (colFlag) ? 0 : 1;
        final double[][] bCD;
        double[] roots, mmT;
        int i,j;

        //add control points
        for (int id = 0; id<4; id++) {
            rowRoots.add((double)minT[mx] + id);
        }
        //if caller choose column first
        if (colFlag) {
            bCD = new double[4][4];
            for (int r = 0; r<4; r++) {
                for (int c = 0; c<4; c++) {
                        i = c;
                        j = r;
                    bCD[r][c] = biCubicData[i][j];//invert row and column
                }
            }
        } else {
            bCD = biCubicData;
        }
        for (int r = 0; r<4; r++) {
            roots = getCubicRoots(minT[mx], minT[mx], minT[mx]+3, bCD[r]);
            if (roots != null) {
                for (double root : roots) {
                    rowRoots.add(root);
                }
            }
        }
        for (double root : rowRoots) {
            mmT = getCubicMinMax(minT[my], getCubicValue(minT[mx], root, bCD[0]), getCubicValue(minT[mx], root, bCD[1]),
                    getCubicValue(minT[mx], root, bCD[2]), getCubicValue(minT[mx], root, bCD[3]));
            if (Double.isNaN(minMaxResult[0]) || minMaxResult[0] > mmT[0]) {
                minMaxResult[0] = mmT[0];
                minMaxResult[1+mx] = root;
                minMaxResult[1+my] = mmT[1];
            } else if (Double.isNaN(minMaxResult[3]) || minMaxResult[3] < mmT[2]) {
                minMaxResult[3] = mmT[2];
                minMaxResult[4+mx] = root;
                minMaxResult[4+my] = mmT[3];
            }
        }
        return minMaxResult;
    }

   /**
    * Return min and max values, ie Cubic interpolation at roots position or
    * min and max pixels values.<br/>
    * Always return double table of length 2.
    *
    * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
    * @param f pixel values from t = {0, 1, 2, 3}.
    * @return min and max values if they exist, ie Cubic interpolation at roots position.
    */
    double[] getCubicMinMax(int t0, double...f) {
        assert (f.length == 4) : "getCubicMinMax : impossible to effectuate cubic interpolation from lesser or more than 4 values";
        final double[] extremum = new double[4];
        extremum[0] = extremum[2] = f[0];
        extremum[1] = extremum[3] = t0;
        for (int i = 1; i<4; i++) {
            if (extremum[0] > f[i]){
                extremum[0] = f[i];
                extremum[1] = t0+i;
            } else if (extremum[2] < f[i]) {
                extremum[2] = f[i];
                extremum[3] = t0+i;
            }
        }
//        extremum[0] = Math.min(Math.min(f[0], f[1]), Math.min(f[2], f[3]));
//        extremum[1] = Math.max(Math.max(f[0], f[1]), Math.max(f[2], f[3]));
        double[] roots = getCubicRoots(t0, t0, t0+3, f);
        double val;
        if (roots != null) {
            for (double r : roots) {
                val = getCubicValue(t0, r, f);
                if (val < extremum[0]) {
                    extremum[0] = val;
                    extremum[1] = r;
                }else if (val > extremum[1]) {
                    extremum[2] = val;
                    extremum[3] = r;
                }
            }
        }
        return extremum;
    }

    /**
     * Cubic interpolation from 4 values.<br/>
     * With always t0 &lt= t&lt= t0 + 3 <br/>
     * <p>For example : cubic interpolation between 4 pixels.<br/>
     *
     *
     * &nbsp;&nbsp;&nbsp;t =&nbsp;&nbsp; 0 &nbsp;1 &nbsp;2 &nbsp;3<br/>
     * f(t) = |f0|f1|f2|f3|<br/>
     * In this example t0 = 0.<br/><br/>
     *
     * Another example :<br/>
     * &nbsp;&nbsp;&nbsp;t =&nbsp; -5 -4 -3 -2<br/>
     * f(t) = |f0|f1|f2|f3|<br/>
     * In this example parameter t0 = -5.</p>
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param t position of interpolation.
     * @param f pixel values from t = {0, 1, 2, 3}.
     * @return cubic interpolation at t position.
     */
    double getCubicValue(double t0, double t, double[]f) {
        assert (f.length == 4) : "impossible to interpolate with less or more than 4 values";
        final double a1 =  f[3]/3 - 3*f[2]/2 + 3*f[1]   - 11*f[0]/6;
        final double a2 = -f[3]/2 + 2*f[2]   - 5*f[1]/2 + f[0];
        final double a3 =  f[3]/6 - f[2]/2   + f[1]/2   - f[0]/6;
        final double x  = t-t0;
        return f[0] + a1*x + a2*x*x + a3*x*x*x;
    }

//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public double[] getMinMaxValue(Rectangle area) {
//        if (minMax != null) {
//            if (area == null && precMinMax == null) return minMax;
//            if (area.equals(precMinMax))            return minMax;
//        }
//        //compute minMax values
//        double[] mmT;
//        minMax = new double[2*numBands];
//        final Rectangle iteRect = (area == null) ? getBoundary() : area;
//        if (!getBoundary().contains(iteRect))
//                throw new IllegalArgumentException("impossible to define min and max values within area out of Iterate object boundary"+iteRect);
//        if(iteRect.width < 4 || iteRect.height < 4)
//            throw new IllegalArgumentException("impossible to define min and max values within area which had border side lesser than 4"+iteRect);
//        minMax = getMinMaxFromSubArea(iteRect.x, iteRect.y);
//        for (int y = iteRect.y; y < iteRect.y + iteRect.height - 4; y++) {
//            for (int x = iteRect.x; x < iteRect.x + iteRect.width - 4; x++) {
//
//                mmT = getMinMaxFromSubArea(x, y);//ici je dois recup les coordonnées./////////////////////
//
//                for (int i = 0; i<numBands; i++) {
//                    if (minMax[2*i] > mmT[2*i])     minMax[2*i]   = mmT[2*i];
//                    if (minMax[2*i+1] > mmT[2*i+1]) minMax[2*i+1] = mmT[2*i + 1];
//                }
//            }
//        }
//        precMinMax = area;
//        return minMax;
//    }

    /**
     * {@inheritDoc }.
     * <p>If Rectangle area parameter is {@code null} method will search minimum
     * and maximum on all iterate object.</p>
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        if (minMax != null) {
            if (area == null && precMinMax == null) return minMax;
            if (area.equals(precMinMax))            return minMax;
        }
        //compute minMax values
        double[] mmT;
        minMax = new double[6*numBands];
        final Rectangle iteRect = (area == null) ? getBoundary() : area;
        if (!getBoundary().contains(iteRect))
                throw new IllegalArgumentException("impossible to define min and max values within area out of Iterate object boundary"+iteRect);
        if(iteRect.width < 4 || iteRect.height < 4)
            throw new IllegalArgumentException("impossible to define min and max values within area which had border side lesser than 4"+iteRect);
        minMax = getMinMaxFromSubArea(iteRect.x, iteRect.y);
        for (int y = iteRect.y; y < iteRect.y + iteRect.height - 4; y++) {
            for (int x = iteRect.x; x < iteRect.x + iteRect.width - 4; x++) {

                mmT = getMinMaxFromSubArea(x, y);

                for (int i = 0; i<numBands; i++) {
                    if (minMax[6*i] > mmT[6*i]) {
                        minMax[6*i]   = mmT[6*i];
                        minMax[6*i+1] = mmT[6*i+1];
                        minMax[6*i+2] = mmT[6*i+2];
                    }
                    if (minMax[6*i+3] > mmT[6*i+3]) {
                        minMax[6*i+3] = mmT[6*i+3];
                        minMax[6*i+4] = mmT[6*i+4];
                        minMax[6*i+5] = mmT[6*i+5];
                    }
                }
            }
        }
        precMinMax = area;
        return minMax;
    }
}
