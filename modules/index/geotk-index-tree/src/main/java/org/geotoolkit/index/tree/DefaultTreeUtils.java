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
package org.geotoolkit.index.tree;

import java.util.List;
import org.apache.sis.math.MathFunctions;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**Some utilities methods.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class DefaultTreeUtils {

    private DefaultTreeUtils() {
    }

    /**
     * @param node to denominate elements number.
     * @return elements number within node.
     */
    public static int countElements(Node node) {
        return node.getChildren().size()+node.getEntries().size();
    }

    public static void recursiveCount(Node n, int[] counter){
        counter[0] = counter[0]+n.getEntries().size();
        for(Node c : n.getChildren()){
            recursiveCount(c, counter);
        }
    }
    
    /**Compute {@code Envelop} bulk.
     *
     * @param envelope {@code Envelope}.
     * @return bulk value.
     */
    public static double getGeneralEnvelopBulk(final Envelope envelope) {
        ArgumentChecks.ensureNonNull("getGeneralEnvelopBulk : gn", envelope);
        final DirectPosition lower = envelope.getLowerCorner();
        final DirectPosition upper = envelope.getUpperCorner();
        int dim = envelope.getDimension();
        if (dim<3) throw new IllegalArgumentException("getGeneralEnvelopBulk : compute envelop bulk with lesser than three dimensions have no sens");
        if (lower.equals(upper)) return 0;
        double bulk = 1;
        for(int i = 0; i<dim; i++){
            bulk *= envelope.getSpan(i);
        }
        return bulk;
    }

    /**Compute {@code Envelope} perimeter.
     *
     * @param envelope {@code Envelope}.
     * @throws IllegalArgumentException if envelope is null.
     * @throws IllegalArgumentException if envelope dimension > 2.
     * @return perimeter value.
     */
    public static double getGeneralEnvelopPerimeter(final Envelope envelope) {
        ArgumentChecks.ensureNonNull("getGeneralEnvelopPerimeter : gn", envelope);
        int dim = envelope.getDimension();
        if (dim>2) throw new IllegalArgumentException("getGeneralEnvelopPerimeter : compute envelop perimeter with more than two dimension have no sens");
        double perim = 0;
        for(int i = 0, l=envelope.getDimension(); i<l;i++) {
            perim+=envelope.getSpan(i);
        }
        return 2*perim;
    }


    /**Compute overlaps between two {@code Envelop}.
     *
     * <blockquote><font size=-1>
     * <strong>NOTE: In first time : compute intersection {@code Envelope} between envelopA and envelopB.
     *                               - If intersection dimension is 2 compute its area.
     *                               - If intersection dimension is 3 compute its bulk.</strong>
     * </font></blockquote>
     *
     * @param envelopA
     * @param envelopB
     * @return intersection between envelopA, envelopB bulk, or area from area dimension.
     */
    public static double getOverlapValue(final Envelope envelopA, final Envelope envelopB) {
        ArgumentChecks.ensureNonNull("getOverlapValue : envelopA", envelopA);
        ArgumentChecks.ensureNonNull("getOverlapValue : envelopB", envelopB);
        final GeneralEnvelope intersectionGN = new GeneralEnvelope(envelopA);
        intersectionGN.intersect(envelopB);
        int dimInter = intersectionGN.getDimension();
        if(dimInter == 2) return getGeneralEnvelopArea(intersectionGN);
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
    public static double getDistanceBetween2DirectPosition(final DirectPosition directPositionA, final DirectPosition directPositionB) {
        ArgumentChecks.ensureNonNull("getDistanceBetween2DirectPosition : directPositionA", directPositionA);
        ArgumentChecks.ensureNonNull("getDistanceBetween2DirectPosition : directPositionB", directPositionB);
        final int length = directPositionA.getDimension();
        if(length!=directPositionB.getDimension())
            throw new IllegalArgumentException("getDistanceBetween2DirectPosition : dpA and dpB are not in same dimension");

        final double[] tab = new double[length];
        final double[] dpACoords = directPositionA.getCoordinate();
        final double[] ordinateDB = directPositionB.getCoordinate();
        for(int i =0; i<length; i++){
            tab[i] = dpACoords[i]-ordinateDB[i];
        }
        return MathFunctions.magnitude(tab);
    }

    /**Compute Euclidean distance between two {@code Envelope} in dimension n.
     *
     * @param envelopA
     * @param envelopB
     * @throws IllegalArgumentException if envelopA or envelopB are null.
     * @throws IllegalArgumentException if envelopA or envelopB are not in same dimension.
     * @return distance between envelopA and envelopB centroids.
     */
    public static double getDistanceBetween2Envelop(final Envelope envelopA, final Envelope envelopB){
        ArgumentChecks.ensureNonNull("getDistanceBetween2Envelop : envelopA", envelopA);
        ArgumentChecks.ensureNonNull("getDistanceBetween2Envelop : envelopB", envelopB);
        if(envelopA.getDimension() != envelopB.getDimension()){
            throw new IllegalArgumentException("getDistanceBetween2Envelop : envelopA and envelopB are not in same dimension");
        }
        return getDistanceBetween2DirectPosition(getMedian(envelopA), getMedian(envelopB));
    }

    /**Compute general boundary of all {@code Envelope} passed in parameter.
     *
     * @param lS GeneralEnvelope List.
     * @throws IllegalArgumentException if {@code Envelope} list lS is null.
     * @throws IllegalArgumentException if {@code Envelope} list lS is empty.
     * @return GeneralEnvelope which is general boundary.
     */
    public static GeneralEnvelope getEnveloppeMin(final List lGE){
        ArgumentChecks.ensureNonNull("getEnveloppeMin : lGE", lGE);
        if(lGE.isEmpty()){
            throw new IllegalArgumentException("impossible to get Enveloppe : empty list");
        }
        Object first = lGE.get(0);
        if (!(first instanceof Envelope) && !(first instanceof Node))
            throw new IllegalArgumentException("list elements should be instance of Node or Envelope : use only for tree work.");
        final boolean isNode = first instanceof Node;
        final GeneralEnvelope envlop = new GeneralEnvelope((isNode)?((Node)first).getBoundary():(Envelope)first);
        for(int i = 1, s = lGE.size(); i < s; i++){
            envlop.add((isNode)?((Node)lGE.get(i)).getBoundary():(Envelope)lGE.get(i));
        }
        return envlop;
    }

    /**Compute {@code Envelope} area in euclidean cartesian space.
     *
     * @param envelope
     * @return candidate area.
     */
    public static double getGeneralEnvelopArea(final Envelope envelope){
        ArgumentChecks.ensureNonNull("getArea : envelop", envelope);
        double area = 0;
        final int dim = envelope.getDimension();
        for(int i = 0; i<dim-1; i++) {
            for(int j = i+1;j<dim;j++) {
                area += envelope.getSpan(i) * envelope.getSpan(j);
            }
        }
        return (dim-1)*(area);
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMedian(int) middle ordinates}
     * for each dimension for all points within the {@code Envelope}.
     *
     * @return The median coordinates.
     *
     * @param env
     */
    public static DirectPosition getMedian(final Envelope env){
        final GeneralDirectPosition position = new GeneralDirectPosition(env.getDimension());
        for (int i=position.ordinates.length; --i>=0;) {
            position.ordinates[i] = env.getMedian(i);
        }
        position.setCoordinateReferenceSystem(env.getCoordinateReferenceSystem());
        return position;
    }
}
