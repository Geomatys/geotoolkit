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
package org.geotoolkit.data.wfs;

/**
 * <p>Java class for IdentifierGenerationOptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="IdentifierGenerationOptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UseExisting"/>
 *     &lt;enumeration value="ReplaceDuplicate"/>
 *     &lt;enumeration value="GenerateNew"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum IdentifierGenerationOption {

    USE_EXISTING("UseExisting"),
    REPLACE_DUPLICATE("ReplaceDuplicate"),
    GENERATE_NEW("GenerateNew");

    private final String value;

    IdentifierGenerationOption(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IdentifierGenerationOption fromValue(String v) {
        for (IdentifierGenerationOption c: IdentifierGenerationOption.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
