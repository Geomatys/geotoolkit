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
import java.util.Map;
import static org.geotoolkit.observation.json.ObservationJsonUtils.*;
import org.geotoolkit.observation.model.CompositePhenomenon;
import org.geotoolkit.observation.model.Phenomenon;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class PhenomenonDeserializer extends JsonDeserializer<Phenomenon> {

    @Override
    public Phenomenon deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode rootNode = ctxt.readTree(parser);

        if (rootNode == null || !rootNode.isObject()) {
            throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON object as root node");
        }

        // composite
        if (rootNode.hasNonNull("component")) {
            return ctxt.readTreeAsValue(rootNode, CompositePhenomenon.class);
        } else {
            String id          = getFieldValue(rootNode,"id").orElseThrow(() -> new JsonMappingException(parser, "No id available"));
            String name        = getFieldValue(rootNode,"name").orElseThrow(() -> new JsonMappingException(parser, "No name available"));
            String definition  = getFieldValue(rootNode, "definition").orElse(null);
            String description = getFieldValue(rootNode, "description").orElse(null);
            Map<String, Object> properties = readProperties(rootNode);
            return new Phenomenon(id, name, definition, description, properties);
        }
    }

}
