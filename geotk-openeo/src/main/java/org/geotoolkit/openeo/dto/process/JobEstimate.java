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
package org.geotoolkit.openeo.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

import java.util.Objects;

/**
 * Response body for GET /jobs/{jobId}/estimate.
 *
 * <p>Provides cost and resource estimates for a batch job before it starts processing.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Batch-Jobs/operation/estimate-job">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        JobEstimate.JSON_PROPERTY_COSTS,
        JobEstimate.JSON_PROPERTY_DURATION,
        JobEstimate.JSON_PROPERTY_SIZE,
        JobEstimate.JSON_PROPERTY_DOWNLOADS_INCLUDED,
        JobEstimate.JSON_PROPERTY_EXPIRES,
        JobEstimate.JSON_PROPERTY_COSTS_FINAL
})
@XmlRootElement(name = "JobEstimate")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JobEstimate")
public class JobEstimate extends DataTransferObject {

    public static final String JSON_PROPERTY_COSTS = "costs";
    @XmlElement(name = "costs")
    @JacksonXmlProperty(localName = "costs")
    @jakarta.annotation.Nullable
    private Double costs;

    public static final String JSON_PROPERTY_DURATION = "duration";
    @XmlElement(name = "duration")
    @JacksonXmlProperty(localName = "duration")
    @jakarta.annotation.Nullable
    private String duration;

    public static final String JSON_PROPERTY_SIZE = "size";
    @XmlElement(name = "size")
    @JacksonXmlProperty(localName = "size")
    @jakarta.annotation.Nullable
    private Long size;

    public static final String JSON_PROPERTY_DOWNLOADS_INCLUDED = "downloads_included";
    @XmlElement(name = "downloads_included")
    @JacksonXmlProperty(localName = "downloads_included")
    @jakarta.annotation.Nullable
    private Integer downloadsIncluded;

    public static final String JSON_PROPERTY_EXPIRES = "expires";
    @XmlElement(name = "expires")
    @JacksonXmlProperty(localName = "expires")
    @jakarta.annotation.Nullable
    private String expires;

    public static final String JSON_PROPERTY_COSTS_FINAL = "costs_final";
    @XmlElement(name = "costs_final")
    @JacksonXmlProperty(localName = "costs_final")
    @jakarta.annotation.Nullable
    private Boolean costsFinal;

    public JobEstimate costs(Double costs) { this.costs = costs; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COSTS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_COSTS)
    public Double getCosts() { return costs; }

    @JsonProperty(JSON_PROPERTY_COSTS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_COSTS)
    public void setCosts(@jakarta.annotation.Nullable Double costs) { this.costs = costs; }

    public JobEstimate duration(String duration) { this.duration = duration; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DURATION)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DURATION)
    public String getDuration() { return duration; }

    @JsonProperty(JSON_PROPERTY_DURATION)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DURATION)
    public void setDuration(@jakarta.annotation.Nullable String duration) { this.duration = duration; }

    public JobEstimate size(Long size) { this.size = size; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SIZE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SIZE)
    public Long getSize() { return size; }

    @JsonProperty(JSON_PROPERTY_SIZE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SIZE)
    public void setSize(@jakarta.annotation.Nullable Long size) { this.size = size; }

    public JobEstimate downloadsIncluded(Integer downloadsIncluded) { this.downloadsIncluded = downloadsIncluded; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DOWNLOADS_INCLUDED)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DOWNLOADS_INCLUDED)
    public Integer getDownloadsIncluded() { return downloadsIncluded; }

    @JsonProperty(JSON_PROPERTY_DOWNLOADS_INCLUDED)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DOWNLOADS_INCLUDED)
    public void setDownloadsIncluded(@jakarta.annotation.Nullable Integer downloadsIncluded) { this.downloadsIncluded = downloadsIncluded; }

    public JobEstimate expires(String expires) { this.expires = expires; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXPIRES)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_EXPIRES)
    public String getExpires() { return expires; }

    @JsonProperty(JSON_PROPERTY_EXPIRES)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_EXPIRES)
    public void setExpires(@jakarta.annotation.Nullable String expires) { this.expires = expires; }

    public JobEstimate costsFinal(Boolean costsFinal) { this.costsFinal = costsFinal; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COSTS_FINAL)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_COSTS_FINAL)
    public Boolean getCostsFinal() { return costsFinal; }

    @JsonProperty(JSON_PROPERTY_COSTS_FINAL)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_COSTS_FINAL)
    public void setCostsFinal(@jakarta.annotation.Nullable Boolean costsFinal) { this.costsFinal = costsFinal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobEstimate that = (JobEstimate) o;
        return Objects.equals(this.costs, that.costs)
                && Objects.equals(this.duration, that.duration)
                && Objects.equals(this.size, that.size)
                && Objects.equals(this.downloadsIncluded, that.downloadsIncluded)
                && Objects.equals(this.expires, that.expires)
                && Objects.equals(this.costsFinal, that.costsFinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(costs, duration, size, downloadsIncluded, expires, costsFinal);
    }
}
