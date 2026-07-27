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
package org.geotoolkit.ogcapi.dto.connectedsystems.common.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * Deserializes a {@link TimePeriod} from a 2 element JSON array {@code [begin, end]}.
 */
public class TimePeriodDeserializer extends JsonDeserializer<TimePeriod> {

    @Override
    public TimePeriod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) {
            return null;
        }
        final String begin = node.size() > 0 && !node.get(0).isNull() ? node.get(0).asText() : null;
        final String end = node.size() > 1 && !node.get(1).isNull() ? node.get(1).asText() : null;
        return new TimePeriod(begin, end);
    }
}
