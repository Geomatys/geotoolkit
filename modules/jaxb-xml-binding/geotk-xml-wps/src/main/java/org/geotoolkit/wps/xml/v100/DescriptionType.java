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
package org.geotoolkit.wps.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDescription;
import org.geotoolkit.ows.xml.AbstractKeywords;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.ows.xml.v110.MetadataType;


/**
 * Description of a WPS process or output object.
 *
 * <p>Java class for DescriptionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Title"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescriptionType", propOrder = {
    "identifier",
    "title",
    "_abstract",
    "metadata"
})
@XmlSeeAlso({
    ProcessBriefType.class,
    OutputDataType.class,
    InputDescriptionType.class,
    OutputDescriptionType.class
})
public class DescriptionType implements AbstractDescription {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected CodeType identifier;
    @XmlElement(name = "Title", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected LanguageStringType title;
    @XmlElement(name = "Abstract", namespace = "http://www.opengis.net/ows/1.1")
    protected LanguageStringType _abstract;
    @XmlElement(name = "Metadata", namespace = "http://www.opengis.net/ows/1.1")
    protected List<MetadataType> metadata;

    public DescriptionType() {

    }

    public DescriptionType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract) {
        this._abstract = _abstract;
        this.title = title;
        this.identifier = identifier;
    }

    /**
     * Unambiguous identifier or name of a process, unique for this server
     * , or unambiguous identifier or name of an output, unique for this process.
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
     * Unambiguous identifier or name of a process, unique for this server,
     * or unambiguous identifier or name of an output, unique for this process.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setIdentifier(final CodeType value) {
        this.identifier = value;
    }

    /**
     * Title of a process or output, normally available for display to a human.
     *
     * @return
     *     possible object is
     *     {@link LanguageStringType }
     *
     */
    public LanguageStringType getTitle() {
        return title;
    }

    /**
     * Title of a process or output, normally available for display to a human.
     *
     * @param value
     *     allowed object is
     *     {@link LanguageStringType }
     *
     */
    public void setTitle(final LanguageStringType value) {
        this.title = value;
    }

    /**
     * Brief narrative description of a process or output,
     * normally available for display to a human.
     *
     * @return
     *     possible object is
     *     {@link LanguageStringType }
     *
     */
    public LanguageStringType getAbstract() {
        return _abstract;
    }

    /**
     * Brief narrative description of a process or output,
     * normally available for display to a human.
     *
     * @param value
     *     allowed object is
     *     {@link LanguageStringType }
     *
     */
    public void setAbstract(final LanguageStringType value) {
        this._abstract = value;
    }

    /**
     * Optional unordered list of additional metadata about this process/input/output.
     * A list of optional and/or required metadata elements for this process/input/output could be specified in an Application Profile for this service.
     * Gets the value of the metadata property.
     *
     * @return Objects of the following type(s) are allowed in the list {@link MetadataType }
     *
     *
     */
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

    @Override
    public String getFirstTitle() {
        return title==null ? null : title.getValue();
    }

    @Override
    public String getFirstAbstract() {
        return _abstract==null ? null : _abstract.getValue();
    }

    /**
     * Information is not available on this version.
     * @return
     */
    @Override
    public List<? extends AbstractKeywords> getKeywords() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (identifier != null) {
            sb.append("Identifier:\n").append(identifier).append('\n');
        }
        if (title != null) {
            sb.append("Title:\n").append(title).append('\n');
        }
        if (_abstract != null) {
            sb.append("Abstract:\n").append(_abstract).append('\n');
        }
        if (metadata != null) {
            sb.append("metadata:\n");
            for (MetadataType out : metadata) {
                sb.append(out).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DescriptionType) {
            final DescriptionType that = (DescriptionType) object;
            return Objects.equals(this._abstract, that._abstract) &&
                   Objects.equals(this.identifier, that.identifier) &&
                   Objects.equals(this.title, that.title) &&
                   Objects.equals(this.metadata, that.metadata);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.identifier);
        hash = 29 * hash + Objects.hashCode(this.title);
        hash = 29 * hash + Objects.hashCode(this._abstract);
        hash = 29 * hash + Objects.hashCode(this.metadata);
        return hash;
    }

}
