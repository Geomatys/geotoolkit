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
package org.geotoolkit.openeo.dto.service;

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
import org.geotoolkit.ogcapi.dto.common.Link;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A secondary web service (e.g. WMS, OGC API) derived from a process graph.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Secondary-Services/operation/describe-service">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Service.JSON_PROPERTY_ID,
        Service.JSON_PROPERTY_TITLE,
        Service.JSON_PROPERTY_DESCRIPTION,
        Service.JSON_PROPERTY_URL,
        Service.JSON_PROPERTY_TYPE,
        Service.JSON_PROPERTY_ENABLED,
        Service.JSON_PROPERTY_PROCESS,
        Service.JSON_PROPERTY_CONFIGURATION,
        Service.JSON_PROPERTY_ATTRIBUTES,
        Service.JSON_PROPERTY_CREATED,
        Service.JSON_PROPERTY_COSTS,
        Service.JSON_PROPERTY_BUDGET,
        Service.JSON_PROPERTY_PLAN,
        Service.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "Service")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Service")
public class Service extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id") @JacksonXmlProperty(localName = "id")
    @jakarta.annotation.Nonnull private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title") @JacksonXmlProperty(localName = "title")
    @jakarta.annotation.Nullable private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description") @JacksonXmlProperty(localName = "description")
    @jakarta.annotation.Nullable private String description;

    public static final String JSON_PROPERTY_URL = "url";
    @XmlElement(name = "url") @JacksonXmlProperty(localName = "url")
    @jakarta.annotation.Nonnull private String url;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type") @JacksonXmlProperty(localName = "type")
    @jakarta.annotation.Nonnull private String type;

    public static final String JSON_PROPERTY_ENABLED = "enabled";
    @XmlElement(name = "enabled") @JacksonXmlProperty(localName = "enabled")
    @jakarta.annotation.Nullable private Boolean enabled;

    public static final String JSON_PROPERTY_PROCESS = "process";
    @XmlElement(name = "process") @JacksonXmlProperty(localName = "process")
    @jakarta.annotation.Nullable private Object process;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    @XmlElement(name = "configuration") @JacksonXmlProperty(localName = "configuration")
    @jakarta.annotation.Nullable private Map<String, Object> configuration;

    public static final String JSON_PROPERTY_ATTRIBUTES = "attributes";
    @XmlElement(name = "attributes") @JacksonXmlProperty(localName = "attributes")
    @jakarta.annotation.Nullable private Map<String, Object> attributes;

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created") @JacksonXmlProperty(localName = "created")
    @jakarta.annotation.Nullable private String created;

    public static final String JSON_PROPERTY_COSTS = "costs";
    @XmlElement(name = "costs") @JacksonXmlProperty(localName = "costs")
    @jakarta.annotation.Nullable private Double costs;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget") @JacksonXmlProperty(localName = "budget")
    @jakarta.annotation.Nullable private Double budget;

    public static final String JSON_PROPERTY_PLAN = "plan";
    @XmlElement(name = "plan") @JacksonXmlProperty(localName = "plan")
    @jakarta.annotation.Nullable private String plan;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links") @JacksonXmlProperty(localName = "links")
    @jakarta.annotation.Nullable private List<Link> links;

    public Service id(String id) { this.id = id; return this; }
    @jakarta.annotation.Nonnull @JsonProperty(JSON_PROPERTY_ID) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_ID)
    public String getId() { return id; }
    @JsonProperty(JSON_PROPERTY_ID) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_ID)
    public void setId(@jakarta.annotation.Nonnull String id) { this.id = id; }

    public Service title(String title) { this.title = title; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_TITLE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public String getTitle() { return title; }
    @JsonProperty(JSON_PROPERTY_TITLE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public void setTitle(@jakarta.annotation.Nullable String title) { this.title = title; }

    public Service description(String description) { this.description = description; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_DESCRIPTION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public String getDescription() { return description; }
    @JsonProperty(JSON_PROPERTY_DESCRIPTION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public void setDescription(@jakarta.annotation.Nullable String description) { this.description = description; }

    public Service url(String url) { this.url = url; return this; }
    @jakarta.annotation.Nonnull @JsonProperty(JSON_PROPERTY_URL) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_URL)
    public String getUrl() { return url; }
    @JsonProperty(JSON_PROPERTY_URL) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_URL)
    public void setUrl(@jakarta.annotation.Nonnull String url) { this.url = url; }

    public Service type(String type) { this.type = type; return this; }
    @jakarta.annotation.Nonnull @JsonProperty(JSON_PROPERTY_TYPE) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public String getType() { return type; }
    @JsonProperty(JSON_PROPERTY_TYPE) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public void setType(@jakarta.annotation.Nonnull String type) { this.type = type; }

    public Service enabled(Boolean enabled) { this.enabled = enabled; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_ENABLED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_ENABLED)
    public Boolean getEnabled() { return enabled; }
    @JsonProperty(JSON_PROPERTY_ENABLED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_ENABLED)
    public void setEnabled(@jakarta.annotation.Nullable Boolean enabled) { this.enabled = enabled; }

    public Service process(Object process) { this.process = process; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_PROCESS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESS)
    public Object getProcess() { return process; }
    @JsonProperty(JSON_PROPERTY_PROCESS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESS)
    public void setProcess(@jakarta.annotation.Nullable Object process) { this.process = process; }

    public Service configuration(Map<String, Object> configuration) { this.configuration = configuration; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_CONFIGURATION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_CONFIGURATION)
    public Map<String, Object> getConfiguration() { return configuration; }
    @JsonProperty(JSON_PROPERTY_CONFIGURATION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_CONFIGURATION)
    public void setConfiguration(@jakarta.annotation.Nullable Map<String, Object> configuration) { this.configuration = configuration; }

    public Service attributes(Map<String, Object> attributes) { this.attributes = attributes; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_ATTRIBUTES) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_ATTRIBUTES)
    public Map<String, Object> getAttributes() { return attributes; }
    @JsonProperty(JSON_PROPERTY_ATTRIBUTES) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_ATTRIBUTES)
    public void setAttributes(@jakarta.annotation.Nullable Map<String, Object> attributes) { this.attributes = attributes; }

    public Service created(String created) { this.created = created; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_CREATED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_CREATED)
    public String getCreated() { return created; }
    @JsonProperty(JSON_PROPERTY_CREATED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_CREATED)
    public void setCreated(@jakarta.annotation.Nullable String created) { this.created = created; }

    public Service costs(Double costs) { this.costs = costs; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_COSTS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_COSTS)
    public Double getCosts() { return costs; }
    @JsonProperty(JSON_PROPERTY_COSTS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_COSTS)
    public void setCosts(@jakarta.annotation.Nullable Double costs) { this.costs = costs; }

    public Service budget(Double budget) { this.budget = budget; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_BUDGET) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public Double getBudget() { return budget; }
    @JsonProperty(JSON_PROPERTY_BUDGET) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public void setBudget(@jakarta.annotation.Nullable Double budget) { this.budget = budget; }

    public Service plan(String plan) { this.plan = plan; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_PLAN) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PLAN)
    public String getPlan() { return plan; }
    @JsonProperty(JSON_PROPERTY_PLAN) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PLAN)
    public void setPlan(@jakarta.annotation.Nullable String plan) { this.plan = plan; }

    public Service links(List<Link> links) { this.links = links; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }
    @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service that = (Service) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
