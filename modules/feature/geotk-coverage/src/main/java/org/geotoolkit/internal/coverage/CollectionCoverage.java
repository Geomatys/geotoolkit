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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.coverage.Coverage;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.geotoolkit.coverage.SampleDimension;
import org.opengis.geometry.DirectPosition;

/**
 * Coverage made of a collection of coverages sharing the same sample dimensions.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CollectionCoverage extends AbstractCoverage {

    /**
     * Parameter used to control evaluation method.
     */
    public static final String PROPERTY_EVALUATION = "evaluation";
    /**
     * Evaluation returns the first coverage values which is not all NaN.
     */
    public static final int EVALUATION_FIRST_NOT_NAN = 0;
    /**
     * Try to combine vector samples to obtain a complete vector without any
     * NaN values.
     */
    public static final int EVALUATION_UNTIL_COMPLETE = 1;

    private final List<Coverage> coverages;
    private final SampleDimension[] sampleDimensions;
    private final int evaluationMode;

    /**
     * Constructs a coverage collection using provided list.
     * The list order is used when evaluating samples.
     *
     * @param name
     *          The coverage name, or {@code null} if none.
     * @param coverages
     *          List of combined coverages, all coverage must have the same sample dimensions.
     * @param properties
     *          The set of properties for this coverage, or {@code null} if there is none.
     *          Keys are {@link String} objects ({@link javax.media.jai.util.CaselessStringKey}
     *          are accepted as well), while values may be any {@link Object}.
     * @return created coverage collection
     */
    public static Coverage create(CharSequence name, List<Coverage> coverages, Map<?,?> properties) {
        return new CollectionCoverage(name, coverages, properties);
    }

    private CollectionCoverage(CharSequence name, List<Coverage> coverages, Map<?,?> properties) {
        super(name, coverages.get(0).getCoordinateReferenceSystem(), null, properties);
        this.coverages = new ArrayList<>(coverages);

        sampleDimensions = this.coverages.get(0).getSampleDimensions().toArray(new SampleDimension[0]);

        if (properties != null && properties.containsKey(PROPERTY_EVALUATION)) {
            evaluationMode = (Integer)properties.get(PROPERTY_EVALUATION);
            if (!(evaluationMode == 0 || evaluationMode == 1)) {
                throw new IllegalArgumentException("Unvalid evaluation mode "+evaluationMode);
            }
        } else {
            evaluationMode = EVALUATION_FIRST_NOT_NAN;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object evaluate(DirectPosition point) throws PointOutsideCoverageException, CannotEvaluateException {
        double[] values = null;

        search:
        for (Coverage coverage : coverages) {
            final Object array;
            try {
                array = coverage.evaluate(point);
            } catch (CannotEvaluateException ex) {
                continue search;
            }

            if (evaluationMode == EVALUATION_FIRST_NOT_NAN) {
                //check if we have at least one defined value
                final int length = Array.getLength(array);
                for (int i=0; i<length; i++) {
                    double d = Array.getDouble(array, i);
                    if (!Double.isNaN(d)) {
                        return array;
                    }
                }
            } else {
                if (values == null) {
                    //first vector, initialize array
                    values = new double[sampleDimensions.length];
                    Arrays.fill(values, Double.NaN);
                }

                boolean complete = true;
                for (int i=0; i<values.length; i++) {
                    if (Double.isNaN(values[i])) {
                        values[i] = Array.getDouble(array, i);
                    }
                    complete &= !Double.isNaN(values[i]);
                }
                if (complete) {
                    return array;
                }
            }
        }

        if (values == null) {
            throw new PointOutsideCoverageException();
        }
        return values;
    }

    @Override
    public List<? extends SampleDimension> getSampleDimensions() {
        return UnmodifiableArrayList.wrap(sampleDimensions);
    }

}
