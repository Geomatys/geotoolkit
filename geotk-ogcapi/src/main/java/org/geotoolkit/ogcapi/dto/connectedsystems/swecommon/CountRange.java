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

/**
 * An integer range SWE Common data component.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class CountRange extends AbstractSimpleComponent {

    public static final String JSON_PROPERTY_CONSTRAINT = "constraint";
    private AllowedValues constraint;

    public static final String JSON_PROPERTY_NIL_VALUES = "nilValues";
    private List<NilValue> nilValues;

    public static final String JSON_PROPERTY_VALUE = "value";
    private List<Integer> value;

    public CountRange() {
        setType("CountRange");
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public AllowedValues getConstraint() {
        return constraint;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public void setConstraint(AllowedValues constraint) {
        this.constraint = constraint;
    }

    @JsonProperty(JSON_PROPERTY_NIL_VALUES)
    public List<NilValue> getNilValues() {
        return nilValues;
    }

    @JsonProperty(JSON_PROPERTY_NIL_VALUES)
    public void setNilValues(List<NilValue> nilValues) {
        this.nilValues = nilValues;
    }

    /**
     * [min, max] integer values. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    public List<Integer> getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(List<Integer> value) {
        this.value = value;
    }

}
