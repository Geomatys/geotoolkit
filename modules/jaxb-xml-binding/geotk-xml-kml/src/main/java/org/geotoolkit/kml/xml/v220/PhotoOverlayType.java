/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.kml.xml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PhotoOverlayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}shape" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PhotoOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PhotoOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhotoOverlayType", propOrder = {
    "rotation",
    "viewVolume",
    "imagePyramid",
    "point",
    "shape",
    "photoOverlaySimpleExtensionGroup",
    "photoOverlayObjectExtensionGroup"
})
public class PhotoOverlayType extends AbstractOverlayType {

    @XmlElement(defaultValue = "0.0")
    protected Double rotation;
    @XmlElement(name = "ViewVolume")
    protected ViewVolumeType viewVolume;
    @XmlElement(name = "ImagePyramid")
    protected ImagePyramidType imagePyramid;
    @XmlElement(name = "Point")
    protected PointType point;
    @XmlElement(defaultValue = "rectangle")
    protected ShapeEnumType shape;
    @XmlElement(name = "PhotoOverlaySimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> photoOverlaySimpleExtensionGroup;
    @XmlElement(name = "PhotoOverlayObjectExtensionGroup")
    protected List<AbstractObjectType> photoOverlayObjectExtensionGroup;

    /**
     * Gets the value of the rotation property.
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
     * Sets the value of the rotation property.
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
     * Gets the value of the viewVolume property.
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
     * Sets the value of the viewVolume property.
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
     * Gets the value of the imagePyramid property.
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
     * Sets the value of the imagePyramid property.
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
     * Gets the value of the point property.
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
     * Sets the value of the point property.
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
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link ShapeEnumType }
     *     
     */
    public ShapeEnumType getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShapeEnumType }
     *     
     */
    public void setShape(ShapeEnumType value) {
        this.shape = value;
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
