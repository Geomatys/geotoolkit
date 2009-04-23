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
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.DescribeRecord;
import org.geotoolkit.util.Utilities;


/**
 * This request allows a user to discover elements of the information model supported by the catalogue. 
 * If no TypeName elements are included, then all of the schemas for the information model must be returned.
 *       
 *  schemaLanguage - preferred schema language (W3C XML Schema by default)
 * 
 *  outputFormat - preferred output format (application/xml by default)
 * 
 * <p>Java class for DescribeRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="TypeName" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="outputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="application/xml" />
 *       &lt;attribute name="schemaLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://www.w3.org/XML/Schema" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "typeName"
})
@XmlRootElement(name = "DescribeRecord")
public class DescribeRecordType extends RequestBaseType implements DescribeRecord {

    @XmlElement(name = "TypeName")
    private List<QName> typeName;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String schemaLanguage;

    /**
     * An empty constructor used by JAXB
     */
    DescribeRecordType() {
        
    }
    
    /**
     * Build a new DescribeRecord Request
     * 
     * @param service Fixed at "CSW".
     * @param version The version of the service.
     * @param typeName A list of QName
     * @param outputFormat The desired MIME type of the response
     * @param schemaLanguage sefault value is http://www.w3.org/XML/Schema
     */
    public DescribeRecordType(String service, String version, List<QName> typeName,
            String outputFormat, String schemaLanguage) {
        super(service, version);
        this.outputFormat   = outputFormat;
        this.schemaLanguage = schemaLanguage;
        this.typeName       = typeName;
        
    }
    
    
    /**
     * Gets the value of the typeName property.
     * (unmodifiable)
     */
    public List<QName> getTypeName() {
        if (typeName == null) {
            typeName = new ArrayList<QName>();
        }
        return Collections.unmodifiableList(typeName);
    }

    /**
     * Gets the value of the outputFormat property.
     */
    public String getOutputFormat() {
        return outputFormat;
    }
    
    /**
     * Gets the value of the outputFormat property.
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * Gets the value of the schemaLanguage property.
     */
    public String getSchemaLanguage() {
        if (schemaLanguage == null) {
            return "http://www.w3.org/XML/Schema";
        } else {
            return schemaLanguage;
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DescribeRecordType && super.equals(object)) {
            final DescribeRecordType that = (DescribeRecordType) object;
            return Utilities.equals(this.outputFormat,   that.outputFormat)   &&
                   Utilities.equals(this.schemaLanguage, that.schemaLanguage) &&
                   Utilities.equals(this.typeName,       that.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 53 * hash + (this.outputFormat != null ? this.outputFormat.hashCode() : 0);
        hash = 53 * hash + (this.schemaLanguage != null ? this.schemaLanguage.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());

        if (outputFormat != null) {
            s.append("outputFormat: ").append(outputFormat).append('\n');
        }
        if (schemaLanguage != null) {
            s.append("schemaLanguage: ").append(schemaLanguage).append('\n');
        }
        if (typeName != null) {
            s.append("typeName: ").append(typeName).append('\n');
        }
        return s.toString();
    }
}
