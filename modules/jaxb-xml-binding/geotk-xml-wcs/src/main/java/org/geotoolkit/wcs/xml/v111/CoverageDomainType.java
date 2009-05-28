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
package org.geotoolkit.wcs.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Definition of the spatial-temporal domain of a coverage. The Domain shall include a SpatialDomain (describing the spatial locations for which coverages can be requested), and should included a TemporalDomain (describing the time instants or intervals for which coverages can be requested). 
 * 
 * <p>Java class for CoverageDomainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageDomainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpatialDomain" type="{http://www.opengis.net/wcs}SpatialDomainType"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}TemporalDomain" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageDomainType", propOrder = {
    "spatialDomain",
    "temporalDomain"
})
public class CoverageDomainType {

    @XmlElement(name = "SpatialDomain", required = true)
    private SpatialDomainType spatialDomain;
    @XmlElement(name = "TemporalDomain")
    private TimeSequenceType temporalDomain;

     /**
     * empty constructor used by JAXB.
     */
    CoverageDomainType() {
    }
    
    /**
     * build a new coverage domain.
     */
    public CoverageDomainType(SpatialDomainType spatialDomain, TimeSequenceType temporalDomain) {
        this.spatialDomain  = spatialDomain;
        this.temporalDomain = temporalDomain;
    }
    
    /**
     * Gets the value of the spatialDomain property.
     * 
     */
    public SpatialDomainType getSpatialDomain() {
        return spatialDomain;
    }
   
    /**
     * Although optional, the TemporalDomain should be included whenever a value is known or a useful estimate is available. 
     */
    public TimeSequenceType getTemporalDomain() {
        return temporalDomain;
    }
}
