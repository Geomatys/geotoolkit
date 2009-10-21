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

import java.util.List;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CodeListType;


/**
 * Unordered list of data transfer formats supported. 
 * 
 * <p>Java class for SupportedFormatsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportedFormatsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}formats" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nativeFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedFormatsType", propOrder = {
    "formats"
})
public class SupportedFormatsType {

    @XmlElement(required = true)
    private List<CodeListType> formats;
    @XmlAttribute
    private String nativeFormat;

    /**
     * Empty constructor use by JAXB
     */
    SupportedFormatsType(){
        
    }
    
    /**
     * Build a new list of supported formats.
     */
     public SupportedFormatsType(String nativeFormat, List<CodeListType> formats){
         this.nativeFormat = nativeFormat;
         this.formats      = formats;
     }
    
    /**
     * Gets the value of the formats property (unmodifiable).
     * 
     */
    public List<CodeListType> getFormats() {
        return Collections.unmodifiableList(formats);
    }

    /**
     * Gets the value of the nativeFormat property.
     */
    public String getNativeFormat() {
        return nativeFormat;
    }
}
