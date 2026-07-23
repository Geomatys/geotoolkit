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
package org.geotoolkit.ogcapi.dto.movingfeatures;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * MF-JSON {@code TemporalPrimitiveValue} object: a sequence of scalar values, each attached to
 * one of the given {@code datetimes}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    TemporalPrimitiveValue.JSON_PROPERTY_DATETIMES,
    TemporalPrimitiveValue.JSON_PROPERTY_VALUES,
    TemporalPrimitiveValue.JSON_PROPERTY_INTERPOLATION
})
@XmlRootElement(name = "TemporalPrimitiveValue")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TemporalPrimitiveValue")
public final class TemporalPrimitiveValue extends DataTransferObject {

    public static final String JSON_PROPERTY_DATETIMES = "datetimes";
    @XmlElement(name = JSON_PROPERTY_DATETIMES)
    @jakarta.annotation.Nonnull
    private List<String> datetimes;

    public static final String JSON_PROPERTY_VALUES = "values";
    //ponytail: spec declares "values" as a bare oneOf(number,string,boolean) scalar, but every
    //example (and any real payload) sends an array of such scalars, one per datetime; modeled
    //as a raw list to match actual usage rather than the (apparently mistaken) schema.
    @XmlElement(name = JSON_PROPERTY_VALUES)
    @jakarta.annotation.Nonnull
    private List<Object> values;

    public static final String JSON_PROPERTY_INTERPOLATION = "interpolation";
    @XmlElement(name = JSON_PROPERTY_INTERPOLATION)
    @jakarta.annotation.Nonnull
    private String interpolation;

    public TemporalPrimitiveValue() {
    }

    public TemporalPrimitiveValue datetimes(@jakarta.annotation.Nonnull List<String> datetimes) {
        this.datetimes = datetimes;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "datetimes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getDatetimes() {
        return datetimes;
    }

    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "datetimes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setDatetimes(@jakarta.annotation.Nonnull List<String> datetimes) {
        this.datetimes = datetimes;
    }

    public TemporalPrimitiveValue values(@jakarta.annotation.Nonnull List<Object> values) {
        this.values = values;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setValues(@jakarta.annotation.Nonnull List<Object> values) {
        this.values = values;
    }

    public TemporalPrimitiveValue interpolation(@jakarta.annotation.Nonnull String interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "interpolation")
    public String getInterpolation() {
        return interpolation;
    }

    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "interpolation")
    public void setInterpolation(@jakarta.annotation.Nonnull String interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalPrimitiveValue other = (TemporalPrimitiveValue) o;
        return Objects.equals(this.datetimes, other.datetimes)
                && Objects.equals(this.values, other.values)
                && Objects.equals(this.interpolation, other.interpolation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datetimes, values, interpolation);
    }

}
