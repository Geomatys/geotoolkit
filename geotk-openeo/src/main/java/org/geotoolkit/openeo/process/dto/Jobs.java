package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Data-Processing/operation/list-jobs">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Jobs.JSON_PROPERTY_JOBS,
        Jobs.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "Jobs")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Jobs")
public class Jobs extends DataTransferObject {

    public static final String JSON_PROPERTY_JOBS = "jobs";
    @XmlElementWrapper(name = "jobs")
    @XmlElement(name = "job")
    @JacksonXmlElementWrapper(localName = "jobs", useWrapping = false)
    @JacksonXmlProperty(localName = "job")
    @jakarta.annotation.Nonnull
    private List<Job> jobs = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public Jobs jobs(List<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public Jobs addJobsItem(Job jobsItem) {
        this.jobs.add(jobsItem);
        return this;
    }

    /**
     * Get jobs
     *
     * @return jobs
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_JOBS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_JOBS)
    public List<Job> getJobs() {
        return jobs;
    }

    @JsonProperty(JSON_PROPERTY_JOBS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_JOBS)
    public void setJobs(@jakarta.annotation.Nonnull List<Job> jobs) {
        this.jobs = jobs;
    }

    public Jobs links(List<Link> links) {
        this.links = links;
        return this;
    }

    public Jobs addLinksItem(Link linksItem) {
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Jobs jobs = (Jobs) o;
        return Objects.equals(this.jobs, jobs.jobs) &&
                Objects.equals(this.links, jobs.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobs, links);
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
