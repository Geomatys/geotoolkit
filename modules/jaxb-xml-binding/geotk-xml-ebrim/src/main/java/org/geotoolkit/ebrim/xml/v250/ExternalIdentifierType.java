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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * 
 * ExternalIdentifier is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 * 			
 * 
 * <p>Java class for ExternalIdentifierType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExternalIdentifierType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;attribute name="registryObject" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="identificationScheme" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="value" use="required" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}LongName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalIdentifierType")
@XmlRootElement(name = "ExternalIdentifier")
public class ExternalIdentifierType extends RegistryObjectType {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String registryObject;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String identificationScheme;
    @XmlAttribute(required = true)
    private String value;

    /**
     * Gets the value of the registryObject property.
     */
    public String getRegistryObject() {
        return registryObject;
    }

    /**
     * Sets the value of the registryObject property.
     */
    public void setRegistryObject(final String value) {
        this.registryObject = value;
    }

    /**
     * Gets the value of the identificationScheme property.
     */
    public String getIdentificationScheme() {
        return identificationScheme;
    }

    /**
     * Sets the value of the identificationScheme property.
    */
    public void setIdentificationScheme(final String value) {
        this.identificationScheme = value;
    }

    /**
     * Gets the value of the value property.
    */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (identificationScheme != null) {
            sb.append("identificationScheme:").append(identificationScheme).append('\n');
        }
        if (registryObject != null) {
            sb.append("registryObject:").append(registryObject).append('\n');
        }
        if (value != null) {
            sb.append("value:").append(value).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ExternalIdentifierType && super.equals(obj)) {
            final ExternalIdentifierType that = (ExternalIdentifierType) obj;
            return Utilities.equals(this.identificationScheme, that.identificationScheme) &&
                   Utilities.equals(this.registryObject,       that.registryObject) &&
                   Utilities.equals(this.value,                that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + super.hashCode();
        hash = 89 * hash + (this.registryObject != null ? this.registryObject.hashCode() : 0);
        hash = 89 * hash + (this.identificationScheme != null ? this.identificationScheme.hashCode() : 0);
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
