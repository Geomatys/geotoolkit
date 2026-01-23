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
package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.openeo.process.dto.deserializer.ProcessDescriptionDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Process-Discovery">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        ProcessDescription.JSON_PROPERTY_PROCESS_ID,
        ProcessDescription.JSON_PROPERTY_TITLE,
        ProcessDescription.JSON_PROPERTY_DESCRIPTION,
        ProcessDescription.JSON_PROPERTY_ARGUMENTS,
        ProcessDescription.JSON_PROPERTY_RETURNS,
        ProcessDescription.JSON_PROPERTY_RESULT
})
@XmlRootElement(name = "ProcessDescription")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ProcessDescription")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = ProcessDescriptionDeserializer.class)
public class ProcessDescription extends DataTransferObject {

    public ProcessDescription() {}

    public ProcessDescription(String processId, String title, String description, Map<String, ProcessDescriptionArgument> arguments, Object returns, Boolean result) {
        this.processId = processId;
        this.title = title;
        this.description = description;
        this.arguments = arguments;
        this.returns = returns;
        this.result = result;
    }

    public static final String JSON_PROPERTY_PROCESS_ID = "process_id";
    @XmlElement(name = "processId")
    @jakarta.annotation.Nonnull
    private String processId;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_ARGUMENTS = "arguments";
    @XmlTransient
    @jakarta.annotation.Nonnull
    private Map<String, ProcessDescriptionArgument> arguments = new HashMap<>();

    public static final String JSON_PROPERTY_RETURNS = "returns";
    @XmlTransient
    @jakarta.annotation.Nullable
    private Object returns = null;

    public static final String JSON_PROPERTY_RESULT = "result";
    @XmlElement(name = "result")
    @jakarta.annotation.Nonnull
    private Boolean result = false;

    /**
     * Get processId
     *
     * @return processId
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROCESS_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "process_id")
    public String getProcessId() {
        return processId;
    }

    @JsonProperty(JSON_PROPERTY_PROCESS_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "process_id")
    public void setProcessId(@jakarta.annotation.Nonnull String processId) {
        this.processId = processId;
    }

    /**
     * Get title
     *
     * @return title
     */
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

    /**
     * Get description
     *
     * @return description
     */
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

    /**
     * Get arguments
     *
     * @return arguments
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ARGUMENTS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Map<String, ProcessDescriptionArgument> getArguments() {
        return arguments;
    }

    @JsonProperty(JSON_PROPERTY_ARGUMENTS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setArguments(@jakarta.annotation.Nonnull Map<String, ProcessDescriptionArgument> arguments) {
        this.arguments = arguments;
    }

    /**
     * Get returns
     *
     * @return returns
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RETURNS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getReturns() {
        return returns;
    }

    @JsonProperty(JSON_PROPERTY_RETURNS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setReturns(@jakarta.annotation.Nullable Object returns) {
        this.returns = returns;
    }

    /**
     * Get result
     *
     * @return result
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_RESULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "result")
    public Boolean getResult() {
        return result;
    }

    @JsonProperty(JSON_PROPERTY_RESULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "result")
    public void setResult(@jakarta.annotation.Nonnull Boolean result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessDescription that = (ProcessDescription) o;
        return Objects.equals(processId, that.processId) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(arguments, that.arguments) && Objects.equals(returns, that.returns) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, title, description, arguments, returns, result);
    }
}
