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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
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
    "rest",
    "location",
    "position"
})
@XmlSeeAlso({
    ComponentArrayType.class,
    AbstractComponentType.class
})
public abstract class AbstractDerivableComponentType extends AbstractProcessType implements AbstractDerivableComponent {

    @XmlElementRefs({
        @XmlElementRef(name = "temporalReferenceFrame", namespace = "http://www.opengis.net/sensorML/1.0.1", type = TemporalReferenceFrame.class),
        @XmlElementRef(name = "spatialReferenceFrame", namespace = "http://www.opengis.net/sensorML/1.0.1", type = SpatialReferenceFrame.class),
        @XmlElementRef(name = "timePosition", namespace = "http://www.opengis.net/sensorML/1.0.1", type = TimePosition.class),
        @XmlElementRef(name = "interfaces", namespace = "http://www.opengis.net/sensorML/1.0.1", type = Interfaces.class)
    })
    private List<Object> rest;

    @XmlElementRef(name = "location", namespace = "http://www.opengis.net/sensorML/1.0.1", type = Location.class)
    private Location location;

    @XmlElementRef(name = "position", namespace = "http://www.opengis.net/sensorML/1.0.1", type = Position.class)
    private Position position;
    
    /**
     * Gets the rest of the content model. 
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link SpatialReferenceFrame }
     * {@link TemporalReferenceFrame }
     * {@link Location }
     * {@link Interfaces }
     * {@link TimePosition }
     * {@link Position }
     * 
     * 
     */
    public List<Object> getRest() {
        if (rest == null) {
            rest = new ArrayList<Object>();
        }
        return this.rest;
    }
    
    public Location getSMLLocation() {
        return location;
    }
    
    public void setSMLLocation(AbstractLocation location) {
        if (location instanceof Location)
            this.location = (Location) location;
        else throw new IllegalArgumentException("Bad version of the location object");
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(AbstractPosition position) {
        if (position instanceof Position)
            this.position = (Position) position;
        else throw new IllegalArgumentException("Bad version of the position object");
    }
}
