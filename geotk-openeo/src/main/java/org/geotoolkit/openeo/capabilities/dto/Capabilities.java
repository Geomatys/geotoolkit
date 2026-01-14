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
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Capabilities.JSON_PROPERTY_API_VERSION,
        Capabilities.JSON_PROPERTY_BACKEND_VERSION,
        Capabilities.JSON_PROPERTY_STAC_VERSION,
        Capabilities.JSON_PROPERTY_ID,
        Capabilities.JSON_PROPERTY_TITLE,
        Capabilities.JSON_PROPERTY_DESCRIPTION,
        Capabilities.JSON_PROPERTY_CONFORMS_TO,
        Capabilities.JSON_PROPERTY_PRODUCTION,
        Capabilities.JSON_PROPERTY_ENDPOINTS,
        Capabilities.JSON_PROPERTY_BILLING,
        Capabilities.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "Capabilities")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Capabilities")
public class Capabilities extends DataTransferObject {

    public static final String JSON_PROPERTY_API_VERSION = "api_version";
    @XmlElement(name = "api_version")
    @jakarta.annotation.Nonnull
    private String apiVersion;

    public static final String JSON_PROPERTY_BACKEND_VERSION = "backend_version";
    @XmlElement(name = "backend_version")
    @jakarta.annotation.Nullable
    private String backendVersion;

    public static final String JSON_PROPERTY_STAC_VERSION = "stac_version";
    @XmlElement(name = "stac_version")
    @jakarta.annotation.Nullable
    private String stacVersion;

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nonnull
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nonnull
    private String description;

    public static final String JSON_PROPERTY_CONFORMS_TO = "conformsTo";
    // JAXB annotation for lists
    @XmlElementWrapper(name = "conformsTo")
    @XmlElement(name = "conformsToItem")
    // Jackson XML annotation for lists (to avoid extra wrapping element)
    @JacksonXmlElementWrapper(localName = "conformsTo", useWrapping = false)
    @JacksonXmlProperty(localName = "conformsToItem")
    @jakarta.annotation.Nonnull
    private List<String> conformsTo = new ArrayList<>();

    public static final String JSON_PROPERTY_PRODUCTION = "production";
    @XmlElement(name = "production")
    @jakarta.annotation.Nullable
    private Boolean production = false;

    public static final String JSON_PROPERTY_ENDPOINTS = "endpoints";
    // JAXB annotation for lists
    @XmlElementWrapper(name = "endpoints")
    @XmlElement(name = "endpoint")
    // Jackson XML annotation for lists (to avoid extra wrapping element)
    @JacksonXmlElementWrapper(localName = "endpoints", useWrapping = false)
    @JacksonXmlProperty(localName = "endpoint")
    @jakarta.annotation.Nonnull
    private List<Endpoint> endpoints = new ArrayList<>();

    public static final String JSON_PROPERTY_BILLING = "billing";
    @XmlElement(name = "billing")
    @jakarta.annotation.Nullable
    private Billing billing;

    public static final String JSON_PROPERTY_LINKS = "links";
    // JAXB annotation for lists
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    // Jackson XML annotation for lists (to avoid extra wrapping element)
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public Capabilities apiVersion(@jakarta.annotation.Nonnull String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * Get apiVersion
     *
     * @return apiVersion
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_API_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "api_version")
    public String getApiVersion() {
        return apiVersion;
    }

    @JsonProperty(JSON_PROPERTY_API_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "api_version")
    public void setApiVersion(@jakarta.annotation.Nonnull String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Capabilities backendVersion(@jakarta.annotation.Nonnull String backendVersion) {
        this.backendVersion = backendVersion;
        return this;
    }

    /**
     * Get backendVersion
     *
     * @return backendVersion
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_BACKEND_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "backend_version")
    public String getBackendVersion() {
        return backendVersion;
    }

    @JsonProperty(JSON_PROPERTY_BACKEND_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "backend_version")
    public void setBackendVersion(@jakarta.annotation.Nonnull String backendVersion) {
        this.backendVersion = backendVersion;
    }

    public Capabilities stacVersion(@jakarta.annotation.Nullable String stacVersion) {
        this.stacVersion = stacVersion;
        return this;
    }

    /**
     * Get stacVersion
     *
     * @return stacVersion
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "stac_version")
    public String getStacVersion() {
        return stacVersion;
    }

    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "stac_version")
    public void setStacVersion(@jakarta.annotation.Nullable String stacVersion) {
        this.stacVersion = stacVersion;
    }

    public Capabilities id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

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

    public Capabilities title(@jakarta.annotation.Nonnull String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nonnull String title) {
        this.title = title;
    }

    public Capabilities description(@jakarta.annotation.Nonnull String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nonnull String description) {
        this.description = description;
    }

    public Capabilities conformsTo(@jakarta.annotation.Nonnull List<String> conformsTo) {
        this.conformsTo = conformsTo;
        return this;
    }

    public Capabilities addConformsTo(String conformsToItem) {
        this.conformsTo.add(conformsToItem);
        return this;
    }

    /**
     * Get conformsTo
     *
     * @return conformsTo
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CONFORMS_TO)
    public List<String> getConformsTo() {
        return conformsTo;
    }

    @JsonProperty(JSON_PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CONFORMS_TO)
    public void setConformsTo(@jakarta.annotation.Nonnull List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    public Capabilities production(@jakarta.annotation.Nullable Boolean production) {
        this.production = production;
        return this;
    }

    /**
     * Get production
     *
     * @return production
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PRODUCTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "production")
    public Boolean isProduction() {
        return production;
    }

    @JsonProperty(JSON_PROPERTY_PRODUCTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "production")
    public void setProduction(@jakarta.annotation.Nullable Boolean production) {
        this.production = production;
    }

    public Capabilities endpoints(@jakarta.annotation.Nonnull List<Endpoint> endpoints) {
        this.endpoints = endpoints;
        return this;
    }

    public Capabilities addEndpointsItem(Endpoint endpointsItem) {
        this.endpoints.add(endpointsItem);
        return this;
    }

    /**
     * Get endpoints
     *
     * @return endpoints
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ENDPOINTS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ENDPOINTS)
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @JsonProperty(JSON_PROPERTY_ENDPOINTS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ENDPOINTS)
    public void setEndpoints(@jakarta.annotation.Nonnull List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public Capabilities billing(@jakarta.annotation.Nullable Billing billing) {
        this.billing = billing;
        return this;
    }

    /**
     * Get billing
     *
     * @return billing
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BILLING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "billing")
    public Billing getBilling() {
        return billing;
    }

    @JsonProperty(JSON_PROPERTY_BILLING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "billing")
    public void setBilling(@jakarta.annotation.Nullable Billing billing) {
        this.billing = billing;
    }

    public Capabilities links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public Capabilities addLinksItem(Link linksItem) {
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nonnull List<Link> links) {
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
        Capabilities capabilities = (Capabilities) o;
        return Objects.equals(this.apiVersion, capabilities.apiVersion) &&
                Objects.equals(this.backendVersion, capabilities.backendVersion) &&
                Objects.equals(this.stacVersion, capabilities.stacVersion) &&
                Objects.equals(this.id, capabilities.id) &&
                Objects.equals(this.title, capabilities.title) &&
                Objects.equals(this.description, capabilities.description) &&
                Objects.equals(this.production, capabilities.production) &&
                Objects.equals(this.endpoints, capabilities.endpoints) &&
                Objects.equals(this.billing, capabilities.billing) &&
                Objects.equals(this.links, capabilities.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiVersion, backendVersion, stacVersion, id, title, description, production, endpoints, billing, links);
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
