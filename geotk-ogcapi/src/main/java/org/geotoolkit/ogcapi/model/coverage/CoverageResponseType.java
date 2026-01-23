/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.model.coverage;

/**
 * @author Quentin BIALOTA
 */
public enum CoverageResponseType {
    DataRecord("DataRecord"),
    DomainSet("DomainSet");

    private final String value;

    CoverageResponseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CoverageResponseType fromValue(String value) {
        for (CoverageResponseType status : CoverageResponseType.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid coverage response type: " + value);
    }
}
