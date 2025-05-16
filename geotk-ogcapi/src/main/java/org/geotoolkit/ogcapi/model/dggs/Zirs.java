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
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * The Zone Identifier Reference System used for this Discrete Global Grid System Reference System.
 */
@JsonPropertyOrder({
    Zirs.JSON_PROPERTY_TEXT_Z_I_R_S,
    Zirs.JSON_PROPERTY_UINT64_Z_I_R_S
})
@XmlRootElement(name = "DggrsDefinitionZirs")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsDefinitionZirs")
public final class Zirs extends DataTransferObject {

    public static final String JSON_PROPERTY_TEXT_Z_I_R_S = "textZIRS";
    @XmlElement(name = "textZIRS")
    @jakarta.annotation.Nonnull
    private ZirsText textZIRS;

    public static final String JSON_PROPERTY_UINT64_Z_I_R_S = "uint64ZIRS";
    @XmlElement(name = "uint64ZIRS")
    @jakarta.annotation.Nullable
    private ZirsUInt64 uint64ZIRS;

    public Zirs() {
    }

    public Zirs textZIRS(@jakarta.annotation.Nonnull ZirsText textZIRS) {
        this.textZIRS = textZIRS;
        return this;
    }

    /**
     * Get textZIRS
     *
     * @return textZIRS
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TEXT_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "textZIRS")
    public ZirsText getTextZIRS() {
        return textZIRS;
    }

    @JsonProperty(JSON_PROPERTY_TEXT_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "textZIRS")
    public void setTextZIRS(@jakarta.annotation.Nonnull ZirsText textZIRS) {
        this.textZIRS = textZIRS;
    }

    public Zirs uint64ZIRS(@jakarta.annotation.Nullable ZirsUInt64 uint64ZIRS) {
        this.uint64ZIRS = uint64ZIRS;
        return this;
    }

    /**
     * Get uint64ZIRS
     *
     * @return uint64ZIRS
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UINT64_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uint64ZIRS")
    public ZirsUInt64 getUint64ZIRS() {
        return uint64ZIRS;
    }

    @JsonProperty(JSON_PROPERTY_UINT64_Z_I_R_S)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uint64ZIRS")
    public void setUint64ZIRS(@jakarta.annotation.Nullable ZirsUInt64 uint64ZIRS) {
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
