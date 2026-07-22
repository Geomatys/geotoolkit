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
@XmlRootElement(name = "Domain")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Domain")
public final class Domain extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_DOMAIN_TYPE = "domainType";
    @XmlElement(name = "domainType")
    @jakarta.annotation.Nullable
    private String domainType;

    public static final String JSON_PROPERTY_AXES = "axes";
    @XmlElement(name = "axes")
    @jakarta.annotation.Nullable
    private Axes axes;

    public static final String JSON_PROPERTY_REFERENCING = "referencing";
    @XmlElement(name = "referencing")
    @jakarta.annotation.Nullable
    private List<ReferenceSystemConnection> referencing;

    public Domain() {
    }

    public Domain type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * Always {@code "Domain"}.
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

    public Domain domainType(@jakarta.annotation.Nullable String domainType) {
        this.domainType = domainType;
        return this;
    }

    /**
     * E.g. {@code "Grid"}, {@code "PointSeries"}, {@code "Trajectory"}.
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

    public Domain axes(@jakarta.annotation.Nullable Axes axes) {
        this.axes = axes;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "axes")
    public Axes getAxes() {
        return axes;
    }

    @JsonProperty(JSON_PROPERTY_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "axes")
    public void setAxes(@jakarta.annotation.Nullable Axes axes) {
        this.axes = axes;
    }

    public Domain referencing(@jakarta.annotation.Nullable List<ReferenceSystemConnection> referencing) {
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
