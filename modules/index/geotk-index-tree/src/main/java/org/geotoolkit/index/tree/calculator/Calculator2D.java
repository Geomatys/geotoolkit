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

import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.*;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * Define a two dimension Calculator.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Calculator2D extends Calculator {

    public Calculator2D(final int[] dims) {
        super(dims);
    }

    /**
     * Compute Euclidean 2D area. {@inheritDoc }
     */
    @Override
    public double getSpace(final Envelope envelop) {
        double result = 1;
        for (int i = 0, l = dims.length; i < l; i++) {
            result *= envelop.getSpan(dims[i]);
        }
        return result;
    }

    /**
     * Compute Euclidean 2D perimeter. {@inheritDoc }
     */
    @Override
    public double getEdge(final Envelope envelop) {
        double result = 0;
        for (int i = 0,l = dims.length; i < l; i++) {
            result += envelop.getSpan(dims[i]);
        }
        return 2 * result;
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Envelope envelopA, final Envelope envelopB) {
        return getDistance(getMedian(envelopA), getMedian(envelopB));
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        final int l = dims.length;
        double[] tab = new double[l];
        for(int i = 0; i < l; i++) {
            tab[i] = positionB.getOrdinate(dims[i]) - positionA.getOrdinate(dims[i]);
        }
        double result = 0;
        for(int i = 0; i<l; i++){
            result += (tab[i]*tab[i]);
        }
        return Math.sqrt(result);
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Node nodeA, final Node nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * Compute Euclidean overlaps 2D area. {@inheritDoc }
     */
    @Override
    public double getOverlaps(final Envelope envelopA, final Envelope envelopB) {
        final GeneralEnvelope ge = new GeneralEnvelope(envelopA);
        ge.intersect(envelopB);
        return getSpace(ge);
    }

    /**
     * Compute Euclidean enlargement 2D area. {@inheritDoc }
     */
    @Override
    public double getEnlargement(final Envelope envMin, final Envelope envMax) {
        return getSpace(envMax) - getSpace(envMin);
    }

    /**
     * Find {@code DirectPosition} Hilbert coordinate from this Node.
     *
     * @param pt {@code DirectPosition}
     * @throws IllegalArgumentException if parameter "dPt" is out of this node
     * boundary.
     * @throws IllegalArgumentException if parameter dPt is null.
     * @return int[] table of length 2 which contains two coordinates.
     */
    public int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) throw new IllegalArgumentException("Point is out of this node boundary");
        final double div  = 2 << (hilbertOrder - 1);
        final double divX = envelop.getSpan(dims[0]) / div;
        final double divY = envelop.getSpan(dims[1]) / div;
        int hdx        = (int) (Math.abs(dPt.getOrdinate(dims[0]) - envelop.getLowerCorner().getOrdinate(dims[0])) / divX);
        int hdy        = (int) (Math.abs(dPt.getOrdinate(dims[1]) - envelop.getLowerCorner().getOrdinate(dims[1])) / divY);
        if (hdx == div) hdx--;
        if (hdy == div) hdy--;
        return new int[]{hdx, hdy};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getHVOfEntry(final Node candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE   = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        if (! bound.contains(ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");
        final Calculator calc       = candidate.getTree().getCalculator();
        final int order             = (Integer) candidate.getUserProperty(PROP_HILBERT_ORDER);
        final int dimH              = 2 << order - 1;
        if (calc.getSpace(bound) <= 0) {
            final double w      = bound.getSpan(dims[0]);
            final double h      = bound.getSpan(dims[1]);
            final int ordinate  = (w > h) ? 0 : 1;
            final int nbCells   = 2 << (2 * order - 1);
            final double fract  = bound.getSpan(dims[ordinate]) / nbCells;
            final double lenght = Math.abs(bound.getLower(dims[ordinate]) - ptCE.getOrdinate(dims[ordinate]));
            int result          = (int) (lenght / fract);
            if (result == nbCells) result--;
            return result;
        } else {
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0] + hCoord[1] * dimH];
        }
    }
}
