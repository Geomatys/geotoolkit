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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.FeatureIdType;
import org.geotoolkit.wfs.xml.TransactionResponse;
import org.geotoolkit.wfs.xml.WFSResponse;
import org.opengis.filter.ResourceId;


/**
 * The response for a transaction request that was successfully completed.
 * If the transaction failed for any reason, an exception report is returned instead.
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionResponseType", propOrder = {
    "transactionSummary",
    "transactionResults",
    "insertResults"
})
@XmlRootElement(name = "TransactionResponse")
public class TransactionResponseType implements WFSResponse, TransactionResponse {

    @XmlElement(name = "TransactionSummary", required = true)
    private TransactionSummaryType transactionSummary;
    @XmlElement(name = "TransactionResults")
    private TransactionResultsType transactionResults;
    @XmlElement(name = "InsertResults", required = true)
    private InsertResultsType insertResults;
    @XmlAttribute(required = true)
    private String version;

    public TransactionResponseType() {

    }

    public TransactionResponseType(final TransactionSummaryType transactionSummary, final TransactionResultsType transactionResults, final InsertResultsType insertResults, final String version) {
        this.transactionSummary = transactionSummary;
        this.transactionResults = transactionResults;
        this.insertResults      = insertResults;
        this.version            = version;
    }

    /**
     * Gets the value of the transactionSummary property.
     */
    public TransactionSummaryType getTransactionSummary() {
        return transactionSummary;
    }

    /**
     * Sets the value of the transactionSummary property.
     */
    public void setTransactionSummary(final TransactionSummaryType value) {
        this.transactionSummary = value;
    }

    /**
     * Gets the value of the transactionResults property.
     */
    public TransactionResultsType getTransactionResults() {
        return transactionResults;
    }

    /**
     * Sets the value of the transactionResults property.
     */
    public void setTransactionResults(final TransactionResultsType value) {
        this.transactionResults = value;
    }

    /**
     * Gets the value of the insertResults property.
     */
    public InsertResultsType getInsertResults() {
        return insertResults;
    }

    /**
     * Sets the value of the insertResults property.
     */
    public void setInsertResults(final InsertResultsType value) {
        this.insertResults = value;
    }

    /**
     * Gets the value of the version property.
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
     */
    public void setVersion(final String value) {
        this.version = value;
    }

    @Override
    public List<ResourceId> getInsertedFID() {
        final List<ResourceId> ids = new ArrayList<ResourceId>();
        if (insertResults != null) {
            final List<InsertedFeatureType> inserted = insertResults.getFeature();
            if (inserted != null) {
                for(InsertedFeatureType ift : inserted){
                    for(FeatureIdType fit : ift.getFeatureId()){
                        ids.add(fit);
                    }
                }
            }
        }
        return ids;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TransactionResponseType]\n");
        if (version != null) {
           sb.append("version: ").append(version).append('\n');
        }
        if (transactionSummary != null) {
           sb.append("transactionSummary: ").append(transactionSummary).append('\n');
        }
        if (transactionResults != null) {
            sb.append("transactionResults: ").append(transactionResults).append('\n');
        }
        if (insertResults != null) {
            sb.append("insertResults: ").append(insertResults).append('\n');
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
        if (object instanceof TransactionResponseType) {
            final TransactionResponseType that = (TransactionResponseType) object;
            return Objects.equals(this.insertResults,      that.insertResults)        &&
                   Objects.equals(this.transactionResults, that.transactionResults)   &&
                   Objects.equals(this.transactionSummary, that.transactionSummary)   &&
                   Objects.equals(this.version,            that.version) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.transactionSummary != null ? this.transactionSummary.hashCode() : 0);
        hash = 19 * hash + (this.transactionResults != null ? this.transactionResults.hashCode() : 0);
        hash = 19 * hash + (this.insertResults != null ? this.insertResults.hashCode() : 0);
        hash = 19 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }
}
