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
package org.geotoolkit.index.tree.calculator;

import java.io.IOException;
import java.util.List;
import org.geotoolkit.index.tree.Node;
import org.apache.sis.util.ArgumentChecks;
import static org.geotoolkit.index.tree.TreeUtilities.*;
import org.geotoolkit.index.tree.Node;

/**
 * Define a generic Calculator to define computing rules of tree.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class Calculator {

    /**
     * @param nodeA
     * @param nodeB
     * @return distance between nodeA, nodeB.
     */
    @Deprecated
    public abstract double getDistance(final Node nodeA, final Node nodeB) throws IOException;
    
    /**
     * @param envelop
     * @return envelop bulk or area.
     */
    public abstract double getSpace(final double[] envelope);

    /**
     * @param envelop
     * @return evelop edge.
     */
    public abstract double getEdge(final double[] envelope);

    /**
     * @param envelopA
     * @param envelopB
     * @return distance between envelopA, envelopB.
     */
    public abstract double getDistanceEnvelope(final double[] envelopeA, final double[] envelopeB);

    /**
     * @param positionA
     * @param positionB
     * @return distance between positionA, positionB.
     */
    public abstract double getDistancePoint(final double[] positionA, final double[] positionB);

    /**
     * @param envelopA
     * @param envelopB
     * @return overlaps between envelopA, envelopB.
     */
    public abstract double getOverlaps(final double[] envelopeA, final double[] envelopeB);

    /**
     * <blockquote><font size=-1> <strong>NOTE : In case of narrowing, value between 0 and 1 
     * is returned.</strong> </font></blockquote>
     *
     * @param envMin
     * @param envMax
     * @return enlargement from envMin to envMax.
     */
    public abstract double getEnlargement(final double[] envelopeMin, final double[] envelopeMax);
    
    /**
     * Sort elements list.
     * 
     * @param index : ordinate choosen to compare.
     * @param lowerOrUpper : true to sort from "lower boundary", false from "upper boundary"
     * @param list : elements which will be sorted.
     * @return sorted list.
     */
    public void sortList(int index, boolean lowerOrUpper, List<Node> list) throws IOException {
        ArgumentChecks.ensureNonNull("list", list);
        if (list.isEmpty()) return ;
        final int siz = list.size();
        double[] env1, env2;
        double val1, val2;
        boolean alreadySort;
        for (int bornMin = 0; bornMin < siz-1; bornMin++) {
            alreadySort = true;
            for (int id2 = siz-1; id2 > bornMin; id2--) {
                env1 = list.get(id2).getBoundary();
                env2 = list.get(id2-1).getBoundary();
                if (lowerOrUpper) {
                    val1 = getMinimum(env1, index);
                    val2 = getMinimum(env2, index);
                } else {
                    val1 = getMaximum(env1, index);
                    val2 = getMaximum(env2, index);
                }
                if (val2 > val1) {
                    alreadySort = false;
                    list.add(id2-1, list.remove(id2));
                }
            }
            if (alreadySort) break;
        }
    }
    
    /**
     * Sort elements list.
     * 
     * @param index : ordinate choosen to compare.
     * @param lowerOrUpper : true to sort from "lower boundary", false from "upper boundary"
     * @param list : elements which will be sorted.
     * @return sorted list.
     */
    public void sort(int index, boolean lowerOrUpper, double[][] tabBoundary) throws IOException {
        ArgumentChecks.ensureNonNull("list", tabBoundary);
        if (tabBoundary.length == 0) return ;
        final int siz = tabBoundary.length;
        double[] envExchange;
        double val1, val2;
        boolean alreadySort;
        for (int bornMin = 0; bornMin < siz-1; bornMin++) {
            alreadySort = true;
            for (int id2 = siz-1; id2 > bornMin; id2--) {
                if (lowerOrUpper) {
                    val1 = getMinimum(tabBoundary[id2], index);
                    val2 = getMinimum(tabBoundary[id2 - 1], index);
                } else {
                    val1 = getMaximum(tabBoundary[id2], index);
                    val2 = getMaximum(tabBoundary[id2 - 1], index);
                }
                if (val2 > val1) {
                    alreadySort        = false;
                    envExchange        = tabBoundary[id2-1];
                    tabBoundary[id2-1] = tabBoundary[id2];
                    tabBoundary[id2]   = envExchange;
                }
            }
            if (alreadySort) break;
        }
    }
    
    /**
     * Sort elements list.
     * 
     * @param index : ordinate choosen to compare.
     * @param lowerOrUpper : true to sort from "lower boundary", false from "upper boundary"
     * @param list : elements which will be sorted.
     * @return sorted list.
     */
    public void sort(int index, boolean lowerOrUpper, Node[] children) throws IOException {
        ArgumentChecks.ensureNonNull("children", children);
        if (children.length == 0) return ;
        final int siz = children.length;
        Node nodeExchange;
        double val1, val2;
        boolean alreadySort;
        for (int bornMin = 0; bornMin < siz-1; bornMin++) {
            alreadySort = true;
            for (int id2 = siz-1; id2 > bornMin; id2--) {
                if (lowerOrUpper) {
                    val1 = getMinimum(children[id2].getBoundary(), index);
                    val2 = getMinimum(children[id2 - 1].getBoundary(), index);
                } else {
                    val1 = getMaximum(children[id2].getBoundary(), index);
                    val2 = getMaximum(children[id2 - 1].getBoundary(), index);
                }
                if (val2 > val1) {
                    alreadySort     = false;
                    nodeExchange    = children[id2-1];
                    children[id2-1] = children[id2];
                    children[id2]   = nodeExchange;
                }
            }
            if (alreadySort) break;
        }
    }
}
