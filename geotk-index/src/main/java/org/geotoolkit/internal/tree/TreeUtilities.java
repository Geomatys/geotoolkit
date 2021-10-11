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
package org.geotoolkit.internal.tree;

import org.apache.sis.math.MathFunctions;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.Envelope;

/**
 * Some utilities methods.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeUtilities {

    /**
     * Properties which define if a {@link Node} is a leaf.<br/>
     * See {@link Node#isLeaf()}.
     */
    public final static byte IS_LEAF  = 1;

    /**
     * Properties which define if a {@link Node} is a data, which mean its child id represent tree identifier of a data.<br/>
     * See {@link Node#isData()}.
     */
    public final static byte IS_DATA  = 2;

    /**
     * Properties which define if a {@link Node} is a Hilbert Cell.<br/>
     * See {@link HilbertNode}.<br/>
     * See {@link HilbertNode#isCell() }.
     */
    public final static byte IS_CELL  = 4;

    /**
     * Properties which define if a {@link Node} is a "tree branch"
     * which mean Node with no particularity properties.
     */
    public final static byte IS_OTHER = 8;

    /**
     * Numbers to identify tree type in file.
     */
    public final static int BASIC_NUMBER      = 188047901;
    public final static int HILBERT_NUMBER    = 69669745;
    public final static int STAR_NUMBER       = 23107209;
    public final static double VERSION_NUMBER = 0.1;

    private TreeUtilities() {
    }

    /**
     * Compute "envelope" bulk from its double coordinates table.
     *
     * @param envelope coordinates.
     * @throws IllegalArgumentException if envelope is null.
     * @throws IllegalArgumentException if envelope dimension < 3.
     * @return bulk value.
     */
    public static double getBulk(final double[] envelope) {
        ArgumentChecks.ensureNonNull("getBulk : envelope", envelope);
        final int dim = envelope.length >> 1;
        if (dim < 3) throw new IllegalArgumentException("getGeneralEnvelopBulk : compute envelop bulk with lesser than three dimensions have no sens");
        double bulk = 1;
        for (int i = 0; i < dim; i++) bulk *= getSpan(envelope, i);
        return bulk;
    }

    /**
     * Compute "Envelope" perimeter from its double coordinates table.
     *
     * @param envelope coordinates.
     * @throws IllegalArgumentException if envelope is null.
     * @throws IllegalArgumentException if envelope dimension > 2.
     * @return perimeter value.
     */
    public static double getPerimeter(final double[] envelope) {
        ArgumentChecks.ensureNonNull("getPerimeter : envelope", envelope);
        final int dim = envelope.length >> 1;
        if (dim != 2) throw new IllegalArgumentException("getGeneralEnvelopPerimeter : compute envelop perimeter with more or lesser than two dimension have no sens");
        double perim = 0;
        for (int i = 0, l = dim; i < l; i++) perim += getSpan(envelope, i);
        return 2 * perim;
    }

    /**
     * Compute overlaps between two {@code Envelop}.
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
    public static double getOverlapValue(final double[] envelopA, final double[] envelopB) {
        ArgumentChecks.ensureNonNull("getOverlapValue : envelopA", envelopA);
        ArgumentChecks.ensureNonNull("getOverlapValue : envelopB", envelopB);
        if (!intersects(envelopA, envelopB, true)) return 0;
        final double[] intersectionGN = intersect(envelopA, envelopB);
        return ((intersectionGN.length >> 1) == 2) ? getArea(intersectionGN) : getBulk(intersectionGN);
    }

    /**
     * Compute Euclidean distance between two {@code double[]} in dimension n.
     *
     * @param positionA : coordinate double table of point A.
     * @param positionB : coordinate double table of point B.
     * @throws IllegalArgumentException if positionA or positionB are null.
     * @throws IllegalArgumentException if positionA or positionB are not in same dimension.
     * @return distance between positionA and positionB.
     */
    public static double getDistanceBetween2Positions(final double[] positionA, final double[] positionB) {
        ArgumentChecks.ensureNonNull("getDistanceBetween2Positions : positionA", positionA);
        ArgumentChecks.ensureNonNull("getDistanceBetween2Positions : positionB", positionB);
        final int length = positionA.length;
        if (length != positionB.length)
            throw new IllegalArgumentException("getDistanceBetween2Positions : positionA and positionB are not in same dimension");
        final double[] tab = new double[length];
        for (int i = 0; i < length; i++) {
            tab[i] = positionA[i] - positionB[i];
        }
        return MathFunctions.magnitude(tab);
    }

    /**
     * Compute Euclidean distance between two {@code Envelope} in dimension n.
     *
     * @param envelopA
     * @param envelopB
     * @throws IllegalArgumentException if envelopA or envelopB are null.
     * @throws IllegalArgumentException if envelopA or envelopB are not in same dimension.
     * @return distance between envelopA and envelopB centroids.
     */
    public static double getDistanceBetween2Envelopes(final double[] envelopA, final double[] envelopB){
        ArgumentChecks.ensureNonNull("getDistanceBetween2Envelopes : envelopA", envelopA);
        ArgumentChecks.ensureNonNull("getDistanceBetween2Envelopes : envelopB", envelopB);
        if(envelopA.length != envelopB.length)
            throw new IllegalArgumentException("getDistanceBetween2Envelopes : envelopA and envelopB are not in same dimension");
        assert (envelopA.length % 2 == 0) :"envelope coordinates length should be modulo 2";
        return getDistanceBetween2Positions(getMedian(envelopA), getMedian(envelopB));
    }

    /**
     * Compute general boundary of all {@code Envelope} passed in parameter.
     *
     * @param lS GeneralEnvelope List.
     * @throws IllegalArgumentException if {@code Envelope} list lS is null.
     * @throws IllegalArgumentException if {@code Envelope} list lS is empty.
     * @return GeneralEnvelope which is general boundary.
     */
    public static double[] getEnvelopeMin(final double[][] coordinates){
        ArgumentChecks.ensureNonNull("getEnveloppeMin : coordinates", coordinates);
        if(coordinates == null || coordinates.length == 0){
            throw new IllegalArgumentException("impossible to get Envelope min from null or empty table.");
        }

        final double[] envelope = coordinates[0].clone();
        for (int i = 1, s = coordinates.length; i < s; i++) {
            add(envelope, coordinates[i]);
        }
        return envelope;
    }

    public static boolean arrayEquals(final double[] expected, final double[] value, final double epsilon) {
        ArgumentChecks.ensureNonNull("arrayEquals : expected : ", expected);
        ArgumentChecks.ensureNonNull("arrayEquals : value : ", value);
        ArgumentChecks.ensurePositive("arrayEquals : epsilon", epsilon);
        final int l = expected.length;
        if (l != value.length) return false;
        for (int i = 0; i < l; i++) if (Math.abs(expected[i] - value[i]) > epsilon) return false;
        return true;
    }

    /**
     * Return double coordinate table which contain Envelope coordinate.
     * Table result length is 2*envelope dimension.
     * First table part contain envelope lower corner coordinates and second part, upper corner coordinates.
     *
     * @param coords table where is store coordinate. if null a new table is create.
     * @return double coordinate table which contain Envelope coordinate.
     */
    public static double[] getCoords(final Envelope envelope) {
        ArgumentChecks.ensureNonNull("getCoords : envelope", envelope);
        final int dim = envelope.getDimension();
        final double[] coords = new double[dim <<1];
        for (int i = 0, d = dim; i < dim; i++, d++) {
            coords[i] = envelope.getMinimum(i);
            coords[d] = envelope.getMaximum(i);
        }
        return coords;
    }

    /**
     * Compute union between two "envelope" coordinate double tables.
     * Result is set in envelopeA.
     *
     * @return envelopeA which contain result of union.
     */
    public static double[] add(final double[] envelopeA, final double[] envelopeB) {
        ArgumentChecks.ensureNonNull("EnvelopeA", envelopeA);
        ArgumentChecks.ensureNonNull("EnvelopeB", envelopeB);
        assert (envelopeA.length == envelopeB.length) :"getUnion : envelope should have same dimension number.";
        final int dim = envelopeA.length >> 1;
        for (int i = 0, d = dim; i < dim; i++, d++) {
            envelopeA[i] = Math.min(envelopeA[i], envelopeB[i]);
            envelopeA[d] = Math.max(envelopeA[d], envelopeB[d]);
        }
        return envelopeA;
    }

    /**
     * Compute intersection between two "envelope" coordinate double tables.
     *
     * @return double table which contain result of intersection or null if none.
     */
    public static double[] intersect(final double[] envelopeA, final double[] envelopeB) {
        ArgumentChecks.ensureNonNull("EnvelopeA", envelopeA);
        ArgumentChecks.ensureNonNull("EnvelopeB", envelopeB);
        assert (envelopeA.length == envelopeB.length) :"intersect : envelope should have same dimension number.";
        final double[] intersect = envelopeA.clone();
        final int dim = intersect.length >> 1;
        for (int i = 0, d = dim; i < dim; i++, d++) {
            intersect[i] = Math.max(intersect[i], envelopeB[i]);
            intersect[d] = Math.min(intersect[d], envelopeB[d]);
            if (intersect[i] > intersect[d]) return null;
        }
        return intersect;
    }

    /**
     * Return true if there is intersection between two "envelope" coordinate double tables.
     *
     * @param envelopeA first envelope coordinates.
     * @param envelopeB second envelope coordinates.
     * @param edgeInclusive if true return true if the 2 "envelope" just touches else false.
     * @return true if there is intersection else false.
     */
    public static boolean intersects(final double[] envelopeA, final double[] envelopeB, final boolean edgeInclusive) {
        ArgumentChecks.ensureNonNull("EnvelopeA", envelopeA);
        ArgumentChecks.ensureNonNull("EnvelopeB", envelopeB);
        assert (envelopeA.length == envelopeB.length) :"intersects : envelope should have same dimension number.";
        final int dim = envelopeA.length >> 1;
        double low, upp;
        for (int i = 0, d = dim; i < dim; i++, d++) {
            low = Math.max(envelopeA[i], envelopeB[i]);
            upp = Math.min(envelopeA[d], envelopeB[d]);
            if (edgeInclusive && low > upp || !edgeInclusive && low >= upp) return false;
        }
        return true;
    }

    /**
     * Return true if the 2 "envelopes" touches them else false.
     *
     * @param envelopeA first envelope coordinates.
     * @param envelopeB second envelope coordinates.
     * @return true if the 2 "envelopes" touches them else false.
     */
    public static boolean touches(final double[] envelopeA, final double[] envelopeB){
        final double epsilon = 1E-15;
        if (!intersects(envelopeA, envelopeB, true)) return false;
        if (intersects(envelopeA, envelopeB, false)) return false;
        final double[] intersection = intersect(envelopeA, envelopeB);
        /**
         * If one dimension from intersection, equals to envelopeA and envelopeB boundary touches is true.
         */
        final int dim = intersection.length >> 1;
        for (int i = 0; i < dim; i++) {
            if (getSpan(intersection, i) < epsilon) {
                final double val = intersection[i];
                final double minA = Math.abs(val - getMinimum(envelopeA, i));
                final double minB = Math.abs(val - getMinimum(envelopeB, i));
                final double maxA = Math.abs(val - getMaximum(envelopeA, i));
                final double maxB = Math.abs(val - getMaximum(envelopeB, i));
                if (((minA < epsilon && maxB < epsilon) || (maxA < epsilon && minB < epsilon))) return true;
            }
        }
        return false;
    }

    /**
     * Return true if envelopeA contain envelopeB or envelope is within envelopeA.
     *
     * @param envelopeA first envelope coordinates.
     * @param envelopeB second envelope coordinates.
     * @return true if envelopeA contain envelopeB or envelope is within envelopeA.
     */
    public static boolean contains(final double[] envelopeA, final double[] envelopeB, final boolean edgeInclusive) {
        ArgumentChecks.ensureNonNull("EnvelopeA", envelopeA);
        ArgumentChecks.ensureNonNull("EnvelopeB", envelopeB);
        assert (envelopeA.length == envelopeB.length) :"contains : envelope should have same dimension number.";
        final int dim = envelopeA.length >> 1;//decalbit
        for (int i = 0, d = dim; i < dim; i++, d++) {
            if ((edgeInclusive && (envelopeB[i] < envelopeA[i] || envelopeB[d] > envelopeA[d]))
            || (!edgeInclusive && (envelopeB[i] <= envelopeA[i] || envelopeB[d] >= envelopeA[d]))) return false;
        }
        return true;
    }

    /**
     * Return true if envelopeA contain envelopeB or envelope is within envelopeA.
     *
     * @param envelopeA first envelope coordinates.
     * @param envelopeB second envelope coordinates.
     * @return true if envelopeA contain envelopeB or envelope is within envelopeA.
     */
    public static boolean contains(final double[] envelope, final double[] point) {
        ArgumentChecks.ensureNonNull("Envelope", envelope);
        ArgumentChecks.ensureNonNull("Point", point);
        final int dim = envelope.length >> 1;
        assert (dim == point.length) :"contains : envelope should have same dimension number.";
        for (int i = 0, d = dim; i < dim; i++, d++) {
            if (point[i] < envelope[i] || point[i] > envelope[d]) return false;
        }
        return true;
    }

    /**Compute {@code Envelope} area in euclidean cartesian space.
     */
    public static double getArea(final double[] envelope){
        ArgumentChecks.ensureNonNull("getArea : envelop", envelope);
        double area = 0;
        final int dim = envelope.length >> 1;
        for(int i = 0; i < dim-1; i++) {
            for(int j = i+1; j < dim; j++) {
                area += getSpan(envelope, i) * getSpan(envelope, j);
            }
        }
        return (dim-1) * (area);
    }

    /**
     * Return span value at index i from envelope table coordinates.
     *
     * @return span value at index i from envelope table coordinates.
     */
    public static double getSpan(final double[] envelope, final int i){
        ArgumentChecks.ensureNonNull("getSpan : envelop", envelope);
        int dim = envelope.length;
        assert (dim % 2 == 0) : "envelope dimension invalide. It should be modulo 2";
        dim = dim >> 1;
        ArgumentChecks.ensureBetween("dimension : envelop", 0, dim, i);
        return envelope[dim + i] - envelope[i];//maybe math.abs
    }

    /**
     * Return minimum value at index i from envelope table coordinates.
     *
     * @return minimum value at index i from envelope table coordinates.
     */
    public static double getMinimum(final double[] envelope, int i){
        ArgumentChecks.ensureNonNull("getSpan : envelop", envelope);
        int dim = envelope.length;//decal bit
        assert (dim % 2 == 0) : "envelope dimension invalide. It should be modulo 2";
        dim = dim >> 1;
        ArgumentChecks.ensureBetween("dimension : envelop", 0, dim, i);
        return envelope[i];
    }

    /**
     * Return maximum value at index i from envelope table coordinates.
     *
     * @return maximum value at index i from envelope table coordinates.
     */
    public static double getMaximum(final double[] envelope, int i){
        ArgumentChecks.ensureNonNull("getSpan : envelop", envelope);
        int dim = envelope.length;//decal bit
        assert (dim % 2 == 0) : "envelope dimension invalide. It should be modulo 2";
        dim = dim >> 1;
        ArgumentChecks.ensureBetween("dimension : envelop", 0, dim, i);
        return envelope[dim + i];
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMedian(int) middle coordinates}
     * for each dimension for all points within the {@code Envelope}.
     *
     * @return The median coordinates.
     */
    public static double[] getMedian(final double[] envelope) {
        ArgumentChecks.ensureNonNull("getMedian : envelop", envelope);
        final int dim = envelope.length >> 1;//decal bit
        final double[] median = new double[dim];
        for (int i = 0, d = dim; i < dim; i++, d++) {
            median[i] = (envelope[i] + envelope[d]) / 2;//decal bit;
        }
        return median;
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMinimum(double[], int) minimum coordinates}
     * for each dimension from the {@code Envelope}.
     *
     * @return The lower corner coordinates.
     */
    public static double[] getLowerCorner(final double[] envelope) {
        ArgumentChecks.ensureNonNull("getLowerCorner : envelop", envelope);
        int dim = envelope.length;//decal bit
        assert (dim % 2 == 0) : "envelope dimension invalide. It should be modulo 2";
        dim = dim >> 1;
        final double[] lowerCorner = new double[dim];
        System.arraycopy(envelope, 0, lowerCorner, 0, dim);
        return lowerCorner;
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMaximum(double[],int) maximum coordinates}
     * for each dimension from the {@code Envelope}.
     *
     * @return The upper coordinates.
     */
    public static double[] getUpperCorner(final double[] envelope) {
        ArgumentChecks.ensureNonNull("getLowerCorner : envelop", envelope);
        int dim = envelope.length;//decal bit
        assert (dim % 2 == 0) : "envelope dimension invalide. It should be modulo 2";
        dim = dim >> 1;
        final double[] upperCorner = new double[dim];
        System.arraycopy(envelope, dim, upperCorner, 0, dim);
        return upperCorner;
    }
}
