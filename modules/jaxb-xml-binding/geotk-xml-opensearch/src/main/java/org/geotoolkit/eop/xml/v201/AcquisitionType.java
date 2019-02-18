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

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v321.CodeWithAuthorityType;
import org.geotoolkit.gml.xml.v321.MeasureType;
import org.geotoolkit.gml.xml.v321.AngleType;


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
 *         &lt;element name="orbitNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="lastOrbitNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="orbitDirection" type="{http://www.opengis.net/eop/2.1}OrbitDirectionValueType" minOccurs="0"/>
 *         &lt;element name="wrsLongitudeGrid" type="{http://www.opengis.net/gml/3.2}CodeWithAuthorityType" minOccurs="0"/>
 *         &lt;element name="wrsLatitudeGrid" type="{http://www.opengis.net/gml/3.2}CodeWithAuthorityType" minOccurs="0"/>
 *         &lt;element name="ascendingNodeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ascendingNodeLongitude" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="startTimeFromAscendingNode" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="completionTimeFromAscendingNode" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="orbitDuration" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="illuminationAzimuthAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="illuminationZenithAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="illuminationElevationAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="instrumentAzimuthAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="instrumentZenithAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="instrumentElevationAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="incidenceAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="acrossTrackIncidenceAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="alongTrackIncidenceAngle" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="pitch" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="roll" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
 *         &lt;element name="yaw" type="{http://www.opengis.net/gml/3.2}AngleType" minOccurs="0"/>
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
    "illuminationAzimuthAngle",
    "illuminationZenithAngle",
    "illuminationElevationAngle",
    "instrumentAzimuthAngle",
    "instrumentZenithAngle",
    "instrumentElevationAngle",
    "incidenceAngle",
    "acrossTrackIncidenceAngle",
    "alongTrackIncidenceAngle",
    "pitch",
    "roll",
    "yaw"
})
public class AcquisitionType {

    protected BigInteger orbitNumber;
    protected BigInteger lastOrbitNumber;
    @XmlSchemaType(name = "string")
    protected OrbitDirectionValueType orbitDirection;
    protected CodeWithAuthorityType wrsLongitudeGrid;
    protected CodeWithAuthorityType wrsLatitudeGrid;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ascendingNodeDate;
    protected MeasureType ascendingNodeLongitude;
    protected MeasureType startTimeFromAscendingNode;
    protected MeasureType completionTimeFromAscendingNode;
    protected MeasureType orbitDuration;
    protected AngleType illuminationAzimuthAngle;
    protected AngleType illuminationZenithAngle;
    protected AngleType illuminationElevationAngle;
    protected AngleType instrumentAzimuthAngle;
    protected AngleType instrumentZenithAngle;
    protected AngleType instrumentElevationAngle;
    protected AngleType incidenceAngle;
    protected AngleType acrossTrackIncidenceAngle;
    protected AngleType alongTrackIncidenceAngle;
    protected AngleType pitch;
    protected AngleType roll;
    protected AngleType yaw;

    /**
     * Obtient la valeur de la propriété orbitNumber.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getOrbitNumber() {
        return orbitNumber;
    }

    /**
     * Définit la valeur de la propriété orbitNumber.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setOrbitNumber(BigInteger value) {
        this.orbitNumber = value;
    }

    /**
     * Obtient la valeur de la propriété lastOrbitNumber.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getLastOrbitNumber() {
        return lastOrbitNumber;
    }

    /**
     * Définit la valeur de la propriété lastOrbitNumber.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setLastOrbitNumber(BigInteger value) {
        this.lastOrbitNumber = value;
    }

    /**
     * Obtient la valeur de la propriété orbitDirection.
     *
     * @return
     *     possible object is
     *     {@link OrbitDirectionValueType }
     *
     */
    public OrbitDirectionValueType getOrbitDirection() {
        return orbitDirection;
    }

    /**
     * Définit la valeur de la propriété orbitDirection.
     *
     * @param value
     *     allowed object is
     *     {@link OrbitDirectionValueType }
     *
     */
    public void setOrbitDirection(OrbitDirectionValueType value) {
        this.orbitDirection = value;
    }

    /**
     * Obtient la valeur de la propriété wrsLongitudeGrid.
     *
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public CodeWithAuthorityType getWrsLongitudeGrid() {
        return wrsLongitudeGrid;
    }

    /**
     * Définit la valeur de la propriété wrsLongitudeGrid.
     *
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public void setWrsLongitudeGrid(CodeWithAuthorityType value) {
        this.wrsLongitudeGrid = value;
    }

    /**
     * Obtient la valeur de la propriété wrsLatitudeGrid.
     *
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public CodeWithAuthorityType getWrsLatitudeGrid() {
        return wrsLatitudeGrid;
    }

    /**
     * Définit la valeur de la propriété wrsLatitudeGrid.
     *
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public void setWrsLatitudeGrid(CodeWithAuthorityType value) {
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
     * Obtient la valeur de la propriété illuminationAzimuthAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getIlluminationAzimuthAngle() {
        return illuminationAzimuthAngle;
    }

    /**
     * Définit la valeur de la propriété illuminationAzimuthAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setIlluminationAzimuthAngle(AngleType value) {
        this.illuminationAzimuthAngle = value;
    }

    /**
     * Obtient la valeur de la propriété illuminationZenithAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getIlluminationZenithAngle() {
        return illuminationZenithAngle;
    }

    /**
     * Définit la valeur de la propriété illuminationZenithAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setIlluminationZenithAngle(AngleType value) {
        this.illuminationZenithAngle = value;
    }

    /**
     * Obtient la valeur de la propriété illuminationElevationAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getIlluminationElevationAngle() {
        return illuminationElevationAngle;
    }

    /**
     * Définit la valeur de la propriété illuminationElevationAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setIlluminationElevationAngle(AngleType value) {
        this.illuminationElevationAngle = value;
    }

    /**
     * Obtient la valeur de la propriété instrumentAzimuthAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getInstrumentAzimuthAngle() {
        return instrumentAzimuthAngle;
    }

    /**
     * Définit la valeur de la propriété instrumentAzimuthAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setInstrumentAzimuthAngle(AngleType value) {
        this.instrumentAzimuthAngle = value;
    }

    /**
     * Obtient la valeur de la propriété instrumentZenithAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getInstrumentZenithAngle() {
        return instrumentZenithAngle;
    }

    /**
     * Définit la valeur de la propriété instrumentZenithAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setInstrumentZenithAngle(AngleType value) {
        this.instrumentZenithAngle = value;
    }

    /**
     * Obtient la valeur de la propriété instrumentElevationAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getInstrumentElevationAngle() {
        return instrumentElevationAngle;
    }

    /**
     * Définit la valeur de la propriété instrumentElevationAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setInstrumentElevationAngle(AngleType value) {
        this.instrumentElevationAngle = value;
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

}
