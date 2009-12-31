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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Reports the total number of features affected by some kind of write action (i.e, insert, update, delete).
 *          
 * 
 * <p>Java class for TransactionSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalInserted" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="totalUpdated" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="totalDeleted" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSummaryType", propOrder = {
    "totalInserted",
    "totalUpdated",
    "totalDeleted"
})
public class TransactionSummaryType {

    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer totalInserted;
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer totalUpdated;
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer totalDeleted;

    public TransactionSummaryType() {

    }

    public TransactionSummaryType(Integer totalInserted, Integer totalUpdated, Integer totalDeleted) {
        this.totalDeleted  = totalDeleted;
        this.totalInserted = totalInserted;
        this.totalUpdated  = totalUpdated;
    }

    /**
     * Gets the value of the totalInserted property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalInserted() {
        return totalInserted;
    }

    /**
     * Sets the value of the totalInserted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalInserted(Integer value) {
        this.totalInserted = value;
    }

    /**
     * Gets the value of the totalUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalUpdated() {
        return totalUpdated;
    }

    /**
     * Sets the value of the totalUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalUpdated(Integer value) {
        this.totalUpdated = value;
    }

    /**
     * Gets the value of the totalDeleted property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalDeleted() {
        return totalDeleted;
    }

    /**
     * Sets the value of the totalDeleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalDeleted(Integer value) {
        this.totalDeleted = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TransactionSummaryType]\n");
        if (totalDeleted != null) {
           sb.append("totalDeleted: ").append(totalDeleted).append('\n');
        }
        if (totalInserted != null) {
           sb.append("totalInserted: ").append(totalInserted).append('\n');
        }
        if (totalUpdated != null) {
            sb.append("totalUpdated: ").append(totalUpdated ).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TransactionSummaryType) {
            final TransactionSummaryType that = (TransactionSummaryType) object;
            return Utilities.equals(this.totalDeleted,   that.totalDeleted)  &&
                   Utilities.equals(this.totalInserted,  that.totalInserted) &&
                   Utilities.equals(this.totalUpdated,   that.totalUpdated);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.totalInserted != null ? this.totalInserted.hashCode() : 0);
        hash = 59 * hash + (this.totalUpdated != null ? this.totalUpdated.hashCode() : 0);
        hash = 59 * hash + (this.totalDeleted != null ? this.totalDeleted.hashCode() : 0);
        return hash;
    }
}
