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

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        RegularAxis.JSON_PROPERTY_UOM_LABEL,
        RegularAxis.JSON_PROPERTY_RESOLUTION
})
public class RegularAxis extends Axis{

    public static final String JSON_PROPERTY_UOM_LABEL = "uomLabel";
    private String uomLabel;

    public static final String JSON_PROPERTY_RESOLUTION = "resolution";
    private double resolution;

    public RegularAxis() {
        super("RegularAxis", null, null, null);
    }

    public RegularAxis(String axisLabel, Object lowerBound, Object upperBound, String uomLabel, double resolution) {
        super("RegularAxis", axisLabel, lowerBound, upperBound);
        this.resolution = resolution;
        this.uomLabel = uomLabel;
    }

    @JsonProperty(JSON_PROPERTY_RESOLUTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public double getResolution() {
        return resolution;
    }

    @JsonProperty(JSON_PROPERTY_RESOLUTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setResolution(double resolution) {
        this.resolution = resolution;
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
