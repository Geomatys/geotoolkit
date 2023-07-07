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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ScreenOverlayType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ScreenOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractOverlayType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}overlayXY" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}screenXY" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rotationXY" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}size" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rotation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ScreenOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ScreenOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ScreenOverlayType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "overlayXY",
    "screenXY",
    "rotationXY",
    "size",
    "rotation",
    "screenOverlaySimpleExtensionGroup",
    "screenOverlayObjectExtensionGroup"
})
public class ScreenOverlayType
    extends AbstractOverlayType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected Vec2Type overlayXY;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected Vec2Type screenXY;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected Vec2Type rotationXY;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected Vec2Type size;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double rotation;
    @XmlElement(name = "ScreenOverlaySimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> screenOverlaySimpleExtensionGroup;
    @XmlElement(name = "ScreenOverlayObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> screenOverlayObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété overlayXY.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getOverlayXY() {
        return overlayXY;
    }

    /**
     * Définit la valeur de la propriété overlayXY.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setOverlayXY(Vec2Type value) {
        this.overlayXY = value;
    }

    /**
     * Obtient la valeur de la propriété screenXY.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getScreenXY() {
        return screenXY;
    }

    /**
     * Définit la valeur de la propriété screenXY.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setScreenXY(Vec2Type value) {
        this.screenXY = value;
    }

    /**
     * Obtient la valeur de la propriété rotationXY.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getRotationXY() {
        return rotationXY;
    }

    /**
     * Définit la valeur de la propriété rotationXY.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setRotationXY(Vec2Type value) {
        this.rotationXY = value;
    }

    /**
     * Obtient la valeur de la propriété size.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getSize() {
        return size;
    }

    /**
     * Définit la valeur de la propriété size.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setSize(Vec2Type value) {
        this.size = value;
    }

    /**
     * Obtient la valeur de la propriété rotation.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRotation() {
        return rotation;
    }

    /**
     * Définit la valeur de la propriété rotation.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRotation(Double value) {
        this.rotation = value;
    }

    /**
     * Gets the value of the screenOverlaySimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the screenOverlaySimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScreenOverlaySimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getScreenOverlaySimpleExtensionGroup() {
        if (screenOverlaySimpleExtensionGroup == null) {
            screenOverlaySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.screenOverlaySimpleExtensionGroup;
    }

    /**
     * Gets the value of the screenOverlayObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the screenOverlayObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScreenOverlayObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getScreenOverlayObjectExtensionGroup() {
        if (screenOverlayObjectExtensionGroup == null) {
            screenOverlayObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.screenOverlayObjectExtensionGroup;
    }

}
