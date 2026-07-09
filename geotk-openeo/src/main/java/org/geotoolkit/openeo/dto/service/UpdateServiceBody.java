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

import java.util.Map;
import java.util.Objects;

/**
 * Request body for PATCH /services/{serviceId}.
 *
 * <p>All fields are optional; only provided fields are modified.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Secondary-Services/operation/update-service">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        UpdateServiceBody.JSON_PROPERTY_TITLE,
        UpdateServiceBody.JSON_PROPERTY_DESCRIPTION,
        UpdateServiceBody.JSON_PROPERTY_TYPE,
        UpdateServiceBody.JSON_PROPERTY_ENABLED,
        UpdateServiceBody.JSON_PROPERTY_PROCESS,
        UpdateServiceBody.JSON_PROPERTY_CONFIGURATION,
        UpdateServiceBody.JSON_PROPERTY_PLAN,
        UpdateServiceBody.JSON_PROPERTY_BUDGET
})
@XmlRootElement(name = "UpdateServiceBody")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "UpdateServiceBody")
public class UpdateServiceBody extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title") @JacksonXmlProperty(localName = "title")
    @jakarta.annotation.Nullable private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description") @JacksonXmlProperty(localName = "description")
    @jakarta.annotation.Nullable private String description;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type") @JacksonXmlProperty(localName = "type")
    @jakarta.annotation.Nullable private String type;

    public static final String JSON_PROPERTY_ENABLED = "enabled";
    @XmlElement(name = "enabled") @JacksonXmlProperty(localName = "enabled")
    @jakarta.annotation.Nullable private Boolean enabled;

    public static final String JSON_PROPERTY_PROCESS = "process";
    @XmlElement(name = "process") @JacksonXmlProperty(localName = "process")
    @jakarta.annotation.Nullable private Object process;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    @XmlElement(name = "configuration") @JacksonXmlProperty(localName = "configuration")
    @jakarta.annotation.Nullable private Map<String, Object> configuration;

    public static final String JSON_PROPERTY_PLAN = "plan";
    @XmlElement(name = "plan") @JacksonXmlProperty(localName = "plan")
    @jakarta.annotation.Nullable private String plan;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget") @JacksonXmlProperty(localName = "budget")
    @jakarta.annotation.Nullable private Double budget;

    public UpdateServiceBody title(String title) { this.title = title; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_TITLE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public String getTitle() { return title; }
    @JsonProperty(JSON_PROPERTY_TITLE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public void setTitle(@jakarta.annotation.Nullable String title) { this.title = title; }

    public UpdateServiceBody description(String description) { this.description = description; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_DESCRIPTION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public String getDescription() { return description; }
    @JsonProperty(JSON_PROPERTY_DESCRIPTION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public void setDescription(@jakarta.annotation.Nullable String description) { this.description = description; }

    public UpdateServiceBody type(String type) { this.type = type; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_TYPE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public String getType() { return type; }
    @JsonProperty(JSON_PROPERTY_TYPE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public void setType(@jakarta.annotation.Nullable String type) { this.type = type; }

    public UpdateServiceBody enabled(Boolean enabled) { this.enabled = enabled; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_ENABLED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_ENABLED)
    public Boolean getEnabled() { return enabled; }
    @JsonProperty(JSON_PROPERTY_ENABLED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_ENABLED)
    public void setEnabled(@jakarta.annotation.Nullable Boolean enabled) { this.enabled = enabled; }

    public UpdateServiceBody process(Object process) { this.process = process; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_PROCESS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESS)
    public Object getProcess() { return process; }
    @JsonProperty(JSON_PROPERTY_PROCESS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESS)
    public void setProcess(@jakarta.annotation.Nullable Object process) { this.process = process; }

    public UpdateServiceBody configuration(Map<String, Object> configuration) { this.configuration = configuration; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_CONFIGURATION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_CONFIGURATION)
    public Map<String, Object> getConfiguration() { return configuration; }
    @JsonProperty(JSON_PROPERTY_CONFIGURATION) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_CONFIGURATION)
    public void setConfiguration(@jakarta.annotation.Nullable Map<String, Object> configuration) { this.configuration = configuration; }

    public UpdateServiceBody plan(String plan) { this.plan = plan; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_PLAN) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PLAN)
    public String getPlan() { return plan; }
    @JsonProperty(JSON_PROPERTY_PLAN) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_PLAN)
    public void setPlan(@jakarta.annotation.Nullable String plan) { this.plan = plan; }

    public UpdateServiceBody budget(Double budget) { this.budget = budget; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_BUDGET) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public Double getBudget() { return budget; }
    @JsonProperty(JSON_PROPERTY_BUDGET) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public void setBudget(@jakarta.annotation.Nullable Double budget) { this.budget = budget; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateServiceBody that = (UpdateServiceBody) o;
        return Objects.equals(this.title, that.title) && Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, type);
    }
}
