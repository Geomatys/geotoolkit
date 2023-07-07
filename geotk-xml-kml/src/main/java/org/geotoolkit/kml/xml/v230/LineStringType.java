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
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour LineStringType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="LineStringType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}extrude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tessellate" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/kml/2.2}AltitudeModeGroup"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}coordinates" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitudeOffset" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LineStringSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LineStringObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "LineStringType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "extrude",
    "tessellate",
    "altitudeMode",
    "seaFloorAltitudeMode",
    "altitudeModeSimpleExtensionGroup",
    "altitudeModeObjectExtensionGroup",
    "coordinates",
    "altitudeOffset",
    "lineStringSimpleExtensionGroup",
    "lineStringObjectExtensionGroup"
})
public class LineStringType
    extends AbstractGeometryType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Boolean extrude;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Boolean tessellate;
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
    @XmlList
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected List<String> coordinates;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double altitudeOffset;
    @XmlElement(name = "LineStringSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> lineStringSimpleExtensionGroup;
    @XmlElement(name = "LineStringObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> lineStringObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété extrude.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isExtrude() {
        return extrude;
    }

    /**
     * Définit la valeur de la propriété extrude.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setExtrude(Boolean value) {
        this.extrude = value;
    }

    /**
     * Obtient la valeur de la propriété tessellate.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isTessellate() {
        return tessellate;
    }

    /**
     * Définit la valeur de la propriété tessellate.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setTessellate(Boolean value) {
        this.tessellate = value;
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
     * Gets the value of the coordinates property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordinates property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordinates().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCoordinates() {
        if (coordinates == null) {
            coordinates = new ArrayList<String>();
        }
        return this.coordinates;
    }

    /**
     * Obtient la valeur de la propriété altitudeOffset.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getAltitudeOffset() {
        return altitudeOffset;
    }

    /**
     * Définit la valeur de la propriété altitudeOffset.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setAltitudeOffset(Double value) {
        this.altitudeOffset = value;
    }

    /**
     * Gets the value of the lineStringSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lineStringSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLineStringSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLineStringSimpleExtensionGroup() {
        if (lineStringSimpleExtensionGroup == null) {
            lineStringSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.lineStringSimpleExtensionGroup;
    }

    /**
     * Gets the value of the lineStringObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lineStringObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLineStringObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLineStringObjectExtensionGroup() {
        if (lineStringObjectExtensionGroup == null) {
            lineStringObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.lineStringObjectExtensionGroup;
    }

}
