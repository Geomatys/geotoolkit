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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * No change from DIF 9
 *
 * <p>Classe Java pour ChronostratigraphicUnitType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ChronostratigraphicUnitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Eon" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Era" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="Period" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="Epoch" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="Stage" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="Detailed_Classification" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}UuidType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChronostratigraphicUnitType", propOrder = {
    "eon",
    "era",
    "period",
    "epoch",
    "stage",
    "detailedClassification"
})
public class ChronostratigraphicUnitType {

    @XmlElement(name = "Eon", required = true)
    protected Object eon;
    @XmlElement(name = "Era")
    protected Object era;
    @XmlElement(name = "Period")
    protected Object period;
    @XmlElement(name = "Epoch")
    protected Object epoch;
    @XmlElement(name = "Stage")
    protected Object stage;
    @XmlElement(name = "Detailed_Classification")
    protected Object detailedClassification;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    /**
     * Obtient la valeur de la propriété eon.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getEon() {
        return eon;
    }

    /**
     * Définit la valeur de la propriété eon.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setEon(Object value) {
        this.eon = value;
    }

    /**
     * Obtient la valeur de la propriété era.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getEra() {
        return era;
    }

    /**
     * Définit la valeur de la propriété era.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setEra(Object value) {
        this.era = value;
    }

    /**
     * Obtient la valeur de la propriété period.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getPeriod() {
        return period;
    }

    /**
     * Définit la valeur de la propriété period.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setPeriod(Object value) {
        this.period = value;
    }

    /**
     * Obtient la valeur de la propriété epoch.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getEpoch() {
        return epoch;
    }

    /**
     * Définit la valeur de la propriété epoch.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setEpoch(Object value) {
        this.epoch = value;
    }

    /**
     * Obtient la valeur de la propriété stage.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getStage() {
        return stage;
    }

    /**
     * Définit la valeur de la propriété stage.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setStage(Object value) {
        this.stage = value;
    }

    /**
     * Obtient la valeur de la propriété detailedClassification.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getDetailedClassification() {
        return detailedClassification;
    }

    /**
     * Définit la valeur de la propriété detailedClassification.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setDetailedClassification(Object value) {
        this.detailedClassification = value;
    }

    /**
     * Obtient la valeur de la propriété uuid.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Définit la valeur de la propriété uuid.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
