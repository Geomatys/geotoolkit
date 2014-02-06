/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swes.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v321.CodeType;


/**
 * <p>Java class for AbstractSWESType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSWESType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identifier" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.opengis.net/gml/3.2}CodeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/swes/2.0}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSWESType", propOrder = {
    "description",
    "identifier",
    "name",
    "extension"
})
@XmlSeeAlso({
    AbstractOfferingType.class,
    //ResultTemplateType.class,
    AbstractContentsType.class,
    FilterDialectMetadataType.class,
    NotificationProducerMetadataType.class,
    SWESEventType.class
})
public abstract class AbstractSWESType {

    private String description;
    @XmlSchemaType(name = "anyURI")
    private String identifier;
    private List<CodeType> name;
    private List<Object> extension;
    @XmlAttribute(namespace = "http://www.opengis.net/swes/2.0")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public AbstractSWESType() {
        
    }
    
    public AbstractSWESType(final String id, final String identifier, final String name, final String description) {
        this.id = id;
        this.identifier = identifier;
        if (name != null) {
            this.name = new ArrayList<>();
            this.name.add(new CodeType(name));
        }
        this.description = description;
    }
    
    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    public String getName() {
        if (name != null && !name.isEmpty()) {
            name.get(0).getValue();
        }
        return null;
    }
    
    /**
     * Gets the value of the name property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link CodeType }
     * 
     */
    public List<CodeType> getFullName() {
        if (name == null) {
            name = new ArrayList<>();
        }
        return this.name;
    }

    /**
     * Gets the value of the extension property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<>();
        }
        return this.extension;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        if (identifier != null) {
            sb.append("identifier:").append(identifier).append('\n');
        }
        if (description != null) {
            sb.append("description:").append(description).append('\n');
        }
        if (name != null) {
            sb.append("name:\n");
            for (CodeType foit : name) {
                sb.append(foit).append('\n');
            }
        }
        if (extension != null) {
            sb.append("extension:\n");
            for (Object foit : extension) {
                sb.append(foit).append('\n');
            }
        }
        return sb.toString();
    }
}
