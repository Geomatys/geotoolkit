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

package org.geotoolkit.eop.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v311.CodeListType;


/**
 * <p>Classe Java pour ProcessingInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ProcessingInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processingCenter" type="{http://www.opengis.net/gml}CodeListType" minOccurs="0"/>
 *         &lt;element name="processingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="compositeType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="DAILY"/>
 *               &lt;enumeration value="WEEKLY"/>
 *               &lt;enumeration value="MONTHLY"/>
 *               &lt;enumeration value=""/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="methodVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processorVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processingLevel" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1A"/>
 *               &lt;enumeration value="1B"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="nativeProductFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessingInformationType", propOrder = {
    "processingCenter",
    "processingDate",
    "compositeType",
    "method",
    "methodVersion",
    "processorName",
    "processorVersion",
    "processingLevel",
    "nativeProductFormat"
})
public class ProcessingInformationType {

    protected CodeListType processingCenter;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar processingDate;
    protected String compositeType;
    protected String method;
    protected String methodVersion;
    protected String processorName;
    protected String processorVersion;
    protected String processingLevel;
    protected String nativeProductFormat;

    /**
     * Obtient la valeur de la propriété processingCenter.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getProcessingCenter() {
        return processingCenter;
    }

    /**
     * Définit la valeur de la propriété processingCenter.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setProcessingCenter(CodeListType value) {
        this.processingCenter = value;
    }

    /**
     * Obtient la valeur de la propriété processingDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getProcessingDate() {
        return processingDate;
    }

    /**
     * Définit la valeur de la propriété processingDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setProcessingDate(XMLGregorianCalendar value) {
        this.processingDate = value;
    }

    /**
     * Obtient la valeur de la propriété compositeType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCompositeType() {
        return compositeType;
    }

    /**
     * Définit la valeur de la propriété compositeType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCompositeType(String value) {
        this.compositeType = value;
    }

    /**
     * Obtient la valeur de la propriété method.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMethod() {
        return method;
    }

    /**
     * Définit la valeur de la propriété method.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMethod(String value) {
        this.method = value;
    }

    /**
     * Obtient la valeur de la propriété methodVersion.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMethodVersion() {
        return methodVersion;
    }

    /**
     * Définit la valeur de la propriété methodVersion.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMethodVersion(String value) {
        this.methodVersion = value;
    }

    /**
     * Obtient la valeur de la propriété processorName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcessorName() {
        return processorName;
    }

    /**
     * Définit la valeur de la propriété processorName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcessorName(String value) {
        this.processorName = value;
    }

    /**
     * Obtient la valeur de la propriété processorVersion.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcessorVersion() {
        return processorVersion;
    }

    /**
     * Définit la valeur de la propriété processorVersion.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcessorVersion(String value) {
        this.processorVersion = value;
    }

    /**
     * Obtient la valeur de la propriété processingLevel.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcessingLevel() {
        return processingLevel;
    }

    /**
     * Définit la valeur de la propriété processingLevel.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcessingLevel(String value) {
        this.processingLevel = value;
    }

    /**
     * Obtient la valeur de la propriété nativeProductFormat.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNativeProductFormat() {
        return nativeProductFormat;
    }

    /**
     * Définit la valeur de la propriété nativeProductFormat.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNativeProductFormat(String value) {
        this.nativeProductFormat = value;
    }

}
