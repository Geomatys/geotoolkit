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
@XmlRootElement(name = "ProcessSummary")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ProcessSummary")
public class ProcessSummary extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private List<String> keywords;

    public static final String JSON_PROPERTY_METADATA = "metadata";
    @XmlElement(name = "metadata")
    @jakarta.annotation.Nullable
    private List<Metadata> metadata;

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_VERSION = "version";
    @XmlElement(name = "version")
    @jakarta.annotation.Nonnull
    private String version;

    public static final String JSON_PROPERTY_JOB_CONTROL_OPTIONS = "jobControlOptions";
    @XmlElement(name = "jobControlOptions")
    @jakarta.annotation.Nullable
    private List<JobControlOption> jobControlOptions;

    public static final String JSON_PROPERTY_OUTPUT_TRANSMISSION = "outputTransmission";
    @XmlElement(name = "outputTransmission")
    @jakarta.annotation.Nullable
    private List<TransmissionMode> outputTransmission;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public ProcessSummary() {
    }

    public ProcessSummary title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    public ProcessSummary description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    public ProcessSummary keywords(@jakarta.annotation.Nullable List<String> keywords) {
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

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setKeywords(@jakarta.annotation.Nullable List<String> keywords) {
        this.keywords = keywords;
    }

    public ProcessSummary metadata(@jakarta.annotation.Nullable List<Metadata> metadata) {
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

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadata")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Metadata> getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadata")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setMetadata(@jakarta.annotation.Nullable List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public ProcessSummary id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

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

    public ProcessSummary version(@jakarta.annotation.Nonnull String version) {
        this.version = version;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "version")
    public String getVersion() {
        return version;
    }

    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "version")
    public void setVersion(@jakarta.annotation.Nonnull String version) {
        this.version = version;
    }

    public ProcessSummary jobControlOptions(@jakarta.annotation.Nullable List<JobControlOption> jobControlOptions) {
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

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_JOB_CONTROL_OPTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "jobControlOptions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<JobControlOption> getJobControlOptions() {
        return jobControlOptions;
    }

    @JsonProperty(JSON_PROPERTY_JOB_CONTROL_OPTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "jobControlOptions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setJobControlOptions(@jakarta.annotation.Nullable List<JobControlOption> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    public ProcessSummary outputTransmission(@jakarta.annotation.Nullable List<TransmissionMode> outputTransmission) {
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

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OUTPUT_TRANSMISSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "outputTransmission")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<TransmissionMode> getOutputTransmission() {
        return outputTransmission;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_TRANSMISSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "outputTransmission")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setOutputTransmission(@jakarta.annotation.Nullable List<TransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }

    public ProcessSummary links(@jakarta.annotation.Nonnull List<Link> links) {
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
}
