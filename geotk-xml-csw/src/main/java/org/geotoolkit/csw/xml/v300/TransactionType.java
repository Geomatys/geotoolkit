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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.Transaction;


/**
 *
 *             Users may insert, update, or delete catalogue entries. If the
 *             verboseResponse attribute has the value "true", then one or more
 *             csw30:InsertResult elements must be included in the response.
 *
 *
 * <p>Classe Java pour TransactionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TransactionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="Insert" type="{http://www.opengis.net/cat/csw/3.0}InsertType"/>
 *           &lt;element name="Update" type="{http://www.opengis.net/cat/csw/3.0}UpdateType"/>
 *           &lt;element name="Delete" type="{http://www.opengis.net/cat/csw/3.0}DeleteType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="verboseResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="requestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionType", propOrder = {
    "insertOrUpdateOrDelete"
})
public class TransactionType extends RequestBaseType implements Transaction {

    @XmlElements({
        @XmlElement(name = "Insert", type = InsertType.class),
        @XmlElement(name = "Update", type = UpdateType.class),
        @XmlElement(name = "Delete", type = DeleteType.class)
    })
    protected List<Object> insertOrUpdateOrDelete;
    @XmlAttribute(name = "verboseResponse")
    protected Boolean verboseResponse;
    @XmlAttribute(name = "requestId")
    @XmlSchemaType(name = "anyURI")
    protected String requestId;

    /**
     * An empty constructor used by JAXB.
     */
    public TransactionType() {}

    /**
     * Build a new transaction request to insert a list of object
     */
    public TransactionType(final String service, final String version, final InsertType... inserts) {
        super(service, version);
        insertOrUpdateOrDelete = new ArrayList<>();
        for (InsertType insert: inserts) {
            insertOrUpdateOrDelete.add(insert);
        }
        verboseResponse = false;
    }

    /**
     * Build a new transaction request to insert a list of object
     */
    public TransactionType(final String service, final String version, final UpdateType... updates) {
        super(service, version);
        insertOrUpdateOrDelete = new ArrayList<>();
        for (UpdateType update: updates) {
            insertOrUpdateOrDelete.add(update);
        }
        verboseResponse = false;
    }

    /**
     * Build a new transaction request to delete a list of object
     */
    public TransactionType(final String service, final String version, final DeleteType delete) {
        super(service, version);
        insertOrUpdateOrDelete = new ArrayList<>();
        if (delete != null) {
            insertOrUpdateOrDelete.add(delete);
        }
        verboseResponse = false;
    }

    /**
     * Gets the value of the insertOrUpdateOrDelete property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insertOrUpdateOrDelete property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsertOrUpdateOrDelete().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsertType }
     * {@link UpdateType }
     * {@link DeleteType }
     *
     *
     */
    @Override
    public List<Object> getInsertOrUpdateOrDelete() {
        if (insertOrUpdateOrDelete == null) {
            insertOrUpdateOrDelete = new ArrayList<>();
        }
        return this.insertOrUpdateOrDelete;
    }

    /**
     * Obtient la valeur de la propriété verboseResponse.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isVerboseResponse() {
        if (verboseResponse == null) {
            return false;
        } else {
            return verboseResponse;
        }
    }

    /**
     * Définit la valeur de la propriété verboseResponse.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setVerboseResponse(Boolean value) {
        this.verboseResponse = value;
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

    @Override
    public String getOutputFormat() {
        return "application/xml";
    }

    @Override
    public void setOutputFormat(final String value) {}

}
