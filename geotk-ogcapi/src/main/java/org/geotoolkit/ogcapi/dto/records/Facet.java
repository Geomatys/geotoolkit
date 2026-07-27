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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Bucketed facet values for a single record property.
 *
 * <p>OGC API - Records Part 2: Facets (draft).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public class Facet extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_BUCKETS = "buckets";
    private List<FacetBucket> buckets = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(JSON_PROPERTY_BUCKETS)
    public List<FacetBucket> getBuckets() {
        return buckets;
    }

    @JsonProperty(JSON_PROPERTY_BUCKETS)
    public void setBuckets(List<FacetBucket> buckets) {
        this.buckets = buckets;
    }

}
