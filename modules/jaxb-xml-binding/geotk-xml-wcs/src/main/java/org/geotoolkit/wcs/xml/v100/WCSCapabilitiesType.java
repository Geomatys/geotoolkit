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
package org.geotoolkit.wcs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.wcs.xml.Content;
import org.geotoolkit.wcs.xml.GetCapabilitiesResponse;


/**
 * 
 * Metadata for a WCS server, also known as Capabilities document. 
 * Reply from a WCS that performed the GetCapabilities operation. 
 * 
 * WCS version 1.0.0 
 * 
 * <p>Java class for WCS_CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WCS_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}Service"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}Capability"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}ContentMetadata"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "capability",
    "contentMetadata"
})
@XmlRootElement(name="WCS_Capabilities")        
public class WCSCapabilitiesType implements GetCapabilitiesResponse {

    @XmlElement(name = "Service", required = true)
    private ServiceType service;
    @XmlElement(name = "Capability", required = true)
    private WCSCapabilityType capability;
    @XmlElement(name = "ContentMetadata", required = true)
    private ContentMetadata contentMetadata;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute
    private String updateSequence;

    /**
     * an empty constructor used by JAXB.
     */
    WCSCapabilitiesType(){
        
    }
    
    /**
     * build a full new Capabilities document version 1.0.0.
     */
    public WCSCapabilitiesType(final ServiceType service, final WCSCapabilityType capability, 
            final ContentMetadata contentMetadata, final String updateSequence) {
        this.service         = service;
        this.capability      = capability;
        this.contentMetadata = contentMetadata;
        this.version         = "1.0.0";
        this.updateSequence  = updateSequence;
    }
    
    /**
     * build a new Capabilities document version 1.0.0 with only the section "service".
     */
    public WCSCapabilitiesType(final ServiceType service) {
        this.service = service;
        this.version = "1.0.0";
    }
    
    /**
     * build a new Capabilities document version 1.0.0 with only the section "Capability".
     */
    public WCSCapabilitiesType(final WCSCapabilityType capability) {
        this.capability = capability;
        this.version    = "1.0.0";
    }
    
    /**
     * build a new Capabilities document version 1.0.0 with only the section "ContentMetadata".
     */
    public WCSCapabilitiesType(final ContentMetadata contentMetadata) {
        this.contentMetadata = contentMetadata;
        this.version         = "1.0.0";
    }
    
    
    /**
     * Gets the value of the service property.
     */
    public ServiceType getService() {
        return service;
    }
    
    public void setService(final ServiceType service) {
        this.service = service; 
    }
    /**
     * Gets the value of the capability property.
     * 
    */
    public WCSCapabilityType getCapability() {
        return capability;
    }

    /**
     * Gets the value of the contentMetadata property.
     */
    public ContentMetadata getContentMetadata() {
        return contentMetadata;
    }

    /**
     * Sets the value of the contentMetadata property.
     */
    public void setContentMetadata(final ContentMetadata value) {
        this.contentMetadata = value;
    }

    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
        }
    }

    /**
     * Gets the value of the updateSequence property.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    @Override
    public Content getContents() {
        return contentMetadata;
    }
}
