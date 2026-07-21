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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "JoinSources")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JoinSources")
public final class JoinSources extends DataTransferObject {

    public static final String JSON_PROPERTY_JOINS = "joins";
    @XmlElement(name = "joins")
    @jakarta.annotation.Nonnull
    private List<JoinSourceSummary> joins = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_NUMBER_MATCHED = "numberMatched";
    @XmlElement(name = "numberMatched")
    @jakarta.annotation.Nullable
    private Integer numberMatched;

    public static final String JSON_PROPERTY_NUMBER_RETURNED = "numberReturned";
    @XmlElement(name = "numberReturned")
    @jakarta.annotation.Nullable
    private Integer numberReturned;

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    @XmlElement(name = "timeStamp")
    @jakarta.annotation.Nullable
    private OffsetDateTime timeStamp;

    public JoinSources() {
    }

    public JoinSources joins(@jakarta.annotation.Nonnull List<JoinSourceSummary> joins) {
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
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_JOINS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "joins")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<JoinSourceSummary> getJoins() {
        return joins;
    }

    @JsonProperty(JSON_PROPERTY_JOINS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "joins")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setJoins(@jakarta.annotation.Nonnull List<JoinSourceSummary> joins) {
        this.joins = joins;
    }

    public JoinSources links(@jakarta.annotation.Nonnull List<Link> links) {
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
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
    }

    public JoinSources numberMatched(@jakarta.annotation.Nullable Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

    /**
     * The number of join sources that are available on the server and match the selection
     * parameters in the request. May be omitted if not known or difficult to compute.
     * @return numberMatched
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberMatched")
    public Integer getNumberMatched() {
        return numberMatched;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberMatched")
    public void setNumberMatched(@jakarta.annotation.Nullable Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    public JoinSources numberReturned(@jakarta.annotation.Nullable Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

    /**
     * The number of join sources returned in this page of the response. May be omitted if not
     * known or difficult to compute.
     * @return numberReturned
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberReturned")
    public Integer getNumberReturned() {
        return numberReturned;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberReturned")
    public void setNumberReturned(@jakarta.annotation.Nullable Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    public JoinSources timeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * The time when this response was generated. Distinct from the creation time of each
     * individual join source, exposed as {@link JoinSourceSummary#getTimeStamp()}.
     * @return timeStamp
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "timeStamp")
    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "timeStamp")
    public void setTimeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
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
