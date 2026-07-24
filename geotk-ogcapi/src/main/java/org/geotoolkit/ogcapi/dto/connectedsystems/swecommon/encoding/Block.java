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
 * A block of binary-encoded data (e.g. a nested record or array), part of a {@link BinaryEncoding}.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Block extends BinaryMember {

    public static final String JSON_PROPERTY_COMPRESSION = "compression";
    private String compression;

    public static final String JSON_PROPERTY_ENCRYPTION = "encryption";
    private String encryption;

    public static final String JSON_PROPERTY_PADDING_BYTES_AFTER = "paddingBytes-after";
    private Integer paddingBytesAfter;

    public static final String JSON_PROPERTY_PADDING_BYTES_BEFORE = "paddingBytes-before";
    private Integer paddingBytesBefore;

    public Block() {
        setType("Block");
    }

    @JsonProperty(JSON_PROPERTY_COMPRESSION)
    public String getCompression() {
        return compression;
    }

    @JsonProperty(JSON_PROPERTY_COMPRESSION)
    public void setCompression(String compression) {
        this.compression = compression;
    }

    @JsonProperty(JSON_PROPERTY_ENCRYPTION)
    public String getEncryption() {
        return encryption;
    }

    @JsonProperty(JSON_PROPERTY_ENCRYPTION)
    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @JsonProperty(JSON_PROPERTY_PADDING_BYTES_AFTER)
    public Integer getPaddingBytesAfter() {
        return paddingBytesAfter;
    }

    @JsonProperty(JSON_PROPERTY_PADDING_BYTES_AFTER)
    public void setPaddingBytesAfter(Integer paddingBytesAfter) {
        this.paddingBytesAfter = paddingBytesAfter;
    }

    @JsonProperty(JSON_PROPERTY_PADDING_BYTES_BEFORE)
    public Integer getPaddingBytesBefore() {
        return paddingBytesBefore;
    }

    @JsonProperty(JSON_PROPERTY_PADDING_BYTES_BEFORE)
    public void setPaddingBytesBefore(Integer paddingBytesBefore) {
        this.paddingBytesBefore = paddingBytesBefore;
    }

}
