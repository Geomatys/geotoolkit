/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoutePreferenceType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RoutePreferenceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Fastest"/>
 *     &lt;enumeration value="Shortest"/>
 *     &lt;enumeration value="Pedestrian"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "RoutePreferenceType")
@XmlEnum
public enum RoutePreferenceType {


    /**
     * Minimize the travel time by vehicle.
     *
     */
    @XmlEnumValue("Fastest")
    FASTEST("Fastest"),

    /**
     * Minimize the travel distance by vehicle.
     *
     */
    @XmlEnumValue("Shortest")
    SHORTEST("Shortest"),

    /**
     * Best route by foot.
     *
     */
    @XmlEnumValue("Pedestrian")
    PEDESTRIAN("Pedestrian");
    private final String value;

    RoutePreferenceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RoutePreferenceType fromValue(String v) {
        for (RoutePreferenceType c: RoutePreferenceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
