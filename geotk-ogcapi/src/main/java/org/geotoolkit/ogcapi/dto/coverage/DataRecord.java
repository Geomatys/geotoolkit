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
package org.geotoolkit.ogcapi.dto.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

import java.util.List;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        DataRecord.JSON_PROPERTY_TYPE,
        DataRecord.JSON_PROPERTY_FIELD,
})
public class DataRecord extends DataTransferObject implements CoverageResponse {

    public static final String JSON_PROPERTY_TYPE = "type";
    private final CoverageResponseType type = CoverageResponseType.DataRecord;

    public static final String JSON_PROPERTY_FIELD = "field";
    private List<DataRecordField> field;

    public DataRecord() {
    }

    public DataRecord(List<DataRecordField> field) {
        this.field = field;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public CoverageResponseType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_FIELD)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<DataRecordField> getField() {
        return field;
    }

    @JsonProperty(JSON_PROPERTY_FIELD)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setField(List<DataRecordField> field) {
        this.field = field;
    }
}
