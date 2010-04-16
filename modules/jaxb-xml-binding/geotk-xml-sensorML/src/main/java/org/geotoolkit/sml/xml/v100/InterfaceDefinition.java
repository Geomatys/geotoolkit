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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractInterfaceDefinition;
import org.geotoolkit.swe.xml.v100.Category;
import org.geotoolkit.util.Utilities;


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
 * @module pending
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

    public InterfaceDefinition(String id, LayerPropertyType applicationLayer, LayerPropertyType dataLinkLayer) {
        this.applicationLayer = applicationLayer;
        this.dataLinkLayer    = dataLinkLayer;
    }

     public InterfaceDefinition(AbstractInterfaceDefinition in) {
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
    public void setServiceLayer(LayerPropertyType value) {
        this.serviceLayer = value;
    }

    /**
     * Sets the value of the serviceLayer property.
     */
    public void setServiceLayer(Category value) {
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
    public void setApplicationLayer(LayerPropertyType value) {
        this.applicationLayer = value;
    }

    /**
     * Sets the value of the applicationLayer property.
     */
    public void setApplicationLayer(Category value) {
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
    public void setPresentationLayer(PresentationLayerPropertyType value) {
        this.presentationLayer = value;
    }

    /**
     * Sets the value of the applicationLayer property.
     */
    public void setPresentationLayer(Category value) {
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
    public void setSessionLayer(LayerPropertyType value) {
        this.sessionLayer = value;
    }

    /**
     * Sets the value of the sessionLayer property.
     */
    public void setSessionLayer(Category value) {
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
    public void setTransportLayer(LayerPropertyType value) {
        this.transportLayer = value;
    }

    /**
     * Sets the value of the transportLayer property.
     */
    public void setTransportLayer(Category value) {
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
    public void setNetworkLayer(LayerPropertyType value) {
        this.networkLayer = value;
    }

    /**
     * Sets the value of the networkLayer property.
     */
    public void setNetworkLayer(Category value) {
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
    public void setDataLinkLayer(LayerPropertyType value) {
        this.dataLinkLayer = value;
    }

    /**
     * Sets the value of the dataLinkLayer property.
     */
    public void setDataLinkLayer(Category value) {
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
    public void setPhysicalLayer(LayerPropertyType value) {
        this.physicalLayer = value;
    }

    /**
     * Sets the value of the physicalLayer property.
     */
    public void setPhysicalLayer(Category value) {
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
    public void setMechanicalLayer(LayerPropertyType value) {
        this.mechanicalLayer = value;
    }

    /**
     * Sets the value of the mechanicalLayer property.
     */
    public void setMechanicalLayer(Category value) {
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
    public void setId(String value) {
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

            return Utilities.equals(this.applicationLayer,  that.applicationLayer) &&
                   Utilities.equals(this.dataLinkLayer,     that.dataLinkLayer)    &&
                   Utilities.equals(this.id,                that.id)               &&
                   Utilities.equals(this.mechanicalLayer,   that.mechanicalLayer)  &&
                   Utilities.equals(this.networkLayer,      that.networkLayer)     &&
                   Utilities.equals(this.physicalLayer,     that.physicalLayer)    &&
                   Utilities.equals(this.presentationLayer, that.presentationLayer)&&
                   Utilities.equals(this.serviceLayer,      that.serviceLayer)     &&
                   Utilities.equals(this.sessionLayer,      that.sessionLayer)     &&
                   Utilities.equals(this.transportLayer,    that.transportLayer);
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
