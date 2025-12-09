package org.geotoolkit.openeo.capabilities.dto;

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
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.atom.xml.Link;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.openeo.process.dto.ProcessParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        ServiceType.JSON_PROPERTY_TYPE,
        ServiceType.JSON_PROPERTY_TITLE,
        ServiceType.JSON_PROPERTY_DESCRIPTION,
        ServiceType.JSON_PROPERTY_DEPRECATED,
        ServiceType.JSON_PROPERTY_EXPERIMENTAL,
        ServiceType.JSON_PROPERTY_PROCESS_PARAMETERS,
        ServiceType.JSON_PROPERTY_CONFIGURATION,
        ServiceType.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "ServiceType")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ServiceType")
public class ServiceType extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title = null;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description = null;

    public static final String JSON_PROPERTY_DEPRECATED = "deprecated";
    @XmlElement(name = "deprecated")
    @jakarta.annotation.Nullable
    private Boolean deprecated = false;

    public static final String JSON_PROPERTY_EXPERIMENTAL = "experimental";
    @XmlElement(name = "experimental")
    @jakarta.annotation.Nullable
    private Boolean experimental = false;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    @XmlElement(name = "configuration")
    @XmlTransient
    @jakarta.annotation.Nonnull
    private Map<String, Argument> configuration = new HashMap<>();

    public static final String JSON_PROPERTY_PROCESS_PARAMETERS = "process_parameters";
    @XmlElementWrapper(name = "process_parameter")
    @XmlElement(name = "process_parameters")
    @JacksonXmlElementWrapper(localName = "process_parameters", useWrapping = false)
    @JacksonXmlProperty(localName = "process_parameter")
    @jakarta.annotation.Nonnull
    private List<ProcessParameter> processParameters = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = null;

    public ServiceType() {
    }

    public ServiceType title(String title) {
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

    public ServiceType description(String description) {
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

    public ServiceType configuration(Map<String, Argument> configuration) {
        this.configuration = configuration;
        return this;
    }

    public ServiceType putConfigurationItem(String key, Argument configItem) {
        if (this.configuration == null) {
            this.configuration = new HashMap<>();
        }
        this.configuration.put(key, configItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "configuration")
    public Map<String, Argument> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(@jakarta.annotation.Nullable Map<String, Argument> configuration) {
        this.configuration = configuration;
    }

    public ServiceType processParameters(List<ProcessParameter> processParameters) {
        this.processParameters = processParameters;
        return this;
    }

    public ServiceType addProcessParametersItem(ProcessParameter processParametersItem) {
        if (this.processParameters == null) {
            this.processParameters = new ArrayList<>();
        }
        this.processParameters.add(processParametersItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROCESS_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "process_parameters")
    public List<ProcessParameter> getProcessParameters() {
        return processParameters;
    }

    public void setProcessParameters(@jakarta.annotation.Nullable List<ProcessParameter> processParameters) {
        this.processParameters = processParameters;
    }

    public ServiceType links(List<Link> links) {
        this.links = links;
        return this;
    }

    public ServiceType addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "links")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceType that = (ServiceType) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(configuration, that.configuration) &&
                Objects.equals(processParameters, that.processParameters) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, configuration, processParameters, links);
    }
}
