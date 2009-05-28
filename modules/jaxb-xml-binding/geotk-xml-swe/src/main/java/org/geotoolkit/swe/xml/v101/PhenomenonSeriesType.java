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
package org.geotoolkit.swe.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A phenomenon defined as a base property convolved with a set of constraints
 *       The set of constraints may be either
 *       * an explicit set of soft-typed measures, intervals and categories
 *       * one or more lists of soft-typed measures, intervals and categories
 *       * one or more sequences of soft-typed measures and intervals
 * 
 * <p>Java class for PhenomenonSeriesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PhenomenonSeriesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}CompoundPhenomenonType">
 *       &lt;sequence>
 *         &lt;element name="base" type="{http://www.opengis.net/swe/1.0.1}PhenomenonPropertyType"/>
 *         &lt;element name="constraintList" type="{http://www.opengis.net/swe/1.0.1}DataArrayPropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="otherConstraint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhenomenonSeriesType", propOrder = {
    "base",
    "constraintList",
    "otherConstraint"
})
public class PhenomenonSeriesType extends CompoundPhenomenonEntry {

    @XmlElement(required = true)
    private PhenomenonPropertyType base;
    @XmlElement(required = true)
    private List<DataArrayPropertyType> constraintList;
    private List<String> otherConstraint;

    /**
     * Gets the value of the base property.
     */
    public PhenomenonPropertyType getBase() {
        return base;
    }

    /**
     * Sets the value of the base property.
     */
    public void setBase(PhenomenonPropertyType value) {
        this.base = value;
    }

    /**
     * Gets the value of the constraintList property.
     */
    public List<DataArrayPropertyType> getConstraintList() {
        if (constraintList == null) {
            constraintList = new ArrayList<DataArrayPropertyType>();
        }
        return this.constraintList;
    }

    /**
     * Gets the value of the otherConstraint property.
     */
    public List<String> getOtherConstraint() {
        if (otherConstraint == null) {
            otherConstraint = new ArrayList<String>();
        }
        return this.otherConstraint;
    }

}
