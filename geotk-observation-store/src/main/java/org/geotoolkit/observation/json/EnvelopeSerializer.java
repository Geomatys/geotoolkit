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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.apache.sis.referencing.IdentifiedObjects;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class EnvelopeSerializer extends JsonSerializer<Envelope> {

    @Override
    public void serialize(Envelope env, JsonGenerator writer, SerializerProvider serializers) throws IOException {
        try {
            writer.writeStartObject();
            if (env.getCoordinateReferenceSystem() != null) {
                String crsName = IdentifiedObjects.lookupURN(env.getCoordinateReferenceSystem(), null);
                writer.writeFieldName("crs");
                writer.writeString(crsName);
            }
            DirectPosition uc = env.getUpperCorner();
            if (uc != null) {
                writer.writeFieldName("upperCorner");
                writer.writeArray(uc.getCoordinates(), 0, uc.getDimension());
            }
            DirectPosition lc = env.getLowerCorner();
            if (lc != null) {
                writer.writeFieldName("lowerCorner");
                writer.writeArray(lc.getCoordinates(), 0, lc.getDimension());
            }
            writer.writeEndObject();
        } catch (FactoryException ex) {
            throw new JsonMappingException(writer, "Error while looking for CRS", ex);
        }
    }

}
