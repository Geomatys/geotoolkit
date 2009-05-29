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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for ConceptualSchemeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConceptualSchemeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Document" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="Authority" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConceptualSchemeType", propOrder = {
    "name",
    "document",
    "authority"
})
public class ConceptualSchemeType {

    @XmlElement(name = "Name", required = true)
    private String name;
    @XmlElement(name = "Document", required = true)
    @XmlSchemaType(name = "anyURI")
    private String document;
    @XmlElement(name = "Authority", required = true)
    @XmlSchemaType(name = "anyURI")
    private String authority;

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the document property.
     */
    public String getDocument() {
        return document;
    }

    /**
     * Gets the value of the authority property.
     */
    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ConceptualSchemeType]").append('\n');
        if (authority != null) {
            sb.append("authority:").append(authority).append('\n');
        }
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (document != null) {
            sb.append("document:").append(document).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ConceptualSchemeType) {
            final ConceptualSchemeType that = (ConceptualSchemeType) object;

            return  Utilities.equals(this.authority, that.authority) &&
                    Utilities.equals(this.name,      that.name)      &&
                    Utilities.equals(this.document,  that.document);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.document != null ? this.document.hashCode() : 0);
        hash = 17 * hash + (this.authority != null ? this.authority.hashCode() : 0);
        return hash;
    }


}
