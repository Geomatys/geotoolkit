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
package org.geotoolkit.wps.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wps.xml.DocumentOutputDefinition;
import org.geotoolkit.wps.xml.OutputDefinition;


/**
 * Definition of a format, encoding,  schema, and unit-of-measure for an output to be returned from a process. 
 * 
 * <p>Java class for OutputDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wps/1.0.0}ComplexDataEncoding"/>
 *       &lt;attribute name="uom" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputDefinitionType", propOrder = {
    "identifier"
})
@XmlSeeAlso({
    DocumentOutputDefinitionType.class
})
public class OutputDefinitionType implements OutputDefinition {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected CodeType identifier;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String uom;
    @XmlAttribute
    protected String mimeType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    public OutputDefinitionType() {
        
    }
    
    public OutputDefinitionType(CodeType identifier) {
        this.identifier = identifier;
    }
    
    public OutputDefinitionType(CodeType identifier, String uom, String mimeType, String encoding, String schema) {
        this.identifier = identifier;
        this.encoding = encoding;
        this.mimeType = mimeType;
        this.schema = schema;
        this.uom = uom;
    }
    
    /**
     * Unambiguous identifier or name of an output, unique for this process. 
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    @Override
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Unambiguous identifier or name of an output, unique for this process. 
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setIdentifier(final CodeType value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setUom(final String value) {
        this.uom = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setMimeType(final String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setEncoding(final String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the schema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the value of the schema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setSchema(final String value) {
        this.schema = value;
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public DocumentOutputDefinition asDoc() {
        return new DocumentOutputDefinitionType(identifier, uom, mimeType, encoding, schema);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (encoding != null) {
            sb.append("encoding:").append(encoding).append('\n');
        }
        if (identifier != null) {
            sb.append("identifier:").append(identifier).append('\n');
        }
        if (mimeType != null) {
            sb.append("mimeType:").append(mimeType).append('\n');
        }
        if (schema != null) {
            sb.append("schema:").append(schema).append('\n');
        }
        if (uom != null) {
            sb.append("uom:").append(uom).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OutputDefinitionType) {
            final OutputDefinitionType that = (OutputDefinitionType) object;
            return Objects.equals(this.encoding, that.encoding) &&
                   Objects.equals(this.identifier, that.identifier) &&
                   Objects.equals(this.schema, that.schema) &&
                   Objects.equals(this.uom, that.uom) &&
                   Objects.equals(this.mimeType, that.mimeType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.identifier);
        hash = 97 * hash + Objects.hashCode(this.uom);
        hash = 97 * hash + Objects.hashCode(this.mimeType);
        hash = 97 * hash + Objects.hashCode(this.encoding);
        hash = 97 * hash + Objects.hashCode(this.schema);
        return hash;
    }
}
