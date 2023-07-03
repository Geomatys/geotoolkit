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
 * <p>Classe Java pour TourControlType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TourControlType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractPlayMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TourControlSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TourControlObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "TourControlType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractPlayMode",
    "tourControlSimpleExtensionGroup",
    "tourControlObjectExtensionGroup"
})
public class TourControlType
    extends AbstractTourPrimitiveType
{

    @XmlElementRef(name = "abstractPlayMode", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractPlayMode;
    @XmlElement(name = "TourControlSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> tourControlSimpleExtensionGroup;
    @XmlElement(name = "TourControlObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> tourControlObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractPlayMode.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link PlayModeEnumType }{@code >}
     *
     */
    public JAXBElement<?> getAbstractPlayMode() {
        return abstractPlayMode;
    }

    /**
     * Définit la valeur de la propriété abstractPlayMode.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link PlayModeEnumType }{@code >}
     *
     */
    public void setAbstractPlayMode(JAXBElement<?> value) {
        this.abstractPlayMode = value;
    }

    /**
     * Gets the value of the tourControlSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tourControlSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTourControlSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getTourControlSimpleExtensionGroup() {
        if (tourControlSimpleExtensionGroup == null) {
            tourControlSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.tourControlSimpleExtensionGroup;
    }

    /**
     * Gets the value of the tourControlObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tourControlObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTourControlObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getTourControlObjectExtensionGroup() {
        if (tourControlObjectExtensionGroup == null) {
            tourControlObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.tourControlObjectExtensionGroup;
    }

}
