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
package org.geotoolkit.openeo.dto.capabilities;

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
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * List of OpenID Connect providers supported by the backend.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Authentication/operation/authenticate-oidc">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        OidcProviders.JSON_PROPERTY_PROVIDERS,
        OidcProviders.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "OidcProviders")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "OidcProviders")
public class OidcProviders extends DataTransferObject {

    public static final String JSON_PROPERTY_PROVIDERS = "providers";
    @XmlElementWrapper(name = "providers")
    @XmlElement(name = "provider")
    @JacksonXmlElementWrapper(localName = "providers", useWrapping = false)
    @JacksonXmlProperty(localName = "provider")
    @jakarta.annotation.Nonnull
    private List<OidcProvider> providers = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public OidcProviders providers(List<OidcProvider> providers) { this.providers = providers; return this; }
    public OidcProviders addProvidersItem(OidcProvider provider) { this.providers.add(provider); return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROVIDERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROVIDERS)
    public List<OidcProvider> getProviders() { return providers; }

    @JsonProperty(JSON_PROPERTY_PROVIDERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROVIDERS)
    public void setProviders(@jakarta.annotation.Nonnull List<OidcProvider> providers) { this.providers = providers; }

    public OidcProviders links(List<Link> links) { this.links = links; return this; }
    public OidcProviders addLinksItem(Link link) { if (this.links == null) this.links = new ArrayList<>(); this.links.add(link); return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OidcProviders that = (OidcProviders) o;
        return Objects.equals(this.providers, that.providers) && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() { return Objects.hash(providers, links); }
}
