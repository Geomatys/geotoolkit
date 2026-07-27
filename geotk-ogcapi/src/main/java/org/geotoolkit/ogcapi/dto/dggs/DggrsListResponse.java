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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * DatasetGetDGGRSList200Response
 */
@JsonPropertyOrder({
    DggrsListResponse.JSON_PROPERTY_LINKS,
    DggrsListResponse.JSON_PROPERTY_DGGRS
})
public final class DggrsListResponse extends DataTransferObject {

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_DGGRS = "dggrs";
    private List<DggrsItem> dggrs = new ArrayList<>();

    public DggrsListResponse() {
    }

    public DggrsListResponse links(List<Link> links) {
        this.links = links;
        return this;
    }

    public DggrsListResponse addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public DggrsListResponse dggrs(List<DggrsItem> dggrs) {
        this.dggrs = dggrs;
        return this;
    }

    public DggrsListResponse addDggrsItem(DggrsItem dggrsItem) {
        if (this.dggrs == null) {
            this.dggrs = new ArrayList<>();
        }
        this.dggrs.add(dggrsItem);
        return this;
    }

    /**
     * Get dggrs
     *
     * @return dggrs
     */
    @JsonProperty(JSON_PROPERTY_DGGRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<DggrsItem> getDggrs() {
        return dggrs;
    }

    @JsonProperty(JSON_PROPERTY_DGGRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDggrs(List<DggrsItem> dggrs) {
        this.dggrs = dggrs;
    }

    /**
     * Return true if this _dataset_getDGGRSList_200_response object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsListResponse datasetGetDGGRSList200Response = (DggrsListResponse) o;
        return Objects.equals(this.links, datasetGetDGGRSList200Response.links)
                && Objects.equals(this.dggrs, datasetGetDGGRSList200Response.dggrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, dggrs);
    }

}
