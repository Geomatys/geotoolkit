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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDomain;


/**
 * Valid domain (or set of values) of one parameter or other quantity used by this server. A non-parameter quantity may not be explicitly represented in the server software. (Informative: An example is the outputFormat parameter of a WFS. Each WFS server should provide a Parameter element for the outputFormat parameter that lists the supported output formats, such as GML2, GML3, etc. as the allowed "Value" elements.)
 *
 * <p>Java class for DomainType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DomainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainType", propOrder = {
    "defaultValue",
    "value",
    "metadata"
})
public class DomainType implements AbstractDomain {

    @XmlElement(name = "Value", required = true)
    private List<String> value;

    /**
     * This attribute has been added for compatibility with other CSW.
     */
    @XmlElement(name = "DefaultValue", required = true)
    private String defaultValue;

    @XmlElement(name = "Metadata")
    private List<MetadataType> metadata;
    @XmlAttribute(required = true)
    private String name;

    /**
     * An empty constructor used by JAXB.
     */
    public DomainType() {

    }

    public DomainType(final DomainType that){
        if (that != null) {
            this.name         = that.name;
            this.defaultValue = that.defaultValue;
            if (that.value != null) {
                this.value = new ArrayList<>(that.value);
            }
            if (that.metadata != null) {
                this.metadata = new ArrayList<>();
                for (MetadataType m : that.metadata) {
                    this.metadata.add(new MetadataType(m));
                }
            }
        }
    }

    /**
     * Build a new Domain with the specified list of values.
     * @param name
     * @param value
     */
    public DomainType(final String name, final List<String> value) {
        this.name  = name;
        this.value = value;
    }

    /**
     * Build a new Domain with the a single value.
     * @param name
     * @param value
     */
    public DomainType(final String name, final String value) {
        this.name  = name;
        if (value != null) {
            this.value = new ArrayList<>();
            this.value.add(value);
        }
    }

    /**
     * return the default value for this domain
     */
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(final String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the value property.
     */
    @Override
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return value;
    }

    @Override
    public void setValue(final List<String> value) {
        this.value = value;
    }

    /**
     * Optional unordered list of additional metadata about this parameter.
     * A list of required and optional metadata elements for this domain should be specified in the Implementation Specification for this service.
     * (Informative: This metadata might specify the meanings of the valid values.) Gets the value of the metadata property.
     * (unmodifiable)
     */
    @Override
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return Collections.unmodifiableList(metadata);
    }

    /**
     * Gets the value of the name property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DomainType) {
            final DomainType that = (DomainType) object;
            return Objects.equals(this.defaultValue, that.defaultValue) &&
                   Objects.equals(this.metadata,     that.metadata)     &&
                   Objects.equals(this.name,         that.name)         &&
                   Objects.equals(this.value,        that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 89 * hash + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        hash = 89 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
