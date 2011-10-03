/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *             The WFS_TransactionResponseType defines the format of
 *             the XML document that a Web Feature Service generates 
 *             in response to a Transaction request.  The response 
 *             includes the completion status of the transaction 
 *             and the feature identifiers of any newly created
 *             feature instances.
 *          
 * 
 * <p>Java class for WFS_TransactionResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WFS_TransactionResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InsertResult" type="{http://www.opengis.net/wfs}InsertResultType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TransactionResult" type="{http://www.opengis.net/wfs}TransactionResultType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WFS_TransactionResponseType", propOrder = {
    "insertResult",
    "transactionResult"
})
public class WFSTransactionResponseType {

    @XmlElement(name = "InsertResult")
    private List<InsertResultType> insertResult;
    @XmlElement(name = "TransactionResult", required = true)
    private TransactionResultType transactionResult;
    @XmlAttribute(required = true)
    private String version;

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
     * Gets the value of the transactionResult property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionResultType }
     *     
     */
    public TransactionResultType getTransactionResult() {
        return transactionResult;
    }

    /**
     * Sets the value of the transactionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionResultType }
     *     
     */
    public void setTransactionResult(TransactionResultType value) {
        this.transactionResult = value;
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
            return "1.0.0";
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
