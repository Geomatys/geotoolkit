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

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.DefaultNode;
import org.geotoolkit.index.tree.DefaultTreeUtils;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
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

    public Calculator2D() {
    }

    /**
     * Compute Euclidean 2D area. {@inheritDoc }
     */
    @Override
    public double getSpace(final GeneralEnvelope envelop) {
        return DefaultTreeUtils.getGeneralEnvelopArea(envelop);
    }

    /**
     * Compute Euclidean 2D perimeter. {@inheritDoc }
     */
    @Override
    public double getEdge(final GeneralEnvelope envelop) {
        return DefaultTreeUtils.getGeneralEnvelopPerimeter(envelop);
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final GeneralEnvelope envelopA, final GeneralEnvelope envelopB) {
        return DefaultTreeUtils.getDistanceBetween2Envelop(envelopA, envelopB);
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DirectPosition positionA, final DirectPosition positionB) {
        return DefaultTreeUtils.getDistanceBetween2DirectPosition(positionA, positionB);
    }

    /**
     * Compute Euclidean 2D distance. {@inheritDoc }
     */
    @Override
    public double getDistance(final DefaultNode nodeA, final DefaultNode nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    /**
     * Compute Euclidean overlaps 2D area. {@inheritDoc }
     */
    @Override
    public double getOverlaps(final GeneralEnvelope envelopA, final GeneralEnvelope envelopB) {
        final GeneralEnvelope ge = new GeneralEnvelope(envelopA);
        ge.intersect(envelopB);
        return DefaultTreeUtils.getGeneralEnvelopArea(ge);
    }

    /**
     * Compute Euclidean enlargement 2D area. {@inheritDoc }
     */
    @Override
    public double getEnlargement(final GeneralEnvelope envMin, final GeneralEnvelope envMax) {
        return DefaultTreeUtils.getGeneralEnvelopArea(envMax) - DefaultTreeUtils.getGeneralEnvelopArea(envMin);
    }

    /**
     * Comparator for 2D space axis. {@inheritDoc }
     */
    @Override
    public Comparator sortFrom(int index, boolean lowerOrUpper, boolean nodeOrGE) {
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
    public void createBasicHL(DefaultNode candidate, int order, GeneralEnvelope bound) {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", order);
        candidate.getChildren().clear();
        final CoordinateReferenceSystem crs = bound.getCoordinateReferenceSystem();
        final List<DirectPosition> listOfCentroidChild = (List<DirectPosition>) candidate.getUserProperty("centroids");
        listOfCentroidChild.clear();
        candidate.setUserProperty("isleaf", true);
        candidate.setUserProperty("hilbertOrder", order);
        candidate.setBound(bound);
//        final List<DefaultNode> listN = (List<DefaultNode>) candidate.getUserProperty("cells");
        final List<DefaultNode> listN = candidate.getChildren();
        listN.clear();
        if (order > 0) {
            final double width = bound.getSpan(0);
            final double height = bound.getSpan(1);
            int dim = (int) Math.pow(2, (Integer) candidate.getUserProperty("hilbertOrder"));
            int[][] tabHV = new int[dim][dim];

            final DirectPosition dp0 = new GeneralDirectPosition(crs);
            final DirectPosition dp1 = new GeneralDirectPosition(crs);
            final DirectPosition dp2 = new GeneralDirectPosition(crs);
            final DirectPosition dp3 = new GeneralDirectPosition(crs);

            double fract, ymin, xmin;
            final int nbCells = ((int) Math.pow(2, 2 * order));
            if (width * height <= 0) {
                if (width <= 0) {
                    fract = height / (2 * nbCells);
                    ymin = bound.getLower(1);
                    xmin = bound.getLower(0);
                    for (int i = 1; i < 2 * nbCells; i += 2) {
                        final DirectPosition dpt = new GeneralDirectPosition(crs);
                        dpt.setOrdinate(0, xmin);
                        dpt.setOrdinate(1, ymin + i * fract);
                        listOfCentroidChild.add(dpt);
                    }
                } else {
                    fract = width / (2 * nbCells);
                    ymin = bound.getLower(1);
                    xmin = bound.getLower(0);
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
                final double w = width / 4;
                final double h = height / 4;
                final double minx = bound.getLower(0) + w;
                final double maxx = bound.getUpper(0) - w;
                final double miny = bound.getLower(1) + h;
                final double maxy = bound.getUpper(1) - h;
                dp0.setOrdinate(0, minx);
                dp0.setOrdinate(1, miny);
                dp1.setOrdinate(0, minx);
                dp1.setOrdinate(1, maxy);
                dp2.setOrdinate(0, maxx);
                dp2.setOrdinate(1, maxy);
                dp3.setOrdinate(0, maxx);
                dp3.setOrdinate(1, miny);
                listOfCentroidChild.add(dp0);
                listOfCentroidChild.add(dp1);
                listOfCentroidChild.add(dp2);
                listOfCentroidChild.add(dp3);

                if (order > 1) {
                    for (int i = 1; i < order; i++) {
                        createHB(candidate);
                    }
                }

                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    DirectPosition ptCTemp = listOfCentroidChild.get(i);
                    ArgumentChecks.ensureNonNull("the crs ptCTemp", ptCTemp.getCoordinateReferenceSystem());
                    int[] tabTemp = getHilbCoord(candidate, ptCTemp, bound, order);
                    tabHV[tabTemp[0]][tabTemp[1]] = i;
                    listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, ptCTemp, i, null));
                }
                candidate.setUserProperty("tabHV", tabHV);
            }
        } else {
            listOfCentroidChild.add(new GeneralDirectPosition(bound.getMedian()));
            listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(0), 0, null));
        }
        candidate.setBound(bound);
    }

    /**
     * Create subnode(s) centroid(s). These centroids define Hilbert curve.
     * Increase the Hilbert order of "HilbertLeaf" passed in parameter by one
     * unity.
     *
     * @param hl HilbertLeaf to increase Hilbert order.
     * @throws IllegalArgumentException if parameter "hl" is null.
     * @throws IllegalArgumentException if parameter hl Hilbert order is larger
     * than them Hilbert RTree.
     */
    private static void createHB(final DefaultNode hl) {

        ArgumentChecks.ensureNonNull("impossible to increase hilbert order", hl);
        if ((Integer) hl.getUserProperty("hilbertOrder") > ((HilbertRTree) hl.getTree()).getHilbertOrder()) {
            throw new IllegalArgumentException("hilbert order is larger than hilbertRTree hilbert order");
        }

        final List<DirectPosition> listOfCentroidChild = (List<DirectPosition>) hl.getUserProperty("centroids");
        final CoordinateReferenceSystem crs = listOfCentroidChild.get(0).getCoordinateReferenceSystem();
        final List<DirectPosition> lPTemp2 = new ArrayList<DirectPosition>(listOfCentroidChild);
        final GeneralEnvelope bound = hl.getBound();
        final DirectPosition centroid = new GeneralDirectPosition(bound.getMedian());
        final double centreX = centroid.getOrdinate(0);
        final double centreY = centroid.getOrdinate(1);
        final double width = bound.getSpan(0);
        final double height = bound.getSpan(1);
        final double quartWidth = (width > 1) ? width / 4 : 1;
        final double quartHeight = (height > 1) ? height / 4 : 1;
        listOfCentroidChild.clear();
        final AffineTransform mt1 = new AffineTransform(1, 0, 0, 1, -centreX, -centreY);
        final AffineTransform rot1 = new AffineTransform();
        final AffineTransform mt21 = new AffineTransform(1 / quartWidth, 0, 0, 1 / quartHeight, 0, 0);
        final AffineTransform mt22 = new AffineTransform(quartWidth, 0, 0, quartHeight, 0, 0);
        final AffineTransform mt2 = new AffineTransform();
        final AffineTransform mt3 = new AffineTransform(1 / 2.0, 0, 0, 1 / 2.0, 0, 0);

        for (int i = 0; i < 4; i++) {

            if (i == 0) {
                rot1.setToRotation(-Math.PI / 2);
                mt2.setTransform(1, 0, 0, 1, centreX - quartWidth, centreY - quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt22);
                mt2.concatenate(rot1);
                mt2.concatenate(mt21);
                mt2.concatenate(mt1);
                Collections.reverse(lPTemp2);
            } else if (i == 1) {

                mt2.setTransform(1, 0, 0, 1, centreX - quartWidth, centreY + quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt1);
                Collections.reverse(lPTemp2);

            } else if (i == 2) {
                mt2.setTransform(1, 0, 0, 1, centreX + quartWidth, centreY + quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt1);
            } else if (i == 3) {
                Collections.reverse(lPTemp2);
                rot1.setToRotation(Math.PI / 2);
                mt2.setTransform(1, 0, 0, 1, centreX + quartWidth, centreY - quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt22);
                mt2.concatenate(rot1);
                mt2.concatenate(mt21);
                mt2.concatenate(mt1);
            }

            for (DirectPosition pt : lPTemp2) {
                final AffineTransform2D mt = new AffineTransform2D(mt2);
                DirectPosition ptt = mt.transform(pt, null);
                DirectPosition ptt2 = new GeneralDirectPosition(crs);
                for (int t = 0; t < ptt.getDimension(); t++) {
                    ptt2.setOrdinate(t, ptt.getOrdinate(t));
                }
                listOfCentroidChild.add(ptt2);
            }
        }
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
    public static int[] getHilbCoord(DefaultNode candidate, final DirectPosition dPt, final GeneralEnvelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!envelop.contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        double div = Math.pow(2, hilbertOrder);
        final double divX = envelop.getSpan(0) / div;
        final double divY = envelop.getSpan(1) / div;
        double hdx = (Math.abs(dPt.getOrdinate(0) - envelop.getLower(0)) / divX);
        double hdy = (Math.abs(dPt.getOrdinate(1) - envelop.getLower(1)) / divY);
        final int hx = (hdx <= 1) ? 0 : 1;
        final int hy = (hdy <= 1) ? 0 : 1;
        return new int[]{hx, hy};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getHVOfEntry(final DefaultNode candidate, final GeneralEnvelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE = entry.getMedian();
        if (! candidate.getBoundary().contains(ptCE)) {////////// attention
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        final GeneralEnvelope bound = candidate.getBound();
        final Calculator calc = candidate.getTree().getCalculator();
        final int order = (Integer) candidate.getUserProperty("hilbertOrder");
        if (calc.getSpace(candidate.getBoundary()) <= 0) {
            final double w = bound.getSpan(0);
            final double h = bound.getSpan(1);
            final int ordinate = (w > h) ? 0 : 1;
            final int nbCells = (int) (Math.pow(2, 2 * order));
            final double fract = bound.getSpan(ordinate) / nbCells;
            final double lenght = Math.abs(bound.getLower(ordinate) - ptCE.getOrdinate(ordinate));
            int result = (int) (lenght / fract);
            if (result == nbCells) {
                result--;
            }
            return result;
        } else {
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]];
        }
    }
}
