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
import org.geotoolkit.ogcapi.dto.common.Link;

import java.util.List;
import java.util.Objects;

/**
 * Response body for GET /services.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Secondary-Services/operation/list-services">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        ServiceList.JSON_PROPERTY_SERVICES,
        ServiceList.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "ServiceList")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ServiceList")
public class ServiceList extends DataTransferObject {

    public static final String JSON_PROPERTY_SERVICES = "services";
    @XmlElement(name = "services") @JacksonXmlProperty(localName = "services")
    @jakarta.annotation.Nonnull
    private List<Service> services;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links") @JacksonXmlProperty(localName = "links")
    @jakarta.annotation.Nullable
    private List<Link> links;

    public ServiceList services(List<Service> services) { this.services = services; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SERVICES) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_SERVICES)
    public List<Service> getServices() { return services; }

    @JsonProperty(JSON_PROPERTY_SERVICES) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_SERVICES)
    public void setServices(@jakarta.annotation.Nonnull List<Service> services) { this.services = services; }

    public ServiceList links(List<Link> links) { this.links = links; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }

    @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceList that = (ServiceList) o;
        return Objects.equals(this.services, that.services) && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(services, links);
    }
}
