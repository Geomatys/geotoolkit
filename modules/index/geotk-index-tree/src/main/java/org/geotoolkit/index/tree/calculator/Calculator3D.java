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

import java.util.*;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.hilbert.Hilbert;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.Matrix4;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Define a three dimension {@code Calculator}.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Calculator3D extends Calculator {
    private final static double PI_2 = Math.PI/2;
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

    private static Matrix4 getRotate_X(final double theta){
        final double cost = Math.cos(theta);
        final double sint = Math.sin(theta);
        return new Matrix4(1, 0, 0, 0, 0, cost, -sint, 0, 0, sint, cost, 0, 0, 0, 0, 1);
    }

    private static Matrix4 getRotate_Y(final double theta){
        final double cost = Math.cos(theta);
        final double sint = Math.sin(theta);
        return new Matrix4(cost, 0, sint, 0, 0, 1, 0, 0, -sint, 0, cost, 0, 0, 0, 0, 1);
    }

    private static Matrix4 getRotate_Z(final double theta){
        final double cost = Math.cos(theta);
        final double sint = Math.sin(theta);
        return new Matrix4(cost, -sint, 0, 0, sint, cost, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void createBasicHL(final Node candidate, final int order, final Envelope bound) throws MismatchedDimensionException, TransformException{
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
            final double length_X = bound.getSpan(0);
            final double length_Y = bound.getSpan(1);
            final double length_Z = bound.getSpan(2);
            int dim = (int) Math.pow(2, (Integer) candidate.getUserProperty("hilbertOrder"));

            final DirectPosition dp0 = new GeneralDirectPosition(crs);
            final DirectPosition dp1 = new GeneralDirectPosition(crs);
            final DirectPosition dp2 = new GeneralDirectPosition(crs);
            final DirectPosition dp3 = new GeneralDirectPosition(crs);
            final DirectPosition dp4 = new GeneralDirectPosition(crs);
            final DirectPosition dp5 = new GeneralDirectPosition(crs);
            final DirectPosition dp6 = new GeneralDirectPosition(crs);
            final DirectPosition dp7 = new GeneralDirectPosition(crs);

            final double dX = length_X / 4;
            final double dY = length_Y / 4;
            final double dZ = length_Z / 4;
            final double minx = bound.getLowerCorner().getOrdinate(0) + dX;
            final double maxx = bound.getUpperCorner().getOrdinate(0) - dX;
            final double miny = bound.getLowerCorner().getOrdinate(1) + dY;
            final double maxy = bound.getUpperCorner().getOrdinate(1) - dY;
            final double minz = bound.getLowerCorner().getOrdinate(2) + dZ;
            final double maxz = bound.getUpperCorner().getOrdinate(2) - dZ;

            if (getSpace(bound) <= 0) {
                final int nbCells2D = ((int) Math.pow(2, 2 * order));
                if(getEdge(bound)<= 0){
                    int index = -1;
                    for(int i = 0; i<3; i++){
                        if(bound.getSpan(i) > 0)index = i;break;
                    }

                    final double fract = bound.getSpan(index)/(2*nbCells2D);
                    final double valMin = bound.getLowerCorner().getOrdinate(index);
                    final DirectPosition dpt = new GeneralDirectPosition(crs);
                    for(int i = 1; i<2*nbCells2D; i+= 2){
                        for(int j = 0; j<bound.getDimension(); j++){
                            if(j!=index)dpt.setOrdinate(j, bound.getMedian(j));
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
                    for(int i = 0; i<3; i++){
                        if(bound.getSpan(i)<=0){
                            index = i;
                        }
                    }
                    int[][] tabHV = new int[dim][dim];
                    int  d0, d1;
                    switch(index){
                        case 0 : d0 = 1;d1 = 2; break;//defined on yz plan
                        case 1 : d0 = 0;d1 = 2; break;//defined on xz
                        case 2 : d0 = 0; d1 = 1;break;//defined on xy
                        default : throw new IllegalStateException("invalid no space index : "+index);
                    }
                    listOfCentroidChild.addAll(createPath(candidate, order, d0, d1));//////
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
//                //pt0
//                dp0.setOrdinate(0, minx);
//                dp0.setOrdinate(1, maxy);
//                dp0.setOrdinate(2, maxz);
////                pt1
//                dp1.setOrdinate(0, minx);
//                dp1.setOrdinate(1, maxy);
//                dp1.setOrdinate(2, minz);
////                pt2
//                dp2.setOrdinate(0, minx);
//                dp2.setOrdinate(1, miny);
//                dp2.setOrdinate(2, minz);
////                pt3
//                dp3.setOrdinate(0, minx);
//                dp3.setOrdinate(1, miny);
//                dp3.setOrdinate(2, maxz);
////                pt4
//                dp4.setOrdinate(0, maxx);
//                dp4.setOrdinate(1, miny);
//                dp4.setOrdinate(2, maxz);
////                pt5
//                dp5.setOrdinate(0, maxx);
//                dp5.setOrdinate(1, miny);
//                dp5.setOrdinate(2, minz);
////                pt6
//                dp6.setOrdinate(0, maxx);
//                dp6.setOrdinate(1, maxy);
//                dp6.setOrdinate(2, minz);
////                pt7
//                dp7.setOrdinate(0, maxx);
//                dp7.setOrdinate(1, maxy);
//                dp7.setOrdinate(2, maxz);
//                listOfCentroidChild.addAll(UnmodifiableArrayList.wrap(dp2, dp3, dp0, dp1, dp6, dp7, dp4, dp5));
//////
//                if (order > 1) {
//                    for (int i = 1; i < order; i++) {
//                            createHB(candidate);
//                    }
//                }
//                if(order==2){
//                    System.out.println("");
//                }

                listOfCentroidChild.addAll(createPath(candidate, order, 0, 1,2));

                //////////////////////////dans le but de faire des verifs ////////////////////
//                if(order==3){
//                    Hilbert hi = new Hilbert(3, new int[12]);
//
//                    int[]base = hi.generateBasicPath(2, new int[]{2,1,2});
//                    System.out.println("generic base dim = "+Arrays.toString(base));
////                    int[] resultbase = hi.iterateDimPath(base, order);
//                    int[] resultbase = new int[(2<<3*order-1)];
//                    boolean[] bool = hi.generateBasicSign(2, new boolean[]{true, true, false});
////                    boolean[] resultbool = hi.procesSignPath3D(bool, order);
//                    boolean[]resultbool = new boolean[(2<<3*order-1)];
//
//                    hi.iterateDimPathBis(base,order, resultbase, resultbool);
//                    String stbool = "";
//
//                    System.out.println("avec hilbert");
//
//                    for(int i = 0;i<resultbool.length;i++){
//                        if(i%64==0){
//                            System.out.println(stbool);
//                            stbool = "";
//                        }
//                        stbool=(resultbool[i])?stbool+"+, ":stbool+"-, ";
//                    }
//                    System.out.println(stbool);
//
//                    String stbase = "";
//                    for(int i :resultbase){
//                        stbase+=i+", ";
//                    }
//                    int compteurPath = 0;
//                    int[]thePath = new int[listOfCentroidChild.size()];
//                    boolean[]theSign = new boolean[listOfCentroidChild.size()];
//                    for(int i = 0;i<listOfCentroidChild.size()-1;i++){
//                        final DirectPosition dpfirst = listOfCentroidChild.get(i);
//                        final DirectPosition dpsec = listOfCentroidChild.get(i+1);
//
//                        for(int j = 0;j<bound.getDimension();j++){
//                            if(Math.abs(dpfirst.getOrdinate(j)-dpsec.getOrdinate(j))>1E-8){
//                                thePath[compteurPath] = j;
//                                double cursi = Math.signum(dpsec.getOrdinate(j)-dpfirst.getOrdinate(j));
//                                boolean curbool = (cursi>0)?true:false;
//                                theSign[compteurPath] = curbool;
//                                compteurPath++;
//                            }
//                        }
//                    }
//
//                    String strpath = "";
//                    String strbool = "";
//                    System.out.println("avec matrice");
//                    for(int i = 0;i<thePath.length;i++){
//                        if(i%64==0){
//                            System.out.println(strbool);
//                            strbool = "";
//                        }
//                        strpath+=thePath[i]+", ";
//                        strbool=(theSign[i])?strbool+"+, ":strbool+"-, ";
//                    }
//                    System.out.println(strbool);
//
//                    System.out.println("length ref = "+thePath.length);
//                    System.out.println("length cur = "+resultbase.length);
////
//                    System.out.println("tab ref = ["+strpath+"]");//patern des coordonnées OK !!!!! ;-)))
//                    System.out.println("tab cur = ["+stbase+"]");
//
////                    System.out.println("bool ref : ["+strbool+"]");
////                    System.out.println("bool cur : ["+stbool+"]");
//
//                }
                //////////////////////////////////////fin verif/////////////////////////////////

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
    public static int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        assert calc instanceof Calculator3D : "getHilbertCoord : calculator3D type required";
        final double div = Math.pow(2, hilbertOrder);
        final double divX = envelop.getSpan(0) / div;
        final double divY = envelop.getSpan(1) / div;
        final double divZ = envelop.getSpan(2) / div;
        double hdx = (Math.abs(dPt.getOrdinate(0) - envelop.getLowerCorner().getOrdinate(0)) / divX);
        double hdy = (Math.abs(dPt.getOrdinate(1) - envelop.getLowerCorner().getOrdinate(1)) / divY);
        double hdz = (Math.abs(dPt.getOrdinate(2) - envelop.getLowerCorner().getOrdinate(2)) / divZ);
        final int hx = (hdx <= 1) ? 0 : 1;
        final int hy = (hdy <= 1) ? 0 : 1;
        final int hz = (hdz <= 1) ? 0 : 1;

        if(calc.getSpace(envelop) <= 0){
            int index = -1;
            for(int i = 0; i<3; i++){
                if(envelop.getSpan(i)<=0){
                    index = i;
                }
            }
            switch(index){
                case 0 : return new int[]{hy, hz};
                case 1 : return new int[]{hx, hz};
                case 2 : return new int[]{hx, hy};
                default : throw new IllegalStateException("hilbertCoord not find");
            }
        }else{
            return new int[]{hx, hy, hz};
        }
    }

    private static List<DirectPosition> createPath(final Node hl, final int ordre, final int ...dims) {
        final Envelope bound = hl.getBound();
        final DirectPosition median = getMedian(bound);
        final int spaceDimension = dims.length;
        final List<DirectPosition> path = new ArrayList<DirectPosition>();
        final int[] generalPath = Hilbert.createPath(spaceDimension, ordre);
        final double[] spans = new double[spaceDimension];
        for(int i = 0; i<spaceDimension;i++){
            spans[i] = bound.getSpan(dims[i])/(2<<(ordre-1));
        }
        final double[] coords = new double[spaceDimension];
        for(int i = 0;i<spaceDimension;i++){
            coords[i] = bound.getMinimum(dims[i]) + spans[i]/2;
        }
        for(int i = 0,l=generalPath.length; i<=l-spaceDimension; i+=spaceDimension){
            final DirectPosition dptemp = new GeneralDirectPosition(median);
            for(int j = 0;j<spaceDimension;j++){
                dptemp.setOrdinate(dims[j], coords[j]+spans[j]*generalPath[i+j]);
            }
            path.add(dptemp);
        }
        return path;
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
    private static void createHB(final Node hl) throws MismatchedDimensionException, TransformException {

        ArgumentChecks.ensureNonNull("impossible to increase hilbert order", hl);
        if ((Integer) hl.getUserProperty("hilbertOrder") > ((HilbertRTree) hl.getTree()).getHilbertOrder()) {
            throw new IllegalArgumentException("hilbert order is larger than hilbertRTree hilbert order");
        }

        final List<DirectPosition> listOfCentroidChild = (List<DirectPosition>) hl.getUserProperty("centroids");
        final List<DirectPosition> lPTemp2 = new ArrayList<DirectPosition>(listOfCentroidChild);
        final Envelope bound = hl.getBound();
        final CoordinateReferenceSystem crs = bound.getCoordinateReferenceSystem();

        //centre du noeud
        final DirectPosition centroid = new GeneralDirectPosition(getMedian(bound));
        final double centreX = centroid.getOrdinate(0);
        final double centreY = centroid.getOrdinate(1);
        final double centreZ = centroid.getOrdinate(2);

        final double length_X = bound.getSpan(0);
        final double length_Y = bound.getSpan(1);
        final double length_Z = bound.getSpan(2);

        final double quart_X = (length_X > 1) ? length_X / 4 : 1;
        final double quart_Y = (length_Y > 1) ? length_Y / 4 : 1;
        final double quart_Z = (length_Z > 1) ? length_Z / 4 : 1;
        listOfCentroidChild.clear();

        //on ramène o centre
        final Matrix4 mtr1 = new Matrix4(1, 0, 0, -centreX, 0, 1, 0, -centreY, 0, 0, 1, -centreZ, 0, 0, 0, 1);
        //on divise coordonnéées par 2
        final Matrix4 demId = new Matrix4(1/2.0, 0, 0, 0, 0, 1/2.0, 0, 0, 0, 0, 1/2.0, 0, 0, 0, 0, 1);
        //on normalise entre ""
        final Matrix4 mtnorm = new Matrix4(1/quart_X, 0, 0, 0, 0, 1/quart_Y, 0, 0, 0, 0, 1/quart_Z, 0, 0, 0, 0, 1);
        //on reformate apres rotation
        final Matrix4 mtform = new Matrix4(quart_X, 0, 0, 0, 0, quart_Y, 0, 0, 0, 0, quart_Z, 0, 0, 0, 0, 1);

        final Matrix4 mtGlob = new Matrix4();
        final Matrix4 mtRot = new Matrix4();

        for (int i = 0; i < 8; i++) {

            switch(i){
                case 0 :{
                    mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X,
                                           0, 1, 0, centreY - quart_Y,
                                           0, 0, 1, centreZ - quart_Z,
                                           0, 0, 0, 1));
                    mtRot.set(getRotate_Z(-PI_2));
                    mtRot.multiply(getRotate_Y(-PI_2));
                }break;

                case 1 : case 2 :{
                    if(i == 1){
                        mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X,
                                               0, 1, 0, centreY - quart_Y,
                                               0, 0, 1, centreZ + quart_Z,
                                               0, 0, 0, 1));
                    }else{
                        mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X,
                                               0, 1, 0, centreY + quart_Y,
                                               0, 0, 1, centreZ + quart_Z,
                                               0, 0, 0, 1));
                    }
                    mtRot.set(getRotate_Y(PI_2));
                    mtRot.multiply(getRotate_Z(PI_2));
                }break;
                case 3 : case 4 :{

                    if(i == 3){
                        mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X,
                                               0, 1, 0, centreY + quart_Y,
                                               0, 0, 1, centreZ - quart_Z,
                                               0, 0, 0, 1));
                    }else{
                        mtGlob.set(new Matrix4(1, 0, 0, centreX + quart_X,
                                               0, 1, 0, centreY + quart_Y,
                                               0, 0, 1, centreZ - quart_Z,
                                               0, 0, 0, 1));
                    }
                    mtRot.set(getRotate_X(Math.PI));
                }break;
                case 5 : case 6 : {
                    if(i == 5){
                        mtGlob.set(new Matrix4(1, 0, 0, centreX + quart_X,
                                               0, 1, 0, centreY + quart_Y,
                                               0, 0, 1, centreZ + quart_Z,
                                               0, 0, 0, 1));
                    }else{
                        mtGlob.set(new Matrix4(1, 0, 0, centreX + quart_X,
                                               0, 1, 0, centreY - quart_Y,
                                               0, 0, 1, centreZ + quart_Z,
                                               0, 0, 0, 1));
                    }
                    mtRot.set(getRotate_Y(-PI_2));
                    mtRot.multiply(getRotate_Z(-PI_2));
                }break;
                case 7 :{

                    mtGlob.set(new Matrix4(1, 0, 0, centreX + quart_X,
                                           0, 1, 0, centreY - quart_Y,
                                           0, 0, 1, centreZ - quart_Z,
                                           0, 0, 0, 1));
                    mtRot.set(getRotate_X(-PI_2));
                    mtRot.multiply(getRotate_Z(PI_2));
                }break;
                default : throw new IllegalStateException("createHB crash ");
            }

            mtGlob.multiply(demId);
            mtGlob.multiply(mtform);
            mtGlob.multiply(mtRot);
            mtGlob.multiply(mtnorm);
            mtGlob.multiply(mtr1);

            final List<DirectPosition> ldpResult = new ArrayList<DirectPosition>();
            for(DirectPosition dpp : lPTemp2){
                ldpResult.add(new GeneralDirectPosition(dpp));
            }
            final MathTransform mtFiGlob = MathTransforms.linear(mtGlob);
            for (DirectPosition pt : ldpResult) {
                DirectPosition ptt = mtFiGlob.transform(pt, pt);
//                DirectPosition ptt2 = new GeneralDirectPosition(crs);
//                for (int t = 0; t < ptt.getDimension(); t++) {
//                    ptt2.setOrdinate(t, ptt.getOrdinate(t));
//                }
//                ldpResult.add(ptt2);
            }

//            switch(i){
//                  case 7 : Collections.reverse(ldpResult);
//            }
            listOfCentroidChild.addAll(ldpResult);
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
        if (! bound.contains(ptCE)) {
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        if(getSpace(bound)<= 0){
            if(getEdge(bound)<=0){
                final int nbCells = (int) (Math.pow(2, 2 * order));
                int index = -1;
                for(int i = 0, d = bound.getDimension();i<d;i++){
                    if(bound.getSpan(i)>0){
                        index = i;
                        break;
                    }
                }
                final double fract = bound.getSpan(index) / nbCells;
                final double lenght = Math.abs(bound.getLowerCorner().getOrdinate(index) - ptCE.getOrdinate(index));
                int result = (int) (lenght / fract);
                if (result == nbCells) {
                    result--;
                }
                return result;
            }
            int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
            return ((int[][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]];

        }
        int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
        return ((int[][][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]][hCoord[2]];
    }

    private static Matrix4 getRotate(int axisRotateIndex, double theta){
        switch(axisRotateIndex){
            case 0 : return getRotate_X(theta);
            case 1 : return getRotate_Y(theta);
            case 2 : return getRotate_Z(theta);
            default : throw new IllegalArgumentException("invalid index"+axisRotateIndex);
        }
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
    private static void createHB2D(final Node hl) throws IllegalArgumentException, TransformException {

        ArgumentChecks.ensureNonNull("impossible to increase hilbert order", hl);
        if ((Integer) hl.getUserProperty("hilbertOrder") > ((HilbertRTree) hl.getTree()).getHilbertOrder()) {
            throw new IllegalArgumentException("hilbert order is larger than hilbertRTree hilbert order");
        }

        final List<DirectPosition> listOfCentroidChild = (List<DirectPosition>) hl.getUserProperty("centroids");
        final List<DirectPosition> lPTemp2 = new ArrayList<DirectPosition>(listOfCentroidChild);
        final Envelope bound = hl.getBound();
        final CoordinateReferenceSystem crs = bound.getCoordinateReferenceSystem();

        //centre du noeud
        final DirectPosition centroid = new GeneralDirectPosition(getMedian(bound));
        final double centreX = centroid.getOrdinate(0);
        final double centreY = centroid.getOrdinate(1);
        final double centreZ = centroid.getOrdinate(2);

        int index = -1;
        for(int i = 0, d = bound.getDimension(); i<d; i++){
            if(bound.getSpan(i)==0){
                index = i;
                break;
            }
        }
        if(index == -1){
            throw new IllegalArgumentException(" createHL2D : invalid index");
        }


        final double length_X = bound.getSpan(0);
        final double length_Y = bound.getSpan(1);
        final double length_Z = bound.getSpan(2);

        final double quart_X = (length_X > 1) ? length_X / 4 : 1;
        final double quart_Y = (length_Y > 1) ? length_Y / 4 : 1;
        final double quart_Z = (length_Z > 1) ? length_Z / 4 : 1;
        listOfCentroidChild.clear();

        //on ramène o centre
        final Matrix4 mtr1 = new Matrix4(1, 0, 0, -centreX, 0, 1, 0, -centreY, 0, 0, 1, -centreZ, 0, 0, 0, 1);
        //on divise coordonnéées par 2
        final Matrix4 demId = new Matrix4(1/2.0, 0, 0, 0, 0, 1/2.0, 0, 0, 0, 0, 1/2.0, 0, 0, 0, 0, 1);
        //on normalise entre ""
        final Matrix4 mtnorm = new Matrix4(1/quart_X, 0, 0, 0, 0, 1/quart_Y, 0, 0, 0, 0, 1/quart_Z, 0, 0, 0, 0, 1);
        //on reformate apres rotation
        final Matrix4 mtform = new Matrix4(quart_X, 0, 0, 0, 0, quart_Y, 0, 0, 0, 0, quart_Z, 0, 0, 0, 0, 1);

        final Matrix4 mtGlob = new Matrix4();
        final Matrix4 mtRot = new Matrix4();

        final int dx = 0;
        final int dy = 1;



        for (int i = 0; i < 4; i++) {
            mtGlob.setIdentity();
            mtRot.setIdentity();
            switch(i) {
                case 0 :{
                    mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X, //00
                                           0, 1, 0, centreY + quart_Y,
                                           0, 0, 1, centreZ + quart_Z,
                                           0, 0, 0, 1));
                    mtRot.set(getRotate(index, -PI_2));
                }break;

                case 1 :{
                    mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X, //01
                                           0, 1, 0, centreY + quart_Y,
                                           0, 0, 1, centreZ - quart_Z,
                                           0, 0, 0, 1));
                }break;
                case 2 :{
                    mtGlob.set(new Matrix4(1, 0, 0, centreX - quart_X, //11
                                           0, 1, 0, centreY + quart_Y,
                                           0, 0, 1, centreZ - quart_Z,
                                           0, 0, 0, 1));
                }break;
                case 3 :{
                    mtGlob.set(new Matrix4(1, 0, 0, centreX + quart_X, //10
                                           0, 1, 0, centreY - quart_Y,
                                           0, 0, 1, centreZ + quart_Z,
                                           0, 0, 0, 1));
                    mtRot.set(getRotate(index, PI_2));
                }break;
                default : throw new IllegalStateException("createHB crash ");
            }

            mtGlob.multiply(demId);
            mtGlob.multiply(mtform);
            mtGlob.multiply(mtRot);
            mtGlob.multiply(mtnorm);
            mtGlob.multiply(mtr1);

            final List<DirectPosition> ldpResult = new ArrayList<DirectPosition>();
            final MathTransform mtFiGlob = MathTransforms.linear(mtGlob);
            for (DirectPosition pt : lPTemp2) {
                DirectPosition ptt = mtFiGlob.transform(pt, null);
                DirectPosition ptt2 = new GeneralDirectPosition(crs);
                for (int t = 0; t < ptt.getDimension(); t++) {
                    ptt2.setOrdinate(t, ptt.getOrdinate(t));
                }
                ldpResult.add(ptt2);
            }

            switch(i){
                case 0 : case 3 : Collections.reverse(ldpResult);
            }
            listOfCentroidChild.addAll(ldpResult);
        }
    }
}
