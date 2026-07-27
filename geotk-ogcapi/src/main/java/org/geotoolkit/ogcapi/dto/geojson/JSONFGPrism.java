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
package org.geotoolkit.ogcapi.dto.geojson;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * JSONFGPrism
 */
@JsonPropertyOrder({
    JSONFGPrism.PROPERTY_TYPE,
    JSONFGPrism.PROPERTY_BBOX,
    JSONFGPrism.PROPERTY_BASE,
    JSONFGPrism.PROPERTY_LOWER,
    JSONFGPrism.PROPERTY_UPPER
})
public class JSONFGPrism extends GeoJSONGeometry {

    public static final String PROPERTY_BASE = "base";
    private GeoJSONGeometry base;

    public static final String PROPERTY_LOWER = "lower";
    private Double lower;

    public static final String PROPERTY_UPPER = "upper";
    private Double upper;

    public JSONFGPrism() {
    }

    @Override
    public String getType() {
        return TYPE_PRISM;
    }

    /**
     * Get base
     *
     * @return base
     */
    @JsonProperty(PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public GeoJSONGeometry getBase() {
        return base;
    }

    @JsonProperty(PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setBase(GeoJSONGeometry base) {
        this.base = base;
    }

    /**
     * Get lower
     *
     * @return lower
     */
    @JsonProperty(PROPERTY_LOWER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getLower() {
        return lower;
    }

    @JsonProperty(PROPERTY_LOWER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLower(Double lower) {
        this.lower = lower;
    }

    /**
     * Get upper
     *
     * @return upper
     */
    @JsonProperty(PROPERTY_UPPER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Double getUpper() {
        return upper;
    }

    @JsonProperty(PROPERTY_UPPER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setUpper(Double upper) {
        this.upper = upper;
    }

    /**
     * Return true if this JSON_FG_Prism object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONFGPrism jsONFGPrism = (JSONFGPrism) o;
        return super.equals(o)
                && Objects.equals(this.base, jsONFGPrism.base)
                && Objects.equals(this.lower, jsONFGPrism.lower)
                && Objects.equals(this.upper, jsONFGPrism.upper);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(base, lower, upper);
    }

}
