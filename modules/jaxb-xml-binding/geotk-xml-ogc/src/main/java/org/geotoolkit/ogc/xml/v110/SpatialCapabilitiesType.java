/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperators;


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
 *         &lt;element name="GeometryOperands" type="{http://www.opengis.net/ogc}GeometryOperandsType"/>
 *         &lt;element name="SpatialOperators" type="{http://www.opengis.net/ogc}SpatialOperatorsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spatial_CapabilitiesType", propOrder = {
    "geometryOperands",
    "spatialOperators"
})
public class SpatialCapabilitiesType implements SpatialCapabilities {

    @XmlElement(name = "GeometryOperands", required = true)
    private GeometryOperandsType geometryOperands;
    @XmlElement(name = "SpatialOperators", required = true)
    private SpatialOperatorsType spatialOperators;

    /**
     * empty constructor used by JAXB
     */
    public SpatialCapabilitiesType() {
        
    }
    
    /**
     * Build a new SpatialCapabilities 
     */
    public SpatialCapabilitiesType(final GeometryOperand[] geometryOperands, final SpatialOperators spatial) {
        this.geometryOperands = new GeometryOperandsType(geometryOperands);
        this.spatialOperators = (SpatialOperatorsType) spatial;
    }

    /**
     * Build a new SpatialCapabilities
     */
    public SpatialCapabilitiesType(final GeometryOperandsType geometryOperands, final SpatialOperators spatial) {
        this.geometryOperands = geometryOperands;
        this.spatialOperators = (SpatialOperatorsType) spatial;
    }

    /**
     * Gets the value of the geometryOperands property.
     */
    public GeometryOperandsType getGeometryOperandsType() {
        return geometryOperands;
    }

    /**
     * Gets the value of the spatialOperators property.
     */
    public SpatialOperatorsType getSpatialOperators() {
        return spatialOperators;
    }

    /**
     * implements SpatialCapabilities geoAPI interface 
     * @return
     */
    public Collection<GeometryOperand> getGeometryOperands() {
        List<GeometryOperand> result = new ArrayList<GeometryOperand>();
        if (geometryOperands != null) {
            for (QName qn: geometryOperands.getGeometryOperand()) {
                result.add(GeometryOperand.get(qn.getNamespaceURI(), qn.getLocalPart()));
            }
        }
        return result;
    }
}
