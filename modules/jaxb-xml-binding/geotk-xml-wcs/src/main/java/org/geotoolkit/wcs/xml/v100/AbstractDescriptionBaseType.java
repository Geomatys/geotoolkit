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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.AbstractGMLEntry;


/**
 * Description of a WCS object. 
 * 
 * WCS version 1.0.0
 * 
 * <p>Java class for AbstractDescriptionBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDescriptionBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}metadataLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guihem Legal
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDescriptionBaseType", propOrder = {
    "metadataLink"
})
@XmlSeeAlso({
    AbstractDescriptionType.class
})
public abstract class AbstractDescriptionBaseType extends AbstractGMLEntry {

    private List<MetadataLinkType> metadataLink;
    
     /**
     * Empty constructor used by JAXB
     */
    AbstractDescriptionBaseType(){
    }
    
    /**
     * build the base of a description.
     */
    public AbstractDescriptionBaseType(List<MetadataLinkType> metadataLink){
        this.metadataLink = metadataLink;
    }
    
    /**
     * build the base of a description.
     */
    public AbstractDescriptionBaseType(MetadataLinkType... metadataLinks){
        this.metadataLink = new ArrayList<MetadataLinkType>();
        
        for (MetadataLinkType e: metadataLinks){
            metadataLink.add(e);
        }
    }
    

    /**
     * Gets the value of the metadataLink property.
     * 
     */
    public List<MetadataLinkType> getMetadataLink() {
        if (metadataLink == null) {
            metadataLink = new ArrayList<MetadataLinkType>();
        }
        return this.metadataLink;
    }

}
