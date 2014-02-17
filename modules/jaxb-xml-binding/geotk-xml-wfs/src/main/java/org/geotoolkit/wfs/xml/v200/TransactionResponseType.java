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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.ResourceIdType;
import org.geotoolkit.wfs.xml.TransactionResponse;
import org.geotoolkit.wfs.xml.WFSResponse;
import org.opengis.filter.identity.FeatureId;


/**
 * <p>Java class for TransactionResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TransactionResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionSummary" type="{http://www.opengis.net/wfs/2.0}TransactionSummaryType"/>
 *         &lt;element name="InsertResults" type="{http://www.opengis.net/wfs/2.0}ActionResultsType" minOccurs="0"/>
 *         &lt;element name="UpdateResults" type="{http://www.opengis.net/wfs/2.0}ActionResultsType" minOccurs="0"/>
 *         &lt;element name="ReplaceResults" type="{http://www.opengis.net/wfs/2.0}ActionResultsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="2.0.0" />
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
    "insertResults",
    "updateResults",
    "replaceResults"
})
@XmlRootElement(name="TransactionResponse", namespace="http://www.opengis.net/wfs/2.0")
public class TransactionResponseType implements WFSResponse, TransactionResponse {

    @XmlElement(name = "TransactionSummary", required = true)
    private TransactionSummaryType transactionSummary;
    @XmlElement(name = "InsertResults")
    private ActionResultsType insertResults;
    @XmlElement(name = "UpdateResults")
    private ActionResultsType updateResults;
    @XmlElement(name = "ReplaceResults")
    private ActionResultsType replaceResults;
    @XmlAttribute(required = true)
    private String version;

    public TransactionResponseType() {

    }

    public TransactionResponseType(final TransactionSummaryType transactionSummary, final ActionResultsType updateResults, final ActionResultsType insertResults, final ActionResultsType replaceResults, final String version) {
        this.transactionSummary = transactionSummary;
        this.updateResults      = updateResults;
        this.insertResults      = insertResults;
        this.replaceResults     = replaceResults;
        this.version            = version;
    }

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
     * Gets the value of the insertResults property.
     *
     * @return
     *     possible object is
     *     {@link ActionResultsType }
     *
     */
    public ActionResultsType getInsertResults() {
        return insertResults;
    }

    /**
     * Sets the value of the insertResults property.
     *
     * @param value
     *     allowed object is
     *     {@link ActionResultsType }
     *
     */
    public void setInsertResults(ActionResultsType value) {
        this.insertResults = value;
    }

    /**
     * Gets the value of the updateResults property.
     *
     * @return
     *     possible object is
     *     {@link ActionResultsType }
     *
     */
    public ActionResultsType getUpdateResults() {
        return updateResults;
    }

    /**
     * Sets the value of the updateResults property.
     *
     * @param value
     *     allowed object is
     *     {@link ActionResultsType }
     *
     */
    public void setUpdateResults(ActionResultsType value) {
        this.updateResults = value;
    }

    /**
     * Gets the value of the replaceResults property.
     *
     * @return
     *     possible object is
     *     {@link ActionResultsType }
     *
     */
    public ActionResultsType getReplaceResults() {
        return replaceResults;
    }

    /**
     * Sets the value of the replaceResults property.
     *
     * @param value
     *     allowed object is
     *     {@link ActionResultsType }
     *
     */
    public void setReplaceResults(ActionResultsType value) {
        this.replaceResults = value;
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
            return "2.0.0";
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

    @Override
    public List<FeatureId> getInsertedFID() {
        final List<FeatureId> ids = new ArrayList<FeatureId>();
        if (insertResults != null) {
            final List<CreatedOrModifiedFeatureType> inserted = insertResults.getFeature();
            if (inserted != null) {
                for(CreatedOrModifiedFeatureType ift : inserted){
                    for(ResourceIdType fit : ift.getResourceId()){
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
        if (replaceResults != null) {
            sb.append("replaceResults: ").append(replaceResults).append('\n');
        }
        if (updateResults != null) {
            sb.append("updateResults: ").append(updateResults).append('\n');
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
                   Objects.equals(this.updateResults,      that.updateResults)   &&
                   Objects.equals(this.replaceResults,     that.replaceResults)   &&
                   Objects.equals(this.transactionSummary, that.transactionSummary)   &&
                   Objects.equals(this.version,            that.version) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.transactionSummary != null ? this.transactionSummary.hashCode() : 0);
        hash = 19 * hash + (this.updateResults != null ? this.updateResults.hashCode() : 0);
        hash = 19 * hash + (this.replaceResults != null ? this.replaceResults.hashCode() : 0);
        hash = 19 * hash + (this.insertResults != null ? this.insertResults.hashCode() : 0);
        hash = 19 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

}
