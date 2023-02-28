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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import static org.geotoolkit.observation.json.ObservationJsonUtils.getFieldValue;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.DefaultTemporalPosition;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class TemporalGeometricPrimitiveDeserializer extends JsonDeserializer<TemporalGeometricPrimitive> {

    @Override
    public TemporalGeometricPrimitive deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode rootNode = ctxt.readTree(parser);

        if (rootNode == null || !rootNode.isObject()) {
            throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON object as root node");
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        String id = rootNode.get("id").textValue();

        // Period
        if (rootNode.hasNonNull("beginning") && rootNode.hasNonNull("ending")) {
            JsonNode beginNode = rootNode.get("beginning");
            JsonNode endNode   = rootNode.get("ending");
            try {
                Instant begin = readInstant(parser, sdf, beginNode);
                Instant end   = readInstant(parser, sdf, endNode);
                return new DefaultPeriod(Collections.singletonMap(IdentifiedObject.NAME_KEY, id), begin, end);

            } catch (ParseException ex) {
                throw new JsonMappingException(parser, "Date parsing exception", ex);
            }
        // instant
        } else if (rootNode.hasNonNull("date")) {
            String dateStr = rootNode.get("date").textValue();
            try {
                Date d = sdf.parse(dateStr);
                return new DefaultInstant(Collections.singletonMap(IdentifiedObject.NAME_KEY, id), d);
            } catch (ParseException ex) {
                throw new JsonMappingException(parser, "Date parsing exception", ex);
            }
        } else {
             throw new JsonMappingException(parser, "Invalid JSON : not a period or an instant");
        }
    }

    private Instant readInstant(JsonParser parser, DateFormat sdf, JsonNode instantNode) throws ParseException, IOException {
        String id = getFieldValue(instantNode, "id").orElseThrow(() -> new JsonMappingException(parser, "No instant id available"));
        if (instantNode.hasNonNull("date")) {
            String dateStr    = instantNode.get("date").textValue();
            Date d = sdf.parse(dateStr);
            return new DefaultInstant(Collections.singletonMap(IdentifiedObject.NAME_KEY, id), d);
        } else if (instantNode.hasNonNull("indeterminatePosition")) {
            IndeterminateValue iValue = IndeterminateValue.valueOf(instantNode.get("indeterminatePosition").asText());
            return new DefaultInstant(Collections.singletonMap(IdentifiedObject.NAME_KEY, id), new DefaultTemporalPosition(iValue));
        } else {
            throw new JsonMappingException(parser, "Invalid JSON : missing date or indeterminatePosition for Instant");
        }
    }
}
