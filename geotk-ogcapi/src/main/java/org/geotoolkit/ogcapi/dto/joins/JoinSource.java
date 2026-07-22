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
@XmlRootElement(name = "JoinSource")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JoinSource")
public final class JoinSource extends DataTransferObject {

    public static final String JSON_PROPERTY_DETAILS = "details";
    @XmlElement(name = "details")
    @jakarta.annotation.Nonnull
    private JoinSourceDetails details;

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    @XmlElement(name = "timeStamp")
    @jakarta.annotation.Nullable
    private OffsetDateTime timeStamp;

    public JoinSource() {
    }

    public JoinSource details(@jakarta.annotation.Nonnull JoinSourceDetails details) {
        this.details = details;
        return this;
    }

    /**
     * Details of the join source.
     * @return details
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DETAILS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "details")
    public JoinSourceDetails getDetails() {
        return details;
    }

    @JsonProperty(JSON_PROPERTY_DETAILS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "details")
    public void setDetails(@jakarta.annotation.Nonnull JoinSourceDetails details) {
        this.details = details;
    }

    public JoinSource id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier for the join source.
     * @return id
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nonnull String id) {
        this.id = id;
    }

    public JoinSource links(@jakarta.annotation.Nonnull List<Link> links) {
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

    public JoinSource timeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * The time when the join source output was requested.
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
