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
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        SecondaryWebServices.JSON_PROPERTY_SERVICES
})
@XmlRootElement(name = "SecondaryWebServices")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "SecondaryWebServices")
public class SecondaryWebServices extends DataTransferObject {

    public static final String JSON_PROPERTY_SERVICES = "services";
    @XmlElementWrapper(name = "services")
    @XmlElement(name = "service")
    @JacksonXmlElementWrapper(localName = "services", useWrapping = false)
    @JacksonXmlProperty(localName = "service")
    @jakarta.annotation.Nonnull
    private List<ServiceType> services = new ArrayList<>();

    public SecondaryWebServices services(List<ServiceType> services) {
        this.services = services;
        return this;
    }

    public SecondaryWebServices addServicesItem(ServiceType servicesItem) {
        this.services.add(servicesItem);
        return this;
    }

    /**
     * Get services
     *
     * @return services
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SERVICES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SERVICES)
    public List<ServiceType> getServices() {
        return services;
    }

    @JsonProperty(JSON_PROPERTY_SERVICES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SERVICES)
    public void setServices(@jakarta.annotation.Nonnull List<ServiceType> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecondaryWebServices secondaryWebServices = (SecondaryWebServices) o;
        return Objects.equals(this.services, secondaryWebServices.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(services);
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
