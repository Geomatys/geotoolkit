/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.display2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * Contain some of methods about image Resolution computing from {@code MathTransform}.
 *
 * - Give source resolution on one point from target resolution.
 * @see #singlePointResolution(org.opengis.geometry.DirectPosition) .
 *
 * - Give appropriate adapted lesser global resolution from {@code Envelope}.
 * @see #getSourceResolution(org.opengis.geometry.Envelope) .
 *
 * - Give sum of {@code Envelope} which represent base {@code Envelope} fractionate
 *   from derivative difference.
 * @see #fractionate(org.opengis.geometry.Envelope) .
 *
 * @author Remi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Resolution {

    /**
     * Transform multi-dimensional point from source {@code CoordinateReferenceSystem}
     * to target {@code CoordinateReferenceSystem}.
     */
    private final MathTransform mathTransform;

    /**
     * Inverse of {@link #mathTransform}.
     */
    private final MathTransform invertMathTransform;

    /**
     * Expected resolution from target {@code CoordinateReferenceSystem}.
     */
    private final double destExpectedRes[];

    /**
     * Sum of {@code Envelope} sub-division.
     * @see #find(org.opengis.geometry.Envelope).
     */
    private final List<Envelope> result = new ArrayList<Envelope>();

    /**
     * Double proportionality value.
     * @see #find(org.opengis.geometry.Envelope).
     */
    private final double ratio;

    /**
     * List of multi-dimensional index ordinate.
     * @see #getSourceResolution(org.opengis.geometry.Envelope).
     */
    private final List<int[]> listOrdinate = new ArrayList<int[]>();

    /**
     * Fifo list use about fractionate {@code Envelope}.
     * @see #fractionate() .
     */
    private final LinkedList<Envelope> envelopeFifo = new LinkedList<Envelope>();

    /**
     * {@code CoordinateReferenceSystem} from current {@code Envelope}.
     */
    private CoordinateReferenceSystem crs;

    /**
     * Dimension from current {@code Envelope}.
     */
    private int dim;

    /**
     * Create class with some of methods about resolution.
     *
     * @param mathTransform current multi-dimensional transformation.
     * @param destExpectedRes expected resolution from target {@code CoordinateReferenceSystem}.
     * @param ratio represent proportionality rapport between derivative values.
     *              If ratio is reached {@code Envelope} is fractionate.
     * @see #fractionate() .
     * @throws NoninvertibleTransformException
     */
    public Resolution(MathTransform mathTransform, double[] destExpectedRes, double ratio) throws NoninvertibleTransformException {
        ArgumentChecks.ensureNonNull("Constructor : mathTransform", mathTransform);
        ArgumentChecks.ensureStrictlyPositive("ratio will be able to strictly positive", ratio);
        this.mathTransform = mathTransform;
        this.invertMathTransform = mathTransform.inverse();
        this.destExpectedRes = destExpectedRes;
        this.ratio = ratio;
    }

    /**
     * Return resolution table which contains resolution from each dimension.
     *
     * @param dp {@code Directposition} will be derivative.
     * @return resolution table which contains resolution from each dimension.
     * @throws NoninvertibleTransformException if {@link #mathTransform} is not inversive.
     * @throws MismatchedDimensionException
     * @throws TransformException if {@link #mathTransform} transformation is impossible.
     */
    public double[] singlePointResolution(final DirectPosition dp) throws NoninvertibleTransformException, MismatchedDimensionException, TransformException {
        final int length = destExpectedRes.length;
        final double[] resultab = new double[length];
        final Matrix matrice = mathTransform.inverse().derivative(dp);
        double m;
        for (int i = 0; i<length; i++) {
            m = matrice.getElement(i, i);
            assert (m != 0) : "matrix  element m("+i+", "+i+") is equal to zero";
            resultab[i] = Math.abs(destExpectedRes[i] * m);
        }
        return resultab;
    }


    /**
     * Find appropriate adapted lesser global resolution from {@code Envelope}.
     *
     * Return a double table of length of 2.
     * table[0] contain appropriate resolution about "X" axis coordinate.
     * table[1] contain appropriate resolution about "Y" axis coordinate.
     *
     * todo : generalize algorithm for N-dimensions.
     *
     * @param envelope area which looking for adapted resolution.
     * @return resolution double table.
     * @throws NoninvertibleTransformException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    public double[] getSourceResolution(final Envelope envelope) throws NoninvertibleTransformException, MismatchedDimensionException, TransformException {
        final double xmin = envelope.getMinimum(0);
        final double ymin = envelope.getMinimum(1);
        final double rW = envelope.getSpan(0);
        final double rH = envelope.getSpan(1);
        double rXTemp, rYTemp;
        double resX = -1;
        double resY = -1;
        Matrix mat;
        int[] ordChoose;

        for (double y = ymin; y<= ymin + rH; y += rH/2.0){
            for (double x = xmin; x <= xmin + rW; x += rW/2.0) {
                mat = mathTransform.inverse().derivative(new DirectPosition2D(x, y));
                ordChoose = getAxis(mat);
                rXTemp = Math.abs(mat.getElement(0, ordChoose[0]));
                rYTemp = Math.abs(mat.getElement(1, ordChoose[1]));
                resX = (resX == -1) ? rXTemp : (resX>rXTemp) ? rXTemp: resX;
                resY = (resY == -1) ? rYTemp : (resY>rYTemp) ? rYTemp: resY;
            }
        }
        return new double[]{Math.abs(resX * destExpectedRes[0]), Math.abs(resY * destExpectedRes[1])};
    }

    /**
     * Sub-divide {@code Envelope} when resolution difference is too large in compare from ratio.
     *
     * @param envelope Envelope will be sub-divide.
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    public void fractionate(Envelope envelope) throws MismatchedDimensionException, TransformException{
        crs = envelope.getCoordinateReferenceSystem();
        dim = envelope.getDimension();
        envelopeFifo.add(envelope);
        fractionate();
    }

    /**
     * Sub-divide {@code Envelope} when resolution difference is too large in compare from ratio.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    private void fractionate() throws MismatchedDimensionException, TransformException {
        GeneralDirectPosition dpLowA, dpUppA;
        GeneralDirectPosition dpLowB, dpUppB;
        Matrix matA, matB;
        double derivSourcA, derivSourcB, tempRatio;
        double v2, s2, val, span, v3, v;
        int ord2;
        int[] ordinateA;
        int[] ordinateB;

        DirectPosition dpLowDest , dpUppDest;
        final DirectPosition dpADest = new GeneralDirectPosition(crs);
        final DirectPosition dpBDest = new GeneralDirectPosition(crs);

        Envelope envelope;

        next:      while (!envelopeFifo.isEmpty()) {
                        envelope = envelopeFifo.pollLast();
                        dpLowDest = envelope.getLowerCorner();
                        dpUppDest = envelope.getUpperCorner();

                        for (int ord = 0; ord < 2; ord++) {
                            ord2 = dim-1-ord;
                            v2 = dpLowDest.getOrdinate(ord);
                            s2 = envelope.getSpan(ord);
                            val = dpLowDest.getOrdinate(ord2);
                            span = envelope.getSpan(ord2);
                            for (v3 = v2; v3 <= v2+s2; v3 += s2/2) {
                                for (v = val; v <= val + span; v += span/2) {
                                    dpADest.setOrdinate(ord, v3);
                                    dpADest.setOrdinate(ord2, v);
                                    dpBDest.setOrdinate(ord, v3 + s2/2);
                                    dpBDest.setOrdinate(ord2, v);
                                    matA = invertMathTransform.derivative(dpADest);
                                    matB = invertMathTransform.derivative(dpBDest);
                                    ordinateA = getAxis(matA);
                                    ordinateB = getAxis(matB);
                                    derivSourcA = Math.abs(matA.getElement(ord, ordinateA[ord]));
                                    derivSourcB = Math.abs(matB.getElement(ord, ordinateB[ord]));

                                    tempRatio = (derivSourcA >= derivSourcB) ? derivSourcA/derivSourcB : derivSourcB/derivSourcA;

                                    if (tempRatio > ratio) {
                                        //split
                                        dpLowA = new GeneralDirectPosition(dpLowDest);
                                        dpUppA = new GeneralDirectPosition(dpUppDest);
                                        dpUppA.setOrdinate(ord, v2 + s2/2);
                                        envelopeFifo.addFirst(new GeneralEnvelope(dpLowA, dpUppA));

                                        dpLowB = new GeneralDirectPosition(dpLowDest);
                                        dpLowB.setOrdinate(ord, v2 + s2/2);
                                        dpUppB = new GeneralDirectPosition(dpUppDest);
                                        envelopeFifo.addFirst(new GeneralEnvelope(dpLowB, dpUppB));
                                        continue next;
                                    }
                                }
                            }
                        }
                        result.add(envelope);
                    }
    }

    /**
     * Return sum of {@code Envelope} sub-division.
     *
     * @return sum of {@code Envelope} sub-division.
     */
    public List<Envelope> getResults() {
        return result;
    }

    /**
     * Find appropriate ordinate index from derivative matrix value.
     *
     * @param derivative Jacobean matrix at point.
     * @return appropriate ordinate table.
     */
    private int[] getAxis(Matrix derivative) {
        final int dimM = derivative.getNumRow();
        generate(dimM);
        double currentScalar = -1;
        double scalarTemp, scalarSom;
        int finalIndex = 0;
        int[] ordinate;
        int numRow, numCol;
        for (int index = 0, l = listOrdinate.size(); index < l; index++) {
            ordinate = listOrdinate.get(index);
            scalarSom = 0;
            for (numCol = 0; numCol < dimM; numCol++) {
                numRow      = ordinate[numCol];
                scalarTemp  = derivative.getElement(numRow, numCol);
                scalarTemp *= scalarTemp;
                scalarSom  += scalarTemp;
            }
            if (scalarSom > currentScalar) {
                currentScalar = scalarSom;
                finalIndex    = index;
            }
        }
        return listOrdinate.get(finalIndex);
    }

    /**
     * Generate all sequences possibilities from dimension.
     *
     * @param dimension space dimension.
     * @return
     */
    private int[] generate(final int dimension) {
        final int[] ordinates = new int[dimension];
        fill(ordinates, 0, ordinates.length);
        return ordinates;
    }

    private void fill(final int[] ordinates, final int currentColumn, final int numDim) {
next:   for (int i=0; i<numDim; i++) {
            // Skip the values already used by previous iteration.
            for (int j=currentColumn; --j>=0;) {
                if (ordinates[j] == i) {
                    // Ordinate already used. Search for the next one.
                    continue next;
                }
            }
            ordinates[currentColumn] = i;
            if (currentColumn+1 < ordinates.length) {
                fill(ordinates, currentColumn+1, numDim);
            } else {
                listOrdinate.add(ordinates);
            }
        }
    }
}
