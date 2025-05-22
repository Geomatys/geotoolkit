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
package org.geotoolkit.ogcapi.model.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * The optional parameters establishing a very specific Discrete Global Grid System, where each zone has a well-defined
 * geometry.
 */
@JsonPropertyOrder({
    DggsParameters.JSON_PROPERTY_ELLIPSOID,
    DggsParameters.JSON_PROPERTY_ORIENTATION
})
@XmlRootElement(name = "DggrsDefinitionDgghParameters")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsDefinitionDgghParameters")
public final class DggsParameters extends DataTransferObject {

    public static final String JSON_PROPERTY_ELLIPSOID = "ellipsoid";
    @XmlElement(name = "ellipsoid")
    @jakarta.annotation.Nullable
    private URI ellipsoid;

    public static final String JSON_PROPERTY_ORIENTATION = "orientation";
    @XmlElement(name = "orientation")
    @jakarta.annotation.Nullable
    private DggsOrientation orientation;

    public DggsParameters() {
    }

    public DggsParameters ellipsoid(@jakarta.annotation.Nullable URI ellipsoid) {
        this.ellipsoid = ellipsoid;
        return this;
    }

    /**
     * Globe Reference System Identifier/Specification
     *
     * @return ellipsoid
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ELLIPSOID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "ellipsoid")
    public URI getEllipsoid() {
        return ellipsoid;
    }

    @JsonProperty(JSON_PROPERTY_ELLIPSOID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "ellipsoid")
    public void setEllipsoid(@jakarta.annotation.Nullable URI ellipsoid) {
        this.ellipsoid = ellipsoid;
    }

    public DggsParameters orientation(@jakarta.annotation.Nullable DggsOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    /**
     * Get orientation
     *
     * @return orientation
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ORIENTATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "orientation")
    public DggsOrientation getOrientation() {
        return orientation;
    }

    @JsonProperty(JSON_PROPERTY_ORIENTATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "orientation")
    public void setOrientation(@jakarta.annotation.Nullable DggsOrientation orientation) {
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
