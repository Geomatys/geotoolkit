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
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Response body of {@code GET /collections/{collectionId}/facets} ({@code application/facets+json}).
 *
 * <p>OGC API - Records Part 2: Facets (draft).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public class Facets extends DataTransferObject {

    public static final String JSON_PROPERTY_FACETS = "facets";
    private List<Facet> facets = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_FACETS)
    public List<Facet> getFacets() {
        return facets;
    }

    @JsonProperty(JSON_PROPERTY_FACETS)
    public void setFacets(List<Facet> facets) {
        this.facets = facets;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
