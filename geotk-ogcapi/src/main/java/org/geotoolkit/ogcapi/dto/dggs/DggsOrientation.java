/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * DggrsDefinitionDgghParametersOrientation
 */
@JsonPropertyOrder({
    DggsOrientation.JSON_PROPERTY_LATITUDE,
    DggsOrientation.JSON_PROPERTY_LONGITUDE,
    DggsOrientation.JSON_PROPERTY_AZIMUTH,
    DggsOrientation.JSON_PROPERTY_DESCRIPTION
})
public final class DggsOrientation extends DataTransferObject {

    public static final String JSON_PROPERTY_LATITUDE = "latitude";
    private Double latitude;

    public static final String JSON_PROPERTY_LONGITUDE = "longitude";
    private Double longitude;

    public static final String JSON_PROPERTY_AZIMUTH = "azimuth";
    private Double azimuth = 0.0;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public DggsOrientation() {
    }

    public DggsOrientation latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Reference geodetic latitude in decimal degrees on reference globe of first vertex to fix the orientation of the
     * polyhedron.
     *
     * @return latitude
     */
    @JsonProperty(JSON_PROPERTY_LATITUDE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty(JSON_PROPERTY_LATITUDE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public DggsOrientation longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Reference longitude in decimal degrees on reference globe of first vertex to fix the orientation of the
     * polyhedron.
     *
     * @return longitude
     */
    @JsonProperty(JSON_PROPERTY_LONGITUDE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty(JSON_PROPERTY_LONGITUDE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public DggsOrientation azimuth(Double azimuth) {
        this.azimuth = azimuth;
        return this;
    }

    /**
     * Azimuth in decimal degrees of second vertex relative to the first vertex.
     *
     * @return azimuth
     */
    @JsonProperty(JSON_PROPERTY_AZIMUTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getAzimuth() {
        return azimuth;
    }

    @JsonProperty(JSON_PROPERTY_AZIMUTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAzimuth(Double azimuth) {
        this.azimuth = azimuth;
    }

    public DggsOrientation description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
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

    /**
     * Return true if this dggrs_definition_dggh_parameters_orientation object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggsOrientation dggrsDefinitionDgghParametersOrientation = (DggsOrientation) o;
        return Objects.equals(this.latitude, dggrsDefinitionDgghParametersOrientation.latitude)
                && Objects.equals(this.longitude, dggrsDefinitionDgghParametersOrientation.longitude)
                && Objects.equals(this.azimuth, dggrsDefinitionDgghParametersOrientation.azimuth)
                && Objects.equals(this.description, dggrsDefinitionDgghParametersOrientation.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, azimuth, description);
    }

}
