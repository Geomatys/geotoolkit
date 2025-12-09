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
 * Based on : <a href="https://api.openeo.org/#tag/Process-Discovery">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Processes.JSON_PROPERTY_PROCESSES,
        Processes.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "Processes")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Processes")
public class Processes extends DataTransferObject {

    public static final String JSON_PROPERTY_PROCESSES = "processes";
    @XmlElementWrapper(name = "processes")
    @XmlElement(name = "process")
    @JacksonXmlElementWrapper(localName = "processes", useWrapping = false)
    @JacksonXmlProperty(localName = "process")
    @jakarta.annotation.Nonnull
    private List<Process> processes = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public Processes processes(List<Process> processes) {
        this.processes = processes;
        return this;
    }

    public Processes addProcessesItem(Process processesItem) {
        this.processes.add(processesItem);
        return this;
    }

    /**
     * Get processes
     *
     * @return processes
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROCESSES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESSES)
    public List<Process> getProcesses() {
        return processes;
    }

    @JsonProperty(JSON_PROPERTY_PROCESSES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROCESSES)
    public void setProcesses(@jakarta.annotation.Nonnull List<Process> processes) {
        this.processes = processes;
    }

    public Processes links(List<Link> links) {
        this.links = links;
        return this;
    }

    public Processes addLinksItem(Link linksItem) {
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
        Processes processes = (Processes) o;
        return Objects.equals(this.processes, processes.processes) &&
                Objects.equals(this.links, processes.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processes, links);
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
