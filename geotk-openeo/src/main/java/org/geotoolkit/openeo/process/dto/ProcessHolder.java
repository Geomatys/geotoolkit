package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Data-Processing/operation/compute-result">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        ProcessHolder.JSON_PROPERTY_PROCESS,
        ProcessHolder.JSON_PROPERTY_BUDGET,
        ProcessHolder.JSON_PROPERTY_PLAN,
        ProcessHolder.JSON_PROPERTY_LOG_LEVEL
})
@XmlRootElement(name = "ProcessHolder")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ProcessHolder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessHolder extends DataTransferObject {

    public static final String JSON_PROPERTY_PROCESS = "process";
    @XmlElement(name = "process")
    @jakarta.annotation.Nonnull
    private Process process;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget")
    @jakarta.annotation.Nullable
    private Float budget;

    public static final String JSON_PROPERTY_PLAN = "plan";
    @XmlElement(name = "plan")
    @jakarta.annotation.Nullable
    private String plan;

    public static final String JSON_PROPERTY_LOG_LEVEL = "log_level";
    @XmlElement(name = "log_level")
    @jakarta.annotation.Nullable
    private String logLevel;

    /**
     * Get process
     *
     * @return process
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROCESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "process")
    public Process getProcess() {
        return process;
    }

    public Process process() {
        return process;
    }

    public void setProcess(@jakarta.annotation.Nonnull Process process) {
        this.process = process;
    }

    /**
     * Get budget
     *
     * @return budget
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "budget")
    public Float getBudget() {
        return budget;
    }

    public Float budget() {
        return budget;
    }

    public void setBudget(@jakarta.annotation.Nullable Float budget) {
        this.budget = budget;
    }

    /**
     * Get plan
     *
     * @return plan
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PLAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "plan")
    public String getPlan() {
        return plan;
    }

    public String plan() {
        return plan;
    }

    public void setPlan(@jakarta.annotation.Nullable String plan) {
        this.plan = plan;
    }

    /**
     * Get logLevel
     *
     * @return logLevel
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LOG_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "log_level")
    public String getLogLevel() {
        return logLevel;
    }

    public String logLevel() {
        return logLevel;
    }

    public void setLogLevel(@jakarta.annotation.Nullable String logLevel) {
        this.logLevel = logLevel;
    }
}
