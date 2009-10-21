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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.SchemaComponent;


/**
 * A schema component includes a schema fragment (type definition) or an entire schema from some target namespace;
 * the schema language is identified by URI. 
 * If the component is a schema fragment its parent MUST be referenced (parentSchema).
 * 
 * <p>Java class for SchemaComponentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SchemaComponentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="targetNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="parentSchema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="schemaLanguage" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchemaComponentType", propOrder = {
    "content"
})
public class SchemaComponentType implements SchemaComponent {

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> content;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String targetNamespace;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String parentSchema;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String schemaLanguage;

    /**
     * An empty constructor used by JAXB.
     */
    public SchemaComponentType() {
        
    }
    
    /**
     * Build a new Schema Component.
     */
    public SchemaComponentType(String targetNamespace, String schemaLanguage, Object xsd) {
        this.targetNamespace = targetNamespace;
        this.schemaLanguage  = schemaLanguage; 
        this.content         = new ArrayList<Object>();
        content.add(xsd);
    }
    
    /**
     * A schema component includes a schema fragment (type definition) or an entire schema from some target namespace;
     * the schema language is identified by URI. 
     * If the component is a schema fragment its parent MUST be referenced (parentSchema).
     * Gets the value of the content property.
     * (unmodifiable)
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return Collections.unmodifiableList(content);
    }

    /**
     * Gets the value of the targetNamespace property.
     * 
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Gets the value of the parentSchema property.
     */
    public String getParentSchema() {
        return parentSchema;
    }
    
    /**
     * Gets the value of the schemaLanguage property.
     * 
     */
    public String getSchemaLanguage() {
        return schemaLanguage;
    }
}
