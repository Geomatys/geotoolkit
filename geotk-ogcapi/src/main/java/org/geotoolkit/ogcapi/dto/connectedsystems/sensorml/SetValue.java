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
 * Assigns a single scalar value to a component of a process, referenced by path, as part of
 * {@link Settings#getSetValues()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class SetValue extends DataTransferObject {

    public static final String JSON_PROPERTY_REF = "ref";
    private String ref;

    public static final String JSON_PROPERTY_VALUE = "value";
    private Object value;

    /**
     * Path reference to the component whose value is being set. (required)
     */
    @JsonProperty(JSON_PROPERTY_REF)
    public String getRef() {
        return ref;
    }

    @JsonProperty(JSON_PROPERTY_REF)
    public void setRef(String ref) {
        this.ref = ref;
    }

    public SetValue ref(String ref) {
        setRef(ref);
        return this;
    }

    /**
     * The value to assign, either a number or a string. (required)
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(Object value) {
        this.value = value;
    }

    public SetValue value(Object value) {
        setValue(value);
        return this;
    }

}
