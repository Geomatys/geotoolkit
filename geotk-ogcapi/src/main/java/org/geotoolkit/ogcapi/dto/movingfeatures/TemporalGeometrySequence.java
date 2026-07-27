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
 * Response wrapper of {@code GET .../items/{mFeatureId}/tgsequence}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    TemporalGeometrySequence.JSON_PROPERTY_TYPE,
    TemporalGeometrySequence.JSON_PROPERTY_GEOMETRY_SEQUENCE,
    TemporalGeometrySequence.JSON_PROPERTY_LINKS,
    TemporalGeometrySequence.JSON_PROPERTY_TIME_STAMP,
    TemporalGeometrySequence.JSON_PROPERTY_NUMBER_MATCHED,
    TemporalGeometrySequence.JSON_PROPERTY_NUMBER_RETURNED
})
public final class TemporalGeometrySequence extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_GEOMETRY_SEQUENCE = "geometrySequence";
    private List<TemporalPrimitiveGeometry> geometrySequence;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    private OffsetDateTime timeStamp;

    public static final String JSON_PROPERTY_NUMBER_MATCHED = "numberMatched";
    private Integer numberMatched;

    public static final String JSON_PROPERTY_NUMBER_RETURNED = "numberReturned";
    private Integer numberReturned;

    public TemporalGeometrySequence() {
    }

    public TemporalGeometrySequence type(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(String type) {
        this.type = type;
    }

    public TemporalGeometrySequence geometrySequence(List<TemporalPrimitiveGeometry> geometrySequence) {
        this.geometrySequence = geometrySequence;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY_SEQUENCE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<TemporalPrimitiveGeometry> getGeometrySequence() {
        return geometrySequence;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY_SEQUENCE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setGeometrySequence(List<TemporalPrimitiveGeometry> geometrySequence) {
        this.geometrySequence = geometrySequence;
    }

    public TemporalGeometrySequence links(List<Link> links) {
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

    public TemporalGeometrySequence timeStamp(OffsetDateTime timeStamp) {
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

    public TemporalGeometrySequence numberMatched(Integer numberMatched) {
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

    public TemporalGeometrySequence numberReturned(Integer numberReturned) {
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
        TemporalGeometrySequence other = (TemporalGeometrySequence) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.geometrySequence, other.geometrySequence)
                && Objects.equals(this.links, other.links)
                && Objects.equals(this.timeStamp, other.timeStamp)
                && Objects.equals(this.numberMatched, other.numberMatched)
                && Objects.equals(this.numberReturned, other.numberReturned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, geometrySequence, links, timeStamp, numberMatched, numberReturned);
    }

}
