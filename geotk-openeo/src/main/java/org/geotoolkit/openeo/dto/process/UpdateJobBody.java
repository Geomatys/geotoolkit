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
 * Request body for updating a batch job (PATCH /jobs/{jobId}).
 * All fields are optional.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Batch-Jobs/operation/update-job">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        UpdateJobBody.JSON_PROPERTY_TITLE,
        UpdateJobBody.JSON_PROPERTY_DESCRIPTION,
        UpdateJobBody.JSON_PROPERTY_PROCESS,
        UpdateJobBody.JSON_PROPERTY_PLAN,
        UpdateJobBody.JSON_PROPERTY_BUDGET,
        UpdateJobBody.JSON_PROPERTY_LOG_LEVEL
})
@XmlRootElement(name = "UpdateJobBody")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "UpdateJobBody")
public class UpdateJobBody extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @JacksonXmlProperty(localName = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @JacksonXmlProperty(localName = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_PROCESS = "process";
    @XmlElement(name = "process")
    @JacksonXmlProperty(localName = "process")
    @jakarta.annotation.Nullable
    private Object process;

    public static final String JSON_PROPERTY_PLAN = "plan";
    @XmlElement(name = "plan")
    @JacksonXmlProperty(localName = "plan")
    @jakarta.annotation.Nullable
    private String plan;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget")
    @JacksonXmlProperty(localName = "budget")
    @jakarta.annotation.Nullable
    private Double budget;

    public static final String JSON_PROPERTY_LOG_LEVEL = "log_level";
    @XmlElement(name = "log_level")
    @JacksonXmlProperty(localName = "log_level")
    @jakarta.annotation.Nullable
    private String logLevel;

    public UpdateJobBody title(String title) { this.title = title; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public String getTitle() { return title; }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public void setTitle(@jakarta.annotation.Nullable String title) { this.title = title; }

    public UpdateJobBody description(String description) { this.description = description; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public String getDescription() { return description; }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public void setDescription(@jakarta.annotation.Nullable String description) { this.description = description; }

    public UpdateJobBody process(Object process) { this.process = process; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROCESS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESS)
    public Object getProcess() { return process; }

    @JsonProperty(JSON_PROPERTY_PROCESS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESS)
    public void setProcess(@jakarta.annotation.Nullable Object process) { this.process = process; }

    public UpdateJobBody plan(String plan) { this.plan = plan; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PLAN)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PLAN)
    public String getPlan() { return plan; }

    @JsonProperty(JSON_PROPERTY_PLAN)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PLAN)
    public void setPlan(@jakarta.annotation.Nullable String plan) { this.plan = plan; }

    public UpdateJobBody budget(Double budget) { this.budget = budget; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public Double getBudget() { return budget; }

    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public void setBudget(@jakarta.annotation.Nullable Double budget) { this.budget = budget; }

    public UpdateJobBody logLevel(String logLevel) { this.logLevel = logLevel; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LOG_LEVEL)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LOG_LEVEL)
    public String getLogLevel() { return logLevel; }

    @JsonProperty(JSON_PROPERTY_LOG_LEVEL)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LOG_LEVEL)
    public void setLogLevel(@jakarta.annotation.Nullable String logLevel) { this.logLevel = logLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateJobBody that = (UpdateJobBody) o;
        return Objects.equals(this.title, that.title)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.process, that.process)
                && Objects.equals(this.plan, that.plan)
                && Objects.equals(this.budget, that.budget)
                && Objects.equals(this.logLevel, that.logLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, process, plan, budget, logLevel);
    }
}
