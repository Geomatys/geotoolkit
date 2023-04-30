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
import org.opengis.metadata.quality.Element;
import org.opengis.metadata.quality.QuantitativeAttributeAccuracy;
import org.opengis.metadata.quality.QuantitativeResult;
import org.opengis.metadata.quality.Result;
import org.opengis.util.Record;
import org.opengis.util.RecordType;

/**
 *
 *  @author Guilhem Legal (geomatys)
 */
public class ElementSerializer extends JsonSerializer<Element> {

    @Override
    public void serialize(Element value, JsonGenerator writer, SerializerProvider serializers) throws IOException {
        if (value instanceof QuantitativeAttributeAccuracy qaa) {
            if (qaa.getNamesOfMeasure().isEmpty()) {
                throw new JsonMappingException(writer, "Incomplete QuantitativeAttributeAccuracy, missing namesOfMeasure.");
            }
            String name = qaa.getNamesOfMeasure().iterator().next().toString();
            writer.writeStartObject();
            writer.writeFieldName("name");
            writer.writeString(name);
            if (!qaa.getResults().isEmpty()) {
                 Result result = qaa.getResults().iterator().next();
                if (result instanceof QuantitativeResult qResult) {
                    if (qResult.getValueUnit() != null) {
                        writer.writeFieldName("uom");
                        writer.writeString(qResult.getValueUnit().getSymbol());
                    }
                    if (!qResult.getValues().isEmpty()) {
                        Record rec = qResult.getValues().iterator().next();
                        RecordType rt = rec.getRecordType();
                        Object recValue = rec.getFields().get(rt.getMembers().iterator().next());
                        writer.writeFieldName("value");
                        if (recValue instanceof Boolean bVal) {
                            writer.writeBoolean(bVal);
                        } else if (recValue instanceof Number num) {
                            writer.writeNumber(num.toString());
                        } else if (recValue != null) {
                            writer.writeString(recValue.toString());
                        }
                    }
                }
            }
            writer.writeEndObject();
        } else {
            throw new JsonMappingException(writer, "Element implemention not suported, only QuantitativeAttributeAccuracy.");
        }
    }
}
