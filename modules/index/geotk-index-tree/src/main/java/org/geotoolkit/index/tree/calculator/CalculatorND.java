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
import static org.geotoolkit.index.tree.TreeUtils.*;
import org.geotoolkit.index.tree.Node;

/**
 * {@link Calculator} defined to compute multi-dimensionnal geometric operations.
 * 
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class CalculatorND extends Calculator {
    
    /**
     * {@inheritDoc }.
     */
    @Override
    @Deprecated
    public double getDistance(Node nodeA, Node nodeB) throws IOException {
        return getDistanceBetween2Envelopes(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSpace(double[] envelope) {
        return (envelope.length/2 <= 2) ? getArea(envelope) : getBulk(envelope);//decal bit
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getEdge(double[] envelope) {
        return (envelope.length/2 <= 2) ? getPerimeter(envelope) : getArea(envelope);//decal bit
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getDistanceEnvelope(double[] envelopeA, double[] envelopeB) {
        return getDistanceBetween2Envelopes(envelopeA, envelopeB);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getDistancePoint(double[] positionA, double[] positionB) {
        return getDistanceBetween2Positions(positionA, positionB);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getOverlaps(double[] envelopeA, double[] envelopeB) {
        int dim = envelopeA.length;
        assert (dim == envelopeB.length) : "dimension not equals";
        dim = dim >> 1;
        double ratio = 1;
        for (int low = 0, upp = dim; low < dim; low++, upp++) {
            final double numerator   = Math.min(envelopeB[upp], envelopeA[upp]) - Math.max(envelopeB[low], envelopeA[low]);
            final double denominator = Math.max(envelopeB[upp], envelopeA[upp]) - Math.min(envelopeB[low], envelopeA[low]);
            if (denominator <= 1E-12) continue;
            ratio *= (numerator / denominator);//intersection/union
        }
        return ratio;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getEnlargement(double[] envelopeMin, double[] envelopeMax) {
        int dim = envelopeMin.length;
        assert (dim == envelopeMax.length) : "dimension not equals";
        dim = dim >> 1;
        final double[] union = envelopeMax.clone();
        //paranoiacUnion
        add(union, envelopeMin);//normaly equal to envMax.
        double ratio = 1;
        for (int low = 0, upp = dim; low < dim; low++, upp++) {
            final double denominator = envelopeMin[upp] - envelopeMin[low];
            if (denominator <= 1E-12) continue;
            ratio *= ((union[upp] - union[low]) / denominator);
        }
        return ratio;
    }
}
