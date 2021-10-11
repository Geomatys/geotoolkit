/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gmlcov.geotiff.xml.v100;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for compressionType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="compressionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="PackBits"/>
 *     &lt;enumeration value="Huffman"/>
 *     &lt;enumeration value="LZW"/>
 *     &lt;enumeration value="JPEG"/>
 *     &lt;enumeration value="Deflate"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "compressionType")
@XmlEnum
public enum CompressionType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("PackBits")
    PACK_BITS("PackBits"),
    @XmlEnumValue("Huffman")
    HUFFMAN("Huffman"),
    LZW("LZW"),
    JPEG("JPEG"),
    @XmlEnumValue("Deflate")
    DEFLATE("Deflate");
    private final String value;

    CompressionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CompressionType fromValue(String v) {
        for (CompressionType c: CompressionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
