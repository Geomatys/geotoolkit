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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.InsertResult;


/**
 *
 *             Returns a "brief" view of any newly created catalogue records.
 *             The handle attribute may reference a particular statement in
 *             the corresponding transaction request.
 *
 *
 * <p>Classe Java pour InsertResultType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="InsertResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/3.0}BriefRecord" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handleRef" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertResultType", propOrder = {
    "briefRecord"
})
public class InsertResultType implements InsertResult {

    @XmlElement(name = "BriefRecord", required = true)
    protected List<Object> briefRecord;
    @XmlAttribute(name = "handleRef")
    @XmlSchemaType(name = "anyURI")
    protected String handleRef;

    public InsertResultType() {
    }

    /**
     * build a new Inserted result record view.
     * briefRecord parameter is set to Object for JAXBContext purpose but it must be a BriefRecordType List
     * (or Node List containing brief records) in order to pass XML validation
     *
     * @param briefRecord must be a BriefRecordTypeList
     * @param handleRef
     */
    public InsertResultType(final List<Object> briefRecord, final String handleRef) {
        this.briefRecord = briefRecord;
        this.handleRef   = handleRef;
    }

    /**
     * Gets the value of the briefRecord property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the briefRecord property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBriefRecord().add(newItem);
     * </pre>
     */
    public List<Object> getBriefRecord() {
        if (briefRecord == null) {
            briefRecord = new ArrayList<>();
        }
        return this.briefRecord;
    }

    /**
     * Obtient la valeur de la propriété handleRef.
     */
    public String getHandleRef() {
        return handleRef;
    }

    /**
     * Définit la valeur de la propriété handleRef.
     */
    public void setHandleRef(String value) {
        this.handleRef = value;
    }
}
