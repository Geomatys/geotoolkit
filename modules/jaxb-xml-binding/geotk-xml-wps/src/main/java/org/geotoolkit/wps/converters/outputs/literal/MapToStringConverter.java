/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;

import java.io.IOException;
import java.util.Map;

/**
 * Converter between a {@linkplain Map map} and return a JSON string representation.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class MapToStringConverter extends SimpleConverter<Map, String> {
    @Override
    public Class<Map> getSourceClass() {
        return Map.class;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public String apply(final Map source) throws UnconvertibleObjectException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            // TODO: use a more complex serialization in order to handle a map with object values
            return mapper.writeValueAsString(source);
        } catch (IOException e) {
            throw new UnconvertibleObjectException("Invalid source : "+source);
        }
    }
}
