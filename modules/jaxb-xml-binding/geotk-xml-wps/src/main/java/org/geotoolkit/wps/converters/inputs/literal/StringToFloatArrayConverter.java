/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.literal;

import java.util.stream.Stream;

/**
 * Convert a String to an array of float.
 * Double in String should be separated by a coma like this : "13.6, 5.4, 182.1, 88.0".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToFloatArrayConverter extends StringToNumberSequenceConverter<float[]> {

    @Override
    public Class<float[]> getTargetClass() {
        return float[].class;
    }

    @Override
    protected float[] convertSequence(Stream<String> values) {
        final Float[] intermediateValues = values
                .map(StringToFloatWArrayConverter::valueOf)
                .toArray(Float[]::new);

        if (intermediateValues.length < 1) return new float[0];
        else if (intermediateValues.length == 1) return new float[] {intermediateValues[0]};
        else {
            float[] result = new float[intermediateValues.length];
            for (int i = 0 ; i < result.length ; i++) {
                result[i] = intermediateValues[i];
            }
            return result;
        }
    }
}
