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
package org.geotoolkit.ogcapi.dto.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A single bucket of a {@link Facet}.
 *
 * <p>OGC API - Records Part 2: Facets (draft).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public class FacetBucket extends DataTransferObject {

    public static final String JSON_PROPERTY_VALUE = "value";
    @XmlElement(name = "value")
    private Object value;

    public static final String JSON_PROPERTY_COUNT = "count";
    @XmlElement(name = "count")
    private Integer count;

    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(Object value) {
        this.value = value;
    }

    @JsonProperty(JSON_PROPERTY_COUNT)
    public Integer getCount() {
        return count;
    }

    @JsonProperty(JSON_PROPERTY_COUNT)
    public void setCount(Integer count) {
        this.count = count;
    }

}
