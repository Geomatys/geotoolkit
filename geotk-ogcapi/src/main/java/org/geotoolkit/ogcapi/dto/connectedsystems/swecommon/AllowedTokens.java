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
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Value constraint for text-like components (Category, Text): either an enumeration of allowed
 * token values, or a regular expression pattern.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class AllowedTokens extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type = "AllowedTokens";

    public static final String JSON_PROPERTY_VALUES = "values";
    private List<String> values;

    public static final String JSON_PROPERTY_PATTERN = "pattern";
    private String pattern;

    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Enumeration of allowed token values. (one of values/pattern)
     */
    @JsonProperty(JSON_PROPERTY_VALUES)
    public List<String> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Regular expression the value must match. (one of values/pattern)
     */
    @JsonProperty(JSON_PROPERTY_PATTERN)
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(JSON_PROPERTY_PATTERN)
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
