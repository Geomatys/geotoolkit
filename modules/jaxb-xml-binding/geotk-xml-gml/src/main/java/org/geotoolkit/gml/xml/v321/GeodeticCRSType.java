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
 * gml:GeodeticCRS is a coordinate reference system based on a geodetic datum.
 * 
 * <p>Java class for GeodeticCRSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeodeticCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCRSType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}ellipsoidalCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}cartesianCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}sphericalCSProperty"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}geodeticDatumProperty"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeodeticCRSType", propOrder = {
    "ellipsoidalCSProperty",
    "cartesianCSProperty",
    "sphericalCSProperty",
    "geodeticDatumProperty"
})
public class GeodeticCRSType
    extends AbstractCRSType
{

    @XmlElementRef(name = "ellipsoidalCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<EllipsoidalCSPropertyType> ellipsoidalCSProperty;
    @XmlElementRef(name = "cartesianCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<CartesianCSPropertyType> cartesianCSProperty;
    @XmlElementRef(name = "sphericalCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<SphericalCSPropertyType> sphericalCSProperty;
    @XmlElementRef(name = "geodeticDatumProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<GeodeticDatumPropertyType> geodeticDatumProperty;

    /**
     * Gets the value of the ellipsoidalCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EllipsoidalCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EllipsoidalCSPropertyType }{@code >}
     *     
     */
    public JAXBElement<EllipsoidalCSPropertyType> getEllipsoidalCSProperty() {
        return ellipsoidalCSProperty;
    }

    /**
     * Sets the value of the ellipsoidalCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EllipsoidalCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EllipsoidalCSPropertyType }{@code >}
     *     
     */
    public void setEllipsoidalCSProperty(JAXBElement<EllipsoidalCSPropertyType> value) {
        this.ellipsoidalCSProperty = ((JAXBElement<EllipsoidalCSPropertyType> ) value);
    }

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
     * Gets the value of the sphericalCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SphericalCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SphericalCSPropertyType }{@code >}
     *     
     */
    public JAXBElement<SphericalCSPropertyType> getSphericalCSProperty() {
        return sphericalCSProperty;
    }

    /**
     * Sets the value of the sphericalCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SphericalCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SphericalCSPropertyType }{@code >}
     *     
     */
    public void setSphericalCSProperty(JAXBElement<SphericalCSPropertyType> value) {
        this.sphericalCSProperty = ((JAXBElement<SphericalCSPropertyType> ) value);
    }

    /**
     * Gets the value of the geodeticDatumProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GeodeticDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeodeticDatumPropertyType }{@code >}
     *     
     */
    public JAXBElement<GeodeticDatumPropertyType> getGeodeticDatumProperty() {
        return geodeticDatumProperty;
    }

    /**
     * Sets the value of the geodeticDatumProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GeodeticDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeodeticDatumPropertyType }{@code >}
     *     
     */
    public void setGeodeticDatumProperty(JAXBElement<GeodeticDatumPropertyType> value) {
        this.geodeticDatumProperty = ((JAXBElement<GeodeticDatumPropertyType> ) value);
    }

}
