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
import java.util.ArrayList;
import static org.geotoolkit.observation.json.ObservationJsonUtils.*;
import org.geotoolkit.observation.model.ComplexResult;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldDataType;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.observation.model.MeasureResult;
import org.geotoolkit.observation.model.Result;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class ResultDeserializer extends JsonDeserializer<Result> {

    @Override
    public Result deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode rootNode = ctxt.readTree(parser);

        if (rootNode == null || !rootNode.isObject()) {
            throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON object as root node");
        }
        // measure result
        if (rootNode.hasNonNull("field")) {
            JsonNode fieldNode = rootNode.get("field");
            int index          = getIntFieldValue(fieldNode, "index").orElseThrow(() -> new JsonMappingException(parser, "No index available"));
            FieldDataType ft   = getFieldValue(fieldNode, "dataType").map(t -> FieldDataType.valueOf(t)).orElseThrow(() -> new JsonMappingException(parser, "No type available"));
            String name        = getFieldValue(fieldNode, "name").orElseThrow(() -> new JsonMappingException(parser, "No name available"));
            String label       = getFieldValue(fieldNode, "label").orElse(null);
            String description = getFieldValue(fieldNode, "description").orElse(null);
            String uom         = getFieldValue(fieldNode, "uom").orElse(null);
            // TODO "qualityFields": []
            // TODO "parameterFields": []
            Field f = new Field(index, ft, name, label, description, uom, FieldType.MEASURE, new ArrayList<>(), new ArrayList<>());
            Object value = null;
            if (rootNode.hasNonNull("value")) {
                switch (ft) {
                    case BOOLEAN  -> value = rootNode.get("value").asBoolean();
                    case QUANTITY -> value = rootNode.get("value").asDouble();
                    case TEXT     -> value = rootNode.get("value").asText();
                    case TIME     -> {
                        try {
                            String dateStr = rootNode.get("value").asText();
                            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
                            value = sdf.parse(dateStr);
                        } catch (ParseException ex) {
                            throw new JsonMappingException(parser, "Date parsing exception", ex);
                        }
                    }

                };
            }
            return new MeasureResult(f, value);
        }
        return ctxt.readTreeAsValue(rootNode, ComplexResult.class);
    }

}
