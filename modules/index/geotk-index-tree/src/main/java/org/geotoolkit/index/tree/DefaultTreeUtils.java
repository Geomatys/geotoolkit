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
package org.geotoolkit.index.tree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.math.XMath;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;

/**Some utils methods.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class DefaultTreeUtils {

    private DefaultTreeUtils() {
    }
    
    /**To compare two {@code DefaultNode} from them boundary box minimum x axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<DefaultNode> NODE3D_COMPARATOR_X_LEFT = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getLower(0));
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getLower(0));
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code DefaultNode} from them boundary box minimum y axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<DefaultNode> NODE3D_COMPARATOR_Y_LEFT = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLower(1));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLower(1));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum z axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<DefaultNode> NODE3D_COMPARATOR_Z_LEFT = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getLower(2));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getLower(2));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum x axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<GeneralEnvelope> GE_COMPARATOR_X_LEFT = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getLower(0));
            java.lang.Double x2 = new java.lang.Double(o2.getLower(0));
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum y axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<GeneralEnvelope> GE_COMPARATOR_Y_LEFT = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getLower(1));
            java.lang.Double y2 = new java.lang.Double(o2.getLower(1));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum z axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<GeneralEnvelope> GE_COMPARATOR_Z_LEFT = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getLower(2));
            java.lang.Double y2 = new java.lang.Double(o2.getLower(2));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code Node3D} from them boundary box minimum x axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<DefaultNode> NODE3D_COMPARATOR_X_RIGHT = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getUpper(0));
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getUpper(0));
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code Node3D} from them boundary box minimum y axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<DefaultNode> NODE3D_COMPARATOR_Y_RIGHT = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpper(1));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpper(1));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum z axis coordinate.
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<DefaultNode> NODE3D_COMPARATOR_Z_RIGHT = new Comparator<DefaultNode>() {

        @Override
        public int compare(DefaultNode o1, DefaultNode o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getUpper(2));
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getUpper(2));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum x axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<GeneralEnvelope> GE_COMPARATOR_X_RIGHT = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getUpper(0));
            java.lang.Double x2 = new java.lang.Double(o2.getUpper(0));
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum y axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<GeneralEnvelope> GE_COMPARATOR_Y_RIGHT = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpper(1));
            java.lang.Double y2 = new java.lang.Double(o2.getUpper(1));
            return y1.compareTo(y2);
        }
    };
    
    /**To compare two {@code GeneralEnvelope} from them boundary box minimum z axis coordinate. 
     * 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<GeneralEnvelope> GE_COMPARATOR_Z_RIGHT = new Comparator<GeneralEnvelope>() {

        @Override
        public int compare(GeneralEnvelope o1, GeneralEnvelope o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getUpper(2));
            java.lang.Double y2 = new java.lang.Double(o2.getUpper(2));
            return y1.compareTo(y2);
        }
    };
    
    /**
     * @param node to denominate elements number.
     * @return elements number within node.
     */
    public static int countElements(Node node){
        return node.getChildren().size()+node.getEntries().size();
    }
    
    /**Compute {@code GeneralEnvelop} bulk.
     * 
     * @param envelope {@code GeneralEnvelope}.
     * @return bulk value.
     */
    public static double getGeneralEnvelopBulk(final GeneralEnvelope envelope){
        ArgumentChecks.ensureNonNull("getGeneralEnvelopBulk : gn", envelope);
        final DirectPosition lower = envelope.getLowerCorner();
        final DirectPosition upper = envelope.getUpperCorner();
        int dim = envelope.getDimension();
        if(dim<3){
            throw new IllegalArgumentException("getGeneralEnvelopBulk : compute envelop bulk with lesser than three dimensions have no sens");
        }
        if(lower.equals(upper)){
            return 0;
        }
        double bulk = 1;
        for(int i = 0; i<3;i++){
            bulk*=envelope.getSpan(i);
        }
        return bulk;
    }
    
    /**Compute {@code GeneralEnvelop} perimeter.
     * 
     * @param envelope {@code Generale Envelop}.
     * @throws IllegalArgumentException if envelope is null.
     * @throws IllegalArgumentException if envelope dimension > 2.
     * @return perimeter value.
     */
    public static double getGeneralEnvelopPerimeter(final GeneralEnvelope envelope){
        ArgumentChecks.ensureNonNull("getGeneralEnvelopPerimeter : gn", envelope);
        int dim = envelope.getDimension();
        if(dim>2){
            throw new IllegalArgumentException("getGeneralEnvelopPerimeter : compute envelop perimeter with more than two dimension have no sens");
        }
        double perim = 0;
        for(int i = 0, l=envelope.getDimension(); i<l;i++){
            perim+=envelope.getSpan(i);
        }
        return 2*perim;
    }
    
    
    /**Compute overlaps between two {@code GeneralEnvelop}.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: In first time : compute intersection {@code GeneralEnvelope} between envelopA and envelopB.
     *                               - If intersection dimension is 2 compute its area.
     *                               - If intersection dimension is 3 compute its bulk.</strong> 
     * </font></blockquote>
     * 
     * @param envelopA
     * @param envelopB
     * @return intersection between envelopA, envelopB bulk, or area from area dimension.
     */
    public static double getOverlapValue(final GeneralEnvelope envelopA, final GeneralEnvelope envelopB){
        ArgumentChecks.ensureNonNull("getOverlapValue : envelopA", envelopA);
        ArgumentChecks.ensureNonNull("getOverlapValue : envelopB", envelopB);
        final GeneralEnvelope intersectionGN = new GeneralEnvelope(envelopA);
        intersectionGN.intersect(envelopB);
        int dimInter = intersectionGN.getDimension();
        if(dimInter == 2){
            return getGeneralEnvelopArea(intersectionGN);
        }
        return getGeneralEnvelopBulk(intersectionGN);
    }
    
    /** Compute Euclidean distance between two {@code DirectPosition} in dimension n.
     * 
     * @param directPositionA
     * @param directPositionB
     * @throws IllegalArgumentException if directPositionA or directPositionB are null.
     * @throws IllegalArgumentException if directPositionA or directPositionB are not in same dimension.
     * @return distance between directPositionA and directPositionB.
     */
    public static double getDistanceBetween2DirectPosition(final DirectPosition directPositionA, final DirectPosition directPositionB){
        ArgumentChecks.ensureNonNull("getDistanceBetween2DirectPosition : directPositionA", directPositionA);
        ArgumentChecks.ensureNonNull("getDistanceBetween2DirectPosition : directPositionB", directPositionB);
        final int length = directPositionA.getDimension();
        if(length!=directPositionB.getDimension()){
            throw new IllegalArgumentException("getDistanceBetween2DirectPosition : dpA and dpB are not in same dimension");
        }
        final double[] tab = new double[length];
        final double[] dpACoords = directPositionA.getCoordinate();
        final double[] ordinateDB = directPositionB.getCoordinate();
        for(int i =0; i<length; i++){
            tab[i] = dpACoords[i]-ordinateDB[i];
        }
        return XMath.magnitude(tab);
    }
    
    /**Compute Euclidean distance between two {@code GeneralEnvelope} in dimension n.
     * 
     * @param envelopA
     * @param envelopB
     * @throws IllegalArgumentException if envelopA or envelopB are null.
     * @throws IllegalArgumentException if envelopA or envelopB are not in same dimension.
     * @return distance between envelopA and envelopB centroids.
     */
    public static double getDistanceBetween2Envelop(final GeneralEnvelope envelopA, final GeneralEnvelope envelopB){
        ArgumentChecks.ensureNonNull("getDistanceBetween2Envelop : envelopA", envelopA);
        ArgumentChecks.ensureNonNull("getDistanceBetween2Envelop : envelopB", envelopB);
        if(envelopA.getDimension() != envelopB.getDimension()){
            throw new IllegalArgumentException("getDistanceBetween2Envelop : envelopA and envelopB are not in same dimension");
        }
        return getDistanceBetween2DirectPosition(envelopA.getMedian(), envelopB.getMedian());
    }
    
    /**Organize all elements from {@code DefaultNode} List and {@code GeneralEnvelope} List by differents criterion.
     * Compare left boundary coordinates elements.
     * 
     * @param index : - 0 : organize all List by smallest left boundary x value to tallest.
     *                - 1 : organize all List by smallest left boundary y value to tallest.
     *                - 2 : organize all List by smallest left boundary z value to tallest.
     * @throws IllegalArgumentException if index is out of required limits.
     * @throws IllegalArgumentException if listNode and listEntries are null.
     */
    public static void organize_List_Elements_From_Left(int index, final List<DefaultNode> listNode, final List<GeneralEnvelope> listEntries) {
        ArgumentChecks.ensureBetween("organize_List3DElements_From_Left : index", 0, 2, index);
        if(listNode==null&&listEntries==null){
            throw new IllegalArgumentException("organize_List3DElements_From_Left : impossible to organize empty lists");
        }
        switch (index) {
            case 0:
                if(listNode!=null){
                    Collections.sort(listNode, NODE3D_COMPARATOR_X_LEFT);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, GE_COMPARATOR_X_LEFT);
                }
                break;

            case 1:
                if(listNode!=null){
                    Collections.sort(listNode, NODE3D_COMPARATOR_Y_LEFT);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, GE_COMPARATOR_Y_LEFT);
                }
                break;
            case 2:
                if(listNode!=null){
                    Collections.sort(listNode, NODE3D_COMPARATOR_Z_LEFT);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, GE_COMPARATOR_Z_LEFT);
                }
                break;
        }
    }
    
    /**Organize all elements from {@code DefaultNode} List and {@code GeneralEnvelope} List by differents criterion.
     * Compare right boundary coordinates elements.
     * 
     * @param index : - 0 : organize all List by smallest right boundary x value to tallest.
     *                - 1 : organize all List by smallest right boundary y value to tallest.
     *                - 2 : organize all List by smallest right boundary z value to tallest.
     * @throws IllegalArgumentException if index is out of required limits.
     * @throws IllegalArgumentException if listNode and listEntries are null.
     */
    public static void organize_List_Elements_From_Right(int index, final List<DefaultNode> listNode, final List<GeneralEnvelope> listEntries) {
        ArgumentChecks.ensureBetween("organize_List3DElements_From_Right : index", 0, 2, index);
        if(listNode==null&&listEntries==null){
            throw new IllegalArgumentException("organize_List3DElements_From_Right : impossible to organize empty lists");
        }
        switch (index) {
            case 0:
                if(listNode!=null){
                    Collections.sort(listNode, NODE3D_COMPARATOR_X_RIGHT);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, GE_COMPARATOR_X_RIGHT);
                }
                break;

            case 1:
                if(listNode!=null){
                    Collections.sort(listNode, NODE3D_COMPARATOR_Y_RIGHT);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, GE_COMPARATOR_Y_RIGHT);
                }
                break;
            case 2:
                if(listNode!=null){
                    Collections.sort(listNode, NODE3D_COMPARATOR_Z_RIGHT);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, GE_COMPARATOR_Z_RIGHT);
                }
                break;
        }
    }
    
    /**Compute general boundary of all shapes passed in parameter.
     * 
     * @param lS GeneralEnvelope List.
     * @throws IllegalArgumentException if {@code GeneralEnvelope} list lS is null.
     * @throws IllegalArgumentException if {@code GeneralEnvelope} list lS is empty.
     * @return Shape which is general boundary.
     */
    public static GeneralEnvelope getEnveloppeMin(final List<GeneralEnvelope> lGE){
        ArgumentChecks.ensureNonNull("getEnveloppeMin : lGE", lGE);
        if(lGE.isEmpty()){
            throw new IllegalArgumentException("impossible to get Enveloppe : empty list");
        }
        final GeneralEnvelope envlop = new GeneralEnvelope(lGE.get(0));
        for(int i = 1, s = lGE.size(); i<s;i++){
            envlop.add(lGE.get(i));
        }
        return envlop;
    }
    
    /**Compute {@code GeneralEnvelope} area in euclidean cartesian space.
     * 
     * @param envelope
     * @return candidate area.
     */
    public static double getGeneralEnvelopArea(final GeneralEnvelope envelope){
        ArgumentChecks.ensureNonNull("getArea : envelop", envelope);
        int area = 0;
        final int dim = envelope.getDimension();
        for(int i = 0; i<dim-1;i++){
            for(int j = i;j<dim;j++){
                area += envelope.getSpan(i)*envelope.getSpan(j);
            }
        }
        return 2*(area);
    }
    
    /**Compute enlargement difference between two {@code GeneralEnvelope}.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: - If dimension is 1 : compute their perimeter difference.
     *               - If dimension is 2 : compute their area difference.
     *               - If dimension is 3 : compute their bulk difference.
     * 
     * Moreover in case of narrowing, negative value is returned.</strong> 
     * </font></blockquote>
     * 
     * @param envMin smallest boundary before enlargement.
     * @param envMax largest boundary after enlargement.
     * @throws IllegalArgumentException if envMin or envMax are null.
     * @throws IllegalArgumentException if dimension envMin != dimension envMax.
     * @throws IllegalArgumentException if dimension is differente of 1 or 2 or 3.
     * @return enlargement or narrowing between envMin envMax.
     */
    public static double getEnlargementValue(final GeneralEnvelope envMin, final GeneralEnvelope envMax){
        ArgumentChecks.ensureNonNull("getEnlargementValue : envelop", envMin);
        ArgumentChecks.ensureNonNull("getEnlargementValue : envelop", envMax);
        final int dimEnvMin = envMin.getDimension();
        final int dimEnvMax = envMax.getDimension();
        if(dimEnvMax != dimEnvMin){
            throw new IllegalArgumentException("getEnlargementValue : not same dimension. Dim envMin = "+dimEnvMin+" dim envMax = "+dimEnvMax);
        }
        switch(dimEnvMin){
            case 1 : return envMax.getSpan(0) - envMin.getSpan(0);
            case 2 : return getGeneralEnvelopArea(envMax) - getGeneralEnvelopArea(envMin);
            case 3 : return getGeneralEnvelopBulk(envMax) - getGeneralEnvelopBulk(envMin);
            default : throw new IllegalArgumentException("dimension not conform.");
        }
    }    
}
