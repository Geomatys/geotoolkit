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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.TransactionSummary;


/**
 *
 *             Reports the total number of catalogue items modified by a
 *             transaction request (i.e, inserted, updated, deleted).
 *             If the client did not specify a requestId, the server may
 *             assign one (a URI value).
 *
 *
 * <p>Classe Java pour TransactionSummaryType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSummaryType", propOrder = {
    "totalInserted",
    "totalUpdated",
    "totalDeleted"
})
public class TransactionSummaryType implements TransactionSummary {

    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer totalInserted;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer totalUpdated;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer totalDeleted;
    @XmlAttribute(name = "requestId")
    @XmlSchemaType(name = "anyURI")
    protected String requestId;

    /**
     * An empty constructor used by JAXB
     */
    TransactionSummaryType() {

    }

    /**
     * Build a new Transation summary.
     */
    public TransactionSummaryType(final int totalInserted, final int totalUpdated, final int totalDeleted, final String requestId) {
        this.requestId = requestId;
        this.totalDeleted = totalDeleted;
        this.totalInserted = totalInserted;
        this.totalUpdated = totalUpdated;
    }

    /**
     * Obtient la valeur de la propriété totalInserted.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public int getTotalInserted() {
        if (totalInserted == null) {
            return 0;
        }
        return totalInserted;
    }

    /**
     * Définit la valeur de la propriété totalInserted.
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
     * Obtient la valeur de la propriété totalUpdated.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public int getTotalUpdated() {
        if (totalUpdated == null) {
            return 0;
        }
        return totalUpdated;
    }

    /**
     * Définit la valeur de la propriété totalUpdated.
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
     * Obtient la valeur de la propriété totalDeleted.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public int getTotalDeleted() {
        if (totalDeleted == null) {
            return 0;
        }
        return totalDeleted;
    }

    /**
     * Définit la valeur de la propriété totalDeleted.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setTotalDeleted(Integer value) {
        this.totalDeleted = value;
    }

    /**
     * Obtient la valeur de la propriété requestId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRequestId() {
        return requestId;
    }

    /**
     * Définit la valeur de la propriété requestId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

}
