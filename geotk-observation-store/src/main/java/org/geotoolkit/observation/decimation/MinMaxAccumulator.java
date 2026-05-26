/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.observation.decimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.observation.decimation.NumberUtils.*;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.observation.result.ResultBuilder;

/**
 * Values accumulator that keep the minimum and maximum value of a slice.
 * This class is not thread-safe.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MinMaxAccumulator extends ValuesAccumulator {

    private static final int MIN = 0;
    private static final int MAX = 1;

    /**
     * Retain the MIN/MAX values for each field.
     */
    private final Map<String, double[]> mapValues;

    /**
     * Build a new MIN/MAX values accumulator.
     *
     * @param start lower bound of the current slice.
     * @param step length of the current slice.
     * @param fields List of the result fields.
     * @param identifierPrefix Prefix to apply to an identifier field.
     * @param builder Result builder where to append the field values.
     * @param skipProfileMain Special flag when we don't want to write the main field.
     * @param onlyProfileMain Special flag when we want to write only the main field.
     */
    public MinMaxAccumulator(Number start, Number step, final List<Field> fields, String obsName, ResultBuilder builder, boolean skipProfileMain, boolean onlyProfileMain) {
        super(start, step, fields, obsName, builder, skipProfileMain, onlyProfileMain);
        this.mapValues = fields.stream()
                .filter(f -> FieldType.MEASURE.equals(f.getType()))
                .collect(Collectors.toMap(
                        field -> field.getName(),
                        key -> new double[]{Double.MAX_VALUE, -Double.MAX_VALUE})
                );
    }

    @Override
    protected void clearValues() {
        for (String key : mapValues.keySet()) {
            mapValues.put(key, new double[]{Double.MAX_VALUE, -Double.MAX_VALUE});
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void append(final Number mainValue, final String field, final double value) {

        mainValues.add(mainValue);

        final double[] previous = mapValues.get(field);
        if (previous == null) {
            throw new IllegalArgumentException("Unknown field: " + field);
        }
        final double minPrevious = previous[MIN];
        final double maxPrevious = previous[MAX];
        if (value > maxPrevious) {
            previous[MAX] = value;
        }
        if (value < minPrevious) {
            previous[MIN] = value;
        }
    }

    /**
     * Return {@code true} if the min and max of the acumulated values are equals.
     *
     * @return {@code true} if the min and max of the acumulated values are equals.
     */
    private boolean minMaxEquals() {
        return fields.stream()
                .filter(f -> FieldType.MEASURE.equals(f.getType()))
                .map(field -> mapValues.get(field.getName()))
                .noneMatch(minMax -> minMax[MIN] != minMax[MAX]);
    }

    private Map<String, Double> getFieldValues(int index) {
        Map<String, Double> fieldValues = new HashMap<>();
        mapValues.forEach((k, v) -> {
            double value;
            if (index == MIN) {
                value = v[MIN];
                if (value == Double.MAX_VALUE) value = Double.NaN;
            } else {
                value = v[MAX];
                if (value == -Double.MAX_VALUE) value = Double.NaN;
            }
            fieldValues.put(k, value);
        });
        return fieldValues;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(Long fixedTime, boolean first, boolean last) throws DataStoreException {

        // if there is only one value in the step, we use the original main value.
        if (mainValues.size() == 1) {
            writeToResult(fixedTime, cpt++, mainValues.iterator().next(), getFieldValues(MIN));

        // if min and max are equals we only write one value in the middle of the step.
        } else if (minMaxEquals() && !(first || last)) {
            writeToResult(fixedTime, cpt++, add(start, div(step, 2)), getFieldValues(MIN));

        // special case where we have only one value in the series, main value has not been recorded
        } else if (first && last) {
            writeToResult(fixedTime, cpt++, start, getFieldValues(MIN));

        // else we write the minimum value at the 1/3 of the step, and the max, at the 2/3 of the step.
        } else {
            // we want to keep the first main value in order to keep the full bounds
            Number minVal = (first) ? start : add(start, div(step, 3));

            // we want to keep the last main value in order to keep the full bounds
            Number maxVal = (last) ? add(start, step) : add(start, mul(div(step, 3), 2));

            //min
            writeToResult(fixedTime, cpt++, minVal, getFieldValues(MIN));
            //max
            writeToResult(fixedTime, cpt++, maxVal, getFieldValues(MAX));
        }
    }
}
