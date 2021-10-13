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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractInterfaceDefinition;
import org.geotoolkit.swe.xml.v100.Category;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="applicationLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="presentationLayer" type="{http://www.opengis.net/sensorML/1.0}PresentationLayerPropertyType" minOccurs="0"/>
 *         &lt;element name="sessionLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="transportLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="networkLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="dataLinkLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="physicalLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="mechanicalLayer" type="{http://www.opengis.net/sensorML/1.0}LayerPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceLayer",
    "applicationLayer",
    "presentationLayer",
    "sessionLayer",
    "transportLayer",
    "networkLayer",
    "dataLinkLayer",
    "physicalLayer",
    "mechanicalLayer"
})
@XmlRootElement(name = "InterfaceDefinition")
public class InterfaceDefinition implements AbstractInterfaceDefinition {

    private LayerPropertyType serviceLayer;
    private LayerPropertyType applicationLayer;
    private PresentationLayerPropertyType presentationLayer;
    private LayerPropertyType sessionLayer;
    private LayerPropertyType transportLayer;
    private LayerPropertyType networkLayer;
    private LayerPropertyType dataLinkLayer;
    private LayerPropertyType physicalLayer;
    private LayerPropertyType mechanicalLayer;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public InterfaceDefinition() {

    }

    public InterfaceDefinition(final String id, final LayerPropertyType applicationLayer, final LayerPropertyType dataLinkLayer) {
        this.applicationLayer = applicationLayer;
        this.dataLinkLayer    = dataLinkLayer;
    }

     public InterfaceDefinition(final AbstractInterfaceDefinition in) {
        if (in != null) {
            if (in.getServiceLayer() != null) {
                this.serviceLayer = new LayerPropertyType(in.getServiceLayer());
            }
            if (in.getApplicationLayer() != null) {
                this.applicationLayer = new LayerPropertyType(in.getApplicationLayer());
            }
            if (in.getDataLinkLayer() != null) {
                this.dataLinkLayer = new LayerPropertyType(in.getDataLinkLayer());
            }
            this.id = in.getId();
            if (in.getMechanicalLayer() != null) {
                this.mechanicalLayer = new LayerPropertyType(in.getMechanicalLayer());
            }
            if (in.getNetworkLayer() != null) {
                this.networkLayer = new LayerPropertyType(in.getNetworkLayer());
            }
            if (in.getPhysicalLayer() != null) {
                this.physicalLayer = new LayerPropertyType(in.getPhysicalLayer());
            }
            if (in.getPresentationLayer() != null) {
                this.presentationLayer = new PresentationLayerPropertyType(in.getPresentationLayer());
            }
            if (in.getServiceLayer() != null) {
                this.serviceLayer = new LayerPropertyType(in.getServiceLayer());
            }
            if (in.getSessionLayer() != null) {
                this.sessionLayer = new LayerPropertyType(in.getSessionLayer());
            }
            if (in.getTransportLayer() != null) {
                this.transportLayer = new LayerPropertyType(in.getTransportLayer());
            }
        }
    }

    /**
     * Gets the value of the serviceLayer property.
     */
    public LayerPropertyType getServiceLayer() {
        return serviceLayer;
    }

    /**
     * Sets the value of the serviceLayer property.
     */
    public void setServiceLayer(final LayerPropertyType value) {
        this.serviceLayer = value;
    }

    /**
     * Sets the value of the serviceLayer property.
     */
    public void setServiceLayer(final Category value) {
        this.serviceLayer = new LayerPropertyType(value);
    }


    /**
     * Gets the value of the applicationLayer property.
     */
    public LayerPropertyType getApplicationLayer() {
        return applicationLayer;
    }

    /**
     * Sets the value of the applicationLayer property.
     */
    public void setApplicationLayer(final LayerPropertyType value) {
        this.applicationLayer = value;
    }

    /**
     * Sets the value of the applicationLayer property.
     */
    public void setApplicationLayer(final Category value) {
        this.applicationLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the presentationLayer property.
     */
    public PresentationLayerPropertyType getPresentationLayer() {
        return presentationLayer;
    }

    /**
     * Sets the value of the presentationLayer property.
     */
    public void setPresentationLayer(final PresentationLayerPropertyType value) {
        this.presentationLayer = value;
    }

    /**
     * Sets the value of the applicationLayer property.
     */
    public void setPresentationLayer(final Category value) {
        this.presentationLayer = new PresentationLayerPropertyType(value);
    }

    /**
     * Gets the value of the sessionLayer property.
     */
    public LayerPropertyType getSessionLayer() {
        return sessionLayer;
    }

    /**
     * Sets the value of the sessionLayer property.
     */
    public void setSessionLayer(final LayerPropertyType value) {
        this.sessionLayer = value;
    }

    /**
     * Sets the value of the sessionLayer property.
     */
    public void setSessionLayer(final Category value) {
        this.sessionLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the transportLayer property.
     */
    public LayerPropertyType getTransportLayer() {
        return transportLayer;
    }

    /**
     * Sets the value of the transportLayer property.
     */
    public void setTransportLayer(final LayerPropertyType value) {
        this.transportLayer = value;
    }

    /**
     * Sets the value of the transportLayer property.
     */
    public void setTransportLayer(final Category value) {
        this.transportLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the networkLayer property.
     */
    public LayerPropertyType getNetworkLayer() {
        return networkLayer;
    }

    /**
     * Sets the value of the networkLayer property.
     */
    public void setNetworkLayer(final LayerPropertyType value) {
        this.networkLayer = value;
    }

    /**
     * Sets the value of the networkLayer property.
     */
    public void setNetworkLayer(final Category value) {
        this.networkLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the dataLinkLayer property.
     */
    public LayerPropertyType getDataLinkLayer() {
        return dataLinkLayer;
    }

    /**
     * Sets the value of the dataLinkLayer property.
     */
    public void setDataLinkLayer(final LayerPropertyType value) {
        this.dataLinkLayer = value;
    }

    /**
     * Sets the value of the dataLinkLayer property.
     */
    public void setDataLinkLayer(final Category value) {
        this.dataLinkLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the physicalLayer property.
     */
    public LayerPropertyType getPhysicalLayer() {
        return physicalLayer;
    }

    /**
     * Sets the value of the physicalLayer property.
     */
    public void setPhysicalLayer(final LayerPropertyType value) {
        this.physicalLayer = value;
    }

    /**
     * Sets the value of the physicalLayer property.
     */
    public void setPhysicalLayer(final Category value) {
        this.physicalLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the mechanicalLayer property.
     */
    public LayerPropertyType getMechanicalLayer() {
        return mechanicalLayer;
    }

    /**
     * Sets the value of the mechanicalLayer property.
     */
    public void setMechanicalLayer(final LayerPropertyType value) {
        this.mechanicalLayer = value;
    }

    /**
     * Sets the value of the mechanicalLayer property.
     */
    public void setMechanicalLayer(final Category value) {
        this.mechanicalLayer = new LayerPropertyType(value);
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(final String value) {
        this.id = value;
    }
    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InterfaceDefinition) {
            final InterfaceDefinition that = (InterfaceDefinition) object;

            return Objects.equals(this.applicationLayer,  that.applicationLayer) &&
                   Objects.equals(this.dataLinkLayer,     that.dataLinkLayer)    &&
                   Objects.equals(this.id,                that.id)               &&
                   Objects.equals(this.mechanicalLayer,   that.mechanicalLayer)  &&
                   Objects.equals(this.networkLayer,      that.networkLayer)     &&
                   Objects.equals(this.physicalLayer,     that.physicalLayer)    &&
                   Objects.equals(this.presentationLayer, that.presentationLayer)&&
                   Objects.equals(this.serviceLayer,      that.serviceLayer)     &&
                   Objects.equals(this.sessionLayer,      that.sessionLayer)     &&
                   Objects.equals(this.transportLayer,    that.transportLayer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.serviceLayer != null ? this.serviceLayer.hashCode() : 0);
        hash = 67 * hash + (this.applicationLayer != null ? this.applicationLayer.hashCode() : 0);
        hash = 67 * hash + (this.presentationLayer != null ? this.presentationLayer.hashCode() : 0);
        hash = 67 * hash + (this.sessionLayer != null ? this.sessionLayer.hashCode() : 0);
        hash = 67 * hash + (this.transportLayer != null ? this.transportLayer.hashCode() : 0);
        hash = 67 * hash + (this.networkLayer != null ? this.networkLayer.hashCode() : 0);
        hash = 67 * hash + (this.dataLinkLayer != null ? this.dataLinkLayer.hashCode() : 0);
        hash = 67 * hash + (this.physicalLayer != null ? this.physicalLayer.hashCode() : 0);
        hash = 67 * hash + (this.mechanicalLayer != null ? this.mechanicalLayer.hashCode() : 0);
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }


}
