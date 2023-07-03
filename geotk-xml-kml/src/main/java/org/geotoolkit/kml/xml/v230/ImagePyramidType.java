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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ImagePyramidType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ImagePyramidType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tileSize" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxWidth" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxHeight" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractGridOrigin" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ImagePyramidSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ImagePyramidObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ImagePyramidType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "tileSize",
    "maxWidth",
    "maxHeight",
    "abstractGridOrigin",
    "imagePyramidSimpleExtensionGroup",
    "imagePyramidObjectExtensionGroup"
})
public class ImagePyramidType
    extends AbstractObjectType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "256")
    protected Integer tileSize;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Integer maxWidth;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Integer maxHeight;
    @XmlElementRef(name = "abstractGridOrigin", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractGridOrigin;
    @XmlElement(name = "ImagePyramidSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> imagePyramidSimpleExtensionGroup;
    @XmlElement(name = "ImagePyramidObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> imagePyramidObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété tileSize.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getTileSize() {
        return tileSize;
    }

    /**
     * Définit la valeur de la propriété tileSize.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setTileSize(Integer value) {
        this.tileSize = value;
    }

    /**
     * Obtient la valeur de la propriété maxWidth.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaxWidth() {
        return maxWidth;
    }

    /**
     * Définit la valeur de la propriété maxWidth.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaxWidth(Integer value) {
        this.maxWidth = value;
    }

    /**
     * Obtient la valeur de la propriété maxHeight.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaxHeight() {
        return maxHeight;
    }

    /**
     * Définit la valeur de la propriété maxHeight.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaxHeight(Integer value) {
        this.maxHeight = value;
    }

    /**
     * Obtient la valeur de la propriété abstractGridOrigin.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GridOriginEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractGridOrigin() {
        return abstractGridOrigin;
    }

    /**
     * Définit la valeur de la propriété abstractGridOrigin.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GridOriginEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractGridOrigin(JAXBElement<?> value) {
        this.abstractGridOrigin = value;
    }

    /**
     * Gets the value of the imagePyramidSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imagePyramidSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImagePyramidSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getImagePyramidSimpleExtensionGroup() {
        if (imagePyramidSimpleExtensionGroup == null) {
            imagePyramidSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.imagePyramidSimpleExtensionGroup;
    }

    /**
     * Gets the value of the imagePyramidObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imagePyramidObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImagePyramidObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getImagePyramidObjectExtensionGroup() {
        if (imagePyramidObjectExtensionGroup == null) {
            imagePyramidObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.imagePyramidObjectExtensionGroup;
    }

}
