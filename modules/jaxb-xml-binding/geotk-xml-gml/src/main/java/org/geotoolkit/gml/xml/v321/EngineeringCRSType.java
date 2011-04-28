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
 * <p>Java class for EngineeringCRSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EngineeringCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCRSType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}affineCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}cartesianCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}cylindricalCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}linearCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}polarCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}sphericalCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}userDefinedCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}coordinateSystem"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}engineeringDatumProperty"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EngineeringCRSType", propOrder = {
    "affineCSProperty",
    "cartesianCSProperty",
    "cylindricalCSProperty",
    "linearCSProperty",
    "polarCSProperty",
    "sphericalCSProperty",
    "userDefinedCSProperty",
    "coordinateSystem",
    "engineeringDatumProperty"
})
public class EngineeringCRSType
    extends AbstractCRSType
{

    @XmlElementRef(name = "affineCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<AffineCSPropertyType> affineCSProperty;
    @XmlElementRef(name = "cartesianCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<CartesianCSPropertyType> cartesianCSProperty;
    private CylindricalCSPropertyType cylindricalCSProperty;
    private LinearCSPropertyType linearCSProperty;
    private PolarCSPropertyType polarCSProperty;
    @XmlElementRef(name = "sphericalCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<SphericalCSPropertyType> sphericalCSProperty;
    private UserDefinedCSPropertyType userDefinedCSProperty;
    @XmlElementRef(name = "coordinateSystem", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<CoordinateSystemPropertyType> coordinateSystem;
    @XmlElementRef(name = "engineeringDatumProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<EngineeringDatumPropertyType> engineeringDatumProperty;

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
     * Gets the value of the cylindricalCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link CylindricalCSPropertyType }
     *     
     */
    public CylindricalCSPropertyType getCylindricalCSProperty() {
        return cylindricalCSProperty;
    }

    /**
     * Sets the value of the cylindricalCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link CylindricalCSPropertyType }
     *     
     */
    public void setCylindricalCSProperty(CylindricalCSPropertyType value) {
        this.cylindricalCSProperty = value;
    }

    /**
     * Gets the value of the linearCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link LinearCSPropertyType }
     *     
     */
    public LinearCSPropertyType getLinearCSProperty() {
        return linearCSProperty;
    }

    /**
     * Sets the value of the linearCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinearCSPropertyType }
     *     
     */
    public void setLinearCSProperty(LinearCSPropertyType value) {
        this.linearCSProperty = value;
    }

    /**
     * Gets the value of the polarCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link PolarCSPropertyType }
     *     
     */
    public PolarCSPropertyType getPolarCSProperty() {
        return polarCSProperty;
    }

    /**
     * Sets the value of the polarCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolarCSPropertyType }
     *     
     */
    public void setPolarCSProperty(PolarCSPropertyType value) {
        this.polarCSProperty = value;
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
     * Gets the value of the userDefinedCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link UserDefinedCSPropertyType }
     *     
     */
    public UserDefinedCSPropertyType getUserDefinedCSProperty() {
        return userDefinedCSProperty;
    }

    /**
     * Sets the value of the userDefinedCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserDefinedCSPropertyType }
     *     
     */
    public void setUserDefinedCSProperty(UserDefinedCSPropertyType value) {
        this.userDefinedCSProperty = value;
    }

    /**
     * Gets the value of the coordinateSystem property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CoordinateSystemPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CoordinateSystemPropertyType }{@code >}
     *     
     */
    public JAXBElement<CoordinateSystemPropertyType> getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Sets the value of the coordinateSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CoordinateSystemPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CoordinateSystemPropertyType }{@code >}
     *     
     */
    public void setCoordinateSystem(JAXBElement<CoordinateSystemPropertyType> value) {
        this.coordinateSystem = ((JAXBElement<CoordinateSystemPropertyType> ) value);
    }

    /**
     * Gets the value of the engineeringDatumProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EngineeringDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EngineeringDatumPropertyType }{@code >}
     *     
     */
    public JAXBElement<EngineeringDatumPropertyType> getEngineeringDatumProperty() {
        return engineeringDatumProperty;
    }

    /**
     * Sets the value of the engineeringDatumProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EngineeringDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EngineeringDatumPropertyType }{@code >}
     *     
     */
    public void setEngineeringDatumProperty(JAXBElement<EngineeringDatumPropertyType> value) {
        this.engineeringDatumProperty = ((JAXBElement<EngineeringDatumPropertyType> ) value);
    }

}
