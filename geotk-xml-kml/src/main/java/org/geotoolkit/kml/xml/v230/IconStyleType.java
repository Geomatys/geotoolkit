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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour IconStyleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="IconStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractColorStyleType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}scale" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}heading" minOccurs="0"/>
 *         &lt;element name="Icon" type="{http://www.opengis.net/kml/2.2}BasicLinkType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}hotSpot" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}IconStyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}IconStyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "IconStyleType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "scale",
    "heading",
    "icon",
    "hotSpot",
    "iconStyleSimpleExtensionGroup",
    "iconStyleObjectExtensionGroup"
})
public class IconStyleType
    extends AbstractColorStyleType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "1.0")
    protected Double scale;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double heading;
    @XmlElement(name = "Icon", namespace = "http://www.opengis.net/kml/2.2")
    protected BasicLinkType icon;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected Vec2Type hotSpot;
    @XmlElement(name = "IconStyleSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> iconStyleSimpleExtensionGroup;
    @XmlElement(name = "IconStyleObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> iconStyleObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété scale.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getScale() {
        return scale;
    }

    /**
     * Définit la valeur de la propriété scale.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setScale(Double value) {
        this.scale = value;
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
     * Obtient la valeur de la propriété icon.
     *
     * @return
     *     possible object is
     *     {@link BasicLinkType }
     *
     */
    public BasicLinkType getIcon() {
        return icon;
    }

    /**
     * Définit la valeur de la propriété icon.
     *
     * @param value
     *     allowed object is
     *     {@link BasicLinkType }
     *
     */
    public void setIcon(BasicLinkType value) {
        this.icon = value;
    }

    /**
     * Obtient la valeur de la propriété hotSpot.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getHotSpot() {
        return hotSpot;
    }

    /**
     * Définit la valeur de la propriété hotSpot.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setHotSpot(Vec2Type value) {
        this.hotSpot = value;
    }

    /**
     * Gets the value of the iconStyleSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the iconStyleSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIconStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getIconStyleSimpleExtensionGroup() {
        if (iconStyleSimpleExtensionGroup == null) {
            iconStyleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.iconStyleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the iconStyleObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the iconStyleObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIconStyleObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getIconStyleObjectExtensionGroup() {
        if (iconStyleObjectExtensionGroup == null) {
            iconStyleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.iconStyleObjectExtensionGroup;
    }

}
