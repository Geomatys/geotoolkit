/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.observation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class PeriodSerializer extends JsonSerializer<Period> {

    @Override
    public void serialize(Period p, JsonGenerator writer, SerializerProvider serializerProvider) throws IOException {
        writer.writeStartObject();
        writer.writeFieldName("id");
        writer.writeString(p.getName().getCode());

        Instant begin = p.getBeginning();
        if (begin != null) {
            writer.writeFieldName("beginning");
            writeInstant(writer, begin);
        }
        Instant end = p.getEnding();
        if (end != null) {
            writer.writeFieldName("ending");
            writeInstant(writer, end);
        }
        writer.writeEndObject();
    }


    private static void writeInstant(JsonGenerator writer, Instant i) throws IOException {
        writer.writeStartObject();
        writer.writeFieldName("id");
        writer.writeString(i.getName().getCode());
        if (i.getDate() != null) {
            writer.writeFieldName("date");
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            writer.writeString(sdf.format(i.getDate()));
        } else if (i.getTemporalPosition() != null && i.getTemporalPosition().getIndeterminatePosition().isPresent()) {
            writer.writeFieldName("indeterminatePosition");
            writer.writeString(i.getTemporalPosition().getIndeterminatePosition().get().name());
        } else {
            throw new JsonMappingException(writer, "Instant must contains at least a date or an indeterminate position.");
        }
        writer.writeEndObject();
    }
}
