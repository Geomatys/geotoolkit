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
public class GridLimits extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private final String type = "GridLimits";

    public static final String JSON_PROPERTY_SRS_NAME = "srsName";
    private final String srsName = "http://www.opengis.net/def/crs/OGC/0/Index4D";

    public static final String JSON_PROPERTY_AXIS_LABELS = "axisLabels";
    private List<String> axisLabels;

    public static final String JSON_PROPERTY_AXIS = "axis";
    private List<IndexAxis> axis;

    public GridLimits() {}

    public GridLimits(List<IndexAxis> axis) {
        this.axis = axis;
        this.axisLabels = GeneralGrid.getAxisLabels(axis.stream()
                                                    .map(a -> (Axis) a)
                                                    .collect(Collectors.toList()));
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_SRS_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getSrsName() {
        return srsName;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_LABELS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<String> getAxisLabels() {
        return axisLabels;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<IndexAxis> getAxis() {
        return axis;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setAxis(List<IndexAxis> axis) {
        this.axis = axis;
        this.axisLabels = GeneralGrid.getAxisLabels(axis.stream()
                .map(a -> (Axis) a)
                .collect(Collectors.toList()));
    }
}
