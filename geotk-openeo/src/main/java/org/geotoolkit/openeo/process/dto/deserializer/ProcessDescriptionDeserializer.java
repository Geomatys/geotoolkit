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

public class ProcessDescriptionDeserializer extends JsonDeserializer<ProcessDescription> {

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
                    return new ProcessDescriptionArgument(jsonParser.getCodec().readValue(valueNode.traverse(jsonParser.getCodec()), BoundingBox.class), ProcessDescriptionArgument.ArgumentType.VALUE);
                } catch (IOException e) {}

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
}
