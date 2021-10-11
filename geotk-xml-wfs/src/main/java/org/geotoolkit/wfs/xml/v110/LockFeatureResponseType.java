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
package org.geotoolkit.wfs.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.LockFeatureResponse;
import org.geotoolkit.wfs.xml.WFSResponse;


/**
 * The LockFeatureResponseType is used to define an element to contains the response to a LockFeature operation.
 *
 *
 * <p>Java class for LockFeatureResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LockFeatureResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs}LockId"/>
 *         &lt;element name="FeaturesLocked" type="{http://www.opengis.net/wfs}FeaturesLockedType" minOccurs="0"/>
 *         &lt;element name="FeaturesNotLocked" type="{http://www.opengis.net/wfs}FeaturesNotLockedType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LockFeatureResponseType", propOrder = {
    "lockId",
    "featuresLocked",
    "featuresNotLocked"
})
@XmlRootElement(name = "LockFeatureResponse")
public class LockFeatureResponseType implements WFSResponse, LockFeatureResponse {

    @XmlElement(name = "LockId", required = true)
    private String lockId;
    @XmlElement(name = "FeaturesLocked")
    private FeaturesLockedType featuresLocked;
    @XmlElement(name = "FeaturesNotLocked")
    private FeaturesNotLockedType featuresNotLocked;

    @Override
    public String getVersion() {
        return "1.1.0";
    }

    /**
     * The LockFeatureResponse includes a LockId element that contains a lock identifier.
     * The lock identifier can be used by a client,
     * in subsequent operations, to operate upon the locked feature instances.
     *
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
     * The LockFeatureResponse includes a LockId element that contains a lock identifier.
     * The lock identifier can be used by a client,
     * in subsequent operations, to operate upon the locked feature instances.
     *
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLockId(final String value) {
        this.lockId = value;
    }

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
    public void setFeaturesLocked(final FeaturesLockedType value) {
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
    public void setFeaturesNotLocked(final FeaturesNotLockedType value) {
        this.featuresNotLocked = value;
    }

}
