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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A boolean scalar SWE Common data component.
 *
 * <p>Named {@code Boolean} to match the standard's {@code type} discriminator value; within this
 * package, use the fully qualified {@code java.lang.Boolean} whenever the JDK wrapper type is
 * meant.</p>
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Boolean extends AbstractSimpleComponent {

    public static final String JSON_PROPERTY_VALUE = "value";
    private java.lang.Boolean value;

    public Boolean() {
        setType("Boolean");
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public java.lang.Boolean getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(java.lang.Boolean value) {
        this.value = value;
    }

}
