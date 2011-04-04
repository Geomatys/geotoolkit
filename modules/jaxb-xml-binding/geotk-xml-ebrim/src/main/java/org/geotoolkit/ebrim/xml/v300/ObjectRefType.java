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
package org.geotoolkit.ebrim.xml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Use to reference an Object by its id.
 * Specifies the id attribute of the object as its id attribute.
 * id attribute in ObjectAttributes is exactly the same syntax and semantics as id attribute in RegistryObject.
 *       
 * 
 * <p>Java class for ObjectRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjectRefType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}IdentifiableType">
 *       &lt;attribute name="createReplica" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectRefType")
public class ObjectRefType extends IdentifiableType {

    @XmlAttribute
    private Boolean createReplica;

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
        final StringBuilder sb = new StringBuilder(super.toString());
        if (createReplica != null) {
            sb.append("createReplica:").append(createReplica).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ObjectRefType && super.equals(obj)) {
            final ObjectRefType that = (ObjectRefType) obj;
            return Utilities.equals(this.createReplica, that.createReplica);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (this.createReplica != null ? this.createReplica.hashCode() : 0);
        return hash;
    }
}
