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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
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
 *         &lt;element name="totalReplaced" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="totalDeleted" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSummaryType", propOrder = {
    "totalInserted",
    "totalUpdated",
    "totalReplaced",
    "totalDeleted"
})
public class TransactionSummaryType {

    @XmlSchemaType(name = "nonNegativeInteger")
    private int totalInserted;
    @XmlSchemaType(name = "nonNegativeInteger")
    private int totalUpdated;
    @XmlSchemaType(name = "nonNegativeInteger")
    private int totalReplaced;
    @XmlSchemaType(name = "nonNegativeInteger")
    private int totalDeleted;

    public TransactionSummaryType() {

    }

    public TransactionSummaryType(final Integer totalInserted, final Integer totalUpdated, final Integer totalDeleted, final Integer totalReplaced) {
        this.totalDeleted  = totalDeleted;
        this.totalInserted = totalInserted;
        this.totalUpdated  = totalUpdated;
        this.totalReplaced = totalReplaced;
    }

    /**
     * Gets the value of the totalInserted property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getTotalInserted() {
        return totalInserted;
    }

    /**
     * Sets the value of the totalInserted property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setTotalInserted(int value) {
        this.totalInserted = value;
    }

    /**
     * Gets the value of the totalUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getTotalUpdated() {
        return totalUpdated;
    }

    /**
     * Sets the value of the totalUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setTotalUpdated(int value) {
        this.totalUpdated = value;
    }

    /**
     * Gets the value of the totalReplaced property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getTotalReplaced() {
        return totalReplaced;
    }

    /**
     * Sets the value of the totalReplaced property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setTotalReplaced(int value) {
        this.totalReplaced = value;
    }

    /**
     * Gets the value of the totalDeleted property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getTotalDeleted() {
        return totalDeleted;
    }

    /**
     * Sets the value of the totalDeleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setTotalDeleted(int value) {
        this.totalDeleted = value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[TransactionSummaryType]\n");
        sb.append("totalDeleted: ").append(totalDeleted).append('\n');
        sb.append("totalInserted: ").append(totalInserted).append('\n');
        sb.append("totalUpdated: ").append(totalUpdated).append('\n');
        sb.append("totalReplaced: ").append(totalReplaced).append('\n');
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
                   Utilities.equals(this.totalReplaced,  that.totalReplaced) &&
                   Utilities.equals(this.totalUpdated,   that.totalUpdated);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.totalInserted;
        hash = 59 * hash + this.totalUpdated;
        hash = 59 * hash + this.totalDeleted;
        hash = 59 * hash + this.totalReplaced;
        return hash;
    }

}
