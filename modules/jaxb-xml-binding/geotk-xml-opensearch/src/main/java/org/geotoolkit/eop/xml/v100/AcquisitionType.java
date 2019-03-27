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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v311.AngleType;
import org.geotoolkit.gml.xml.v311.MeasureType;


/**
 * <p>Classe Java pour AcquisitionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AcquisitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="orbitNumber" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lastOrbitNumber" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="orbitDirection" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ASCENDING"/>
 *               &lt;enumeration value="DESCENDING"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="wrsLongitudeGrid" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anySimpleType">
 *                 &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="wrsLatitudeGrid" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anySimpleType">
 *                 &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ascendingNodeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ascendingNodeLongitude" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="startTimeFromAscendingNode" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="completionTimeFromAscendingNode" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="orbitDuration" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="acrossTrackIncidenceAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="alongTrackIncidenceAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="incidenceAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="pitch" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="roll" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="yaw" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcquisitionType", propOrder = {
    "orbitNumber",
    "lastOrbitNumber",
    "orbitDirection",
    "wrsLongitudeGrid",
    "wrsLatitudeGrid",
    "ascendingNodeDate",
    "ascendingNodeLongitude",
    "startTimeFromAscendingNode",
    "completionTimeFromAscendingNode",
    "orbitDuration",
    "acrossTrackIncidenceAngle",
    "alongTrackIncidenceAngle",
    "incidenceAngle",
    "pitch",
    "roll",
    "yaw"
})
public class AcquisitionType {

    protected Integer orbitNumber;
    protected Integer lastOrbitNumber;
    protected String orbitDirection;
    protected AcquisitionType.WrsLongitudeGrid wrsLongitudeGrid;
    protected AcquisitionType.WrsLatitudeGrid wrsLatitudeGrid;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ascendingNodeDate;
    protected MeasureType ascendingNodeLongitude;
    protected MeasureType startTimeFromAscendingNode;
    protected MeasureType completionTimeFromAscendingNode;
    protected MeasureType orbitDuration;
    protected AngleType acrossTrackIncidenceAngle;
    protected AngleType alongTrackIncidenceAngle;
    protected AngleType incidenceAngle;
    protected AngleType pitch;
    protected AngleType roll;
    protected AngleType yaw;

    /**
     * Obtient la valeur de la propriété orbitNumber.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getOrbitNumber() {
        return orbitNumber;
    }

    /**
     * Définit la valeur de la propriété orbitNumber.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setOrbitNumber(Integer value) {
        this.orbitNumber = value;
    }

    /**
     * Obtient la valeur de la propriété lastOrbitNumber.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getLastOrbitNumber() {
        return lastOrbitNumber;
    }

    /**
     * Définit la valeur de la propriété lastOrbitNumber.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setLastOrbitNumber(Integer value) {
        this.lastOrbitNumber = value;
    }

    /**
     * Obtient la valeur de la propriété orbitDirection.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrbitDirection() {
        return orbitDirection;
    }

    /**
     * Définit la valeur de la propriété orbitDirection.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrbitDirection(String value) {
        this.orbitDirection = value;
    }

    /**
     * Obtient la valeur de la propriété wrsLongitudeGrid.
     *
     * @return
     *     possible object is
     *     {@link AcquisitionType.WrsLongitudeGrid }
     *
     */
    public AcquisitionType.WrsLongitudeGrid getWrsLongitudeGrid() {
        return wrsLongitudeGrid;
    }

    /**
     * Définit la valeur de la propriété wrsLongitudeGrid.
     *
     * @param value
     *     allowed object is
     *     {@link AcquisitionType.WrsLongitudeGrid }
     *
     */
    public void setWrsLongitudeGrid(AcquisitionType.WrsLongitudeGrid value) {
        this.wrsLongitudeGrid = value;
    }

    /**
     * Obtient la valeur de la propriété wrsLatitudeGrid.
     *
     * @return
     *     possible object is
     *     {@link AcquisitionType.WrsLatitudeGrid }
     *
     */
    public AcquisitionType.WrsLatitudeGrid getWrsLatitudeGrid() {
        return wrsLatitudeGrid;
    }

    /**
     * Définit la valeur de la propriété wrsLatitudeGrid.
     *
     * @param value
     *     allowed object is
     *     {@link AcquisitionType.WrsLatitudeGrid }
     *
     */
    public void setWrsLatitudeGrid(AcquisitionType.WrsLatitudeGrid value) {
        this.wrsLatitudeGrid = value;
    }

    /**
     * Obtient la valeur de la propriété ascendingNodeDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getAscendingNodeDate() {
        return ascendingNodeDate;
    }

    /**
     * Définit la valeur de la propriété ascendingNodeDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setAscendingNodeDate(XMLGregorianCalendar value) {
        this.ascendingNodeDate = value;
    }

    /**
     * Obtient la valeur de la propriété ascendingNodeLongitude.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getAscendingNodeLongitude() {
        return ascendingNodeLongitude;
    }

    /**
     * Définit la valeur de la propriété ascendingNodeLongitude.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setAscendingNodeLongitude(MeasureType value) {
        this.ascendingNodeLongitude = value;
    }

    /**
     * Obtient la valeur de la propriété startTimeFromAscendingNode.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getStartTimeFromAscendingNode() {
        return startTimeFromAscendingNode;
    }

    /**
     * Définit la valeur de la propriété startTimeFromAscendingNode.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setStartTimeFromAscendingNode(MeasureType value) {
        this.startTimeFromAscendingNode = value;
    }

    /**
     * Obtient la valeur de la propriété completionTimeFromAscendingNode.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getCompletionTimeFromAscendingNode() {
        return completionTimeFromAscendingNode;
    }

    /**
     * Définit la valeur de la propriété completionTimeFromAscendingNode.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setCompletionTimeFromAscendingNode(MeasureType value) {
        this.completionTimeFromAscendingNode = value;
    }

    /**
     * Obtient la valeur de la propriété orbitDuration.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getOrbitDuration() {
        return orbitDuration;
    }

    /**
     * Définit la valeur de la propriété orbitDuration.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setOrbitDuration(MeasureType value) {
        this.orbitDuration = value;
    }

    /**
     * Obtient la valeur de la propriété acrossTrackIncidenceAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getAcrossTrackIncidenceAngle() {
        return acrossTrackIncidenceAngle;
    }

    /**
     * Définit la valeur de la propriété acrossTrackIncidenceAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setAcrossTrackIncidenceAngle(AngleType value) {
        this.acrossTrackIncidenceAngle = value;
    }

    /**
     * Obtient la valeur de la propriété alongTrackIncidenceAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getAlongTrackIncidenceAngle() {
        return alongTrackIncidenceAngle;
    }

    /**
     * Définit la valeur de la propriété alongTrackIncidenceAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setAlongTrackIncidenceAngle(AngleType value) {
        this.alongTrackIncidenceAngle = value;
    }

    /**
     * Obtient la valeur de la propriété incidenceAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getIncidenceAngle() {
        return incidenceAngle;
    }

    /**
     * Définit la valeur de la propriété incidenceAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setIncidenceAngle(AngleType value) {
        this.incidenceAngle = value;
    }

    /**
     * Obtient la valeur de la propriété pitch.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getPitch() {
        return pitch;
    }

    /**
     * Définit la valeur de la propriété pitch.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setPitch(AngleType value) {
        this.pitch = value;
    }

    /**
     * Obtient la valeur de la propriété roll.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getRoll() {
        return roll;
    }

    /**
     * Définit la valeur de la propriété roll.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setRoll(AngleType value) {
        this.roll = value;
    }

    /**
     * Obtient la valeur de la propriété yaw.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getYaw() {
        return yaw;
    }

    /**
     * Définit la valeur de la propriété yaw.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setYaw(AngleType value) {
        this.yaw = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anySimpleType">
     *       &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class WrsLatitudeGrid {

        @XmlValue
        @XmlSchemaType(name = "anySimpleType")
        protected Object value;
        @XmlAttribute(name = "codeSpace")
        @XmlSchemaType(name = "anyURI")
        protected String codeSpace;

        /**
         * Obtient la valeur de la propriété value.
         *
         * @return
         *     possible object is
         *     {@link Object }
         *
         */
        public Object getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         *
         * @param value
         *     allowed object is
         *     {@link Object }
         *
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété codeSpace.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCodeSpace() {
            return codeSpace;
        }

        /**
         * Définit la valeur de la propriété codeSpace.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCodeSpace(String value) {
            this.codeSpace = value;
        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anySimpleType">
     *       &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class WrsLongitudeGrid {

        @XmlValue
        @XmlSchemaType(name = "anySimpleType")
        protected Object value;
        @XmlAttribute(name = "codeSpace")
        @XmlSchemaType(name = "anyURI")
        protected String codeSpace;

        /**
         * Obtient la valeur de la propriété value.
         *
         * @return
         *     possible object is
         *     {@link Object }
         *
         */
        public Object getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         *
         * @param value
         *     allowed object is
         *     {@link Object }
         *
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété codeSpace.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCodeSpace() {
            return codeSpace;
        }

        /**
         * Définit la valeur de la propriété codeSpace.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCodeSpace(String value) {
            this.codeSpace = value;
        }

    }

}
