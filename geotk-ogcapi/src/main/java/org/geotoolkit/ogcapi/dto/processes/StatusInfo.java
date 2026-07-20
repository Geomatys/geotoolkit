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
package org.geotoolkit.ogcapi.dto.processes;

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
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Status of a job, returned by {@code GET /jobs/{jobID}} and as the
 * async-execution response of {@code POST /processes/{processID}/execution}.
 */
@JsonPropertyOrder({
    StatusInfo.JSON_PROPERTY_PROCESS_ID,
    StatusInfo.JSON_PROPERTY_TYPE,
    StatusInfo.JSON_PROPERTY_JOB_ID,
    StatusInfo.JSON_PROPERTY_STATUS,
    StatusInfo.JSON_PROPERTY_MESSAGE,
    StatusInfo.JSON_PROPERTY_CREATED,
    StatusInfo.JSON_PROPERTY_STARTED,
    StatusInfo.JSON_PROPERTY_FINISHED,
    StatusInfo.JSON_PROPERTY_UPDATED,
    StatusInfo.JSON_PROPERTY_PROGRESS,
    StatusInfo.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "StatusInfo")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "StatusInfo")
public final class StatusInfo extends DataTransferObject {

    public static final String JSON_PROPERTY_PROCESS_ID = "processID";
    @XmlElement(name = "processID")
    @jakarta.annotation.Nullable
    private String processID;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private String type = "process";

    public static final String JSON_PROPERTY_JOB_ID = "jobID";
    @XmlElement(name = "jobID")
    @jakarta.annotation.Nonnull
    private String jobID;

    public static final String JSON_PROPERTY_STATUS = "status";
    @XmlElement(name = "status")
    @jakarta.annotation.Nonnull
    private StatusCode status;

    public static final String JSON_PROPERTY_MESSAGE = "message";
    @XmlElement(name = "message")
    @jakarta.annotation.Nullable
    private String message;

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created")
    @jakarta.annotation.Nullable
    private OffsetDateTime created;

    public static final String JSON_PROPERTY_STARTED = "started";
    @XmlElement(name = "started")
    @jakarta.annotation.Nullable
    private OffsetDateTime started;

    public static final String JSON_PROPERTY_FINISHED = "finished";
    @XmlElement(name = "finished")
    @jakarta.annotation.Nullable
    private OffsetDateTime finished;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    @XmlElement(name = "updated")
    @jakarta.annotation.Nullable
    private OffsetDateTime updated;

    public static final String JSON_PROPERTY_PROGRESS = "progress";
    @XmlElement(name = "progress")
    @jakarta.annotation.Nullable
    private Integer progress;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nullable
    private List<Link> links;

    public StatusInfo() {
    }

    public StatusInfo processID(@jakarta.annotation.Nullable String processID) {
        this.processID = processID;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROCESS_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "processID")
    public String getProcessID() {
        return processID;
    }

    @JsonProperty(JSON_PROPERTY_PROCESS_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "processID")
    public void setProcessID(@jakarta.annotation.Nullable String processID) {
        this.processID = processID;
    }

    public StatusInfo type(@jakarta.annotation.Nonnull String type) {
        this.type = type;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull String type) {
        this.type = type;
    }

    public StatusInfo jobID(@jakarta.annotation.Nonnull String jobID) {
        this.jobID = jobID;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_JOB_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "jobID")
    public String getJobID() {
        return jobID;
    }

    @JsonProperty(JSON_PROPERTY_JOB_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "jobID")
    public void setJobID(@jakarta.annotation.Nonnull String jobID) {
        this.jobID = jobID;
    }

    public StatusInfo status(@jakarta.annotation.Nonnull StatusCode status) {
        this.status = status;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "status")
    public StatusCode getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "status")
    public void setStatus(@jakarta.annotation.Nonnull StatusCode status) {
        this.status = status;
    }

    public StatusInfo message(@jakarta.annotation.Nullable String message) {
        this.message = message;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "message")
    public void setMessage(@jakarta.annotation.Nullable String message) {
        this.message = message;
    }

    public StatusInfo created(@jakarta.annotation.Nullable OffsetDateTime created) {
        this.created = created;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public OffsetDateTime getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public void setCreated(@jakarta.annotation.Nullable OffsetDateTime created) {
        this.created = created;
    }

    public StatusInfo started(@jakarta.annotation.Nullable OffsetDateTime started) {
        this.started = started;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STARTED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "started")
    public OffsetDateTime getStarted() {
        return started;
    }

    @JsonProperty(JSON_PROPERTY_STARTED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "started")
    public void setStarted(@jakarta.annotation.Nullable OffsetDateTime started) {
        this.started = started;
    }

    public StatusInfo finished(@jakarta.annotation.Nullable OffsetDateTime finished) {
        this.finished = finished;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FINISHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "finished")
    public OffsetDateTime getFinished() {
        return finished;
    }

    @JsonProperty(JSON_PROPERTY_FINISHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "finished")
    public void setFinished(@jakarta.annotation.Nullable OffsetDateTime finished) {
        this.finished = finished;
    }

    public StatusInfo updated(@jakarta.annotation.Nullable OffsetDateTime updated) {
        this.updated = updated;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public OffsetDateTime getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public void setUpdated(@jakarta.annotation.Nullable OffsetDateTime updated) {
        this.updated = updated;
    }

    public StatusInfo progress(@jakarta.annotation.Nullable Integer progress) {
        this.progress = progress;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROGRESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "progress")
    public Integer getProgress() {
        return progress;
    }

    @JsonProperty(JSON_PROPERTY_PROGRESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "progress")
    public void setProgress(@jakarta.annotation.Nullable Integer progress) {
        this.progress = progress;
    }

    public StatusInfo links(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
        return this;
    }

    public StatusInfo addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }
}
