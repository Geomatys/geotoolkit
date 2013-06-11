/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Complete reference to a remote or local resource,
 *       allowing including metadata about that resource.
 * 
 * <p>Java class for ReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}AbstractReferenceBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Identifier" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Format" type="{http://www.opengis.net/ows/2.0}MimeType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
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
    @XmlElementRef(name = "Metadata", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends MetadataType>> metadata;

    /**
     * Optional unique identifier of the referenced
     *               resource.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setIdentifier(CodeType value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link LanguageStringType }
     * 
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
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Optional unordered list of additional metadata
     *               about this resource. A list of optional metadata elements for
     *               this ReferenceType could be specified in the Implementation
     *               Specification for each use of this type in a specific
     *               OWS.Gets the value of the metadata property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     * {@link JAXBElement }{@code <}{@link AdditionalParametersType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends MetadataType>> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<JAXBElement<? extends MetadataType>>();
        }
        return this.metadata;
    }

}
