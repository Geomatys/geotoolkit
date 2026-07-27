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
import java.net.URI;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The optional parameters establishing a very specific Discrete Global Grid System, where each zone has a well-defined
 * geometry.
 */
@JsonPropertyOrder({
    DggsParameters.JSON_PROPERTY_ELLIPSOID,
    DggsParameters.JSON_PROPERTY_ORIENTATION
})
public final class DggsParameters extends DataTransferObject {

    public static final String JSON_PROPERTY_ELLIPSOID = "ellipsoid";
    private URI ellipsoid;

    public static final String JSON_PROPERTY_ORIENTATION = "orientation";
    private DggsOrientation orientation;

    public DggsParameters() {
    }

    public DggsParameters ellipsoid(URI ellipsoid) {
        this.ellipsoid = ellipsoid;
        return this;
    }

    /**
     * Globe Reference System Identifier/Specification
     *
     * @return ellipsoid
     */
    @JsonProperty(JSON_PROPERTY_ELLIPSOID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getEllipsoid() {
        return ellipsoid;
    }

    @JsonProperty(JSON_PROPERTY_ELLIPSOID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEllipsoid(URI ellipsoid) {
        this.ellipsoid = ellipsoid;
    }

    public DggsParameters orientation(DggsOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    /**
     * Get orientation
     *
     * @return orientation
     */
    @JsonProperty(JSON_PROPERTY_ORIENTATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DggsOrientation getOrientation() {
        return orientation;
    }

    @JsonProperty(JSON_PROPERTY_ORIENTATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setOrientation(DggsOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Return true if this dggrs_definition_dggh_parameters object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggsParameters dggrsDefinitionDgghParameters = (DggsParameters) o;
        return Objects.equals(this.ellipsoid, dggrsDefinitionDgghParameters.ellipsoid)
                && Objects.equals(this.orientation, dggrsDefinitionDgghParameters.orientation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ellipsoid, orientation);
    }

}
