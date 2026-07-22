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
 * Binds a {@link ReferenceSystem} to the {@link Domain} axes it applies to, per the
 * CoverageJSON {@code referencing} array entries.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    ReferenceSystemConnection.JSON_PROPERTY_COORDINATES,
    ReferenceSystemConnection.JSON_PROPERTY_SYSTEM
})
@XmlRootElement(name = "ReferenceSystemConnection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ReferenceSystemConnection")
public final class ReferenceSystemConnection extends DataTransferObject {

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = "coordinates")
    @jakarta.annotation.Nullable
    private List<String> coordinates;

    public static final String JSON_PROPERTY_SYSTEM = "system";
    @XmlElement(name = "system")
    @jakarta.annotation.Nullable
    private ReferenceSystem system;

    public ReferenceSystemConnection() {
    }

    public ReferenceSystemConnection coordinates(@jakarta.annotation.Nullable List<String> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public ReferenceSystemConnection addCoordinatesItem(String coordinatesItem) {
        if (this.coordinates == null) {
            this.coordinates = new java.util.ArrayList<>();
        }
        this.coordinates.add(coordinatesItem);
        return this;
    }

    /**
     * Names of the {@link Axes} that this reference system applies to.
     *
     * @return coordinates
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nullable List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public ReferenceSystemConnection system(@jakarta.annotation.Nullable ReferenceSystem system) {
        this.system = system;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "system")
    public ReferenceSystem getSystem() {
        return system;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "system")
    public void setSystem(@jakarta.annotation.Nullable ReferenceSystem system) {
        this.system = system;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReferenceSystemConnection other = (ReferenceSystemConnection) o;
        return Objects.equals(this.coordinates, other.coordinates)
                && Objects.equals(this.system, other.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, system);
    }

}
