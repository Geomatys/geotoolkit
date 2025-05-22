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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Grid;

/**
 * DggsJsonDimensionsInner
 */
@JsonPropertyOrder({
    Dimension.JSON_PROPERTY_NAME,
    Dimension.JSON_PROPERTY_DEFINITION,
    Dimension.JSON_PROPERTY_UNIT,
    Dimension.JSON_PROPERTY_UNIT_LANG,
    Dimension.JSON_PROPERTY_GRID,
    Dimension.JSON_PROPERTY_INTERVAL
})
@XmlRootElement(name = "DggsJsonDimensionsInner")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggsJsonDimensionsInner")
public final class Dimension extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

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
    private URI unitLang;

    public static final String JSON_PROPERTY_GRID = "grid";
    @XmlElement(name = "grid")
    @jakarta.annotation.Nullable
    private Grid grid;

    public static final String JSON_PROPERTY_INTERVAL = "interval";
    @XmlElement(name = "interval")
    @jakarta.annotation.Nonnull
    private List<Object> interval = new ArrayList<>();

    public Dimension() {
    }

    public Dimension name(@jakarta.annotation.Nonnull String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nonnull String name) {
        this.name = name;
    }

    public Dimension definition(@jakarta.annotation.Nullable URI definition) {
        this.definition = definition;
        return this;
    }

    /**
     * Get definition
     *
     * @return definition
     */
    @jakarta.annotation.Nullable
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

    public Dimension unit(@jakarta.annotation.Nullable String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * Get unit
     *
     * @return unit
     */
    @jakarta.annotation.Nullable
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

    public Dimension unitLang(@jakarta.annotation.Nullable URI unitLang) {
        this.unitLang = unitLang;
        return this;
    }

    /**
     * Get unitLang
     *
     * @return unitLang
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unitLang")
    public URI getUnitLang() {
        return unitLang;
    }

    @JsonProperty(JSON_PROPERTY_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unitLang")
    public void setUnitLang(@jakarta.annotation.Nullable URI unitLang) {
        this.unitLang = unitLang;
    }

    public Dimension grid(@jakarta.annotation.Nullable Grid grid) {
        this.grid = grid;
        return this;
    }

    /**
     * Get grid
     *
     * @return grid
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "grid")
    public Grid getGrid() {
        return grid;
    }

    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "grid")
    public void setGrid(@jakarta.annotation.Nullable Grid grid) {
        this.grid = grid;
    }

    public Dimension interval(@jakarta.annotation.Nonnull List<Object> interval) {
        this.interval = interval;
        return this;
    }

    public Dimension addIntervalItem(Object intervalItem) {
        if (this.interval == null) {
            this.interval = new ArrayList<>();
        }
        this.interval.add(intervalItem);
        return this;
    }

    /**
     * Get interval
     *
     * @return interval
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "interval")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getInterval() {
        return interval;
    }

    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "interval")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setInterval(@jakarta.annotation.Nonnull List<Object> interval) {
        this.interval = interval;
    }

    /**
     * Return true if this dggs_json_dimensions_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dimension dggsJsonDimensionsInner = (Dimension) o;
        return Objects.equals(this.name, dggsJsonDimensionsInner.name)
                && Objects.equals(this.definition, dggsJsonDimensionsInner.definition)
                && Objects.equals(this.unit, dggsJsonDimensionsInner.unit)
                && Objects.equals(this.unitLang, dggsJsonDimensionsInner.unitLang)
                && Objects.equals(this.grid, dggsJsonDimensionsInner.grid)
                && Objects.equals(this.interval, dggsJsonDimensionsInner.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, definition, unit, unitLang, grid, interval);
    }

}
