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
import java.util.stream.Collectors;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        GridLimits.JSON_PROPERTY_TYPE,
        GridLimits.JSON_PROPERTY_SRS_NAME,
        GridLimits.JSON_PROPERTY_AXIS_LABELS,
        GridLimits.JSON_PROPERTY_AXIS
})
@XmlRootElement(name = "GridLimits")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GridLimits")
public class GridLimits extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    private final String type = "GridLimits";

    public static final String JSON_PROPERTY_SRS_NAME = "srsName";
    @XmlElement(name = "srsName")
    private final String srsName = "http://www.opengis.net/def/crs/OGC/0/Index4D";

    public static final String JSON_PROPERTY_AXIS_LABELS = "axisLabels";
    @XmlElement(name = "axisLabels")
    private List<String> axisLabels;

    public static final String JSON_PROPERTY_AXIS = "axis";
    @XmlElement(name = "axis")
    private List<IndexAxis> axis;

    public GridLimits(List<IndexAxis> axis) {
        this.axis = axis;
        this.axisLabels = GeneralGrid.getAxisLabels(axis.stream()
                                                    .map(a -> (Axis) a)
                                                    .collect(Collectors.toList()));
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SRS_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "srsName")
    public String getSrsName() {
        return srsName;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_AXIS_LABELS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axisLabels")
    public List<String> getAxisLabels() {
        return axisLabels;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axis")
    public List<IndexAxis> getAxis() {
        return axis;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "axis")
    public void setAxis(List<IndexAxis> axis) {
        this.axis = axis;
        this.axisLabels = GeneralGrid.getAxisLabels(axis.stream()
                .map(a -> (Axis) a)
                .collect(Collectors.toList()));
    }
}
