/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.kml.xml.v220;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for colorModeEnumType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="colorModeEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="random"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * Specifies the color mode for a graphic element.
 * Values for <colorMode> are normal (no effect) and random.  A value of random applies a random linear scale to the base <color> as follows:
 * To achieve a truly random selection of colors, specify a base <color> of transparent white (00ffffff).
 * If a single color component is specified (for example, a value of ff0000ff for red), random color values for that one component (red) will be selected.  In this case, the values would range from 00 (black) to ff (full red).
 * If values for two or for all three color components are specified, a random linear scale is applied to each color component, with results ranging from black to the maximum values specified for each component.
 * The opacity of a color comes from the alpha component of color and is never randomized.
 *
 */
@XmlType(name = "colorModeEnumType")
@XmlEnum
public enum ColorModeEnumType {

    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("random")
    RANDOM("random");
    private final String value;

    ColorModeEnumType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColorModeEnumType fromValue(final String v) {
        for (ColorModeEnumType c: ColorModeEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
