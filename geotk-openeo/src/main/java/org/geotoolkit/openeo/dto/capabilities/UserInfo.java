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
 * Information about the authenticated user.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Account-Management/operation/describe-account">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        UserInfo.JSON_PROPERTY_USER_ID,
        UserInfo.JSON_PROPERTY_NAME,
        UserInfo.JSON_PROPERTY_DEFAULT_PLAN,
        UserInfo.JSON_PROPERTY_STORAGE,
        UserInfo.JSON_PROPERTY_BUDGET,
        UserInfo.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "UserInfo")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "UserInfo")
public class UserInfo extends DataTransferObject {

    public static final String JSON_PROPERTY_USER_ID = "user_id";
    @XmlElement(name = "user_id")
    @JacksonXmlProperty(localName = "user_id")
    @jakarta.annotation.Nonnull
    private String userId;

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @JacksonXmlProperty(localName = "name")
    @jakarta.annotation.Nullable
    private String name;

    public static final String JSON_PROPERTY_DEFAULT_PLAN = "default_plan";
    @XmlElement(name = "default_plan")
    @JacksonXmlProperty(localName = "default_plan")
    @jakarta.annotation.Nullable
    private String defaultPlan;

    public static final String JSON_PROPERTY_STORAGE = "storage";
    @XmlElement(name = "storage")
    @JacksonXmlProperty(localName = "storage")
    @jakarta.annotation.Nullable
    private Object storage;

    public static final String JSON_PROPERTY_BUDGET = "budget";
    @XmlElement(name = "budget")
    @JacksonXmlProperty(localName = "budget")
    @jakarta.annotation.Nullable
    private Double budget;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public UserInfo userId(String userId) { this.userId = userId; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_USER_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_USER_ID)
    public String getUserId() { return userId; }

    @JsonProperty(JSON_PROPERTY_USER_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_USER_ID)
    public void setUserId(@jakarta.annotation.Nonnull String userId) { this.userId = userId; }

    public UserInfo name(String name) { this.name = name; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_NAME)
    public String getName() { return name; }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_NAME)
    public void setName(@jakarta.annotation.Nullable String name) { this.name = name; }

    public UserInfo defaultPlan(String defaultPlan) { this.defaultPlan = defaultPlan; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEFAULT_PLAN)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DEFAULT_PLAN)
    public String getDefaultPlan() { return defaultPlan; }

    @JsonProperty(JSON_PROPERTY_DEFAULT_PLAN)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DEFAULT_PLAN)
    public void setDefaultPlan(@jakarta.annotation.Nullable String defaultPlan) { this.defaultPlan = defaultPlan; }

    public UserInfo storage(Object storage) { this.storage = storage; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STORAGE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_STORAGE)
    public Object getStorage() { return storage; }

    @JsonProperty(JSON_PROPERTY_STORAGE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_STORAGE)
    public void setStorage(@jakarta.annotation.Nullable Object storage) { this.storage = storage; }

    public UserInfo budget(Double budget) { this.budget = budget; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public Double getBudget() { return budget; }

    @JsonProperty(JSON_PROPERTY_BUDGET)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_BUDGET)
    public void setBudget(@jakarta.annotation.Nullable Double budget) { this.budget = budget; }

    public UserInfo links(List<Link> links) { this.links = links; return this; }
    public UserInfo addLinksItem(Link linksItem) { if (this.links == null) this.links = new ArrayList<>(); this.links.add(linksItem); return this; }

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
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(this.userId, userInfo.userId)
                && Objects.equals(this.name, userInfo.name)
                && Objects.equals(this.defaultPlan, userInfo.defaultPlan)
                && Objects.equals(this.storage, userInfo.storage)
                && Objects.equals(this.budget, userInfo.budget)
                && Objects.equals(this.links, userInfo.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, defaultPlan, storage, budget, links);
    }
}
