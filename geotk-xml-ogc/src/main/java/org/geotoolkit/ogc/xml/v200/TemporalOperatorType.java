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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.TemporalOperand;
import org.geotoolkit.filter.capability.TemporalOperator;


/**
 * <p>Java class for TemporalOperatorType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TemporalOperatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperands" type="{http://www.opengis.net/fes/2.0}TemporalOperandsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.opengis.net/fes/2.0}TemporalOperatorNameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalOperatorType", propOrder = {
    "temporalOperands"
})
public class TemporalOperatorType extends TemporalOperator {

    @XmlElement(name = "TemporalOperands")
    private TemporalOperandsType temporalOperands;
    @XmlAttribute(required = true)
    private String name;

    public TemporalOperatorType() {
        super("");
    }

    public TemporalOperatorType(final String name) {
        super(name);
        this.name = name;
    }

    /**
     * Gets the value of the temporalOperands property.
     */
    public TemporalOperandsType getTemporalOperandsType() {
        return temporalOperands;
    }

    /**
     * Sets the value of the temporalOperands property.
     */
    public void setTemporalOperands(TemporalOperandsType value) {
        this.temporalOperands = value;
    }

    /**
     * Gets the value of the name property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Implements SpatialOperator geoAPI interface
     */
    public Collection<TemporalOperand> getTemporalOperands2() {
        List<TemporalOperand> result = new ArrayList<>();
        if (temporalOperands != null) {
            for (TemporalOperandsType.TemporalOperand qn: temporalOperands.getTemporalOperand()) {
                result.add(TemporalOperand.valueOf(/*qn.getName().getNamespaceURI(),*/ qn.getName().getLocalPart()));
            }
        }
        return result;
    }
}
