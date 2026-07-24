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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.encoding;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A delimiter-separated text (CSV-like) encoding.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class TextEncoding extends AbstractEncoding {

    public static final String JSON_PROPERTY_COLLAPSE_WHITE_SPACES = "collapseWhiteSpaces";
    private java.lang.Boolean collapseWhiteSpaces;

    public static final String JSON_PROPERTY_DECIMAL_SEPARATOR = "decimalSeparator";
    private String decimalSeparator;

    public static final String JSON_PROPERTY_TOKEN_SEPARATOR = "tokenSeparator";
    private String tokenSeparator;

    public static final String JSON_PROPERTY_BLOCK_SEPARATOR = "blockSeparator";
    private String blockSeparator;

    public TextEncoding() {
        setType("TextEncoding");
    }

    @JsonProperty(JSON_PROPERTY_COLLAPSE_WHITE_SPACES)
    public java.lang.Boolean getCollapseWhiteSpaces() {
        return collapseWhiteSpaces;
    }

    @JsonProperty(JSON_PROPERTY_COLLAPSE_WHITE_SPACES)
    public void setCollapseWhiteSpaces(java.lang.Boolean collapseWhiteSpaces) {
        this.collapseWhiteSpaces = collapseWhiteSpaces;
    }

    @JsonProperty(JSON_PROPERTY_DECIMAL_SEPARATOR)
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    @JsonProperty(JSON_PROPERTY_DECIMAL_SEPARATOR)
    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    /**
     * Character(s) separating token values within a record. (required)
     */
    @JsonProperty(JSON_PROPERTY_TOKEN_SEPARATOR)
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    @JsonProperty(JSON_PROPERTY_TOKEN_SEPARATOR)
    public void setTokenSeparator(String tokenSeparator) {
        this.tokenSeparator = tokenSeparator;
    }

    /**
     * Character(s) separating records (blocks) within the stream. (required)
     */
    @JsonProperty(JSON_PROPERTY_BLOCK_SEPARATOR)
    public String getBlockSeparator() {
        return blockSeparator;
    }

    @JsonProperty(JSON_PROPERTY_BLOCK_SEPARATOR)
    public void setBlockSeparator(String blockSeparator) {
        this.blockSeparator = blockSeparator;
    }

}
