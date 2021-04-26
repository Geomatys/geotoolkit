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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert a String to an array of boolean.
 * Boolean in String should be separated by a coma like this : "true, false, true, false".
 * Return an empty array if source is null or empty.
 *
 * @author Guilhem Legal
 */
public class StringToBooleanArrayConverter extends StringToNumberSequenceConverter<boolean[]> {

    @Override
    public Class<boolean[]> getTargetClass() {
        return boolean[].class;
    }

    @Override
    protected boolean[] convertSequence(Stream<String> values) {
        final Boolean[] booleanList = values.map(Boolean::valueOf)
                .toArray(Boolean[]::new);

        if (booleanList.length < 1) return new boolean[0];
        else if (booleanList.length == 1) return new boolean[] { booleanList[0] };
        else {
            final boolean[] outArray = new boolean[booleanList.length];
            for (int i = 0; i < booleanList.length; i++) {
                outArray[i] = booleanList[i];
            }
            return outArray;
        }
    }
}
