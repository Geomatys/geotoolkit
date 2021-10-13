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


package org.geotoolkit.wfs.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.AllSomeType;


/**
 * <p>Java class for GetFeatureWithLockType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetFeatureWithLockType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}GetFeatureType">
 *       &lt;attribute name="expiry" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="300" />
 *       &lt;attribute name="lockAction" type="{http://www.opengis.net/wfs/2.0}AllSomeType" default="ALL" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureWithLockType")
public class GetFeatureWithLockType extends GetFeatureType {

    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private int expiry = 300;
    @XmlAttribute
    private AllSomeType lockAction;

    /**
     * Gets the value of the expiry property.
     *
     * @return
     *     possible object is
     *     {@link int }
     *
     */
    public int getExpiry() {
        return expiry;
    }

    /**
     * Sets the value of the expiry property.
     *
     * @param value
     *     allowed object is
     *     {@link int }
     *
     */
    public void setExpiry(int value) {
        this.expiry = value;
    }

    /**
     * Gets the value of the lockAction property.
     *
     * @return
     *     possible object is
     *     {@link AllSomeType }
     *
     */
    public AllSomeType getLockAction() {
        if (lockAction == null) {
            return AllSomeType.ALL;
        } else {
            return lockAction;
        }
    }

    /**
     * Sets the value of the lockAction property.
     *
     * @param value
     *     allowed object is
     *     {@link AllSomeType }
     *
     */
    public void setLockAction(AllSomeType value) {
        this.lockAction = value;
    }

}
