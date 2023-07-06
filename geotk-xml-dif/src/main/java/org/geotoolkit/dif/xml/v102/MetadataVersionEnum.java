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

package org.geotoolkit.dif.xml.v102;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour MetadataVersionEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="MetadataVersionEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VERSION 9.8.1"/>
 *     &lt;enumeration value="VERSION 9.8.2"/>
 *     &lt;enumeration value="VERSION 9.8.2.2"/>
 *     &lt;enumeration value="VERSION 9.8.3"/>
 *     &lt;enumeration value="VERSION 9.8.4"/>
 *     &lt;enumeration value="VERSION 9.9.3"/>
 *     &lt;enumeration value="VERSION 10.2"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "MetadataVersionEnum")
@XmlEnum
public enum MetadataVersionEnum {

    @XmlEnumValue("VERSION 9.8.1")
    VERSION_9_8_1("VERSION 9.8.1"),
    @XmlEnumValue("VERSION 9.8.2")
    VERSION_9_8_2("VERSION 9.8.2"),
    @XmlEnumValue("VERSION 9.8.2.2")
    VERSION_9_8_2_2("VERSION 9.8.2.2"),
    @XmlEnumValue("VERSION 9.8.3")
    VERSION_9_8_3("VERSION 9.8.3"),
    @XmlEnumValue("VERSION 9.8.4")
    VERSION_9_8_4("VERSION 9.8.4"),
    @XmlEnumValue("VERSION 9.9.3")
    VERSION_9_9_3("VERSION 9.9.3"),
    @XmlEnumValue("VERSION 10.2")
    VERSION_10_2("VERSION 10.2");
    private final String value;

    MetadataVersionEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MetadataVersionEnum fromValue(String v) {
        for (MetadataVersionEnum c: MetadataVersionEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
