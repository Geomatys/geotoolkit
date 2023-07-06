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

import java.util.Collection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.filter.capability.GeometryOperand;
import org.geotoolkit.filter.capability.SpatialCapabilities;
import org.geotoolkit.filter.capability.SpatialOperators;


/**
 * <p>Java class for Spatial_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Spatial_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spatial_Operators" type="{http://www.opengis.net/ogc}Spatial_OperatorsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spatial_CapabilitiesType", propOrder = {
    "spatialOperators"
})
public class SpatialCapabilitiesType extends SpatialCapabilities {

    @XmlElement(name = "Spatial_Operators", required = true)
    private SpatialOperatorsType spatialOperators;

    public SpatialCapabilitiesType() {
    }

    public SpatialCapabilitiesType(final SpatialOperators spatialOperators) {
        this.spatialOperators = (SpatialOperatorsType) spatialOperators;
    }

    /**
     * Gets the value of the spatialOperators property.
     */
    @Override
    public SpatialOperatorsType getSpatialOperators() {
        return spatialOperators;
    }

    /**
     * Sets the value of the spatialOperators property.
     */
    public void setSpatialOperators(SpatialOperatorsType value) {
        this.spatialOperators = value;
    }

    @Override
    public Collection<GeometryOperand> getGeometryOperands() {
        return null;
    }
}
