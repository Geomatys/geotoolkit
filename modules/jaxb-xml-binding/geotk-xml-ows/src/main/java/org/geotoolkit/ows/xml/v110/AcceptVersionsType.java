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
package org.geotoolkit.ows.xml.v110;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Prioritized sequence of one or more specification versions accepted by client, with preferred versions listed first. See Version negotiation subclause for more information. 
 * 
 * <p>Java class for AcceptVersionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AcceptVersionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Version" type="{http://www.opengis.net/ows/1.1}VersionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcceptVersionsType", propOrder = {
    "version"
})
public class AcceptVersionsType implements org.geotoolkit.ows.xml.AcceptVersions {

    @XmlElement(name = "Version", required = true)
    private List<String> version;

    /**
     * Empty constructor used by JAXB.
     */
    AcceptVersionsType(){
        
    }
    
    /**
     * Build a new List of acceptVersion.
     */
    public AcceptVersionsType(String... versions){
        version = new ArrayList<String>();
        for (String v: versions) {
            version.add(v);
        }
    }
    
    /**
     * Gets the value of the version property.
     */
    public List<String> getVersion() {
        if (version == null) {
            version = new ArrayList<String>();
        }
        return Collections.unmodifiableList(version);
    }
    
    /**
     * Add a new accepted version to the list.
     * 
     * @param version a number of version.
     */
    public void addVersion(String version) {
        this.version.add(version);
    }

}
