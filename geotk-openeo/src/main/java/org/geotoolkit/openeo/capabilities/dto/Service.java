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
package org.geotoolkit.openeo.capabilities.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Request body for creating a new Secondary Web Service via POST /services.
 * Based on: <a href="https://api.openeo.org/#tag/Secondary-Services/operation/create-service">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Service.JSON_PROPERTY_PROCESS,
        Service.JSON_PROPERTY_TYPE,
        Service.JSON_PROPERTY_TITLE,
        Service.JSON_PROPERTY_DESCRIPTION,
        Service.JSON_PROPERTY_CONFIGURATION,
        Service.JSON_PROPERTY_PLAN,
        Service.JSON_PROPERTY_BUDGET,
        Service.JSON_PROPERTY_ENABLED
})
@XmlRootElement(name = "Service")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Service")
public class Service extends DataTransferObject {

    public static final String JSON_PROPERTY_PROCESS = "process";
    @XmlElement(name = "process")
    private Process process;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    private String type;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    @XmlElement(name = "configuration")
    @XmlTransient
    @jakarta.annotation.Nullable
    private Map<String, Object> configuration;

    public static final String JSON_PROPERTY_PLAN = "plan";
    @XmlElement(name = "plan")
    @jakarta.annotation.Nullable
    private String plan;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget")
    @jakarta.annotation.Nullable
    private BigDecimal budget;

    public static final String JSON_PROPERTY_ENABLED = "enabled";
    @XmlElement(name = "enabled")
    @jakarta.annotation.Nullable
    private Boolean enabled = true;

    public Service() {
        this.configuration = new HashMap<>();
    }

    public Service process(Process process) {
        this.process = process;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROCESS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "process")
    public Process getProcess() {
        return process;
    }

    public void setProcess(@jakarta.annotation.Nonnull Process process) {
        this.process = process;
    }

    public Service type(String type) {
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

    public void setType(@jakarta.annotation.Nonnull String type) {
        this.type = type;
    }

    public Service title(String title) {
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

    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    public Service description(String description) {
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

    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    public Service configuration(Map<String, Object> configuration) {
        this.configuration = configuration;
        return this;
    }

    public Service putConfigurationItem(String key, Object value) {
        if (this.configuration == null) {
            this.configuration = new HashMap<>();
        }
        this.configuration.put(key, value);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "configuration")
    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(@jakarta.annotation.Nullable Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public Service plan(String plan) {
        this.plan = plan;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PLAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "plan")
    public String getPlan() {
        return plan;
    }

    public void setPlan(@jakarta.annotation.Nullable String plan) {
        this.plan = plan;
    }

    public Service budget(BigDecimal budget) {
        this.budget = budget;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "budget")
    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(@jakarta.annotation.Nullable BigDecimal budget) {
        this.budget = budget;
    }

    public Service enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ENABLED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(@jakarta.annotation.Nullable Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service that = (Service) o;
        return Objects.equals(process, that.process) &&
                Objects.equals(type, that.type) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(configuration, that.configuration) &&
                Objects.equals(plan, that.plan) &&
                Objects.equals(budget, that.budget) &&
                Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(process, type, title, description, configuration, plan, budget, enabled);
    }
}
