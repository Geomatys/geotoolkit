/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PhysicalSystemType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PhysicalSystemType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorml/2.0}AbstractPhysicalProcessType">
 *       &lt;sequence>
 *         &lt;element name="components" type="{http://www.opengis.net/sensorml/2.0}ComponentListPropertyType" minOccurs="0"/>
 *         &lt;element name="connections" type="{http://www.opengis.net/sensorml/2.0}ConnectionListPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhysicalSystemType", propOrder = {
    "components",
    "connections"
})
public class PhysicalSystemType
    extends AbstractPhysicalProcessType
{

    protected ComponentListPropertyType components;
    protected ConnectionListPropertyType connections;

    /**
     * Gets the value of the components property.
     *
     * @return
     *     possible object is
     *     {@link ComponentListPropertyType }
     *
     */
    public ComponentListPropertyType getComponents() {
        return components;
    }

    /**
     * Sets the value of the components property.
     *
     * @param value
     *     allowed object is
     *     {@link ComponentListPropertyType }
     *
     */
    public void setComponents(ComponentListPropertyType value) {
        this.components = value;
    }

    /**
     * Gets the value of the connections property.
     *
     * @return
     *     possible object is
     *     {@link ConnectionListPropertyType }
     *
     */
    public ConnectionListPropertyType getConnections() {
        return connections;
    }

    /**
     * Sets the value of the connections property.
     *
     * @param value
     *     allowed object is
     *     {@link ConnectionListPropertyType }
     *
     */
    public void setConnections(ConnectionListPropertyType value) {
        this.connections = value;
    }

}
