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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v321.CodeListType;


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
 *         &lt;element name="processingCenter" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
 *         &lt;element name="processingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="compositeType" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         &lt;element name="method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="methodVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processorVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processingLevel" type="{http://www.opengis.net/eop/2.1}ProcessingLevelValueType" minOccurs="0"/>
 *         &lt;element name="nativeProductFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="auxiliaryDataSetFileName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="processingMode" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
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
    "nativeProductFormat",
    "auxiliaryDataSetFileName",
    "processingMode"
})
public class ProcessingInformationType {

    protected CodeListType processingCenter;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar processingDate;
    protected Duration compositeType;
    protected String method;
    protected String methodVersion;
    protected String processorName;
    protected String processorVersion;
    @XmlSchemaType(name = "anySimpleType")
    protected String processingLevel;
    protected String nativeProductFormat;
    protected List<String> auxiliaryDataSetFileName;
    protected CodeListType processingMode;

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
     *     {@link Duration }
     *
     */
    public Duration getCompositeType() {
        return compositeType;
    }

    /**
     * Définit la valeur de la propriété compositeType.
     *
     * @param value
     *     allowed object is
     *     {@link Duration }
     *
     */
    public void setCompositeType(Duration value) {
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

    /**
     * Gets the value of the auxiliaryDataSetFileName property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the auxiliaryDataSetFileName property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuxiliaryDataSetFileName().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAuxiliaryDataSetFileName() {
        if (auxiliaryDataSetFileName == null) {
            auxiliaryDataSetFileName = new ArrayList<String>();
        }
        return this.auxiliaryDataSetFileName;
    }

    /**
     * Obtient la valeur de la propriété processingMode.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getProcessingMode() {
        return processingMode;
    }

    /**
     * Définit la valeur de la propriété processingMode.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setProcessingMode(CodeListType value) {
        this.processingMode = value;
    }

}
