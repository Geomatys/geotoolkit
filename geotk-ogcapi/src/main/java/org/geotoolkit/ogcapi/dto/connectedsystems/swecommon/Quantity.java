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
 * A numeric scalar SWE Common data component.
 *
 * <p>{@code value} is typed {@code Object} since the standard allows either a plain number or one
 * of the special string tokens {@code NaN}/{@code Infinity}/{@code +Infinity}/{@code -Infinity}
 * (Jackson naturally (de)serializes either shape without extra code).</p>
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Quantity extends AbstractSimpleComponent {

    public static final String JSON_PROPERTY_UOM = "uom";
    private UnitReference uom;

    public static final String JSON_PROPERTY_CONSTRAINT = "constraint";
    private AllowedValues constraint;

    public static final String JSON_PROPERTY_NIL_VALUES = "nilValues";
    private List<NilValue> nilValues;

    public static final String JSON_PROPERTY_VALUE = "value";
    private Object value;

    public Quantity() {
        setType("Quantity");
    }

    /**
     * Unit of measure. (required)
     */
    @JsonProperty(JSON_PROPERTY_UOM)
    public UnitReference getUom() {
        return uom;
    }

    @JsonProperty(JSON_PROPERTY_UOM)
    public void setUom(UnitReference uom) {
        this.uom = uom;
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

    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(Object value) {
        this.value = value;
    }

}
