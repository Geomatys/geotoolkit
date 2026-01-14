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
package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.List;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        DataRecord.JSON_PROPERTY_TYPE,
        DataRecord.JSON_PROPERTY_FIELD,
})
@XmlRootElement(name = "DataRecord")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DataRecord")
public class DataRecord extends DataTransferObject implements CoverageResponse {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    private final CoverageResponseType type = CoverageResponseType.DataRecord;

    public static final String JSON_PROPERTY_FIELD = "field";
    @XmlElement(name = "field")
    @jakarta.annotation.Nullable
    private List<DataRecordField> field;

    public DataRecord() {
    }

    public DataRecord(List<DataRecordField> field) {
        this.field = field;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public CoverageResponseType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_FIELD)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "field")
    public List<DataRecordField> getField() {
        return field;
    }

    @JsonProperty(JSON_PROPERTY_FIELD)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "field")
    public void setField(List<DataRecordField> field) {
        this.field = field;
    }
}
