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

import java.util.List;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        IrregularAxis.JSON_PROPERTY_UOM_LABEL,
        IrregularAxis.JSON_PROPERTY_COORDINATE
})
public class IrregularAxis extends Axis{

    public static final String JSON_PROPERTY_UOM_LABEL = "uomLabel";
    private String uomLabel;

    public static final String JSON_PROPERTY_COORDINATE = "coordinate";
    private List<Object> coordinate;

    public IrregularAxis() {
        super("IrregularAxis", null, null, null);
    }

    public IrregularAxis(String axisLabel, Object lowerBound, Object upperBound, String uomLabel, List<Object> coordinate) {
        super("IrregularAxis", axisLabel, lowerBound, upperBound);
        this.coordinate = coordinate;
        this.uomLabel = uomLabel;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Object> getCoordinate() {
        return coordinate;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCoordinate(List<Object> coordinate) {
        this.coordinate = coordinate;
    }

    @JsonProperty(JSON_PROPERTY_UOM_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getUomLabel() {
        return uomLabel;
    }

    @JsonProperty(JSON_PROPERTY_UOM_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setUomLabel(String uomLabel) {
        this.uomLabel = uomLabel;
    }
}
