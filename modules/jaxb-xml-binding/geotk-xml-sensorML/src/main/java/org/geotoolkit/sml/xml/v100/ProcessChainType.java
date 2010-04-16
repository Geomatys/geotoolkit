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
import org.geotoolkit.sml.xml.AbstractProcessChain;


/**
 * Complex Type for process chains
 * 
 * <p>Java class for ProcessChainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessChainType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractPureProcessType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}components"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}connections"/>
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
@XmlType(name = "ProcessChainType", propOrder = {
    "components",
    "connections"
})
public class ProcessChainType extends AbstractPureProcessType implements AbstractProcessChain {

    @XmlElement(required = true)
    private Components components;
    @XmlElement(required = true)
    private Connections connections;

    public ProcessChainType() {

    }

    public ProcessChainType(AbstractProcessChain pc) {
        super(pc);
        if (pc != null) {
            if (pc.getComponents() != null) {
                this.components = new Components(pc.getComponents());
            }
            if (pc.getConnections() != null) {
                this.connections = new Connections(pc.getConnections());
            }
        }
    }
    
    /**
     * Gets the value of the components property.
     */
    public Components getComponents() {
        return components;
    }

    /**
     * Sets the value of the components property.
     */
    public void setComponents(Components value) {
        this.components = value;
    }

    /**
     * Gets the value of the connections property.
     *     
     */
    public Connections getConnections() {
        return connections;
    }

    /**
     * Sets the value of the connections property.
      */
    public void setConnections(Connections value) {
        this.connections = value;
    }

}
