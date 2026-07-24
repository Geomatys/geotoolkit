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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.encoding.AbstractEncoding;

/**
 * The SWE Common "DataStream" component: describes a stream of repeated records. Distinct from
 * {@link org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.DataStream}, the OGC API - Connected
 * Systems Part 2 REST resource of the same name.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class DataStream extends AbstractSweIdentifiable {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type = "DataStream";

    public static final String JSON_PROPERTY_ELEMENT_TYPE = "elementType";
    private AbstractDataComponent elementType;

    public static final String JSON_PROPERTY_ENCODING = "encoding";
    private AbstractEncoding encoding;

    public static final String JSON_PROPERTY_VALUES = "values";
    private JsonNode values;

    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Named component describing each record of the stream. (required)
     */
    @JsonProperty(JSON_PROPERTY_ELEMENT_TYPE)
    public AbstractDataComponent getElementType() {
        return elementType;
    }

    @JsonProperty(JSON_PROPERTY_ELEMENT_TYPE)
    public void setElementType(AbstractDataComponent elementType) {
        this.elementType = elementType;
    }

    /**
     * Encoding used for the stream's values. (required)
     */
    @JsonProperty(JSON_PROPERTY_ENCODING)
    public AbstractEncoding getEncoding() {
        return encoding;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING)
    public void setEncoding(AbstractEncoding encoding) {
        this.encoding = encoding;
    }

    /**
     * Encoded values, inline or by reference. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALUES)
    public JsonNode getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    public void setValues(JsonNode values) {
        this.values = values;
    }

}
