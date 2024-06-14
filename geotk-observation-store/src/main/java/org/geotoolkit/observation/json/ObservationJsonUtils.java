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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Result;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.quality.Element;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class ObservationJsonUtils {

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new JsonMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        final SimpleModule module = new SimpleModule();
        module.addSerializer(Identifier.class, new IdentifierSerializer());
        module.addSerializer(Geometry.class, new GeometrySerializer());
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addSerializer(Period.class, new PeriodSerializer());
        module.addSerializer(Element.class, new ElementSerializer());
        module.addSerializer(Envelope.class, new EnvelopeSerializer());

        module.addDeserializer(TemporalPrimitive.class, new TemporalGeometricPrimitiveDeserializer());
        module.addDeserializer(Geometry.class, new GeometryDeserializer());
        module.addDeserializer(Result.class, new ResultDeserializer());
        module.addDeserializer(Element.class, new ElementDeserializer());
        module.addDeserializer(Phenomenon.class, new PhenomenonDeserializer());
        module.addDeserializer(Envelope.class, new EnvelopeDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    public static Optional<String> getFieldValue(JsonNode node, String fieldName) {
        if (node.hasNonNull(fieldName)) {
            return Optional.of(node.get(fieldName).asText());
        }
        return Optional.empty();
    }

    public static Optional<Integer> getIntFieldValue(JsonNode node, String fieldName) {
        if (node.hasNonNull(fieldName)) {
            return Optional.of(node.get(fieldName).asInt());
        }
        return Optional.empty();
    }

    public static Map<String, Object> readProperties(JsonNode rootNode) {
        JsonNode propNode = rootNode.get("properties");
        Map<String, Object> properties = null;
        if (propNode != null) {
            properties = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = propNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                Object value = readNodeValue(next.getValue());
                properties.put(next.getKey(), value);
            }
        }
        return properties;
    }

    private static Object readNodeValue(JsonNode node) {
        JsonNodeType nodeType = Objects.requireNonNull(node, "Json node to read").getNodeType();
        return switch (nodeType) {
            case BOOLEAN          -> node.asBoolean();
            case MISSING, NULL    -> null;
            case STRING           -> node.asText();
            case NUMBER           -> node.asDouble();
            case ARRAY            -> StreamSupport.stream(node.spliterator(), false)
                                       .map(ObservationJsonUtils::readNodeValue)
                                       .toList();
            case OBJECT, POJO     -> throw new UnsupportedOperationException("Not supported yet: object node");
            case BINARY           -> throw new UnsupportedOperationException("Not supported yet: binary data");
        };
    }
}
