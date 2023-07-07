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
 * <p>Classe Java pour PhotoOverlayType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PhotoOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractOverlayType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rotation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ViewVolume" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ImagePyramid" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Point" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractShape" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PhotoOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PhotoOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PhotoOverlayType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "rotation",
    "viewVolume",
    "imagePyramid",
    "point",
    "abstractShape",
    "photoOverlaySimpleExtensionGroup",
    "photoOverlayObjectExtensionGroup"
})
public class PhotoOverlayType
    extends AbstractOverlayType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double rotation;
    @XmlElement(name = "ViewVolume", namespace = "http://www.opengis.net/kml/2.2")
    protected ViewVolumeType viewVolume;
    @XmlElement(name = "ImagePyramid", namespace = "http://www.opengis.net/kml/2.2")
    protected ImagePyramidType imagePyramid;
    @XmlElement(name = "Point", namespace = "http://www.opengis.net/kml/2.2")
    protected PointType point;
    @XmlElementRef(name = "abstractShape", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractShape;
    @XmlElement(name = "PhotoOverlaySimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> photoOverlaySimpleExtensionGroup;
    @XmlElement(name = "PhotoOverlayObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> photoOverlayObjectExtensionGroup;

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
     * Obtient la valeur de la propriété viewVolume.
     *
     * @return
     *     possible object is
     *     {@link ViewVolumeType }
     *
     */
    public ViewVolumeType getViewVolume() {
        return viewVolume;
    }

    /**
     * Définit la valeur de la propriété viewVolume.
     *
     * @param value
     *     allowed object is
     *     {@link ViewVolumeType }
     *
     */
    public void setViewVolume(ViewVolumeType value) {
        this.viewVolume = value;
    }

    /**
     * Obtient la valeur de la propriété imagePyramid.
     *
     * @return
     *     possible object is
     *     {@link ImagePyramidType }
     *
     */
    public ImagePyramidType getImagePyramid() {
        return imagePyramid;
    }

    /**
     * Définit la valeur de la propriété imagePyramid.
     *
     * @param value
     *     allowed object is
     *     {@link ImagePyramidType }
     *
     */
    public void setImagePyramid(ImagePyramidType value) {
        this.imagePyramid = value;
    }

    /**
     * Obtient la valeur de la propriété point.
     *
     * @return
     *     possible object is
     *     {@link PointType }
     *
     */
    public PointType getPoint() {
        return point;
    }

    /**
     * Définit la valeur de la propriété point.
     *
     * @param value
     *     allowed object is
     *     {@link PointType }
     *
     */
    public void setPoint(PointType value) {
        this.point = value;
    }

    /**
     * Obtient la valeur de la propriété abstractShape.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link ShapeEnumType }{@code >}
     *
     */
    public JAXBElement<?> getAbstractShape() {
        return abstractShape;
    }

    /**
     * Définit la valeur de la propriété abstractShape.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link ShapeEnumType }{@code >}
     *
     */
    public void setAbstractShape(JAXBElement<?> value) {
        this.abstractShape = value;
    }

    /**
     * Gets the value of the photoOverlaySimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the photoOverlaySimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhotoOverlaySimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getPhotoOverlaySimpleExtensionGroup() {
        if (photoOverlaySimpleExtensionGroup == null) {
            photoOverlaySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.photoOverlaySimpleExtensionGroup;
    }

    /**
     * Gets the value of the photoOverlayObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the photoOverlayObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhotoOverlayObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getPhotoOverlayObjectExtensionGroup() {
        if (photoOverlayObjectExtensionGroup == null) {
            photoOverlayObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.photoOverlayObjectExtensionGroup;
    }

}
