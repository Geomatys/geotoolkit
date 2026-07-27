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
 * Response body of {@code GET /processes}.
 */
@JsonPropertyOrder({
    ProcessList.JSON_PROPERTY_PROCESSES,
    ProcessList.JSON_PROPERTY_LINKS
})
public final class ProcessList extends DataTransferObject {

    public static final String JSON_PROPERTY_PROCESSES = "processes";
    private List<ProcessSummary> processes = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public ProcessList() {
    }

    public ProcessList processes(List<ProcessSummary> processes) {
        this.processes = processes;
        return this;
    }

    public ProcessList addProcessesItem(ProcessSummary processesItem) {
        if (this.processes == null) {
            this.processes = new ArrayList<>();
        }
        this.processes.add(processesItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_PROCESSES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<ProcessSummary> getProcesses() {
        return processes;
    }

    @JsonProperty(JSON_PROPERTY_PROCESSES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setProcesses(List<ProcessSummary> processes) {
        this.processes = processes;
    }

    public ProcessList links(List<Link> links) {
        this.links = links;
        return this;
    }

    public ProcessList addLinksItem(Link linksItem) {
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
