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
package org.geotoolkit.ogcapi.dto.sensorthings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A user-defined grouping of Observations, e.g. for a citizen-science campaign submission.
 *
 * <p>STAplus extension.</p>
 *
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html#observationgroup
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    ObservationGroup.JSON_PROPERTY_NAME,
    ObservationGroup.JSON_PROPERTY_DESCRIPTION,
    ObservationGroup.JSON_PROPERTY_CREATION_TIME,
    ObservationGroup.JSON_PROPERTY_PURPOSE,
    ObservationGroup.JSON_PROPERTY_END_TIME,
    ObservationGroup.JSON_PROPERTY_TERMS_OF_USE,
    ObservationGroup.JSON_PROPERTY_PRIVACY_POLICY,
    ObservationGroup.JSON_PROPERTY_DATA_QUALITY,
    ObservationGroup.JSON_PROPERTY_PROPERTIES
})
@XmlRootElement(name = "ObservationGroup")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ObservationGroup")
public final class ObservationGroup extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nonnull
    private String description;

    public static final String JSON_PROPERTY_CREATION_TIME = "creationTime";
    @XmlElement(name = "creationTime")
    @jakarta.annotation.Nonnull
    private String creationTime;

    public static final String JSON_PROPERTY_PURPOSE = "purpose";
    @XmlElement(name = "purpose")
    @jakarta.annotation.Nullable
    private String purpose;

    public static final String JSON_PROPERTY_END_TIME = "endTime";
    @XmlElement(name = "endTime")
    @jakarta.annotation.Nullable
    private String endTime;

    public static final String JSON_PROPERTY_TERMS_OF_USE = "termsOfUse";
    @XmlElement(name = "termsOfUse")
    @jakarta.annotation.Nullable
    private String termsOfUse;

    public static final String JSON_PROPERTY_PRIVACY_POLICY = "privacyPolicy";
    @XmlElement(name = "privacyPolicy")
    @jakarta.annotation.Nullable
    private String privacyPolicy;

    public static final String JSON_PROPERTY_DATA_QUALITY = "dataQuality";
    @XmlElement(name = "dataQuality")
    @jakarta.annotation.Nullable
    private Object dataQuality;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nullable
    private Map<String, Object> properties = new HashMap<>();

    public ObservationGroup() {
    }

    public ObservationGroup name(@jakarta.annotation.Nonnull String name) {
        this.name = name;
        return this;
    }

    /**
     * A property provided by the user. (required)
     *
     * @return the name
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nonnull String name) {
        this.name = name;
    }

    public ObservationGroup description(@jakarta.annotation.Nonnull String description) {
        this.description = description;
        return this;
    }

    /**
     * A property provided by the user. (required)
     *
     * @return the description
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nonnull String description) {
        this.description = description;
    }

    public ObservationGroup creationTime(@jakarta.annotation.Nonnull String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this group was created. (required)
     *
     * @return the creationTime
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "creationTime")
    public String getCreationTime() {
        return creationTime;
    }

    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "creationTime")
    public void setCreationTime(@jakarta.annotation.Nonnull String creationTime) {
        this.creationTime = creationTime;
    }

    public ObservationGroup purpose(@jakarta.annotation.Nullable String purpose) {
        this.purpose = purpose;
        return this;
    }

    /**
     * The purpose of this group, e.g. the campaign or study it belongs to. (optional)
     *
     * @return the purpose
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PURPOSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "purpose")
    public String getPurpose() {
        return purpose;
    }

    @JsonProperty(JSON_PROPERTY_PURPOSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "purpose")
    public void setPurpose(@jakarta.annotation.Nullable String purpose) {
        this.purpose = purpose;
    }

    public ObservationGroup endTime(@jakarta.annotation.Nullable String endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this group's data collection ended. (optional)
     *
     * @return the endTime
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_END_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "endTime")
    public String getEndTime() {
        return endTime;
    }

    @JsonProperty(JSON_PROPERTY_END_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "endTime")
    public void setEndTime(@jakarta.annotation.Nullable String endTime) {
        this.endTime = endTime;
    }

    public ObservationGroup termsOfUse(@jakarta.annotation.Nullable String termsOfUse) {
        this.termsOfUse = termsOfUse;
        return this;
    }

    /**
     * The terms of use applying to this group's data. (optional)
     *
     * @return the termsOfUse
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TERMS_OF_USE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "termsOfUse")
    public String getTermsOfUse() {
        return termsOfUse;
    }

    @JsonProperty(JSON_PROPERTY_TERMS_OF_USE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "termsOfUse")
    public void setTermsOfUse(@jakarta.annotation.Nullable String termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public ObservationGroup privacyPolicy(@jakarta.annotation.Nullable String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
        return this;
    }

    /**
     * The privacy policy applying to this group's data. (optional)
     *
     * @return the privacyPolicy
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PRIVACY_POLICY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "privacyPolicy")
    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    @JsonProperty(JSON_PROPERTY_PRIVACY_POLICY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "privacyPolicy")
    public void setPrivacyPolicy(@jakarta.annotation.Nullable String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public ObservationGroup dataQuality(@jakarta.annotation.Nullable Object dataQuality) {
        this.dataQuality = dataQuality;
        return this;
    }

    /**
     * A JSON object or string describing the quality of this group's data. (optional)
     *
     * @return the dataQuality
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DATA_QUALITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dataQuality")
    public Object getDataQuality() {
        return dataQuality;
    }

    @JsonProperty(JSON_PROPERTY_DATA_QUALITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dataQuality")
    public void setDataQuality(@jakarta.annotation.Nullable Object dataQuality) {
        this.dataQuality = dataQuality;
    }

    public ObservationGroup properties(@jakarta.annotation.Nullable Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public ObservationGroup putPropertiesItem(String key, Object propertiesItem) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, propertiesItem);
        return this;
    }

    /**
     * A JSON object containing user-annotated properties. (optional)
     *
     * @return the properties
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "properties")
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "properties")
    public void setProperties(@jakarta.annotation.Nullable Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObservationGroup other = (ObservationGroup) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.creationTime, other.creationTime)
                && Objects.equals(this.purpose, other.purpose)
                && Objects.equals(this.endTime, other.endTime)
                && Objects.equals(this.termsOfUse, other.termsOfUse)
                && Objects.equals(this.privacyPolicy, other.privacyPolicy)
                && Objects.equals(this.dataQuality, other.dataQuality)
                && Objects.equals(this.properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, creationTime, purpose, endTime,
                termsOfUse, privacyPolicy, dataQuality, properties);
    }
}
