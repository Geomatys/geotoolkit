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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour HistogramType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="HistogramType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bandId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="min" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="max" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="mean" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stdDeviation" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HistogramType", propOrder = {
    "bandId",
    "min",
    "max",
    "mean",
    "stdDeviation"
})
public class HistogramType {

    protected String bandId;
    protected int min;
    protected int max;
    protected Double mean;
    protected Double stdDeviation;

    /**
     * Obtient la valeur de la propriété bandId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBandId() {
        return bandId;
    }

    /**
     * Définit la valeur de la propriété bandId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBandId(String value) {
        this.bandId = value;
    }

    /**
     * Obtient la valeur de la propriété min.
     *
     */
    public int getMin() {
        return min;
    }

    /**
     * Définit la valeur de la propriété min.
     *
     */
    public void setMin(int value) {
        this.min = value;
    }

    /**
     * Obtient la valeur de la propriété max.
     *
     */
    public int getMax() {
        return max;
    }

    /**
     * Définit la valeur de la propriété max.
     *
     */
    public void setMax(int value) {
        this.max = value;
    }

    /**
     * Obtient la valeur de la propriété mean.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMean() {
        return mean;
    }

    /**
     * Définit la valeur de la propriété mean.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMean(Double value) {
        this.mean = value;
    }

    /**
     * Obtient la valeur de la propriété stdDeviation.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getStdDeviation() {
        return stdDeviation;
    }

    /**
     * Définit la valeur de la propriété stdDeviation.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setStdDeviation(Double value) {
        this.stdDeviation = value;
    }

}
