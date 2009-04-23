/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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


/**
 * 
 *             The response for a transaction request that was successfully
 *             completed. If the transaction failed for any reason, a service 
 *             exception report indicating a TransactionFailure is returned
 *             instead.
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
}
