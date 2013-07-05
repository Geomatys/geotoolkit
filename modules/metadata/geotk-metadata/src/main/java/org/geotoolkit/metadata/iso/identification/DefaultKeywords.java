/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.identification;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.KeywordType;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Keywords, their type and reference source.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MD_Keywords_Type", propOrder={
    "keywords",
    "type",
    "thesaurusName"
})
@XmlRootElement(name = "MD_Keywords")
public class DefaultKeywords extends MetadataEntity implements Keywords {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 48691634443678266L;

    /**
     * Commonly used word(s) or formalised word(s) or phrase(s) used to describe the subject.
     */
    private Collection<InternationalString> keywords;

    /**
     * Subject matter used to group similar keywords.
     */
    private KeywordType type;

    /**
     * Name of the formally registered thesaurus or a similar authoritative source of keywords.
     */
    private Citation thesaurusName;

    /**
     * Constructs an initially empty keywords.
     */
    public DefaultKeywords() {
        super();
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultKeywords(final Keywords source) {
        super(source);
    }

    /**
     * Creates keywords initialized to the given list.
     *
     * @param keywords Commonly used word(s) or formalised word(s) or phrase(s) used to describe
     *                 the subject, or {@code null} if none.
     */
    public DefaultKeywords(final Collection<? extends InternationalString> keywords) {
        if (keywords != null) {
            setKeywords(keywords);
        }
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultKeywords castOrCopy(final Keywords object) {
        return (object == null) || (object instanceof DefaultKeywords)
                ? (DefaultKeywords) object : new DefaultKeywords(object);
    }

    /**
     * Returns commonly used word(s) or formalised word(s) or phrase(s) used to describe the subject.
     */
    @Override
    @XmlElement(name = "keyword", required = true)
    public synchronized Collection<InternationalString> getKeywords() {
        return keywords = nonNullCollection(keywords, InternationalString.class);
    }

    /**
     * Sets commonly used word(s) or formalised word(s) or phrase(s) used to describe the subject.
     *
     * @param newValues The new keywords.
     */
    public synchronized void setKeywords(final Collection<? extends InternationalString> newValues) {
        keywords = copyCollection(newValues, keywords, InternationalString.class);
    }

    /**
     * Returns the subject matter used to group similar keywords.
     */
    @Override
    @XmlElement(name = "type")
    public synchronized KeywordType getType() {
        return type;
    }

    /**
     * Sets the subject matter used to group similar keywords.
     *
     * @param newValue The new keyword type.
     */
    public synchronized void setType(final KeywordType newValue) {
        checkWritePermission();
        type = newValue;
    }

    /**
     * Returns the name of the formally registered thesaurus
     * or a similar authoritative source of keywords.
     */
    @Override
    @XmlElement(name = "thesaurusName")
    public synchronized Citation getThesaurusName() {
        return thesaurusName;
    }

    /**
     * Sets the name of the formally registered thesaurus or a similar authoritative source
     * of keywords.
     *
     * @param newValue The new thesaurus name.
     */
    public synchronized void setThesaurusName(final Citation newValue) {
        checkWritePermission();
        thesaurusName = newValue;
    }
}
