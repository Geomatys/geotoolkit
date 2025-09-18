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
package org.geotoolkit.ogcapi.model.geojson;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;

/**
 * JSONFGMultiPolyhedron
 */
@JsonPropertyOrder({
    JSONFGMultiPolyhedron.PROPERTY_TYPE,
    JSONFGMultiPolyhedron.PROPERTY_BBOX,
    JSONFGMultiPolyhedron.PROPERTY_COORDINATES
})
@XmlRootElement(name = "JSONFGMultiPolyhedron")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JSONFGMultiPolyhedron")
public class JSONFGMultiPolyhedron extends GeoJSONGeometry {

    public static final String PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = PROPERTY_COORDINATES)
    @jakarta.annotation.Nonnull
    private List<List<List<List<List<List<Double>>>>>> coordinates = new ArrayList<>();

    public JSONFGMultiPolyhedron() {
    }

    @Override
    public String getType() {
        return TYPE_MULTIPOLYHEDRON;
    }

    /**
     * Get coordinates
     *
     * @return coordinates
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_COORDINATES)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<List<List<List<List<Double>>>>>> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_COORDINATES)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nonnull List<List<List<List<List<List<Double>>>>>> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Return true if this JSON_FG_MultiPolyhedron object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONFGMultiPolyhedron jsONFGMultiPolyhedron = (JSONFGMultiPolyhedron) o;
        return super.equals(o)
                && Objects.equals(this.coordinates, jsONFGMultiPolyhedron.coordinates);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(coordinates);
    }

}
