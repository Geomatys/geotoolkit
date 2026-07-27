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
public final class StatusInfo extends DataTransferObject {

    public static final String JSON_PROPERTY_PROCESS_ID = "processID";
    private String processID;

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type = "process";

    public static final String JSON_PROPERTY_JOB_ID = "jobID";
    private String jobID;

    public static final String JSON_PROPERTY_STATUS = "status";
    private StatusCode status;

    public static final String JSON_PROPERTY_MESSAGE = "message";
    private String message;

    public static final String JSON_PROPERTY_CREATED = "created";
    private OffsetDateTime created;

    public static final String JSON_PROPERTY_STARTED = "started";
    private OffsetDateTime started;

    public static final String JSON_PROPERTY_FINISHED = "finished";
    private OffsetDateTime finished;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    private OffsetDateTime updated;

    public static final String JSON_PROPERTY_PROGRESS = "progress";
    private Integer progress;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    public StatusInfo() {
    }

    public StatusInfo processID(String processID) {
        this.processID = processID;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_PROCESS_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getProcessID() {
        return processID;
    }

    @JsonProperty(JSON_PROPERTY_PROCESS_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public StatusInfo type(String type) {
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

    public StatusInfo jobID(String jobID) {
        this.jobID = jobID;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_JOB_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getJobID() {
        return jobID;
    }

    @JsonProperty(JSON_PROPERTY_JOB_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public StatusInfo status(StatusCode status) {
        this.status = status;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public StatusCode getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setStatus(StatusCode status) {
        this.status = status;
    }

    public StatusInfo message(String message) {
        this.message = message;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getMessage() {
        return message;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMessage(String message) {
        this.message = message;
    }

    public StatusInfo created(OffsetDateTime created) {
        this.created = created;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public StatusInfo started(OffsetDateTime started) {
        this.started = started;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_STARTED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getStarted() {
        return started;
    }

    @JsonProperty(JSON_PROPERTY_STARTED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStarted(OffsetDateTime started) {
        this.started = started;
    }

    public StatusInfo finished(OffsetDateTime finished) {
        this.finished = finished;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_FINISHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getFinished() {
        return finished;
    }

    @JsonProperty(JSON_PROPERTY_FINISHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFinished(OffsetDateTime finished) {
        this.finished = finished;
    }

    public StatusInfo updated(OffsetDateTime updated) {
        this.updated = updated;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    public StatusInfo progress(Integer progress) {
        this.progress = progress;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_PROGRESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getProgress() {
        return progress;
    }

    @JsonProperty(JSON_PROPERTY_PROGRESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public StatusInfo links(List<Link> links) {
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
}
