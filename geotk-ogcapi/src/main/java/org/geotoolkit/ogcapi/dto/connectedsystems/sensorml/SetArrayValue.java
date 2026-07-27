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
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Assigns an array of values to a component of a process, referenced by path, as part of
 * {@link Settings#getSetArrayValues()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class SetArrayValue extends DataTransferObject {

    public static final String JSON_PROPERTY_REF = "ref";
    private String ref;

    public static final String JSON_PROPERTY_VALUE = "value";
    private List<Object> value;

    /**
     * Path reference to the component whose values are being set. (required)
     */
    @JsonProperty(JSON_PROPERTY_REF)
    public String getRef() {
        return ref;
    }

    @JsonProperty(JSON_PROPERTY_REF)
    public void setRef(String ref) {
        this.ref = ref;
    }

    public SetArrayValue ref(String ref) {
        setRef(ref);
        return this;
    }

    /**
     * The array of values to assign. (required)
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    public List<Object> getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(List<Object> value) {
        this.value = value;
    }

    public SetArrayValue value(List<Object> value) {
        setValue(value);
        return this;
    }

}
