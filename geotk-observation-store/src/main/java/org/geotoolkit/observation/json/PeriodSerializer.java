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
import org.apache.sis.temporal.TemporalObjects;
import org.apache.sis.xml.IdentifiedObject;
import org.apache.sis.xml.IdentifierSpace;
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
        writer.writeString(InstantSerializer.getIdentifier(p));

        Instant begin = null, end = null;
        Instant i = p.getBeginning();
        if (i != null) begin = copy(i);
        i = p.getEnding();
        if (i != null) end = copy(i);
        if (begin != null) {
            writer.writeFieldName("beginning");
            ObservationJsonUtils.writeInstant(writer, begin);
        }
        if (end != null) {
            writer.writeFieldName("ending");
            ObservationJsonUtils.writeInstant(writer, end);
        }
        writer.writeEndObject();
    }

    private static Instant copy(Instant i) {
        var t = TemporalObjects.createInstant(i.getPosition());
        if (t != null) {
            if (i instanceof IdentifiedObject m) {
                ((IdentifiedObject) t).getIdentifierMap().putAll(m.getIdentifierMap());
            } else if (i instanceof org.opengis.referencing.IdentifiedObject m) {
                ((IdentifiedObject) t).getIdentifierMap().putSpecialized(IdentifierSpace.ID, m.getName().getCode());
            }
        }
        return t;
    }
}

