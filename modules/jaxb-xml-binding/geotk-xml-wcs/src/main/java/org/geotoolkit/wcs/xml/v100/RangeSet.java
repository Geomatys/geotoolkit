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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeSet")
public class RangeSet {
    
    @XmlElement(name="RangeSet")
    private RangeSetType rangeSet;
    
    /**
     * An empty constructor used by JAXB.
     */
    RangeSet(){
        
    }
    
    /**
     * Build a new RangeSet.
     */
    public RangeSet(RangeSetType rangeSet){
        this.rangeSet = rangeSet;
    }
    
    /**
     * Return the rangeSet property
     */
    public RangeSetType getRangeSet(){
        return rangeSet;
    }

}
