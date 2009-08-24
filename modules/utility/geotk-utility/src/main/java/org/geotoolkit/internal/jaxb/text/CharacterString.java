/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 */
package org.geotoolkit.internal.jaxb.text;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.metadata.FreeText;


/**
 * JAXB adapter that wrap the string value with a {@code <gco:CharacterString>}
 * element, for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
@XmlSeeAlso(FreeText.class)
public class CharacterString {
    /**
     * The text or anchor value. It can only by an instance of
     * {@link CharSequence} or {@link AnchorType}.
     */
    protected Object text;

    /**
     * Empty constructor for JAXB only.
     */
    public CharacterString() {
    }

    /**
     * Builds an adapter for the given text.
     *
     * @param text The string to marshall.
     */
    public CharacterString(final CharSequence text) {
        this.text = text;
    }

    /**
     * Builds an adapter for the given text.
     *
     * @param text The string to marshall.
     */
    public CharacterString(final AnchorType text) {
        this.text = text;
    }

    /**
     * Returns the text. This method may be called by JAXB at marshalling-time.
     *
     * @return The text, or {@code null}.
     */
    @XmlElement(name = "CharacterString", namespace = Namespaces.GCO)
    public final String getCharacterString() {
        final Object text = this.text;
        return (text instanceof CharSequence) ? text.toString() : null;
    }

    /**
     * Sets the value to the given string.
     *
     * @param text The new text.
     */
    public final void setCharacterString(final String text) {
        this.text = text;
    }

    /**
     * Returns the text associated with a reference.
     * This method may be called by JAXB at marshalling-time.
     *
     * @return The anchor, or {@code null}.
     */
    @XmlElement(name = "Anchor", namespace = Namespaces.GMX)
    public final AnchorType getAnchor() {
        final Object text = this.text;
        return (text instanceof AnchorType) ? (AnchorType) text : null;
    }

    /**
     * Sets the value for the metadata string.
     * This method may be called at unmarshalling-time by JAXB.
     *
     * @param anchor The new anchor.
     */
    public final void setAnchor(final AnchorType anchor) {
        this.text = anchor;
    }

    /**
     * Returns the text as a string, or {@code null} if none.
     * The null value is expected by various {@code unmarshal} methods.
     */
    @Override
    public final String toString() {
        final Object text = this.text;
        return (text != null) ? text.toString() : null;
    }
}
