/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008 Geomatys
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


package org.geotoolkit.internal.jaxb.backend.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractLayer;
import org.geotoolkit.internal.jaxb.backend.AbstractService;
import org.geotoolkit.internal.jaxb.backend.AbstractWMSCapabilities;


/**
 * <p>Root element of a getCapabilities Document version 1.3.0.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}Service"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Capability"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.3.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "capability"
})
@XmlRootElement(name = "WMS_Capabilities", namespace = "http://www.opengis.net/wms")
public class WMSCapabilities extends AbstractWMSCapabilities {

    @XmlElement(name = "Service", required = true)
    private Service service;
    @XmlElement(name = "Capability", required = true)
    private Capability capability;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String updateSequence;

    /**
     * An empty constructor used by JAXB.
     */
    WMSCapabilities() {
    }

    /**
     * Build a new WMSCapabilities object.
     */
    public WMSCapabilities(final Service service, final Capability capability, 
            final String version, final String updateSequence) {
        this.capability     = capability;
        this.service        = service;
        this.updateSequence = updateSequence;
        this.version        = version;
    }

    
    /**
     * Gets the value of the service property.
     * 
     */
    public Service getService() {
        return service;
    }
    
    public void setService(AbstractService service) {
        if (service instanceof Service)
            this.service = (Service) service;
        else
            throw new IllegalArgumentException("not the good version object, expected 1.3.0"); 
    }

    /**
     * Gets the value of the capability property.
     * 
     */
    public Capability getCapability() {
        return capability;
    }

    /**
     * Gets the value of the version property.
     * 
     */
    public String getVersion() {
        if (version == null) {
            return "1.3.0";
        } else {
            return version;
        }
    }

    /**
     * Gets the value of the updateSequence property.
     * 
     */
    public String getUpdateSequence() {
        return updateSequence;
    }
    
    /**
     * Get a specific layer from the capabilities document.
     * 
     */
    public AbstractLayer getLayerFromName(String name) {
        for( Layer layer : getCapability().getLayer().getLayer()){
            if(layer.getName().equals(name)){
                return (AbstractLayer)layer; 
            }
        }        
        return null;
    }
}
