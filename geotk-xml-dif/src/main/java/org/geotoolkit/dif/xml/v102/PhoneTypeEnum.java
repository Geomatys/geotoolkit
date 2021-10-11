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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PhoneTypeEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PhoneTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Direct Line"/>
 *     &lt;enumeration value="Primary"/>
 *     &lt;enumeration value="Telephone"/>
 *     &lt;enumeration value="Fax"/>
 *     &lt;enumeration value="Mobile"/>
 *     &lt;enumeration value="Modem"/>
 *     &lt;enumeration value="TDD/TTY Phone"/>
 *     &lt;enumeration value="U.S. toll free"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "PhoneTypeEnum")
@XmlEnum
public enum PhoneTypeEnum {

    @XmlEnumValue("Direct Line")
    DIRECT_LINE("Direct Line"),
    @XmlEnumValue("Primary")
    PRIMARY("Primary"),
    @XmlEnumValue("Telephone")
    TELEPHONE("Telephone"),
    @XmlEnumValue("Fax")
    FAX("Fax"),
    @XmlEnumValue("Mobile")
    MOBILE("Mobile"),
    @XmlEnumValue("Modem")
    MODEM("Modem"),
    @XmlEnumValue("TDD/TTY Phone")
    TDD_TTY_PHONE("TDD/TTY Phone"),
    @XmlEnumValue("U.S. toll free")
    U_S_TOLL_FREE("U.S. toll free"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    PhoneTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PhoneTypeEnum fromValue(String v) {
        for (PhoneTypeEnum c: PhoneTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
