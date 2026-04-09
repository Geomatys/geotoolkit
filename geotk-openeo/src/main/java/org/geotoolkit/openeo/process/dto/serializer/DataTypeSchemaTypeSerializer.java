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
package org.geotoolkit.openeo.process.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.geotoolkit.openeo.process.dto.DataTypeSchema;

import java.io.IOException;
import java.util.List;

/**
 * Serializes a {@code List<DataTypeSchema.Type>} following the openEO / JSON Schema convention:
 * <ul>
 *   <li>Empty or null list → {@code null} (field omitted when {@code @JsonInclude(NON_NULL)}).</li>
 *   <li>Single-element list → a plain JSON string, e.g. {@code "number"}.</li>
 *   <li>Multi-element list → a JSON array, e.g. {@code ["number", "null"]}.</li>
 * </ul>
 */
public class DataTypeSchemaTypeSerializer extends JsonSerializer<List<DataTypeSchema.Type>> {

    @Override
    public void serialize(List<DataTypeSchema.Type> types, JsonGenerator gen, SerializerProvider providers)
            throws IOException {

        if (types == null || types.isEmpty()) {
            gen.writeNull();
            return;
        }

        if (types.size() == 1) {
            // Preferred form for single types: plain string
            gen.writeString(types.get(0).getValue());
        } else {
            // Multiple types: array form  ["number", "null"]
            gen.writeStartArray();
            for (DataTypeSchema.Type t : types) {
                gen.writeString(t.getValue());
            }
            gen.writeEndArray();
        }
    }
}