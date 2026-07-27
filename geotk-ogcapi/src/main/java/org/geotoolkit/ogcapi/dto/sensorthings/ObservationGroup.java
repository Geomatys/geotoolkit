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
public final class ObservationGroup extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_CREATION_TIME = "creationTime";
    private String creationTime;

    public static final String JSON_PROPERTY_PURPOSE = "purpose";
    private String purpose;

    public static final String JSON_PROPERTY_END_TIME = "endTime";
    private String endTime;

    public static final String JSON_PROPERTY_TERMS_OF_USE = "termsOfUse";
    private String termsOfUse;

    public static final String JSON_PROPERTY_PRIVACY_POLICY = "privacyPolicy";
    private String privacyPolicy;

    public static final String JSON_PROPERTY_DATA_QUALITY = "dataQuality";
    private Object dataQuality;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private Map<String, Object> properties = new HashMap<>();

    public ObservationGroup() {
    }

    public ObservationGroup name(String name) {
        this.name = name;
        return this;
    }

    /**
     * A property provided by the user. (required)
     *
     * @return the name
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    public ObservationGroup description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A property provided by the user. (required)
     *
     * @return the description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public ObservationGroup creationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this group was created. (required)
     *
     * @return the creationTime
     */
    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCreationTime() {
        return creationTime;
    }

    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public ObservationGroup purpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    /**
     * The purpose of this group, e.g. the campaign or study it belongs to. (optional)
     *
     * @return the purpose
     */
    @JsonProperty(JSON_PROPERTY_PURPOSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPurpose() {
        return purpose;
    }

    @JsonProperty(JSON_PROPERTY_PURPOSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public ObservationGroup endTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this group's data collection ended. (optional)
     *
     * @return the endTime
     */
    @JsonProperty(JSON_PROPERTY_END_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getEndTime() {
        return endTime;
    }

    @JsonProperty(JSON_PROPERTY_END_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ObservationGroup termsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
        return this;
    }

    /**
     * The terms of use applying to this group's data. (optional)
     *
     * @return the termsOfUse
     */
    @JsonProperty(JSON_PROPERTY_TERMS_OF_USE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTermsOfUse() {
        return termsOfUse;
    }

    @JsonProperty(JSON_PROPERTY_TERMS_OF_USE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTermsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public ObservationGroup privacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
        return this;
    }

    /**
     * The privacy policy applying to this group's data. (optional)
     *
     * @return the privacyPolicy
     */
    @JsonProperty(JSON_PROPERTY_PRIVACY_POLICY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    @JsonProperty(JSON_PROPERTY_PRIVACY_POLICY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public ObservationGroup dataQuality(Object dataQuality) {
        this.dataQuality = dataQuality;
        return this;
    }

    /**
     * A JSON object or string describing the quality of this group's data. (optional)
     *
     * @return the dataQuality
     */
    @JsonProperty(JSON_PROPERTY_DATA_QUALITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getDataQuality() {
        return dataQuality;
    }

    @JsonProperty(JSON_PROPERTY_DATA_QUALITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDataQuality(Object dataQuality) {
        this.dataQuality = dataQuality;
    }

    public ObservationGroup properties(Map<String, Object> properties) {
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
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setProperties(Map<String, Object> properties) {
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
