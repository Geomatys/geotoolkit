/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.mathml.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour notationstyle.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="notationstyle">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="longdiv"/>
 *     &lt;enumeration value="actuarial"/>
 *     &lt;enumeration value="radical"/>
 *     &lt;enumeration value="box"/>
 *     &lt;enumeration value="roundedbox"/>
 *     &lt;enumeration value="circle"/>
 *     &lt;enumeration value="left"/>
 *     &lt;enumeration value="right"/>
 *     &lt;enumeration value="top"/>
 *     &lt;enumeration value="bottom"/>
 *     &lt;enumeration value="updiagonalstrike"/>
 *     &lt;enumeration value="downdiagonalstrike"/>
 *     &lt;enumeration value="verticalstrike"/>
 *     &lt;enumeration value="horizontalstrike"/>
 *     &lt;enumeration value="madruwb"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "notationstyle")
@XmlEnum
public enum Notationstyle {

    @XmlEnumValue("longdiv")
    LONGDIV("longdiv"),
    @XmlEnumValue("actuarial")
    ACTUARIAL("actuarial"),
    @XmlEnumValue("radical")
    RADICAL("radical"),
    @XmlEnumValue("box")
    BOX("box"),
    @XmlEnumValue("roundedbox")
    ROUNDEDBOX("roundedbox"),
    @XmlEnumValue("circle")
    CIRCLE("circle"),
    @XmlEnumValue("left")
    LEFT("left"),
    @XmlEnumValue("right")
    RIGHT("right"),
    @XmlEnumValue("top")
    TOP("top"),
    @XmlEnumValue("bottom")
    BOTTOM("bottom"),
    @XmlEnumValue("updiagonalstrike")
    UPDIAGONALSTRIKE("updiagonalstrike"),
    @XmlEnumValue("downdiagonalstrike")
    DOWNDIAGONALSTRIKE("downdiagonalstrike"),
    @XmlEnumValue("verticalstrike")
    VERTICALSTRIKE("verticalstrike"),
    @XmlEnumValue("horizontalstrike")
    HORIZONTALSTRIKE("horizontalstrike"),
    @XmlEnumValue("madruwb")
    MADRUWB("madruwb");
    private final String value;

    Notationstyle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Notationstyle fromValue(String v) {
        for (Notationstyle c: Notationstyle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
