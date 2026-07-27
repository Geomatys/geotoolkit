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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;

/**
 * A raw binary encoding.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class BinaryEncoding extends AbstractEncoding {

    public static final String JSON_PROPERTY_BYTE_ORDER = "byteOrder";
    private String byteOrder;

    public static final String JSON_PROPERTY_BYTE_ENCODING = "byteEncoding";
    private String byteEncoding;

    public static final String JSON_PROPERTY_BYTE_LENGTH = "byteLength";
    private Integer byteLength;

    public static final String JSON_PROPERTY_MEMBERS = "members";
    private List<BinaryMember> members;

    public BinaryEncoding() {
        setType("BinaryEncoding");
    }

    /**
     * Byte order: {@code bigEndian} or {@code littleEndian}. (required)
     */
    @JsonProperty(JSON_PROPERTY_BYTE_ORDER)
    public String getByteOrder() {
        return byteOrder;
    }

    @JsonProperty(JSON_PROPERTY_BYTE_ORDER)
    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
    }

    /**
     * Byte encoding: {@code base64} or {@code raw}. (required)
     */
    @JsonProperty(JSON_PROPERTY_BYTE_ENCODING)
    public String getByteEncoding() {
        return byteEncoding;
    }

    @JsonProperty(JSON_PROPERTY_BYTE_ENCODING)
    public void setByteEncoding(String byteEncoding) {
        this.byteEncoding = byteEncoding;
    }

    @JsonProperty(JSON_PROPERTY_BYTE_LENGTH)
    public Integer getByteLength() {
        return byteLength;
    }

    @JsonProperty(JSON_PROPERTY_BYTE_LENGTH)
    public void setByteLength(Integer byteLength) {
        this.byteLength = byteLength;
    }

    /**
     * Ordered description of the binary members (blocks/components). (required)
     */
    @JsonProperty(JSON_PROPERTY_MEMBERS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<BinaryMember> getMembers() {
        return members;
    }

    @JsonProperty(JSON_PROPERTY_MEMBERS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setMembers(List<BinaryMember> members) {
        this.members = members;
    }

}
