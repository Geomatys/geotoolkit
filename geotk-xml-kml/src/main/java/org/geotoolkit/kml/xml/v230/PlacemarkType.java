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
 * <p>Classe Java pour PlacemarkType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PlacemarkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractGeometryGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PlacemarkSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PlacemarkObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PlacemarkType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractGeometryGroup",
    "placemarkSimpleExtensionGroup",
    "placemarkObjectExtensionGroup"
})
public class PlacemarkType
    extends AbstractFeatureType
{

    @XmlElementRef(name = "AbstractGeometryGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractGeometryType> abstractGeometryGroup;
    @XmlElement(name = "PlacemarkSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> placemarkSimpleExtensionGroup;
    @XmlElement(name = "PlacemarkObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> placemarkObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractGeometryGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ModelType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiTrackType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TrackType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractGeometryType> getAbstractGeometryGroup() {
        return abstractGeometryGroup;
    }

    /**
     * Définit la valeur de la propriété abstractGeometryGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ModelType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiTrackType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TrackType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *
     */
    public void setAbstractGeometryGroup(JAXBElement<? extends AbstractGeometryType> value) {
        this.abstractGeometryGroup = value;
    }

    /**
     * Gets the value of the placemarkSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the placemarkSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlacemarkSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getPlacemarkSimpleExtensionGroup() {
        if (placemarkSimpleExtensionGroup == null) {
            placemarkSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.placemarkSimpleExtensionGroup;
    }

    /**
     * Gets the value of the placemarkObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the placemarkObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlacemarkObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getPlacemarkObjectExtensionGroup() {
        if (placemarkObjectExtensionGroup == null) {
            placemarkObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.placemarkObjectExtensionGroup;
    }

}
