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
 * Define a three dimension {@code Calculator}.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Calculator3D extends Calculator {

    public Calculator3D(final int[] dims) {
        super(dims);
    }

    /**
     * Compute Euclidean 3D bulk. {@inheritDoc }
     */
    @Override
    public double getSpace(final Envelope envelop) {
        double result = 1;
        for(int i = 0,l = dims.length; i<l; i++) {
            result *= envelop.getSpan(dims[i]);
        }
        return result;
    }

    /**
     * Compute Euclidean 3D area. {@inheritDoc }
     */
    @Override
    public double getEdge(final Envelope envelop) {
        double result = 0;
        for(int i = 0,l = dims.length;i<l; i++) {
            for(int j = i + 1; j<l; j++) {
                result += envelop.getSpan(dims[i]) * envelop.getSpan(dims[j]);
            }
        }
        return 2*result;
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Envelope envelopA, final Envelope envelopB) {
        return getDistance(getMedian(envelopA), getMedian(envelopB));
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Node nodeA, final Node nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        final int l = dims.length;
        double[] tab = new double[l];
        for(int i = 0; i<l; i++) {
            tab[i] = positionB.getOrdinate(dims[i]) - positionA.getOrdinate(dims[i]);
        }
        double result = 0;
        for(int i = 0; i<l; i++) {
            result += (tab[i]*tab[i]);
        }
        return Math.sqrt(result);
    }

    /**
     * Compute Euclidean overlaps 3D area. {@inheritDoc }
     */
    @Override
    public double getOverlaps(final Envelope envelopA, final Envelope envelopB) {
        final GeneralEnvelope ge = new GeneralEnvelope(envelopA);
        ge.intersect(envelopB);
        return getSpace(ge);
    }

    /**
     * Compute Euclidean enlargement 3D bulk. {@inheritDoc }
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
     * @return int[] table of length 3 which contains 3 coordinates.
     */
    public int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        assert calc instanceof Calculator3D : "getHilbertCoord : calculator3D type required";
        final double div  = 2 << hilbertOrder - 1;
        final double divX = envelop.getSpan(dims[0]) / div;
        final double divY = envelop.getSpan(dims[1]) / div;
        final double divZ = envelop.getSpan(dims[2]) / div;
        double hdx        = (Math.abs(dPt.getOrdinate(dims[0]) - envelop.getLowerCorner().getOrdinate(dims[0])) / divX);
        double hdy        = (Math.abs(dPt.getOrdinate(dims[1]) - envelop.getLowerCorner().getOrdinate(dims[1])) / divY);
        double hdz        = (Math.abs(dPt.getOrdinate(dims[2]) - envelop.getLowerCorner().getOrdinate(dims[2])) / divZ);
        int hx      = (int) hdx; 
        int hy      = (int) hdy;
        int hz      = (int) hdz;
        if (hx == div) hx--;
        if (hy == div) hy--;
        if (hz == div) hz--;

        if (calc.getSpace(envelop) <= 0) {
            int index = -1;
            for(int i = 0; i < 3; i++) {
                if (envelop.getSpan(dims[i]) <= 0) {
                    index = i; break;
                }
            }
            switch (index) {
                case 0 : return new int[] {hy, hz};
                case 1 : return new int[] {hx, hz};
                case 2 : return new int[] {hx, hy};
                default : throw new IllegalStateException("hilbertCoord not find");
            }
        } else {
            return new int[] {hx, hy, hz};
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getHVOfEntry(final Node candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        final int order = (Integer) candidate.getUserProperty(PROP_HILBERT_ORDER);
        final int dimH  = 2 << order - 1;
        if (! bound.contains(ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");
        if (getSpace(bound) <= 0) {//2D
            if (getEdge(bound) <= 0) {//1D
                final int nbCells = 2 << 2 * order - 1;
                int index = -1;
                for(int i = 0, d = dims.length; i < d; i++) {
                    if(bound.getSpan(dims[i]) > 0) {
                        index = dims[i];
                        break;
                    }
                }
                final double fract  = bound.getSpan(index) / nbCells;
                final double lenght = Math.abs(bound.getLowerCorner().getOrdinate(index) - ptCE.getOrdinate(index));
                int result          = (int) (lenght / fract);
                if (result == nbCells) result--;
                return result;
            }
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0] + hCoord[1] * dimH];
        }
        int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
        return ((int[]) candidate.getUserProperty(PROP_HILBERT_TABLE))[hCoord[0] + hCoord[1] * dimH + hCoord[2] * dimH*dimH];
    }
}
