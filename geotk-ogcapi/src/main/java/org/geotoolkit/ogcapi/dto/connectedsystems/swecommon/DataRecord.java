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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;

/**
 * A record (named tuple) SWE Common data component.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class DataRecord extends AbstractDataComponent {

    public static final String JSON_PROPERTY_FIELDS = "fields";
    private List<AbstractDataComponent> fields;

    public DataRecord() {
        setType("DataRecord");
    }

    /**
     * Ordered list of named fields. (required)
     */
    @JsonProperty(JSON_PROPERTY_FIELDS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<AbstractDataComponent> getFields() {
        return fields;
    }

    @JsonProperty(JSON_PROPERTY_FIELDS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setFields(List<AbstractDataComponent> fields) {
        this.fields = fields;
    }

}
