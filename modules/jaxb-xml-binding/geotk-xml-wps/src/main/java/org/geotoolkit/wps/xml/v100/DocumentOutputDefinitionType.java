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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.wps.xml.DocumentOutputDefinition;


/**
 * Definition of a format, encoding,  schema, and unit-of-measure for an output to be returned from a process.
 *
 * In this use, the DescriptionType shall describe this process input or output.
 *
 * <p>Java class for DocumentOutputDefinitionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DocumentOutputDefinitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}OutputDefinitionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Abstract" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="asReference" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentOutputDefinitionType", propOrder = {
    "title",
    "_abstract"
})
public class DocumentOutputDefinitionType extends OutputDefinitionType implements DocumentOutputDefinition {

    @XmlElement(name = "Title", namespace = "http://www.opengis.net/ows/1.1")
    protected LanguageStringType title;
    @XmlElement(name = "Abstract", namespace = "http://www.opengis.net/ows/1.1")
    protected LanguageStringType _abstract;
    @XmlAttribute
    protected Boolean asReference;

    public DocumentOutputDefinitionType() {

    }

    public DocumentOutputDefinitionType(CodeType identifier, Boolean asReference) {
        super(identifier);
        this.asReference = asReference;
    }

    public DocumentOutputDefinitionType(CodeType identifier, String uom, String mimeType, String encoding, String schema) {
        super(identifier, uom, mimeType, encoding, schema);
    }


    /**
     * Title of the process output, normally available for display to a human.
     * This element should be used if the client wishes to customize the Title in the execute response.
     * This element should not be used if the Title provided for this output in the ProcessDescription is adequate.
     *
     * @return
     *     possible object is
     *     {@link LanguageStringType }
     *
     */
    @Override
    public LanguageStringType getTitle() {
        return title;
    }

    /**
     * Title of the process output, normally available for display to a human.
     * This element should be used if the client wishes to customize the Title in the execute response.
     * This element should not be used if the Title provided for this output in the ProcessDescription is adequate.
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
     * Brief narrative description of a process output, normally available for display to a human.
     * This element should be used if the client wishes to customize the Abstract in the execute response.
     * This element should not be used if the Abstract provided for this output in the ProcessDescription is adequate.
     *
     * @return
     *     possible object is
     *     {@link LanguageStringType }
     *
     */
    @Override
    public LanguageStringType getAbstract() {
        return _abstract;
    }

    /**
     * Brief narrative description of a process output, normally available for display to a human.
     * This element should be used if the client wishes to customize the Abstract in the execute response.
     * This element should not be used if the Abstract provided for this output in the ProcessDescription is adequate.
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
     * Gets the value of the asReference property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isAsReference() {
        if (asReference == null) {
            return false;
        } else {
            return asReference;
        }
    }

    /**
     * Sets the value of the asReference property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    @Override
    public void setAsReference(final Boolean value) {
        this.asReference = value;
    }

    @Override
    public boolean isReference() {
        if (asReference == null) {
            return false;
        } else {
            return asReference;
        }
    }

    @Override
    public DocumentOutputDefinition asDoc() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (_abstract != null) {
            sb.append("_abstract:").append(_abstract).append('\n');
        }
        if (title != null) {
            sb.append("title:").append(title).append('\n');
        }
        if (asReference != null) {
            sb.append("asReference:\n").append(asReference).append('\n');
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
        if (object instanceof DocumentOutputDefinitionType && super.equals(object)) {
            final DocumentOutputDefinitionType that = (DocumentOutputDefinitionType) object;
            return Objects.equals(this._abstract, that._abstract) &&
                   Objects.equals(this.asReference, that.asReference) &&
                   Objects.equals(this.title, that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.title);
        hash = 23 * hash + Objects.hashCode(this._abstract);
        hash = 23 * hash + Objects.hashCode(this.asReference);
        return hash;
    }
}
