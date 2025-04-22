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
import org.geotoolkit.observation.OMUtils;
import static org.geotoolkit.observation.json.ObservationJsonUtils.getFieldValue;
import org.geotoolkit.observation.model.FieldDataType;
import org.opengis.metadata.quality.Element;

/**
 *
 *  @author Guilhem Legal (geomatys)
 */
public class ElementDeserializer extends JsonDeserializer<Element> {

    @Override
    public Element deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode rootNode = ctxt.readTree(parser);

        if (rootNode == null || !rootNode.isObject()) {
            throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON object as root node");
        }
        String name  = getFieldValue(rootNode, "name").orElseThrow(() -> new JsonMappingException(parser, "No name available"));
        String uom   = getFieldValue(rootNode, "uom").orElse(null);
        FieldDataType ft = null;
        Object value = null;
        if (rootNode.hasNonNull("value")) {
            JsonNode vNode = rootNode.get("value");
            if (vNode.isDouble()) {
                ft = FieldDataType.QUANTITY;
                value = vNode.asDouble();
            } else if (vNode.isBoolean()) {
                ft = FieldDataType.BOOLEAN;
                value = vNode.asBoolean();

            // TODO time?
            }  else {
                ft = FieldDataType.TEXT;
                value = vNode.asText();
            }
        }
        return OMUtils.createQualityElement(name, uom, ft, value);
    }

}
