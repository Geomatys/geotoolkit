/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

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
 *         &lt;element name="GeometryOperands" type="{http://www.opengis.net/fes/2.0}GeometryOperandsType"/>
 *         &lt;element name="SpatialOperators" type="{http://www.opengis.net/fes/2.0}SpatialOperatorsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
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
     *
     * @return
     *     possible object is
     *     {@link GeometryOperandsType }
     *
     */
    public GeometryOperandsType getGeometryOperandsType() {
        return geometryOperands;
    }

    /**
     * Sets the value of the geometryOperands property.
     *
     * @param value
     *     allowed object is
     *     {@link GeometryOperandsType }
     *
     */
    public void setGeometryOperands(GeometryOperandsType value) {
        this.geometryOperands = value;
    }

    /**
     * implements SpatialCapabilities geoAPI interface
     * @return
     */
    public Collection<GeometryOperand> getGeometryOperands() {
        List<GeometryOperand> result = new ArrayList<GeometryOperand>();
        if (geometryOperands != null) {
            for (GeometryOperandsType.GeometryOperand qn: geometryOperands.getGeometryOperand()) {
                result.add(GeometryOperand.valueOf(/*qn.getName().getNamespaceURI(),*/ qn.getName().getLocalPart()));
            }
        }
        return result;
    }

    /**
     * Gets the value of the spatialOperators property.
     *
     * @return
     *     possible object is
     *     {@link SpatialOperatorsType }
     *
     */
    public SpatialOperatorsType getSpatialOperators() {
        return spatialOperators;
    }

    /**
     * Sets the value of the spatialOperators property.
     *
     * @param value
     *     allowed object is
     *     {@link SpatialOperatorsType }
     *
     */
    public void setSpatialOperators(SpatialOperatorsType value) {
        this.spatialOperators = value;
    }

}
