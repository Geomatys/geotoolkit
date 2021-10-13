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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * ExternalLink is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 *
 *
 * <p>Java class for ExternalLinkType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExternalLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;attribute name="externalURI" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalLinkType")
@XmlRootElement(name = "ExternalLink")
public class ExternalLinkType extends RegistryObjectType {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String externalURI;

    /**
     * Gets the value of the externalURI property.
     */
    public String getExternalURI() {
        return externalURI;
    }

    /**
     * Sets the value of the externalURI property.
     */
    public void setExternalURI(final String value) {
        this.externalURI = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (externalURI != null) {
            sb.append("externalURI:").append(externalURI).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ExternalLinkType && super.equals(obj)) {
            final ExternalLinkType that = (ExternalLinkType) obj;
            return Objects.equals(this.externalURI,      that.externalURI);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + super.hashCode();
        hash = 59 * hash + (this.externalURI != null ? this.externalURI.hashCode() : 0);
        return hash;
    }


}
