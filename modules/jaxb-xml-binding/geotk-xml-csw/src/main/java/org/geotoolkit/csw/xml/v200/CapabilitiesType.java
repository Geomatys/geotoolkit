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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.AbstractCapabilities;
import org.geotoolkit.csw.xml.CSWResponse;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v100.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v100.OperationsMetadata;
import org.geotoolkit.ows.xml.v100.ServiceIdentification;
import org.geotoolkit.ows.xml.v100.ServiceProvider;
import org.opengis.filter.capability.FilterCapabilities;


/**
 * 
 * XML encoded CSW GetCapabilities operation response. 
 * This document provides clients with service metadata about a specific service instance,
 * including metadata about the tightly-coupled data served.
 * If the server does not implement the updateSequence parameter, 
 * the server shall always return the complete Capabilities document, 
 * without the updateSequence parameter. 
 * When the server implements the updateSequence parameter and the GetCapabilities operation request included
 * the updateSequence parameter with the current value, 
 * the server shall return this element with only the "version" and "updateSequence" attributes. 
 * Otherwise, all optional elements shall be included or not depending on the actual value of the Contents parameter in the GetCapabilities operation request.
 *          
 * 
 * <p>Java class for CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesType")
public class CapabilitiesType extends CapabilitiesBaseType implements AbstractCapabilities, CSWResponse {

    /**
     * An empty constructor used by JAXB
     */
    public CapabilitiesType(){
    }
    
     /**
     * Build a new Capabilities document
     */
    public CapabilitiesType(final String version, final String updateSequence){
        super(version, updateSequence);
    }
    
    /**
     * Build a new Capabilities document
     */
    public CapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence){
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
    }

    @Override
    public FilterCapabilities getFilterCapabilities() {
        return null;
    }
    
    @Override
    public CapabilitiesType applySections(final Sections sections) {
        ServiceIdentification si = null;
        ServiceProvider       sp = null;
        OperationsMetadata    om = null;
        
        //we enter the information for service identification.
        if (sections.containsSection("ServiceIdentification") || sections.containsSection("All")) {
            si = getServiceIdentification();
        }

        //we enter the information for service provider.
        if (sections.containsSection("ServiceProvider") || sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        //we enter the operation Metadata
        if (sections.containsSection("OperationsMetadata") || sections.containsSection("All")) {
            om = getOperationsMetadata();
        }
        return new CapabilitiesType(si, sp, om, "2.0.2", getUpdateSequence());
    }
}
