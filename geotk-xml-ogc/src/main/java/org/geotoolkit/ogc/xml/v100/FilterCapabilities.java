/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;
import org.geotoolkit.filter.capability.TemporalCapabilities;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spatial_Capabilities" type="{http://www.opengis.net/ogc}Spatial_CapabilitiesType"/>
 *         &lt;element name="Scalar_Capabilities" type="{http://www.opengis.net/ogc}Scalar_CapabilitiesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "spatialCapabilities",
    "scalarCapabilities"
})
@XmlRootElement(name = "Filter_Capabilities")
public class FilterCapabilities extends org.geotoolkit.filter.capability.FilterCapabilities {

    @XmlElement(name = "Spatial_Capabilities", required = true)
    private SpatialCapabilitiesType spatialCapabilities;
    @XmlElement(name = "Scalar_Capabilities", required = true)
    private ScalarCapabilitiesType scalarCapabilities;

    public FilterCapabilities() {
    }

    public FilterCapabilities(final SpatialCapabilitiesType spatialCapabilities, final ScalarCapabilitiesType scalarCapabilities) {
        this.scalarCapabilities = scalarCapabilities;
        this.spatialCapabilities = spatialCapabilities;
    }

    /**
     * Gets the value of the spatialCapabilities property.
     */
    @Override
    public SpatialCapabilitiesType getSpatialCapabilities() {
        return spatialCapabilities;
    }

    /**
     * Sets the value of the spatialCapabilities property.
     */
    public void setSpatialCapabilities(SpatialCapabilitiesType value) {
        this.spatialCapabilities = value;
    }

    /**
     * Gets the value of the scalarCapabilities property.
     */
    @Override
    public ScalarCapabilitiesType getScalarCapabilities() {
        return scalarCapabilities;
    }

    /**
     * Sets the value of the scalarCapabilities property.
     */
    public void setScalarCapabilities(ScalarCapabilitiesType value) {
        this.scalarCapabilities = value;
    }

    @Override
    public TemporalCapabilities getTemporalCapabilities() {
        return null;
    }

    @Override
    public DefaultIdCapabilities getIdCapabilities() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
