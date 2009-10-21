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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * The response for a transaction request that was successfully completed.
 * If the transaction failed for any reason, a service exception report
 * indicating a TransactionFailure is returned instead.
 *          
 * 
 * <p>Java class for TransactionResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionSummary" type="{http://www.opengis.net/cat/csw/2.0.2}TransactionSummaryType"/>
 *         &lt;element name="InsertResult" type="{http://www.opengis.net/cat/csw/2.0.2}InsertResultType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionResponseType", propOrder = {
    "transactionSummary",
    "insertResult"
})
@XmlRootElement(name = "TransactionResponse")
public class TransactionResponseType {

    @XmlElement(name = "TransactionSummary", required = true)
    private TransactionSummaryType transactionSummary;
    @XmlElement(name = "InsertResult")
    private List<InsertResultType> insertResult;
    @XmlAttribute
    private String version;

    /**
     * An empty constructor used by JAXB
     */
    TransactionResponseType() {
        
    }
    
    /**
     * Build a new response to a transaction
     */
    public TransactionResponseType(TransactionSummaryType transactionSummary, List<InsertResultType> insertResult,
            String version) {
        this.transactionSummary = transactionSummary;
        this.insertResult       = insertResult;
        this.version            = version;
    }
    
    /**
     * Gets the value of the transactionSummary property.
     */
    public TransactionSummaryType getTransactionSummary() {
        return transactionSummary;
    }

    /**
     * Gets the value of the insertResult property.
     * (unmodifiable)
     */
    public List<InsertResultType> getInsertResult() {
        if (insertResult == null) {
            insertResult = new ArrayList<InsertResultType>();
        }
        return Collections.unmodifiableList(insertResult);
    }

    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TransactionResponseType) {
            final TransactionResponseType that = (TransactionResponseType) object;
            return Utilities.equals(this.insertResult,       that.insertResult)       &&
                   Utilities.equals(this.transactionSummary, that.transactionSummary) &&
                   Utilities.equals(this.version,            that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.transactionSummary != null ? this.transactionSummary.hashCode() : 0);
        hash = 37 * hash + (this.insertResult != null ? this.insertResult.hashCode() : 0);
        hash = 37 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[TransactionResponseType]\n");

        if (insertResult != null) {
            s.append(insertResult.size()).append(" insertResult: ").append('\n');
            for (InsertResultType ins : insertResult) {
                s.append(ins).append('\n');
            }
        }
        if (transactionSummary != null) {
            s.append("transactionSummary: ").append(transactionSummary).append('\n');
        }
        if (version != null) {
            s.append("version:").append(version).append('n');
        }
        return s.toString();
    }
}
