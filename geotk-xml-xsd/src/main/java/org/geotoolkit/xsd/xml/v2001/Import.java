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
package org.geotoolkit.xsd.xml.v2001;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="schemaLocation" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "import")
public class Import extends Annotated {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String namespace;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String schemaLocation;

    public Import() {

    }

    public Import(final String namespace, final String schemaLocation) {
        this.namespace      = namespace;
        this.schemaLocation = schemaLocation;
    }

    /**
     * Gets the value of the namespace property.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     */
    public void setNamespace(final String value) {
        this.namespace = value;
    }

    /**
     * Gets the value of the schemaLocation property.
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Sets the value of the schemaLocation property.
     *
     */
    public void setSchemaLocation(final String value) {
        this.schemaLocation = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Import && super.equals(object)) {
            final Import that = (Import) object;
            return Objects.equals(this.namespace,      that.namespace)     &&
                   Objects.equals(this.schemaLocation, that.schemaLocation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.namespace != null ? this.namespace.hashCode() : 0);
        hash = 59 * hash + (this.schemaLocation != null ? this.schemaLocation.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (namespace != null) {
            sb.append("namespace:").append(namespace).append('\n');
        }
        if (schemaLocation != null) {
            sb.append("schemaLocation:").append(schemaLocation).append('\n');
        }
        return  sb.toString();
    }

}
