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
 * Summary description of a process, as returned in a {@link ProcessList}.
 * Not final: {@link ProcessDescription} extends it with the {@code inputs}/
 * {@code outputs} maps, matching the standard's {@code allOf} composition.
 */
@JsonPropertyOrder({
    ProcessSummary.JSON_PROPERTY_TITLE,
    ProcessSummary.JSON_PROPERTY_DESCRIPTION,
    ProcessSummary.JSON_PROPERTY_KEYWORDS,
    ProcessSummary.JSON_PROPERTY_METADATA,
    ProcessSummary.JSON_PROPERTY_ID,
    ProcessSummary.JSON_PROPERTY_VERSION,
    ProcessSummary.JSON_PROPERTY_JOB_CONTROL_OPTIONS,
    ProcessSummary.JSON_PROPERTY_OUTPUT_TRANSMISSION,
    ProcessSummary.JSON_PROPERTY_LINKS
})
public class ProcessSummary extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords;

    public static final String JSON_PROPERTY_METADATA = "metadata";
    private List<Metadata> metadata;

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_VERSION = "version";
    private String version;

    public static final String JSON_PROPERTY_JOB_CONTROL_OPTIONS = "jobControlOptions";
    private List<JobControlOption> jobControlOptions;

    public static final String JSON_PROPERTY_OUTPUT_TRANSMISSION = "outputTransmission";
    private List<TransmissionMode> outputTransmission;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public ProcessSummary() {
    }

    public ProcessSummary title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public ProcessSummary description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public ProcessSummary keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public ProcessSummary addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public ProcessSummary metadata(List<Metadata> metadata) {
        this.metadata = metadata;
        return this;
    }

    public ProcessSummary addMetadataItem(Metadata metadataItem) {
        if (this.metadata == null) {
            this.metadata = new ArrayList<>();
        }
        this.metadata.add(metadataItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Metadata> getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public ProcessSummary id(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(String id) {
        this.id = id;
    }

    public ProcessSummary version(String version) {
        this.version = version;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getVersion() {
        return version;
    }

    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setVersion(String version) {
        this.version = version;
    }

    public ProcessSummary jobControlOptions(List<JobControlOption> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
        return this;
    }

    public ProcessSummary addJobControlOptionsItem(JobControlOption jobControlOptionsItem) {
        if (this.jobControlOptions == null) {
            this.jobControlOptions = new ArrayList<>();
        }
        this.jobControlOptions.add(jobControlOptionsItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_JOB_CONTROL_OPTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<JobControlOption> getJobControlOptions() {
        return jobControlOptions;
    }

    @JsonProperty(JSON_PROPERTY_JOB_CONTROL_OPTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setJobControlOptions(List<JobControlOption> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    public ProcessSummary outputTransmission(List<TransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
        return this;
    }

    public ProcessSummary addOutputTransmissionItem(TransmissionMode outputTransmissionItem) {
        if (this.outputTransmission == null) {
            this.outputTransmission = new ArrayList<>();
        }
        this.outputTransmission.add(outputTransmissionItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_TRANSMISSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<TransmissionMode> getOutputTransmission() {
        return outputTransmission;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_TRANSMISSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setOutputTransmission(List<TransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }

    public ProcessSummary links(List<Link> links) {
        this.links = links;
        return this;
    }

    public ProcessSummary addLinksItem(Link linksItem) {
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
