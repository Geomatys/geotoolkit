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
 *          If the xml:lang attribute is not included in a Title, Abstract or Keyword element, then no language is specified for that element unless specified by another means.  All Title, Abstract and Keyword elements in the same Description that share the same xml:lang attribute value represent the description of the parent object in that language. Multiple Title or Abstract elements shall not exist in the same Description with the same xml:lang attribute value unless otherwise specified.
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
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Title" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Keywords" maxOccurs="unbounded" minOccurs="0"/>
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
    //CoverageSummaryType.class,
    //CoverageDescriptionType.class,
    //FieldType.class,
    //AxisType.class,
    BasicIdentificationType.class,
    ServiceIdentification.class
})
public class DescriptionType implements AbstractDescription {

    @XmlElement(name = "Title")
    private List<LanguageStringType> title;
    @XmlElement(name = "Abstract")
    private List<LanguageStringType> _abstract;
    @XmlElement(name = "Keywords")
    private List<KeywordsType> keywords;

    /**
     * An empty constructor used by JAXB.
     */
    protected DescriptionType() {
    }

    public DescriptionType(final String title,  final String _abstract,
            final List<String> keywords) {
        if (title != null) {
            this.title = new ArrayList<>();
            this.title.add(new LanguageStringType(title));
        }
        if (_abstract != null) {
            this._abstract = new ArrayList<>();
            this._abstract.add(new LanguageStringType(_abstract));
        }
        if (keywords != null) {
            this.keywords = new ArrayList<>();
            this.keywords.add(new KeywordsType(keywords));
        }
    }

    /**
     * Build a new DescriptionType (full version).
     */
    public DescriptionType(final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords) {
        this._abstract =  _abstract;
        this.keywords  = keywords;
        this.title     = title;
    }

     /**
     * Build a new DescriptionType (full version).
     */
    public DescriptionType(final LanguageStringType title, final LanguageStringType _abstract, final KeywordsType keywords) {
        this._abstract = new ArrayList<>();
        this._abstract.add(_abstract);
        this.keywords  = new ArrayList<>();
        this.keywords.add(keywords);
        this.title     = new ArrayList<>();
        this.title.add(title);
    }

    /**
     * Gets the value of the title property.
     */
    public List<LanguageStringType> getTitle() {
        if (title == null) {
            title = new ArrayList<>();
        }
        return title;
    }

    public void setTitle(final List<LanguageStringType> titles) {
        this.title = titles;
    }

    public void setTitle(final LanguageStringType title) {
        if (this.title == null) {
            this.title = new ArrayList<>();
        }
        if (title != null) {
            this.title.add(title);
        }
    }

    public void setTitle(final String title) {
        if (this.title == null) {
            this.title = new ArrayList<>();
        }
        if (title != null) {
            this.title.add(new LanguageStringType(title));
        }
    }

    @Override
    public String getFirstTitle() {
        if (title != null && title.size() > 0) {
            return title.get(0).getValue();
        }
        return null;
    }

    /**
     * Gets the value of the abstract property.
     */
    public List<LanguageStringType> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<>();
        }
        return _abstract;
    }

    public void setAbstract(final List<LanguageStringType> abstracts) {
        this._abstract = abstracts;
    }

    public void setAbstract(final LanguageStringType _abstract) {
        if (this._abstract == null) {
            this._abstract = new ArrayList<>();
        }
        if (_abstract != null) {
            this._abstract.add(_abstract);
        }
    }

    public void setAbstract(final String _abstract) {
        if (this._abstract == null) {
            this._abstract = new ArrayList<>();
        }
        if (_abstract != null) {
            this._abstract.add(new LanguageStringType(_abstract));
        }
    }

    @Override
    public String getFirstAbstract() {
        if (_abstract != null && _abstract.size() > 0) {
            return _abstract.get(0).getValue();
        }
        return null;
    }

    /**
     * Gets the value of the keywords property.
     */
    @Override
    public List<KeywordsType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    /**
     * Gets the value of the keywords property.
     */
    public void setKeywords(final List<KeywordsType> keywords) {
        this.keywords = keywords;
    }

    public void setKeywordValues(final List<String> keywords) {
        if (keywords != null) {
            this.keywords = new ArrayList<>();
            this.keywords.add(new KeywordsType(keywords));
        }
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
               Objects.equals(this.keywords, that.keywords) &&
               Objects.equals(this.title, that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 59 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[").append(this.getClass().getSimpleName()).append(']').append('\n');
        if (title != null) {
            s.append("title:").append('\n');
            for (LanguageStringType l:getTitle()) {
                 s.append(l).append('\n');
            }
        }
        if (_abstract != null) {
            s.append("abstract:").append('\n');
            for (LanguageStringType l:getAbstract()) {
                 s.append(l).append('\n');
            }
        }
        if (keywords != null) {
            s.append("keywords:").append('\n');
            for (KeywordsType l:getKeywords()) {
                 s.append(l).append('\n');
            }
        }
        return s.toString();
    }
}
