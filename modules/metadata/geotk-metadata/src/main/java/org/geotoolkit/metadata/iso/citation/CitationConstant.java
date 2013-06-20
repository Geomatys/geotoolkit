/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.metadata.iso.citation;

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.PresentationForm;

import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.xml.IdentifierSpace;

import static java.util.Collections.singleton;


/**
 * A citation to be declared as a public static final constant.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
class CitationConstant extends DefaultCitation {
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
     * @param name  The field name in the {@link Citations} class.
     * @param identifier The identifier, or {@code null} if none.
     */
    CitationConstant(final CharSequence title, final String name, final String identifier) {
        super(title);
        replacement = new Serialized(name);
        setIdentifier(identifier);
    }

    /**
     * Constructs a citation with the specified responsible party.
     *
     * @param party The name for an organization that is responsible for the resource.
     * @param name  The field name in the {@link Citations} class.
     * @param identifier The identifier, or {@code null} if none.
     */
    CitationConstant(final ResponsibleParty party, final String name, final String identifier) {
        super(party);
        replacement = new Serialized(name);
        setIdentifier(identifier);
    }

    /**
     * A citation constant also used as a namespace for identifiers.
     *
     * @param <T> The identifier type.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.19
     *
     * @since 3.19
     * @module
     */
    static final class Authority<T> extends CitationConstant implements IdentifierSpace<T> {
        private static final long serialVersionUID = 9049409961960288134L;

        /** The identifier namespace. */
        private final String namespace;

        Authority(CharSequence     title, String name, String namespace) {super(title, name, namespace); this.namespace = namespace;}
        Authority(ResponsibleParty party, String name, String namespace) {super(party, name, namespace); this.namespace = namespace;}
        @Override public String getName() {return namespace;}
    }

    /**
     * Sets the alternative title.
     */
    final void setAlternateTitle(final String title) {
        assert !title.equals(getTitle().toString(null)) : title;
        setAlternateTitles(singleton(new SimpleInternationalString(title)));
    }

    /**
     * Sets the identifier. This is used as a convenience method for the creation of constants.
     */
    private void setIdentifier(final String identifier) {
        if (identifier != null) {
            setIdentifiers(singleton(new DefaultIdentifier(identifier)));
        }
    }

    /**
     * Sets the presentation form to the given value. Any previous values are overwritten.
     */
    final void setPresentationForm(final PresentationForm form) {
        setPresentationForms(singleton(form));
    }

    /**
     * Returns a clone of this object as an instance of {@link DefaultCitation}.
     * We do not allow clones to be instance of {@link CitationConstant}.
     *
     * @return A modifiable clone of this citation.
     */
    @Override
    protected final DefaultCitation clone() {
        return new DefaultCitation(this);
    }

    /**
     * Returns the object to be serialized instead than this one.
     *
     * @return The object to be serialized.
     * @throws ObjectStreamException Should never be thrown.
     */
    protected final Object writeReplace() throws ObjectStreamException {
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
         * Returns the constant value that this object replaces.
         *
         * @return The constant value that this object replaces.
         * @throws ObjectStreamException If an error occurred while resolving the class.
         */
        protected Object readResolve() throws ObjectStreamException {
            try {
                return getContainer().getField(name).get(null);
            } catch (ReflectiveOperationException e) {
                throw new InvalidObjectException(e.toString());
            }
        }
    }
}
