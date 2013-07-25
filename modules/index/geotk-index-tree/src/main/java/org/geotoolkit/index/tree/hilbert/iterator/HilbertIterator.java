/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.index.tree.hilbert.iterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Iterator which follow multi-dimensional Hilbert curve.
 *
 * @author Rémi Marechal        (Geomatys).
 * @author Martin Desruisseaux  (Geomatys).
 */
public class HilbertIterator implements Iterator<int[]> {

    private final HilbertIterator subOrder;
    private final boolean[] sign;
    private final int[]ordinates;
    private final int[]coordinates;
    private final int l;
    private final int nbCells;
    private final int hilbertOrder;
    private final int dimension;
    private final int modulo;
    private int compteurCells = 0;
    private int currentPos = 0;
    private int compteurJoin = 0;
    private int joinInf = 1;
    private int ordinate;
    private int currentSign;
    private boolean signVal;
    private boolean oneTime = true;
    private boolean firstTime = true;

    /**
     * Create Iterator to build Hilbert curve.
     *
     * @param hilbertOrder Hilbert curve order.
     * @param dimension : space dimension where curve is define.
     */
    public HilbertIterator(int hilbertOrder, int dimension) {
        this(hilbertOrder, dimension, true);
    }

    /**
     * Create Iterator to build Hilbert curve.
     *
     * @param hilbertOrder Hilbert curve order.
     * @param dimension : space dimension where curve is define.
     * @param superParent : define which HilbertIterator is tallest.
     */
    private HilbertIterator(int hilbertOrder, int dimension, boolean superParent) {
        this.hilbertOrder = hilbertOrder;
        this.dimension = dimension;
        nbCells = 2 << (dimension*hilbertOrder - 1);
        modulo = 2 << dimension-1;
        l = modulo - 1;
        ordinates = generateBasicOrd(dimension-1, new int[]{dimension-1,dimension-2,dimension-1});
        if (superParent) {
            sign = new boolean[dimension];
            Arrays.fill(sign, true);
        } else {
            sign = null;
        }
        coordinates = new int[dimension];
        subOrder = (hilbertOrder != 1) ? new HilbertIterator(hilbertOrder-1, dimension, false) : null;
    }

    /**
     * Create appropriate first Hilbert curve path iteration.
     *
     * @param remainingDim space dimension.
     * @param currentlyPath
     * @return order 1 Hilbert curve path.
     */
    private int[] generateBasicOrd(int remainingDim, int[] currentlyPath) {
        if (remainingDim == 1) return currentlyPath;
        final int length = currentlyPath.length;
        final int size = 2*length+1;
        final int[] path = new int[size];
        System.arraycopy(currentlyPath.clone(), 0, path, 0, length);
        path[length] = remainingDim-2;
        System.arraycopy(currentlyPath.clone(), 0, path, length+1, length);
        return generateBasicOrd(remainingDim-1, path);
    }

    /**
     * Appropriate Hilbert ordinate operation.
     *
     * @param val current sub-order ordinate.
     * @param operande current ordinate.
     */
    private int oLogic(int val, final int operande) {
        return Math.abs((val + operande) % dimension);
    }

    /**
     * Return true if next Hilbert iteration is possible.
     *
     * @return true if next iterate exit else false.
     */
    private boolean hasNextOperande() {
        if (subOrder == null) return currentPos < l;
        return (compteurJoin < l || subOrder.hasNextOperande());
    }

    /**
     * Compute which ordinate is followed by Hilbert curve and its sens.
     */
    private void nextOperande() {
        if (hilbertOrder == 1) {
            ordinate = ordinates[currentPos];
            currentPos++;
            if (sign != null) {
                signVal = sign[ordinate];
                sign[ordinate] = !sign[ordinate];
            }
        } else {
            if (subOrder.hasNextOperande()) {
                subOrder.nextOperande();
                ordinate = oLogic(subOrder.ordinate, ordinates[currentPos]);
                if (sign != null) {
                    sign[ordinate] = !sign[ordinate];
                    signVal = (joinInf % modulo == 0) ? sign[ordinate] : !sign[ordinate];
                }
                joinInf++;
            } else {
                ordinate = ordinates[compteurJoin];
                compteurJoin++;
                joinInf = 1;
                if (currentPos > 0 && currentPos < l-1) {
                    oneTime = !oneTime;
                    if(oneTime) currentPos += (currentPos != l-2) ? 2 : 1;
                } else {
                    currentPos++;
                }
                if (sign != null) {
                    sign[ordinate] = !sign[ordinate];
                    signVal = sign[ordinate];
                }
                if (compteurJoin < l+1) subOrder.rewind();
            }
        }
    }

/**
 * Return next Hilbert curve point coordinates.
 *
 * @return next Hilbert curve point coordinates.
 */
    @Override
    public int[] next() {
        compteurCells++;
        if (firstTime) {
            firstTime = false;
            return coordinates.clone();
        }
        nextOperande();
        currentSign = (signVal) ? 1 : -1;
        coordinates[ordinate] += currentSign;
        return coordinates.clone();
    }

    /**
     * Initialize HilbertIterator.
     */
    public void rewind() {
        currentPos = compteurJoin = 0;
        oneTime = true;
        joinInf = 1;
        if (sign != null) Arrays.fill(sign, true);
        if (subOrder != null) subOrder.rewind();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not used.");
    }

    /**
     * Returns true if a next Hilbert curve point will be able to compute else false.
     * In other words if {@link HilbertIterator#next() }.
     *
     * @return true if a next point will be able to compute else false.
     */
    @Override
    public boolean hasNext() {
        return compteurCells < nbCells;
    }
}
