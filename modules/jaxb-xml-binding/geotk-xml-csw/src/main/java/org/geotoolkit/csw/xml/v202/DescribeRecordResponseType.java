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
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DescribeRecordResponse;


/**
 * The response contains a list of matching schema components
 *          in the requested schema language.
 * 
 * <p>Java class for DescribeRecordResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeRecordResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SchemaComponent" type="{http://www.opengis.net/cat/csw/2.0.2}SchemaComponentType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeRecordResponseType", propOrder = {
    "schemaComponent"
})
@XmlRootElement(name = "DescribeRecordResponse")
public class DescribeRecordResponseType implements DescribeRecordResponse {

    @XmlElement(name = "SchemaComponent")
    private List<SchemaComponentType> schemaComponent;

    /**
     * An empty constructor used by JAXB.
     */
    public DescribeRecordResponseType() {
        
    }
    
    /**
     * Build a new response to a describeRecord request.
     */
    public DescribeRecordResponseType(SchemaComponentType... schemaCompo) {
        schemaComponent = new ArrayList<SchemaComponentType>();
        for (SchemaComponentType sc: schemaCompo) {
            schemaComponent.add(sc);
        }
    }
    
    /**
     * Build a new response to a describeRecord request.
     */
    public DescribeRecordResponseType(List<SchemaComponentType> schemaComponent) {
        this.schemaComponent = schemaComponent;
    }
    
    /**
     * Gets the value of the schemaComponent property.
     * (unmodifiable)
     */
    public List<SchemaComponentType> getSchemaComponent() {
        if (schemaComponent == null) {
            schemaComponent = new ArrayList<SchemaComponentType>();
        }
        return Collections.unmodifiableList(schemaComponent);
    }

}
