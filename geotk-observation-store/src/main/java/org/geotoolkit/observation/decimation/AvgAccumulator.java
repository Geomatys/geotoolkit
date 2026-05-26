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

import java.util.ArrayList;
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
 * Values accumulator that compute the average value of a slice.
 * This class is not thread-safe.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AvgAccumulator extends ValuesAccumulator {

    /**
     * retain all the values accumulated for each fields.
     */
    final Map<String, List<Double>> mapValues;

    /**
     * Build a new Average values accumulator.
     *
     * @param start lower bound of the current slice.
     * @param step length of the current slice.
     * @param fields List of the result fields.
     * @param identifierPrefix Prefix to apply to an identifier field.
     * @param builder Result builder where to append the field values.
     * @param skipProfileMain Special flag when we don't want to write the main field.
     * @param onlyProfileMain Special flag when we want to write only the main field.
     */
    public AvgAccumulator(Number start, Number step, final List<Field> fields, String obsName, ResultBuilder builder, boolean skipProfileMain, boolean onlyProfileMain) {
        super(start, step, fields, obsName, builder, skipProfileMain, onlyProfileMain);
        this.mapValues = fields.stream()
                .filter(f -> FieldType.MEASURE.equals(f.getType()))
                .collect(Collectors.toMap(
                    field -> field.getName(),
                    key -> new ArrayList<>())
                );

    }

    @Override
    protected void clearValues() {
        for (String key : mapValues.keySet()) {
            mapValues.put(key, new ArrayList<>());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void append(final Number mainValue, final String field, final double value) {

        mainValues.add(mainValue);

        final List<Double> previous = mapValues.get(field);
        if (previous == null) {
            throw new IllegalArgumentException("Unknown field: " + field);
        }
        previous.add(value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(Long fixedTime, boolean first, boolean last) throws DataStoreException {
        Map<String, Double> fieldValues = new HashMap<>();
        mapValues.forEach((k, v) -> {
            double avg = v.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(Double.NaN);
            fieldValues.put(k, avg);
        });

        // if there is only one value in the step, we use the original main value.
        if (mainValues.size() == 1) {
            writeToResult(fixedTime, cpt++, mainValues.iterator().next(), fieldValues);

        // special case where we have only one value in the series, main value has not been recorded
        } else if (first && last) {
            writeToResult(fixedTime, cpt++, start, fieldValues);

        // else we write the avg value at the middle of the step.
        } else {
            // we want to keep the first main value in order to keep the full bounds
            if (first) {
                writeToResult(fixedTime, cpt++, start, fieldValues);

            // we want to keep the last main value in order to keep the full bounds
            } else if (last) {
                writeToResult(fixedTime, cpt++, add(start, step), fieldValues);
            } else {
                // append mid avg value
                writeToResult(fixedTime, cpt++, add(start, div(step, 2)), fieldValues);
            }
        }
    }
}
