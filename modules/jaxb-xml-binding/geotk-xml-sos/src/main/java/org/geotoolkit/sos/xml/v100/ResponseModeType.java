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
package org.geotoolkit.sos.xml.v100;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for responseModeType.
 * 
 */
@XmlType(name = "responseModeType")
@XmlEnum
public enum ResponseModeType {

    @XmlEnumValue("inline")
    INLINE("inline"),
    @XmlEnumValue("attached")
    ATTACHED("attached"),
    @XmlEnumValue("out-of-band")
    OUT_OF_BAND("out-of-band"),
    @XmlEnumValue("resultTemplate")
    RESULT_TEMPLATE("resultTemplate");
    private final String value;

    ResponseModeType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResponseModeType fromValue(final String v) {
        for (ResponseModeType c: ResponseModeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
