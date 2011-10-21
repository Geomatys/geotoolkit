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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractDerivableComponent;
import org.geotoolkit.sml.xml.AbstractLocation;
import org.geotoolkit.sml.xml.AbstractPosition;
import org.geotoolkit.util.ComparisonMode;
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
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0.1}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}spatialReferenceFrame" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}temporalReferenceFrame" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}location"/>
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}position"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}timePosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}interfaces" minOccurs="0"/>
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
@XmlSeeAlso({
    ComponentArrayType.class,
    AbstractComponentType.class
})
public abstract class AbstractDerivableComponentType extends AbstractProcessType implements AbstractDerivableComponent {

    @XmlElement(name = "spatialReferenceFrame",  namespace = "http://www.opengis.net/sensorML/1.0.1", type = SpatialReferenceFrame.class)
    private SpatialReferenceFrame spatialReferenceFrame;

    @XmlElement(name = "location", namespace = "http://www.opengis.net/sensorML/1.0.1", type = Location.class)
    private Location location;

    @XmlElement(name = "position", namespace = "http://www.opengis.net/sensorML/1.0.1", type = Position.class)
    private Position position;

    @XmlElement(name = "timePosition", namespace = "http://www.opengis.net/sensorML/1.0.1", type = TimePosition.class)
    private TimePosition timePosition;

    @XmlElement(name = "temporalReferenceFrame", namespace = "http://www.opengis.net/sensorML/1.0.1", type = TemporalReferenceFrame.class)
    private TemporalReferenceFrame temporalReferenceFrame;

    @XmlElement(name = "interfaces", namespace = "http://www.opengis.net/sensorML/1.0.1", type = Interfaces.class)
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

    public Location getSMLLocation() {
        return location;
    }
    
    public void setSMLLocation(final AbstractLocation location) {
        if (location != null) {
            this.location = new Location(location);
        }
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(final AbstractPosition position) {
        if (position != null) {
            this.position = new Position(position);
        }
    }

    /**
     * @return the spatialReferenceFrame
     */
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
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractDerivableComponentType && super.equals(object, mode)) {
            final AbstractDerivableComponentType that = (AbstractDerivableComponentType) object;
            return Utilities.equals(this.interfaces,             that.interfaces)            &&
                   Utilities.equals(this.location,               that.location)              &&
                   Utilities.equals(this.position,               that.position)              &&
                   Utilities.equals(this.spatialReferenceFrame,  that.spatialReferenceFrame) &&
                   Utilities.equals(this.temporalReferenceFrame, that.temporalReferenceFrame)&&
                   Utilities.equals(this.timePosition,           that.timePosition);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.interfaces != null ? this.interfaces.hashCode() : 0);
        hash = 17 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 17 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 17 * hash + (this.spatialReferenceFrame != null ? this.spatialReferenceFrame.hashCode() : 0);
        hash = 17 * hash + (this.temporalReferenceFrame != null ? this.temporalReferenceFrame.hashCode() : 0);
        hash = 17 * hash + (this.timePosition != null ? this.timePosition.hashCode() : 0);
        return hash;
    }
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (interfaces != null) {
            s.append("interfaces:").append(interfaces).append('\n');
        }
        if (location != null) {
            s.append("location:").append(location).append('\n');
        }
        if (position != null) {
            s.append("position:").append(position).append('\n');
        }
        if (spatialReferenceFrame != null) {
            s.append("spatialReferenceFrame:").append(spatialReferenceFrame).append('\n');
        }
        if (temporalReferenceFrame != null) {
            s.append("temporalReferenceFrame:").append(temporalReferenceFrame).append('\n');
        }
        if (timePosition != null) {
            s.append("timePosition:").append(timePosition).append('\n');
        }
        return s.toString();
    }
    
}
