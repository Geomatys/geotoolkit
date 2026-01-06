package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.atom.xml.Link;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.openeo.process.dto.serializer.JobSerializer;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Batch-Jobs/operation/list-jobs">OpenEO Doc</a>
 */

@JsonPropertyOrder({
        Job.JSON_PROPERTY_ID,
        Job.JSON_PROPERTY_TITLE,
        Job.JSON_PROPERTY_DESCRIPTION,
        Job.JSON_PROPERTY_PROCESS,
        Job.JSON_PROPERTY_STATUS,
        Job.JSON_PROPERTY_PROGRESS,
        Job.JSON_PROPERTY_CREATED,
        Job.JSON_PROPERTY_UPDATED,
        Job.JSON_PROPERTY_PLAN,
        Job.JSON_PROPERTY_COSTS,
        Job.JSON_PROPERTY_BUDGET,
        Job.JSON_PROPERTY_LOG_LEVEL,
        Job.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "Job")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Job")
@JsonSerialize(using = JobSerializer.class)
public class Job extends DataTransferObject {

    public Job() {
    }

    public Job(String id, String title, String description, Process process, Status status, float progress,
               XMLGregorianCalendar created, XMLGregorianCalendar updated, String plan, float costs,
               float budget, String logLevel, List<Link> links) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.process = process;
        this.status = status;
        this.progress = progress;
        this.created = created;
        this.updated = updated;
        this.plan = plan;
        this.costs = costs;
        this.budget = budget;
        this.logLevel = logLevel;
        this.links = links;
    }

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_PROCESS = "process";
    @XmlElement(name = "process")
    @jakarta.annotation.Nonnull
    private Process process;

    public static final String JSON_PROPERTY_STATUS = "status";
    @XmlElement(name = "status")
    @jakarta.annotation.Nonnull
    private Status status;

    public static final String JSON_PROPERTY_PROGRESS = "progress";
    @XmlElement(name = "progress")
    @jakarta.annotation.Nonnull
    private float progress;

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created")
    @jakarta.annotation.Nullable
    private XMLGregorianCalendar created;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    @XmlElement(name = "updated")
    @jakarta.annotation.Nullable
    private XMLGregorianCalendar updated;

    public static final String JSON_PROPERTY_PLAN = "plan";
    @XmlElement(name = "plan")
    @jakarta.annotation.Nullable
    private String plan;

    public static final String JSON_PROPERTY_COSTS = "costs";
    @XmlElement(name = "costs")
    @jakarta.annotation.Nullable
    private float costs;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget")
    @jakarta.annotation.Nullable
    private float budget;

    public static final String JSON_PROPERTY_LOG_LEVEL = "log_level";
    @XmlElement(name = "logLevel")
    @jakarta.annotation.Nullable
    private String logLevel;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links;

    /**
     * Get id
     *
     * @return id
     */
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

    @JsonProperty(JSON_PROPERTY_PROCESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "process")
    public void setProcess(@jakarta.annotation.Nonnull Process process) {
        this.process = process;
    }

    /**
     * Get status
     *
     * @return status
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "status")
    public void setStatus(@jakarta.annotation.Nonnull Status status) {
        this.status = status;
    }

    /**
     * Get progress
     *
     * @return progress
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROGRESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "progress")
    public float getProgress() {
        return progress;
    }

    @JsonProperty(JSON_PROPERTY_PROGRESS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "progress")
    public void setProgress(@jakarta.annotation.Nonnull float progress) {
        this.progress = progress;
    }

    /**
     * Get created
     *
     * @return created
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public void setCreated(@jakarta.annotation.Nullable XMLGregorianCalendar created) {
        this.created = created;
    }

    /**
     * Get updated
     *
     * @return updated
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public XMLGregorianCalendar getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public void setUpdated(@jakarta.annotation.Nullable XMLGregorianCalendar updated) {
        this.updated = updated;
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

    @JsonProperty(JSON_PROPERTY_PLAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "plan")
    public void setPlan(@jakarta.annotation.Nullable String plan) {
        this.plan = plan;
    }

    /**
     * Get costs
     *
     * @return costs
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COSTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "costs")
    public float getCosts() {
        return costs;
    }

    @JsonProperty(JSON_PROPERTY_COSTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "costs")
    public void setCosts(@jakarta.annotation.Nullable float costs) {
        this.costs = costs;
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
    public float getBudget() {
        return budget;
    }

    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "budget")
    public void setBudget(@jakarta.annotation.Nullable float budget) {
        this.budget = budget;
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

    @JsonProperty(JSON_PROPERTY_LOG_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "log_level")
    public void setLogLevel(@jakarta.annotation.Nullable String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Float.compare(progress, job.progress) == 0 && Float.compare(costs, job.costs) == 0 &&
                Float.compare(budget, job.budget) == 0 && Objects.equals(id, job.id) &&
                Objects.equals(title, job.title) && Objects.equals(description, job.description) &&
                Objects.equals(process, job.process) && Objects.equals(status, job.status) &&
                Objects.equals(created, job.created) && Objects.equals(updated, job.updated) &&
                Objects.equals(plan, job.plan) && Objects.equals(logLevel, job.logLevel) &&
                Objects.equals(links, job.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, process, status, progress, created, updated, plan, costs, budget, logLevel, links);
    }
}
