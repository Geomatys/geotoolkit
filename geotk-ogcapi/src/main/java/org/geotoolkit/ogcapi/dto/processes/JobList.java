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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Response body of {@code GET /jobs} (non-Core "Job list" conformance class).
 */
@JsonPropertyOrder({
    JobList.JSON_PROPERTY_JOBS,
    JobList.JSON_PROPERTY_LINKS
})
public final class JobList extends DataTransferObject {

    public static final String JSON_PROPERTY_JOBS = "jobs";
    private List<StatusInfo> jobs = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public JobList() {
    }

    public JobList jobs(List<StatusInfo> jobs) {
        this.jobs = jobs;
        return this;
    }

    public JobList addJobsItem(StatusInfo jobsItem) {
        if (this.jobs == null) {
            this.jobs = new ArrayList<>();
        }
        this.jobs.add(jobsItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_JOBS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<StatusInfo> getJobs() {
        return jobs;
    }

    @JsonProperty(JSON_PROPERTY_JOBS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setJobs(List<StatusInfo> jobs) {
        this.jobs = jobs;
    }

    public JobList links(List<Link> links) {
        this.links = links;
        return this;
    }

    public JobList addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

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
}
