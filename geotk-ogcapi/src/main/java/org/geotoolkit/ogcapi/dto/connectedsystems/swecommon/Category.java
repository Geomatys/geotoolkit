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

/**
 * A categorical (enumerated token) scalar SWE Common data component.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Category extends AbstractSimpleComponent {

    public static final String JSON_PROPERTY_CODE_SPACE = "codeSpace";
    private String codeSpace;

    public static final String JSON_PROPERTY_CONSTRAINT = "constraint";
    private AllowedTokens constraint;

    public static final String JSON_PROPERTY_NIL_VALUES = "nilValues";
    private java.util.List<NilValue> nilValues;

    public static final String JSON_PROPERTY_VALUE = "value";
    private String value;

    public Category() {
        setType("Category");
    }

    /**
     * URI of the dictionary defining the allowed token values. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CODE_SPACE)
    public String getCodeSpace() {
        return codeSpace;
    }

    @JsonProperty(JSON_PROPERTY_CODE_SPACE)
    public void setCodeSpace(String codeSpace) {
        this.codeSpace = codeSpace;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public AllowedTokens getConstraint() {
        return constraint;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public void setConstraint(AllowedTokens constraint) {
        this.constraint = constraint;
    }

    @JsonProperty(JSON_PROPERTY_NIL_VALUES)
    public java.util.List<NilValue> getNilValues() {
        return nilValues;
    }

    @JsonProperty(JSON_PROPERTY_NIL_VALUES)
    public void setNilValues(java.util.List<NilValue> nilValues) {
        this.nilValues = nilValues;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(String value) {
        this.value = value;
    }

}
