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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Reference to a unit of measure, by UCUM code and/or by URI. At least one of {@code code}/{@code
 * href} is present.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class UnitReference extends DataTransferObject {

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_SYMBOL = "symbol";
    private String symbol;

    public static final String JSON_PROPERTY_CODE = "code";
    private String code;

    public static final String JSON_PROPERTY_HREF = "href";
    private String href;

    /**
     * Human readable label for the unit. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Preferred unit symbol for UI display. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SYMBOL)
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty(JSON_PROPERTY_SYMBOL)
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * UCUM code of the unit. (required unless href is provided)
     */
    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * URI of the unit definition. (required unless code is provided)
     */
    @JsonProperty(JSON_PROPERTY_HREF)
    public String getHref() {
        return href;
    }

    @JsonProperty(JSON_PROPERTY_HREF)
    public void setHref(String href) {
        this.href = href;
    }

}
