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
 * A join source, as returned by {@code GET /collections/{collectionId}/joins/{joinId}} and
 * {@code POST /collections/{collectionId}/joins} (spec schema {@code JoinSourceResponseObject}).
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html#_response_schema_for_the_join_source
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    JoinSource.JSON_PROPERTY_DETAILS,
    JoinSource.JSON_PROPERTY_ID,
    JoinSource.JSON_PROPERTY_LINKS,
    JoinSource.JSON_PROPERTY_TIME_STAMP
})
public final class JoinSource extends DataTransferObject {

    public static final String JSON_PROPERTY_DETAILS = "details";
    private JoinSourceDetails details;

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    private OffsetDateTime timeStamp;

    public JoinSource() {
    }

    public JoinSource details(JoinSourceDetails details) {
        this.details = details;
        return this;
    }

    /**
     * Details of the join source.
     * @return details
     */
    @JsonProperty(JSON_PROPERTY_DETAILS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public JoinSourceDetails getDetails() {
        return details;
    }

    @JsonProperty(JSON_PROPERTY_DETAILS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDetails(JoinSourceDetails details) {
        this.details = details;
    }

    public JoinSource id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier for the join source.
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

    public JoinSource links(List<Link> links) {
        this.links = links;
        return this;
    }

    public JoinSource addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links supporting hypermedia navigation, including a {@code self} link to this response and
     * an {@code alternate} link for every other media type supported by the service.
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

    public JoinSource timeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * The time when the join source output was requested.
     * @return timeStamp
     */
    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JoinSource that = (JoinSource) o;
        return Objects.equals(this.details, that.details)
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.links, that.links)
                && Objects.equals(this.timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(details, id, links, timeStamp);
    }

}
