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
import java.util.Objects;

/**
 * A citizen-science campaign that ObservationGroups can be associated with.
 *
 * <p>STAplus extension.</p>
 *
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html#campaign
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Campaign.JSON_PROPERTY_NAME,
    Campaign.JSON_PROPERTY_DESCRIPTION,
    Campaign.JSON_PROPERTY_TERMS_OF_USE,
    Campaign.JSON_PROPERTY_CREATION_TIME,
    Campaign.JSON_PROPERTY_CLASSIFICATION,
    Campaign.JSON_PROPERTY_PRIVACY_POLICY,
    Campaign.JSON_PROPERTY_URL,
    Campaign.JSON_PROPERTY_START_TIME,
    Campaign.JSON_PROPERTY_END_TIME
})
public final class Campaign extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_TERMS_OF_USE = "termsOfUse";
    private String termsOfUse;

    public static final String JSON_PROPERTY_CREATION_TIME = "creationTime";
    private String creationTime;

    public static final String JSON_PROPERTY_CLASSIFICATION = "classification";
    private String classification;

    public static final String JSON_PROPERTY_PRIVACY_POLICY = "privacyPolicy";
    private String privacyPolicy;

    public static final String JSON_PROPERTY_URL = "url";
    private String url;

    public static final String JSON_PROPERTY_START_TIME = "startTime";
    private String startTime;

    public static final String JSON_PROPERTY_END_TIME = "endTime";
    private String endTime;

    public Campaign() {
    }

    public Campaign name(String name) {
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

    public Campaign description(String description) {
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

    public Campaign termsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
        return this;
    }

    /**
     * The terms of use applying to this campaign's data. (required)
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

    public Campaign creationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this campaign was created. (required)
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

    public Campaign classification(String classification) {
        this.classification = classification;
        return this;
    }

    /**
     * A free-text classification of the campaign. (optional)
     *
     * @return the classification
     */
    @JsonProperty(JSON_PROPERTY_CLASSIFICATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getClassification() {
        return classification;
    }

    @JsonProperty(JSON_PROPERTY_CLASSIFICATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Campaign privacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
        return this;
    }

    /**
     * The privacy policy applying to this campaign's data. (optional)
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

    public Campaign url(String url) {
        this.url = url;
        return this;
    }

    /**
     * A URL with more information about the campaign. (optional)
     *
     * @return the url
     */
    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getUrl() {
        return url;
    }

    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUrl(String url) {
        this.url = url;
    }

    public Campaign startTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * The ISO 8601 date-time the campaign started. (optional)
     *
     * @return the startTime
     */
    @JsonProperty(JSON_PROPERTY_START_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getStartTime() {
        return startTime;
    }

    @JsonProperty(JSON_PROPERTY_START_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Campaign endTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * The ISO 8601 date-time the campaign ended. (optional)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Campaign other = (Campaign) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.termsOfUse, other.termsOfUse)
                && Objects.equals(this.creationTime, other.creationTime)
                && Objects.equals(this.classification, other.classification)
                && Objects.equals(this.privacyPolicy, other.privacyPolicy)
                && Objects.equals(this.url, other.url)
                && Objects.equals(this.startTime, other.startTime)
                && Objects.equals(this.endTime, other.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, termsOfUse, creationTime,
                classification, privacyPolicy, url, startTime, endTime);
    }
}
