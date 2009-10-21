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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * This type defines the LockFeature operation.
 * The LockFeature element contains one or more Lock elements that define which
 * features of a particular type should be locked.  
 * A lock identifier (lockId) is returned to the client application which
 * can be used by subsequent operations to reference the locked features.
 *          
 * 
 * <p>Java class for LockFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LockFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element name="Lock" type="{http://www.opengis.net/wfs}LockType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="expiry" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="5" />
 *       &lt;attribute name="lockAction" type="{http://www.opengis.net/wfs}AllSomeType" default="ALL" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LockFeatureType", propOrder = {
    "lock"
})
@XmlRootElement(name = "LockFeature")
public class LockFeatureType extends BaseRequestType {

    @XmlElement(name = "Lock", required = true)
    private List<LockType> lock;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer expiry;
    @XmlAttribute
    private AllSomeType lockAction;

    public LockFeatureType() {

    }

    public LockFeatureType(String service, String version, String handle, List<LockType> lock, Integer expiry, AllSomeType lockAction) {
        super(service, version, handle);
        this.expiry     = expiry;
        this.lock       = lock;
        this.lockAction = lockAction;
    }
    
    /**
     * Gets the value of the lock property.
     */
    public List<LockType> getLock() {
        if (lock == null) {
            lock = new ArrayList<LockType>();
        }
        return this.lock;
    }

    /**
     * Gets the value of the expiry property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExpiry() {
        if (expiry == null) {
            return new Integer("5");
        } else {
            return expiry;
        }
    }

    /**
     * Sets the value of the expiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExpiry(Integer value) {
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
