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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * DggrsDefinitionDgghDefinitionConstraints
 */
@JsonPropertyOrder({
    DggsConstraints.JSON_PROPERTY_CELL_AXIS_ALIGNED,
    DggsConstraints.JSON_PROPERTY_CELL_CONFORMAL,
    DggsConstraints.JSON_PROPERTY_CELL_EQUI_ANGULAR,
    DggsConstraints.JSON_PROPERTY_CELL_EQUI_DISTANT,
    DggsConstraints.JSON_PROPERTY_CELL_EQUAL_SIZED
})
@XmlRootElement(name = "DggrsDefinitionDgghDefinitionConstraints")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsDefinitionDgghDefinitionConstraints")
public final class DggsConstraints extends DataTransferObject {

    public static final String JSON_PROPERTY_CELL_AXIS_ALIGNED = "cellAxisAligned";
    @XmlElement(name = "cellAxisAligned")
    @jakarta.annotation.Nullable
    private Boolean cellAxisAligned = false;

    public static final String JSON_PROPERTY_CELL_CONFORMAL = "cellConformal";
    @XmlElement(name = "cellConformal")
    @jakarta.annotation.Nullable
    private Boolean cellConformal = false;

    public static final String JSON_PROPERTY_CELL_EQUI_ANGULAR = "cellEquiAngular";
    @XmlElement(name = "cellEquiAngular")
    @jakarta.annotation.Nullable
    private Boolean cellEquiAngular = false;

    public static final String JSON_PROPERTY_CELL_EQUI_DISTANT = "cellEquiDistant";
    @XmlElement(name = "cellEquiDistant")
    @jakarta.annotation.Nullable
    private Boolean cellEquiDistant = false;

    public static final String JSON_PROPERTY_CELL_EQUAL_SIZED = "cellEqualSized";
    @XmlElement(name = "cellEqualSized")
    @jakarta.annotation.Nullable
    private Boolean cellEqualSized = false;

    public DggsConstraints() {
    }

    public DggsConstraints cellAxisAligned(@jakarta.annotation.Nullable Boolean cellAxisAligned) {
        this.cellAxisAligned = cellAxisAligned;
        return this;
    }

    /**
     * Set to true if all edges of the geometry of all zones are aligned with one of the axis of the &#x60;crs&#x60;.
     *
     * @return cellAxisAligned
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CELL_AXIS_ALIGNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellAxisAligned")
    public Boolean getCellAxisAligned() {
        return cellAxisAligned;
    }

    @JsonProperty(JSON_PROPERTY_CELL_AXIS_ALIGNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellAxisAligned")
    public void setCellAxisAligned(@jakarta.annotation.Nullable Boolean cellAxisAligned) {
        this.cellAxisAligned = cellAxisAligned;
    }

    public DggsConstraints cellConformal(@jakarta.annotation.Nullable Boolean cellConformal) {
        this.cellConformal = cellConformal;
        return this;
    }

    /**
     * Get cellConformal
     *
     * @return cellConformal
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CELL_CONFORMAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellConformal")
    public Boolean getCellConformal() {
        return cellConformal;
    }

    @JsonProperty(JSON_PROPERTY_CELL_CONFORMAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellConformal")
    public void setCellConformal(@jakarta.annotation.Nullable Boolean cellConformal) {
        this.cellConformal = cellConformal;
    }

    public DggsConstraints cellEquiAngular(@jakarta.annotation.Nullable Boolean cellEquiAngular) {
        this.cellEquiAngular = cellEquiAngular;
        return this;
    }

    /**
     * Get cellEquiAngular
     *
     * @return cellEquiAngular
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CELL_EQUI_ANGULAR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellEquiAngular")
    public Boolean getCellEquiAngular() {
        return cellEquiAngular;
    }

    @JsonProperty(JSON_PROPERTY_CELL_EQUI_ANGULAR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellEquiAngular")
    public void setCellEquiAngular(@jakarta.annotation.Nullable Boolean cellEquiAngular) {
        this.cellEquiAngular = cellEquiAngular;
    }

    public DggsConstraints cellEquiDistant(@jakarta.annotation.Nullable Boolean cellEquiDistant) {
        this.cellEquiDistant = cellEquiDistant;
        return this;
    }

    /**
     * Get cellEquiDistant
     *
     * @return cellEquiDistant
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CELL_EQUI_DISTANT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellEquiDistant")
    public Boolean getCellEquiDistant() {
        return cellEquiDistant;
    }

    @JsonProperty(JSON_PROPERTY_CELL_EQUI_DISTANT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellEquiDistant")
    public void setCellEquiDistant(@jakarta.annotation.Nullable Boolean cellEquiDistant) {
        this.cellEquiDistant = cellEquiDistant;
    }

    public DggsConstraints cellEqualSized(@jakarta.annotation.Nullable Boolean cellEqualSized) {
        this.cellEqualSized = cellEqualSized;
        return this;
    }

    /**
     * Set to true if the area of all zones is the same for a particular zone geometry type of any specifc discrete
     * global grid of the DGG hierarchy.
     *
     * @return cellEqualSized
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CELL_EQUAL_SIZED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellEqualSized")
    public Boolean getCellEqualSized() {
        return cellEqualSized;
    }

    @JsonProperty(JSON_PROPERTY_CELL_EQUAL_SIZED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellEqualSized")
    public void setCellEqualSized(@jakarta.annotation.Nullable Boolean cellEqualSized) {
        this.cellEqualSized = cellEqualSized;
    }

    /**
     * Return true if this dggrs_definition_dggh_definition_constraints object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggsConstraints dggrsDefinitionDgghDefinitionConstraints = (DggsConstraints) o;
        return Objects.equals(this.cellAxisAligned, dggrsDefinitionDgghDefinitionConstraints.cellAxisAligned)
                && Objects.equals(this.cellConformal, dggrsDefinitionDgghDefinitionConstraints.cellConformal)
                && Objects.equals(this.cellEquiAngular, dggrsDefinitionDgghDefinitionConstraints.cellEquiAngular)
                && Objects.equals(this.cellEquiDistant, dggrsDefinitionDgghDefinitionConstraints.cellEquiDistant)
                && Objects.equals(this.cellEqualSized, dggrsDefinitionDgghDefinitionConstraints.cellEqualSized);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellAxisAligned, cellConformal, cellEquiAngular, cellEquiDistant, cellEqualSized);
    }

}
