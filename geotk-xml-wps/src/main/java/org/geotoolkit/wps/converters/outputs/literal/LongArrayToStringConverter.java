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
package org.geotoolkit.wps.converters.outputs.literal;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.sis.util.ObjectConverter;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.inputs.literal.StringToLongArrayConverter;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class LongArrayToStringConverter extends SimpleConverter<long[], String> {

    @Override
    public Class<long[]> getSourceClass() {
        return long[].class;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public String apply(final long[] source) throws UnconvertibleObjectException {
        if (source == null)
            return null;
        return Arrays.stream(source)
                .mapToObj(Long::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public ObjectConverter<String, long[]> inverse() throws UnsupportedOperationException {
        return new StringToLongArrayConverter();
    }
}
