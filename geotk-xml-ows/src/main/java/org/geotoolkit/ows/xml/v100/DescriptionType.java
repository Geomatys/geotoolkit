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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractAdditionalParameters;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.AbstractDescription;
import org.geotoolkit.ows.xml.AbstractMetadata;
import org.geotoolkit.ows.xml.AbstractOwsContextDescription;


/**
 * Human-readable descriptive information for the object it is included within.
 * This type shall be extended if needed for specific OWS use to include additional metadata for each type of information. This type shall not be restricted for a specific OWS to change the multiplicity (or optionality) of some elements.
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
 *         &lt;element ref="{http://www.opengis.net/ows}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Keywords" maxOccurs="unbounded" minOccurs="0"/>
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
    "title",
    "_abstract",
    "keywords"
})
@XmlSeeAlso({
    IdentificationType.class,
    ServiceIdentification.class
})
public class DescriptionType implements AbstractDescription {

    @XmlElement(name = "Title")
    private String title;
    @XmlElement(name = "Abstract")
    private String _abstract;
    @XmlElement(name = "Keywords")
    private List<KeywordsType> keywords;

    /**
     * An empty constructor used by JAXB
     */
     DescriptionType() {

     }

     /**
     * Build a new description
     */
     public DescriptionType(final String title, final String _abstract,  final List<KeywordsType> keywords) {
         this._abstract = _abstract;
         this.title     = title;
         this.keywords  = keywords;
     }

      /**
     * Build a new description
     */
     public DescriptionType(final String title, final String _abstract,  final KeywordsType keywords) {
         this._abstract = _abstract;
         this.title     = title;
         this.keywords  = new ArrayList<>();
         this.keywords.add(keywords);
     }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String getFirstTitle() {
        return title;
    }

    /**
     * Gets the value of the abstract property.
     */
    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(final String _abstract) {
        this._abstract = _abstract;
    }

    @Override
     public String getFirstAbstract() {
        return _abstract;
    }

    /**
     * Gets the value of the keywords property.
     * (unmodifiable)
     */
    @Override
    public List<KeywordsType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return Collections.unmodifiableList(keywords);
    }

    @Override
    public AbstractCodeType getIdentifier() {
        return null;
    }

    @Override
    public List<? extends AbstractMetadata> getMetadata() {
        return new ArrayList<>();
    }

    @Override
    public List<? extends AbstractAdditionalParameters> getAdditionalParameters() {
        return new ArrayList<>();
    }

    /**
     * not implemented
     *
     * @return
     */
    @Override
    public AbstractOwsContextDescription getOwsContext() {
        return null;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DescriptionType) {
            final DescriptionType that = (DescriptionType) object;
            return Objects.equals(this._abstract, that._abstract) &&
                   Objects.equals(this.keywords,  that.keywords)   &&
                   Objects.equals(this.title,     that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 17 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 17 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']').append('\n');
        if (_abstract != null) {
            sb.append("abstract:").append(_abstract).append('\n');
        }
        if (title != null) {
            sb.append("title:").append(title).append('\n');
        }
        if (keywords != null) {
            sb.append("keywords:").append('\n');
            for (KeywordsType k : keywords) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
    }
}
