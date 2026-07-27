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
package org.geotoolkit.ogcapi.dto.coveragejson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Extent;

/**
 * Describes one parameter of a {@link CoverageJson}, per the CoverageJSON {@code Parameter}
 * object. Keyed by parameter name in {@link CoverageJson#getParameters()} and
 * {@link org.geotoolkit.ogcapi.dto.edr.EdrCollection#getParameterNames()}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    Parameter.JSON_PROPERTY_TYPE,
    Parameter.JSON_PROPERTY_DESCRIPTION,
    Parameter.JSON_PROPERTY_UNIT,
    Parameter.JSON_PROPERTY_OBSERVED_PROPERTY,
    Parameter.JSON_PROPERTY_CATEGORY_ENCODING,
    Parameter.JSON_PROPERTY_EXTENT,
    Parameter.JSON_PROPERTY_MEASUREMENT_TYPE
})
public final class Parameter extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_UNIT = "unit";
    private Unit unit;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY = "observedProperty";
    private ObservedProperty observedProperty;

    public static final String JSON_PROPERTY_CATEGORY_ENCODING = "categoryEncoding";
    private Map<String, Integer> categoryEncoding;

    public static final String JSON_PROPERTY_EXTENT = "extent";
    private Extent extent;

    public static final String JSON_PROPERTY_MEASUREMENT_TYPE = "measurementType";
    private MeasurementType measurementType;

    public Parameter() {
    }

    public Parameter type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Always {@code "Parameter"}.
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(String type) {
        this.type = type;
    }

    public Parameter description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public Parameter unit(Unit unit) {
        this.unit = unit;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Unit getUnit() {
        return unit;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Parameter observedProperty(ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObservedProperty(ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
    }

    public Parameter categoryEncoding(Map<String, Integer> categoryEncoding) {
        this.categoryEncoding = categoryEncoding;
        return this;
    }

    public Parameter putCategoryEncodingItem(String key, Integer categoryEncodingItem) {
        if (this.categoryEncoding == null) {
            this.categoryEncoding = new java.util.LinkedHashMap<>();
        }
        this.categoryEncoding.put(key, categoryEncodingItem);
        return this;
    }

    /**
     * Maps {@link Category} ids from {@link ObservedProperty#getCategories()} to the
     * numeric values used in the {@link CoverageJson}'s {@code ranges}.
     *
     * @return categoryEncoding
     */
    @JsonProperty(JSON_PROPERTY_CATEGORY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Integer> getCategoryEncoding() {
        return categoryEncoding;
    }

    @JsonProperty(JSON_PROPERTY_CATEGORY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCategoryEncoding(Map<String, Integer> categoryEncoding) {
        this.categoryEncoding = categoryEncoding;
    }

    public Parameter extent(Extent extent) {
        this.extent = extent;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Extent getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public Parameter measurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_MEASUREMENT_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    @JsonProperty(JSON_PROPERTY_MEASUREMENT_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Parameter other = (Parameter) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.unit, other.unit)
                && Objects.equals(this.observedProperty, other.observedProperty)
                && Objects.equals(this.categoryEncoding, other.categoryEncoding)
                && Objects.equals(this.extent, other.extent)
                && Objects.equals(this.measurementType, other.measurementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, description, unit, observedProperty, categoryEncoding, extent, measurementType);
    }

}
