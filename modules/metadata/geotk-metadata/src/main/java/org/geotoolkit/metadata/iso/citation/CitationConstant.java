/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.metadata.iso.citation;

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;

import org.opengis.metadata.citation.ResponsibleParty;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * A citation to be declared as a public static final constant.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
final class CitationConstant extends DefaultCitation {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -2997857814910523464L;

    /**
     * The object to use in replacement of this citation during serialization.
     */
    private final Serialized replacement;

    /**
     * Constructs a citation with the specified title.
     *
     * @param title The title, as a {@link String} or an {@link InternationalString} object.
     * @param name  The name of a field in the {@link Citations} class.
     */
    public CitationConstant(final CharSequence title, final String name) {
        super(title);
        replacement = new Serialized(name);
    }

    /**
     * Constructs a citation with the specified responsible party.
     *
     * @param party The name for an organization that is responsible for the resource.
     * @param name  The name of a field in the {@link Citations} class.
     */
    public CitationConstant(final ResponsibleParty party, final String name) {
        super(party);
        replacement = new Serialized(name);
    }

    /**
     * Adds the specified identifier as a CRS authority factory. This is used as a convenience
     * method for the creation of constants, and for making sure that all of them use the same
     * identifier type.
     */
    final void addAuthority(final String identifier, final boolean asTitle) {
        if (asTitle) {
            assert !identifier.equals(getTitle().toString(null)) : identifier;
            getAlternateTitles().add(new SimpleInternationalString(identifier));
        }
        getIdentifiers().add(new DefaultIdentifier(identifier));
    }

    /**
     * Returns a clone of this object as an instance of {@link DefaultCitation}.
     * We do not allow clones to be instance of {@link CitationConstant}.
     *
     * @return A modifiable clone of this citation.
     */
    @Override
    public DefaultCitation clone() {
        return new DefaultCitation(this);
    }

    /**
     * Returns the object to be serialized instead than this one.
     *
     * @return The object to be serialized.
     * @throws ObjectStreamException Should never be thrown.
     */
    protected Object writeReplace() throws ObjectStreamException {
        return replacement;
    }

    /**
     * Object to be serialized instead of constants defined in {@link Citations}.
     * On deserialization, this object returns the constant value that it is replacing.
     * Advantage of doing so is to reduce the size of the stream (since many authority
     * citations are predefined constants), and ensure that deserialization of those
     * citation returns the singleton, which help both memory usage and performance.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static class Serialized implements Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 6177391499370692010L;

        /**
         * The name of a field in the {@link Citations} class.
         */
        private final String name;

        /**
         * Creates a new instance of {@code Serialized} for the given field.
         *
         * @param name The name of a field in the {@link Citations} class.
         */
        public Serialized(final String name) {
            this.name = name;
        }

        /**
         * Returns the class which is defining the constants.
         * The default implementation returns {@link Citations}.
         *
         * @return The class which is defining the constants.
         */
        protected Class<?> getContainer() {
            return Citations.class;
        }

        /**
         * Returns the contant value that this object replaces.
         *
         * @return The constant value that this object replaces.
         * @throws ObjectStreamException If an error occurred while resolving the class.
         */
        protected Object readResolve() throws ObjectStreamException {
            try {
                return getContainer().getField(name).get(null);
            } catch (NoSuchFieldException e) {
                throw new InvalidObjectException(e.toString());
            } catch (IllegalAccessException e) {
                throw new InvalidObjectException(e.toString());
            }
        }
    }
}
