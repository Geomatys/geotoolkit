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
package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        Axis.JSON_PROPERTY_TYPE,
        Axis.JSON_PROPERTY_AXIS_LABEL,
        Axis.JSON_PROPERTY_LOWER_BOUND,
        Axis.JSON_PROPERTY_UPPER_BOUND
})
@XmlRootElement(name = "Axis")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Axis")
public abstract class Axis extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private String type;

    public static final String JSON_PROPERTY_AXIS_LABEL = "axisLabel";
    @XmlElement(name = "axisLabel")
    @jakarta.annotation.Nonnull
    private String axisLabel;

    public static final String JSON_PROPERTY_LOWER_BOUND = "lowerBound";
    @XmlElement(name = "lowerBound")
    @jakarta.annotation.Nonnull
    private Object lowerBound;

    public static final String JSON_PROPERTY_UPPER_BOUND = "upperBound";
    @XmlElement(name = "upperBound")
    @jakarta.annotation.Nonnull
    private Object upperBound;

    public Axis() {}

    public Axis(String type, String axisLabel, Object lowerBound, Object upperBound) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(String type) {
        this.type = type;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_AXIS_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axisLabel")
    public String getAxisLabel() {
        return axisLabel;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axisLabel")
    public void setAxisLabel(String axisLabel) {
        this.axisLabel = axisLabel;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_LOWER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "lowerBound")
    public Object getLowerBound() {
        return lowerBound;
    }

    @JsonProperty(JSON_PROPERTY_LOWER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "lowerBound")
    public void setLowerBound(Object lowerBound) {
        this.lowerBound = lowerBound;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_UPPER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "upperBound")
    public Object getUpperBound() {
        return upperBound;
    }

    @JsonProperty(JSON_PROPERTY_UPPER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "upperBound")
    public void setUpperBound(Object upperBound) {
        this.upperBound = upperBound;
    }
}
