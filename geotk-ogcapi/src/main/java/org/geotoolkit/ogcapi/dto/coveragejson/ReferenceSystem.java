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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A CoverageJSON {@code ReferenceSystem} (e.g. {@code GeographicCRS}, {@code TemporalRS},
 * {@code VerticalCRS}), referenced by a {@link ReferenceSystemConnection}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    ReferenceSystem.JSON_PROPERTY_TYPE,
    ReferenceSystem.JSON_PROPERTY_ID,
    ReferenceSystem.JSON_PROPERTY_CALENDAR
})
@XmlRootElement(name = "ReferenceSystem")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ReferenceSystem")
public final class ReferenceSystem extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private String id;

    public static final String JSON_PROPERTY_CALENDAR = "calendar";
    @XmlElement(name = "calendar")
    @jakarta.annotation.Nullable
    private String calendar;

    public ReferenceSystem() {
    }

    public ReferenceSystem type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

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

    public ReferenceSystem id(@jakarta.annotation.Nullable String id) {
        this.id = id;
        return this;
    }

    /**
     * URI identifying the reference system.
     *
     * @return id
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nullable String id) {
        this.id = id;
    }

    public ReferenceSystem calendar(@jakarta.annotation.Nullable String calendar) {
        this.calendar = calendar;
        return this;
    }

    /**
     * For a {@code TemporalRS}, e.g. {@code "Gregorian"}.
     *
     * @return calendar
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CALENDAR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "calendar")
    public String getCalendar() {
        return calendar;
    }

    @JsonProperty(JSON_PROPERTY_CALENDAR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "calendar")
    public void setCalendar(@jakarta.annotation.Nullable String calendar) {
        this.calendar = calendar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReferenceSystem other = (ReferenceSystem) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.calendar, other.calendar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, calendar);
    }

}
