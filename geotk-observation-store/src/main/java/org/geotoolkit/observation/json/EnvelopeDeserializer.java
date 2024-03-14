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
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class EnvelopeDeserializer extends JsonDeserializer<Envelope> {

    @Override
    public Envelope deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final JsonNode rootNode = ctxt.readTree(parser);

        if (rootNode == null || !rootNode.isObject()) {
            throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON object as root node");
        }
        GeneralEnvelope result;
        try {
            CoordinateReferenceSystem crs;
            if (rootNode.hasNonNull("crs")) {
                crs = CRS.forCode(rootNode.get("crs").asText());
            } else {
                crs = CommonCRS.defaultGeographic();
            }
            DirectPosition upper = readDirectPosition(parser, rootNode.get("upperCorner"), crs);
            DirectPosition lower = readDirectPosition(parser, rootNode.get("lowerCorner"), crs);
            result = new GeneralEnvelope(lower, upper);
            result.setCoordinateReferenceSystem(crs);
        } catch (FactoryException ex) {
            throw new JsonMappingException(parser, "Cannot create Enveloe object due to SIS CRS problem", ex);
        }
        return result;
    }

    private DirectPosition readDirectPosition(JsonParser parser, JsonNode posNode, CoordinateReferenceSystem crs) throws IOException {
        if (posNode == null || !posNode.isArray()) {
            throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON Array for Upper/lower corner must be an array");
        }
        GeneralDirectPosition pos = new GeneralDirectPosition(crs);
        int i = 0;
        for (JsonNode c : posNode) {
            pos.setCoordinate(i, c.asDouble());
        }
        return pos;
    }


}
