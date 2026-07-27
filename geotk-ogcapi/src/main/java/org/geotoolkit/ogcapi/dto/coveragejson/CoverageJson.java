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
public final class CoverageJson extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_DOMAIN_TYPE = "domainType";
    private String domainType;

    public static final String JSON_PROPERTY_DOMAIN = "domain";
    private Domain domain;

    public static final String JSON_PROPERTY_COVERAGES = "coverages";
    private List<CoverageJson> coverages;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private Map<String, Parameter> parameters;

    public static final String JSON_PROPERTY_RANGES = "ranges";
    private Map<String, NdArray> ranges;

    public static final String JSON_PROPERTY_REFERENCING = "referencing";
    private List<ReferenceSystemConnection> referencing;

    public CoverageJson() {
    }

    public CoverageJson type(String type) {
        this.type = type;
        return this;
    }

    /**
     * {@code "Coverage"}, {@code "CoverageCollection"} or {@code "Domain"}.
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(String type) {
        this.type = type;
    }

    public CoverageJson domainType(String domainType) {
        this.domainType = domainType;
        return this;
    }

    /**
     * Only set when {@link #getType()} is {@code "CoverageCollection"}, as a hint for the
     * {@link #getDomainType()} shared by all {@link #getCoverages()}.
     *
     * @return domainType
     */
    @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDomainType() {
        return domainType;
    }

    @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public CoverageJson domain(Domain domain) {
        this.domain = domain;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DOMAIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Domain getDomain() {
        return domain;
    }

    @JsonProperty(JSON_PROPERTY_DOMAIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public CoverageJson coverages(List<CoverageJson> coverages) {
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
    @JsonProperty(JSON_PROPERTY_COVERAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<CoverageJson> getCoverages() {
        return coverages;
    }

    @JsonProperty(JSON_PROPERTY_COVERAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCoverages(List<CoverageJson> coverages) {
        this.coverages = coverages;
    }

    public CoverageJson parameters(Map<String, Parameter> parameters) {
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

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public CoverageJson ranges(Map<String, NdArray> ranges) {
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

    @JsonProperty(JSON_PROPERTY_RANGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, NdArray> getRanges() {
        return ranges;
    }

    @JsonProperty(JSON_PROPERTY_RANGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRanges(Map<String, NdArray> ranges) {
        this.ranges = ranges;
    }

    public CoverageJson referencing(List<ReferenceSystemConnection> referencing) {
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

    @JsonProperty(JSON_PROPERTY_REFERENCING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<ReferenceSystemConnection> getReferencing() {
        return referencing;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setReferencing(List<ReferenceSystemConnection> referencing) {
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
