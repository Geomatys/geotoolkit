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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *             The response for a transaction request that was successfully
 *             completed. If the transaction failed for any reason, an
 *             exception report is returned instead.
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
 *         &lt;element name="TransactionSummary" type="{http://www.opengis.net/wfs}TransactionSummaryType"/>
 *         &lt;element name="TransactionResults" type="{http://www.opengis.net/wfs}TransactionResultsType" minOccurs="0"/>
 *         &lt;element name="InsertResults" type="{http://www.opengis.net/wfs}InsertResultsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.1.0" />
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
    "transactionResults",
    "insertResults"
})
@XmlRootElement(name = "TransactionResponse")
public class TransactionResponseType {

    @XmlElement(name = "TransactionSummary", required = true)
    private TransactionSummaryType transactionSummary;
    @XmlElement(name = "TransactionResults")
    private TransactionResultsType transactionResults;
    @XmlElement(name = "InsertResults", required = true)
    private InsertResultsType insertResults;
    @XmlAttribute(required = true)
    private String version;

    /**
     * Gets the value of the transactionSummary property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionSummaryType }
     *     
     */
    public TransactionSummaryType getTransactionSummary() {
        return transactionSummary;
    }

    /**
     * Sets the value of the transactionSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionSummaryType }
     *     
     */
    public void setTransactionSummary(TransactionSummaryType value) {
        this.transactionSummary = value;
    }

    /**
     * Gets the value of the transactionResults property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionResultsType }
     *     
     */
    public TransactionResultsType getTransactionResults() {
        return transactionResults;
    }

    /**
     * Sets the value of the transactionResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionResultsType }
     *     
     */
    public void setTransactionResults(TransactionResultsType value) {
        this.transactionResults = value;
    }

    /**
     * Gets the value of the insertResults property.
     * 
     * @return
     *     possible object is
     *     {@link InsertResultsType }
     *     
     */
    public InsertResultsType getInsertResults() {
        return insertResults;
    }

    /**
     * Sets the value of the insertResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsertResultsType }
     *     
     */
    public void setInsertResults(InsertResultsType value) {
        this.insertResults = value;
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
            return "1.1.0";
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

}
