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
package org.geotoolkit.csw.xml.v202;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * 
 *          Reports the total number of catalogue items modified by a transaction 
 *          request (i.e, inserted, updated, deleted). If the client did not 
 *          specify a requestId, the server may assign one (a URI value).
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
 *       &lt;attribute name="requestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
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
    private int totalInserted;
    @XmlSchemaType(name = "nonNegativeInteger")
    private int totalUpdated;
    @XmlSchemaType(name = "nonNegativeInteger")
    private int totalDeleted;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String requestId;

    /**
     * An empty constructor used by JAXB
     */
    TransactionSummaryType() {
        
    }
    
    /**
     * Build a new Transation summary.
     */
    public TransactionSummaryType(final int totalInserted, final int totalUpdated, final int totalDeleted, final String requestId) {
        this.requestId     = requestId;
        this.totalDeleted  = totalDeleted;
        this.totalInserted = totalInserted;
        this.totalUpdated  = totalUpdated;
    }
    
    /**
     * Gets the value of the totalInserted property.
     */
    public int getTotalInserted() {
        return totalInserted;
    }

    /**
     * Gets the value of the totalUpdated property.
     */
    public int getTotalUpdated() {
        return totalUpdated;
    }

    /**
     * Gets the value of the totalDeleted property.
     */
    public int getTotalDeleted() {
        return totalDeleted;
    }

    /**
     * Gets the value of the requestId property.
     */
    public String getRequestId() {
        return requestId;
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
            return Utilities.equals(this.requestId,      that.requestId)     &&
                   Utilities.equals(this.totalDeleted,   that.totalDeleted)  &&
                   Utilities.equals(this.totalInserted,  that.totalInserted) &&
                   Utilities.equals(this.totalUpdated,   that.totalUpdated);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.totalInserted;
        hash = 29 * hash + this.totalUpdated;
        hash = 29 * hash + this.totalDeleted;
        hash = 29 * hash + (this.requestId != null ? this.requestId.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[TransactionResponseType]\n");

        if (requestId != null) {
            s.append("requestId: ").append(requestId).append('\n');
        }
        s.append("totalDeleted").append(totalDeleted).append('\n');
        s.append("totalInserted").append(totalInserted).append('\n');
        s.append("totalUpdated").append(totalUpdated).append('\n');
        return s.toString();
    }

}
