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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@code OffsetDateTime[][]} as a JSON array of arrays of ISO 8601 date-time strings,
 * with {@code null} elements written as JSON null (open-ended interval boundaries).
 *
 * <p>Example output: {@code [["2020-01-01T00:00:00Z", null]]}</p>
 */
public class TemporalExtentIntervalSerializer extends JsonSerializer<OffsetDateTime[][]> {

    @Override
    public void serialize(OffsetDateTime[][] value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeStartArray();
        for (OffsetDateTime[] inner : value) {
            gen.writeStartArray();
            for (OffsetDateTime dt : inner) {
                if (dt == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(dt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                }
            }
            gen.writeEndArray();
        }
        gen.writeEndArray();
    }
}