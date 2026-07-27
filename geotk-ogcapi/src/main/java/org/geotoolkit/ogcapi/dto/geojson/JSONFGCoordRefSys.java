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
import org.geotoolkit.ogcapi.dto.DataTransferObject;


/**
 * CoordRefSys
 */
@JsonPropertyOrder({
    JSONFGCoordRefSys.PROPERTY_TYPE,
    JSONFGCoordRefSys.PROPERTY_HREF
})
public class JSONFGCoordRefSys extends DataTransferObject {

    public static final String TYPE_REFERENCE = "Reference";

    public static final String PROPERTY_HREF = "href";
    public static final String PROPERTY_TYPE = GeoJSONObject.PROPERTY_TYPE;

    private String type;

    private String href;

    public JSONFGCoordRefSys() {
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getType() {
        return type;
    }

    @JsonProperty(PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get href
     *
     * @return href
     */
    @JsonProperty(PROPERTY_HREF)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getHref() {
        return href;
    }

    @JsonProperty(PROPERTY_HREF)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Return true if this CoordRefSys object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONFGCoordRefSys other = (JSONFGCoordRefSys) o;
        return Objects.equals(this.href, other.href)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, type);
    }

}
