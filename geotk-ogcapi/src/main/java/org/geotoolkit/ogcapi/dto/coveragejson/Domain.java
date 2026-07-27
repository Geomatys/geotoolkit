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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The spatiotemporal domain of a {@link CoverageJson}, per the CoverageJSON {@code Domain} object.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    Domain.JSON_PROPERTY_TYPE,
    Domain.JSON_PROPERTY_DOMAIN_TYPE,
    Domain.JSON_PROPERTY_AXES,
    Domain.JSON_PROPERTY_REFERENCING
})
public final class Domain extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_DOMAIN_TYPE = "domainType";
    private String domainType;

    public static final String JSON_PROPERTY_AXES = "axes";
    private Axes axes;

    public static final String JSON_PROPERTY_REFERENCING = "referencing";
    private List<ReferenceSystemConnection> referencing;

    public Domain() {
    }

    public Domain type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Always {@code "Domain"}.
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

    public Domain domainType(String domainType) {
        this.domainType = domainType;
        return this;
    }

    /**
     * E.g. {@code "Grid"}, {@code "PointSeries"}, {@code "Trajectory"}.
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

    public Domain axes(Axes axes) {
        this.axes = axes;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Axes getAxes() {
        return axes;
    }

    @JsonProperty(JSON_PROPERTY_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAxes(Axes axes) {
        this.axes = axes;
    }

    public Domain referencing(List<ReferenceSystemConnection> referencing) {
        this.referencing = referencing;
        return this;
    }

    public Domain addReferencingItem(ReferenceSystemConnection referencingItem) {
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
        Domain other = (Domain) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.domainType, other.domainType)
                && Objects.equals(this.axes, other.axes)
                && Objects.equals(this.referencing, other.referencing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, domainType, axes, referencing);
    }

}
