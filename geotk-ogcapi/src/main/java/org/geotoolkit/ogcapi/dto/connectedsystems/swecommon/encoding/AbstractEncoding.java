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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.encoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Base of the four SWE Common data encoding types.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = TextEncoding.class, name = "TextEncoding"),
    @Type(value = JsonEncoding.class, name = "JSONEncoding"),
    @Type(value = XmlEncoding.class, name = "XMLEncoding"),
    @Type(value = BinaryEncoding.class, name = "BinaryEncoding")
})
public abstract class AbstractEncoding extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Discriminator identifying the concrete encoding type. (required)
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

}
