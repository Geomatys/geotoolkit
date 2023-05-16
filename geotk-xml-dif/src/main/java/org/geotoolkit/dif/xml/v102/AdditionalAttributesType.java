/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 One of three classes of structures for storing "name-value" style information:
 *                 * Additional Attributes - granual attributes to be advertised in the collection
 *                 * Measured Parameters - Parameters measured in the Granuals to be advertised in the collection
 *                 * Extended Metadata - Collection metadata values, or other external - non granual details - specific to the provider or collection
 *
 *                 | DIF 9             | ECHO 10              | UMM                  | DIF 10                | Notes                                                                             |
 *                 | ----------------- | -------------------- | -------------------- | --------------------- |-----------------------------------------------------------------------------------|
 *                 | Extended_Metadata | AdditionalAttributes | AdditionalAttributes | Additional_Attributes | Added to match ECHO field                                                         |
 *                 | Extended_Metadata | AdditionalAttributes | Measured Parameter?  | Extended_Metadata     | A new structure has been proposed for DIF 10.2, but use Extended Metadata for now |
 *                 | Extended_Metadata |           -          | ExtendedMetadata     | Extended_Metadata     |                                                                                   |
 *
 *
 * <p>Classe Java pour AdditionalAttributesType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AdditionalAttributesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DataType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeasurementResolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParameterRangeBegin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParameterRangeEnd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParameterUnitsOfMeasure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParameterValueAccuracy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ValueAccuracyExplanation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalAttributesType", propOrder = {
    "name",
    "dataType",
    "description",
    "measurementResolution",
    "parameterRangeBegin",
    "parameterRangeEnd",
    "parameterUnitsOfMeasure",
    "parameterValueAccuracy",
    "valueAccuracyExplanation",
    "value"
})
public class AdditionalAttributesType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "DataType", required = true)
    protected String dataType;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "MeasurementResolution")
    protected String measurementResolution;
    @XmlElement(name = "ParameterRangeBegin")
    protected String parameterRangeBegin;
    @XmlElement(name = "ParameterRangeEnd")
    protected String parameterRangeEnd;
    @XmlElement(name = "ParameterUnitsOfMeasure")
    protected String parameterUnitsOfMeasure;
    @XmlElement(name = "ParameterValueAccuracy")
    protected String parameterValueAccuracy;
    @XmlElement(name = "ValueAccuracyExplanation")
    protected String valueAccuracyExplanation;
    @XmlElement(name = "Value")
    protected String value;

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété dataType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Définit la valeur de la propriété dataType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    /**
     * Obtient la valeur de la propriété description.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propriété measurementResolution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMeasurementResolution() {
        return measurementResolution;
    }

    /**
     * Définit la valeur de la propriété measurementResolution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMeasurementResolution(String value) {
        this.measurementResolution = value;
    }

    /**
     * Obtient la valeur de la propriété parameterRangeBegin.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParameterRangeBegin() {
        return parameterRangeBegin;
    }

    /**
     * Définit la valeur de la propriété parameterRangeBegin.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParameterRangeBegin(String value) {
        this.parameterRangeBegin = value;
    }

    /**
     * Obtient la valeur de la propriété parameterRangeEnd.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParameterRangeEnd() {
        return parameterRangeEnd;
    }

    /**
     * Définit la valeur de la propriété parameterRangeEnd.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParameterRangeEnd(String value) {
        this.parameterRangeEnd = value;
    }

    /**
     * Obtient la valeur de la propriété parameterUnitsOfMeasure.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParameterUnitsOfMeasure() {
        return parameterUnitsOfMeasure;
    }

    /**
     * Définit la valeur de la propriété parameterUnitsOfMeasure.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParameterUnitsOfMeasure(String value) {
        this.parameterUnitsOfMeasure = value;
    }

    /**
     * Obtient la valeur de la propriété parameterValueAccuracy.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParameterValueAccuracy() {
        return parameterValueAccuracy;
    }

    /**
     * Définit la valeur de la propriété parameterValueAccuracy.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParameterValueAccuracy(String value) {
        this.parameterValueAccuracy = value;
    }

    /**
     * Obtient la valeur de la propriété valueAccuracyExplanation.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValueAccuracyExplanation() {
        return valueAccuracyExplanation;
    }

    /**
     * Définit la valeur de la propriété valueAccuracyExplanation.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValueAccuracyExplanation(String value) {
        this.valueAccuracyExplanation = value;
    }

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

}
