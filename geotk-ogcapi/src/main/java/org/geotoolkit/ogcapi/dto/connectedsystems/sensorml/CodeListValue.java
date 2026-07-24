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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A single value taken from a controlled code list, used by {@link LegalConstraint}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class CodeListValue extends DataTransferObject {

    public static final String JSON_PROPERTY_CODE_SPACE = "codeSpace";
    private String codeSpace;

    public static final String JSON_PROPERTY_VALUE = "value";
    private String value;

    /**
     * URI of the code space (dictionary/authority) the value is taken from. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CODE_SPACE)
    public String getCodeSpace() {
        return codeSpace;
    }

    @JsonProperty(JSON_PROPERTY_CODE_SPACE)
    public void setCodeSpace(String codeSpace) {
        this.codeSpace = codeSpace;
    }

    public CodeListValue codeSpace(String codeSpace) {
        setCodeSpace(codeSpace);
        return this;
    }

    /**
     * Value taken from the code list. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(String value) {
        this.value = value;
    }

    public CodeListValue value(String value) {
        setValue(value);
        return this;
    }

}
