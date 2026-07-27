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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Restrictions on the availability of the collection that the user needs to
 * be aware of before using or redistributing the data: * unclassified:
 * Available for general disclosure * restricted: Not for general disclosure
 * * confidential: Available for someone who can be entrusted with
 * information * secret: Kept or meant to be kept private, unknown, or
 * hidden from all but a select group of people * topSecret: Of the highest
 * secrecy
 */
public enum AccessConstraintsCode {
    UNCLASSIFIED("unclassified"),
    RESTRICTED("restricted"),
    CONFIDENTIAL("confidential"),
    SECRET("secret"),
    TOP_SECRET("topSecret");
    private final String value;

    AccessConstraintsCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static AccessConstraintsCode fromValue(String value) {
        for (AccessConstraintsCode b : AccessConstraintsCode.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
