/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The direction for text in this language. The default, &#x60;ltr&#x60;
 * (left-to-right), represents the most common situation. However, care
 * should be taken to set the value of &#x60;dir&#x60; appropriately if the
 * language direction is not &#x60;ltr&#x60;. Other values supported are
 * &#x60;rtl&#x60; (right-to-left), &#x60;ttb&#x60; (top-to-bottom), and
 * &#x60;btt&#x60; (bottom-to-top).
 */
@XmlType(name = "DirEnum")
@XmlEnum(value = String.class)
public enum LanguageDirection {
    @XmlEnumValue(value = "ltr")
    LTR(String.valueOf("ltr")),
    @XmlEnumValue(value = "rtl")
    RTL(String.valueOf("rtl")),
    @XmlEnumValue(value = "ttb")
    TTB(String.valueOf("ttb")),
    @XmlEnumValue(value = "btt")
    BTT(String.valueOf("btt"));
    private String value;

    LanguageDirection(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static LanguageDirection fromValue(String value) {
        for (LanguageDirection b : LanguageDirection.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
