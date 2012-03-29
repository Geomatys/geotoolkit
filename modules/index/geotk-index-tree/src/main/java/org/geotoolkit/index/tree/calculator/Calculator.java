/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.hilbert.Hilbert;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

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
     * Return a {@code Comparator} to sort list elements.
     *
     * @param index : ordinate choosen to compare.
     * @param lowerOrUpper : true to sort from "lower boundary", false from
     * "upper boundary"
     * @param nodeOrGE : true  to sort {@code Node} type elements,
     *                   false to sort {@code Envelope} type elements.
     */
    public abstract Comparator sortFrom(final int index, final boolean lowerOrUpper, final boolean nodeOrGE);

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
    public abstract void createBasicHL(final Node candidate, final int order, final Envelope bound)throws MismatchedDimensionException, TransformException;

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
        final int[] generalPath = Hilbert.createPath(spaceDimension, order);
        final double[] spans = new double[spaceDimension];
        for(int i = 0; i<spaceDimension;i++){
            spans[i] = bound.getSpan(dims[i])/(2<<(order-1));
        }
        final double[] coords = new double[spaceDimension];
        for(int i = 0;i<spaceDimension;i++){
            coords[i] = bound.getMinimum(dims[i]) + spans[i]/2;
        }
        for(int i = 0,l=generalPath.length; i<=l-spaceDimension; i+=spaceDimension){
            final DirectPosition dptemp = new GeneralDirectPosition(median);
            for(int j = 0;j<spaceDimension;j++){
                dptemp.setOrdinate(dims[j], coords[j]+spans[j]*generalPath[i+j]);
            }
            path.add(dptemp);
        }
        return path;
    }

}
