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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour FlyToType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FlyToType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}duration" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractFlyToMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractViewGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}FlyToSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}FlyToObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "FlyToType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "duration",
    "abstractFlyToMode",
    "abstractViewGroup",
    "flyToSimpleExtensionGroup",
    "flyToObjectExtensionGroup"
})
public class FlyToType
    extends AbstractTourPrimitiveType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double duration;
    @XmlElementRef(name = "abstractFlyToMode", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractFlyToMode;
    @XmlElementRef(name = "AbstractViewGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractViewType> abstractViewGroup;
    @XmlElement(name = "FlyToSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> flyToSimpleExtensionGroup;
    @XmlElement(name = "FlyToObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> flyToObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété duration.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getDuration() {
        return duration;
    }

    /**
     * Définit la valeur de la propriété duration.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setDuration(Double value) {
        this.duration = value;
    }

    /**
     * Obtient la valeur de la propriété abstractFlyToMode.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FlyToModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractFlyToMode() {
        return abstractFlyToMode;
    }

    /**
     * Définit la valeur de la propriété abstractFlyToMode.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FlyToModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractFlyToMode(JAXBElement<?> value) {
        this.abstractFlyToMode = value;
    }

    /**
     * Obtient la valeur de la propriété abstractViewGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractViewType> getAbstractViewGroup() {
        return abstractViewGroup;
    }

    /**
     * Définit la valeur de la propriété abstractViewGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *
     */
    public void setAbstractViewGroup(JAXBElement<? extends AbstractViewType> value) {
        this.abstractViewGroup = value;
    }

    /**
     * Gets the value of the flyToSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flyToSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlyToSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getFlyToSimpleExtensionGroup() {
        if (flyToSimpleExtensionGroup == null) {
            flyToSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.flyToSimpleExtensionGroup;
    }

    /**
     * Gets the value of the flyToObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flyToObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlyToObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getFlyToObjectExtensionGroup() {
        if (flyToObjectExtensionGroup == null) {
            flyToObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.flyToObjectExtensionGroup;
    }

}
