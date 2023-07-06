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


package org.geotoolkit.opt.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.MeasureType;


/**
 * <p>Classe Java pour EarthObservationResultType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://earth.esa.int/eop}EarthObservationResultType">
 *       &lt;sequence>
 *         &lt;element name="cloudCoverPercentage" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="cloudCoverPercentageAssessmentConfidence" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="cloudCoverPercentageQuotationMode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="AUTOMATIC"/>
 *               &lt;enumeration value="MANUAL"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="snowCoverPercentage" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="snowCoverPercentageAssessmentConfidence" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="snowCoverPercentageQuotationMode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="AUTOMATIC"/>
 *               &lt;enumeration value="MANUAL"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationResultType", propOrder = {
    "cloudCoverPercentage",
    "cloudCoverPercentageAssessmentConfidence",
    "cloudCoverPercentageQuotationMode",
    "snowCoverPercentage",
    "snowCoverPercentageAssessmentConfidence",
    "snowCoverPercentageQuotationMode"
})
public class EarthObservationResultType
    extends org.geotoolkit.eop.xml.v100.EarthObservationResultType
{

    protected MeasureType cloudCoverPercentage;
    protected MeasureType cloudCoverPercentageAssessmentConfidence;
    protected String cloudCoverPercentageQuotationMode;
    protected MeasureType snowCoverPercentage;
    protected MeasureType snowCoverPercentageAssessmentConfidence;
    protected String snowCoverPercentageQuotationMode;

    /**
     * Obtient la valeur de la propriété cloudCoverPercentage.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getCloudCoverPercentage() {
        return cloudCoverPercentage;
    }

    /**
     * Définit la valeur de la propriété cloudCoverPercentage.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setCloudCoverPercentage(MeasureType value) {
        this.cloudCoverPercentage = value;
    }

    /**
     * Obtient la valeur de la propriété cloudCoverPercentageAssessmentConfidence.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getCloudCoverPercentageAssessmentConfidence() {
        return cloudCoverPercentageAssessmentConfidence;
    }

    /**
     * Définit la valeur de la propriété cloudCoverPercentageAssessmentConfidence.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setCloudCoverPercentageAssessmentConfidence(MeasureType value) {
        this.cloudCoverPercentageAssessmentConfidence = value;
    }

    /**
     * Obtient la valeur de la propriété cloudCoverPercentageQuotationMode.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCloudCoverPercentageQuotationMode() {
        return cloudCoverPercentageQuotationMode;
    }

    /**
     * Définit la valeur de la propriété cloudCoverPercentageQuotationMode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCloudCoverPercentageQuotationMode(String value) {
        this.cloudCoverPercentageQuotationMode = value;
    }

    /**
     * Obtient la valeur de la propriété snowCoverPercentage.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getSnowCoverPercentage() {
        return snowCoverPercentage;
    }

    /**
     * Définit la valeur de la propriété snowCoverPercentage.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setSnowCoverPercentage(MeasureType value) {
        this.snowCoverPercentage = value;
    }

    /**
     * Obtient la valeur de la propriété snowCoverPercentageAssessmentConfidence.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getSnowCoverPercentageAssessmentConfidence() {
        return snowCoverPercentageAssessmentConfidence;
    }

    /**
     * Définit la valeur de la propriété snowCoverPercentageAssessmentConfidence.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setSnowCoverPercentageAssessmentConfidence(MeasureType value) {
        this.snowCoverPercentageAssessmentConfidence = value;
    }

    /**
     * Obtient la valeur de la propriété snowCoverPercentageQuotationMode.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSnowCoverPercentageQuotationMode() {
        return snowCoverPercentageQuotationMode;
    }

    /**
     * Définit la valeur de la propriété snowCoverPercentageQuotationMode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSnowCoverPercentageQuotationMode(String value) {
        this.snowCoverPercentageQuotationMode = value;
    }

}
