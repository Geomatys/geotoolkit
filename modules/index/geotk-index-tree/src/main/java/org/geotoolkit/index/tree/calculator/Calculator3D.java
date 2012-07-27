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

import java.util.Comparator;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
            final double x1 = o1.getBoundary().getLowerCorner().getOrdinate(dims[0]);
            final double x2 = o2.getBoundary().getLowerCorner().getOrdinate(dims[0]);
            return Double.compare(x1, x2);
        }
    };

    /**
     * To compare two {@code Node} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Node> nodeComparatorYLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double y1 = o1.getBoundary().getLowerCorner().getOrdinate(dims[1]);
            final double y2 = o2.getBoundary().getLowerCorner().getOrdinate(dims[1]);
            return Double.compare(y1, y2);
        }
    };

    /**
     * To compare two {@code Node} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Node> nodeComparatorZLow = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double z1 = o1.getBoundary().getLowerCorner().getOrdinate(2);
            final double z2 = o2.getBoundary().getLowerCorner().getOrdinate(2);
            return Double.compare(z1, z2);
        }
    };

    /**
     * To compare two {@code Envelope} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorXLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double x1 = o1.getLowerCorner().getOrdinate(dims[0]);
            final double x2 = o2.getLowerCorner().getOrdinate(dims[0]);
            return Double.compare(x1, x2);
        }
    };

    /**
     * To compare two {@code Envelope} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorYLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double y1 = o1.getLowerCorner().getOrdinate(dims[1]);
            final double y2 = o2.getLowerCorner().getOrdinate(dims[1]);
            return Double.compare(y1, y2);
        }
    };

    /**
     * To compare two {@code Envelope} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorZLow = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double z1 = o1.getLowerCorner().getOrdinate(dims[2]);
            final double z2 = o2.getLowerCorner().getOrdinate(dims[2]);
            return Double.compare(z1, z2);
        }
    };

    /**
     * To compare two {@code Node} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Node> nodeComparatorXUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double x1 = o1.getBoundary().getUpperCorner().getOrdinate(dims[0]);
            final double x2 = o2.getBoundary().getUpperCorner().getOrdinate(dims[0]);
            return Double.compare(x1, x2);
        }
    };

    /**
     * To compare two {@code Node} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Node> nodeComparatorYUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double y1 = o1.getBoundary().getUpperCorner().getOrdinate(dims[1]);
            final double y2 = o2.getBoundary().getUpperCorner().getOrdinate(dims[1]);
            return Double.compare(y1, y2);
        }
    };

    /**
     * To compare two {@code Node} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Node> nodeComparatorZUpp = new Comparator<Node>() {

        @Override
        public int compare(Node o1, Node o2) {
            final double z1 = o1.getBoundary().getUpperCorner().getOrdinate(dims[2]);
            final double z2 = o2.getBoundary().getUpperCorner().getOrdinate(dims[2]);
            return Double.compare(z1, z2);
        }
    };

    /**
     * To compare two {@code Envelope} from them boundary box minimum x axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorXUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double x1 = o1.getUpperCorner().getOrdinate(dims[0]);
            final double x2 = o2.getUpperCorner().getOrdinate(dims[0]);
            return Double.compare(x1, x2);
        }
    };

    /**
     * To compare two {@code Envelope} from them boundary box minimum y axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorYUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double y1 = o1.getUpperCorner().getOrdinate(dims[1]);
            final double y2 = o2.getUpperCorner().getOrdinate(dims[1]);
            return Double.compare(y1, y2);
        }
    };

    /**
     * To compare two {@code Envelope} from them boundary box minimum z axis coordinate.
     */
    private final Comparator<Envelope> gEComparatorZUpp = new Comparator<Envelope>() {

        @Override
        public int compare(Envelope o1, Envelope o2) {
            final double z1 = o1.getUpperCorner().getOrdinate(dims[2]);
            final double z2 = o2.getUpperCorner().getOrdinate(dims[2]);
            return Double.compare(z1, z2);
        }
    };

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
            for(int j = i+1; j<l; j++) {
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
     * Comparator for 3D space axis. {@inheritDoc}
     */
    @Override
    public Comparator sortFrom(final int index, final boolean lowerOrUpper, final boolean nodeOrGE) {
        ArgumentChecks.ensureBetween("sortFrom : index ", 0, 2, index);
        if (lowerOrUpper) {
            if (nodeOrGE) {
                switch (index) {
                    case 0 : return nodeComparatorXLow;
                    case 1 : return nodeComparatorYLow;
                    case 2 : return nodeComparatorZLow;
                    default: throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0 : return gEComparatorXLow;
                    case 1 : return gEComparatorYLow;
                    case 2 : return gEComparatorZLow;
                    default: throw new IllegalStateException("no comparator finded");
                }
            }
        } else {
            if (nodeOrGE) {
                switch (index) {
                    case 0 : return nodeComparatorXUpp;
                    case 1 : return nodeComparatorYUpp;
                    case 2 : return nodeComparatorZUpp;
                    default: throw new IllegalStateException("no comparator finded");
                }
            } else {
                switch (index) {
                    case 0 : return gEComparatorXUpp;
                    case 1 : return gEComparatorYUpp;
                    case 2 : return gEComparatorZUpp;
                    default: throw new IllegalStateException("no comparator finded");
                }
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) throws MismatchedDimensionException {
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
            final int dim = 2<<((Integer) candidate.getUserProperty("hilbertOrder"))-1;
            if (getSpace(bound) <= 0) {
                final int nbCells2D = 2<<(2*order-1);
                if (getEdge(bound) <= 0) {
                    int index = -1;
                    for(int i = 0; i<3; i++) {
                        if(bound.getSpan(dims[i]) > 0) {
                            index = dims[i];break;
                        }
                    }
                    final double fract = bound.getSpan(index)/(2*nbCells2D);
                    final double valMin = bound.getLowerCorner().getOrdinate(index);
                    final DirectPosition dpt = new GeneralDirectPosition(crs);
                    for(int i = 1; i<2*nbCells2D; i+= 2) {
                        for(int j = 0, spacedim = bound.getDimension(); j<spacedim; j++) {
                            if (j!=index) dpt.setOrdinate(j, bound.getMedian(j));
                        }
                        dpt.setOrdinate(index, valMin + i * fract);
                        listOfCentroidChild.add(dpt);
                    }
                    int[] groundZero = new int[nbCells2D];
                    for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                        groundZero[i] = i;
                        listN.add(HilbertRTree.createCell(candidate.getTree(), candidate, listOfCentroidChild.get(i), i, null));
                    }
                    candidate.setUserProperty("tabHV", groundZero);
                }else{
                    int index = -1;
                    for(int i = 0; i<3; i++) {
                        if(bound.getSpan(dims[i]) <= 0) {
                            index = i;break;
                        }
                    }
                    int[][] tabHV = new int[dim][dim];
                    int  d0, d1;
                    switch(index){
                        case 0  : d0 = 1; d1 = 2; break;//defined on yz plan
                        case 1  : d0 = 0; d1 = 2; break;//defined on xz
                        case 2  : d0 = 0; d1 = 1; break;//defined on xy
                        default : throw new IllegalStateException("invalid no space index : "+index);
                    }
                    listOfCentroidChild.addAll(createPath(candidate, order, dims[d0], dims[d1]));
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

                int[][][] tabHV = new int[dim][dim][dim];
                listOfCentroidChild.addAll(createPath(candidate, order, dims[0], dims[1],dims[2]));
                for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                    final DirectPosition ptCTemp = listOfCentroidChild.get(i);
                    ArgumentChecks.ensureNonNull("the crs ptCTemp", ptCTemp.getCoordinateReferenceSystem());
                    int[] tabTemp = getHilbCoord(candidate, ptCTemp, bound, order);
                    tabHV[tabTemp[0]][tabTemp[1]][tabTemp[2]] = i;
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
     * @return int[] table of length 3 which contains 3 coordinates.
     */
    public int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        assert calc instanceof Calculator3D : "getHilbertCoord : calculator3D type required";
        final double div = 2<<hilbertOrder-1;
        final double divX = envelop.getSpan(dims[0]) / div;
        final double divY = envelop.getSpan(dims[1]) / div;
        final double divZ = envelop.getSpan(dims[2]) / div;
        double hdx = (Math.abs(dPt.getOrdinate(dims[0]) - envelop.getLowerCorner().getOrdinate(dims[0])) / divX);
        double hdy = (Math.abs(dPt.getOrdinate(dims[1]) - envelop.getLowerCorner().getOrdinate(dims[1])) / divY);
        double hdz = (Math.abs(dPt.getOrdinate(dims[2]) - envelop.getLowerCorner().getOrdinate(dims[2])) / divZ);
        final int hx = (hdx <= 1) ? 0 : 1;
        final int hy = (hdy <= 1) ? 0 : 1;
        final int hz = (hdz <= 1) ? 0 : 1;

        if (calc.getSpace(envelop) <= 0) {
            int index = -1;
            for(int i = 0; i<3; i++) {
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
        final int order = (Integer) candidate.getUserProperty("hilbertOrder");
        if (! bound.contains(ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");
        if (getSpace(bound) <= 0) {
            if (getEdge(bound) <= 0) {
                final int nbCells = 2 << 2*order-1;
                int index = -1;
                for(int i = 0, d = dims.length; i<d; i++) {
                    if(bound.getSpan(dims[i]) > 0) {
                        index = dims[i];
                        break;
                    }
                }
                final double fract = bound.getSpan(index) / nbCells;
                final double lenght = Math.abs(bound.getLowerCorner().getOrdinate(index) - ptCE.getOrdinate(index));
                int result = (int) (lenght / fract);
                if (result == nbCells) result--;
                return result;
            }
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]];
        }
        int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
        return ((int[][][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]][hCoord[2]];
    }
}
