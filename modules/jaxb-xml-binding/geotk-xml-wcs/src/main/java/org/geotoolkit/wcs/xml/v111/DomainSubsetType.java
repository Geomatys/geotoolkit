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
package org.geotoolkit.wcs.xml.v111;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType;


/**
 * Definition of the desired subset of the domain of the coverage. Contains a spatial BoundingBox and optionally a TemporalSubset. 
 * 
 * <p>Java class for DomainSubsetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainSubsetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}BoundingBox"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/1.1.1}TemporalSubset" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainSubsetType", propOrder = {
    "boundingBox",
    "temporalSubset"
})
public class DomainSubsetType {

    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class)
    private JAXBElement<? extends BoundingBoxType> boundingBox;
    @XmlElement(name = "TemporalSubset")
    private TimeSequenceType temporalSubset;

    /**
     * Empty constructor used by JAXB.
     */
     DomainSubsetType(){
         
     }
     
     /**
      * Build a new Domain Subset.
      */
     public DomainSubsetType(TimeSequenceType temporal, BoundingBoxType boundingBox){
         this.temporalSubset = temporal;
         org.geotoolkit.ows.xml.v110.ObjectFactory facto = new org.geotoolkit.ows.xml.v110.ObjectFactory();
         this.boundingBox = facto.createBoundingBox(boundingBox);
     }
    
    /**
     * Definition of desired spatial subset of a coverage domain. 
     * When the entire spatial extent of this coverage is desired, 
     * this BoundingBox can be copied from the Domain part of the Coverage Description. 
     * However, the entire spatial extent may be larger than a WCS server can output, 
     * in which case the server shall respond with an error message. 
     * Notice that WCS use of this BoundingBox is further specified in specification Subclause 7.5. 
     */
    public JAXBElement<? extends BoundingBoxType> getBoundingBox() {
        return boundingBox;
    }

    /**
     * Optional definition of desired temporal subset of a coverage domain. 
     * If this data structure is omitted, the entire Temporal domain shall be output. 
     */
    public TimeSequenceType getTemporalSubset() {
        return temporalSubset;
    }
}
