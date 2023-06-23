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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour TrackType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TrackType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}extrude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tessellate" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/kml/2.2}AltitudeModeGroup"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}when" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}coord" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}angles" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Model" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ExtendedData" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TrackSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TrackObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "TrackType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "extrude",
    "tessellate",
    "altitudeMode",
    "seaFloorAltitudeMode",
    "altitudeModeSimpleExtensionGroup",
    "altitudeModeObjectExtensionGroup",
    "when",
    "coord",
    "angles",
    "model",
    "extendedData",
    "trackSimpleExtensionGroup",
    "trackObjectExtensionGroup"
})
public class TrackType
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
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected List<String> when;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected List<String> coord;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected List<String> angles;
    @XmlElement(name = "Model", namespace = "http://www.opengis.net/kml/2.2")
    protected ModelType model;
    @XmlElement(name = "ExtendedData", namespace = "http://www.opengis.net/kml/2.2")
    protected ExtendedDataType extendedData;
    @XmlElement(name = "TrackSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> trackSimpleExtensionGroup;
    @XmlElement(name = "TrackObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> trackObjectExtensionGroup;

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
     * Gets the value of the when property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the when property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWhen().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getWhen() {
        if (when == null) {
            when = new ArrayList<String>();
        }
        return this.when;
    }

    /**
     * Gets the value of the coord property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coord property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoord().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCoord() {
        if (coord == null) {
            coord = new ArrayList<String>();
        }
        return this.coord;
    }

    /**
     * Gets the value of the angles property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the angles property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAngles().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAngles() {
        if (angles == null) {
            angles = new ArrayList<String>();
        }
        return this.angles;
    }

    /**
     * Obtient la valeur de la propriété model.
     *
     * @return
     *     possible object is
     *     {@link ModelType }
     *
     */
    public ModelType getModel() {
        return model;
    }

    /**
     * Définit la valeur de la propriété model.
     *
     * @param value
     *     allowed object is
     *     {@link ModelType }
     *
     */
    public void setModel(ModelType value) {
        this.model = value;
    }

    /**
     * Obtient la valeur de la propriété extendedData.
     *
     * @return
     *     possible object is
     *     {@link ExtendedDataType }
     *
     */
    public ExtendedDataType getExtendedData() {
        return extendedData;
    }

    /**
     * Définit la valeur de la propriété extendedData.
     *
     * @param value
     *     allowed object is
     *     {@link ExtendedDataType }
     *
     */
    public void setExtendedData(ExtendedDataType value) {
        this.extendedData = value;
    }

    /**
     * Gets the value of the trackSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trackSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrackSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getTrackSimpleExtensionGroup() {
        if (trackSimpleExtensionGroup == null) {
            trackSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.trackSimpleExtensionGroup;
    }

    /**
     * Gets the value of the trackObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trackObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrackObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getTrackObjectExtensionGroup() {
        if (trackObjectExtensionGroup == null) {
            trackObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.trackObjectExtensionGroup;
    }

}
