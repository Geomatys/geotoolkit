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
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Define a two dimension Calculator.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Calculator2D extends Calculator {

    /**
     * To compare two {@code Node} from them boundary box minimum x axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Node} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Envelope} from them boundary box minimum x
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Envelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Node} from them boundary box minimum x axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Node} from them boundary box minimum y axis
     * coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Envelope} from them boundary box minimum x
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
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
     * To compare two {@code Envelope} from them boundary box minimum y
     * axis coordinate.
     *
     * @see StarNode#organizeFrom(int)
     */
    private final Comparator<Envelope> gEComparatorYUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpperCorner().getOrdinate(1));
            java.lang.Double y2 = new java.lang.Double(o2.getUpperCorner().getOrdinate(1));
            return y1.compareTo(y2);
        }
    };

    public Calculator2D() {
    }

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
        return getDistanceBetween2DirectPosition(getMedian(envelopA), getMedian(envelopB));
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        return getDistanceBetween2DirectPosition(positionA, positionB);
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
                        return nodeComparatorXLow;
                    case 1:
                        return nodeComparatorYLow;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0:
                        return gEComparatorXLow;
                    case 1:
                        return gEComparatorYLow;
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
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0:
                        return gEComparatorXUpp;
                    case 1:
                        return gEComparatorYUpp;
                    default:
                        throw new IllegalStateException("no comparator finded");
                }
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", order);
        candidate.getChildren().clear();
        final CoordinateReferenceSystem crs = bound.getCoordinateReferenceSystem();
        final List<DirectPosition> listOfCentroidChild = (List<DirectPosition>) candidate.getUserProperty("centroids");
        listOfCentroidChild.clear();
        candidate.setUserProperty("isleaf", true);
        candidate.setUserProperty("hilbertOrder", order);
        candidate.setBound(bound);
        final List<Node> listN = candidate.getChildren();
        listN.clear();
        if (order > 0) {
            final double width = bound.getSpan(0);
            final double height = bound.getSpan(1);
            final int dim = 2<<((Integer) candidate.getUserProperty("hilbertOrder"))-1;
            int[][] tabHV = new int[dim][dim];

            double fract, ymin, xmin;
            final int nbCells = 2<<(2*order-1);
            if (width * height <= 0) {
                if (width <= 0) {
                    fract = height / (2 * nbCells);
                    ymin = bound.getLowerCorner().getOrdinate(1);
                    xmin = bound.getLowerCorner().getOrdinate(0);
                    for (int i = 1; i < 2 * nbCells; i += 2) {
                        final DirectPosition dpt = new GeneralDirectPosition(crs);
                        dpt.setOrdinate(0, xmin);
                        dpt.setOrdinate(1, ymin + i * fract);
                        listOfCentroidChild.add(dpt);
                    }
                } else {
                    fract = width / (2 * nbCells);
                    ymin = bound.getLowerCorner().getOrdinate(1);
                    xmin = bound.getLowerCorner().getOrdinate(0);
                    for (int i = 1; i < 2 * nbCells; i += 2) {
                        final DirectPosition dpt = new GeneralDirectPosition(crs);
                        dpt.setOrdinate(0, xmin + i * fract);
                        dpt.setOrdinate(1, ymin);
                        listOfCentroidChild.add(dpt);
                    }
                }
                int[] groundZero = new int[nbCells];
                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    groundZero[i] = i;
                    listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(i), i, null));
                }
                candidate.setUserProperty("tabHV", groundZero);
            } else {
                listOfCentroidChild.addAll(createPath(candidate, order, 0,1));
                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    final DirectPosition ptCTemp = listOfCentroidChild.get(i);
                    ArgumentChecks.ensureNonNull("the crs ptCTemp", ptCTemp.getCoordinateReferenceSystem());
                    int[] tabTemp = getHilbCoord(candidate, ptCTemp, bound, order);
                    tabHV[tabTemp[0]][tabTemp[1]] = i;
                    listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, ptCTemp, i, null));
                }
                candidate.setUserProperty("tabHV", tabHV);
            }
        } else {
            listOfCentroidChild.add(new GeneralDirectPosition(getMedian(bound)));
            listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(0), 0, null));
        }
        candidate.setBound(bound);
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
    public static int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        double div = 2<<(hilbertOrder-1);
        final double divX = envelop.getSpan(0) / div;
        final double divY = envelop.getSpan(1) / div;
        double hdx = (Math.abs(dPt.getOrdinate(0) - envelop.getLowerCorner().getOrdinate(0)) / divX);
        double hdy = (Math.abs(dPt.getOrdinate(1) - envelop.getLowerCorner().getOrdinate(1)) / divY);
        final int hx = (hdx <= 1) ? 0 : 1;
        final int hy = (hdy <= 1) ? 0 : 1;
        return new int[]{hx, hy};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getHVOfEntry(final Node candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        if (! bound.contains(ptCE)) {
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        final int order = (Integer) candidate.getUserProperty("hilbertOrder");
        if (calc.getSpace(bound) <= 0) {
            final double w = bound.getSpan(0);
            final double h = bound.getSpan(1);
            final int ordinate = (w > h) ? 0 : 1;
            final int nbCells = 2<<(2*order-1);
            final double fract = bound.getSpan(ordinate) / nbCells;
            final double lenght = Math.abs(bound.getLower(ordinate) - ptCE.getOrdinate(ordinate));
            int result = (int) (lenght / fract);
            if (result == nbCells)result--;
            return result;
        } else {
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]];
        }
    }
}
