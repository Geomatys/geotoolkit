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
 * A CRS supported by a data query, identified by its URI and, optionally, its WKT definition.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    CrsObject.JSON_PROPERTY_CRS,
    CrsObject.JSON_PROPERTY_WKT
})
public final class CrsObject extends DataTransferObject {

    public static final String JSON_PROPERTY_CRS = "crs";
    private String crs;

    public static final String JSON_PROPERTY_WKT = "wkt";
    private String wkt;

    public CrsObject() {
    }

    public CrsObject crs(String crs) {
        this.crs = crs;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(String crs) {
        this.crs = crs;
    }

    public CrsObject wkt(String wkt) {
        this.wkt = wkt;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_WKT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getWkt() {
        return wkt;
    }

    @JsonProperty(JSON_PROPERTY_WKT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrsObject other = (CrsObject) o;
        return Objects.equals(this.crs, other.crs)
                && Objects.equals(this.wkt, other.wkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crs, wkt);
    }

}
