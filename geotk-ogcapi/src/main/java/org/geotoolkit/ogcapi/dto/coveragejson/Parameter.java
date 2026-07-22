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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Parameter")
public final class Parameter extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_UNIT = "unit";
    @XmlElement(name = "unit")
    @jakarta.annotation.Nullable
    private Unit unit;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY = "observedProperty";
    @XmlElement(name = "observedProperty")
    @jakarta.annotation.Nullable
    private ObservedProperty observedProperty;

    public static final String JSON_PROPERTY_CATEGORY_ENCODING = "categoryEncoding";
    @XmlElement(name = "categoryEncoding")
    @jakarta.annotation.Nullable
    private Map<String, Integer> categoryEncoding;

    public static final String JSON_PROPERTY_EXTENT = "extent";
    @XmlElement(name = "extent")
    @jakarta.annotation.Nullable
    private Extent extent;

    public static final String JSON_PROPERTY_MEASUREMENT_TYPE = "measurementType";
    @XmlElement(name = "measurementType")
    @jakarta.annotation.Nullable
    private MeasurementType measurementType;

    public Parameter() {
    }

    public Parameter type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * Always {@code "Parameter"}.
     *
     * @return type
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable String type) {
        this.type = type;
    }

    public Parameter description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    public Parameter unit(@jakarta.annotation.Nullable Unit unit) {
        this.unit = unit;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unit")
    public Unit getUnit() {
        return unit;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unit")
    public void setUnit(@jakarta.annotation.Nullable Unit unit) {
        this.unit = unit;
    }

    public Parameter observedProperty(@jakarta.annotation.Nullable ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedProperty")
    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedProperty")
    public void setObservedProperty(@jakarta.annotation.Nullable ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
    }

    public Parameter categoryEncoding(@jakarta.annotation.Nullable Map<String, Integer> categoryEncoding) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "categoryEncoding")
    public Map<String, Integer> getCategoryEncoding() {
        return categoryEncoding;
    }

    @JsonProperty(JSON_PROPERTY_CATEGORY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "categoryEncoding")
    public void setCategoryEncoding(@jakarta.annotation.Nullable Map<String, Integer> categoryEncoding) {
        this.categoryEncoding = categoryEncoding;
    }

    public Parameter extent(@jakarta.annotation.Nullable Extent extent) {
        this.extent = extent;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "extent")
    public Extent getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "extent")
    public void setExtent(@jakarta.annotation.Nullable Extent extent) {
        this.extent = extent;
    }

    public Parameter measurementType(@jakarta.annotation.Nullable MeasurementType measurementType) {
        this.measurementType = measurementType;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MEASUREMENT_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "measurementType")
    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    @JsonProperty(JSON_PROPERTY_MEASUREMENT_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "measurementType")
    public void setMeasurementType(@jakarta.annotation.Nullable MeasurementType measurementType) {
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
