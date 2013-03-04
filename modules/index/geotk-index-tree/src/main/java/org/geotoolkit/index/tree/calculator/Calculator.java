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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.PROP_HILBERT_ORDER;
import static org.geotoolkit.index.tree.Node.PROP_HILBERT_TABLE;
import static org.geotoolkit.index.tree.Node.PROP_ISLEAF;
import org.geotoolkit.index.tree.hilbert.HilbertIterator;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;

/**
 * Define a generic Calculator to define computing rules of tree.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class Calculator {

    final int[]dims;

    public Calculator(final int[] dims) {
        this.dims = dims;
    }


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
     * Method exclusively used by {@code HilbertRTree}.
     *
     * Create subnode(s) centroid(s). These centroids define Hilbert curve.
     * Increase the Hilbert order of {@code Node} passed in parameter by
     * one unity.
     *
     * @param candidate HilbertLeaf to increase Hilbert order.
     * @param
     * @throws IllegalArgumentException if param "candidate" is null.
     * @throws IllegalArgumentException if param hl Hilbert order is larger than
     * them Hilbert RTree order.
     */
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) throws MismatchedDimensionException {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", order);
        assert order <= ((HilbertRTree)candidate.getTree()).getHilbertOrder() : 
                "impossible to build HilbertLeaf with Hilbert order higher than tree Hilbert order.";
        candidate.getChildren().clear();
        candidate.setUserProperty(PROP_ISLEAF, true);
        candidate.setUserProperty(PROP_HILBERT_ORDER, order);
        candidate.setBound(bound);
        final List<Node> listN = candidate.getChildren();
        listN.clear();
        if (order > 0) {
            int dim = bound.getDimension();
            int dim2 = dim;
            for (int d = 0; d < dim2; d++) if (bound.getSpan(d) <= 1E-9) dim--;
            final int nbCells = 2 << (dim * order - 1);
            int[] tabHV = new int[nbCells];
            for (int i = 0; i < nbCells; i++) {
                tabHV[i] = i;
                listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, null, i, null));
            }
            candidate.setUserProperty(PROP_HILBERT_TABLE, tabHV);
        } else {
            listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, null, 0, null));
        }
        candidate.setBound(bound);
    }
    
    /**
     * Find Hilbert order of an entry from candidate.
     *
     * @param candidate entry 's hilbert value from it.
     * @param entry which we looking for its Hilbert order.
     * @throws IllegalArgumentException if parameter "entry" is out of this node
     * boundary.
     * @throws IllegalArgumentException if entry is null.
     * @return integer the entry Hilbert order.
     */
    public abstract int getHVOfEntry(final Node candidate, final Envelope entry);

    public int[] getDims() {
        return dims;
    }

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
    
    /**Create subnode(s) centroid(s). These centroids define Hilbert curve.
     *
     * @param hl HilbertLeaf to create Hilbert curve (subnode centroids).
     * @param order Hilbert curve order.
     * @param dims[] space dimension.
     */
    protected List<DirectPosition> createPath(final Node hl, final int order, final int ...dims) {
        final Envelope bound = hl.getBound();
        final DirectPosition median = getMedian(bound);
        final int spaceDimension = dims.length;
        final List<DirectPosition> path = new ArrayList<DirectPosition>();

        final HilbertIterator hIt = new HilbertIterator(order, spaceDimension);

        final double[] spans = new double[spaceDimension];
        for(int i = 0; i<spaceDimension; i++) {
            spans[i] = bound.getSpan(dims[i]) / (2<<(order-1));
        }
        final double[] coords = new double[spaceDimension];
        for(int i = 0; i<spaceDimension; i++) {
            coords[i] = bound.getMinimum(dims[i]) + spans[i]/2;
        }
        final DirectPosition dptemp = new GeneralDirectPosition(median);
        int[] coordinate;
        while (hIt.hasNext()) {
            coordinate = hIt.next();
            for(int i=0; i<spaceDimension; i++) {
                dptemp.setOrdinate(dims[i], coords[i] + spans[i]*coordinate[i]);
            }
            path.add(new GeneralDirectPosition(dptemp));
        }
        return path;
    }
}
