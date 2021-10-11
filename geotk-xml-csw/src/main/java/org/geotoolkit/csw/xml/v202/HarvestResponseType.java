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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.HarvestResponse;


/**
 * <p>Java class for HarvestResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="HarvestResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}Acknowledgement"/>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}TransactionResponse"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HarvestResponseType", propOrder = {
    "acknowledgement",
    "transactionResponse"
})
@XmlRootElement(name = "HarvestResponse")
public class HarvestResponseType implements HarvestResponse {

    @XmlElement(name = "Acknowledgement")
    private AcknowledgementType acknowledgement;
    @XmlElement(name = "TransactionResponse")
    private TransactionResponseType transactionResponse;

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
     * Gets the value of the acknowledgement property.
     */
    public AcknowledgementType getAcknowledgement() {
        return acknowledgement;
    }

    /**
     * Gets the value of the transactionResponse property.
     */
    public TransactionResponseType getTransactionResponse() {
        return transactionResponse;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof HarvestResponseType) {
            final HarvestResponseType that = (HarvestResponseType) object;
            return Objects.equals(this.acknowledgement,      that.acknowledgement)   &&
                   Objects.equals(this.transactionResponse,  that.transactionResponse);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.acknowledgement != null ? this.acknowledgement.hashCode() : 0);
        hash = 37 * hash + (this.transactionResponse != null ? this.transactionResponse.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[HarvestResponse]\n");

        if (acknowledgement != null) {
            s.append("acknowledgement: ").append(acknowledgement).append('\n');
        }
        if (transactionResponse != null) {
            s.append("transactionResponse: ").append(transactionResponse).append('\n');
        }
        return s.toString();
    }
}
