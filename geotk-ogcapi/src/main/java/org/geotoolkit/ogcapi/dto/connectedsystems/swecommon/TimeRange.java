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
 * A date-time range SWE Common data component.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class TimeRange extends AbstractSimpleComponent {

    public static final String JSON_PROPERTY_REFERENCE_TIME = "referenceTime";
    private String referenceTime;

    public static final String JSON_PROPERTY_LOCAL_FRAME = "localFrame";
    private String localFrame;

    public static final String JSON_PROPERTY_UOM = "uom";
    private UnitReference uom;

    public static final String JSON_PROPERTY_CONSTRAINT = "constraint";
    private AllowedTimes constraint;

    public static final String JSON_PROPERTY_NIL_VALUES = "nilValues";
    private List<NilValue> nilValues;

    public static final String JSON_PROPERTY_VALUE = "value";
    private List<Object> value;

    public TimeRange() {
        setType("TimeRange");
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_TIME)
    public String getReferenceTime() {
        return referenceTime;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_TIME)
    public void setReferenceTime(String referenceTime) {
        this.referenceTime = referenceTime;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_FRAME)
    public String getLocalFrame() {
        return localFrame;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_FRAME)
    public void setLocalFrame(String localFrame) {
        this.localFrame = localFrame;
    }

    /**
     * Unit of measure of the time value. (required)
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
    public AllowedTimes getConstraint() {
        return constraint;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public void setConstraint(AllowedTimes constraint) {
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
     * [min, max] date-time values. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    public List<Object> getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(List<Object> value) {
        this.value = value;
    }

}
