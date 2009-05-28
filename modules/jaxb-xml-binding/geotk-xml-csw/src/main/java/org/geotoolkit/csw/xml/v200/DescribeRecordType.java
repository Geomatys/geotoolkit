/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DescribeRecord;


/**
 * 
 * This request allows a user to discover elements of the nformation model supported by the catalogue service.
 * If no TypeName elements are provided, then all of the schemas for the information model are returned
 *       
 *  schemaLanguage - preferred schema language (W3C XML Schema by default)
 *
 *  outputFormat   - preferred output format (text/xml by default)
 *          
 * 
 * <p>Java class for DescribeRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="TypeName" type="{http://www.opengis.net/cat/csw}TypeNameType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="outputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="text/xml" />
 *       &lt;attribute name="schemaLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://www.w3.org/XML/Schema" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeRecordType", propOrder = {
    "typeName"
})
public class DescribeRecordType extends RequestBaseType implements DescribeRecord {

    @XmlElement(name = "TypeName")
    private List<TypeNameType> typeName;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String schemaLanguage;

    /**
     * Gets the value of the typeName property.
     * 
     */
    public List<TypeNameType> getTypeName() {
        if (typeName == null) {
            typeName = new ArrayList<TypeNameType>();
        }
        return this.typeName;
    }

    /**
     * Gets the value of the outputFormat property.
     * 
     */
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "text/xml";
        } else {
            return outputFormat;
        }
    }

    /**
     * Sets the value of the outputFormat property.
     * 
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the schemaLanguage property.
     * 
     */
    public String getSchemaLanguage() {
        if (schemaLanguage == null) {
            return "http://www.w3.org/XML/Schema";
        } else {
            return schemaLanguage;
        }
    }

    /**
     * Sets the value of the schemaLanguage property.
     * 
     */
    public void setSchemaLanguage(String value) {
        this.schemaLanguage = value;
    }

}
