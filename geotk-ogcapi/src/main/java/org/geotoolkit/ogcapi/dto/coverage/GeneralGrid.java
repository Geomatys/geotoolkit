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
        GeneralGrid.JSON_PROPERTY_TYPE,
        GeneralGrid.JSON_PROPERTY_SRS_NAME,
        GeneralGrid.JSON_PROPERTY_AXIS_LABELS,
        GeneralGrid.JSON_PROPERTY_AXIS,
        GeneralGrid.JSON_PROPERTY_GRID_LIMITS
})
public class GeneralGrid extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private final String type = "GeneralGridCoverage";

    public static final String JSON_PROPERTY_SRS_NAME = "srsName";
    private String srsName;

    public static final String JSON_PROPERTY_AXIS_LABELS = "axisLabels";
    private List<String> axisLabels;

    public static final String JSON_PROPERTY_AXIS = "axis";
    private List<Axis> axis;

    public static final String JSON_PROPERTY_GRID_LIMITS = "gridLimits";
    private GridLimits gridLimits;

    public GeneralGrid() {}

    public GeneralGrid(String srsName, List<Axis> axis, GridLimits gridLimits) {
        this.srsName = srsName;
        this.axis = axis;
        this.gridLimits = gridLimits;
        this.axisLabels = getAxisLabels(axis);
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

    @JsonProperty(JSON_PROPERTY_SRS_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_LABELS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<String> getAxisLabels() {
        return axisLabels;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Axis> getAxis() {
        return axis;
    }

    @JsonProperty(JSON_PROPERTY_AXIS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setAxis(List<Axis> axis) {
        this.axis = axis;
        this.axisLabels = getAxisLabels(axis);
    }

    @JsonProperty(JSON_PROPERTY_GRID_LIMITS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public GridLimits getGridLimits() {
        return gridLimits;
    }

    @JsonProperty(JSON_PROPERTY_GRID_LIMITS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setGridLimits(GridLimits gridLimits) {
        this.gridLimits = gridLimits;
    }

    static List<String> getAxisLabels(List<Axis> axis) {
        return axis.stream()
                .map(Axis::getAxisLabel)
                .collect(Collectors.toList());
    }
}
