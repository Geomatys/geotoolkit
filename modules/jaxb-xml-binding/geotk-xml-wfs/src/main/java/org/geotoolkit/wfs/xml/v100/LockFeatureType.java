/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.*;
import org.apache.sis.util.Version;
import org.geotoolkit.wfs.xml.AllSomeType;
import org.geotoolkit.wfs.xml.LockFeature;


/**
 * This type defines the LockFeature operation.  The LockFeature
 * element contains one or more Lock elements that define
 * which features of a particular type should be locked.  A lock
 * identifier (lockId) is returned to the client application which
 * can be used by subsequent operations to reference the locked
 * features.
 *
 *
 * <p>Java class for LockFeatureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LockFeatureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Lock" type="{http://www.opengis.net/wfs}LockType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WFS" />
 *       &lt;attribute name="expiry" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="lockAction" type="{http://www.opengis.net/wfs}AllSomeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LockFeatureType", propOrder = {
    "lock"
})
public class LockFeatureType implements LockFeature {

    @XmlElement(name = "Lock", required = true)
    private List<LockType> lock;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer expiry;
    @XmlAttribute
    private AllSomeType lockAction;

    @XmlTransient
    private Map<String, String> prefixMapping;

    public LockFeatureType() {

    }

    public LockFeatureType(final String service, final String version, final List<LockType> lock, final Integer expiry, final AllSomeType lockAction) {
        this.service = service;
        this.version = version;
        this.expiry     = expiry;
        this.lock       = lock;
        this.lockAction = lockAction;
    }

    /**
     * Gets the value of the lock property.
     *
    * Objects of the following type(s) are allowed in the list
     * {@link LockType }
     *
     *
     */
    public List<LockType> getLock() {
        if (lock == null) {
            lock = new ArrayList<LockType>();
        }
        return this.lock;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Version getVersion() {
        if (version == null) {
            return new Version("1.0.0");
        } else {
            return new Version(version);
        }
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getService() {
        if (service == null) {
            return "WFS";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setService(String value) {
        this.service = value;
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
        return expiry;
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
        return lockAction;
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

    public String getHandle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setHandle(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, String> getPrefixMapping() {
        return prefixMapping;
    }

    /**
     * @param prefixMapping the prefixMapping to set
     */
    public void setPrefixMapping(Map<String, String> prefixMapping) {
        this.prefixMapping = prefixMapping;
    }
}
