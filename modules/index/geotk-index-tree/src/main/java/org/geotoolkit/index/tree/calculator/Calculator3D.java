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

import java.util.Comparator;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
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

    /**
     * To compare two {@code Node} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Node> nodeComparatorXLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getLowerCorner().getOrdinate(0));
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getLowerCorner().getOrdinate(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code Node} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Node> nodeComparatorYLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLowerCorner().getOrdinate(1));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLowerCorner().getOrdinate(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Node} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Node> nodeComparatorZLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLowerCorner().getOrdinate(2));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLowerCorner().getOrdinate(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Envelope} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorXLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getLowerCorner().getOrdinate(0));
            java.lang.Double x2 = new java.lang.Double(o2.getLowerCorner().getOrdinate(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code Envelope} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorYLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getLowerCorner().getOrdinate(1));
            java.lang.Double y2 = new java.lang.Double(o2.getLowerCorner().getOrdinate(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Envelope} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorZLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getLowerCorner().getOrdinate(2));
            java.lang.Double y2 = new java.lang.Double(o2.getLowerCorner().getOrdinate(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Node} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Node> nodeComparatorXUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getUpperCorner().getOrdinate(0));
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getUpperCorner().getOrdinate(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code Node} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Node> nodeComparatorYUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpperCorner().getOrdinate(1));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpperCorner().getOrdinate(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Node} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Node> nodeComparatorZUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpperCorner().getOrdinate(2));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpperCorner().getOrdinate(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Envelope} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorXUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getUpperCorner().getOrdinate(0));
            java.lang.Double x2 = new java.lang.Double(o2.getUpperCorner().getOrdinate(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code Envelope} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorYUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpperCorner().getOrdinate(1));
            java.lang.Double y2 = new java.lang.Double(o2.getUpperCorner().getOrdinate(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Envelope} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorZUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpperCorner().getOrdinate(2));
            java.lang.Double y2 = new java.lang.Double(o2.getUpperCorner().getOrdinate(2));
            return y1.compareTo(y2);
        }
    };

    public Calculator3D() {
    }

    /**
     * Compute Euclidean 3D bulk. {@inheritDoc }
     */
    @Override
    public double getSpace(final Envelope envelop) {
        return getGeneralEnvelopBulk(envelop);
    }

    /**
     * Compute Euclidean 3D area. {@inheritDoc }
     */
    @Override
    public double getEdge(final Envelope envelop) {
        return getGeneralEnvelopArea(envelop);
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final Envelope envelopA, final Envelope envelopB) {
        return getDistanceBetween2Envelop(envelopA, envelopB);
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
        return getDistanceBetween2DirectPosition(positionA, positionB);
    }

    /**
     * Compute Euclidean overlaps 3D area. {@inheritDoc }
     */
    @Override
    public double getOverlaps(final Envelope envelopA, final Envelope envelopB) {
        final GeneralEnvelope ge = new GeneralEnvelope(envelopA);
        ge.intersect(envelopB);
        return getGeneralEnvelopBulk(ge);
    }

    /**
     * Compute Euclidean enlargement 3D bulk. {@inheritDoc }
     */
    @Override
    public double getEnlargement(final Envelope envMin, final Envelope envMax) {
        return getGeneralEnvelopBulk(envMax) - getGeneralEnvelopBulk(envMin);
    }

    /**
     * Comparator for 3D space axis. {@inheritDoc}
     */
    @Override
    public Comparator sortFrom(final int index, final boolean lowerOrUpper, final boolean nodeOrGE) {
        ArgumentChecks.ensureBetween("sortFrom : index ", 0, 2, index);
        if (lowerOrUpper) {
            if (nodeOrGE) {
                switch (index) {
                    case 0:
                        return nodeComparatorXLow;
                    case 1:
                        return nodeComparatorYLow;
                    case 2:
                        return nodeComparatorZLow;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0:
                        return gEComparatorXLow;
                    case 1:
                        return gEComparatorYLow;
                    case 2:
                        return gEComparatorZLow;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            }
        } else {
            if (nodeOrGE) {
                switch (index) {
                    case 0:
                        return nodeComparatorXUpp;
                    case 1:
                        return nodeComparatorYUpp;
                    case 2:
                        return nodeComparatorZUpp;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0:
                        return gEComparatorXUpp;
                    case 1:
                        return gEComparatorYUpp;
                    case 2:
                        return gEComparatorZUpp;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getHVOfEntry(final Node candidate, final Envelope entry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
