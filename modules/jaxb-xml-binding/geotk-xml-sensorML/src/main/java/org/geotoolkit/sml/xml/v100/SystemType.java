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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.sml.xml.System;
import org.geotoolkit.util.ComparisonMode;


/**
 * <p>Java class for SystemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SystemType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractComponentType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}components" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}positions" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}connections" minOccurs="0"/>
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
@XmlType(name = "SystemType")
public class SystemType extends AbstractComponentType implements System {

    @XmlElement(required = true)
    private Components components;
    @XmlElement(required = true)
    private Positions positions;
    @XmlElement(required = true)
    private Connections connections;

    public SystemType() {

    }
    
    public SystemType(final System sy) {
        super(sy);
        if (sy != null) {
            if (sy.getComponents() != null) {
                this.components = new Components(sy.getComponents());
            }
            if (sy.getConnections() != null) {
                this.connections = new Connections(sy.getConnections());
            }
            if (sy.getPositions() != null) {
                this.positions = new Positions(sy.getPositions());
            }
        }
    }
     
    /**
     * @return the components
     */
    @Override
    public Components getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(final Components components) {
        this.components = components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(final ComponentList components) {
        this.components = new Components(components);
    }
    
    /**
     * @return the positions
     */
    @Override
    public Positions getPositions() {
        return positions;
    }

    /**
     * @param positions the positions to set
     */
    public void setPositions(final Positions positions) {
        this.positions = positions;
    }

    /**
     * @param positions the positions to set
     */
    public void setPositions(final PositionList positions) {
        this.positions = new Positions(positions);
    }

    /**
     * @return the connections
     */
    public Connections getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(final Connections connections) {
        this.connections = connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(final ConnectionList connections) {
        this.connections = new Connections(connections);
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof SystemType && super.equals(object, mode)) {
            final SystemType that = (SystemType) object;
            return Utilities.equals(this.components,  that.components)  &&
                   Utilities.equals(this.connections, that.connections) &&
                   Utilities.equals(this.positions,   that.positions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.components != null ? this.components.hashCode() : 0);
        hash = 71 * hash + (this.positions != null ? this.positions.hashCode() : 0);
        hash = 71 * hash + (this.connections != null ? this.connections.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (components != null) {
            sb.append("components:").append(components).append('\n');
        }
        if (positions != null) {
            sb.append("positions:").append(positions).append('\n');
        }
        if (connections != null) {
            sb.append("connections:").append(connections).append('\n');
        }
        return sb.toString();
     }
}
