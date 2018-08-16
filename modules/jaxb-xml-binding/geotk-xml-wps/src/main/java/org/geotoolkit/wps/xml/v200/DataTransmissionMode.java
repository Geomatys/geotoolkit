/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.Arrays;
import java.util.Optional;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataTransmissionMode.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTransmissionMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="value"/>
 *     &lt;enumeration value="reference"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "DataTransmissionModeType")
@XmlEnum
public enum DataTransmissionMode {

    @XmlEnumValue("value")
    VALUE("value"),
    @XmlEnumValue("reference")
    REFERENCE("reference");
    private final String value;

    DataTransmissionMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Optional<DataTransmissionMode> fromValue(String v) {
        if (v == null || (v = v.trim()).isEmpty()) {
            return Optional.empty();
        }

        final String queriedVal = v;
        return Arrays.stream(values())
                .filter(mode -> mode.value().equalsIgnoreCase(queriedVal))
                .findAny();
    }
}
