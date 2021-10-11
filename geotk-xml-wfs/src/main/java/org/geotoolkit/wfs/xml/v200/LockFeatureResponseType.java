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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.LockFeatureResponse;


/**
 * <p>Java class for LockFeatureResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LockFeatureResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FeaturesLocked" type="{http://www.opengis.net/wfs/2.0}FeaturesLockedType" minOccurs="0"/>
 *         &lt;element name="FeaturesNotLocked" type="{http://www.opengis.net/wfs/2.0}FeaturesNotLockedType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lockId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LockFeatureResponseType", propOrder = {
    "featuresLocked",
    "featuresNotLocked"
})
public class LockFeatureResponseType implements LockFeatureResponse {

    @XmlElement(name = "FeaturesLocked")
    private FeaturesLockedType featuresLocked;
    @XmlElement(name = "FeaturesNotLocked")
    private FeaturesNotLockedType featuresNotLocked;
    @XmlAttribute
    private String lockId;

    /**
     * Gets the value of the featuresLocked property.
     *
     * @return
     *     possible object is
     *     {@link FeaturesLockedType }
     *
     */
    public FeaturesLockedType getFeaturesLocked() {
        return featuresLocked;
    }

    /**
     * Sets the value of the featuresLocked property.
     *
     * @param value
     *     allowed object is
     *     {@link FeaturesLockedType }
     *
     */
    public void setFeaturesLocked(FeaturesLockedType value) {
        this.featuresLocked = value;
    }

    /**
     * Gets the value of the featuresNotLocked property.
     *
     * @return
     *     possible object is
     *     {@link FeaturesNotLockedType }
     *
     */
    public FeaturesNotLockedType getFeaturesNotLocked() {
        return featuresNotLocked;
    }

    /**
     * Sets the value of the featuresNotLocked property.
     *
     * @param value
     *     allowed object is
     *     {@link FeaturesNotLockedType }
     *
     */
    public void setFeaturesNotLocked(FeaturesNotLockedType value) {
        this.featuresNotLocked = value;
    }

    /**
     * Gets the value of the lockId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLockId() {
        return lockId;
    }

    /**
     * Sets the value of the lockId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLockId(String value) {
        this.lockId = value;
    }

}
