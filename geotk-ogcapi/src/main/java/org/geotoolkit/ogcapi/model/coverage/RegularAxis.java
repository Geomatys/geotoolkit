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

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        RegularAxis.JSON_PROPERTY_UOM_LABEL,
        RegularAxis.JSON_PROPERTY_RESOLUTION
})
@XmlRootElement(name = "RegularAxis")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "RegularAxis")
public class RegularAxis extends Axis{

    public static final String JSON_PROPERTY_UOM_LABEL = "uomLabel";
    @XmlElement(name = "uomLabel")
    private String uomLabel;

    public static final String JSON_PROPERTY_RESOLUTION = "resolution";
    @XmlElement(name = "resolution")
    private double resolution;

    public RegularAxis() {
        super("RegularAxis", null, null, null);
    }

    public RegularAxis(String axisLabel, Object lowerBound, Object upperBound, String uomLabel, double resolution) {
        super("RegularAxis", axisLabel, lowerBound, upperBound);
        this.resolution = resolution;
        this.uomLabel = uomLabel;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_RESOLUTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "resolution")
    public double getResolution() {
        return resolution;
    }

    @JsonProperty(JSON_PROPERTY_RESOLUTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "resolution")
    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_UOM_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uomLabel")
    public String getUomLabel() {
        return uomLabel;
    }

    @JsonProperty(JSON_PROPERTY_UOM_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uomLabel")
    public void setUomLabel(String uomLabel) {
        this.uomLabel = uomLabel;
    }
}
