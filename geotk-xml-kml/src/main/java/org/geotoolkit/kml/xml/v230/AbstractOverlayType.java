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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour AbstractOverlayType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AbstractOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}color" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}drawOrder" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Icon" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AbstractOverlayType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "color",
    "drawOrder",
    "icon",
    "abstractOverlaySimpleExtensionGroup",
    "abstractOverlayObjectExtensionGroup"
})
@XmlSeeAlso({
    ScreenOverlayType.class,
    GroundOverlayType.class,
    PhotoOverlayType.class
})
public abstract class AbstractOverlayType
    extends AbstractFeatureType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", type = String.class, defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] color;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Integer drawOrder;
    @XmlElement(name = "Icon", namespace = "http://www.opengis.net/kml/2.2")
    protected LinkType icon;
    @XmlElement(name = "AbstractOverlaySimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> abstractOverlaySimpleExtensionGroup;
    @XmlElement(name = "AbstractOverlayObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> abstractOverlayObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété color.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public byte[] getColor() {
        return color;
    }

    /**
     * Définit la valeur de la propriété color.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(byte[] value) {
        this.color = value;
    }

    /**
     * Obtient la valeur de la propriété drawOrder.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getDrawOrder() {
        return drawOrder;
    }

    /**
     * Définit la valeur de la propriété drawOrder.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setDrawOrder(Integer value) {
        this.drawOrder = value;
    }

    /**
     * Obtient la valeur de la propriété icon.
     *
     * @return
     *     possible object is
     *     {@link LinkType }
     *
     */
    public LinkType getIcon() {
        return icon;
    }

    /**
     * Définit la valeur de la propriété icon.
     *
     * @param value
     *     allowed object is
     *     {@link LinkType }
     *
     */
    public void setIcon(LinkType value) {
        this.icon = value;
    }

    /**
     * Gets the value of the abstractOverlaySimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractOverlaySimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractOverlaySimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAbstractOverlaySimpleExtensionGroup() {
        if (abstractOverlaySimpleExtensionGroup == null) {
            abstractOverlaySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractOverlaySimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractOverlayObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractOverlayObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractOverlayObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAbstractOverlayObjectExtensionGroup() {
        if (abstractOverlayObjectExtensionGroup == null) {
            abstractOverlayObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractOverlayObjectExtensionGroup;
    }

}
