/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.Position;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for PositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PositionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractVectorType">
 *       &lt;sequence>
 *         &lt;element name="time" type="{http://www.opengis.net/swe/1.0}TimePropertyType" minOccurs="0"/>
 *         &lt;element name="location" type="{http://www.opengis.net/swe/1.0}VectorPropertyType" minOccurs="0"/>
 *         &lt;element name="orientation" type="{http://www.opengis.net/swe/1.0}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *         &lt;element name="velocity" type="{http://www.opengis.net/swe/1.0}VectorPropertyType" minOccurs="0"/>
 *         &lt;element name="angularVelocity" type="{http://www.opengis.net/swe/1.0}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *         &lt;element name="acceleration" type="{http://www.opengis.net/swe/1.0}VectorPropertyType" minOccurs="0"/>
 *         &lt;element name="angularAcceleration" type="{http://www.opengis.net/swe/1.0}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.opengis.net/swe/1.0}VectorOrSquareMatrixPropertyType" minOccurs="0"/>
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

    public PositionType() {

    }

    public PositionType(String referenceFrame, String localFrame,VectorPropertyType location) {
        super(referenceFrame, localFrame);
        this.location    = location;
    }


    public PositionType(String referenceFrame, String localFrame,VectorPropertyType location,
            VectorOrSquareMatrixPropertyType orientation) {
        super(referenceFrame, localFrame);
        this.location    = location;
        this.orientation = orientation;
    }

    public PositionType(String referenceFrame, String localFrame, VectorType location) {
        super(referenceFrame, localFrame);
        this.location    = new VectorPropertyType(location);
    }


    public PositionType(String referenceFrame, String localFrame, VectorType location,
            VectorType orientation) {
        super(referenceFrame, localFrame);
        this.location    = new VectorPropertyType(location);
        this.orientation = new VectorOrSquareMatrixPropertyType(orientation);
    }

    public PositionType(String referenceFrame, String localFrame, VectorType location,
            SquareMatrixType orientation) {
        super(referenceFrame, localFrame);
        this.location    = new VectorPropertyType(location);
        this.orientation = new VectorOrSquareMatrixPropertyType(orientation);
    }
    
    public PositionType(VectorPropertyType location, VectorOrSquareMatrixPropertyType orientation) {
        this.location    = location;
        this.orientation = orientation;
    }

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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof PositionType && super.equals(object)) {
            final PositionType  that = (PositionType ) object;
            return Utilities.equals(this.acceleration,        that.acceleration)        &&
                   Utilities.equals(this.angularAcceleration, that.angularAcceleration) &&
                   Utilities.equals(this.angularVelocity,     that.angularVelocity)     &&
                   Utilities.equals(this.location,            that.location)            &&
                   Utilities.equals(this.orientation,         that.orientation)         &&
                   Utilities.equals(this.state,               that.state)               &&
                   Utilities.equals(this.time,                that.time)                &&
                   Utilities.equals(this.velocity,            that.velocity);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 29 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 29 * hash + (this.orientation != null ? this.orientation.hashCode() : 0);
        hash = 29 * hash + (this.velocity != null ? this.velocity.hashCode() : 0);
        hash = 29 * hash + (this.angularVelocity != null ? this.angularVelocity.hashCode() : 0);
        hash = 29 * hash + (this.acceleration != null ? this.acceleration.hashCode() : 0);
        hash = 29 * hash + (this.angularAcceleration != null ? this.angularAcceleration.hashCode() : 0);
        hash = 29 * hash + (this.state != null ? this.state.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (acceleration != null) {
            s.append("acceleration:").append(acceleration).append('\n');
        }
        if (angularAcceleration != null) {
            s.append("angularAcceleration:").append(angularAcceleration).append('\n');
        }
        if (angularVelocity != null) {
            s.append("angularVelocity:").append(angularVelocity).append('\n');
        }
        if (location != null) {
            s.append("location:").append(location).append('\n');
        }
        if (orientation != null) {
            s.append("orientation:").append(orientation).append('\n');
        }
        if (state != null) {
            s.append("state:").append(state).append('\n');
        }
        if (time != null) {
            s.append("time:").append(time).append('\n');
        }
        if (velocity != null) {
            s.append("velocity:").append(velocity).append('\n');
        }
        return s.toString();
    }
}
