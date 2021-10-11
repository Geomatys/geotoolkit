/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.eop.xml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.CodeListType;
import org.geotoolkit.gml.xml.v321.CodeWithAuthorityType;
import org.geotoolkit.ows.xml.v200.ServiceReferenceType;


/**
 * <p>Classe Java pour BrowseInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="BrowseInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.opengis.net/eop/2.1}BrowseTypeValueType"/>
 *         &lt;element name="subType" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
 *         &lt;element name="referenceSystemIdentifier" type="{http://www.opengis.net/gml/3.2}CodeWithAuthorityType"/>
 *         &lt;element name="fileName">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ows/2.0}ServiceReference"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrowseInformationType", propOrder = {
    "type",
    "subType",
    "referenceSystemIdentifier",
    "fileName"
})
public class BrowseInformationType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String type;
    protected CodeListType subType;
    @XmlElement(required = true)
    protected CodeWithAuthorityType referenceSystemIdentifier;
    @XmlElement(required = true)
    protected BrowseInformationType.FileName fileName;

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété subType.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getSubType() {
        return subType;
    }

    /**
     * Définit la valeur de la propriété subType.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setSubType(CodeListType value) {
        this.subType = value;
    }

    /**
     * Obtient la valeur de la propriété referenceSystemIdentifier.
     *
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public CodeWithAuthorityType getReferenceSystemIdentifier() {
        return referenceSystemIdentifier;
    }

    /**
     * Définit la valeur de la propriété referenceSystemIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public void setReferenceSystemIdentifier(CodeWithAuthorityType value) {
        this.referenceSystemIdentifier = value;
    }

    /**
     * Obtient la valeur de la propriété fileName.
     *
     * @return
     *     possible object is
     *     {@link BrowseInformationType.FileName }
     *
     */
    public BrowseInformationType.FileName getFileName() {
        return fileName;
    }

    /**
     * Définit la valeur de la propriété fileName.
     *
     * @param value
     *     allowed object is
     *     {@link BrowseInformationType.FileName }
     *
     */
    public void setFileName(BrowseInformationType.FileName value) {
        this.fileName = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/ows/2.0}ServiceReference"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "serviceReference"
    })
    public static class FileName {

        @XmlElement(name = "ServiceReference", namespace = "http://www.opengis.net/ows/2.0", required = true)
        protected ServiceReferenceType serviceReference;

        /**
         * Obtient la valeur de la propriété serviceReference.
         *
         * @return
         *     possible object is
         *     {@link ServiceReferenceType }
         *
         */
        public ServiceReferenceType getServiceReference() {
            return serviceReference;
        }

        /**
         * Définit la valeur de la propriété serviceReference.
         *
         * @param value
         *     allowed object is
         *     {@link ServiceReferenceType }
         *
         */
        public void setServiceReference(ServiceReferenceType value) {
            this.serviceReference = value;
        }

    }

}
