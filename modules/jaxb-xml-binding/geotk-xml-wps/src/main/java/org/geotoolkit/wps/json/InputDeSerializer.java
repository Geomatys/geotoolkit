/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class InputDeSerializer extends StdDeserializer<InputBase> {

    public InputDeSerializer() {
        this(null);
    }

    public InputDeSerializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public InputBase deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode bboxNode = node.get("bbox");
        JsonNode formNode = node.get("format");
        JsonNode valueNode = node.get("value");

        if (bboxNode != null) {
            String crs = null;
            List<Double> bbox = new ArrayList<>();
            JsonNode crsNode = node.get("crs");
            if (crsNode instanceof TextNode) {
                crs = crsNode.asText();
            }
            if (bboxNode instanceof ArrayNode) {
                ArrayNode an = (ArrayNode) bboxNode;
                Iterator<JsonNode> it = an.elements();
                while (it.hasNext()) {
                    JsonNode n = it.next();
                    if (n instanceof NumericNode) {
                        bbox.add(((NumericNode)n).doubleValue());
                    }
                }
            }
            return new BoundingBoxInput(crs, bbox);
        } else if (formNode != null) {
            Format format = readFormat(formNode);
            ValueType value = readValue(valueNode);
            return new ComplexInput(format, value);
        } else {
            String value = null;
            if (valueNode instanceof TextNode) {
                value = valueNode.asText();
            }
            NameReferenceType dataType = readNameReference(node.get("dataType"));
            NameReferenceType uomType  = readNameReference(node.get("uomType"));
            return new LiteralInput(value, dataType, uomType);
        }
    }

    private NameReferenceType readNameReference(JsonNode n) {
        if (n != null) {
            String name = null;
            if (n.get("name") instanceof TextNode) {
                name = n.get("name").asText();
            }
            String href = null;
            if (n.get("reference") instanceof TextNode) {
                href = n.get("reference").asText();
            }
            return new NameReferenceType(name, href);
        }
        return null;
    }

    private ValueType readValue(JsonNode n) {
        if (n != null) {
            String inlineValue = null;
            if (n.get("inlineValue") instanceof TextNode) {
                inlineValue = n.get("inlineValue").asText();
            }
            String href = null;
            if (n.get("href") instanceof TextNode) {
                href = n.get("href").asText();
            }
            return new ValueType(inlineValue, href);
        }
        return null;
    }


    private Format readFormat(JsonNode n) {
        if (n != null) {
            String mimeType = null;
            if (n.get("mimeType") instanceof TextNode) {
                mimeType = n.get("mimeType").asText();
            }
            String schema = null;
            if (n.get("schema") instanceof TextNode) {
                schema = n.get("schema").asText();
            }
            String encoding = null;
            if (n.get("encoding") instanceof TextNode) {
                schema = n.get("encoding").asText();
            }
            return new Format(mimeType, schema, encoding);
        }
        return null;
    }
}


