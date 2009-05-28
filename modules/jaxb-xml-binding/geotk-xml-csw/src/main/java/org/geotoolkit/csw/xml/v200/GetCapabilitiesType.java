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
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ows.xml.v100.AcceptFormatsType;
import org.geotoolkit.ows.xml.v100.AcceptVersionsType;
import org.geotoolkit.ows.xml.v100.SectionsType;


/**
 * 
 * Request to a CSW to perform the GetCapabilities operation.
 * This operation allows a client to retrieve a Capabilities XML document providing metadata for the specific CSW server.
 *        
 * 
 * <p>Java class for GetCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="service" type="{http://www.w3.org/2001/XMLSchema}string" default="CSW" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilitiesType extends org.geotoolkit.ows.xml.v100.GetCapabilitiesType {

    @XmlAttribute
    private String service;

    /**
     * An empty constructor used by JAXB
     */
    GetCapabilitiesType() {
    }
    
    /**
     * Build a new getCapabilities request with the specified service
     * 
     * @param acceptVersions The different versions accepted by the client.
     * @param sections The different sections of the capabilities document requested.
     *                 one or more of "ServiceIdentification", "ServiceProvider", "OperationsMetadata", "Filter_Capabilities", "All".
     * @param acceptFormats The different fomat (MIME type) accepted by the client.
     * @param updateSequence not used yet.
     * @param service MUST be CSW.
     */
    public GetCapabilitiesType(AcceptVersionsType acceptVersions, SectionsType sections,
            AcceptFormatsType acceptFormats, String updateSequence, String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence);
        this.service = service;
    }
    
    /**
     * Gets the value of the service property.
     * 
     */
    public String getService() {
        if (service == null) {
            return "CSW";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     * 
     */
    public void setService(String value) {
        this.service = value;
    }

}
