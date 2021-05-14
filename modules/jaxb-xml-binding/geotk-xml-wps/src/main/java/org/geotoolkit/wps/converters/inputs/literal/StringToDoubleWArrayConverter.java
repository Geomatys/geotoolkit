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
 * Convert a String to an array of double.
 * Double in String should be separated by a coma like this : "13.5, 5.8, 182.556, 88.0".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToDoubleWArrayConverter extends StringToNumberSequenceConverter<Double[]> {

    @Override
    public Class<Double[]> getTargetClass() {
        return Double[].class;
    }

    @Override
    protected Double[] convertSequence(Stream<String> values) {
        return values
                .map(StringToDoubleWArrayConverter::valueOf)
                .toArray(Double[]::new);
    }

    private static Double valueOf(final String token) {
        if (token.isEmpty()) return Double.NaN;
        else return Double.valueOf(token);
    }
}
