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
import java.util.Comparator;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getGeneralEnvelopArea;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**Define a Geographic Calculator.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class GeoCalculator extends Calculator{

    final double radius;
    private final static double TO_RAD = Math.PI/180;
    public GeoCalculator(double radius, int...dims) {
        super(dims);
        this.radius = radius;
    }

    /**
     * To compare two {@code Node} from them boundary box minimum x axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Node> nodeComparatorλLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final DirectPosition o1LC = o1.getBoundary().getLowerCorner();
            final DirectPosition o2LC = o2.getBoundary().getLowerCorner();
            final double λ1 = o1LC.getOrdinate(dims[0])*TO_RAD*cos(TO_RAD*o1LC.getOrdinate(dims[1]));
            final double λ2 = o2LC.getOrdinate(dims[0])*TO_RAD*cos(TO_RAD*o1LC.getOrdinate(dims[1]));
            return Double.compare(λ1, λ2);
        }
    };
    /**
     * To compare two {@code Node} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Node> nodeComparatorφLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double φ1 = TO_RAD*o1.getBoundary().getLowerCorner().getOrdinate(dims[1]);
            final double φ2 = TO_RAD*o2.getBoundary().getLowerCorner().getOrdinate(dims[1]);
            return Double.compare(φ1, φ2);
        }
    };

    /**
     * To compare two {@code Node} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Node> nodeComparatorHLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double h1 = TO_RAD*o1.getBoundary().getLowerCorner().getOrdinate(dims[2]);
            final double h2 = TO_RAD*o2.getBoundary().getLowerCorner().getOrdinate(dims[2]);
            return Double.compare(h1, h2);
        }
    };


    /**
     * To compare two {@code Envelope} from them boundary box minimum x
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorλLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final DirectPosition o1LC = o1.getLowerCorner();
            final DirectPosition o2LC = o2.getLowerCorner();
            final double λ1 = TO_RAD*o1LC.getOrdinate(dims[0])*cos(TO_RAD*o1LC.getOrdinate(dims[1]));
            final double λ2 = TO_RAD*o2LC.getOrdinate(dims[0])*cos(TO_RAD*o1LC.getOrdinate(dims[1]));
            return Double.compare(λ1, λ2);
        }
    };
    /**
     * To compare two {@code Envelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorφLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double φ1 = TO_RAD*o1.getLowerCorner().getOrdinate(dims[1]);
            final double φ2 = TO_RAD*o2.getLowerCorner().getOrdinate(dims[1]);
            return Double.compare(φ1, φ2);
        }
    };
    /**
     * To compare two {@code Envelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorHLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double h1 = TO_RAD*o1.getLowerCorner().getOrdinate(dims[2]);
            final double h2 = TO_RAD*o2.getLowerCorner().getOrdinate(dims[2]);
            return Double.compare(h1, h2);
        }
    };
    /**
     * To compare two {@code Node} from them boundary box minimum x axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Node> nodeComparatorλUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final DirectPosition o1UC = o1.getBoundary().getUpperCorner();
            final DirectPosition o2UC = o2.getBoundary().getUpperCorner();
            final double λ1 = TO_RAD*o1UC.getOrdinate(dims[0])*cos(TO_RAD*o1UC.getOrdinate(dims[1]));
            final double λ2 = TO_RAD*o2UC.getOrdinate(dims[0])*cos(TO_RAD*o1UC.getOrdinate(dims[1]));
            return Double.compare(λ1, λ2);
        }
    };
    /**
     * To compare two {@code Node} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Node> nodeComparatorφUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double φ1 = TO_RAD*o1.getBoundary().getUpperCorner().getOrdinate(dims[1]);
            final double φ2 = TO_RAD*o2.getBoundary().getUpperCorner().getOrdinate(dims[1]);
            return Double.compare(φ1, φ2);
        }
    };
    /**
     * To compare two {@code Node} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Node> nodeComparatorHUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double h1 = TO_RAD*o1.getBoundary().getUpperCorner().getOrdinate(dims[2]);
            final double h2 = TO_RAD*o2.getBoundary().getUpperCorner().getOrdinate(dims[2]);
            return Double.compare(h1, h2);
        }
    };
    /**
     * To compare two {@code Envelope} from them boundary box minimum x
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorλUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final DirectPosition o1UC = o1.getUpperCorner();
            final DirectPosition o2UC = o2.getUpperCorner();
            final double λ1 = TO_RAD*o1UC.getOrdinate(dims[0])*cos(TO_RAD*o1UC.getOrdinate(dims[1]));
            final double λ2 = TO_RAD*o2UC.getOrdinate(dims[0])*cos(TO_RAD*o1UC.getOrdinate(dims[1]));
            return Double.compare(λ1, λ2);
        }
    };
    /**
     * To compare two {@code Envelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorφUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double φ1 = TO_RAD*o1.getUpperCorner().getOrdinate(dims[1]);
            final double φ2 = TO_RAD*o2.getUpperCorner().getOrdinate(dims[1]);
            return Double.compare(φ1, φ2);
        }
    };
    /**
     * To compare two {@code Envelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorHUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double h1 = TO_RAD*o1.getUpperCorner().getOrdinate(dims[2]);
            final double h2 = TO_RAD*o2.getUpperCorner().getOrdinate(dims[2]);
            return Double.compare(h1, h2);
        }
    };

    /**
     * Compute Geographic bulk. {@inheritDoc }
     */
    @Override
    public double getSpace(final Envelope envelop) {
        final DirectPosition dpL = envelop.getLowerCorner();
        final DirectPosition dpU = envelop.getUpperCorner();
        double dλ = Math.abs(TO_RAD*(dpU.getOrdinate(dims[0]) - dpL.getOrdinate(dims[0])));
        dλ*=cos(TO_RAD*((dpL.getOrdinate(dims[1]) + dpU.getOrdinate(dims[1]))/2));
        dλ = Math.abs(dλ);
        double dφ = Math.abs(TO_RAD*(dpU.getOrdinate(dims[1]) - dpL.getOrdinate(dims[1])));
        double ray = (dims.length>2)?Math.min(dpL.getOrdinate(dims[2]),dpU.getOrdinate(dims[2]))+radius:radius;
        dλ*=ray;dφ*=ray;
        assert dλ >=0:"dλ<0"+dλ;
        assert dφ >=0:"dφ<0"+dφ;
        assert ray >=0:"ray<0"+ray;
        return (dims.length>2)?dλ*dφ*Math.abs(dpU.getOrdinate(dims[2]) - dpL.getOrdinate(dims[2])):dλ*dφ;
    }

    /**
     * Compute Geographic area Envelop. {@inheritDoc }
     */
    @Override
    public double getEdge(final Envelope envelop) {
        final DirectPosition dpL = envelop.getLowerCorner();
        final DirectPosition dpU = envelop.getUpperCorner();
        double dλ = Math.abs(TO_RAD*(dpU.getOrdinate(dims[0]) - dpL.getOrdinate(dims[0])));
        dλ*=cos(TO_RAD*((dpL.getOrdinate(dims[1]) + dpU.getOrdinate(dims[1]))/2.0f));
        dλ = Math.abs(dλ);
        double dφ = Math.abs(TO_RAD*(dpU.getOrdinate(dims[1]) - dpL.getOrdinate(dims[1])));
        final double ray = (dims.length>2)?Math.min(dpL.getOrdinate(dims[2]),dpU.getOrdinate(dims[2]))+radius:radius;
        dλ*=ray;dφ*=ray;
        assert dλ >=0:"dλ<0"+dλ;
        assert dφ >=0:"dφ<0"+dφ;
        assert ray >=0:"ray<0"+ray;
        if(dims.length>2){
            double dR = Math.abs(dpL.getOrdinate(dims[2]) - dpU.getOrdinate(dims[2]));
            return 2*(dλ*dφ+dλ*dR+dφ*dR);
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
        double dλ = Math.abs(TO_RAD*(positionB.getOrdinate(dims[0]) - positionA.getOrdinate(dims[0])));
        dλ*=cos(TO_RAD*((positionA.getOrdinate(dims[1])+positionB.getOrdinate(dims[1]))/2));
        double dφ = TO_RAD*(positionA.getOrdinate(dims[1])-positionB.getOrdinate(dims[1]));
        double dR = (dims.length>2)?Math.abs(positionA.getOrdinate(dims[2])-positionB.getOrdinate(dims[2])):0;
        double ray = (dims.length>2)?Math.min(positionA.getOrdinate(dims[2]),positionB.getOrdinate(dims[2]))+radius:radius;
        dλ*=dλ;ray*=ray;dR*=dR;dφ*=dφ;
        return Math.sqrt(dλ*ray+dφ*ray+dR);
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

    /**
     * Comparator for Geographic space axis. {@inheritDoc }
     */
    @Override
    public Comparator sortFrom(final int index, final boolean lowerOrUpper, final boolean nodeOrGE) {
        ArgumentChecks.ensureBetween("sortFrom : index ", 0, 2, index);
        if (lowerOrUpper) {
            if (nodeOrGE) {
                switch (index) {
                    case 0 : return nodeComparatorλLow;
                    case 1 : return nodeComparatorφLow;
                    case 2 : return nodeComparatorHLow;
                    default:throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0 : return gEComparatorλLow;
                    case 1 : return gEComparatorφLow;
                    case 2 : return gEComparatorHLow;
                    default: throw new IllegalStateException("no comparator finded");
                }
            }
        } else {
            if (nodeOrGE) {
                switch (index) {
                    case 0 : return nodeComparatorλUpp;
                    case 1 : return nodeComparatorφUpp;
                    case 2 : return nodeComparatorHUpp;
                    default: throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0 : return gEComparatorλUpp;
                    case 1 : return gEComparatorφUpp;
                    case 2 : return gEComparatorHUpp;
                    default: throw new IllegalStateException("no comparator finded");
                }
            }
        }
    }
}
