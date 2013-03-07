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

import java.util.List;
import org.geotoolkit.index.tree.Node;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * Define a generic Calculator to define computing rules of tree.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class Calculator {

    /**
     * @param envelop
     * @return envelop bulk or area.
     */
    public abstract double getSpace(final Envelope envelop);

    /**
     * @param envelop
     * @return evelop edge.
     */
    public abstract double getEdge(final Envelope envelop);

    /**
     * @param envelopA
     * @param envelopB
     * @return distance between envelopA, envelopB.
     */
    public abstract double getDistance(final Envelope envelopA, final Envelope envelopB);

    /**
     * @param positionA
     * @param positionB
     * @return distance between positionA, positionB.
     */
    public abstract double getDistance(final DirectPosition positionA, final DirectPosition positionB);

    /**
     * @param nodeA
     * @param nodeB
     * @return distance between nodeA, nodeB.
     */
    public abstract double getDistance(final Node nodeA, final Node nodeB);

    /**
     * @param envelopA
     * @param envelopB
     * @return overlaps between envelopA, envelopB.
     */
    public abstract double getOverlaps(final Envelope envelopA, final Envelope envelopB);

    /**
     * <blockquote><font size=-1> <strong>NOTE : In case of narrowing, negative
     * value is returned.</strong> </font></blockquote>
     *
     * @param envMin
     * @param envMax
     * @return enlargement from envMin to envMax.
     */
    public abstract double getEnlargement(final Envelope envMin, final Envelope envMax);

    /**
     * Sort elements list.
     * 
     * @param index : ordinate choosen to compare.
     * @param lowerOrUpper : true to sort from "lower boundary", false from "upper boundary"
     * @param list : elements which will be sorted.
     * @return sorted list.
     */
    public List sortList(int index, boolean lowerOrUpper, List list) {
        ArgumentChecks.ensureNonNull("list", list);
        if (list.isEmpty()) return list;
        boolean alreadySort;
        final boolean isNode = (list.get(0) instanceof Node);
        if (!isNode) assert (list.get(0) instanceof Envelope) : "objects should be instance of Envelope if they aren't Node";
        final int siz = list.size();
        Envelope env1, env2;
        double val1, val2;
        
        for (int bornMin = 0; bornMin < siz-1; bornMin++) {
            alreadySort = true;
            for (int id2 = siz-1; id2 > bornMin; id2--) {
                if (isNode) {
                    env1 = ((Node)list.get(id2)).getBoundary();
                    env2 = ((Node)list.get(id2-1)).getBoundary();
                } else {
                    env1 = ((Envelope)list.get(id2));
                    env2 = ((Envelope)list.get(id2-1));
                }
                if (lowerOrUpper) {
                    val1 = env1.getMinimum(index);
                    val2 = env2.getMinimum(index);
                } else {
                    val1 = env1.getMaximum(index);
                    val2 = env2.getMaximum(index);
                }
                if (val2 > val1) {
                    alreadySort = false;
                    list.add(id2-1, list.remove(id2));
                }
            }
            if (alreadySort) break;
        }
        return list;
    }
}
