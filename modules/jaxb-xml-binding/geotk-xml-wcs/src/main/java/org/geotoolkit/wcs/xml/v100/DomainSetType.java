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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the spatial-temporal domain set of a coverage offering. The domainSet shall include a SpatialDomain (describing the spatial locations for which coverages can be requested), a TemporalDomain (describing the time instants or inter-vals for which coverages can be requested), or both. 
 * 
 * <p>Java class for DomainSetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainSetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.opengis.net/wcs}spatialDomain"/>
 *           &lt;element ref="{http://www.opengis.net/wcs}temporalDomain" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}temporalDomain"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainSetType", propOrder = {
    "content"
})
public class DomainSetType {

    @XmlElementRefs({
        @XmlElementRef(name = "spatialDomain", namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "temporalDomain", namespace = "http://www.opengis.net/wcs", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> content;

    /**
     * Empty constructor used by JAXB.
     */
    DomainSetType() {
        
    }
    
    /**
     * Empty constructor used by JAXB.
     */
    public DomainSetType(final SpatialDomainType spatialDomain, final TimeSequenceType temporalDomain) {
        ObjectFactory factory = new ObjectFactory();
        content = new ArrayList<JAXBElement<?>>();
        if (spatialDomain != null)
            content.add(factory.createSpatialDomain(spatialDomain));
        if (temporalDomain != null)
            content.add(factory.createTemporalDomain(temporalDomain));
    }
    
    
    
    /**
     * Gets the rest of the content model. 
     * 
     */
    public List<JAXBElement<?>> getContent() {
        return Collections.unmodifiableList(content);
    }

}
