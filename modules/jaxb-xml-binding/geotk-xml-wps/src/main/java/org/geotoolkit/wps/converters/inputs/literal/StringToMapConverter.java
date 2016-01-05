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
package org.geotoolkit.wps.converters.inputs.literal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;

import java.io.IOException;
import java.util.Map;

/**
 * Converter between a JSON string representing a {@linkplain Map map}
 * and its matching object.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class StringToMapConverter extends SimpleConverter<String, Map> {
    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Map> getTargetClass() {
        return Map.class;
    }

    @Override
    public Map apply(final String source) throws UnconvertibleObjectException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            // TODO: use a more complex serialization in order to handle a map with object values
            return mapper.readValue(source, Map.class);
        } catch (IOException e) {
            throw new UnconvertibleObjectException("Invalid source String : "+source);
        }
    }
}
