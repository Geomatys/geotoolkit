/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import org.geotoolkit.coverage.AbstractCoverage;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.coverage.SampleDimension;
import org.opengis.geometry.DirectPosition;

/**
 * Coverage combining sample dimensions of two coverages.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CompoundCoverage extends AbstractCoverage {

    private final Coverage coverage1;
    private final Coverage coverage2;
    private final int nbSamples1;
    private final SampleDimension[] sampleDimensions;

    /**
     * Constructs a coverage combining sample dimensions from provided coverages.
     *
     * @param name
     *          The coverage name, or {@code null} if none.
     * @param coverage1
     *          First compound coverage
     * @param coverage2
     *          Second compound coverage
     * @param properties
     *          The set of properties for this coverage, or {@code null} if there is none.
     *          Keys are {@link String} objects ({@link javax.media.jai.util.CaselessStringKey}
     *          are accepted as well), while values may be any {@link Object}.
     * @return created coverage collection
     */
    public static Coverage create(CharSequence name, Coverage coverage1, Coverage coverage2, Map<?,?> properties) {
        return new CompoundCoverage(name, coverage1, coverage2, properties);
    }

    private CompoundCoverage(CharSequence name, Coverage coverage1, Coverage coverage2, Map<?,?> properties) {
        super(name, coverage1.getCoordinateReferenceSystem(), null, properties);
        this.coverage1 = coverage1;
        this.coverage2 = coverage2;

        nbSamples1 = coverage1.getNumSampleDimensions();
        final int nbSamples2 = coverage2.getNumSampleDimensions();
        sampleDimensions = new SampleDimension[nbSamples1 + nbSamples2];
        for (int i=0; i<nbSamples1; i++) sampleDimensions[i] = coverage1.getSampleDimension(i);
        for (int i=nbSamples1,k=0; i<sampleDimensions.length; i++,k++) sampleDimensions[i] = coverage2.getSampleDimension(k);
    }

    @Override
    public Object evaluate(DirectPosition point) throws PointOutsideCoverageException, CannotEvaluateException {
        final double[] values = new double[sampleDimensions.length];
        Arrays.fill(values, Double.NaN);

        int nbFail = 0;
        try {
            final Object array = coverage1.evaluate(point);
            for (int i=0; i<nbSamples1; i++) {
                values[i] = Array.getDouble(array, i);
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        try {
            final Object array = coverage2.evaluate(point);
            for (int i=nbSamples1,k=0; i<values.length; i++,k++) {
                values[i] = Array.getDouble(array, k);
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        if (nbFail == 2) {
            throw new PointOutsideCoverageException();
        }

        return values;
    }

    @Override
    public int getNumSampleDimensions() {
        return sampleDimensions.length;
    }

    @Override
    public SampleDimension getSampleDimension(int index) throws IndexOutOfBoundsException {
        return sampleDimensions[index];
    }

}
