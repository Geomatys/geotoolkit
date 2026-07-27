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

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        DataRecordField.JSON_PROPERTY_TYPE,
        DataRecordField.JSON_PROPERTY_NAME,
        DataRecordField.JSON_PROPERTY_DESCRIPTION,
        DataRecordField.JSON_PROPERTY_ENCODING_INFO
})
public class DataRecordField extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private final String type = "Quantity";

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_ENCODING_INFO = "encodingInfo";
    private EncodingInfo encodingInfo;

    public DataRecordField() {}

    public DataRecordField(String name, String description, EncodingInfo encodingInfo) {
        this.name = name;
        this.description = description;
        this.encodingInfo = encodingInfo;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING_INFO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public EncodingInfo getEncodingInfo() {
        return encodingInfo;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING_INFO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setEncodingInfo(EncodingInfo encodingInfo) {
        this.encodingInfo = encodingInfo;
    }
}
