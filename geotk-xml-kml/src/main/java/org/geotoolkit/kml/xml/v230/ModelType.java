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
 * <p>Classe Java pour ModelType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ModelType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/kml/2.2}AltitudeModeGroup"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Location" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Orientation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Scale" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Link" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ResourceMap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ModelSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ModelObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ModelType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "altitudeMode",
    "seaFloorAltitudeMode",
    "altitudeModeSimpleExtensionGroup",
    "altitudeModeObjectExtensionGroup",
    "location",
    "orientation",
    "scale",
    "link",
    "resourceMap",
    "modelSimpleExtensionGroup",
    "modelObjectExtensionGroup"
})
public class ModelType
    extends AbstractGeometryType
{

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
    @XmlElement(name = "Location", namespace = "http://www.opengis.net/kml/2.2")
    protected LocationType location;
    @XmlElement(name = "Orientation", namespace = "http://www.opengis.net/kml/2.2")
    protected OrientationType orientation;
    @XmlElement(name = "Scale", namespace = "http://www.opengis.net/kml/2.2")
    protected ScaleType scale;
    @XmlElement(name = "Link", namespace = "http://www.opengis.net/kml/2.2")
    protected LinkType link;
    @XmlElement(name = "ResourceMap", namespace = "http://www.opengis.net/kml/2.2")
    protected ResourceMapType resourceMap;
    @XmlElement(name = "ModelSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> modelSimpleExtensionGroup;
    @XmlElement(name = "ModelObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> modelObjectExtensionGroup;

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
     * Obtient la valeur de la propriété location.
     *
     * @return
     *     possible object is
     *     {@link LocationType }
     *
     */
    public LocationType getLocation() {
        return location;
    }

    /**
     * Définit la valeur de la propriété location.
     *
     * @param value
     *     allowed object is
     *     {@link LocationType }
     *
     */
    public void setLocation(LocationType value) {
        this.location = value;
    }

    /**
     * Obtient la valeur de la propriété orientation.
     *
     * @return
     *     possible object is
     *     {@link OrientationType }
     *
     */
    public OrientationType getOrientation() {
        return orientation;
    }

    /**
     * Définit la valeur de la propriété orientation.
     *
     * @param value
     *     allowed object is
     *     {@link OrientationType }
     *
     */
    public void setOrientation(OrientationType value) {
        this.orientation = value;
    }

    /**
     * Obtient la valeur de la propriété scale.
     *
     * @return
     *     possible object is
     *     {@link ScaleType }
     *
     */
    public ScaleType getScale() {
        return scale;
    }

    /**
     * Définit la valeur de la propriété scale.
     *
     * @param value
     *     allowed object is
     *     {@link ScaleType }
     *
     */
    public void setScale(ScaleType value) {
        this.scale = value;
    }

    /**
     * Obtient la valeur de la propriété link.
     *
     * @return
     *     possible object is
     *     {@link LinkType }
     *
     */
    public LinkType getLink() {
        return link;
    }

    /**
     * Définit la valeur de la propriété link.
     *
     * @param value
     *     allowed object is
     *     {@link LinkType }
     *
     */
    public void setLink(LinkType value) {
        this.link = value;
    }

    /**
     * Obtient la valeur de la propriété resourceMap.
     *
     * @return
     *     possible object is
     *     {@link ResourceMapType }
     *
     */
    public ResourceMapType getResourceMap() {
        return resourceMap;
    }

    /**
     * Définit la valeur de la propriété resourceMap.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceMapType }
     *
     */
    public void setResourceMap(ResourceMapType value) {
        this.resourceMap = value;
    }

    /**
     * Gets the value of the modelSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getModelSimpleExtensionGroup() {
        if (modelSimpleExtensionGroup == null) {
            modelSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.modelSimpleExtensionGroup;
    }

    /**
     * Gets the value of the modelObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getModelObjectExtensionGroup() {
        if (modelObjectExtensionGroup == null) {
            modelObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.modelObjectExtensionGroup;
    }

}
