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
package org.geotoolkit.ogcapi.dto.sensorthings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The unit of measurement of an observed value, used by {@link Datastream} and
 * {@link MultiDatastream}. The whole {@code unitOfMeasurement}/{@code unitOfMeasurements} value
 * itself may be {@code null} for a Datastream whose observations are not numeric.
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#unitofmeasurement
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    UnitOfMeasurement.JSON_PROPERTY_NAME,
    UnitOfMeasurement.JSON_PROPERTY_SYMBOL,
    UnitOfMeasurement.JSON_PROPERTY_DEFINITION
})
public final class UnitOfMeasurement extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_SYMBOL = "symbol";
    private String symbol;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public UnitOfMeasurement() {
    }

    public UnitOfMeasurement name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The full name of the unit of measurement, e.g. {@code "DegreeCelsius"}. (optional)
     *
     * @return the name
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    public UnitOfMeasurement symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * The symbol of the unit, e.g. {@code "degC"}. (optional)
     *
     * @return the symbol
     */
    @JsonProperty(JSON_PROPERTY_SYMBOL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty(JSON_PROPERTY_SYMBOL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public UnitOfMeasurement definition(String definition) {
        this.definition = definition;
        return this;
    }

    /**
     * A URI identifying the unit of measurement, e.g. a QUDT or UCUM URI. (optional)
     *
     * @return the definition
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnitOfMeasurement other = (UnitOfMeasurement) o;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.symbol, other.symbol)
                && Objects.equals(this.definition, other.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol, definition);
    }
}
