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
package org.geotoolkit.wcs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311modified.GridType;


/**
 * Definition of a subset of a coverage spatial domain.
 * Currently, only a grid subset of a coverage domain. 
 * 
 * <p>Java class for SpatialSubsetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpatialSubsetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/wcs}SpatialDomainType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Grid"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialSubsetType")
public class SpatialSubsetType extends SpatialDomainType {
    
    /**
     * An empty constructor used by JAXB
     */
    SpatialSubsetType(){
    }
    
    /**
     * Build a new Spatial subset.
     */
    public SpatialSubsetType(EnvelopeEntry envelope, GridType grid){
        super(envelope, grid);
    }
    
    

}
