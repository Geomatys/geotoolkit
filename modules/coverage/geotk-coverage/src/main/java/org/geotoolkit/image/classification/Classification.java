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
package org.geotoolkit.image.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.util.ArgumentChecks;

/**
 * <p>Define and compute two sort of data classifications.<br/>
 * Quantile classification.<br/>
 * Quantile classification is the most basic classification.<br/>
 * Algorithm divide in some equal parts(at best, if its possible) all ascending organized data.<br/><br/>
 * Jenks classification.<br/>
 * Jenks method is the most effective, but also most costly in computing terms.<br/>
 * For each case, in first time,  the algorithm computes the "intra-class variance"
 * ie the average of the variances of each of the classes.<br/>
 * A second step, consist to calculates the "inter-class variance",
 * ie the variance of each of the generated classes.<br/>
 * The aim is thus to minimize the "intra-class variance" so that each
 * elements group has generated individuals who "look at best"
 * and maximize the "inter-class variance" in order to obtain the most dissimilar classes possible.</p>
 *
 * @author Rémi Marechal (Geomatys).
 */
public class Classification {

    /**
     * data will be classified.
     */
    private final double[] data;

    /**
     * Number of class which fragment data.
     */
    private final int classNumber;

    /**
     * List will be contain classification result.
     */
    private final List<double[]> classList;

    /**
     * Data value number.
     */
    private final int dataLength;

    /**
     * <p>Define and compute two sort of data classifications.<br/>
     * Quantile classification.<br/>
     * Jenks classification.<br/><br/>
     *
     * Note : if "classNumber" parameter equal 1, the 2 classification made add
     * in result list only one ascending order class.</p>
     *
     * @param data table will be classified.
     * @param classNumber class number.
     */
    public Classification(double[] data, int classNumber) {
        ArgumentChecks.ensureNonNull("data table", data);
        if (classNumber < 1)
            throw new IllegalArgumentException("impossible to classify datas with"
                + " class number lesser 1");
        if (classNumber > data.length)
            throw new IllegalArgumentException("impossible to classify datas"
                + " with class number larger than overall elements number");
        this.data = data;
        this.classNumber = classNumber;
        this.classList = new ArrayList<double[]>(classNumber);
        this.dataLength = data.length;
    }

    /**
     * Class data from quantile method.
     */
    public void computeQuantile() {
        Arrays.sort(data);
        if (classNumber == 1) {
            classList.add(data);
            return;
        }
        int lowLimit = 0;
        int highLimit, comp, l, j;
        double[] result;
        for (int i = 1; i<=classNumber; i++) {
            highLimit = (int) Math.round(i*((double)dataLength)/classNumber);
            l = highLimit-lowLimit;
            comp = 0;
            result = new double[l];
            for (j = lowLimit; j<highLimit; j++) {
                result[comp++] = data[j];
            }
            lowLimit +=l;
            classList.add(result);
        }
    }

    /**
     * Class data from Jenks method.
     */
    public void computeJenks() {
        Arrays.sort(data);
        if (classNumber == 1) {
            classList.add(data);
            return;
        }
        int[] finalSequenceKept = null;
        final int[] jSequence = new int[classNumber];
        final JenkSequence jSeq = new JenkSequence(jSequence, dataLength);
        int max, len, min;
        double moy, currentVar, currentVariance, varianceIntraClass, varianceInterClass;
        double[] average  = new double[classNumber];
        double[] variance = new double[classNumber];
        double diff = 0;
        //for each classes possibilities.
        while (jSeq.next()) {
            min = 0;
            //for each sequence index.
            for (int i = 0; i<classNumber; i++) {
                max = jSequence[i];
                len = max - min;
                moy = 0;
                currentVariance = 0;
                //average computing.
                for (int j = min; j<max;j++) {
                    moy+=data[j];
                }
                moy/=len;
                average[i] = moy;
                //variance computing.
                for (int j = min; j<max;j++) {
                    currentVar = data[j] - moy;
                    currentVar *= currentVar;
                    currentVariance += currentVar;
                }
                variance[i] = currentVariance/len;
                //next table begin index.
                min = max;
            }
            /**
             * Average of classes variances.
             * Named SDBC or "intra classes variances".
             */
            varianceIntraClass = getAverage(variance);
            /**
             * Variance of classes averages.
             * Named SDAM or "inter classes variance".
             */
            varianceInterClass = getVariance(average);
            if (finalSequenceKept == null) {
                finalSequenceKept = jSequence.clone();
                diff = (varianceInterClass - varianceIntraClass)/varianceInterClass;
            } else {
                if (diff < (varianceInterClass - varianceIntraClass)/varianceInterClass) {
                    finalSequenceKept = jSequence.clone();
                    diff = (varianceInterClass - varianceIntraClass)/varianceInterClass;
                }
            }
        }
        min = 0;
        int compteur;
        double[] result;
        for (int i = 0; i<classNumber; i++) {
            max = finalSequenceKept[i];
            len = max - min;
            compteur = 0;
            result = new double[len];
            for (int j = min; j<max; j++) {
                result[compteur++] = data[j];
            }
            classList.add(result);
            min = max;
        }
    }

    /**
     * Return classification result.
     * @return classification result.
     */
    public List<double[]> getClasses() {
        return classList;
    }

    /**
     * Return variance from double table elements.
     *
     * @param values table which contain value to compute variance.
     * @return variance of double elements.
     */
    double getVariance(double[] values) {
        assert (values != null) : "variance values table is null";
        final int length = values.length;
        double moy = 0;
        double var;
        double variance = 0;
        for (int i = 0; i<length; i++) {
            moy += values[i];
        }
        moy /= length;
        for (int i = 0; i<length; i++) {
            var = values[i]-moy;
            var *= var;
            variance += var;
        }
        return variance /= length;
    }

    /**
     * Return average from double table elements.
     *
     * @param values table which contain value to compute average.
     * @return average from double table elements.
     */
    private double getAverage(double[] values) {
        assert (values != null) : "average values table is null";
        final int length = values.length;
        double result = 0;
        for (int i = 0; i<length; i++) {
            result += values[i];
        }
        return result /= length;
    }
}
