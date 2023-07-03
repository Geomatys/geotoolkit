/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

package org.geotoolkit.kml.xml.v230;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour LookAtType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="LookAtType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractViewType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}longitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}latitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}heading" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tilt" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}range" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/kml/2.2}AltitudeModeGroup"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}horizFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LookAtSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LookAtObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookAtType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "longitude",
    "latitude",
    "altitude",
    "heading",
    "tilt",
    "range",
    "altitudeMode",
    "seaFloorAltitudeMode",
    "altitudeModeSimpleExtensionGroup",
    "altitudeModeObjectExtensionGroup",
    "horizFov",
    "lookAtSimpleExtensionGroup",
    "lookAtObjectExtensionGroup"
})
public class LookAtType
    extends AbstractViewType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double longitude;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double latitude;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double altitude;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double heading;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double tilt;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double range;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "clampToGround")
    @XmlSchemaType(name = "string")
    protected AltitudeModeEnumType altitudeMode;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    @XmlSchemaType(name = "string")
    protected SeaFloorAltitudeModeEnumType seaFloorAltitudeMode;
    @XmlElement(name = "AltitudeModeSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> altitudeModeSimpleExtensionGroup;
    @XmlElement(name = "AltitudeModeObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> altitudeModeObjectExtensionGroup;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected Double horizFov;
    @XmlElement(name = "LookAtSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> lookAtSimpleExtensionGroup;
    @XmlElement(name = "LookAtObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> lookAtObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété longitude.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Définit la valeur de la propriété longitude.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLongitude(Double value) {
        this.longitude = value;
    }

    /**
     * Obtient la valeur de la propriété latitude.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Définit la valeur de la propriété latitude.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLatitude(Double value) {
        this.latitude = value;
    }

    /**
     * Obtient la valeur de la propriété altitude.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * Définit la valeur de la propriété altitude.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setAltitude(Double value) {
        this.altitude = value;
    }

    /**
     * Obtient la valeur de la propriété heading.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getHeading() {
        return heading;
    }

    /**
     * Définit la valeur de la propriété heading.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setHeading(Double value) {
        this.heading = value;
    }

    /**
     * Obtient la valeur de la propriété tilt.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getTilt() {
        return tilt;
    }

    /**
     * Définit la valeur de la propriété tilt.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setTilt(Double value) {
        this.tilt = value;
    }

    /**
     * Obtient la valeur de la propriété range.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRange() {
        return range;
    }

    /**
     * Définit la valeur de la propriété range.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRange(Double value) {
        this.range = value;
    }

    /**
     * Obtient la valeur de la propriété altitudeMode.
     *
     * @return
     *     possible object is
     *     {@link AltitudeModeEnumType }
     *
     */
    public AltitudeModeEnumType getAltitudeMode() {
        return altitudeMode;
    }

    /**
     * Définit la valeur de la propriété altitudeMode.
     *
     * @param value
     *     allowed object is
     *     {@link AltitudeModeEnumType }
     *
     */
    public void setAltitudeMode(AltitudeModeEnumType value) {
        this.altitudeMode = value;
    }

    /**
     * Obtient la valeur de la propriété seaFloorAltitudeMode.
     *
     * @return
     *     possible object is
     *     {@link SeaFloorAltitudeModeEnumType }
     *
     */
    public SeaFloorAltitudeModeEnumType getSeaFloorAltitudeMode() {
        return seaFloorAltitudeMode;
    }

    /**
     * Définit la valeur de la propriété seaFloorAltitudeMode.
     *
     * @param value
     *     allowed object is
     *     {@link SeaFloorAltitudeModeEnumType }
     *
     */
    public void setSeaFloorAltitudeMode(SeaFloorAltitudeModeEnumType value) {
        this.seaFloorAltitudeMode = value;
    }

    /**
     * Gets the value of the altitudeModeSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altitudeModeSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltitudeModeSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAltitudeModeSimpleExtensionGroup() {
        if (altitudeModeSimpleExtensionGroup == null) {
            altitudeModeSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.altitudeModeSimpleExtensionGroup;
    }

    /**
     * Gets the value of the altitudeModeObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altitudeModeObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltitudeModeObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAltitudeModeObjectExtensionGroup() {
        if (altitudeModeObjectExtensionGroup == null) {
            altitudeModeObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.altitudeModeObjectExtensionGroup;
    }

    /**
     * Obtient la valeur de la propriété horizFov.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getHorizFov() {
        return horizFov;
    }

    /**
     * Définit la valeur de la propriété horizFov.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setHorizFov(Double value) {
        this.horizFov = value;
    }

    /**
     * Gets the value of the lookAtSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lookAtSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLookAtSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLookAtSimpleExtensionGroup() {
        if (lookAtSimpleExtensionGroup == null) {
            lookAtSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.lookAtSimpleExtensionGroup;
    }

    /**
     * Gets the value of the lookAtObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lookAtObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLookAtObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLookAtObjectExtensionGroup() {
        if (lookAtObjectExtensionGroup == null) {
            lookAtObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.lookAtObjectExtensionGroup;
    }

}
