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
import org.geotoolkit.index.tree.DefaultNode;
import org.geotoolkit.index.tree.DefaultTreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;

/**
 * Define a three dimension {@code Calculator}.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Calculator3D extends Calculator {

    /**
     * To compare two {@code DefaultNode} from them boundary box minimum x axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<DefaultNode> nodeComparatorXLow = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getLower(0));
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getLower(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code DefaultNode} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<DefaultNode> nodeComparatorYLow = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLower(1));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLower(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum z
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<DefaultNode> nodeComparatorZLow = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLower(2));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLower(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum x
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<GeneralEnvelope> gEComparatorXLow = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getLower(0));
            java.lang.Double x2 = new java.lang.Double(o2.getLower(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<GeneralEnvelope> gEComparatorYLow = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getLower(1));
            java.lang.Double y2 = new java.lang.Double(o2.getLower(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum z
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<GeneralEnvelope> gEComparatorZLow = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getLower(2));
            java.lang.Double y2 = new java.lang.Double(o2.getLower(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code Node3D} from them boundary box minimum x axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<DefaultNode> nodeComparatorXUpp = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getUpper(0));
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getUpper(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code Node3D} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<DefaultNode> nodeComparatorYUpp = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpper(1));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpper(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum z
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<DefaultNode> nodeComparatorZUpp = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpper(2));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpper(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum x
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<GeneralEnvelope> gEComparatorXUpp = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getUpper(0));
            java.lang.Double x2 = new java.lang.Double(o2.getUpper(0));
            return x1.compareTo(x2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<GeneralEnvelope> gEComparatorYUpp = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpper(1));
            java.lang.Double y2 = new java.lang.Double(o2.getUpper(1));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * To compare two {@code GeneralEnvelope} from them boundary box minimum z
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<GeneralEnvelope> gEComparatorZUpp = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpper(2));
            java.lang.Double y2 = new java.lang.Double(o2.getUpper(2));
            return y1.compareTo(y2);
        }
    };

    public Calculator3D() {
    }

    /**
     * Compute Euclidean 3D bulk. {@inheritDoc }
     */
    @Override
    public double getSpace(final GeneralEnvelope envelop) {
        return DefaultTreeUtils.getGeneralEnvelopBulk(envelop);
    }

    /**
     * Compute Euclidean 3D area. {@inheritDoc }
     */
    @Override
    public double getEdge(final GeneralEnvelope envelop) {
        return DefaultTreeUtils.getGeneralEnvelopArea(envelop);
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final GeneralEnvelope envelopA, final GeneralEnvelope envelopB) {
        return DefaultTreeUtils.getDistanceBetween2Envelop(envelopA, envelopB);
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(DefaultNode nodeA, DefaultNode nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * Compute Euclidean 3D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        return DefaultTreeUtils.getDistanceBetween2DirectPosition(positionA, positionB);
    }

    /**
     * Compute Euclidean overlaps 3D area. {@inheritDoc }
     */
    @Override
    public double getOverlaps(final GeneralEnvelope envelopA, final GeneralEnvelope envelopB) {
        final GeneralEnvelope ge = new GeneralEnvelope(envelopA);
        ge.intersect(envelopB);
        return DefaultTreeUtils.getGeneralEnvelopBulk(ge);
    }

    /**
     * Compute Euclidean enlargement 3D bulk. {@inheritDoc }
     */
    @Override
    public double getEnlargement(final GeneralEnvelope envMin, final GeneralEnvelope envMax) {
        return DefaultTreeUtils.getGeneralEnvelopBulk(envMax) - DefaultTreeUtils.getGeneralEnvelopBulk(envMin);
    }

    /**
     * Comparator for 3D space axis. {@inheritDoc}
     */
    @Override
    public Comparator sortFrom(int index, boolean lowerOrUpper, boolean nodeOrGE) {
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
    public void createBasicHL(DefaultNode candidate, int order, GeneralEnvelope bound) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getHVOfEntry(DefaultNode candidate, GeneralEnvelope entry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
