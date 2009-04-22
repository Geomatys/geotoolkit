/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package org.geotoolkit.inspire.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.OnlineResourceType;
import org.geotoolkit.util.Utilities;


/**
 * Connect point URL to translanted capabilities document. The language attribute shall be defined by a 3-letter code as described in ISO 639-2.
 * 
 * <p>Java class for DocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}OnlineResourceType">
 *       &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentType")
public class DocumentType extends OnlineResourceType {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anySimpleType")
    private String language;

    public DocumentType() {

    }

    public DocumentType(String href, String language) {
        super(href);
        this.language = language;
    }

    /**
     * Gets the value of the language property.
    */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DocumentType && super.equals(object)) {
            final DocumentType that = (DocumentType) object;
            return Utilities.equals(this.language, that.language);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 43 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if ( language != null) {
            sb.append("language:").append(language).append('\n');
        }
        return sb.toString();
    }

}
