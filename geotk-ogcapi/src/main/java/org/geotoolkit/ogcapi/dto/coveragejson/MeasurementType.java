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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Whether a {@link Parameter} represents a {@code "Category"} or a {@code "Range"} of values.
 * The two CoverageJSON schema variants are flattened into this single class.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    MeasurementType.JSON_PROPERTY_TYPE,
    MeasurementType.JSON_PROPERTY_LABEL
})
public final class MeasurementType extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public MeasurementType() {
    }

    public MeasurementType type(String type) {
        this.type = type;
        return this;
    }

    /**
     * {@code "Category"} or {@code "Range"}.
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

    public MeasurementType label(String label) {
        this.label = label;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeasurementType other = (MeasurementType) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.label, other.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, label);
    }

}
