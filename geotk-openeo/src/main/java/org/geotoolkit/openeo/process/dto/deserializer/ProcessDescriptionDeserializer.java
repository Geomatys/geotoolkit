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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.geotoolkit.openeo.process.dto.BoundingBox;
import org.geotoolkit.openeo.process.dto.ProcessDescription;
import org.geotoolkit.openeo.process.dto.ProcessDescriptionArgument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ProcessDescriptionDeserializer extends JsonDeserializer<ProcessDescription> {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.openeo.process.dto.deserializer");

    @Override
    public ProcessDescription deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonMappingException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String processId = null;
        if(jsonNode.get("process_id") != null) {
            processId = jsonNode.get("process_id").asText();
        }

        String title = null;
        if(jsonNode.get("title") != null) {
            title = jsonNode.get("title").asText();
        }

        String description = null;
        if(jsonNode.get("description") != null) {
            description = jsonNode.get("description").asText();
        }

        Map<String, ProcessDescriptionArgument> arguments = deserializeArguments(jsonNode.get("arguments"), jsonParser);

        Object returns = null;
        Boolean result = null;
        if (jsonNode.hasNonNull("returns")) {
            returns = jsonNode.get("returns").traverse(jsonParser.getCodec());
        }
        if (jsonNode.hasNonNull("result")) {
            result = jsonNode.get("result").asBoolean();
        } else {
            result = false;
        }

        return new ProcessDescription(processId, title, description, arguments, returns, result);
    }

    private Map<String, ProcessDescriptionArgument> deserializeArguments(JsonNode argumentsNode, JsonParser jsonParser) throws JsonMappingException {
        Map<String, ProcessDescriptionArgument> arguments = new HashMap<>();
        if (argumentsNode.isObject()) {
            for (Iterator<String> it = argumentsNode.fieldNames(); it.hasNext(); ) {
                String key = it.next();
                JsonNode valueNode = argumentsNode.get(key);
                ProcessDescriptionArgument value = deserializeArgument(valueNode, jsonParser);
                arguments.put(key, value);
            }
        }
        return arguments;
    }

    private ProcessDescriptionArgument deserializeArgument(JsonNode valueNode, JsonParser jsonParser) throws JsonMappingException {
        if (valueNode.isObject()) {
            TextNode fromNode = (TextNode) valueNode.get("from_node");
            TextNode fromParameterNode = (TextNode) valueNode.get("from_parameter");
            ObjectNode processGraphNode = (ObjectNode) valueNode.get("process_graph");

            if (fromNode != null) {
                return new ProcessDescriptionArgument(fromNode.asText(), ProcessDescriptionArgument.ArgumentType.FROM_NODE);

            } else if (fromParameterNode != null) {
                return new ProcessDescriptionArgument(fromParameterNode.asText(), ProcessDescriptionArgument.ArgumentType.FROM_PARAMETER);

            } else if (processGraphNode != null) { //TODO: Support subprocess graphs
                throw new JsonMappingException(jsonParser, "Process graph is not supported");

            } else {
                try { //Bounding Box Object
                    if (isStrictlyBoundingBox(valueNode)) {
                        LOGGER.info("The argument is a bounding box");
                        return new ProcessDescriptionArgument(
                                jsonParser.getCodec().readValue(valueNode.traverse(jsonParser.getCodec()), BoundingBox.class),
                                ProcessDescriptionArgument.ArgumentType.VALUE
                        );
                    } else {
                        // Force a jump to the catch block to use the Object fallback
                        LOGGER.info("Node contains extra fields, treat as generic Object");
                    }
                } catch (IOException e) {
                    LOGGER.warning("Error while reading bounding box argument, trying to read it as a generic value");
                }

                try {
                    return new ProcessDescriptionArgument(jsonParser.getCodec().readValue(valueNode.traverse(jsonParser.getCodec()), Object.class), ProcessDescriptionArgument.ArgumentType.VALUE);
                } catch (IOException e) {
                    throw new JsonMappingException(jsonParser, "Impossible to read the value of the argument");
                }
                //throw new JsonMappingException(jsonParser, "Invalid argument format. Expected 'from_node', 'from_parameter' or 'process_graph'");
            }

        } else if (valueNode.isArray()) {
            List<ProcessDescriptionArgument> nestedArguments = new ArrayList<>();

            for (JsonNode elementNode : valueNode) {
                ProcessDescriptionArgument nestedArgument = deserializeArgument(elementNode, jsonParser);
                nestedArguments.add(nestedArgument);
            }
            return new ProcessDescriptionArgument(nestedArguments, ProcessDescriptionArgument.ArgumentType.ARRAY);

        } else {
            try {
                return new ProcessDescriptionArgument(jsonParser.getCodec().readValue(valueNode.traverse(jsonParser.getCodec()), Object.class), ProcessDescriptionArgument.ArgumentType.VALUE);
            } catch (IOException e) {
                throw new JsonMappingException(jsonParser, "Impossible to read the value of the argument");
            }
        }
    }

    /**
     * Helper method to check if the JSON contains ONLY BoundingBox keys.
     */
    private boolean isStrictlyBoundingBox(JsonNode node) {
        if (!node.isObject()) return false;

        // Define the "allowed" keys for a BoundingBox
        java.util.Set<String> allowedKeys = java.util.Set.of(
                "west", "south", "east", "north", "base", "height", "crs"
        );

        java.util.Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!allowedKeys.contains(fieldName)) {
                return false;
            }
        }

        // Also ensure it has the mandatory keys (optional, but safer)
        return node.has("west") && node.has("south") && node.has("east") && node.has("north");
    }
}
