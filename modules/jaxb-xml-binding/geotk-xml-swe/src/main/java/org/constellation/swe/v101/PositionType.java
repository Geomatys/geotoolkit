/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008, Geomatys
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


package org.constellation.swe.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.constellation.swe.Position;


/**
 * <p>Java class for PositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PositionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractVectorType">
 *       &lt;sequence>
 *         &lt;element name="time" type="{http://www.opengis.net/swe/1.0.1}TimePropertyType" minOccurs="0"/>
 *         &lt;element name="location" type="{http://www.opengis.net/swe/1.0.1}VectorPropertyType" minOccurs="0"/>
 *         &lt;element name="orientation" type="{http://www.opengis.net/swe/1.0.1}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *         &lt;element name="velocity" type="{http://www.opengis.net/swe/1.0.1}VectorPropertyType" minOccurs="0"/>
 *         &lt;element name="angularVelocity" type="{http://www.opengis.net/swe/1.0.1}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *         &lt;element name="acceleration" type="{http://www.opengis.net/swe/1.0.1}VectorPropertyType" minOccurs="0"/>
 *         &lt;element name="angularAcceleration" type="{http://www.opengis.net/swe/1.0.1}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.opengis.net/swe/1.0.1}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PositionType", propOrder = {
    "time",
    "location",
    "orientation",
    "velocity",
    "angularVelocity",
    "acceleration",
    "angularAcceleration",
    "state"
})
public class PositionType extends AbstractVectorType implements Position {

    private TimePropertyType time;
    private VectorPropertyType location;
    private VectorOrSquareMatrixPropertyType orientation;
    private VectorPropertyType velocity;
    private VectorOrSquareMatrixPropertyType angularVelocity;
    private VectorPropertyType acceleration;
    private VectorOrSquareMatrixPropertyType angularAcceleration;
    private VectorOrSquareMatrixPropertyType state;

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link TimePropertyType }
     *     
     */
    public TimePropertyType getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePropertyType }
     *     
     */
    public void setTime(TimePropertyType value) {
        this.time = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link VectorPropertyType }
     *     
     */
    public VectorPropertyType getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorPropertyType }
     *     
     */
    public void setLocation(VectorPropertyType value) {
        this.location = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     * @return
     *     possible object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public VectorOrSquareMatrixPropertyType getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public void setOrientation(VectorOrSquareMatrixPropertyType value) {
        this.orientation = value;
    }

    /**
     * Gets the value of the velocity property.
     * 
     * @return
     *     possible object is
     *     {@link VectorPropertyType }
     *     
     */
    public VectorPropertyType getVelocity() {
        return velocity;
    }

    /**
     * Sets the value of the velocity property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorPropertyType }
     *     
     */
    public void setVelocity(VectorPropertyType value) {
        this.velocity = value;
    }

    /**
     * Gets the value of the angularVelocity property.
     * 
     * @return
     *     possible object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public VectorOrSquareMatrixPropertyType getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * Sets the value of the angularVelocity property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public void setAngularVelocity(VectorOrSquareMatrixPropertyType value) {
        this.angularVelocity = value;
    }

    /**
     * Gets the value of the acceleration property.
     * 
     * @return
     *     possible object is
     *     {@link VectorPropertyType }
     *     
     */
    public VectorPropertyType getAcceleration() {
        return acceleration;
    }

    /**
     * Sets the value of the acceleration property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorPropertyType }
     *     
     */
    public void setAcceleration(VectorPropertyType value) {
        this.acceleration = value;
    }

    /**
     * Gets the value of the angularAcceleration property.
     * 
     * @return
     *     possible object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public VectorOrSquareMatrixPropertyType getAngularAcceleration() {
        return angularAcceleration;
    }

    /**
     * Sets the value of the angularAcceleration property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public void setAngularAcceleration(VectorOrSquareMatrixPropertyType value) {
        this.angularAcceleration = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public VectorOrSquareMatrixPropertyType getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorOrSquareMatrixPropertyType }
     *     
     */
    public void setState(VectorOrSquareMatrixPropertyType value) {
        this.state = value;
    }

}
