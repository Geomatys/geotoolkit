/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.wcs.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v200.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v200.OperationsMetadata;
import org.geotoolkit.ows.xml.v200.SectionsType;
import org.geotoolkit.ows.xml.v200.ServiceIdentification;
import org.geotoolkit.ows.xml.v200.ServiceProvider;
import org.geotoolkit.wcs.xml.GetCapabilitiesResponse;
import org.geotoolkit.wcs.xml.WCSResponse;


/**
 * <p>Java class for CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}ServiceMetadata" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}Contents" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesType", propOrder = {
    "serviceMetadata",
    "contents"
})
@XmlRootElement(name = "Capabilities")
public class CapabilitiesType extends CapabilitiesBaseType implements GetCapabilitiesResponse, WCSResponse {

    @XmlElement(name = "ServiceMetadata")
    private ServiceMetadataType serviceMetadata;
    @XmlElement(name = "Contents")
    private ContentsType contents;

    /**
     * An empty constructor used by JAXB
     */
    CapabilitiesType(){}
    
    /**
     * Build a new Capabilities document.
     */
    public CapabilitiesType(final String version, final String updateSequence) {
        super(null, null, null, version, updateSequence);
    }
    
    /**
     * Build a new Capabilities document.
     */
    public CapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final ContentsType contents,
            final ServiceMetadataType serviceMetadata) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
        this.contents = contents;
        this.serviceMetadata = serviceMetadata;
    }
    
    /**
     * Despite its name this element should not be confuse with the OWSServiceMetadata defined in OWS Common.
     * 
     * @return
     *     possible object is
     *     {@link ServiceMetadataType }
     *     
     */
    public ServiceMetadataType getServiceMetadata() {
        return serviceMetadata;
    }

    /**
     * Sets the value of the serviceMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceMetadataType }
     *     
     */
    public void setServiceMetadata(ServiceMetadataType value) {
        this.serviceMetadata = value;
    }

    /**
     * Gets the value of the contents property.
     * 
     * @return
     *     possible object is
     *     {@link ContentsType }
     *     
     */
    @Override
    public ContentsType getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentsType }
     *     
     */
    public void setContents(ContentsType value) {
        this.contents = value;
    }

    @Override
    public CapabilitiesType applySections(Sections sections) {
        if (sections == null) {
            sections = new SectionsType("All");
        }
        ServiceIdentification si = null;
        ServiceProvider sp       = null;
        OperationsMetadata om    = null;
        ContentsType ct          = null;
        //we add the static sections if the are included in the requested sections
        if (sections.containsSection("ServiceProvider") || sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        if (sections.containsSection("ServiceIdentification") || sections.containsSection("All")) {
            si = getServiceIdentification();
        }
        if (sections.containsSection("OperationsMetadata") || sections.containsSection("All")) {
            om = getOperationsMetadata();
        }
        // if the user does not request the contents section we can return the result.
        if (sections.containsSection("Contents") || sections.containsSection("All")) {
            ct = contents;
        }
        return new CapabilitiesType(si, sp, om, "2.0.0", getUpdateSequence(), ct, getServiceMetadata());
    }
}
