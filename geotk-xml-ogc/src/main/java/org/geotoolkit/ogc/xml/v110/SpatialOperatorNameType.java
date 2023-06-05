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
package org.geotoolkit.ogc.xml.v110;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for SpatialOperatorNameType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SpatialOperatorNameType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BBOX"/>
 *     &lt;enumeration value="Equals"/>
 *     &lt;enumeration value="Disjoint"/>
 *     &lt;enumeration value="Intersects"/>
 *     &lt;enumeration value="Touches"/>
 *     &lt;enumeration value="Crosses"/>
 *     &lt;enumeration value="Within"/>
 *     &lt;enumeration value="Contains"/>
 *     &lt;enumeration value="Overlaps"/>
 *     &lt;enumeration value="Beyond"/>
 *     &lt;enumeration value="DWithin"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlEnum
public enum SpatialOperatorNameType {

    BBOX("BBOX"),
    @XmlEnumValue("Equals")
    EQUALS("Equals"),
    @XmlEnumValue("Disjoint")
    DISJOINT("Disjoint"),
    @XmlEnumValue("Intersects")
    INTERSECTS("Intersects"),
    @XmlEnumValue("Touches")
    TOUCHES("Touches"),
    @XmlEnumValue("Crosses")
    CROSSES("Crosses"),
    @XmlEnumValue("Within")
    WITHIN("Within"),
    @XmlEnumValue("Contains")
    CONTAINS("Contains"),
    @XmlEnumValue("Overlaps")
    OVERLAPS("Overlaps"),
    @XmlEnumValue("Beyond")
    BEYOND("Beyond"),
    @XmlEnumValue("DWithin")
    D_WITHIN("DWithin");
    private final String value;

    SpatialOperatorNameType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SpatialOperatorNameType fromValue(final String v) {
        for (SpatialOperatorNameType c: SpatialOperatorNameType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }
}
