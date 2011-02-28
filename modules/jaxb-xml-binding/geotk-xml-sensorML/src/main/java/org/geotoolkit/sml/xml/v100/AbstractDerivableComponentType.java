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
package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractDerivableComponent;
import org.geotoolkit.sml.xml.AbstractLocation;
import org.geotoolkit.sml.xml.AbstractPosition;
import org.geotoolkit.util.Utilities;


/**
 * Complex Type to allow creation of component profiles by extension
 * 
 * <p>Java class for AbstractDerivableComponentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDerivableComponentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}spatialReferenceFrame" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}temporalReferenceFrame" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0}location"/>
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0}position"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}timePosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}interfaces" minOccurs="0"/>
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
@XmlType(name = "AbstractDerivableComponentType", propOrder = {
    "location",
    "spatialReferenceFrame",
    "position",
    "timePosition",
    "temporalReferenceFrame",
    "interfaces"

})
@XmlSeeAlso({AbstractComponentType.class, ComponentArrayType.class}) 
public abstract class AbstractDerivableComponentType extends AbstractProcessType implements AbstractDerivableComponent {

    @XmlElement(name = "spatialReferenceFrame",  namespace = "http://www.opengis.net/sensorML/1.0", type = SpatialReferenceFrame.class)
    private SpatialReferenceFrame spatialReferenceFrame;

    @XmlElement(name = "location", namespace = "http://www.opengis.net/sensorML/1.0", type = Location.class)
    private Location location;

    @XmlElement(name = "position", namespace = "http://www.opengis.net/sensorML/1.0", type = Position.class)
    private Position position;

    @XmlElement(name = "timePosition", namespace = "http://www.opengis.net/sensorML/1.0", type = TimePosition.class)
    private TimePosition timePosition;

    @XmlElement(name = "temporalReferenceFrame", namespace = "http://www.opengis.net/sensorML/1.0", type = TemporalReferenceFrame.class)
    private TemporalReferenceFrame temporalReferenceFrame;

    @XmlElement(name = "interfaces", namespace = "http://www.opengis.net/sensorML/1.0", type = Interfaces.class)
    private Interfaces interfaces;

    public AbstractDerivableComponentType() {

    }

    public AbstractDerivableComponentType(final AbstractDerivableComponent ad) {
        super(ad);
        if (ad != null) {
            if (ad.getInterfaces() != null) {
                this.interfaces = new Interfaces(ad.getInterfaces());
            }
            if (ad.getPosition() != null) {
                this.position = new Position(ad.getPosition());
            }
            if (ad.getSMLLocation() != null) {
                this.location = new Location(ad.getSMLLocation());
            }
            if (ad.getSpatialReferenceFrame() != null) {
                this.spatialReferenceFrame = new SpatialReferenceFrame(ad.getSpatialReferenceFrame());
            }
            if (ad.getTemporalReferenceFrame() != null) {
                this.temporalReferenceFrame = new TemporalReferenceFrame(ad.getTemporalReferenceFrame());
            }
            if (ad.getTimePosition() != null) {
                this.timePosition = new TimePosition(ad.getTimePosition());
            }
        }
    }
    

    @Override
    public Location getSMLLocation() {
        return location;
    }

    @Override
    public void setSMLLocation(final AbstractLocation location) {
        if (location instanceof Location)
            this.location = (Location) location;
        else throw new IllegalArgumentException("Bad version of the location object");
    }

    /**
     * @return the spatialReferenceFrame
     */
    @Override
    public SpatialReferenceFrame getSpatialReferenceFrame() {
        return spatialReferenceFrame;
    }

    /**
     * @param spatialReferenceFrame the spatialReferenceFrame to set
     */
    public void setSpatialReferenceFrame(final SpatialReferenceFrame spatialReferenceFrame) {
        this.spatialReferenceFrame = spatialReferenceFrame;
    }

    /**
     * @return the position
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    @Override
    public void setPosition(final AbstractPosition position) {
        if (position instanceof Position)
            this.position = (Position) position;
        else throw new IllegalArgumentException("Bad version of the position object");
    }
    
    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractDerivableComponentType && super.equals(object)) {
            final AbstractDerivableComponentType that = (AbstractDerivableComponentType) object;
            return Utilities.equals(this.interfaces,             that.interfaces) &&
                   Utilities.equals(this.temporalReferenceFrame, that.temporalReferenceFrame) &&
                   Utilities.equals(this.timePosition,           that.timePosition) &&
                   Utilities.equals(this.spatialReferenceFrame,  that.spatialReferenceFrame) &&
                   Utilities.equals(this.location,               that.location) &&
                   Utilities.equals(this.position,               that.position);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.spatialReferenceFrame != null ? this.spatialReferenceFrame.hashCode() : 0);
        hash = 73 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 73 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 73 * hash + (this.timePosition != null ? this.timePosition.hashCode() : 0);
        hash = 73 * hash + (this.temporalReferenceFrame != null ? this.temporalReferenceFrame.hashCode() : 0);
        hash = 73 * hash + (this.interfaces != null ? this.interfaces.hashCode() : 0);
        return hash;
    }

    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (location != null)
            sb.append("location: ").append(location).append('\n');
        if (position != null)
            sb.append("position: ").append(position).append('\n');
        if (spatialReferenceFrame != null)
            sb.append("spatialReferenceFrame: ").append(spatialReferenceFrame).append('\n');
        if (timePosition != null)
            sb.append("timePosition: ").append(timePosition).append('\n');
        if (interfaces != null)
            sb.append("timePosition: ").append(timePosition).append('\n');
        if (temporalReferenceFrame != null)
            sb.append("temporalReferenceFrame: ").append(temporalReferenceFrame).append('\n');
        return sb.toString();
    }

    /**
     * @return the timePosition
     */
    public TimePosition getTimePosition() {
        return timePosition;
    }

    /**
     * @param timePosition the timePosition to set
     */
    public void setTimePosition(final TimePosition timePosition) {
        this.timePosition = timePosition;
    }

    /**
     * @return the temporalReferenceFrame
     */
    public TemporalReferenceFrame getTemporalReferenceFrame() {
        return temporalReferenceFrame;
    }

    /**
     * @param temporalReferenceFrame the temporalReferenceFrame to set
     */
    public void setTemporalReferenceFrame(final TemporalReferenceFrame temporalReferenceFrame) {
        this.temporalReferenceFrame = temporalReferenceFrame;
    }

    /**
     * @return the interfaces
     */
    public Interfaces getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(final Interfaces interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(final InterfaceList interfaces) {
        this.interfaces = new Interfaces(interfaces);
    }

}
