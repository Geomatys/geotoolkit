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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.CodeWithAuthorityType;
import org.geotoolkit.gml.xml.v321.MultiSurfacePropertyType;
import org.geotoolkit.ows.xml.v200.ServiceReferenceType;


/**
 * <p>Classe Java pour MaskInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="MaskInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.opengis.net/eop/2.1}MaskTypeValueType"/>
 *         &lt;element name="subType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="format" type="{http://www.opengis.net/eop/2.1}FormatValueType"/>
 *         &lt;element name="referenceSystemIdentifier" type="{http://www.opengis.net/gml/3.2}CodeWithAuthorityType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="fileName">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element ref="{http://www.opengis.net/ows/2.0}ServiceReference"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element ref="{http://www.opengis.net/eop/2.1}multiExtentOf"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaskInformationType", propOrder = {
    "type",
    "subType",
    "format",
    "referenceSystemIdentifier",
    "fileName",
    "multiExtentOf"
})
public class MaskInformationType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String type;
    protected String subType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String format;
    protected CodeWithAuthorityType referenceSystemIdentifier;
    protected MaskInformationType.FileName fileName;
    protected MultiSurfacePropertyType multiExtentOf;

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
     *     {@link String }
     *
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Définit la valeur de la propriété subType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubType(String value) {
        this.subType = value;
    }

    /**
     * Obtient la valeur de la propriété format.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFormat() {
        return format;
    }

    /**
     * Définit la valeur de la propriété format.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFormat(String value) {
        this.format = value;
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
     *     {@link MaskInformationType.FileName }
     *
     */
    public MaskInformationType.FileName getFileName() {
        return fileName;
    }

    /**
     * Définit la valeur de la propriété fileName.
     *
     * @param value
     *     allowed object is
     *     {@link MaskInformationType.FileName }
     *
     */
    public void setFileName(MaskInformationType.FileName value) {
        this.fileName = value;
    }

    /**
     * Mask member extent. Expected structure is gml:Polygon/gml:exterior/gml:LinearRing/gml:posList with 0 to n gml:Polygon/gml:interior/gml:LinearRing/gml:posList elements representing the holes.
     *
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *
     */
    public MultiSurfacePropertyType getMultiExtentOf() {
        return multiExtentOf;
    }

    /**
     * Définit la valeur de la propriété multiExtentOf.
     *
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *
     */
    public void setMultiExtentOf(MultiSurfacePropertyType value) {
        this.multiExtentOf = value;
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
