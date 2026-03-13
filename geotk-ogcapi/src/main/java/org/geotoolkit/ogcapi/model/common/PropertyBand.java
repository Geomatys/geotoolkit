/*
 * Geotoolkit - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2026, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.model.common;

import java.util.Objects;
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
 * PropertyBand
 *
 * @author Quentin BIALOTA (Geomatys)
 */
@JsonPropertyOrder({
        PropertyBand.JSON_PROPERTY_TITLE,
        PropertyBand.JSON_PROPERTY_DESCRIPTION,
        PropertyBand.JSON_PROPERTY_TYPE,
        PropertyBand.JSON_PROPERTY_MINIMUM,
        PropertyBand.JSON_PROPERTY_MAXIMUM,
        PropertyBand.JSON_PROPERTY_STATISTICS,
        PropertyBand.JSON_PROPERTY_PROPERTY_SEQ
})
@XmlRootElement(name = "PropertyBand")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "PropertyBand")
public final class PropertyBand extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type = "number"; // Usually 'number' or 'integer'

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    @XmlElement(name = "minimum")
    @jakarta.annotation.Nullable
    private Double minimum; // Theoretical min (e.g. 0)

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    @XmlElement(name = "maximum")
    @jakarta.annotation.Nullable
    private Double maximum; // Theoretical max (e.g. 65535)

    public static final String JSON_PROPERTY_STATISTICS = "x-ogc-statistics";
    @XmlElement(name = "x-ogc-statistics")
    @jakarta.annotation.Nullable
    private RangeStatistics statistics;

    public static final String JSON_PROPERTY_PROPERTY_SEQ = "x-ogc-propertySeq";
    @XmlElement(name = "x-ogc-propertySeq")
    private int propertySeq;

    public PropertyBand() {
    }

    public PropertyBand(String title, String description, Double minimum, Double maximum, RangeStatistics statistics, int propertySeq) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.statistics = statistics;
        this.title = title;
        this.description = description;
        this.propertySeq = propertySeq;
    }

    /**
     * Fluent API
     */
    public PropertyBand title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    public PropertyBand description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    public PropertyBand type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    public PropertyBand minimum(@jakarta.annotation.Nullable Double minimum) {
        this.minimum = minimum;
        return this;
    }

    public PropertyBand maximum(@jakarta.annotation.Nullable Double maximum) {
        this.maximum = maximum;
        return this;
    }

    public PropertyBand statistics(@jakarta.annotation.Nullable RangeStatistics statistics) {
        this.statistics = statistics;
        return this;
    }

    public PropertyBand propertySeq(int propertySeq) {
        this.propertySeq = propertySeq;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    /**
     * Get type
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
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable String type) {
        this.type = type;
    }

    /**
     * Get minimum
     *
     * @return minimum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public Double getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JacksonXmlProperty(localName = "minimum")
    public void setMinimum(@jakarta.annotation.Nullable Double minimum) {
        this.minimum = minimum;
    }

    /**
     * Get maximum
     *
     * @return maximum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public Double getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JacksonXmlProperty(localName = "maximum")
    public void setMaximum(@jakarta.annotation.Nullable Double maximum) {
        this.maximum = maximum;
    }

    /**
     * Get statistics
     *
     * @return statistics
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STATISTICS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-statistics")
    public RangeStatistics getStatistics() {
        return statistics;
    }

    @JsonProperty(JSON_PROPERTY_STATISTICS)
    @JacksonXmlProperty(localName = "x-ogc-statistics")
    public void setStatistics(@jakarta.annotation.Nullable RangeStatistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Get propertySeq
     *
     * @return propertySeq
     */
    @JsonProperty(JSON_PROPERTY_PROPERTY_SEQ)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-propertySeq")
    public int getPropertySeq() {
        return propertySeq;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTY_SEQ)
    @JacksonXmlProperty(localName = "x-ogc-propertySeq")
    public void setPropertySeq(int propertySeq) {
        this.propertySeq = propertySeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyBand that = (PropertyBand) o;
        return propertySeq == that.propertySeq
                && Objects.equals(this.title, that.title)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.type, that.type)
                && Objects.equals(this.minimum, that.minimum)
                && Objects.equals(this.maximum, that.maximum)
                && Objects.equals(this.statistics, that.statistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, type, minimum, maximum, statistics, propertySeq);
    }

}