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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The unit of measure of a {@link Parameter}, per the CoverageJSON {@code unit} object.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    Unit.JSON_PROPERTY_ID,
    Unit.JSON_PROPERTY_LABEL,
    Unit.JSON_PROPERTY_SYMBOL,
    Unit.JSON_PROPERTY_SYMBOL_TYPE
})
@XmlRootElement(name = "Unit")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Unit")
public final class Unit extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    @XmlElement(name = "label")
    @jakarta.annotation.Nullable
    private String label;

    public static final String JSON_PROPERTY_SYMBOL = "symbol";
    @XmlElement(name = "symbol")
    @jakarta.annotation.Nullable
    private String symbol;

    public static final String JSON_PROPERTY_SYMBOL_TYPE = "symbolType";
    @XmlElement(name = "symbolType")
    @jakarta.annotation.Nullable
    private String symbolType;

    public Unit() {
    }

    public Unit id(@jakarta.annotation.Nullable String id) {
        this.id = id;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nullable String id) {
        this.id = id;
    }

    public Unit label(@jakarta.annotation.Nullable String label) {
        this.label = label;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "label")
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "label")
    public void setLabel(@jakarta.annotation.Nullable String label) {
        this.label = label;
    }

    public Unit symbol(@jakarta.annotation.Nullable String symbol) {
        this.symbol = symbol;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SYMBOL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty(JSON_PROPERTY_SYMBOL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "symbol")
    public void setSymbol(@jakarta.annotation.Nullable String symbol) {
        this.symbol = symbol;
    }

    public Unit symbolType(@jakarta.annotation.Nullable String symbolType) {
        this.symbolType = symbolType;
        return this;
    }

    /**
     * URI identifying the type/scheme of {@link #getSymbol()} (e.g. UCUM).
     *
     * @return symbolType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SYMBOL_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "symbolType")
    public String getSymbolType() {
        return symbolType;
    }

    @JsonProperty(JSON_PROPERTY_SYMBOL_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "symbolType")
    public void setSymbolType(@jakarta.annotation.Nullable String symbolType) {
        this.symbolType = symbolType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit other = (Unit) o;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.label, other.label)
                && Objects.equals(this.symbol, other.symbol)
                && Objects.equals(this.symbolType, other.symbolType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, symbol, symbolType);
    }

}
