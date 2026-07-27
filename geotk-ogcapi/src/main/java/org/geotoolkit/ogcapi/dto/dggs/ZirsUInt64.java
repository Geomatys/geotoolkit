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
 * 64-bit unsigned integer zone indexing scheme
 */
@JsonPropertyOrder({
    ZirsUInt64.JSON_PROPERTY_DESCRIPTION,
    ZirsUInt64.JSON_PROPERTY_TYPE
})
public final class ZirsUInt64 extends DataTransferObject {

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    /**
     * May be one of, but not limited to :
     * "ogc2DTMSHexLevelRowCol"
     */
    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public ZirsUInt64() {
    }

    public ZirsUInt64 description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDescription(String description) {
        this.description = description;
    }

    public ZirsUInt64 type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return true if this dggrs_definition_zirs_uint64ZIRS object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZirsUInt64 dggrsDefinitionZirsUint64ZIRS = (ZirsUInt64) o;
        return Objects.equals(this.description, dggrsDefinitionZirsUint64ZIRS.description)
                && Objects.equals(this.type, dggrsDefinitionZirsUint64ZIRS.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, type);
    }

}
