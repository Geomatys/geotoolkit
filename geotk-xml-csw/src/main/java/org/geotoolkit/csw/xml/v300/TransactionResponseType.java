/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.TransactionResponse;


/**
 *
 *             The response for a transaction request that was successfully
 *             completed. If the transaction failed for any reason, a service
 *             exception report indicating a TransactionFailure is returned
 *             instead.
 *
 *
 * <p>Classe Java pour TransactionResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TransactionResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionSummary" type="{http://www.opengis.net/cat/csw/3.0}TransactionSummaryType"/>
 *         &lt;element name="InsertResult" type="{http://www.opengis.net/cat/csw/3.0}InsertResultType" maxOccurs="unbounded" minOccurs="0"/>
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
public class TransactionResponseType implements TransactionResponse {

    @XmlElement(name = "TransactionSummary", required = true)
    protected TransactionSummaryType transactionSummary;
    @XmlElement(name = "InsertResult")
    protected List<InsertResultType> insertResult;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * An empty constructor used by JAXB
     */
    TransactionResponseType() {

    }

    /**
     * Build a new response to a transaction
     */
    public TransactionResponseType(final TransactionSummaryType transactionSummary, final List<InsertResultType> insertResult,
            final String version) {
        this.transactionSummary = transactionSummary;
        this.insertResult       = insertResult;
        this.version            = version;
    }

    /**
     * Obtient la valeur de la propriété transactionSummary.
     *
     * @return
     *     possible object is
     *     {@link TransactionSummaryType }
     *
     */
    @Override
    public TransactionSummaryType getTransactionSummary() {
        return transactionSummary;
    }

    /**
     * Définit la valeur de la propriété transactionSummary.
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
     * Gets the value of the insertResult property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insertResult property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsertResult().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsertResultType }
     *
     *
     */
    public List<InsertResultType> getInsertResult() {
        if (insertResult == null) {
            insertResult = new ArrayList<InsertResultType>();
        }
        return this.insertResult;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
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
