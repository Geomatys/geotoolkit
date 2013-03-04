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

import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import static org.junit.Assert.assertTrue;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * Test Resolution class.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class ResolutionTest {

    /**
     * Tested {@link Resolution} Object.
     */
    private final Resolution resolution;

    public ResolutionTest(MathTransform mathTransform, double[] destExpectRes, double ratio) throws NoninvertibleTransformException {
        this.resolution = new Resolution(mathTransform, destExpectRes, ratio);
    }

    /**
     * Test Envelope sub-division.
     *
     * @param envelopeDest envelope to divide.
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    protected void testFractEnvelope(Envelope envelopeDest) throws MismatchedDimensionException, TransformException {
        resolution.fractionate(envelopeDest);
        List<Envelope> le = resolution.getResults();
        assertTrue(checkAreaEnvelope(envelopeDest, le));
        assertTrue(checkBoundaryEnvelope(envelopeDest, le));
    }

    /**
     * Verify that sum of {@code Envelope} boundary within list equals to reference Envelope boundary.
     *
     * @param envRef Reference envelope.
     * @param lEnv Envelope list.
     * @return true if sum of envelope boundary within lEnv equals envRef boundary else false.
     */
    private boolean checkBoundaryEnvelope(final Envelope envRef, final List<Envelope> lEnv) {
        assert !lEnv.isEmpty() : "empty envelope list";
        final GeneralEnvelope envTest = new GeneralEnvelope(lEnv.get(0));
        for (int i = 0, s = lEnv.size(); i<s; i++) envTest.add(lEnv.get(i));
        return envRef.equals(envTest);
    }

    /**
     * Verify that sum of {@code Envelope} area within list equals to reference Envelope area.
     *
     * @param envRef Reference envelope.
     * @param lEnv Envelope list.
     * @return true if sum of envelope area within lEnv equals envRef area else false.
     */
    private boolean checkAreaEnvelope(final Envelope envRef, final List<Envelope> lEnv) {
        assert !lEnv.isEmpty() : "empty envelope list";
        final double areaRef = getGeneralEnvelopArea(envRef);
        double areaTemp = 0;
        for (Envelope env : lEnv) {
            areaTemp += getGeneralEnvelopArea(env);
        }
        return ((int)areaRef*100) == ((int)areaTemp*100);
    }

    /**Compute {@code Envelope} area in euclidian space.
     *
     * @param envelope
     * @return candidate area.
     */
    private double getGeneralEnvelopArea(final Envelope envelope){
        ArgumentChecks.ensureNonNull("getArea : envelop", envelope);
        double area = 0;
        final int dim = envelope.getDimension();
        for(int i = 0; i<dim-1; i++) {
            for(int j = i+1; j<dim; j++) {
                area += (envelope.getSpan(i) * envelope.getSpan(j));
            }
        }
        return (dim-1) * area;
    }
}
