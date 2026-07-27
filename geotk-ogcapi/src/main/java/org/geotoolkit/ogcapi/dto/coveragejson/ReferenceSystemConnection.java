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
public final class ReferenceSystemConnection extends DataTransferObject {

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    private List<String> coordinates;

    public static final String JSON_PROPERTY_SYSTEM = "system";
    private ReferenceSystem system;

    public ReferenceSystemConnection() {
    }

    public ReferenceSystemConnection coordinates(List<String> coordinates) {
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
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public ReferenceSystemConnection system(ReferenceSystem system) {
        this.system = system;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ReferenceSystem getSystem() {
        return system;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSystem(ReferenceSystem system) {
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
