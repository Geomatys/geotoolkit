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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour GranuleSpatialRepresentationEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="GranuleSpatialRepresentationEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CARTESIAN"/>
 *     &lt;enumeration value="GEODETIC"/>
 *     &lt;enumeration value="ORBIT"/>
 *     &lt;enumeration value="NO_SPATIAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "GranuleSpatialRepresentationEnum")
@XmlEnum
public enum GranuleSpatialRepresentationEnum {

    CARTESIAN,
    GEODETIC,
    ORBIT,
    NO_SPATIAL;

    public String value() {
        return name();
    }

    public static GranuleSpatialRepresentationEnum fromValue(String v) {
        return valueOf(v);
    }

}
