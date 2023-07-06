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
import org.geotoolkit.gml.xml.v321.CodeListType;
import org.geotoolkit.gml.xml.v321.MeasureType;


/**
 * <p>Classe Java pour SensorType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SensorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sensorType" type="{http://www.opengis.net/eop/2.1}SensorTypeValueType" minOccurs="0"/>
 *         &lt;element name="operationalMode" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
 *         &lt;element name="resolution" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="swathIdentifier" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
 *         &lt;element name="wavelengthInformation" type="{http://www.opengis.net/eop/2.1}WavelengthInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SensorType", propOrder = {
    "sensorType",
    "operationalMode",
    "resolution",
    "swathIdentifier",
    "wavelengthInformation"
})
public class SensorType {

    @XmlSchemaType(name = "anySimpleType")
    protected String sensorType;
    protected CodeListType operationalMode;
    protected MeasureType resolution;
    protected CodeListType swathIdentifier;
    protected List<WavelengthInformationPropertyType> wavelengthInformation;

    /**
     * Obtient la valeur de la propriété sensorType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * Définit la valeur de la propriété sensorType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSensorType(String value) {
        this.sensorType = value;
    }

    /**
     * Obtient la valeur de la propriété operationalMode.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getOperationalMode() {
        return operationalMode;
    }

    /**
     * Définit la valeur de la propriété operationalMode.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setOperationalMode(CodeListType value) {
        this.operationalMode = value;
    }

    /**
     * Obtient la valeur de la propriété resolution.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getResolution() {
        return resolution;
    }

    /**
     * Définit la valeur de la propriété resolution.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setResolution(MeasureType value) {
        this.resolution = value;
    }

    /**
     * Obtient la valeur de la propriété swathIdentifier.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getSwathIdentifier() {
        return swathIdentifier;
    }

    /**
     * Définit la valeur de la propriété swathIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setSwathIdentifier(CodeListType value) {
        this.swathIdentifier = value;
    }

    /**
     * Gets the value of the wavelengthInformation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wavelengthInformation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWavelengthInformation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WavelengthInformationPropertyType }
     *
     *
     */
    public List<WavelengthInformationPropertyType> getWavelengthInformation() {
        if (wavelengthInformation == null) {
            wavelengthInformation = new ArrayList<WavelengthInformationPropertyType>();
        }
        return this.wavelengthInformation;
    }

}
