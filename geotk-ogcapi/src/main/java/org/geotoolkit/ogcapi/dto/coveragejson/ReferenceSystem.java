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
public final class ReferenceSystem extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_CALENDAR = "calendar";
    private String calendar;

    public ReferenceSystem() {
    }

    public ReferenceSystem type(String type) {
        this.type = type;
        return this;
    }

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

    public ReferenceSystem id(String id) {
        this.id = id;
        return this;
    }

    /**
     * URI identifying the reference system.
     *
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setId(String id) {
        this.id = id;
    }

    public ReferenceSystem calendar(String calendar) {
        this.calendar = calendar;
        return this;
    }

    /**
     * For a {@code TemporalRS}, e.g. {@code "Gregorian"}.
     *
     * @return calendar
     */
    @JsonProperty(JSON_PROPERTY_CALENDAR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCalendar() {
        return calendar;
    }

    @JsonProperty(JSON_PROPERTY_CALENDAR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCalendar(String calendar) {
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
