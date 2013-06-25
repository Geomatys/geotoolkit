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

import java.util.Arrays;
import java.util.List;
import org.geotoolkit.index.tree.Node;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.Envelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;

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
    public abstract double getDistance(final Node nodeA, final Node nodeB);
    
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
    public void sortList(int index, boolean lowerOrUpper, List list, List<Object> listObject) {
        ArgumentChecks.ensureNonNull("list", list);
        if (list.isEmpty()) return ;
        boolean alreadySort;
        final boolean isNode = (list.get(0) instanceof Node);
        if (isNode) assert(listObject == null):"listObject should be null.";        
        
        if (!isNode) {
            assert (list.get(0) instanceof double[]) : "objects should be instance of double[] if they aren't Node.";
        }
        final int siz = list.size();
        double[] env1, env2;
        double val1, val2;
        
        for (int bornMin = 0; bornMin < siz-1; bornMin++) {
            alreadySort = true;
            for (int id2 = siz-1; id2 > bornMin; id2--) {
                if (isNode) {
                    env1 = ((Node)list.get(id2)).getBoundary();
                    env2 = ((Node)list.get(id2-1)).getBoundary();
                } else {
                    env1 = (double[])list.get(id2);
                    env2 = (double[])list.get(id2-1);
                }
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
                    if (!isNode && listObject != null) listObject.add(id2-1,listObject.remove(id2));
                }
            }
            if (alreadySort) break;
        }
    }
}
