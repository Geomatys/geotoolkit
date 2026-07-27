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
 * Response of {@code GET /collections/{collectionId}/joins}, listing the join sources related to
 * a specific collection (spec schema {@code CollectionJoinsResponseObject}).
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html#_response_schema_for_the_join_sources
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    JoinSources.JSON_PROPERTY_JOINS,
    JoinSources.JSON_PROPERTY_LINKS,
    JoinSources.JSON_PROPERTY_NUMBER_MATCHED,
    JoinSources.JSON_PROPERTY_NUMBER_RETURNED,
    JoinSources.JSON_PROPERTY_TIME_STAMP
})
public final class JoinSources extends DataTransferObject {

    public static final String JSON_PROPERTY_JOINS = "joins";
    private List<JoinSourceSummary> joins = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_NUMBER_MATCHED = "numberMatched";
    private Integer numberMatched;

    public static final String JSON_PROPERTY_NUMBER_RETURNED = "numberReturned";
    private Integer numberReturned;

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    private OffsetDateTime timeStamp;

    public JoinSources() {
    }

    public JoinSources joins(List<JoinSourceSummary> joins) {
        this.joins = joins;
        return this;
    }

    public JoinSources addJoinsItem(JoinSourceSummary joinsItem) {
        if (this.joins == null) {
            this.joins = new ArrayList<>();
        }
        this.joins.add(joinsItem);
        return this;
    }

    /**
     * Metadata describing each join source hosted by the server for this collection.
     * @return joins
     */
    @JsonProperty(JSON_PROPERTY_JOINS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<JoinSourceSummary> getJoins() {
        return joins;
    }

    @JsonProperty(JSON_PROPERTY_JOINS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setJoins(List<JoinSourceSummary> joins) {
        this.joins = joins;
    }

    public JoinSources links(List<Link> links) {
        this.links = links;
        return this;
    }

    public JoinSources addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links supporting hypermedia navigation, including a {@code self} link to this response and
     * an {@code alternate} link for every other media type supported by the service, and a
     * {@code next} link if the response is paged.
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

    public JoinSources numberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

    /**
     * The number of join sources that are available on the server and match the selection
     * parameters in the request. May be omitted if not known or difficult to compute.
     * @return numberMatched
     */
    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getNumberMatched() {
        return numberMatched;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNumberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    public JoinSources numberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

    /**
     * The number of join sources returned in this page of the response. May be omitted if not
     * known or difficult to compute.
     * @return numberReturned
     */
    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getNumberReturned() {
        return numberReturned;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNumberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    public JoinSources timeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * The time when this response was generated. Distinct from the creation time of each
     * individual join source, exposed as {@link JoinSourceSummary#getTimeStamp()}.
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
        JoinSources that = (JoinSources) o;
        return Objects.equals(this.joins, that.joins)
                && Objects.equals(this.links, that.links)
                && Objects.equals(this.numberMatched, that.numberMatched)
                && Objects.equals(this.numberReturned, that.numberReturned)
                && Objects.equals(this.timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joins, links, numberMatched, numberReturned, timeStamp);
    }

}
