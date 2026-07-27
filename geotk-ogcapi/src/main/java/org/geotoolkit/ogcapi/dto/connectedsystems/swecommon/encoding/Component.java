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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.encoding;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single binary-encoded scalar value, part of a {@link BinaryEncoding}.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Component extends BinaryMember {

    public static final String JSON_PROPERTY_ENCRYPTION = "encryption";
    private String encryption;

    public static final String JSON_PROPERTY_SIGNIFICANT_BITS = "significantBits";
    private Integer significantBits;

    public static final String JSON_PROPERTY_BIT_LENGTH = "bitLength";
    private Integer bitLength;

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    private String dataType;

    public Component() {
        setType("Component");
    }

    @JsonProperty(JSON_PROPERTY_ENCRYPTION)
    public String getEncryption() {
        return encryption;
    }

    @JsonProperty(JSON_PROPERTY_ENCRYPTION)
    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @JsonProperty(JSON_PROPERTY_SIGNIFICANT_BITS)
    public Integer getSignificantBits() {
        return significantBits;
    }

    @JsonProperty(JSON_PROPERTY_SIGNIFICANT_BITS)
    public void setSignificantBits(Integer significantBits) {
        this.significantBits = significantBits;
    }

    @JsonProperty(JSON_PROPERTY_BIT_LENGTH)
    public Integer getBitLength() {
        return bitLength;
    }

    @JsonProperty(JSON_PROPERTY_BIT_LENGTH)
    public void setBitLength(Integer bitLength) {
        this.bitLength = bitLength;
    }

    /**
     * URI identifying the primitive binary data type (e.g. byte/int/float encoding). (required)
     */
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

}
