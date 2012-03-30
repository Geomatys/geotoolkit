/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.calculator;

import static java.lang.Math.*;
import java.util.Comparator;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * TODO : all
 * @author rmarech
 */
public class GeoCalculator extends Calculator2D{

    final double radius;
    final int[]dims;
    public GeoCalculator(double radius, int...dims) {
        this.dims = dims;
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
            java.lang.Double x1 = new java.lang.Double(o1LC.getOrdinate(dims[0])*cos(o1LC.getOrdinate(dims[1])));
            java.lang.Double x2 = new java.lang.Double(o2LC.getOrdinate(dims[0])*cos(o1LC.getOrdinate(dims[1])));
            return x1.compareTo(x2);
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
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLowerCorner().getOrdinate(dims[1]));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLowerCorner().getOrdinate(dims[1]));
            return y1.compareTo(y2);
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
            java.lang.Double x1 = new java.lang.Double(o1LC.getOrdinate(dims[0])*cos(o1LC.getOrdinate(dims[1])));
            java.lang.Double x2 = new java.lang.Double(o2LC.getOrdinate(dims[0])*cos(o1LC.getOrdinate(dims[1])));
            return x1.compareTo(x2);
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
            java.lang.Double y1 = new java.lang.Double(o1.getLowerCorner().getOrdinate(dims[1]));
            java.lang.Double y2 = new java.lang.Double(o2.getLowerCorner().getOrdinate(dims[1]));
            return y1.compareTo(y2);
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
            java.lang.Double x1 = new java.lang.Double(o1UC.getOrdinate(dims[0])*cos(o1UC.getOrdinate(dims[1])));
            java.lang.Double x2 = new java.lang.Double(o2UC.getOrdinate(dims[0])*cos(o1UC.getOrdinate(dims[1])));
            return x1.compareTo(x2);
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
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpperCorner().getOrdinate(dims[1]));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpperCorner().getOrdinate(dims[1]));
            return y1.compareTo(y2);
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
            java.lang.Double x1 = new java.lang.Double(o1UC.getOrdinate(dims[0])*cos(o1UC.getOrdinate(dims[1])));
            java.lang.Double x2 = new java.lang.Double(o2UC.getOrdinate(dims[0])*cos(o1UC.getOrdinate(dims[1])));
            return x1.compareTo(x2);
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
            java.lang.Double y1 = new java.lang.Double(o1.getUpperCorner().getOrdinate(dims[1]));
            java.lang.Double y2 = new java.lang.Double(o2.getUpperCorner().getOrdinate(dims[1]));
            return y1.compareTo(y2);
        }
    };

    /**
     * Compute Euclidean 2D area. {@inheritDoc }
     */
    @Override
    public double getSpace(final Envelope envelop) {



        return getGeneralEnvelopArea(envelop);
    }

    /**
     * Compute Euclidean 2D perimeter. {@inheritDoc }
     */
    @Override
    public double getEdge(final Envelope envelop) {
        return getGeneralEnvelopPerimeter(envelop);
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Envelope envelopA, final Envelope envelopB) {
        final DirectPosition dpA = getMedian(envelopA);
        final DirectPosition dpB = getMedian(envelopB);
        double dλ = dpB.getOrdinate(dims[0]) - dpA.getOrdinate(dims[0]);
        dλ*=cos(dpA.getOrdinate(dims[1])+dpB.getOrdinate(dims[1])/2);
        double dφ = dpA.getOrdinate(dims[1])-dpB.getOrdinate(dims[1]);
        double dR = (dims.length>2)?Math.abs(dpA.getOrdinate(dims[2])-dpB.getOrdinate(dims[2])):0;
        double ray = (dims.length>2)?Math.min(dpA.getOrdinate(dims[2]),dpB.getOrdinate(dims[2])):radius;
        dλ*=dλ;ray*=ray;dR*=dR;dφ*=dφ;
        return Math.sqrt(dλ*ray+dφ*ray+dR);
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        double dλ = positionB.getOrdinate(dims[0]) - positionA.getOrdinate(dims[0]);
        dλ*=cos(positionA.getOrdinate(dims[1])+positionB.getOrdinate(dims[1])/2);
        double dφ = positionA.getOrdinate(dims[1])-positionB.getOrdinate(dims[1]);
        double dR = (dims.length>2)?Math.abs(positionA.getOrdinate(dims[2])-positionB.getOrdinate(dims[2])):0;
        double ray = (dims.length>2)?Math.min(positionA.getOrdinate(dims[2]),positionB.getOrdinate(dims[2])):radius;
        dλ*=dλ;ray*=ray;dR*=dR;dφ*=dφ;
        return Math.sqrt(dλ*ray+dφ*ray+dR);
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
        return getGeneralEnvelopArea(ge);
    }

    /**
     * Compute Euclidean enlargement 2D area. {@inheritDoc }
     */
    @Override
    public double getEnlargement(final Envelope envMin, final Envelope envMax) {
        return getGeneralEnvelopArea(envMax) - getGeneralEnvelopArea(envMin);
    }

    /**
     * Comparator for 2D space axis. {@inheritDoc }
     */
    @Override
    public Comparator sortFrom(final int index, final boolean lowerOrUpper, final boolean nodeOrGE) {
        ArgumentChecks.ensureBetween("sortFrom : index ", 0, 1, index);
        if (lowerOrUpper) {
            if (nodeOrGE) {
                switch (index) {
                    case 0:
                        return nodeComparatorλLow;
                    case 1:
                        return nodeComparatorφLow;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0:
                        return gEComparatorλLow;
                    case 1:
                        return gEComparatorφLow;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            }
        } else {
            if (nodeOrGE) {
                switch (index) {
                    case 0:
                        return nodeComparatorλUpp;
                    case 1:
                        return nodeComparatorφUpp;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0:
                        return gEComparatorλUpp;
                    case 1:
                        return gEComparatorφUpp;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            }
        }
    }
}
