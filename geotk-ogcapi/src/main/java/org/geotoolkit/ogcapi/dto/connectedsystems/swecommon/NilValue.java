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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A single reserved "nil" value with the reason it is used, shared shape for the standard's
 * {@code NilValuesText}/{@code NilValuesInteger}/{@code NilValuesNumber}/{@code NilValuesTime}
 * component-specific list types (each is just {@code array<NilValue>} with a differently typed
 * {@code value}, so one generic class covers all four here; {@code value} is typed {@code Object}).
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class NilValue extends DataTransferObject {

    public static final String JSON_PROPERTY_REASON = "reason";
    private String reason;

    public static final String JSON_PROPERTY_VALUE = "value";
    private Object value;

    /**
     * URI describing the reason for the nil value. (required)
     */
    @JsonProperty(JSON_PROPERTY_REASON)
    public String getReason() {
        return reason;
    }

    @JsonProperty(JSON_PROPERTY_REASON)
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * The reserved nil value itself (String, Integer, or Number/special token depending on context). (required)
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(Object value) {
        this.value = value;
    }

}
