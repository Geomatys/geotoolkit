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
package org.geotoolkit.ogcapi.dto.movingfeatures;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Response wrapper of {@code GET .../items/{mFeatureId}/tproperties}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    TemporalProperties.JSON_PROPERTY_TEMPORAL_PROPERTIES,
    TemporalProperties.JSON_PROPERTY_LINKS,
    TemporalProperties.JSON_PROPERTY_TIME_STAMP,
    TemporalProperties.JSON_PROPERTY_NUMBER_MATCHED,
    TemporalProperties.JSON_PROPERTY_NUMBER_RETURNED
})
public final class TemporalProperties extends DataTransferObject {

    public static final String JSON_PROPERTY_TEMPORAL_PROPERTIES = "temporalProperties";
    //ponytail: spec declares this as oneOf(array of "parametricValues"-shaped keyed objects,
    //array of temporalProperty); modeled as a raw list to pass either shape through untyped
    //rather than modeling both branches.
    private List<Object> temporalProperties;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    private OffsetDateTime timeStamp;

    public static final String JSON_PROPERTY_NUMBER_MATCHED = "numberMatched";
    private Integer numberMatched;

    public static final String JSON_PROPERTY_NUMBER_RETURNED = "numberReturned";
    private Integer numberReturned;

    public TemporalProperties() {
    }

    public TemporalProperties temporalProperties(List<Object> temporalProperties) {
        this.temporalProperties = temporalProperties;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Object> getTemporalProperties() {
        return temporalProperties;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTemporalProperties(List<Object> temporalProperties) {
        this.temporalProperties = temporalProperties;
    }

    public TemporalProperties links(List<Link> links) {
        this.links = links;
        return this;
    }

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

    public TemporalProperties timeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

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

    public TemporalProperties numberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

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

    public TemporalProperties numberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalProperties other = (TemporalProperties) o;
        return Objects.equals(this.temporalProperties, other.temporalProperties)
                && Objects.equals(this.links, other.links)
                && Objects.equals(this.timeStamp, other.timeStamp)
                && Objects.equals(this.numberMatched, other.numberMatched)
                && Objects.equals(this.numberReturned, other.numberReturned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temporalProperties, links, timeStamp, numberMatched, numberReturned);
    }

}
