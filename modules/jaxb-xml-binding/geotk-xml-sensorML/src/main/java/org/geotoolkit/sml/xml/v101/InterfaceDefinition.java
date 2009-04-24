/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="serviceLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="applicationLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="presentationLayer" type="{http://www.opengis.net/sensorML/1.0.1}PresentationLayerPropertyType" minOccurs="0"/>
 *         &lt;element name="sessionLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="transportLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="networkLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="dataLinkLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="physicalLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *         &lt;element name="mechanicalLayer" type="{http://www.opengis.net/sensorML/1.0.1}LayerPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
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
public class InterfaceDefinition {

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
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     * Gets the value of the serviceLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getServiceLayer() {
        return serviceLayer;
    }

    /**
     * Sets the value of the serviceLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setServiceLayer(LayerPropertyType value) {
        this.serviceLayer = value;
    }

    /**
     * Gets the value of the applicationLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getApplicationLayer() {
        return applicationLayer;
    }

    /**
     * Sets the value of the applicationLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setApplicationLayer(LayerPropertyType value) {
        this.applicationLayer = value;
    }

    /**
     * Gets the value of the presentationLayer property.
     * 
     * @return
     *     possible object is
     *     {@link PresentationLayerPropertyType }
     *     
     */
    public PresentationLayerPropertyType getPresentationLayer() {
        return presentationLayer;
    }

    /**
     * Sets the value of the presentationLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link PresentationLayerPropertyType }
     *     
     */
    public void setPresentationLayer(PresentationLayerPropertyType value) {
        this.presentationLayer = value;
    }

    /**
     * Gets the value of the sessionLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getSessionLayer() {
        return sessionLayer;
    }

    /**
     * Sets the value of the sessionLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setSessionLayer(LayerPropertyType value) {
        this.sessionLayer = value;
    }

    /**
     * Gets the value of the transportLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getTransportLayer() {
        return transportLayer;
    }

    /**
     * Sets the value of the transportLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setTransportLayer(LayerPropertyType value) {
        this.transportLayer = value;
    }

    /**
     * Gets the value of the networkLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getNetworkLayer() {
        return networkLayer;
    }

    /**
     * Sets the value of the networkLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setNetworkLayer(LayerPropertyType value) {
        this.networkLayer = value;
    }

    /**
     * Gets the value of the dataLinkLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getDataLinkLayer() {
        return dataLinkLayer;
    }

    /**
     * Sets the value of the dataLinkLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setDataLinkLayer(LayerPropertyType value) {
        this.dataLinkLayer = value;
    }

    /**
     * Gets the value of the physicalLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getPhysicalLayer() {
        return physicalLayer;
    }

    /**
     * Sets the value of the physicalLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setPhysicalLayer(LayerPropertyType value) {
        this.physicalLayer = value;
    }

    /**
     * Gets the value of the mechanicalLayer property.
     * 
     * @return
     *     possible object is
     *     {@link LayerPropertyType }
     *     
     */
    public LayerPropertyType getMechanicalLayer() {
        return mechanicalLayer;
    }

    /**
     * Sets the value of the mechanicalLayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerPropertyType }
     *     
     */
    public void setMechanicalLayer(LayerPropertyType value) {
        this.mechanicalLayer = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
