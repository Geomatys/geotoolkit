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
package org.geotoolkit.image.classification;

import java.util.LinkedList;
import java.util.List;
import org.apache.sis.util.ArgumentChecks;

/**
 * <p>Define and compute two sort of data classifications.<br/>
 * Quantile classification.<br/>
 * Quantile classification is the most basic classification.<br/>
 * Algorithm divide in some equal parts(at best, if its possible) all data.<br/><br/>
 * Jenks classification.<br/>
 * Jenks method is the most effective, but also most costly in computing terms.<br/>
 * For each case, in first time,  the algorithm computes the "intra-class variance"
 * ie the average of the variances of each of the classes.<br/>
 * A second step, consist to calculates the "inter-class variance",
 * ie the variance of each of the generated classes.<br/>
 * The aim is thus to minimize the "intra-class variance" so that each
 * elements group has generated individuals who "look at best"
 * and maximize the "inter-class variance" in order to obtain the most dissimilar classes possible.<br/><br/>
 * Data will aren't sort in ascending order.<br/><br/>
 *
 * Code example : <br/><br/>
 *
 * {@code Classification classify = new Classification();}<br/>
 * {@code classify.setData(double[]data);}<br/>
 * {@code classify.setClassNumber(5);}<br/>
 * {@code classify.computeQuantile();}<br/>
 * {@code or}<br/>
 * {@code classify.computeJenks();}<br/>
 *
 * {@code //results in two forms}<br/>
 * {@code classify.getIndex();}<br/>
 * {@code classify.getClasses();}
 * </p>
 *
 * @author Rémi Marechal (Geomatys).
 */
public class Classification {

    /**
     * data will be classified.
     */
    private double[] data = null;

    /**
     * Number of class which fragment data.
     */
    private int classNumber;

    /**
     * List will be contain classification result.
     */
    private final List<double[]> classList;

    /**
     * Data value number.
     */
    private int dataLength;

    /**
     * Begin and ending classes index from {@link #data} table.
     */
    private int[] index = null;

    /**
     * true if re-compute class list from {@link #index} table else false.
     */
    private boolean reComputeList;


    /**
     * Define and compute two sort of data classifications.<br/>
     * Quantile classification.<br/>
     * Jenks classification.
     */
    public Classification() {
        this.classNumber = 1;
        this.classList   = new LinkedList<double[]>();
    }

    /**
     * Class data from quantile method.
     */
    public void computeQuantile() {
        if (data == null)
            throw new IllegalArgumentException("you must set data");
        if (classNumber > dataLength)
            throw new IllegalArgumentException("impossible to classify datas"
                + " with class number larger than overall elements number");
        this.index = new int[classNumber];
        this.reComputeList = true;
        if (classNumber == 1) {
            index[0] = dataLength;
            return;
        }
        for (int i = 1; i<=classNumber; i++)
            index[i-1] = (int) Math.round(i*((double)dataLength)/classNumber);
    }

    /**
     * Class data from Jenks method.
     */
    public void computeJenks() {
        computeJenks(true);
    }

    /**
     * Class data from Jenks method.
     */
    public void computeJenks(boolean checkData) {
        if (data == null)
            throw new IllegalArgumentException("you must set data");
        if (classNumber > dataLength)
            throw new IllegalArgumentException("impossible to classify datas"
                + " with class number larger than overall elements number");
        if (checkData) {
            if(!checkJenksDataValidity()) {
                throw new IllegalArgumentException("not enough distincts datas for the requested number of classes");
            }
        }

        this.index = new int[classNumber];
        this.reComputeList = true;
        if (classNumber == 1) {
            index[0] = dataLength;
            return;
        }

        final int nbCol = classNumber + 1;
        int lenght = dataLength + 1;
        lenght    *= nbCol;
        final int[] indexClassTab = new int[lenght];
        final double[] moyVarTab  = new double[lenght];
        int currentIndex;
        for (int i = 0; i < classNumber; i++) {
            currentIndex = nbCol + i + 1;
            indexClassTab[currentIndex] = 1;
            moyVarTab[currentIndex]     = 0;
            for (int j = 2; j <= dataLength; j++)
                moyVarTab[j*nbCol + i + 1] = Double.POSITIVE_INFINITY;
        }
        double somA, somB, len, currentVal, diff = 0;
        int currentId, idTemp;
        int idl = 1;
        while (idl < dataLength) {
            somA = somB = len = 0;
            int deb = 1;
            while (deb <= idl+1) {
                currentId = idl - deb+1;
                currentVal = data[currentId];
                somB += currentVal * currentVal;
                somA += currentVal;
                len++;
                diff = somB - (somA * somA) / len;
                idTemp = currentId;
                if (idTemp != 0)
                    for (int j = 1; j < classNumber; j++) {
                        currentIndex = (idl+1) * nbCol + j + 1;
                        if (moyVarTab[currentIndex]    >= (diff + moyVarTab[idTemp*nbCol+j])) {
                            indexClassTab[currentIndex] = currentId + 1;
                            moyVarTab[currentIndex]     = diff + moyVarTab[idTemp*nbCol+j];
                        }
                    }
                deb++;
            }
            currentIndex = (idl + 1) * nbCol + 1;
            indexClassTab[currentIndex] = 1;
            moyVarTab[currentIndex]     = diff;
            idl++;
        }
        int idata = dataLength;
        index[classNumber - 1] = dataLength;
        for (int j = classNumber; j >= 2; j--)
            index[j - 2] = idata = indexClassTab[idata * nbCol + j] - 1;
    }

    /**
     * Return classification result.
     *
     * @return classification result.
     */
    public List<double[]> getClasses() {
        if (index == null)
            throw new IllegalStateException("you must call compute method to fill index table");
        if (!reComputeList) return classList;
        int max, len, min = 0;
        double[] result;
        classList.clear();
        for (int i = 0; i<classNumber; i++) {
            max    = index[i];
            len    = max-min;
            result = new double[len];
            System.arraycopy(data, min, result, 0, len);
            classList.add(result);
            min    = max;
        }
        reComputeList = false;
        return classList;
    }

    /**
     * <p>Return classes separation index from {@link #data} table.<br/><br/>
     * for example : caller want class 10 data in 3 distinct class.<br/>
     * first class  second class   third class<br/>
     * &nbsp;&nbsp;[4]&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;&nbsp;[7]&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;&nbsp;[10]<br/>
     * With ending index is exclusive.</p>
     *
     * @return classes separation index from {@link #data} table.
     */
    public int[] getIndex() {
        if (index == null)
            throw new IllegalStateException("you must call compute method to fill index table");
        return index;
    }

    /**
     * Set data which will be classified.
     *
     * @param data which will be classified.
     */
    public void setData(double ...data) {
        ArgumentChecks.ensureNonNull("data table", data);
        if (data.length < classNumber)
            throw new IllegalArgumentException("classNumber will not be able to > dataLenght. dataLenght = "+dataLength);
        this.data       = data;
        this.dataLength = data.length;
        this.index      = null;
    }

    /**
     * Set class number.
     *
     * @param classNumber class number ask by caller.
     */
    public void setClassNumber(int classNumber) {
        ArgumentChecks.ensureStrictlyPositive("classNumber", classNumber);
        this.classNumber = classNumber;
        this.index       = null;
    }

    /**
     * Verify there are enough distinct data, from {@link #data} to compute Jenks classes.
     *
     * @return true if Jenks computing is possible else false.
     */
    private boolean checkJenksDataValidity() {
        final int[] tabNbreClass = new int[dataLength];
        double currentVal;
        int nbreSameElmt;
        for (int idCurrentVal = 0; idCurrentVal < dataLength; idCurrentVal++) {
            currentVal = data[idCurrentVal];
            nbreSameElmt = 1;
            for (int idtest = 0; idtest < dataLength; idtest++) {
                if (currentVal == data[idtest] && idCurrentVal != idtest) nbreSameElmt++;
            }
            tabNbreClass[nbreSameElmt]++;
        }
        int nbreClassMax = 0;
        for (int i = 1; i<dataLength; i++) {
            nbreClassMax += tabNbreClass[i]/i;
            if (nbreClassMax >= classNumber) return true;
        }
        return false;
    }
}
