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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.CodeListType;
import org.geotoolkit.gml.xml.v321.CodeWithAuthorityType;
import org.geotoolkit.gml.xml.v321.MeasureListType;
import org.geotoolkit.ows.xml.v200.ServiceReferenceType;


/**
 * <p>Classe Java pour ProductInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ProductInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="referenceSystemIdentifier" type="{http://www.opengis.net/gml/3.2}CodeWithAuthorityType" minOccurs="0"/>
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
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="size" type="{http://www.opengis.net/gml/3.2}MeasureListType" minOccurs="0"/>
 *         &lt;element name="timeliness" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductInformationType", propOrder = {
    "referenceSystemIdentifier",
    "fileName",
    "version",
    "size",
    "timeliness"
})
public class ProductInformationType {

    protected CodeWithAuthorityType referenceSystemIdentifier;
    @XmlElement(required = true)
    protected ProductInformationType.FileName fileName;
    protected String version;
    protected MeasureListType size;
    protected CodeListType timeliness;

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
     *     {@link ProductInformationType.FileName }
     *
     */
    public ProductInformationType.FileName getFileName() {
        return fileName;
    }

    /**
     * Définit la valeur de la propriété fileName.
     *
     * @param value
     *     allowed object is
     *     {@link ProductInformationType.FileName }
     *
     */
    public void setFileName(ProductInformationType.FileName value) {
        this.fileName = value;
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

    /**
     * Obtient la valeur de la propriété size.
     *
     * @return
     *     possible object is
     *     {@link MeasureListType }
     *
     */
    public MeasureListType getSize() {
        return size;
    }

    /**
     * Définit la valeur de la propriété size.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureListType }
     *
     */
    public void setSize(MeasureListType value) {
        this.size = value;
    }

    /**
     * Obtient la valeur de la propriété timeliness.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getTimeliness() {
        return timeliness;
    }

    /**
     * Définit la valeur de la propriété timeliness.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setTimeliness(CodeListType value) {
        this.timeliness = value;
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
