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
 * The default JSON encoding.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class JsonEncoding extends AbstractEncoding {

    public static final String JSON_PROPERTY_RECORDS_AS_ARRAYS = "recordsAsArrays";
    private java.lang.Boolean recordsAsArrays;

    public static final String JSON_PROPERTY_VECTORS_AS_ARRAYS = "vectorsAsArrays";
    private java.lang.Boolean vectorsAsArrays;

    public JsonEncoding() {
        setType("JSONEncoding");
    }

    /**
     * If true, DataRecord values are encoded as JSON arrays instead of JSON objects. (optional, default false)
     */
    @JsonProperty(JSON_PROPERTY_RECORDS_AS_ARRAYS)
    public java.lang.Boolean getRecordsAsArrays() {
        return recordsAsArrays;
    }

    @JsonProperty(JSON_PROPERTY_RECORDS_AS_ARRAYS)
    public void setRecordsAsArrays(java.lang.Boolean recordsAsArrays) {
        this.recordsAsArrays = recordsAsArrays;
    }

    /**
     * If true, Vector values are encoded as JSON arrays instead of JSON objects. (optional, default false)
     */
    @JsonProperty(JSON_PROPERTY_VECTORS_AS_ARRAYS)
    public java.lang.Boolean getVectorsAsArrays() {
        return vectorsAsArrays;
    }

    @JsonProperty(JSON_PROPERTY_VECTORS_AS_ARRAYS)
    public void setVectorsAsArrays(java.lang.Boolean vectorsAsArrays) {
        this.vectorsAsArrays = vectorsAsArrays;
    }

}
