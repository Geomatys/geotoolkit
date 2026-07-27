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
package org.geotoolkit.ogcapi.dto.joins;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Summary of a join source, as listed in the {@code joins} array of the response of
 * {@code GET /collections/{collectionId}/joins} (spec schema {@code JoinsObject}).
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html#_response_schema_for_the_join_sources
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    JoinSourceSummary.JSON_PROPERTY_ID,
    JoinSourceSummary.JSON_PROPERTY_TIME_STAMP,
    JoinSourceSummary.JSON_PROPERTY_LINKS
})
public final class JoinSourceSummary extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    private OffsetDateTime timeStamp;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public JoinSourceSummary() {
    }

    public JoinSourceSummary id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Identifier of the join source.
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(String id) {
        this.id = id;
    }

    public JoinSourceSummary timeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * The time when the join source was generated.
     * @return timeStamp
     */
    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public JoinSourceSummary links(List<Link> links) {
        this.links = links;
        return this;
    }

    public JoinSourceSummary addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links supporting hypermedia navigation. Includes, for each supported encoding, a link to
     * the join source resource (relation {@link org.geotoolkit.ogcapi.dto.LinkRelations#OGC_JOINS_JOIN_SOURCE}).
     * @return links
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JoinSourceSummary that = (JoinSourceSummary) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.timeStamp, that.timeStamp)
                && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeStamp, links);
    }

}
