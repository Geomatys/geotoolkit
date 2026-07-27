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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.encoding;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The XML encoding.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class XmlEncoding extends AbstractEncoding {

    public static final String JSON_PROPERTY_NAMESPACE = "namespace";
    private String namespace;

    public XmlEncoding() {
        setType("XMLEncoding");
    }

    @JsonProperty(JSON_PROPERTY_NAMESPACE)
    public String getNamespace() {
        return namespace;
    }

    @JsonProperty(JSON_PROPERTY_NAMESPACE)
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

}
