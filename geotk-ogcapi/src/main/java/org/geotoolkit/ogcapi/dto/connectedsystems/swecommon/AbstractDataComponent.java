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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base of every SWE Common 3.0 data component ("AnyComponent" union of the standard).
 *
 * <p>The {@code name} field is not part of the SWE Common data component model itself: it is
 * added here to directly carry the value of the {@code SoftNamedProperty} mixin the standard
 * wraps around components used in a named context ({@code DataRecord.fields}, {@code
 * DataArray}/{@code Matrix}.{@code elementType}, {@code Vector.coordinates}, {@code
 * DataChoice.items}). It is {@code null} when the component is used standalone.</p>
 *
 * <p>Simplification: the standard allows such named entries to alternatively be a bare
 * by-reference pointer ({@code AssociationAttributeGroup}: {@code href}/{@code role}/{@code
 * arcrole}/{@code title}) instead of an inline component. This binding only models the common,
 * inline-component case.</p>
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = Boolean.class, name = "Boolean"),
    @Type(value = Count.class, name = "Count"),
    @Type(value = CountRange.class, name = "CountRange"),
    @Type(value = Quantity.class, name = "Quantity"),
    @Type(value = QuantityRange.class, name = "QuantityRange"),
    @Type(value = Time.class, name = "Time"),
    @Type(value = TimeRange.class, name = "TimeRange"),
    @Type(value = Category.class, name = "Category"),
    @Type(value = CategoryRange.class, name = "CategoryRange"),
    @Type(value = Text.class, name = "Text"),
    @Type(value = DataRecord.class, name = "DataRecord"),
    @Type(value = Vector.class, name = "Vector"),
    @Type(value = DataArray.class, name = "DataArray"),
    @Type(value = Matrix.class, name = "Matrix"),
    @Type(value = DataChoice.class, name = "DataChoice"),
    @Type(value = Geometry.class, name = "Geometry")
})
public abstract class AbstractDataComponent extends AbstractSweIdentifiable {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_UPDATABLE = "updatable";
    private java.lang.Boolean updatable;

    public static final String JSON_PROPERTY_OPTIONAL = "optional";
    private java.lang.Boolean optional;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    /**
     * Discriminator identifying the concrete component type. (required)
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    /**
     * If true, the component value can be externally updated (i.e. is variable). (optional, default false)
     */
    @JsonProperty(JSON_PROPERTY_UPDATABLE)
    public java.lang.Boolean getUpdatable() {
        return updatable;
    }

    @JsonProperty(JSON_PROPERTY_UPDATABLE)
    public void setUpdatable(java.lang.Boolean updatable) {
        this.updatable = updatable;
    }

    /**
     * If true, data for this component can be omitted in a data stream. (optional, default false)
     */
    @JsonProperty(JSON_PROPERTY_OPTIONAL)
    public java.lang.Boolean getOptional() {
        return optional;
    }

    @JsonProperty(JSON_PROPERTY_OPTIONAL)
    public void setOptional(java.lang.Boolean optional) {
        this.optional = optional;
    }

    /**
     * Semantic link (URI) for the property represented by this component. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * Name of this component within its enclosing named list (fields/coordinates/items/elementType). (optional)
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

}
