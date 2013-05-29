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

import org.apache.sis.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

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
    public double getSpace(Envelope envelop) { 
        final int dim = envelop.getDimension();
        if (dim <= 2) return getGeneralEnvelopArea(envelop);
        return getGeneralEnvelopBulk(envelop);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getEdge(Envelope envelop) {
        final int dim = envelop.getDimension();
        if (dim <= 2) return getGeneralEnvelopPerimeter(envelop);
        return getGeneralEnvelopArea(envelop);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getDistance(Envelope envelopA, Envelope envelopB) {
        return getDistance(getMedian(envelopA), getMedian(envelopB));
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getDistance(DirectPosition positionA, DirectPosition positionB) {
        return getDistanceBetween2DirectPosition(positionA, positionB);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getDistance(Node nodeA, Node nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getOverlaps(Envelope envelopA, Envelope envelopB) {
        final int dim = envelopA.getDimension();
        assert (dim == envelopB.getDimension()) : "dimension not equals";
        final GeneralEnvelope union = new GeneralEnvelope(envelopA);
        union.add(envelopB);
        final GeneralEnvelope intersection = new GeneralEnvelope(envelopA);
        intersection.intersects(envelopB, true);
        double ratio = 1;
        for (int d = 0; d < dim; d++) {
            final double denominator = union.getSpan(d);
            if (denominator <= 1E-12) continue;
            ratio *= (intersection.getSpan(d) / denominator);
        }
        return ratio;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getEnlargement(Envelope envMin, Envelope envMax) {
        final int dim = envMin.getDimension();
        assert (dim == envMax.getDimension()) : "dimension not equals";
        //paranoiacUnion
        final GeneralEnvelope union = new GeneralEnvelope(envMin);
        union.add(envMax);//normaly equal to envMax.
        double ratio = 1;
        for (int d = 0; d < dim; d++) {
            final double denominator = envMin.getSpan(d);
            if (denominator <= 1E-12) continue;
            ratio *= (union.getSpan(d) / denominator);
        }
        return ratio;
    }
}
