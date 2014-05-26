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
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.filter.capability.TemporalOperand;
import org.opengis.filter.capability.TemporalOperators;


/**
 * <p>Java class for Temporal_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Temporal_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperands" type="{http://www.opengis.net/fes/2.0}TemporalOperandsType"/>
 *         &lt;element name="TemporalOperators" type="{http://www.opengis.net/fes/2.0}TemporalOperatorsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Temporal_CapabilitiesType", propOrder = {
    "temporalOperands",
    "temporalOperators"
})
public class TemporalCapabilitiesType implements TemporalCapabilities {

    @XmlElement(name = "TemporalOperands", required = true)
    private TemporalOperandsType temporalOperands;
    @XmlElement(name = "TemporalOperators", required = true)
    private TemporalOperatorsType temporalOperators;

    /**
     * empty constructor used by JAXB
     */
    public TemporalCapabilitiesType() {

    }

    /**
     * Build a new SpatialCapabilities
     */
    public TemporalCapabilitiesType(final TemporalOperand[] geometryOperands, final TemporalOperators spatial) {
        this.temporalOperands = new TemporalOperandsType(geometryOperands);
        this.temporalOperators = (TemporalOperatorsType) spatial;
    }

    /**
     * Gets the value of the temporalOperands property.
     *
     * @return
     *     possible object is
     *     {@link TemporalOperandsType }
     *
     */
    public TemporalOperandsType getTemporalOperandsType() {
        return temporalOperands;
    }

    /**
     * Sets the value of the temporalOperands property.
     *
     * @param value
     *     allowed object is
     *     {@link TemporalOperandsType }
     *
     */
    public void setTemporalOperands(TemporalOperandsType value) {
        this.temporalOperands = value;
    }

    /**
     * Gets the value of the temporalOperators property.
     *
     * @return
     *     possible object is
     *     {@link TemporalOperatorsType }
     *
     */
    @Override
    public TemporalOperatorsType getTemporalOperators() {
        return temporalOperators;
    }

    /**
     * Sets the value of the temporalOperators property.
     *
     * @param value
     *     allowed object is
     *     {@link TemporalOperatorsType }
     *
     */
    public void setTemporalOperators(TemporalOperatorsType value) {
        this.temporalOperators = value;
    }

    /**
     * implements SpatialCapabilities geoAPI interface
     * @return
     */
    @Override
    public Collection<TemporalOperand> getTemporalOperands() {
        List<TemporalOperand> result = new ArrayList<TemporalOperand>();
        if (temporalOperands != null) {
            for (TemporalOperandsType.TemporalOperand qn: temporalOperands.getTemporalOperand()) {
                result.add(TemporalOperand.valueOf(/*qn.getName().getNamespaceURI(),*/ qn.getName().getLocalPart()));
            }
        }
        return result;
    }

}
