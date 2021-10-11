/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.metalinker;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour hashType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="hashType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ed2k"/>
 *     &lt;enumeration value="md4"/>
 *     &lt;enumeration value="md5"/>
 *     &lt;enumeration value="sha1"/>
 *     &lt;enumeration value="sha256"/>
 *     &lt;enumeration value="sha384"/>
 *     &lt;enumeration value="sha512"/>
 *     &lt;enumeration value="rmd160"/>
 *     &lt;enumeration value="tiger"/>
 *     &lt;enumeration value="crc32"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "hashType")
@XmlEnum
public enum HashType {

    @XmlEnumValue("ed2k")
    ED_2_K("ed2k"),
    @XmlEnumValue("md4")
    MD_4("md4"),
    @XmlEnumValue("md5")
    MD_5("md5"),
    @XmlEnumValue("sha1")
    SHA_1("sha1"),
    @XmlEnumValue("sha256")
    SHA_256("sha256"),
    @XmlEnumValue("sha384")
    SHA_384("sha384"),
    @XmlEnumValue("sha512")
    SHA_512("sha512"),
    @XmlEnumValue("rmd160")
    RMD_160("rmd160"),
    @XmlEnumValue("tiger")
    TIGER("tiger"),
    @XmlEnumValue("crc32")
    CRC_32("crc32");
    private final String value;

    HashType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HashType fromValue(String v) {
        for (HashType c: HashType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
