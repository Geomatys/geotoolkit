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

import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert a String to an array of long.
 * Double in String should be separated by a coma like this : "13, 5, 182, 88".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToLongArrayConverter extends StringToNumberSequenceConverter<long[]> {

    @Override
    public Class<long[]> getTargetClass() {
        return long[].class;
    }

    @Override
    protected long[] convertSequence(Stream<String> values) {
        return values
                .mapToLong(Long::parseLong)
                .toArray();
    }
}
