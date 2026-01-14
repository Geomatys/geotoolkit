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
package org.geotoolkit.stac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/EO-Data-Discovery">OpenEO Doc</a>
 * Based on : <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md#spatial-extent-object">STAC Spec Github</a>
 */
@JsonPropertyOrder({
        DimensionSpatial.JSON_PROPERTY_AXIS,
        DimensionSpatial.JSON_PROPERTY_EXTENT,
        DimensionSpatial.JSON_PROPERTY_VALUES,
        DimensionSpatial.JSON_PROPERTY_STEP,
        DimensionSpatial.JSON_PROPERTY_UNIT,
        DimensionSpatial.JSON_PROPERTY_REFERENCE_SYSTEM
})
@XmlRootElement(name = "DimensionSpatial")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DimensionSpatial")
public class DimensionSpatial extends Dimension {

    // --- PROPERTY CONSTANTS ---
    public static final String JSON_PROPERTY_AXIS = "axis";
    public static final String JSON_PROPERTY_EXTENT = "extent";
    public static final String JSON_PROPERTY_VALUES = "values";
    public static final String JSON_PROPERTY_STEP = "step";
    public static final String JSON_PROPERTY_UNIT = "unit";
    public static final String JSON_PROPERTY_REFERENCE_SYSTEM = "reference_system";

    public DimensionSpatial(String axis, List<Object> extent, List<Object> values, String unit, String referenceSystem) {
        super(TypeEnum.SPATIAL);
        this.axis = axis;
        this.extent = extent;
        this.values = values;
        this.step = null;
        this.unit = unit;
        this.referenceSystem = referenceSystem;
    }

    public DimensionSpatial(String axis, List<Object> extent, String step, String unit, String referenceSystem) {
        super(TypeEnum.SPATIAL);
        this.axis = axis;
        this.extent = extent;
        this.values = null;
        this.step = step;
        this.unit = unit;
        this.referenceSystem = referenceSystem;
    }

    @XmlElement(name = "axis")
    private String axis;

    @XmlElement(name = "extent")
    private List<Object> extent = null;

    @XmlElement(name = "values")
    private List<Object> values = null;

    @XmlElement(name = "step")
    private String step = null;

    /** Units should be compliant with {@link "https://ncics.org/portfolio/other-resources/udunits2/"}. */
    @XmlElement(name = "unit")
    private String unit = null;

    @XmlElement(name = "referenceSystem")
    private String referenceSystem = null;

    public DimensionSpatial axis(String axis) {
        this.axis = axis;
        return this;
    }

    public DimensionSpatial extent(List<Object> extent) {
        this.extent = extent;
        return this;
    }

    public DimensionSpatial addExtentItem(Double extentItem) {
        if (this.extent == null) {
            this.extent = new ArrayList<>();
        }
        this.extent.add(extentItem);
        return this;
    }

    public DimensionSpatial values(List<Object> values) {
        this.values = values;
        return this;
    }

    public DimensionSpatial addValuesItem(Double valuesItem) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        this.values.add(valuesItem);
        return this;
    }

    public DimensionSpatial step(String step) {
        this.step = step;
        return this;
    }

    public DimensionSpatial referenceSystem(String referenceSystem) {
        this.referenceSystem = referenceSystem;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axis")
    public String getAxis() {
        return axis;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axis")
    public void setAxis(String axis) {
        this.axis = axis;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "extent")
    public List<Object> getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "extent")
    public void setExtent(List<Object> extent) {
        this.extent = extent;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    public void setValues(List<Object> values) {
        this.values = values;
    }

    @JsonProperty(JSON_PROPERTY_STEP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "step")
    public String getStep() {
        return step;
    }

    @JsonProperty(JSON_PROPERTY_STEP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "step")
    public void setStep(String step) {
        this.step = step;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "referenceSystem")
    public String getReferenceSystem() {
        return referenceSystem;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "referenceSystem")
    public void setReferenceSystem(String referenceSystem) {
        this.referenceSystem = referenceSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DimensionSpatial dimensionSpatial = (DimensionSpatial) o;
        return Objects.equals(this.axis, dimensionSpatial.axis) &&
                Objects.equals(this.extent, dimensionSpatial.extent) &&
                Objects.equals(this.values, dimensionSpatial.values) &&
                Objects.equals(this.step, dimensionSpatial.step) &&
                Objects.equals(this.referenceSystem, dimensionSpatial.referenceSystem) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axis, extent, values, step, referenceSystem, super.hashCode());
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
