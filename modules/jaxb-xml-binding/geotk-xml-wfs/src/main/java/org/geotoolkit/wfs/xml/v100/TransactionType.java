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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * The TransactionType defines the Transaction operation. 
 * A Transaction element contains one or more Insert, Update Delete and Native elements that allow a client application
 * to create, modify or remove feature instances from the feature repository that a Web Feature Service controls.
 *          
 * 
 * <p>Java class for TransactionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs}LockId" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/wfs}Insert"/>
 *           &lt;element ref="{http://www.opengis.net/wfs}Update"/>
 *           &lt;element ref="{http://www.opengis.net/wfs}Delete"/>
 *           &lt;element ref="{http://www.opengis.net/wfs}Native"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WFS" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="releaseAction" type="{http://www.opengis.net/wfs}AllSomeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionType", propOrder = {
    "lockId",
    "insertOrUpdateOrDelete"
})
public class TransactionType {

    @XmlElement(name = "LockId")
    private String lockId;
    @XmlElements({
        @XmlElement(name = "Native", type = NativeType.class),
        @XmlElement(name = "Delete", type = DeleteElementType.class),
        @XmlElement(name = "Insert", type = InsertElementType.class),
        @XmlElement(name = "Update", type = UpdateElementType.class)
    })
    private List<Object> insertOrUpdateOrDelete;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute
    private String handle;
    @XmlAttribute
    private AllSomeType releaseAction;

    public TransactionType() {

    }

    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final DeleteElementType delete) {
        this.service = service;
        this.version = version;
        this.handle  = handle;
        this.releaseAction = releaseAction;
        this.insertOrUpdateOrDelete = new ArrayList<Object>();
        if (delete != null) {
            this.insertOrUpdateOrDelete.add(delete);
        }
    }
    
    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final InsertElementType insert) {
        this.service = service;
        this.version = version;
        this.handle  = handle;
        this.releaseAction = releaseAction;
        this.insertOrUpdateOrDelete = new ArrayList<Object>();
        if (insert != null) {
            this.insertOrUpdateOrDelete.add(insert);
        }
    }
    
    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final UpdateElementType udpate) {
        this.service = service;
        this.version = version;
        this.handle  = handle;
        this.releaseAction = releaseAction;
        this.insertOrUpdateOrDelete = new ArrayList<Object>();
        if (udpate != null) {
            this.insertOrUpdateOrDelete.add(udpate);
        }
    }
    
    /**
     * In order for a client application to operate upon locked  feature instances, 
     * the Transaction request must include the LockId element. 
     * The content of this element must be the lock identifier the client application obtained from
     * a previous GetFeatureWithLock or LockFeature operation.
     * 
     * If the correct lock identifier is specified the Web Feature Service knows that the client application may
     * operate upon the locked feature instances.
     * 
     * No LockId element needs to be specified to operate upon unlocked features.
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

    /**
     * Gets the value of the insertOrUpdateOrDelete property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insertOrUpdateOrDelete property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsertOrUpdateOrDelete().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NativeType }
     * {@link DeleteElementType }
     * {@link InsertElementType }
     * {@link UpdateElementType }
     * 
     * 
     */
    public List<Object> getInsertOrUpdateOrDelete() {
        if (insertOrUpdateOrDelete == null) {
            insertOrUpdateOrDelete = new ArrayList<Object>();
        }
        return this.insertOrUpdateOrDelete;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
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
     * Gets the value of the handle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandle(String value) {
        this.handle = value;
    }

    /**
     * Gets the value of the releaseAction property.
     * 
     * @return
     *     possible object is
     *     {@link AllSomeType }
     *     
     */
    public AllSomeType getReleaseAction() {
        return releaseAction;
    }

    /**
     * Sets the value of the releaseAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllSomeType }
     *     
     */
    public void setReleaseAction(AllSomeType value) {
        this.releaseAction = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[transactionType]").append('\n');
        if (lockId != null) {
            sb.append("lockId=").append(lockId).append('\n');
        }
        if (releaseAction != null) {
            sb.append("releaseAction=").append(releaseAction).append('\n');
        }
        if (insertOrUpdateOrDelete != null) {
            sb.append("insertOrUpdateOrDelete:").append('\n');
            for (Object obj : insertOrUpdateOrDelete) {
                sb.append(obj).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TransactionType) {
            final TransactionType that = (TransactionType) object;
            return  Utilities.equals(this.insertOrUpdateOrDelete, that.insertOrUpdateOrDelete) &&
                    Utilities.equals(this.lockId, that.lockId) &&
                    Utilities.equals(this.releaseAction, that.releaseAction);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.lockId != null ? this.lockId.hashCode() : 0);
        hash = 37 * hash + (this.insertOrUpdateOrDelete != null ? this.insertOrUpdateOrDelete.hashCode() : 0);
        hash = 37 * hash + (this.releaseAction != null ? this.releaseAction.hashCode() : 0);
        return hash;
    }
}
