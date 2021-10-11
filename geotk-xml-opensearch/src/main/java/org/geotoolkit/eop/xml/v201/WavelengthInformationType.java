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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.MeasureListType;
import org.geotoolkit.gml.xml.v321.MeasureType;


/**
 * <p>Classe Java pour WavelengthInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="WavelengthInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="discreteWavelengths" type="{http://www.opengis.net/gml/3.2}MeasureListType" minOccurs="0"/>
 *         &lt;element name="endWavelength" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="spectralRange" type="{http://www.opengis.net/eop/2.1}SpectralRangeValueType" minOccurs="0"/>
 *         &lt;element name="startWavelength" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="wavelengthResolution" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WavelengthInformationType", propOrder = {
    "discreteWavelengths",
    "endWavelength",
    "spectralRange",
    "startWavelength",
    "wavelengthResolution"
})
public class WavelengthInformationType {

    protected MeasureListType discreteWavelengths;
    protected MeasureType endWavelength;
    @XmlSchemaType(name = "anySimpleType")
    protected String spectralRange;
    protected MeasureType startWavelength;
    protected MeasureType wavelengthResolution;

    /**
     * Obtient la valeur de la propriété discreteWavelengths.
     *
     * @return
     *     possible object is
     *     {@link MeasureListType }
     *
     */
    public MeasureListType getDiscreteWavelengths() {
        return discreteWavelengths;
    }

    /**
     * Définit la valeur de la propriété discreteWavelengths.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureListType }
     *
     */
    public void setDiscreteWavelengths(MeasureListType value) {
        this.discreteWavelengths = value;
    }

    /**
     * Obtient la valeur de la propriété endWavelength.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getEndWavelength() {
        return endWavelength;
    }

    /**
     * Définit la valeur de la propriété endWavelength.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setEndWavelength(MeasureType value) {
        this.endWavelength = value;
    }

    /**
     * Obtient la valeur de la propriété spectralRange.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpectralRange() {
        return spectralRange;
    }

    /**
     * Définit la valeur de la propriété spectralRange.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpectralRange(String value) {
        this.spectralRange = value;
    }

    /**
     * Obtient la valeur de la propriété startWavelength.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getStartWavelength() {
        return startWavelength;
    }

    /**
     * Définit la valeur de la propriété startWavelength.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setStartWavelength(MeasureType value) {
        this.startWavelength = value;
    }

    /**
     * Obtient la valeur de la propriété wavelengthResolution.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getWavelengthResolution() {
        return wavelengthResolution;
    }

    /**
     * Définit la valeur de la propriété wavelengthResolution.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setWavelengthResolution(MeasureType value) {
        this.wavelengthResolution = value;
    }

}
