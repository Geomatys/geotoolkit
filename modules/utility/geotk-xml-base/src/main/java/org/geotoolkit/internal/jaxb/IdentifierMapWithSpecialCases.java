/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.xml.IdentifierSpace;
import org.apache.sis.xml.XLink;


/**
 * A map of identifiers which handles some identifiers in a special way.
 * The identifiers for the following authorities are handled in a special way.
 * See usages of {@link #isSpecialCase(Citation)} for spotting the code where
 * a special handling is applied.
 * <p>
 * <ul>
 *   <li>{@link IdentifierSpace#HREF}, handled as a shortcut to {@link XLink#getHRef()}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
final class IdentifierMapWithSpecialCases extends IdentifierMapAdapter {
    /**
     * For cros-version compatibility.
     */
    private static final long serialVersionUID = 5139573827448780289L;

    /**
     * Creates a new map which will be a view over the given identifiers.
     *
     * @param identifiers The identifiers to wrap in a map view.
     */
    IdentifierMapWithSpecialCases(final Collection<Identifier> identifiers) {
        super(identifiers);
    }

    /**
     * Returns {@code true} if the given authority is the special case.
     * See javadoc for more information about special cases.
     * <p>
     * <b>Implementation note:</b> if more special cases are added in the future, replace
     * the {@code boolean} return value by an {@code int} using the codes declared in
     * {@link IdentifierAuthority}. Then, callers can use switch statements.
     */
    private static boolean isSpecialCase(final Object authority) {
        return (authority == IdentifierSpace.HREF);
    }

    /**
     * Extracts the {@code xlink:href} value from the {@link XLink} if presents. This method does
     * not test if an explicit {@code xlink:href} identifier exists - this check must be done by
     * the caller <strong>before</strong> to invoke this method.
     */
    private String getHRef() {
        final XLink link = super.getSpecialized(IdentifierSpace.XLINK);
        if (link != null) {
            final URI href = link.getHRef();
            if (href != null) {
                return href.toString();
            }
        }
        return null;
    }

    /**
     * Sets the {@code xlink:href} value, which may be null. If an explicit {@code xlink:href}
     * identifier exists, it is removed before to set the new {@code href} in the {@link XLink}
     * object.
     */
    private URI setHRef(final URI href) {
        super.putSpecialized(IdentifierSpace.HREF, null);
        XLink link = super.getSpecialized(IdentifierSpace.XLINK);
        if (link != null) {
            final URI old = link.getHRef();
            link.setHRef(href);
            return old;
        }
        if (href != null) {
            link = new XLink();
            link.setHRef(href);
            super.putSpecialized(IdentifierSpace.XLINK, link);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(final Object code) {
        return super.containsValue(code) || Objects.equals(code, getHRef());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(final Object authority) {
        if (super.containsKey(authority)) {
            return true;
        }
        if (isSpecialCase(authority)) {
            final XLink link = super.getSpecialized(IdentifierSpace.XLINK);
            if (link != null) {
                return link.getHRef() != null;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getSpecialized(final IdentifierSpace<T> authority) {
        T value = super.getSpecialized(authority);
        if (value == null && isSpecialCase(authority)) {
            final XLink link = super.getSpecialized(IdentifierSpace.XLINK);
            if (link != null) {
                value = (T) link.getHRef();
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(final Object authority) {
        String value = super.get(authority);
        if (value == null && isSpecialCase(authority)) {
            value = getHRef();
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String put(final Citation authority, final String code) throws UnsupportedOperationException {
        if (isSpecialCase(authority)) {
            try {
                final URI old = setHRef((code != null) ? new URI(code) : null);
                return (old != null) ? old.toString() : null;
            } catch (URISyntaxException e) {
                // Do not log the exception, since it will be
                // reported by super.put(Citation, String).
            }
        }
        return super.put(authority, code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T putSpecialized(final IdentifierSpace<T> authority, final T value) throws UnsupportedOperationException {
        if (isSpecialCase(authority)) {
            return (T) setHRef((URI) value);
        }
        return super.putSpecialized(authority, value);
    }
}
