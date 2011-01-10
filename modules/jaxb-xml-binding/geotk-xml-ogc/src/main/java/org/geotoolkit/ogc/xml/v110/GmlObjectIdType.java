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
package org.geotoolkit.ogc.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.opengis.filter.identity.GmlObjectId;


/**
 * <p>Java class for GmlObjectIdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GmlObjectIdType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}AbstractIdType">
 *       &lt;attribute ref="{http://www.opengis.net/gml}id use="required""/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GmlObjectIdType")
public class GmlObjectIdType extends AbstractIdType implements GmlObjectId {

    @XmlAttribute(namespace = "http://www.opengis.net/gml", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     * An empty constructor used by JAXB
     */
    public GmlObjectIdType() {
        
    }
    
    /**
     * Build a new GML object Id with the specified ID
     */
    public GmlObjectIdType(final String id) {
        this.id = id;
    }
    
    public String getID() {
        return id;
    }

    public boolean matches(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
