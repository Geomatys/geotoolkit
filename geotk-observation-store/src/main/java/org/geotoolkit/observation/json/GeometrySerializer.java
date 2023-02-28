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
import org.apache.sis.internal.feature.jts.JTS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public void serialize(Geometry t, JsonGenerator writer, SerializerProvider serializerProvider) throws IOException {
        try {
            writer.writeStartObject();
            CoordinateReferenceSystem crs = JTS.getCoordinateReferenceSystem(t);
            if (crs != null) {
                String code =  IdentifiedObjects.lookupURN(crs, null);
                writer.writeFieldName("crs");
                writer.writeString(code);
            }
            writer.writeFieldName("geometry");
            writer.writeString(t.toText());
            writer.writeEndObject();
        } catch (FactoryException ex) {
            throw new JsonMappingException(writer, "Error while looking for CRS", ex);
        }
    }
}
