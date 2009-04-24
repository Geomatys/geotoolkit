/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
 * A scalar Phenomenon is defined by adding constraints to an existing property.
 * 
 * <p>Java class for ConstrainedPhenomenonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConstrainedPhenomenonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}PhenomenonType">
 *       &lt;sequence>
 *         &lt;element name="base" type="{http://www.opengis.net/swe/1.0.1}PhenomenonPropertyType"/>
 *         &lt;element name="otherConstraint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="singleConstraint" type="{http://www.opengis.net/swe/1.0.1}AnyDataPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConstrainedPhenomenonType", propOrder = {
    "base",
    "otherConstraint",
    "singleConstraint"
})
public class ConstrainedPhenomenonType extends PhenomenonEntry {

    @XmlElement(required = true)
    private PhenomenonPropertyType base;
    private List<String> otherConstraint;
    private List<AnyDataPropertyType> singleConstraint;

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
     * Gets the value of the otherConstraint property.
     * 
     */
    public List<String> getOtherConstraint() {
        if (otherConstraint == null) {
            otherConstraint = new ArrayList<String>();
        }
        return this.otherConstraint;
    }

    /**
     * Gets the value of the singleConstraint property.
     */
    public List<AnyDataPropertyType> getSingleConstraint() {
        if (singleConstraint == null) {
            singleConstraint = new ArrayList<AnyDataPropertyType>();
        }
        return this.singleConstraint;
    }

}
