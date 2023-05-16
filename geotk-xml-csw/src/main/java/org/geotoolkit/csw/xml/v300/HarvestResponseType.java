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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.HarvestResponse;


/**
 * <p>Classe Java pour HarvestResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="HarvestResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/3.0}Acknowledgement"/>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/3.0}TransactionResponse"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HarvestResponseType", propOrder = {
    "acknowledgement",
    "transactionResponse"
})
public class HarvestResponseType implements HarvestResponse {

    @XmlElement(name = "Acknowledgement")
    protected AcknowledgementType acknowledgement;
    @XmlElement(name = "TransactionResponse")
    protected TransactionResponseType transactionResponse;

    /**
     * An empty constructor used by JAXB
     */
    HarvestResponseType() {

    }

    /**
     * Build a new Response to an harvest request (synchronous mode)
     */
    public HarvestResponseType(final TransactionResponseType transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    /**
     * Build a new Response to an harvest request (synchronous mode)
     */
    public HarvestResponseType(final AcknowledgementType acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

    /**
     * Obtient la valeur de la propriété acknowledgement.
     */
    public AcknowledgementType getAcknowledgement() {
        return acknowledgement;
    }

    /**
     * Définit la valeur de la propriété acknowledgement.
     */
    public void setAcknowledgement(AcknowledgementType value) {
        this.acknowledgement = value;
    }

    /**
     * Obtient la valeur de la propriété transactionResponse.
     */
    public TransactionResponseType getTransactionResponse() {
        return transactionResponse;
    }

    /**
     * Définit la valeur de la propriété transactionResponse.
     */
    public void setTransactionResponse(TransactionResponseType value) {
        this.transactionResponse = value;
    }
}
