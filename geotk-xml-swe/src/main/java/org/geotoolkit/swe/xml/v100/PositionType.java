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
package org.geotoolkit.swe.xml.v100;

import java.net.URI;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.Position;
import org.apache.sis.util.ComparisonMode;


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
 * @module
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

    public PositionType(final URI referenceFrame, final URI localFrame,final VectorPropertyType location) {
        super(referenceFrame, localFrame);
        this.location    = location;
    }


    public PositionType(final URI referenceFrame, final URI localFrame,final VectorPropertyType location,
            final VectorOrSquareMatrixPropertyType orientation) {
        super(referenceFrame, localFrame);
        this.location    = location;
        this.orientation = orientation;
    }

    public PositionType(final URI referenceFrame, final URI localFrame, final VectorType location) {
        super(referenceFrame, localFrame);
        this.location    = new VectorPropertyType(location);
    }


    public PositionType(final URI referenceFrame, final URI localFrame, final VectorType location,
            final VectorType orientation) {
        super(referenceFrame, localFrame);
        this.location    = new VectorPropertyType(location);
        this.orientation = new VectorOrSquareMatrixPropertyType(orientation);
    }

    public PositionType(final URI referenceFrame, final URI localFrame, final VectorType location,
            final SquareMatrixType orientation) {
        super(referenceFrame, localFrame);
        this.location    = new VectorPropertyType(location);
        this.orientation = new VectorOrSquareMatrixPropertyType(orientation);
    }

    public PositionType(final VectorPropertyType location, final VectorOrSquareMatrixPropertyType orientation) {
        this.location    = location;
        this.orientation = orientation;
    }

    public PositionType(final Position pos) {
        super(pos);
        if (pos != null) {
            if (pos.getAcceleration() != null) {
                this.acceleration = new VectorPropertyType(pos.getAcceleration());
            }
            if (pos.getAngularAcceleration() != null) {
                this.angularAcceleration = new VectorOrSquareMatrixPropertyType(pos.getAngularAcceleration());
            }
            if (pos.getAngularVelocity() != null) {
                this.angularVelocity = new VectorOrSquareMatrixPropertyType(pos.getAngularVelocity());
            }
            if (pos.getLocation() != null) {
                this.location = new VectorPropertyType(pos.getLocation());
            }
            if (pos.getOrientation() != null) {
                this.orientation = new VectorOrSquareMatrixPropertyType(pos.getOrientation());
            }
            if (pos.getState() != null) {
                this.state = new VectorOrSquareMatrixPropertyType(pos.getState());
            }
            if (pos.getVelocity() != null) {
                this.velocity = new VectorPropertyType(pos.getVelocity());
            }
            if (pos.getTime() != null) {
                this.time = new TimePropertyType(pos.getTime());
            }
        }
     }

    /**
     * Gets the value of the time property.
     *
     * @return
     *     possible object is
     *     {@link TimePropertyType }
     *
     */
    @Override
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
    public void setTime(final TimePropertyType value) {
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
    @Override
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
    public void setLocation(final VectorPropertyType value) {
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
    @Override
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
    public void setOrientation(final VectorOrSquareMatrixPropertyType value) {
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
    @Override
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
    public void setVelocity(final VectorPropertyType value) {
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
    @Override
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
    public void setAngularVelocity(final VectorOrSquareMatrixPropertyType value) {
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
    @Override
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
    public void setAcceleration(final VectorPropertyType value) {
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
    @Override
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
    public void setAngularAcceleration(final VectorOrSquareMatrixPropertyType value) {
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
    @Override
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
    public void setState(final VectorOrSquareMatrixPropertyType value) {
        this.state = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof PositionType && super.equals(object, mode)) {
            final PositionType  that = (PositionType ) object;
            return Objects.equals(this.acceleration,        that.acceleration)        &&
                   Objects.equals(this.angularAcceleration, that.angularAcceleration) &&
                   Objects.equals(this.angularVelocity,     that.angularVelocity)     &&
                   Objects.equals(this.location,            that.location)            &&
                   Objects.equals(this.orientation,         that.orientation)         &&
                   Objects.equals(this.state,               that.state)               &&
                   Objects.equals(this.time,                that.time)                &&
                   Objects.equals(this.velocity,            that.velocity);

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
