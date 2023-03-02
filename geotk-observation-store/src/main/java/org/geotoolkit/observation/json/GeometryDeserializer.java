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
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import static org.geotoolkit.observation.json.ObservationJsonUtils.getFieldValue;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class GeometryDeserializer extends JsonDeserializer<Geometry> {

    @Override
    public Geometry deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        try {
            final JsonNode rootNode = ctxt.readTree(parser);

            if (rootNode == null || !rootNode.isObject()) {
                throw new JsonMappingException(parser, "Invalid JSON : Expecting JSON object as root node");
            }
            CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
            if (rootNode.hasNonNull("crs")) {
                 String crsId = rootNode.get("crs").textValue();
                 crs = CRS.forCode(crsId);
            }

            String wkt = getFieldValue(rootNode, "geometry").orElseThrow(() -> new JsonMappingException(parser, "No geometry available"));
            Geometry geom = new WKTReader().read(wkt);
            JTS.setCRS(geom, crs);
            return geom;
        } catch (ParseException | FactoryException ex) {
            throw new JsonMappingException(parser, "Geometry parsing exception", ex);
        }
    }
}
