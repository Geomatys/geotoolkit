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
package org.geotoolkit.ebrim.xml.v250;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * Use to reference an Object by its id.
 * Specifies the id attribute of the object as its id attribute.
 * id attribute in ObjectAttributes is exactly the same syntax and semantics as
 * id attribute in RegistryObject.
 *
 *
 * <p>Java class for ObjectRefType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ObjectRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="home" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="createReplica" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectRefType")
public class ObjectRefType {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String id;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String home;
    @XmlAttribute
    private Boolean createReplica;

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Gets the value of the home property.
     */
    public String getHome() {
        return home;
    }

    /**
     * Sets the value of the home property.
     */
    public void setHome(final String value) {
        this.home = value;
    }

    /**
     * Gets the value of the createReplica property.
     */
    public boolean isCreateReplica() {
        if (createReplica == null) {
            return false;
        } else {
            return createReplica;
        }
    }

    /**
     * Sets the value of the createReplica property.
    */
    public void setCreateReplica(final Boolean value) {
        this.createReplica = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ObjectRefType]\n");
        if (createReplica != null) {
            sb.append("createReplica:").append(createReplica).append('\n');
        }
        if (home != null) {
            sb.append("home:").append(home).append('\n');
        }
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ObjectRefType) {
            final ObjectRefType that = (ObjectRefType) obj;
            return Objects.equals(this.createReplica, that.createReplica) &&
                   Objects.equals(this.id,            that.id) &&
                   Objects.equals(this.home,          that.home) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.home != null ? this.home.hashCode() : 0);
        hash = 89 * hash + (this.createReplica != null ? this.createReplica.hashCode() : 0);
        return hash;
    }
}
