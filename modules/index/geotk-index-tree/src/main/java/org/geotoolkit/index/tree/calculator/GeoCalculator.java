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

import static java.lang.Math.cos;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getGeneralEnvelopArea;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**Define a Geographic Calculator.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class GeoCalculator extends Calculator {

    final double radius;
    private final static double TO_RAD = Math.PI / 180;
    public GeoCalculator(double radius, int...dims) {
        super(dims);
        this.radius = radius;
    }

    /**
     * Compute Geographic bulk. {@inheritDoc }
     */
    @Override
    public double getSpace(final Envelope envelop) {
        final DirectPosition dpL = envelop.getLowerCorner();
        final DirectPosition dpU = envelop.getUpperCorner();
        double dλ = Math.abs(TO_RAD * (dpU.getOrdinate(dims[0]) - dpL.getOrdinate(dims[0])));
        dλ*=cos(TO_RAD * ((dpL.getOrdinate(dims[1]) + dpU.getOrdinate(dims[1])) / 2));
        dλ = Math.abs(dλ);
        double dφ  = Math.abs(TO_RAD * (dpU.getOrdinate(dims[1]) - dpL.getOrdinate(dims[1])));
        double ray = (dims.length > 2) ? Math.min(dpL.getOrdinate(dims[2]),dpU.getOrdinate(dims[2])) + radius : radius;
        dλ *= ray; dφ *= ray;
        assert dλ >=0  : "dλ<0" + dλ;
        assert dφ >=0  : "dφ<0" + dφ;
        assert ray >=0 :"ray<0" + ray;
        return (dims.length > 2) ? dλ * dφ * Math.abs(dpU.getOrdinate(dims[2]) - dpL.getOrdinate(dims[2])) : dλ * dφ;
    }

    /**
     * Compute Geographic area Envelop. {@inheritDoc }
     */
    @Override
    public double getEdge(final Envelope envelop) {
        final DirectPosition dpL = envelop.getLowerCorner();
        final DirectPosition dpU = envelop.getUpperCorner();
        double dλ = Math.abs(TO_RAD * (dpU.getOrdinate(dims[0]) - dpL.getOrdinate(dims[0])));
        dλ*=cos(TO_RAD * ((dpL.getOrdinate(dims[1]) + dpU.getOrdinate(dims[1])) / 2.0f));
        dλ = Math.abs(dλ);
        double dφ = Math.abs(TO_RAD * (dpU.getOrdinate(dims[1]) - dpL.getOrdinate(dims[1])));
        final double ray = (dims.length > 2) ? Math.min(dpL.getOrdinate(dims[2]), dpU.getOrdinate(dims[2])) + radius : radius;
        dλ *= ray; dφ *= ray;
        assert dλ  >= 0 : "dλ<0"  + dλ;
        assert dφ  >= 0 : "dφ<0"  + dφ;
        assert ray >= 0 : "ray<0" + ray;
        if(dims.length > 2){
            double dR = Math.abs(dpL.getOrdinate(dims[2]) - dpU.getOrdinate(dims[2]));
            return 2 * (dλ * dφ + dλ * dR + dφ * dR);
        }
        return 2*(dλ+dφ);
    }

    /**
     * Compute Geographic distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Envelope envelopA, final Envelope envelopB) {
        return getDistance(getMedian(envelopA), getMedian(envelopB));
    }

    /**
     * Compute Geographic distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        double dλ = Math.abs(TO_RAD * (positionB.getOrdinate(dims[0]) - positionA.getOrdinate(dims[0])));
        dλ        *= cos(TO_RAD * ((positionA.getOrdinate(dims[1]) + positionB.getOrdinate(dims[1])) / 2));
        double dφ  = TO_RAD * (positionA.getOrdinate(dims[1]) - positionB.getOrdinate(dims[1]));
        double dR  = (dims.length > 2) ? Math.abs(positionA.getOrdinate(dims[2]) - positionB.getOrdinate(dims[2])) : 0;
        double ray = (dims.length > 2) ? Math.min(positionA.getOrdinate(dims[2]), positionB.getOrdinate(dims[2])) + radius : radius;
        dλ *= dλ; ray *= ray; dR *= dR; dφ *= dφ;
        return Math.sqrt(dλ * ray + dφ * ray + dR);
    }

    /**
     * Compute Geographic distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Node nodeA, final Node nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * Compute Euclidean overlaps Geographic area. {@inheritDoc }
     */
    @Override
    public double getOverlaps(final Envelope envelopA, final Envelope envelopB) {
        final GeneralEnvelope ge = new GeneralEnvelope(envelopA);
        ge.intersect(envelopB);
        return getGeneralEnvelopArea(ge);
    }

    /**
     * Compute Euclidean enlargement Geographic area. {@inheritDoc }
     */
    @Override
    public double getEnlargement(final Envelope envMin, final Envelope envMax) {
        return getGeneralEnvelopArea(envMax) - getGeneralEnvelopArea(envMin);
    }
}
