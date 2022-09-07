/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.observation.model;

import java.util.Date;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum FieldType {

    QUANTITY("Quantity"),
    TEXT("Text"),
    BOOLEAN("Boolean"),
    TIME("Time");

    public final String label;

    private FieldType(String label) {
        this.label = label;
    }

    public static FieldType fromLabel(String label) {
        if (label == null) return null;
        switch (label) {
            case "Quantity": return QUANTITY;
            case "Text": return TEXT;
            case "Boolean": return BOOLEAN;
            case "Time": return TIME;
            default: throw new IllegalArgumentException("Unexpected value for field type enum:" + label);
        }
    }

    public Class<?> getJavaType() {
        switch (this) {
            case QUANTITY: return Double.class;
            case TEXT: return String.class;
            case BOOLEAN: return Boolean.class;
            case TIME: return Date.class;
            default: throw new IllegalArgumentException("Unexpected value for field type enum:" + this);
        }
    }
}
