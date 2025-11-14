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
package org.geotoolkit.ogcapi.model.common;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * The domain intervals for any additional dimensions of the extent (envelope) beyond those described in temporal and
 * spatial.
 */
@JsonPropertyOrder({
    OtherDimension.JSON_PROPERTY_INTERVAL,
    OtherDimension.JSON_PROPERTY_TRS,
    OtherDimension.JSON_PROPERTY_VRS,
    OtherDimension.JSON_PROPERTY_GRID,
    OtherDimension.JSON_PROPERTY_DEFINITION,
    OtherDimension.JSON_PROPERTY_UNIT,
    OtherDimension.JSON_PROPERTY_UNIT_LANG,
    OtherDimension.JSON_PROPERTY_VARIABLE_TYPE
})
@XmlRootElement(name = "OtherDimension")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "OtherDimension")
public class OtherDimension extends DataTransferObject {

    public static final String JSON_PROPERTY_INTERVAL = "interval";
    @XmlElement(name = "interval")
    @jakarta.annotation.Nullable
    private List<List<Object>> interval = new ArrayList<>();

    public static final String JSON_PROPERTY_TRS = "trs";
    @XmlElement(name = "trs")
    @jakarta.annotation.Nullable
    private String trs;

    public static final String JSON_PROPERTY_VRS = "vrs";
    @XmlElement(name = "vrs")
    @jakarta.annotation.Nullable
    private String vrs;

    public static final String JSON_PROPERTY_GRID = "grid";
    @XmlElement(name = "grid")
    @jakarta.annotation.Nullable
    private Grid grid;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    @XmlElement(name = "definition")
    @jakarta.annotation.Nullable
    private URI definition;

    public static final String JSON_PROPERTY_UNIT = "unit";
    @XmlElement(name = "unit")
    @jakarta.annotation.Nullable
    private String unit;

    public static final String JSON_PROPERTY_UNIT_LANG = "unitLang";
    @XmlElement(name = "unitLang")
    @jakarta.annotation.Nullable
    private String unitLang = "UCUM";


    public static final String JSON_PROPERTY_VARIABLE_TYPE = "variableType";
    @XmlElement(name = "variableType")
    private VariableType variableType;

    public OtherDimension() {
    }

    public OtherDimension interval(@jakarta.annotation.Nullable List<List<Object>> interval) {
        this.interval = interval;
        return this;
    }

    public OtherDimension addIntervalItem(List<Object> intervalItem) {
        if (this.interval == null) {
            this.interval = new ArrayList<>();
        }
        this.interval.add(intervalItem);
        return this;
    }

    /**
     * One or more intervals that describe the extent for this dimension of the dataset. The value &#x60;null&#x60; is
     * supported and indicates an unbounded or half-bounded interval. The first interval describes the overall extent of
     * the data for this dimension. All subsequent intervals describe more precise intervals, e.g., to identify clusters
     * of data. Clients only interested in the overall extent will only need to access the first item (a pair of lower
     * and upper bound values).
     *
     * @return interval
     */
    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "interval")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<Object>> getInterval() {
        return interval;
    }

    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "interval")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setInterval(@jakarta.annotation.Nullable List<List<Object>> interval) {
        this.interval = interval;
    }

    public OtherDimension trs(@jakarta.annotation.Nullable String trs) {
        this.trs = trs;
        return this;
    }

    /**
     * temporal coordinate reference system (e.g. as defined by Features for &#39;temporal&#39;)
     *
     * @return trs
     */
    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "trs")
    public String getTrs() {
        return trs;
    }

    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "trs")
    public void setTrs(@jakarta.annotation.Nullable String trs) {
        this.trs = trs;
    }

    public OtherDimension vrs(@jakarta.annotation.Nullable String vrs) {
        this.vrs = vrs;
        return this;
    }

    /**
     * vertical coordinate reference system (e.g. as defined in EDR for &#39;vertical&#39;)
     *
     * @return vrs
     */
    @JsonProperty(JSON_PROPERTY_VRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "vrs")
    public String getVrs() {
        return vrs;
    }

    @JsonProperty(JSON_PROPERTY_VRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "vrs")
    public void setVrs(@jakarta.annotation.Nullable String vrs) {
        this.vrs = vrs;
    }

    public OtherDimension grid(@jakarta.annotation.Nullable Grid grid) {
        this.grid = grid;
        return this;
    }

    /**
     * Get grid
     *
     * @return grid
     */
    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "grid")
    public Grid getGrid() {
        return grid;
    }

    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "grid")
    public void setGrid(@jakarta.annotation.Nullable Grid grid) {
        this.grid = grid;
    }

    public OtherDimension definition(@jakarta.annotation.Nullable URI definition) {
        this.definition = definition;
        return this;
    }

    /**
     * A URI to the definition of the measured or observed property corresponding to this dimension.
     *
     * @return definition
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "definition")
    public URI getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "definition")
    public void setDefinition(@jakarta.annotation.Nullable URI definition) {
        this.definition = definition;
    }

    public OtherDimension unit(@jakarta.annotation.Nullable String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * The unit of measure in which the interval and/or grid values are expressed.
     *
     * @return unit
     */
    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unit")
    public void setUnit(@jakarta.annotation.Nullable String unit) {
        this.unit = unit;
    }

    public OtherDimension unitLang(@jakarta.annotation.Nullable String unitLang) {
        this.unitLang = unitLang;
        return this;
    }

    /**
     * The language (or vocabulary) in which the unit is expressed (defaults to \&quot;UCUM\&quot; if not specified).
     *
     * @return unitLang
     */
    @JsonProperty(JSON_PROPERTY_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unitLang")
    public String getUnitLang() {
        return unitLang;
    }

    @JsonProperty(JSON_PROPERTY_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unitLang")
    public void setUnitLang(@jakarta.annotation.Nullable String unitLang) {
        this.unitLang = unitLang;
    }

    public OtherDimension variableType(@jakarta.annotation.Nullable VariableType variableType) {
        this.variableType = variableType;
        return this;
    }

    /**
     * The type of variable which may inform correct interpretation and interpolation methods
     *
     * @return variableType
     */
    @JsonProperty(JSON_PROPERTY_VARIABLE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "variableType")
    public VariableType getVariableType() {
        return variableType;
    }

    @JsonProperty(JSON_PROPERTY_VARIABLE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "variableType")
    public void setVariableType(@jakarta.annotation.Nullable VariableType variableType) {
        this.variableType = variableType;
    }

    /**
     * Return true if this extent_allOf_otherdim object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OtherDimension extentAllOfOtherdim = (OtherDimension) o;
        return Objects.equals(this.interval, extentAllOfOtherdim.interval)
                && Objects.equals(this.trs, extentAllOfOtherdim.trs)
                && Objects.equals(this.vrs, extentAllOfOtherdim.vrs)
                && Objects.equals(this.grid, extentAllOfOtherdim.grid)
                && Objects.equals(this.definition, extentAllOfOtherdim.definition)
                && Objects.equals(this.unit, extentAllOfOtherdim.unit)
                && Objects.equals(this.unitLang, extentAllOfOtherdim.unitLang)
                && Objects.equals(this.variableType, extentAllOfOtherdim.variableType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, trs, vrs, grid, definition, unit, unitLang, variableType);
    }

}
