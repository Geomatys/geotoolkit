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
 * <p>Classe Java pour PersonnelRoleEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PersonnelRoleEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INVESTIGATOR"/>
 *     &lt;enumeration value="INVESTIGATOR, TECHNICAL CONTACT"/>
 *     &lt;enumeration value="METADATA AUTHOR"/>
 *     &lt;enumeration value="METADATA AUTHOR, TECHNICAL CONTACT"/>
 *     &lt;enumeration value="TECHNICAL CONTACT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "PersonnelRoleEnum")
@XmlEnum
public enum PersonnelRoleEnum {

    INVESTIGATOR("INVESTIGATOR"),
    @XmlEnumValue("INVESTIGATOR, TECHNICAL CONTACT")
    INVESTIGATOR_TECHNICAL_CONTACT("INVESTIGATOR, TECHNICAL CONTACT"),
    @XmlEnumValue("METADATA AUTHOR")
    METADATA_AUTHOR("METADATA AUTHOR"),
    @XmlEnumValue("METADATA AUTHOR, TECHNICAL CONTACT")
    METADATA_AUTHOR_TECHNICAL_CONTACT("METADATA AUTHOR, TECHNICAL CONTACT"),
    @XmlEnumValue("TECHNICAL CONTACT")
    TECHNICAL_CONTACT("TECHNICAL CONTACT");
    private final String value;

    PersonnelRoleEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PersonnelRoleEnum fromValue(String v) {
        for (PersonnelRoleEnum c: PersonnelRoleEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
