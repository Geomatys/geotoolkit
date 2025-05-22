/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.dggs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * The base definition of the hierarchical series of Discrete Global Grid, which may be parameterized.
 */
@JsonPropertyOrder({
    DggsDefinition.JSON_PROPERTY_CRS,
    DggsDefinition.JSON_PROPERTY_BASE_POLYHEDRON,
    DggsDefinition.JSON_PROPERTY_REFINEMENT_RATIO,
    DggsDefinition.JSON_PROPERTY_CONSTRAINTS,
    DggsDefinition.JSON_PROPERTY_SPATIAL_DIMENSIONS,
    DggsDefinition.JSON_PROPERTY_TEMPORAL_DIMENSIONS,
    DggsDefinition.JSON_PROPERTY_ZONE_TYPES,
    DggsDefinition.JSON_PROPERTY_REFINEMENT_STRATEGY
})
@XmlRootElement(name = "DggrsDefinitionDgghDefinition")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsDefinitionDgghDefinition")
public final class DggsDefinition extends DataTransferObject {

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private Crs crs;

    public static final String JSON_PROPERTY_BASE_POLYHEDRON = "basePolyhedron";
    @XmlElement(name = "basePolyhedron")
    @jakarta.annotation.Nullable
    private String basePolyhedron;

    public static final String JSON_PROPERTY_REFINEMENT_RATIO = "refinementRatio";
    @XmlElement(name = "refinementRatio")
    @jakarta.annotation.Nullable
    private Integer refinementRatio;

    public static final String JSON_PROPERTY_CONSTRAINTS = "constraints";
    @XmlElement(name = "constraints")
    @jakarta.annotation.Nullable
    private DggsConstraints constraints;

    public static final String JSON_PROPERTY_SPATIAL_DIMENSIONS = "spatialDimensions";
    @XmlElement(name = "spatialDimensions")
    @jakarta.annotation.Nonnull
    private Integer spatialDimensions;

    public static final String JSON_PROPERTY_TEMPORAL_DIMENSIONS = "temporalDimensions";
    @XmlElement(name = "temporalDimensions")
    @jakarta.annotation.Nonnull
    private Integer temporalDimensions;

    public static final String JSON_PROPERTY_ZONE_TYPES = "zoneTypes";
    @XmlElement(name = "zoneTypes")
    @jakarta.annotation.Nullable
    private List<String> zoneTypes = new ArrayList<>();

    /**
     * Gets or Sets refinementStrategy
     */
    @XmlType(name = "RefinementStrategyEnum")
    @XmlEnum(String.class)
    public enum RefinementStrategyEnum {
        @XmlEnumValue("centredChildCell")
        CENTRED_CHILD_CELL(String.valueOf("centredChildCell")),
        @XmlEnumValue("nestedChildCell")
        NESTED_CHILD_CELL(String.valueOf("nestedChildCell")),
        @XmlEnumValue("nodeCentredChildCell")
        NODE_CENTRED_CHILD_CELL(String.valueOf("nodeCentredChildCell")),
        @XmlEnumValue("edgeCentredChildCell")
        EDGE_CENTRED_CHILD_CELL(String.valueOf("edgeCentredChildCell")),
        @XmlEnumValue("faceCentredChildCell")
        FACE_CENTRED_CHILD_CELL(String.valueOf("faceCentredChildCell")),
        @XmlEnumValue("solidCentredChildCell")
        SOLID_CENTRED_CHILD_CELL(String.valueOf("solidCentredChildCell"));

        private String value;

        RefinementStrategyEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static RefinementStrategyEnum fromValue(String value) {
            for (RefinementStrategyEnum b : RefinementStrategyEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_REFINEMENT_STRATEGY = "refinementStrategy";
    @XmlElement(name = "refinementStrategy")
    @jakarta.annotation.Nullable
    private List<RefinementStrategyEnum> refinementStrategy = new ArrayList<>();

    public DggsDefinition() {
    }

    public DggsDefinition crs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
    }

    public DggsDefinition basePolyhedron(@jakarta.annotation.Nullable String basePolyhedron) {
        this.basePolyhedron = basePolyhedron;
        return this;
    }

    /**
     * The Type/Class of Polyhedron used to construct the Discrete Global Grid System - if it is constructued using a
     * Base Polyhedron.
     *
     * @return basePolyhedron
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BASE_POLYHEDRON)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "basePolyhedron")
    public String getBasePolyhedron() {
        return basePolyhedron;
    }

    @JsonProperty(JSON_PROPERTY_BASE_POLYHEDRON)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "basePolyhedron")
    public void setBasePolyhedron(@jakarta.annotation.Nullable String basePolyhedron) {
        this.basePolyhedron = basePolyhedron;
    }

    public DggsDefinition refinementRatio(@jakarta.annotation.Nullable Integer refinementRatio) {
        this.refinementRatio = refinementRatio;
        return this;
    }

    /**
     * The ratio of the area of zones between two consecutive hierarchy level (the ratio of child zones to parent zones,
     * also called the aperture).
     *
     * @return refinementRatio
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REFINEMENT_RATIO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "refinementRatio")
    public Integer getRefinementRatio() {
        return refinementRatio;
    }

    @JsonProperty(JSON_PROPERTY_REFINEMENT_RATIO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "refinementRatio")
    public void setRefinementRatio(@jakarta.annotation.Nullable Integer refinementRatio) {
        this.refinementRatio = refinementRatio;
    }

    public DggsDefinition constraints(@jakarta.annotation.Nullable DggsConstraints constraints) {
        this.constraints = constraints;
        return this;
    }

    /**
     * Get constraints
     *
     * @return constraints
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "constraints")
    public DggsConstraints getConstraints() {
        return constraints;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "constraints")
    public void setConstraints(@jakarta.annotation.Nullable DggsConstraints constraints) {
        this.constraints = constraints;
    }

    public DggsDefinition spatialDimensions(@jakarta.annotation.Nonnull Integer spatialDimensions) {
        this.spatialDimensions = spatialDimensions;
        return this;
    }

    /**
     * Number of Spatial Dimensions defined by the Discrete Global Grid System.
     *
     * @return spatialDimensions
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SPATIAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "spatialDimensions")
    public Integer getSpatialDimensions() {
        return spatialDimensions;
    }

    @JsonProperty(JSON_PROPERTY_SPATIAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "spatialDimensions")
    public void setSpatialDimensions(@jakarta.annotation.Nonnull Integer spatialDimensions) {
        this.spatialDimensions = spatialDimensions;
    }

    public DggsDefinition temporalDimensions(@jakarta.annotation.Nonnull Integer temporalDimensions) {
        this.temporalDimensions = temporalDimensions;
        return this;
    }

    /**
     * Number of Temporal Dimensions defined by the Discrete Global Grid System.
     *
     * @return temporalDimensions
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TEMPORAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "temporalDimensions")
    public Integer getTemporalDimensions() {
        return temporalDimensions;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "temporalDimensions")
    public void setTemporalDimensions(@jakarta.annotation.Nonnull Integer temporalDimensions) {
        this.temporalDimensions = temporalDimensions;
    }

    public DggsDefinition zoneTypes(@jakarta.annotation.Nullable List<String> zoneTypes) {
        this.zoneTypes = zoneTypes;
        return this;
    }

    public DggsDefinition addZoneTypesItem(String zoneTypesItem) {
        if (this.zoneTypes == null) {
            this.zoneTypes = new ArrayList<>();
        }
        this.zoneTypes.add(zoneTypesItem);
        return this;
    }

    /**
     * Get zoneTypes
     *
     * @return zoneTypes
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ZONE_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "zoneTypes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getZoneTypes() {
        return zoneTypes;
    }

    @JsonProperty(JSON_PROPERTY_ZONE_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "zoneTypes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setZoneTypes(@jakarta.annotation.Nullable List<String> zoneTypes) {
        this.zoneTypes = zoneTypes;
    }

    public DggsDefinition refinementStrategy(@jakarta.annotation.Nullable List<RefinementStrategyEnum> refinementStrategy) {
        this.refinementStrategy = refinementStrategy;
        return this;
    }

    public DggsDefinition addRefinementStrategyItem(RefinementStrategyEnum refinementStrategyItem) {
        if (this.refinementStrategy == null) {
            this.refinementStrategy = new ArrayList<>();
        }
        this.refinementStrategy.add(refinementStrategyItem);
        return this;
    }

    /**
     * The refinement strategy used by the Discrete Global Grid System
     *
     * @return refinementStrategy
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REFINEMENT_STRATEGY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "refinementStrategy")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<RefinementStrategyEnum> getRefinementStrategy() {
        return refinementStrategy;
    }

    @JsonProperty(JSON_PROPERTY_REFINEMENT_STRATEGY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "refinementStrategy")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setRefinementStrategy(@jakarta.annotation.Nullable List<RefinementStrategyEnum> refinementStrategy) {
        this.refinementStrategy = refinementStrategy;
    }

    /**
     * Return true if this dggrs_definition_dggh_definition object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggsDefinition dggrsDefinitionDgghDefinition = (DggsDefinition) o;
        return Objects.equals(this.crs, dggrsDefinitionDgghDefinition.crs)
                && Objects.equals(this.basePolyhedron, dggrsDefinitionDgghDefinition.basePolyhedron)
                && Objects.equals(this.refinementRatio, dggrsDefinitionDgghDefinition.refinementRatio)
                && Objects.equals(this.constraints, dggrsDefinitionDgghDefinition.constraints)
                && Objects.equals(this.spatialDimensions, dggrsDefinitionDgghDefinition.spatialDimensions)
                && Objects.equals(this.temporalDimensions, dggrsDefinitionDgghDefinition.temporalDimensions)
                && Objects.equals(this.zoneTypes, dggrsDefinitionDgghDefinition.zoneTypes)
                && Objects.equals(this.refinementStrategy, dggrsDefinitionDgghDefinition.refinementStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crs, basePolyhedron, refinementRatio, constraints, spatialDimensions, temporalDimensions, zoneTypes, refinementStrategy);
    }

}
