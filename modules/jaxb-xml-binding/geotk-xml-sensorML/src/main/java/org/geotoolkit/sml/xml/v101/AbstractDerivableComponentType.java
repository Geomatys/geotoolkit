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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractDerivableComponent;
import org.geotoolkit.sml.xml.AbstractLocation;
import org.geotoolkit.sml.xml.AbstractPosition;


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

    @XmlElementRef(name = "spatialReferenceFrame",  namespace = "http://www.opengis.net/sensorML/1.0", type = SpatialReferenceFrame.class)
    private SpatialReferenceFrame spatialReferenceFrame;

    @XmlElementRef(name = "location", namespace = "http://www.opengis.net/sensorML/1.0", type = Location.class)
    private Location location;

    @XmlElementRef(name = "position", namespace = "http://www.opengis.net/sensorML/1.0", type = Position.class)
    private Position position;

    @XmlElementRef(name = "timePosition", namespace = "http://www.opengis.net/sensorML/1.0", type = TimePosition.class)
    private TimePosition timePosition;

    @XmlElementRef(name = "temporalReferenceFrame", namespace = "http://www.opengis.net/sensorML/1.0", type = TemporalReferenceFrame.class)
    private TemporalReferenceFrame temporalReferenceFrame;

    @XmlElementRef(name = "interfaces", namespace = "http://www.opengis.net/sensorML/1.0", type = Interfaces.class)
    private Interfaces interfaces;

    public AbstractDerivableComponentType() {

    }

    public AbstractDerivableComponentType(AbstractDerivableComponent ad) {
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
    
    public void setSMLLocation(AbstractLocation location) {
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
    
    public void setPosition(AbstractPosition position) {
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
    public void setSpatialReferenceFrame(SpatialReferenceFrame spatialReferenceFrame) {
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
    public void setTimePosition(TimePosition timePosition) {
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
    public void setTemporalReferenceFrame(TemporalReferenceFrame temporalReferenceFrame) {
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
    public void setInterfaces(Interfaces interfaces) {
        this.interfaces = interfaces;
    }
}
