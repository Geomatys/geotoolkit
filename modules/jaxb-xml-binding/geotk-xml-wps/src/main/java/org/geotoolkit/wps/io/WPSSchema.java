/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.io;

/**
 * Enum of supported schemas for the WPS.
 *
 * @author Quentin Boileau (Geomatys).
 */
public enum WPSSchema {

    OGC_FEATURE_3_1_1("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd"),
    OGC_GML_3_1_1("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"),
    MATHML_3("http://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd");
    public final String schema;

    private WPSSchema(final String schema) {
        this.schema = schema;
    }

    public String getValue() {
        return schema;
    }

    public static WPSSchema customValueOf(final String candidate) {
        for (final WPSSchema schema : values()) {
            if (schema.getValue() != null) {
                if (schema.getValue().equalsIgnoreCase(candidate)) {
                    return schema;
                }
            }
        }
        return null;
    }
}
