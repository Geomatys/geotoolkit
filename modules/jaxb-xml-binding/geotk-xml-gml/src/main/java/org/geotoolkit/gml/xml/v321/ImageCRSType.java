/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImageCRSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCRSType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}cartesianCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}affineCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}usesObliqueCartesianCS"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}imageDatumProperty"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageCRSType", propOrder = {
    "cartesianCSProperty",
    "affineCSProperty",
    "usesObliqueCartesianCS",
    "imageDatumProperty"
})
public class ImageCRSType
    extends AbstractCRSType
{

    @XmlElementRef(name = "cartesianCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<CartesianCSPropertyType> cartesianCSProperty;
    @XmlElementRef(name = "affineCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<AffineCSPropertyType> affineCSProperty;
    private ObliqueCartesianCSPropertyType usesObliqueCartesianCS;
    @XmlElementRef(name = "imageDatumProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<ImageDatumPropertyType> imageDatumProperty;

    /**
     * Gets the value of the cartesianCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CartesianCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CartesianCSPropertyType }{@code >}
     *     
     */
    public JAXBElement<CartesianCSPropertyType> getCartesianCSProperty() {
        return cartesianCSProperty;
    }

    /**
     * Sets the value of the cartesianCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CartesianCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CartesianCSPropertyType }{@code >}
     *     
     */
    public void setCartesianCSProperty(JAXBElement<CartesianCSPropertyType> value) {
        this.cartesianCSProperty = ((JAXBElement<CartesianCSPropertyType> ) value);
    }

    /**
     * Gets the value of the affineCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AffineCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AffineCSPropertyType }{@code >}
     *     
     */
    public JAXBElement<AffineCSPropertyType> getAffineCSProperty() {
        return affineCSProperty;
    }

    /**
     * Sets the value of the affineCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AffineCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AffineCSPropertyType }{@code >}
     *     
     */
    public void setAffineCSProperty(JAXBElement<AffineCSPropertyType> value) {
        this.affineCSProperty = ((JAXBElement<AffineCSPropertyType> ) value);
    }

    /**
     * Gets the value of the usesObliqueCartesianCS property.
     * 
     * @return
     *     possible object is
     *     {@link ObliqueCartesianCSPropertyType }
     *     
     */
    public ObliqueCartesianCSPropertyType getUsesObliqueCartesianCS() {
        return usesObliqueCartesianCS;
    }

    /**
     * Sets the value of the usesObliqueCartesianCS property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObliqueCartesianCSPropertyType }
     *     
     */
    public void setUsesObliqueCartesianCS(ObliqueCartesianCSPropertyType value) {
        this.usesObliqueCartesianCS = value;
    }

    /**
     * Gets the value of the imageDatumProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ImageDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ImageDatumPropertyType }{@code >}
     *     
     */
    public JAXBElement<ImageDatumPropertyType> getImageDatumProperty() {
        return imageDatumProperty;
    }

    /**
     * Sets the value of the imageDatumProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ImageDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ImageDatumPropertyType }{@code >}
     *     
     */
    public void setImageDatumProperty(JAXBElement<ImageDatumPropertyType> value) {
        this.imageDatumProperty = ((JAXBElement<ImageDatumPropertyType> ) value);
    }

}
