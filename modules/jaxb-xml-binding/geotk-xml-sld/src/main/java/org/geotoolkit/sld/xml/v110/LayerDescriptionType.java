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
package org.geotoolkit.sld.xml.v110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.se.xml.v110.OnlineResourceType;


/**
 * <p>Java class for LayerDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LayerDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="owsType" type="{http://www.opengis.net/sld}owsTypeType"/>
 *         &lt;element ref="{http://www.opengis.net/se}OnlineResource"/>
 *         &lt;element name="TypeName" type="{http://www.opengis.net/sld}TypeNameType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayerDescriptionType", propOrder = {
    "owsType",
    "onlineResource",
    "typeName"
})
public class LayerDescriptionType {

    @XmlElement(required = true)
    private String owsType= "wcs";
    @XmlElement(name = "OnlineResource", namespace = "http://www.opengis.net/se", required = true)
    private OnlineResourceType onlineResource;
    @XmlElement(name = "TypeName", required = true)
    private List<TypeNameType> typeName = new ArrayList<TypeNameType>();

    /**
     * An empty Constructor used by jaxB
     */
    LayerDescriptionType() {}
    
    /**
     * Build a new LayerDescriptionType
     * 
     */
    public LayerDescriptionType(final OnlineResourceType onlineResource, final TypeNameType... typeNames) {
        this.onlineResource = onlineResource;
        for (final TypeNameType element : typeNames) {
            this.typeName.add(element);
        }
    }
    
    /**
     * Gets the value of the owsType property.
     */
    public String getOwsType() {
        return owsType;
    }

    /**
     * Gets the value of the onlineResource property.
     */
    public OnlineResourceType getOnlineResource() {
        return onlineResource;
    }

    /**
     * Gets the value of the typeName property.
     * (unmodifiable)
     */
    public List<TypeNameType> getTypeName() {
        return Collections.unmodifiableList(typeName);
    }

}
