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
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.apache.sis.xml.IdentifiedObject;
import org.apache.sis.xml.IdentifierSpace;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class InstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant i, JsonGenerator writer, SerializerProvider serializerProvider) throws IOException {
        ObservationJsonUtils.writeInstant(writer, i);
    }

    static String getIdentifier(final TemporalPrimitive t) {
        if (t instanceof IdentifiedObject i) {
            String id = i.getIdentifierMap().getSpecialized(IdentifierSpace.ID);
            if (id != null) return id;
        }
        return "unnamed";
    }
}
