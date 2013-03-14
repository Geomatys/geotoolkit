/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sos.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.AcceptFormatsType;
import org.geotoolkit.ows.xml.v110.AcceptVersionsType;
import org.geotoolkit.ows.xml.v110.SectionsType;
import org.geotoolkit.sos.xml.GetCapabilities;


/**
 * <p>Java class for GetCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}GetCapabilitiesType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" type="{http://www.w3.org/2001/XMLSchema}string" default="SOS" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType", propOrder = {
    "extension"
})
@XmlRootElement(name="GetCapabilities")
public class GetCapabilitiesType extends org.geotoolkit.ows.xml.v110.GetCapabilitiesType implements GetCapabilities {

    private List<Object> extension;
    
    private static List<String> ACCEPTED_SECTIONS = Arrays.asList("All",
                                                                  "ServiceIdentification",
                                                                  "ServiceProvider",
                                                                  "OperationsMetadata",
                                                                  "Filter_Capabilities",
                                                                  "Contents");

    /**
     * minimal getCapabilities request.
     */
    public GetCapabilitiesType() {
        
    }
    
    /**
     * Build a new getCapabilities request with the specified service
     */
    public GetCapabilitiesType(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence, service);
    }

     /**
     * Build a new getCapabilities (simplified version).
     */
    public GetCapabilitiesType(final String acceptVersions, final String acceptFormats) {
        super(acceptVersions, acceptFormats, "SOS");
    }
    
    /**
     * Gets the value of the extension property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<Object>();
        }
        return this.extension;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getService() {
        if (service == null) {
            return "SOS";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setService(String value) {
        this.service = value;
    }

    @Override
    public boolean isValidSections() {
        if (sections != null) {
            for (String section : sections.getSection()) {
                if (!ACCEPTED_SECTIONS.contains(section)) {
                    return false;
                }
            }
        }
        return true;
    }
}
