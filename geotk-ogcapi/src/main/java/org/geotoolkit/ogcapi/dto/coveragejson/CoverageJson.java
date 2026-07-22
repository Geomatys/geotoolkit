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
package org.geotoolkit.ogcapi.dto.coveragejson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A CoverageJSON document, the default response body of every EDR data query. Models the
 * {@code Coverage}, {@code CoverageCollection} and {@code Domain}-only top-level variants as
 * one flattened class, distinguished at runtime by {@link #getType()}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    CoverageJson.JSON_PROPERTY_TYPE,
    CoverageJson.JSON_PROPERTY_DOMAIN_TYPE,
    CoverageJson.JSON_PROPERTY_DOMAIN,
    CoverageJson.JSON_PROPERTY_COVERAGES,
    CoverageJson.JSON_PROPERTY_PARAMETERS,
    CoverageJson.JSON_PROPERTY_RANGES,
    CoverageJson.JSON_PROPERTY_REFERENCING
})
@XmlRootElement(name = "CoverageJson")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "CoverageJson")
public final class CoverageJson extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_DOMAIN_TYPE = "domainType";
    @XmlElement(name = "domainType")
    @jakarta.annotation.Nullable
    private String domainType;

    public static final String JSON_PROPERTY_DOMAIN = "domain";
    @XmlElement(name = "domain")
    @jakarta.annotation.Nullable
    private Domain domain;

    public static final String JSON_PROPERTY_COVERAGES = "coverages";
    @XmlElement(name = "coverages")
    @jakarta.annotation.Nullable
    private List<CoverageJson> coverages;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    @XmlElement(name = "parameters")
    @jakarta.annotation.Nullable
    private Map<String, Parameter> parameters;

    public static final String JSON_PROPERTY_RANGES = "ranges";
    @XmlElement(name = "ranges")
    @jakarta.annotation.Nullable
    private Map<String, NdArray> ranges;

    public static final String JSON_PROPERTY_REFERENCING = "referencing";
    @XmlElement(name = "referencing")
    @jakarta.annotation.Nullable
    private List<ReferenceSystemConnection> referencing;

    public CoverageJson() {
    }

    public CoverageJson type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * {@code "Coverage"}, {@code "CoverageCollection"} or {@code "Domain"}.
     *
     * @return type
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable String type) {
        this.type = type;
    }

    public CoverageJson domainType(@jakarta.annotation.Nullable String domainType) {
        this.domainType = domainType;
        return this;
    }

    /**
     * Only set when {@link #getType()} is {@code "CoverageCollection"}, as a hint for the
     * {@link #getDomainType()} shared by all {@link #getCoverages()}.
     *
     * @return domainType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "domainType")
    public String getDomainType() {
        return domainType;
    }

    @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "domainType")
    public void setDomainType(@jakarta.annotation.Nullable String domainType) {
        this.domainType = domainType;
    }

    public CoverageJson domain(@jakarta.annotation.Nullable Domain domain) {
        this.domain = domain;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DOMAIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "domain")
    public Domain getDomain() {
        return domain;
    }

    @JsonProperty(JSON_PROPERTY_DOMAIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "domain")
    public void setDomain(@jakarta.annotation.Nullable Domain domain) {
        this.domain = domain;
    }

    public CoverageJson coverages(@jakarta.annotation.Nullable List<CoverageJson> coverages) {
        this.coverages = coverages;
        return this;
    }

    public CoverageJson addCoveragesItem(CoverageJson coveragesItem) {
        if (this.coverages == null) {
            this.coverages = new java.util.ArrayList<>();
        }
        this.coverages.add(coveragesItem);
        return this;
    }

    /**
     * Only set when {@link #getType()} is {@code "CoverageCollection"}.
     *
     * @return coverages
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COVERAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "coverages")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<CoverageJson> getCoverages() {
        return coverages;
    }

    @JsonProperty(JSON_PROPERTY_COVERAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "coverages")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoverages(@jakarta.annotation.Nullable List<CoverageJson> coverages) {
        this.coverages = coverages;
    }

    public CoverageJson parameters(@jakarta.annotation.Nullable Map<String, Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public CoverageJson putParametersItem(String key, Parameter parametersItem) {
        if (this.parameters == null) {
            this.parameters = new java.util.LinkedHashMap<>();
        }
        this.parameters.put(key, parametersItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "parameters")
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "parameters")
    public void setParameters(@jakarta.annotation.Nullable Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public CoverageJson ranges(@jakarta.annotation.Nullable Map<String, NdArray> ranges) {
        this.ranges = ranges;
        return this;
    }

    public CoverageJson putRangesItem(String key, NdArray rangesItem) {
        if (this.ranges == null) {
            this.ranges = new java.util.LinkedHashMap<>();
        }
        this.ranges.put(key, rangesItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RANGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "ranges")
    public Map<String, NdArray> getRanges() {
        return ranges;
    }

    @JsonProperty(JSON_PROPERTY_RANGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "ranges")
    public void setRanges(@jakarta.annotation.Nullable Map<String, NdArray> ranges) {
        this.ranges = ranges;
    }

    public CoverageJson referencing(@jakarta.annotation.Nullable List<ReferenceSystemConnection> referencing) {
        this.referencing = referencing;
        return this;
    }

    public CoverageJson addReferencingItem(ReferenceSystemConnection referencingItem) {
        if (this.referencing == null) {
            this.referencing = new java.util.ArrayList<>();
        }
        this.referencing.add(referencingItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REFERENCING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "referencing")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ReferenceSystemConnection> getReferencing() {
        return referencing;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "referencing")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setReferencing(@jakarta.annotation.Nullable List<ReferenceSystemConnection> referencing) {
        this.referencing = referencing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoverageJson other = (CoverageJson) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.domainType, other.domainType)
                && Objects.equals(this.domain, other.domain)
                && Objects.equals(this.coverages, other.coverages)
                && Objects.equals(this.parameters, other.parameters)
                && Objects.equals(this.ranges, other.ranges)
                && Objects.equals(this.referencing, other.referencing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, domainType, domain, coverages, parameters, ranges, referencing);
    }

}
