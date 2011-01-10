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
package org.geotoolkit.ows.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Complete reference to a remote or local resource, allowing including metadata about that resource. 
 * 
 * <p>Java class for ReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}AbstractReferenceBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Format" type="{http://www.opengis.net/ows/1.1}MimeType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceType", propOrder = {
    "identifier",
    "_abstract",
    "format",
    "metadata"
})
@XmlSeeAlso({
    ServiceReferenceType.class
})
public class ReferenceType extends AbstractReferenceBaseType {

    @XmlElement(name = "Identifier")
    private CodeType identifier;
    @XmlElement(name = "Abstract")
    private List<LanguageStringType> _abstract;
    @XmlElement(name = "Format")
    private String format;
    @XmlElement(name = "Metadata")
    private List<MetadataType> metadata;

    /**
     * Optional unique identifier of the referenced resource. 
     * 
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     */
    public void setIdentifier(final CodeType value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the abstract property.
     * 
     */
    public List<LanguageStringType> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<LanguageStringType>();
        }
        return this._abstract;
    }

    /**
     * Gets the value of the format property.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     */
    public void setFormat(final String value) {
        this.format = value;
    }

    /**
     * Optional unordered list of additional metadata about this resource. 
     * A list of optional metadata elements for this ReferenceType could be specified in the Implementation Specification for each use of this type in a specific OWS. Gets the value of the metadata property.
     * 
     */
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<MetadataType>();
        }
        return this.metadata;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ReferenceType && super.equals(object)) {
            final ReferenceType that = (ReferenceType) object;
            return Utilities.equals(this._abstract,  that._abstract)  &&
                   Utilities.equals(this.format,     that.format)     &&
                   Utilities.equals(this.identifier, that.identifier) &&
                   Utilities.equals(this.metadata,   that.metadata);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        hash = 29 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 29 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 29 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        return hash;
    }
}
