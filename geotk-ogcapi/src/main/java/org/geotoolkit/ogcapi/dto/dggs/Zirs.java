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
 * The Zone Identifier Reference System used for this Discrete Global Grid System Reference System.
 */
@JsonPropertyOrder({
    Zirs.JSON_PROPERTY_TEXT_Z_I_R_S,
    Zirs.JSON_PROPERTY_UINT64_Z_I_R_S
})
public final class Zirs extends DataTransferObject {

    public static final String JSON_PROPERTY_TEXT_Z_I_R_S = "textZIRS";
    private ZirsText textZIRS;

    public static final String JSON_PROPERTY_UINT64_Z_I_R_S = "uint64ZIRS";
    private ZirsUInt64 uint64ZIRS;

    public Zirs() {
    }

    public Zirs textZIRS(ZirsText textZIRS) {
        this.textZIRS = textZIRS;
        return this;
    }

    /**
     * Get textZIRS
     *
     * @return textZIRS
     */
    @JsonProperty(JSON_PROPERTY_TEXT_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public ZirsText getTextZIRS() {
        return textZIRS;
    }

    @JsonProperty(JSON_PROPERTY_TEXT_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTextZIRS(ZirsText textZIRS) {
        this.textZIRS = textZIRS;
    }

    public Zirs uint64ZIRS(ZirsUInt64 uint64ZIRS) {
        this.uint64ZIRS = uint64ZIRS;
        return this;
    }

    /**
     * Get uint64ZIRS
     *
     * @return uint64ZIRS
     */
    @JsonProperty(JSON_PROPERTY_UINT64_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ZirsUInt64 getUint64ZIRS() {
        return uint64ZIRS;
    }

    @JsonProperty(JSON_PROPERTY_UINT64_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUint64ZIRS(ZirsUInt64 uint64ZIRS) {
        this.uint64ZIRS = uint64ZIRS;
    }

    /**
     * Return true if this dggrs_definition_zirs object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Zirs dggrsDefinitionZirs = (Zirs) o;
        return Objects.equals(this.textZIRS, dggrsDefinitionZirs.textZIRS)
                && Objects.equals(this.uint64ZIRS, dggrsDefinitionZirs.uint64ZIRS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textZIRS, uint64ZIRS);
    }

}
