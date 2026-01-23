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
package org.geotoolkit.openeo.process.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.geotoolkit.openeo.process.dto.DataTypeSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataTypeSchemaTypeDeserializer extends JsonDeserializer<List<DataTypeSchema.Type>> {

    @Override
    public List<DataTypeSchema.Type> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonMappingException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        if (jsonNode.isTextual()) {
            List<DataTypeSchema.Type> typeList = new ArrayList<>();
            typeList.add(DataTypeSchema.Type.fromValue(jsonNode.asText(), false));
            return typeList;

        } else if (jsonNode.isArray()) {
            ArrayNode typeArray = (ArrayNode) jsonNode;
            List<DataTypeSchema.Type> typeList = new ArrayList<>();
            for (JsonNode typeNode : typeArray) {
                typeList.add(DataTypeSchema.Type.fromValue(typeNode.asText(), false));
            }
            return typeList;

        } else {
            // If type is not a string and not a list of string, throw an exception
            throw new JsonMappingException(jsonParser, "The property \"type\" is not a string or a list of string");
        }
    }
}
