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
 * An OpenID Connect provider.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Authentication/operation/authenticate-oidc">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        OidcProvider.JSON_PROPERTY_ID,
        OidcProvider.JSON_PROPERTY_ISSUER,
        OidcProvider.JSON_PROPERTY_SCOPES,
        OidcProvider.JSON_PROPERTY_TITLE,
        OidcProvider.JSON_PROPERTY_DESCRIPTION,
        OidcProvider.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "OidcProvider")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "OidcProvider")
public class OidcProvider extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @JacksonXmlProperty(localName = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_ISSUER = "issuer";
    @XmlElement(name = "issuer")
    @JacksonXmlProperty(localName = "issuer")
    @jakarta.annotation.Nonnull
    private String issuer;

    public static final String JSON_PROPERTY_SCOPES = "scopes";
    @XmlElementWrapper(name = "scopes")
    @XmlElement(name = "scope")
    @JacksonXmlElementWrapper(localName = "scopes", useWrapping = false)
    @JacksonXmlProperty(localName = "scope")
    @jakarta.annotation.Nullable
    private List<String> scopes = new ArrayList<>();

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @JacksonXmlProperty(localName = "title")
    @jakarta.annotation.Nonnull
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @JacksonXmlProperty(localName = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public OidcProvider id(String id) { this.id = id; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ID)
    public String getId() { return id; }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ID)
    public void setId(@jakarta.annotation.Nonnull String id) { this.id = id; }

    public OidcProvider issuer(String issuer) { this.issuer = issuer; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ISSUER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ISSUER)
    public String getIssuer() { return issuer; }

    @JsonProperty(JSON_PROPERTY_ISSUER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ISSUER)
    public void setIssuer(@jakarta.annotation.Nonnull String issuer) { this.issuer = issuer; }

    public OidcProvider scopes(List<String> scopes) { this.scopes = scopes; return this; }
    public OidcProvider addScopesItem(String scope) { if (this.scopes == null) this.scopes = new ArrayList<>(); this.scopes.add(scope); return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SCOPES)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SCOPES)
    public List<String> getScopes() { return scopes; }

    @JsonProperty(JSON_PROPERTY_SCOPES)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SCOPES)
    public void setScopes(@jakarta.annotation.Nullable List<String> scopes) { this.scopes = scopes; }

    public OidcProvider title(String title) { this.title = title; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public String getTitle() { return title; }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TITLE)
    public void setTitle(@jakarta.annotation.Nonnull String title) { this.title = title; }

    public OidcProvider description(String description) { this.description = description; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public String getDescription() { return description; }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DESCRIPTION)
    public void setDescription(@jakarta.annotation.Nullable String description) { this.description = description; }

    public OidcProvider links(List<Link> links) { this.links = links; return this; }
    public OidcProvider addLinksItem(Link link) { if (this.links == null) this.links = new ArrayList<>(); this.links.add(link); return this; }

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
        OidcProvider that = (OidcProvider) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.issuer, that.issuer)
                && Objects.equals(this.scopes, that.scopes)
                && Objects.equals(this.title, that.title)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, issuer, scopes, title, description, links);
    }
}
