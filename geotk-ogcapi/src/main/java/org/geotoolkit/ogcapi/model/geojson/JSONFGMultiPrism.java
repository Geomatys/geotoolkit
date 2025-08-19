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
 * JSONFGMultiPrism
 */
@JsonPropertyOrder({
    JSONFGMultiPrism.PROPERTY_TYPE,
    JSONFGMultiPrism.PROPERTY_BBOX,
    JSONFGMultiPrism.PROPERTY_PRISMS
})
@XmlRootElement(name = "JSONFGMultiPrism")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JSONFGMultiPrism")
public class JSONFGMultiPrism extends GeoJSONGeometry {

    public static final String PROPERTY_PRISMS = "prisms";
    @XmlElement(name = PROPERTY_PRISMS)
    @jakarta.annotation.Nonnull
    private List<JSONFGPrism> prisms = new ArrayList<>();

    public JSONFGMultiPrism() {
    }

    @Override
    public String getType() {
        return TYPE_MULTIPRISM;
    }

    /**
     * Get prisms
     *
     * @return prisms
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_PRISMS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_PRISMS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<JSONFGPrism> getPrisms() {
        return prisms;
    }

    @JsonProperty(PROPERTY_PRISMS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_PRISMS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setPrisms(@jakarta.annotation.Nonnull List<JSONFGPrism> prisms) {
        this.prisms = prisms;
    }

    /**
     * Return true if this JSON_FG_Multi_Prism object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONFGMultiPrism jsONFGMultiPrism = (JSONFGMultiPrism) o;
        return super.equals(o)
                && Objects.equals(this.prisms, jsONFGMultiPrism.prisms);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(prisms);
    }

}
