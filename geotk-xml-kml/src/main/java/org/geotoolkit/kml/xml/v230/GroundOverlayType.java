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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour GroundOverlayType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GroundOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractOverlayType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitude" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/kml/2.2}AltitudeModeGroup"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractExtentGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}GroundOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}GroundOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "GroundOverlayType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "altitude",
    "altitudeMode",
    "seaFloorAltitudeMode",
    "altitudeModeSimpleExtensionGroup",
    "altitudeModeObjectExtensionGroup",
    "abstractExtentGroup",
    "groundOverlaySimpleExtensionGroup",
    "groundOverlayObjectExtensionGroup"
})
public class GroundOverlayType
    extends AbstractOverlayType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double altitude;
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
    @XmlElementRef(name = "AbstractExtentGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractExtentType> abstractExtentGroup;
    @XmlElement(name = "GroundOverlaySimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> groundOverlaySimpleExtensionGroup;
    @XmlElement(name = "GroundOverlayObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> groundOverlayObjectExtensionGroup;

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
     * Obtient la valeur de la propriété abstractExtentGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractExtentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonQuadType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonAltBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractLatLonBoxType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractExtentType> getAbstractExtentGroup() {
        return abstractExtentGroup;
    }

    /**
     * Définit la valeur de la propriété abstractExtentGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractExtentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonQuadType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonAltBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractLatLonBoxType }{@code >}
     *
     */
    public void setAbstractExtentGroup(JAXBElement<? extends AbstractExtentType> value) {
        this.abstractExtentGroup = value;
    }

    /**
     * Gets the value of the groundOverlaySimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groundOverlaySimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroundOverlaySimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getGroundOverlaySimpleExtensionGroup() {
        if (groundOverlaySimpleExtensionGroup == null) {
            groundOverlaySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.groundOverlaySimpleExtensionGroup;
    }

    /**
     * Gets the value of the groundOverlayObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groundOverlayObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroundOverlayObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getGroundOverlayObjectExtensionGroup() {
        if (groundOverlayObjectExtensionGroup == null) {
            groundOverlayObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.groundOverlayObjectExtensionGroup;
    }

}
