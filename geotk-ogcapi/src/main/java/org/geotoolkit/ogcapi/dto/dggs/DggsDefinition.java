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
package org.geotoolkit.ogcapi.dto.dggs;

import org.geotoolkit.ogcapi.dto.common.Crs;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

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
public final class DggsDefinition extends DataTransferObject {

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    public static final String JSON_PROPERTY_BASE_POLYHEDRON = "basePolyhedron";
    private String basePolyhedron;

    public static final String JSON_PROPERTY_REFINEMENT_RATIO = "refinementRatio";
    private Integer refinementRatio;

    public static final String JSON_PROPERTY_CONSTRAINTS = "constraints";
    private DggsConstraints constraints;

    public static final String JSON_PROPERTY_SPATIAL_DIMENSIONS = "spatialDimensions";
    private Integer spatialDimensions;

    public static final String JSON_PROPERTY_TEMPORAL_DIMENSIONS = "temporalDimensions";
    private Integer temporalDimensions;

    public static final String JSON_PROPERTY_ZONE_TYPES = "zoneTypes";
    private List<String> zoneTypes = new ArrayList<>();

    /**
     * Gets or Sets refinementStrategy
     */
    public enum RefinementStrategyEnum {
        CENTRED_CHILD_CELL(String.valueOf("centredChildCell")),
        NESTED_CHILD_CELL(String.valueOf("nestedChildCell")),
        NODE_CENTRED_CHILD_CELL(String.valueOf("nodeCentredChildCell")),
        EDGE_CENTRED_CHILD_CELL(String.valueOf("edgeCentredChildCell")),
        FACE_CENTRED_CHILD_CELL(String.valueOf("faceCentredChildCell")),
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
    private List<RefinementStrategyEnum> refinementStrategy = new ArrayList<>();

    public DggsDefinition() {
    }

    public DggsDefinition crs(Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(Crs crs) {
        this.crs = crs;
    }

    public DggsDefinition basePolyhedron(String basePolyhedron) {
        this.basePolyhedron = basePolyhedron;
        return this;
    }

    /**
     * The Type/Class of Polyhedron used to construct the Discrete Global Grid System - if it is constructued using a
     * Base Polyhedron.
     *
     * @return basePolyhedron
     */
    @JsonProperty(JSON_PROPERTY_BASE_POLYHEDRON)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getBasePolyhedron() {
        return basePolyhedron;
    }

    @JsonProperty(JSON_PROPERTY_BASE_POLYHEDRON)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setBasePolyhedron(String basePolyhedron) {
        this.basePolyhedron = basePolyhedron;
    }

    public DggsDefinition refinementRatio(Integer refinementRatio) {
        this.refinementRatio = refinementRatio;
        return this;
    }

    /**
     * The ratio of the area of zones between two consecutive hierarchy level (the ratio of child zones to parent zones,
     * also called the aperture).
     *
     * @return refinementRatio
     */
    @JsonProperty(JSON_PROPERTY_REFINEMENT_RATIO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getRefinementRatio() {
        return refinementRatio;
    }

    @JsonProperty(JSON_PROPERTY_REFINEMENT_RATIO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRefinementRatio(Integer refinementRatio) {
        this.refinementRatio = refinementRatio;
    }

    public DggsDefinition constraints(DggsConstraints constraints) {
        this.constraints = constraints;
        return this;
    }

    /**
     * Get constraints
     *
     * @return constraints
     */
    @JsonProperty(JSON_PROPERTY_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DggsConstraints getConstraints() {
        return constraints;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setConstraints(DggsConstraints constraints) {
        this.constraints = constraints;
    }

    public DggsDefinition spatialDimensions(Integer spatialDimensions) {
        this.spatialDimensions = spatialDimensions;
        return this;
    }

    /**
     * Number of Spatial Dimensions defined by the Discrete Global Grid System.
     *
     * @return spatialDimensions
     */
    @JsonProperty(JSON_PROPERTY_SPATIAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Integer getSpatialDimensions() {
        return spatialDimensions;
    }

    @JsonProperty(JSON_PROPERTY_SPATIAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setSpatialDimensions(Integer spatialDimensions) {
        this.spatialDimensions = spatialDimensions;
    }

    public DggsDefinition temporalDimensions(Integer temporalDimensions) {
        this.temporalDimensions = temporalDimensions;
        return this;
    }

    /**
     * Number of Temporal Dimensions defined by the Discrete Global Grid System.
     *
     * @return temporalDimensions
     */
    @JsonProperty(JSON_PROPERTY_TEMPORAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Integer getTemporalDimensions() {
        return temporalDimensions;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTemporalDimensions(Integer temporalDimensions) {
        this.temporalDimensions = temporalDimensions;
    }

    public DggsDefinition zoneTypes(List<String> zoneTypes) {
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
    @JsonProperty(JSON_PROPERTY_ZONE_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getZoneTypes() {
        return zoneTypes;
    }

    @JsonProperty(JSON_PROPERTY_ZONE_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setZoneTypes(List<String> zoneTypes) {
        this.zoneTypes = zoneTypes;
    }

    public DggsDefinition refinementStrategy(List<RefinementStrategyEnum> refinementStrategy) {
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
    @JsonProperty(JSON_PROPERTY_REFINEMENT_STRATEGY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<RefinementStrategyEnum> getRefinementStrategy() {
        return refinementStrategy;
    }

    @JsonProperty(JSON_PROPERTY_REFINEMENT_STRATEGY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRefinementStrategy(List<RefinementStrategyEnum> refinementStrategy) {
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
