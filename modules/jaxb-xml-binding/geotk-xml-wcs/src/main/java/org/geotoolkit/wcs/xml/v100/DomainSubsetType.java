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
 * Defines the desired subset of the domain set of the coverage. 
 * Is a GML property containing either or both spatialSubset and temporalSubset GML objects. 
 * 
 * <p>Java class for DomainSubsetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainSubsetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.opengis.net/wcs}spatialSubset"/>
 *           &lt;element ref="{http://www.opengis.net/wcs}temporalSubset" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}temporalSubset"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainSubsetType", propOrder = {
    "content"
})
public class DomainSubsetType {

    @XmlElementRefs({
        @XmlElementRef(name = "temporalSubset", namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "spatialSubset", namespace = "http://www.opengis.net/wcs", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> content;

    /**
     * An empty constructor used by JAXB
     */
    DomainSubsetType() {
        
    }
    
    /**
     * Build a new Domain subset with the specified spatial
     * and temporal subset
     */
    public DomainSubsetType(TimeSequenceType temporalSubset, SpatialSubsetType spatialSubset) {
        this.content = new ArrayList<JAXBElement<?>>();
        ObjectFactory factory = new ObjectFactory();
        if (temporalSubset != null)
            content.add(factory.createTemporalSubset(temporalSubset));
        if (spatialSubset != null)
        content.add(factory.createSpatialSubset(spatialSubset));
    }
    
    /**
     * Build a new Domain subset with a list of JAXB element
     */
    public DomainSubsetType(List<JAXBElement<?>> content) {
        this.content = content;
        
    }
    
    /**
     * Gets the rest of the content model.
     * (unmodifiable) 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return Collections.unmodifiableList(content);
    }
    
    /**
     * Return the temporal subSet. 
     */
    public TimeSequenceType getTemporalSubSet() {
        for (JAXBElement<?> element: content) {
            if (element.getName().getLocalPart().equalsIgnoreCase("temporalSubset")) {
                return (TimeSequenceType)element.getValue();
            }
        }
        return null;
    }
    
    /**
     * Return the spatial subSet. 
     */
    public SpatialSubsetType getSpatialSubSet() {
        for (JAXBElement<?> element: content) {
            if (element.getName().getLocalPart().equalsIgnoreCase("spatialSubset")) {
                return (SpatialSubsetType)element.getValue();
            }
        }
        return null;
    }

}
